package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.*;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Services.CategoriesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    private final Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        logger.info("Initializing CategoriesController.");
        this.categoriesService = categoriesService;
    }

    @PostMapping("/category/zone/{zoneId}")
    public ResponseEntity<AddCategoryResponse> createCategory(Authentication authentication, @NotBlank @PathVariable String zoneId, @Valid @RequestBody AddCategoryRequest addCategoryRequest) {
        logger.info("Received request to create a new category for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Create category request: {}", addCategoryRequest);
        Category category = this.categoriesService.addCategory(addCategoryRequest, zoneId, authentication);
        logger.info("Successfully created category with ID: {} for zone {}.", category.getCategoryId(), zoneId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddCategoryResponse(category));
    }

    @GetMapping("/categories/zone/{zoneId}")
    public ResponseEntity<GetCategoriesResponse> getAllCategories(Authentication authentication, @NotBlank @PathVariable String zoneId) {
        logger.info("Received request to get all categories for zone {}. User: {}", zoneId, authentication.getName());
        List<CategoryDb> categories = this.categoriesService.getAllCategories(zoneId, authentication);
        logger.info("Successfully retrieved {} categories for zone {}.", categories.size(), zoneId);
        return ResponseEntity.status(HttpStatus.OK).body(new GetCategoriesResponse(categories));
    }

    @PatchMapping("/category/{categoryId}/zone/{zoneId}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            Authentication authentication,
            @RequestBody @Valid UpdateCategoryRequest updateCategoryRequest,
            @NotNull @PathVariable Long categoryId,
            @NotBlank @PathVariable String zoneId) {
        logger.info("Received request to update category with ID {} in zone {}. User: {}", categoryId, zoneId, authentication.getName());
        logger.debug("Update category request: {}", updateCategoryRequest);
        Category category = this.categoriesService.updateCategory(categoryId, zoneId, updateCategoryRequest, authentication);
        logger.info("Successfully updated category with ID: {} in zone {}.", category.getCategoryId(), zoneId);
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateCategoryResponse(category));

    }

}