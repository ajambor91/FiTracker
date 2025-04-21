package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTrackerExpenses.SecurityUtils.WithMockJwt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.ZONE_TEST_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockJwt
@ActiveProfiles("integration")
@Tag("integration")
public class ExpensesControllerIntegrationTest extends AbstractIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    public ExpensesControllerIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @AfterEach
    public void cleanDatabase() {
        this.truncateTable("app_data.expenses");
        this.truncateTable("app_data.categories");
        this.truncateTable("app_data.app_user");
        this.truncateTable("app_data.exp_categories");
    }

    @Test
    @DisplayName("Should add and return expense")
    public void testAddExpense() throws Exception {
        this.insertTestUser();
        this.mockMvc.perform(
                        post("/expenses/zone/" + ZONE_TEST_ID + "/expense")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                                .content("""
                                        {
                                            "zoneId": "abc",
                                            "currency": "PLN",
                                            "value": 100,
                                            "name": "Test expense",
                                            "categoriesIds": [1]
                                            }
                                        """)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("PLN"))
                .andExpect(jsonPath("$.value").value(100.00))
                .andExpect(jsonPath("$.name").value("Test expense"))
                .andExpect(jsonPath("$.categoriesIds[0]").value(1))
                .andExpect(jsonPath("$.expenseId").exists());
    }

    @Test
    @DisplayName("Should add and return Unauthorized")
    public void testAddExpenseUnauthorized() throws Exception {
        this.insertTestUser();
        this.mockMvc.perform(
                        post("/expenses/zone/unauthZone/expense")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                                .content("""
                                        {
                                            "zoneId": "unauthZone",
                                            "currency": "PLN",
                                            "value": 100,
                                            "name": "Test expense",
                                            "categoriesIds": [1]
                                            }
                                        """)
                ).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User does not have privileges to zone"))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should add and return NotFound on unknown category")
    public void testAddExpenseNotFound() throws Exception {
        this.insertTestUser();
        this.mockMvc.perform(
                        post("/expenses/zone/" + ZONE_TEST_ID + "/expense")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                                .content("""
                                        {
                                            "zoneId": "abc",
                                            "currency": "PLN",
                                            "value": 100,
                                            "name": "Test expense",
                                            "categoriesIds": [12]
                                            }
                                        """)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cannot find categories: [12]"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Should get summary by category")
    public void testGetSummaryCategory() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                        get("/expenses/zone/" + ZONE_TEST_ID + "/summary?currency=PLN&groupBy=category&periodStart=2025-04-01&periodEnd=2027-04-30")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.summaries[0].categoryName").value("Test category"))
                .andExpect(jsonPath("$.summaries[0].expensesValue").value(100));
    }

    @Test
    @DisplayName("Should get summary by category")
    public void testGetSummaryDate() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                        get("/expenses/zone/" + ZONE_TEST_ID + "/summary?currency=PLN&groupBy=day&periodStart=2025-04-01&periodEnd=2027-04-30")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.summaries[0].date").exists())
                .andExpect(jsonPath("$.summaries[0].expensesValue").value(100));
    }

    @Test
    @DisplayName("Should get summary return not found when group id is unknown")
    public void testGetSummaryNotFoundByGroup() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                get("/expenses/zone/" + ZONE_TEST_ID + "/summary?currency=PLN&groupBy=invalid&periodStart=2025-04-01&periodEnd=2027-04-30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer TOKEN")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return top expenses")
    public void testGetTopSummary() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                        get("/expenses/zone/" + ZONE_TEST_ID + "/summary/top?currency=PLN&type=expenses&periodStart=2025-04-01&periodEnd=2027-04-30")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses[0].expenseName").value("Test expense"))
                .andExpect(jsonPath("$.expenses[0].expenseValue").value(100.00))
                .andExpect(jsonPath("$.expenses[0].categoryName").value("Test category"))
                .andExpect(jsonPath("$.expenses[0].date").exists());
    }

    @Test
    @DisplayName("Should return NotFound when type is unknown")
    public void testGetTopSummaryNotFound() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                get("/expenses/zone/" + ZONE_TEST_ID + "/summary/top?currency=PLN&type=invalid&periodStart=2025-04-01&periodEnd=2027-04-30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer TOKEN")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return sums")
    public void testGetSummarySum() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                        get("/expenses/zone/" + ZONE_TEST_ID + "/sum?currency=PLN&groupBy=category&periodStart=2025-04-01&periodEnd=2027-04-30")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer TOKEN")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.sum[0].categoryName").value("Test category"))
                .andExpect(jsonPath("$.sum[0].categoryValue").value(100.00))
                .andExpect(jsonPath("$.sum[0].overallSum").value(100.00));
    }

    @Test
    @DisplayName("Should return NotFound in sums when groupBy i unknown")
    public void testGetSummarySumNotFound() throws Exception {
        this.insertTestUser();
        this.insertTextExpenses();
        this.mockMvc.perform(
                get("/expenses/zone/" + ZONE_TEST_ID + "/sum?currency=PLN&groupBy=unknown&periodStart=2025-04-01&periodEnd=2027-04-30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer TOKEN")
        ).andExpect(status().isNotFound());
    }

    private void insertTestUser() {
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (1, 1, 'abc');");
        this.insertTestData("INSERT INTO app_data.categories (category_id, name, description, zone_id, created_at, updated_at) VALUES " +
                "(1,'Test category', 'test description', 'abc', NOW(), NOW());");
    }

    private void insertTextExpenses() {
        this.insertTestData("INSERT INTO app_data.expenses (expense_id, user_id, name, currency, description, value, created_at, updated_at) VALUES (1, 1, 'Test expense', 'PLN', 'TestDescription', 100, NOW(), NOW());");
        this.insertTestData("INSERT INTO app_data.exp_categories (category_id, expense_id) VALUES (1,1);");
    }


}
