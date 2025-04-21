package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTrackerExpenses.SecurityUtils.WithMockJwt;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockJwt
@ActiveProfiles("integration")
@Tag("integration")
@SpringBootTest
public class CategoriesControllerIntegrationTest extends AbstractIntegrationTest {

    private final MockMvc categoriesController;

    @Autowired
    public CategoriesControllerIntegrationTest(MockMvc categoriesController) {
        this.categoriesController = categoriesController;
    }

    @BeforeEach
    public void setup() {
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (1, 1, 'abc');");

    }

    @AfterEach
    public void cleanDatabase() {
        this.truncateTable("app_data.expenses");
        this.truncateTable("app_data.categories");
        this.truncateTable("app_data.app_user");
        this.truncateTable("app_data.exp_categories");
    }

    @Test
    @DisplayName("Should add category")
    public void testCreateCategory() throws Exception {
        this.categoriesController.perform(
                        post("/categories/category/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                                .content(
                                        """
                                                {
                                                    "zoneId":"abc",
                                                    "name": "zone_test",
                                                    "description": "Test description"
                                                }
                                                """
                                )

                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("zone_test"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.zoneId").value("abc"))
                .andExpect(jsonPath("$.categoryId").exists());
    }

    @Test
    @DisplayName("Should throw user does not have privileges when addi category")
    public void testCreateCategoryUnauthorized() throws Exception {
        this.categoriesController.perform(
                post("/categories/category/zone/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(
                                """
                                        {
                                            "zoneId":"abc",
                                            "name": "zone_test",
                                            "description": "Test description"
                                        }
                                        """
                        )

        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should throw BadRequest when adding category with invalid data")
    public void testCreateCategoryBadRequest() throws Exception {
        this.categoriesController.perform(
                post("/categories/category/zone/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(
                                """
                                        {
                                            "zoneId":"abc",
                                            "description": "Test description"
                                        }
                                        """
                        )

        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update category")
    public void testUpdateCategory() throws Exception {
        this.insertTestCategory();
        this.categoriesController.perform(
                        patch("/categories/category/" + CATEGORY_TEST_ID + "/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                                .content(
                                        """
                                                {   
                                                    "name": "zone_test_updated",
                                                    "description": "Test description_updated"
                                                }
                                                """
                                )

                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("zone_test_updated"))
                .andExpect(jsonPath("$.description").value("Test description_updated"))
                .andExpect(jsonPath("$.zoneId").value("abc"));
    }

    @Test
    @DisplayName("Should throw NotFound")
    public void testUpdateCategoryNotFound() throws Exception {
        this.insertTestCategory();
        this.categoriesController.perform(
                patch("/categories/category/" + CATEGORY_TEST_ID_SECOND + "/zone/" + ZONE_TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(
                                """
                                        {   
                                            "name": "zone_test_updated",
                                            "description": "Test description_updated"
                                        }
                                        """
                        )

        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should throw BadRequest on invalid payload when updating category")
    public void testUpdateCategoryBadRequest() throws Exception {
        this.insertTestCategory();
        this.categoriesController.perform(
                patch("/categories/category/" + CATEGORY_TEST_ID_SECOND + "/zone/" + ZONE_TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(
                                """
                                        {   
                                            "description": "Test description_updated"
                                        }
                                        """
                        )

        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return all categories for zone")
    public void testGetAllCategories() throws Exception {
        this.insertTestCategory();
        this.categoriesController.perform(
                        get("/categories/categories/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isNotEmpty())
                .andExpect(jsonPath("$.categories[0].categoryId").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Test category"))
                .andExpect(jsonPath("$.categories[0].description").value("test description"));
    }

    @Test
    @DisplayName("Should return empty categories list for zone")
    public void testGetAllCategoriesEmptyList() throws Exception {
        this.categoriesController.perform(
                        get("/categories/categories/zone/" + ZONE_TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isEmpty());
    }


    private void insertTestCategory() {
        this.insertTestData("INSERT INTO app_data.categories (category_id, name, description, zone_id, created_at, updated_at) VALUES " +
                "(1,'Test category', 'test description', 'abc', NOW(), NOW());");
    }
}
