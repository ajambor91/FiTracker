package aj.FiTracker.FiTrackerExpenses.Controllers;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.*;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Services.CategoriesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping("/category/zone/{zoneId}")
    public ResponseEntity<AddCategoryResponse> createCategory(Authentication authentication, @NotBlank @PathVariable String zoneId, @Valid @RequestBody AddCategoryRequest addCategoryRequest) {
        Category category = this.categoriesService.addCategory(addCategoryRequest, zoneId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddCategoryResponse(category));
    }

    @GetMapping("/categories/zone/{zoneId}")
    public ResponseEntity<GetCategoriesResponse> getAllCategories(Authentication authentication, @NotBlank @PathVariable String zoneId) {
        List<CategoryDb> categories = this.categoriesService.getAllCategories(zoneId, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new GetCategoriesResponse(categories));
    }

    @PatchMapping("/category/{categoryId}/zone/{zoneId}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            Authentication authentication,
            @RequestBody @Valid UpdateCategoryRequest updateCategoryRequest,
            @NotBlank @PathVariable long categoryId,
            @NotBlank @PathVariable String zoneId) {
        Category category = this.categoriesService.updateCategory(categoryId, zoneId, updateCategoryRequest, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateCategoryResponse(category));

    }

}