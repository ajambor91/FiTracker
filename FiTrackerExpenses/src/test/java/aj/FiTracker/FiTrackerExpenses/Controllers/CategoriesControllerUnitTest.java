package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.*;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Services.CategoriesService;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Tag("unit")
@ActiveProfiles("unit")
@ExtendWith(MockitoExtension.class)
public class CategoriesControllerUnitTest {
    private Authentication authenticationMock;
    private CategoriesService categoriesServiceMock;
    private CategoriesController categoriesController;

    @BeforeEach
    public void setup() {
        this.authenticationMock = mock(Authentication.class);
        this.categoriesServiceMock = mock(CategoriesService.class);
        this.categoriesController = new CategoriesController(this.categoriesServiceMock);
    }

    @Test
    @DisplayName("Should add category")
    public void testCreateCategory() {
        Category category = TestUtils.createTestCategory();
        AddCategoryRequest addCategoryRequest = TestUtils.createAddCategoryRequest();
        when(this.categoriesServiceMock.addCategory(
                eq(addCategoryRequest),
                eq(ZONE_TEST_ID),
                any(Authentication.class))).thenReturn(category);
        ResponseEntity<AddCategoryResponse> addCategoryResponse = this.categoriesController.createCategory(this.authenticationMock, ZONE_TEST_ID, addCategoryRequest);
        verify(this.categoriesServiceMock, times(1)).addCategory(
                eq(addCategoryRequest),
                eq(ZONE_TEST_ID),
                any(Authentication.class));

        assertEquals(HttpStatus.CREATED, addCategoryResponse.getStatusCode());
        AddCategoryResponse addCategoryResponseBody = addCategoryResponse.getBody();
        assertEquals(ZONE_TEST_ID, addCategoryResponseBody.getZoneId());
        assertEquals(CATEGORY_TEST_NAME, addCategoryResponseBody.getName());
        assertEquals(CATEGORY_TEST_ID, addCategoryResponseBody.getCategoryId());
    }

    @Test
    @DisplayName("Should get all users categories")
    public void testGetAllCategories() {
        List<CategoryDb> categoryDbs = List.of(TestUtils.createTestCategoryDb());
        when(this.categoriesServiceMock.getAllCategories(
                eq(ZONE_TEST_ID),
                any(Authentication.class))).thenReturn(categoryDbs);
        ResponseEntity<GetCategoriesResponse> categories = this.categoriesController.getAllCategories(this.authenticationMock, ZONE_TEST_ID);
        verify(this.categoriesServiceMock, times(1)).getAllCategories(
                eq(ZONE_TEST_ID),
                any(Authentication.class));

        assertEquals(HttpStatus.OK, categories.getStatusCode());
        GetCategoriesResponse response = categories.getBody();
        assertInstanceOf(List.class, response.getCategories());
        assertEquals(1, response.getCategories().size());
        CategoryDb resCategory = response.getCategories().getFirst();
        assertEquals(CATEGORY_TEST_ID, resCategory.categoryId());
        assertEquals(CATEGORY_TEST_NAME, resCategory.name());
        assertEquals(CATEGORY_TEST_DESCRIPTION, resCategory.description());
    }

    @Test
    @DisplayName("Should update category")
    public void testUpdateCategory() {
        UpdateCategoryRequest updateCategoryRequest = TestUtils.createUpdateCategoryRequest();
        Category category = TestUtils.createTestCategory();
        when(this.categoriesServiceMock.updateCategory(
                eq(CATEGORY_TEST_ID),
                eq(ZONE_TEST_ID),
                any(UpdateCategoryRequest.class),
                any(Authentication.class)
        )).thenReturn(category);
        ResponseEntity<UpdateCategoryResponse> response = this.categoriesController.updateCategory(
                this.authenticationMock,
                updateCategoryRequest,
                CATEGORY_TEST_ID,
                ZONE_TEST_ID
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UpdateCategoryResponse updateCategoryResponse = response.getBody();
        assertEquals(CATEGORY_TEST_NAME, updateCategoryResponse.getName());
        assertEquals(ZONE_TEST_ID, updateCategoryResponse.getZoneId());

    }


}
