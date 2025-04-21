package aj.FiTracker.FiTrackerExpenses.Controllers;


import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.*;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Services.ExpensesService;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Tag("unit")
@ActiveProfiles("unit")
public class ExpensesControllerUnitTest {
    private ExpensesController expensesController;
    private Authentication authenticationMock;
    private ExpensesService expensesServiceMock;

    @BeforeEach
    public void setup() {
        this.authenticationMock = mock(Authentication.class);
        this.expensesServiceMock = mock(ExpensesService.class);
        this.expensesController = new ExpensesController(this.expensesServiceMock);
    }

    @Test
    @DisplayName("Should add and return expense")
    public void testAddExpense() {
        Expense testExpense = TestUtils.addTestExpense();
        AddExpenseRequest addExpenseRequest = TestUtils.addExpenseRequest();
        when(this.expensesServiceMock.addExpense(
                eq(addExpenseRequest),
                eq(ZONE_TEST_ID),
                any(Authentication.class)

        )).thenReturn(testExpense);

        ResponseEntity<AddExpenseResponse> addExpenseResponse = this.expensesController.addExpense(
                this.authenticationMock,
                ZONE_TEST_ID,
                addExpenseRequest
        );
        verify(this.expensesServiceMock, times(1)).addExpense(
                eq(addExpenseRequest),
                eq(ZONE_TEST_ID),
                any(Authentication.class)
        );

        assertEquals(HttpStatus.CREATED, addExpenseResponse.getStatusCode());
        AddExpenseResponse addExpenseResponseBody = addExpenseResponse.getBody();
        assertEquals(TEST_EXPENSE_VALUE, addExpenseResponseBody.getValue());
        assertEquals(TEST_EXPENSE_NAME, addExpenseResponseBody.getName());
        assertEquals(DEFAULT_CURRENCY, addExpenseResponseBody.getCurrency());
    }

    @Test
    @DisplayName("Should return summary by category")
    public void testGetSummaryByCategory() {
        List<TotalSummaryByCategory> totalSummaryByCategories = List.of(
                new TotalSummaryByCategory(CATEGORY_TEST_NAME, TEST_EXPENSE_VALUE)
        );
        when(this.expensesServiceMock.getSummaryByCategory(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        )).thenReturn(totalSummaryByCategories);
        ResponseEntity<SummaryByCategory> summaryByCategories = (ResponseEntity<SummaryByCategory>) this.expensesController.getSummary(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "category",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, times(1)).getSummaryByCategory(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        );
        verify(this.expensesServiceMock, never()).getSummaryByDate(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.OK, summaryByCategories.getStatusCode());
        SummaryByCategory totalSummaryByCategoryBody = summaryByCategories.getBody();
        assertEquals(1, totalSummaryByCategoryBody.getSummaries().size());
        TotalSummaryByCategory totalSummaryByCategory = totalSummaryByCategoryBody.getSummaries().getFirst();
        assertEquals(CATEGORY_TEST_NAME, totalSummaryByCategory.categoryName());
        assertEquals(TEST_EXPENSE_VALUE, totalSummaryByCategory.expensesValue());
    }

    @Test
    @DisplayName("Should return NotFound when cannot find type in summary by category")
    public void testGetSummaryByCategoryNotFound() {

        ResponseEntity<List<TotalSummaryByCategory>> summaryByCategories = (ResponseEntity<List<TotalSummaryByCategory>>) this.expensesController.getSummary(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "invalid",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, never()).getSummaryByCategory(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Authentication.class)
        );
        verify(this.expensesServiceMock, never()).getSummaryByDate(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, summaryByCategories.getStatusCode());
    }

    @Test
    @DisplayName("Should return summary by day")
    public void testGetSummaryByDay() {
        List<TotalSummaryByDate> totalSummaryByDates = List.of(
                new TotalSummaryByDate(Date.valueOf(LocalDate.now()), TEST_EXPENSE_VALUE)
        );
        when(this.expensesServiceMock.getSummaryByDate(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        )).thenReturn(totalSummaryByDates);
        ResponseEntity<SummaryByDate> summary = (ResponseEntity<SummaryByDate>) this.expensesController.getSummary(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "day",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, times(1)).getSummaryByDate(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        );
        verify(this.expensesServiceMock, never()).getSummaryByCategory(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.OK, summary.getStatusCode());
        assertEquals(1, summary.getBody().getSummaries().size());
        TotalSummaryByDate totalSummaryByDate = summary.getBody().getSummaries().getFirst();
        assertEquals(TEST_EXPENSE_VALUE, totalSummaryByDate.expensesValue());
    }


    @Test
    @DisplayName("Should return top expenses")
    public void testGetTopSummary() {
        List<TopExpense> topExpenseList = List.of(new TopExpense(TEST_EXPENSE_NAME, TEST_EXPENSE_VALUE, CATEGORY_TEST_NAME, Date.valueOf(LocalDate.now())));
        when(this.expensesServiceMock.getSummaryTopExpenses(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        )).thenReturn(topExpenseList);
        ResponseEntity<SummaryTopExpenses> response = (ResponseEntity<SummaryTopExpenses>) this.expensesController.getTopSummary(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "expenses",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, times(1)).getSummaryTopExpenses(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getExpenses().size());
        TopExpense topExpense = response.getBody().getExpenses().getFirst();
        assertEquals(TEST_EXPENSE_NAME, topExpense.expenseName());
        assertEquals(TEST_EXPENSE_VALUE, topExpense.expenseValue());
        assertEquals(CATEGORY_TEST_NAME, topExpense.categoryName());
    }

    @Test
    @DisplayName("Should return NotFound when cannot find type in top expenses")
    public void testGetTopSummaryNotFound() {

        ResponseEntity<SummaryTopExpenses> response = (ResponseEntity<SummaryTopExpenses>) this.expensesController.getTopSummary(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "invalid",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, never()).getSummaryTopExpenses(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return sums")
    public void testGetSummarySum() {
        List<ExpensesSum> expensesSums = List.of(new ExpensesSum(CATEGORY_TEST_NAME, TEST_EXPENSE_VALUE, TEST_EXPENSE_VALUE));
        when(this.expensesServiceMock.getSummarySum(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        )).thenReturn(expensesSums);
        ResponseEntity<GeneralSum> generalSumResponse = (ResponseEntity<GeneralSum>) this.expensesController.getSummarySum(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "category",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, times(1)).getSummarySum(
                eq(ZONE_TEST_ID),
                eq("PLN"),
                eq(TEST_FROM_DATE_PARAM),
                eq(TEST_END_DATE_PARAM),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.OK, generalSumResponse.getStatusCode());
        assertEquals(1, generalSumResponse.getBody().getSum().size());
        ExpensesSum expensesSum = generalSumResponse.getBody().getSum().getFirst();
        assertEquals(CATEGORY_TEST_NAME, expensesSum.categoryName());
        assertEquals(TEST_EXPENSE_VALUE, expensesSum.overallSum());
        assertEquals(TEST_EXPENSE_VALUE, expensesSum.categoryValue());
    }

    @Test
    @DisplayName("Should return NotFound when cannot find type in sums")
    public void testGetSummarySumNotFound() {

        ResponseEntity<GeneralSum> generalSumResponse = (ResponseEntity<GeneralSum>) this.expensesController.getSummarySum(
                ZONE_TEST_ID,
                "PLN",
                List.of(Long.valueOf(1)),
                "invalid",
                TEST_FROM_DATE_PARAM,
                TEST_END_DATE_PARAM,
                USER_TEST_ID,
                this.authenticationMock
        );
        verify(this.expensesServiceMock, never()).getSummarySum(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Authentication.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, generalSumResponse.getStatusCode());
    }

}
