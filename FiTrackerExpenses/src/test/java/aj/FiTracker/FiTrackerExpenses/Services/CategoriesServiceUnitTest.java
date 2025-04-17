package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.UpdateCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Repositories.CategoriesRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class CategoriesServiceUnitTest {
    private Authentication authenticationMock;
    private Jwt jwtMock;

    private MembersService membersServiceMock;
    private CategoriesRepository categoriesRepositoryMock;
    private CategoriesService categoriesService;

    @BeforeEach
    public void setup() {
        this.jwtMock = mock(Jwt.class);
        when(this.jwtMock.getClaimAsString(eq("sub"))).thenReturn(String.valueOf(USER_TEST_ID));
        when(this.jwtMock.getClaimAsString(eq("name"))).thenReturn(USER_TEST_NAME);
        this.authenticationMock = mock(Authentication.class);
        when(this.authenticationMock.getPrincipal()).thenReturn(this.jwtMock);
        this.categoriesRepositoryMock = mock(CategoriesRepository.class);
        this.membersServiceMock = mock(MembersService.class);
        when(this.membersServiceMock.getUserByZoneIdAndId(anyLong(), any(String.class))).thenReturn(TestUtils.createTestUser());
        this.categoriesService = new CategoriesService(this.categoriesRepositoryMock, this.membersServiceMock);
    }

    @Test
    @DisplayName("Should add category and them return")
    public void testAddCategory() {
        AddCategoryRequest addCategoryRequest = TestUtils.createAddCategoryRequest();
        when(this.categoriesRepositoryMock.save(any(Category.class))).thenReturn(TestUtils.createTestCategory());
        Category category = this.categoriesService.addCategory(addCategoryRequest, ZONE_TEST_ID, this.authenticationMock);
        verify(this.categoriesRepositoryMock, times(1)).save(any(Category.class));
        assertInstanceOf(Category.class, category);
        assertEquals(CATEGORY_TEST_ID, category.getCategoryId());
        assertEquals(CATEGORY_TEST_NAME, category.getName());
        assertEquals(ZONE_TEST_ID, category.getZoneId());
    }

    @Test
    @DisplayName("Should throw InternalServerException when adding Category")
    public void testAddCategoryInternalServerException() {
        AddCategoryRequest addCategoryRequest = TestUtils.createAddCategoryRequest();
        when(this.categoriesRepositoryMock.save(any(Category.class))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.categoriesService.addCategory(addCategoryRequest, ZONE_TEST_ID, this.authenticationMock);
            verify(this.categoriesRepositoryMock, times(1)).save(any(Category.class));

        });
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Boom!", exception.getMessage());
    }

    @Test
    @DisplayName("Should update category and return")
    public void testUpdateCategory() {
        UpdateCategoryRequest updateCategoryRequest = TestUtils.createUpdateCategoryRequest();
        when(this.categoriesRepositoryMock.save(any(Category.class))).thenReturn(TestUtils.createTestCategory());
        Category category = this.categoriesService.updateCategory(CATEGORY_TEST_ID, ZONE_TEST_ID, updateCategoryRequest, this.authenticationMock);
        verify(this.categoriesRepositoryMock, times(1)).save(any(Category.class));
        assertInstanceOf(Category.class, category);
        assertEquals(CATEGORY_TEST_ID, category.getCategoryId());
        assertEquals(CATEGORY_TEST_NAME, category.getName());
        assertEquals(ZONE_TEST_ID, category.getZoneId());
    }

    @Test
    @DisplayName("Should throw InternalServerException when updating Category")
    public void testUpdateCategoryInternalServerException() {
        UpdateCategoryRequest updateCategoryRequest = TestUtils.createUpdateCategoryRequest();
        when(this.categoriesRepositoryMock.save(any(Category.class))).thenThrow(new RuntimeException("Boom!"));
        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.categoriesService.updateCategory(CATEGORY_TEST_ID, ZONE_TEST_ID, updateCategoryRequest, this.authenticationMock);
            verify(this.categoriesRepositoryMock, times(1)).save(any(Category.class));
        });
        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Boom!", exception.getMessage());
    }

    @Test
    @DisplayName("Should return all categories for a given zone")
    public void testGetAllCategories() {
        List<CategoryDb> expectedCategories = List.of(createTestCategoryDb(), createTestCategoryDbSecond());
        when(this.categoriesRepositoryMock.findByZoneId(eq(ZONE_TEST_ID))).thenReturn(expectedCategories);

        List<CategoryDb> actualCategories = this.categoriesService.getAllCategories(String.valueOf(ZONE_TEST_ID), this.authenticationMock);

        verify(this.membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(String.valueOf(ZONE_TEST_ID)));
        verify(this.categoriesRepositoryMock, times(1)).findByZoneId(eq(ZONE_TEST_ID));
        assertEquals(expectedCategories.size(), actualCategories.size());
        assertTrue(actualCategories.containsAll(expectedCategories));
    }

    @Test
    @DisplayName("Should return an empty set if no categories exist for a zone")
    public void testGetAllCategoriesNoCategories() {
        when(this.categoriesRepositoryMock.findByZoneId(eq(ZONE_TEST_ID))).thenReturn(Collections.emptyList());

        List<CategoryDb> actualCategories = this.categoriesService.getAllCategories(String.valueOf(ZONE_TEST_ID), this.authenticationMock);

        verify(this.membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(String.valueOf(ZONE_TEST_ID)));
        verify(this.categoriesRepositoryMock, times(1)).findByZoneId(eq(ZONE_TEST_ID));
        assertTrue(actualCategories.isEmpty());
    }

    @Test
    @DisplayName("Should throw InternalServerException when retrieving all categories fails")
    public void testGetAllCategoriesInternalServerException() {
        when(this.categoriesRepositoryMock.findByZoneId(eq(ZONE_TEST_ID))).thenThrow(new RuntimeException("Database error!"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            this.categoriesService.getAllCategories(String.valueOf(ZONE_TEST_ID), this.authenticationMock);
            verify(this.membersServiceMock, times(1)).getUserByZoneIdAndId(eq(USER_TEST_ID), eq(String.valueOf(ZONE_TEST_ID)));
            verify(this.categoriesRepositoryMock, times(1)).findByZoneId(eq(ZONE_TEST_ID));
        });

        assertInstanceOf(InternalServerException.class, exception);
        assertEquals("Database error!", exception.getMessage());
    }
}
