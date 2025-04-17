package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.UpdateCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Repositories.CategoriesRepository;
import aj.FiTracker.FiTrackerExpenses.Security.JWTClaimsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final MembersService membersService;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository, MembersService membersService) {
        this.categoriesRepository = categoriesRepository;
        this.membersService = membersService;
    }

    public Category addCategory(AddCategoryRequest addCategoryRequest, String zoneId, Authentication authentication) {
        try {

            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            Category categoryToAdd = new Category(addCategoryRequest, zoneId);
            this.categoriesRepository.save(categoryToAdd);
            return categoryToAdd;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional
    public Category updateCategory(long categoryId, String zoneId, UpdateCategoryRequest updateCategoryRequest, Authentication authentication) {
        try {

            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            Category categoryToAdd = new Category(updateCategoryRequest, categoryId, zoneId);
            this.categoriesRepository.save(categoryToAdd);
            return categoryToAdd;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryDb> getAllCategories(String zoneId, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);

            return this.categoriesRepository.findByZoneId(zoneId);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }
}
