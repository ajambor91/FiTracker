package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.UpdateCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Repositories.CategoriesRepository;
import aj.FiTracker.FiTrackerExpenses.Security.JWTClaimsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CategoriesService {
    private final Logger logger = LoggerFactory.getLogger(CategoriesService.class);

    private final CategoriesRepository categoriesRepository;
    private final MembersService membersService;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository, MembersService membersService) {
        logger.info("Initializing CategoriesService.");
        this.categoriesRepository = categoriesRepository;
        this.membersService = membersService;
    }

    @Transactional
    public Category addCategory(AddCategoryRequest addCategoryRequest, String zoneId, Authentication authentication) {
        logger.info("Attempting to add a new category to zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Add category request: {}", addCategoryRequest);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            logger.debug("Extracted claims for user ID: {}", jwtClaims.userId());

            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);

            Category categoryToAdd = new Category(addCategoryRequest, zoneId);
            logger.debug("Created new category object: {}", categoryToAdd);

            Category savedCategory = this.categoriesRepository.save(categoryToAdd);
            logger.info("Successfully added category with ID: {} to zone {}.", savedCategory.getCategoryId(), zoneId);
            logger.debug("Saved category: {}", savedCategory);

            return savedCategory;
        } catch (Exception e) {
            logger.error("An internal server error occurred while adding category to zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional
    public Category updateCategory(long categoryId, String zoneId, UpdateCategoryRequest updateCategoryRequest, Authentication authentication) {
        logger.info("Attempting to update category with ID {} in zone {}. User: {}", categoryId, zoneId, authentication.getName());
        logger.debug("Update category request: {}", updateCategoryRequest);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            logger.debug("Extracted claims for user ID: {}", jwtClaims.userId());

            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);


            logger.warn("Updating category by creating a new object with provided ID and request data.");
            Category categoryToUpdate = new Category(updateCategoryRequest, categoryId, zoneId);
            logger.debug("Created category object for update: {}", categoryToUpdate);


            Category updatedCategory = this.categoriesRepository.save(categoryToUpdate);
            logger.info("Successfully updated category with ID: {} in zone {}.", updatedCategory.getCategoryId(), zoneId);
            logger.debug("Updated category: {}", updatedCategory);

            return updatedCategory;
        } catch (Exception e) {
            logger.error("An internal server error occurred while updating category with ID {} in zone {}.", categoryId, zoneId, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryDb> getAllCategories(String zoneId, Authentication authentication) {
        logger.info("Attempting to get all categories for zone {}. User: {}", zoneId, authentication.getName());
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            logger.debug("Extracted claims for user ID: {}", jwtClaims.userId());

            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);

            List<CategoryDb> categories = this.categoriesRepository.findByZoneId(zoneId);
            logger.info("Successfully retrieved {} categories for zone {}.", categories.size(), zoneId);
            logger.debug("Retrieved categories: {}", categories);
            return categories;
        } catch (Exception e) {
            logger.error("An internal server error occurred while getting all categories for zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }
}