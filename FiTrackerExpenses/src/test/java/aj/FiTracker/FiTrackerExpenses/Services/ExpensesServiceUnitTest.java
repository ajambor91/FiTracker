package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddExpenseRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Exceptions.CannotFindCategoriesException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Repositories.CategoriesRepository;
import aj.FiTracker.FiTrackerExpenses.Repositories.ExpensesRepository;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ExpensesServiceUnitTest {
    private Jwt jwtMock;
    private Authentication authenticationMock;
    private ExpensesService expensesService;
    private ExpensesRepository expensesRepositoryMock;
    private CategoriesRepository categoriesRepositoryMock;
    private MembersService membersServiceMock;

    @BeforeEach
    public void setup() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(USER_TEST_ID));
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(USER_TEST_NAME);
        this.authenticationMock = mock(Authentication.class);
        when(this.authenticationMock.getPrincipal()).thenReturn(this.jwtMock);
        this.categoriesRepositoryMock = mock(CategoriesRepository.class);
        this.membersServiceMock = mock(MembersService.class);
        this.expensesRepositoryMock = mock(ExpensesRepository.class);
        this.expensesService = new ExpensesService(this.expensesRepositoryMock, this.categoriesRepositoryMock, this.membersServiceMock);
    }

    @Test
    @DisplayName("Should add expense and return")
    public void testAddExpense() {
        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();
        when(categoriesRepositoryMock.findByCategoryIdInAndZoneId(eq(List.of(CATEGORY_TEST_ID)), eq(ZONE_TEST_ID))).thenReturn(List.of(createTestCategory()));
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(createTestUser());
        when(expensesRepositoryMock.saveAndFlush(any(Expense.class))).thenReturn(addTestExpense());
        Expense testExpense = this.expensesService.addExpense(addExpenseRequest, ZONE_TEST_ID, authenticationMock);
        verify(categoriesRepositoryMock, times(1)).findByCategoryIdInAndZoneId(eq(List.of(CATEGORY_TEST_ID)), eq(ZONE_TEST_ID));
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(expensesRepositoryMock, times(1)).saveAndFlush(any(Expense.class));
        assertEquals(USER_TEST_ID, testExpense.getUser());
        assertEquals(DEFAULT_CURRENCY, testExpense.getCurrency());
        assertEquals(TEST_EXPENSE_NAME, testExpense.getName());
        assertEquals(TEST_EXPENSE_VALUE, testExpense.getValue());
        assertInstanceOf(Set.class, testExpense.getCategories());
        assertEquals(1, testExpense.getCategories().size());
    }

    @Test
    @DisplayName("Should add expense and throw UserUnauthorizedException")
    public void testAddExpenseUserUnauthorizedException() {
        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenThrow(UserUnauthorizedException.class);
        assertThrows(UserUnauthorizedException.class, () -> {
            Expense testExpense = this.expensesService.addExpense(addExpenseRequest, ZONE_TEST_ID, authenticationMock);

        });
        verify(categoriesRepositoryMock, never()).findByCategoryIdInAndZoneId(anyList(), anyString());
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(expensesRepositoryMock, never()).saveAndFlush(any(Expense.class));
    }

    @Test
    @DisplayName("Should add expense and throw CannotFindCategoriesException")
    public void testAddExpenseUserCannotFindCategoriesException() {
        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();
        when(categoriesRepositoryMock.findByCategoryIdInAndZoneId(eq(List.of(CATEGORY_TEST_ID)), eq(ZONE_TEST_ID))).thenThrow(CannotFindCategoriesException.class);
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(createTestUser());
        CannotFindCategoriesException exception = assertThrows(CannotFindCategoriesException.class, () -> {
            Expense testExpense = this.expensesService.addExpense(addExpenseRequest, ZONE_TEST_ID, authenticationMock);

        });
        verify(categoriesRepositoryMock, times(1)).findByCategoryIdInAndZoneId(eq(List.of(CATEGORY_TEST_ID)), eq(ZONE_TEST_ID));
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(expensesRepositoryMock, never()).saveAndFlush(any(Expense.class));
    }

    @Test
    @DisplayName("Should return top summaries by category")
    public void testGetSummaryByCategory() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(createTestUser());
        when(this.expensesRepositoryMock.getSummaryByCategory(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)

        )).thenReturn(List.of(
                new TotalSummaryByCategory(CATEGORY_TEST_NAME, TEST_EXPENSE_VALUE)
        ));
        List<TotalSummaryByCategory> totalSummaryByCategories = this.expensesService.getSummaryByCategory(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(this.expensesRepositoryMock, times(1)).getSummaryByCategory(eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        assertEquals(1, totalSummaryByCategories.size());
        TotalSummaryByCategory totalSummaryByCategory = totalSummaryByCategories.getFirst();
        assertEquals(CATEGORY_TEST_NAME, totalSummaryByCategory.categoryName());
        assertEquals(TEST_EXPENSE_VALUE, totalSummaryByCategory.expensesValue());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException when trying get total summary by category")
    public void testGetSummaryByCategoryUserUnauthorizedException() {
        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenThrow(UserUnauthorizedException.class);
        assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummaryByCategory(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(this.expensesRepositoryMock, never()).getSummaryByCategory(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should getSummaryTopExpenses")
    public void testGetSummaryTopExpenses() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(createTestUser());
        when(expensesRepositoryMock.getTopExpenses(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(
                new TopExpense(TEST_EXPENSE_NAME, TEST_EXPENSE_VALUE, CATEGORY_TEST_NAME, Date.valueOf(LocalDateTime.now().toLocalDate()))
        ));
        List<TopExpense> topExpenses = this.expensesService.getSummaryTopExpenses(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        verify(expensesRepositoryMock, times(1)).getTopExpenses(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        assertEquals(1, topExpenses.size());
        TopExpense topExpense = topExpenses.getFirst();
        assertEquals(TEST_EXPENSE_NAME, topExpense.expenseName());
        assertEquals(TEST_EXPENSE_VALUE, topExpense.expenseValue());
        assertEquals(CATEGORY_TEST_NAME, topExpense.categoryName());
        assertInstanceOf(Date.class, topExpense.date());

    }


    @Test
    @DisplayName("Should  UserUnauthorizedException getSummaryTopExpenses")
    public void testGetSummaryTopExpensesUserUnauthorizedException() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> {
            List<TopExpense> topExpenses = this.expensesService.getSummaryTopExpenses(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));

        verify(expensesRepositoryMock, never()).getTopExpenses(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

    }

    @Test
    @DisplayName("Should get expenses sums getSummarySum")
    public void testGetSummarySum() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(createTestUser());
        when(expensesRepositoryMock.getExpensesSum(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(
                new ExpensesSum(CATEGORY_TEST_NAME, CATEGORY_VALUE, OVERALL_VALUE)
        ));
        List<ExpensesSum> expensesSum = this.expensesService.getSummarySum(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(expensesRepositoryMock, times(1)).getExpensesSum(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        assertEquals(1, expensesSum.size());
        ExpensesSum expenseSum = expensesSum.getFirst();
        assertEquals(CATEGORY_TEST_NAME, expenseSum.categoryName());
        assertEquals(CATEGORY_VALUE, expenseSum.categoryValue());
        assertEquals(OVERALL_VALUE, expenseSum.overallSum());
    }

    @Test
    @DisplayName("Should throw userUnauthorizedException getSummarySum")
    public void testGetSummarySumuserUnauthorizedException() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenThrow(UserUnauthorizedException.class);
        assertThrows(UserUnauthorizedException.class, () -> {
            List<ExpensesSum> expensesSum = this.expensesService.getSummarySum(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });

        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(expensesRepositoryMock, never()).getExpensesSum(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("Should getSummaryByDate")
    public void testGetSummaryByDate() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenReturn(createTestUser());
        when(expensesRepositoryMock.getSummaryByDay(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(
                new TotalSummaryByDate(Date.valueOf(LocalDateTime.now().toLocalDate()), TEST_EXPENSE_VALUE)
        ));
        List<TotalSummaryByDate> totalSummaryByDates = this.expensesService.getSummaryByDate(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(this.expensesRepositoryMock, times(1)).getSummaryByDay(
                eq(ZONE_TEST_ID),
                eq(DEFAULT_CURRENCY),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        assertEquals(1, totalSummaryByDates.size());
        TotalSummaryByDate totalSummaryByDate = totalSummaryByDates.getFirst();
        assertEquals(TEST_EXPENSE_VALUE, totalSummaryByDate.expensesValue());
        assertInstanceOf(Date.class, totalSummaryByDate.date());
    }

    @Test
    @DisplayName("Should throw UserUnauthorizedException getSummaryByDate")
    public void testGetSummaryByDateUserUnauthorizedException() {
        when(membersServiceMock.getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID))).thenThrow(UserUnauthorizedException.class);
        assertThrows(UserUnauthorizedException.class, () -> {
            List<TotalSummaryByDate> totalSummaryByDates = this.expensesService.getSummaryByDate(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });

        verify(membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(ZONE_TEST_ID));
        verify(this.expensesRepositoryMock, never()).getSummaryByDay(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

    }


}
