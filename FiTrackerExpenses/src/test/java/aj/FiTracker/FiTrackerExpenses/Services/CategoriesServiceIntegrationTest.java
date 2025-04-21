package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.UpdateCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Exceptions.CannotFindCategoriesException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.CategoryAlreadyExistsException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("integration")
public class CategoriesServiceIntegrationTest extends AbstractIntegrationTest {
    private final CategoriesService categoriesService;
    private Jwt jwtMock;
    private Authentication authenticationMock;

    @Autowired
    public CategoriesServiceIntegrationTest(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @BeforeEach
    public void setup() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(USER_TEST_ID));
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(USER_TEST_NAME);
        this.authenticationMock = mock(Authentication.class);
        when(this.authenticationMock.getPrincipal()).thenReturn(this.jwtMock);
        this.insertTestData("INSERT INTO app_data.app_user (id, app_user_id, app_zone_id) VALUES (1, 1, 'abc');");
    }

    @AfterEach
    public void clean() {
        this.truncateTable("app_data.expenses");
        this.truncateTable("app_data.categories");
        this.truncateTable("app_data.app_user");
        this.truncateTable("app_data.exp_categories");
    }

    @Test
    @DisplayName("Should add and return category")
    public void testAddCategory() {
        AddCategoryRequest addCategoryRequest = TestUtils.createAddCategoryRequest();
        Category category = this.categoriesService.addCategory(addCategoryRequest, ZONE_TEST_ID, this.authenticationMock);
        assertEquals(ZONE_TEST_ID, category.getZoneId());
        assertEquals(CATEGORY_TEST_ID, category.getCategoryId());
        assertEquals(CATEGORY_TEST_NAME, category.getName());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertInstanceOf(LocalDateTime.class, category.getCreatedAt());
        assertInstanceOf(LocalDateTime.class, category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw category already exists")
    public void testAddCategoryCategoryAlreadyExistsException() {
        this.insertTestCategory();
        AddCategoryRequest addCategoryRequest = TestUtils.createAddCategoryRequest();
        assertThrows(CategoryAlreadyExistsException.class, () -> {
            Category category = this.categoriesService.addCategory(addCategoryRequest, ZONE_TEST_ID, this.authenticationMock);
        });
    }

    @Test
    @DisplayName("Should throw user does not have privileges to zone")
    public void testAddCategoryCategoryUserUnauthorizedException() {
        this.insertTestCategory();
        AddCategoryRequest addCategoryRequest = TestUtils.createAddCategoryRequest();
        assertThrows(UserUnauthorizedException.class, () -> {
            Category category = this.categoriesService.addCategory(addCategoryRequest, "Invalid zone id", this.authenticationMock);
        });
    }

    @Test
    @DisplayName("Should update category")
    public void testUpdateCategory() {
        this.insertTestCategory();
        UpdateCategoryRequest updateCategoryRequest = TestUtils.createUpdateCategoryRequestAnotherData();
        Category category = this.categoriesService.updateCategory(CATEGORY_TEST_ID, ZONE_TEST_ID, updateCategoryRequest, this.authenticationMock);
        assertEquals(CATEGORY_TEST_NAME_SECOND, category.getName());
        assertEquals(CATEGORY_TEST_DESCRIPTION, category.getDescription());
        assertNotNull(category.getUpdatedAt());
        assertNotNull(category.getCreatedAt());
        assertInstanceOf(LocalDateTime.class, category.getUpdatedAt());
        assertInstanceOf(LocalDateTime.class, category.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw not found category when trying to update")
    public void testUpdateCategoryNotFound() {
        UpdateCategoryRequest updateCategoryRequest = TestUtils.createUpdateCategoryRequest();
        CannotFindCategoriesException cannotFindCategoriesException = assertThrows(CannotFindCategoriesException.class, () -> {
            this.categoriesService.updateCategory(CATEGORY_TEST_ID, ZONE_TEST_ID, updateCategoryRequest, this.authenticationMock);
        });
        assertEquals("Cannot find category " + CATEGORY_TEST_ID + " for zone: " + ZONE_TEST_ID, cannotFindCategoriesException.getMessage());
    }

    @Test
    @DisplayName("Should throw not found category when trying to update")
    public void testUpdateCategoryUnauthorized() {
        UpdateCategoryRequest updateCategoryRequest = TestUtils.createUpdateCategoryRequest();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.categoriesService.updateCategory(CATEGORY_TEST_ID, "UNKNOWN_ZONE", updateCategoryRequest, this.authenticationMock);
        });
        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());
    }

    @Test
    @DisplayName("Should get all user categories")
    public void testGetAllCategories() {
        this.insertTestCategory();
        List<CategoryDb> categoryDbList = this.categoriesService.getAllCategories(ZONE_TEST_ID, this.authenticationMock);
        assertEquals(1, categoryDbList.size());
        CategoryDb categoryDb = categoryDbList.getFirst();
        assertEquals(CATEGORY_TEST_ID, categoryDb.categoryId());
        assertEquals(CATEGORY_TEST_DESCRIPTION, categoryDb.description());
        assertEquals(CATEGORY_TEST_NAME, categoryDb.name());
    }

    @Test
    @DisplayName("Should throw unauthorized when getting all user categories")
    public void testGetAllCategoriesUnathorized() {
        this.insertTestCategory();
        UserUnauthorizedException userUnauthorizedException = assertThrows(UserUnauthorizedException.class, () -> {
            this.categoriesService.getAllCategories("INVALID_ZONE", this.authenticationMock);
        });
        assertEquals("User does not have privileges to zone", userUnauthorizedException.getMessage());

    }

    @Test
    @DisplayName("Should return empty categories listy")
    public void testGetAllCategoriesEmptyList() {

        List<CategoryDb> categoryDbList = this.categoriesService.getAllCategories(ZONE_TEST_ID, this.authenticationMock);
        assertTrue(categoryDbList.isEmpty());

    }

    private void insertTestCategory() {
        this.insertTestData("INSERT INTO app_data.categories (category_id, name, description, zone_id, created_at, updated_at) VALUES " +
                "(1,'zone_test', 'Test description', 'abc', NOW(), NOW());");
    }

}
