package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddExpenseRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Exceptions.CannotFindCategoriesException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("integration")
@Tag("integration")
@SpringBootTest
public class ExpensesServiceIntegrationTest extends AbstractIntegrationTest {
    private final ExpensesService expensesService;
    private Authentication authenticationMock;
    private Jwt jwtMock;

    @Autowired
    public ExpensesServiceIntegrationTest(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @BeforeEach
    public void setup() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(USER_TEST_ID));
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(USER_TEST_NAME);
        this.authenticationMock = mock(Authentication.class);
        when(this.authenticationMock.getPrincipal()).thenReturn(this.jwtMock);
    }

    @AfterEach
    public void clean() {
        this.truncateTable("app_data.expenses");
        this.truncateTable("app_data.categories");
        this.truncateTable("app_data.app_user");
        this.truncateTable("app_data.exp_categories");
    }

    @Test
    @DisplayName("Should add expense")
    public void testAddExpense() {
        this.insertTestUser();
        this.insertTestCategory();

        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();

        Expense expense = this.expensesService.addExpense(addExpenseRequest, ZONE_TEST_ID, this.authenticationMock);
        assertNotNull(expense.getExpenseId());
        assertEquals(TEST_EXPENSE_NAME, expense.getName());
        assertEquals(DEFAULT_CURRENCY, expense.getCurrency());
        assertEquals(USER_TEST_ID, expense.getUser());
        assertFalse(expense.getCategories().isEmpty());
        assertEquals(TEST_EXPENSE_VALUE, expense.getValue());
        assertInstanceOf(LocalDateTime.class, expense.getCreatedAt());
        assertInstanceOf(LocalDateTime.class, expense.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw invalid user when categories are set and user is null when adding expense")
    public void testAddExpenseUserUnauthorized() {
        this.insertTestCategory();

        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();

        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.addExpense(addExpenseRequest, ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("User does not have privileges to zone", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw invalid user when categories and user are set, but zone is invalid ")
    public void testAddExpenseUserUnauthorizedInvalidZone() {
        this.insertTestCategory();
        this.insertTestUser();
        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();

        UserUnauthorizedException exception = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.addExpense(addExpenseRequest, "INVALID_ZONE", this.authenticationMock);
        });
        assertEquals("User does not have privileges to zone", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw cannot find categories when user is valid, but categories cannot be find")
    public void testAddExpenseCannotFindCategories() {
        this.insertTestUser();

        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();

        CannotFindCategoriesException exception = assertThrows(CannotFindCategoriesException.class, () -> {
            this.expensesService.addExpense(addExpenseRequest, ZONE_TEST_ID, this.authenticationMock);
        });
        assertEquals("Cannot find categories: [1]", exception.getMessage());

    }

    @Test
    @DisplayName("Should get monthly summary by category")
    public void testGetSummarySum() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        List<ExpensesSum> expensesSums = this.expensesService.getSummarySum(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(expensesSums.isEmpty());
        assertEquals(1, expensesSums.size());
        ExpensesSum sum = expensesSums.getFirst();
        assertEquals("zone_test", sum.categoryName());
        assertEquals(BigDecimal.valueOf(100), sum.overallSum());
        assertEquals(BigDecimal.valueOf(100), sum.categoryValue());
    }

    @Test
    @DisplayName("Should get monthly summary by category with two expenses")
    public void testGetSummarySumWithTwoExpenses() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        List<ExpensesSum> expensesSums = this.expensesService.getSummarySum(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(expensesSums.isEmpty());
        assertEquals(1, expensesSums.size());
        ExpensesSum sum = expensesSums.getFirst();
        assertEquals("zone_test", sum.categoryName());
        assertEquals(BigDecimal.valueOf(200), sum.overallSum());
        assertEquals(BigDecimal.valueOf(200), sum.categoryValue());
    }

    @Test
    @DisplayName("Should return empty list when cannot find record by currency")
    public void testGetSummarySumyUnknownCurrency() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        List<ExpensesSum> expensesSums = this.expensesService.getSummarySum(
                ZONE_TEST_ID,
                "USD",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertTrue(expensesSums.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when cannot find by period")
    public void testGetSummarySumBadPeriod() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        List<ExpensesSum> expensesSums = this.expensesService.getSummarySum(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM_BAD,
                TEST_END_DATE_PARAM_BAD,
                this.authenticationMock
        );
        assertTrue(expensesSums.isEmpty());
    }

    @Test
    @DisplayName("Should return throw Unauthorized when user is not set in database")
    public void testgetSummarySumUnknownUser() {
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummarySum(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM_BAD,
                    TEST_END_DATE_PARAM_BAD,
                    this.authenticationMock
            );
        });
        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }


    @Test
    @DisplayName("Should return throw Unauthorized when zone is unknown")
    public void testGetSummarySumInvalidZOne() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummarySum(
                    "INVALID_ZONE",
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM_BAD,
                    TEST_END_DATE_PARAM_BAD,
                    this.authenticationMock
            );
        });
        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Get summary by category")
    public void testFetSummaryByCategory() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        List<TotalSummaryByCategory> totalSummaryByCategories = this.expensesService.getSummaryByCategory(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(totalSummaryByCategories.isEmpty());
        TotalSummaryByCategory totalSummaryByCategory = totalSummaryByCategories.getFirst();
        assertEquals("zone_test", totalSummaryByCategory.categoryName());
        assertEquals(BigDecimal.valueOf(100), totalSummaryByCategory.expensesValue());
    }

    @Test
    @DisplayName("Get summary by category multi expenses")
    public void testFetSummaryByCategoryWithMultiExpenses() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        List<TotalSummaryByCategory> totalSummaryByCategories = this.expensesService.getSummaryByCategory(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(totalSummaryByCategories.isEmpty());
        TotalSummaryByCategory totalSummaryByCategory = totalSummaryByCategories.getFirst();
        assertEquals("zone_test", totalSummaryByCategory.categoryName());
        assertEquals(BigDecimal.valueOf(200), totalSummaryByCategory.expensesValue());
    }


    @Test
    @DisplayName("Should return throw Unauthorized when zone is unknown")
    public void testGetSummaryByCategoryInvalidZone() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummaryByCategory(
                    "INVALID_ZONE",
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });
        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should return throw Unauthorized when user is unknown")
    public void testGetSummaryByCategoryInvalidUser() {
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummaryByCategory(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });
        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should return empty list when bad period")
    public void testGetSummaryByCategoryBadPeriod() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();

        List<TotalSummaryByCategory> totalSummaryByCategory = this.expensesService.getSummaryByCategory(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM_BAD,
                TEST_END_DATE_PARAM_BAD,
                this.authenticationMock
        );
        assertTrue(totalSummaryByCategory.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when bad period in get summary by day")
    public void testGetSummaryByDayBadPeriod() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();

        List<TotalSummaryByDate> totalSummaryByCategory = this.expensesService.getSummaryByDate(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM_BAD,
                TEST_END_DATE_PARAM_BAD,
                this.authenticationMock
        );
        assertTrue(totalSummaryByCategory.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when bad user in get summary by day")
    public void testGetSummaryByDayInvalidUser() {
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            List<TotalSummaryByDate> totalSummaryByCategory = this.expensesService.getSummaryByDate(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM_BAD,
                    TEST_END_DATE_PARAM_BAD,
                    this.authenticationMock
            );
        });

        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should return empty list when bad zone in get summary by day")
    public void testGetSummaryByDayInvalidZone() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            List<TotalSummaryByDate> totalSummaryByCategory = this.expensesService.getSummaryByDate(
                    "INVALID_ZONE",
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });

        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should return summary by day")
    public void testGetSummaryByDay() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        List<TotalSummaryByDate> totalSummaryByDates = this.expensesService.getSummaryByDate(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(totalSummaryByDates.isEmpty());
        TotalSummaryByDate totalSummaryByDate = totalSummaryByDates.getFirst();
        assertEquals(BigDecimal.valueOf(200), totalSummaryByDate.expensesValue());
        assertInstanceOf(Date.class, totalSummaryByDate.date());
    }

    @Test
    @DisplayName("Should return top expenses")
    public void testGetSummaryTopExpenses() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        List<TopExpense> topExpenses = this.expensesService.getSummaryTopExpenses(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(topExpenses.isEmpty());
        TopExpense topExpense = topExpenses.getFirst();
        assertEquals("Test expense", topExpense.expenseName());
        assertEquals("zone_test", topExpense.categoryName());
        assertEquals(BigDecimal.valueOf(100), topExpense.expenseValue());
        assertInstanceOf(Date.class, topExpense.date());
    }

    @Test
    @DisplayName("Should return top expenses with multi expenses")
    public void testGetSummaryTopExpensesMulti() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        List<TopExpense> topExpenses = this.expensesService.getSummaryTopExpenses(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                this.authenticationMock
        );
        assertFalse(topExpenses.isEmpty());
        assertEquals(2, topExpenses.size());
        TopExpense topExpense = topExpenses.getFirst();
        assertEquals("Test expense", topExpense.expenseName());
        assertEquals("zone_test", topExpense.categoryName());
        assertEquals(BigDecimal.valueOf(100), topExpense.expenseValue());
        assertInstanceOf(Date.class, topExpense.date());
    }

    @Test
    @DisplayName("Should throw unauthorized user when zone is not valid")
    public void testGetSummaryTopExpensesInvalidZone() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummaryTopExpenses(
                    "INVALID_ZONE",
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });

        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should throw unauthorized user when user is not valid")
    public void testGetSummaryTopExpensesInvalidUser() {
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.expensesService.getSummaryTopExpenses(
                    ZONE_TEST_ID,
                    DEFAULT_CURRENCY,
                    TEST_FROM_DATE_PARAM,
                    TEST_END_DATE_PARAM,
                    this.authenticationMock
            );
        });

        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should return empty list, when period is valid")
    public void testGetSummaryTopExpensesBadPeriod() {
        this.insertTestUser();
        this.insertTestCategory();
        this.createTestExpenses();
        this.addAnotherExpense();

        List<TopExpense> topExpenses = this.expensesService.getSummaryTopExpenses(
                ZONE_TEST_ID,
                DEFAULT_CURRENCY,
                TEST_FROM_DATE_PARAM_BAD,
                TEST_END_DATE_PARAM_BAD,
                this.authenticationMock
        );

        assertTrue(topExpenses.isEmpty());
    }

    private void insertTestCategory() {
        this.insertTestData("INSERT INTO app_data.categories (category_id, name, description, zone_id, created_at, updated_at) VALUES " +
                "(1,'zone_test', 'Test description', 'abc', NOW(), NOW());");
    }

    private void insertTestUser() {
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (1, 1, 'abc');");

    }

    private void createTestExpenses() {
        this.insertTestData("INSERT INTO app_data.expenses (expense_id, user_id, name, currency, description, value, created_at, updated_at) VALUES (1, 1, 'Test expense', 'PLN', 'TestDescription', 100, NOW(), NOW());");
        this.insertTestData("INSERT INTO app_data.exp_categories (category_id, expense_id) VALUES (1,1);");
    }

    private void addAnotherExpense() {
        this.insertTestData("INSERT INTO app_data.expenses (expense_id, user_id, name, currency, description, value, created_at, updated_at) VALUES (2, 1, 'Test expense', 'PLN', 'TestDescription', 100, NOW(), NOW());");
        this.insertTestData("INSERT INTO app_data.exp_categories (category_id, expense_id) VALUES (1,2);");

    }


}
