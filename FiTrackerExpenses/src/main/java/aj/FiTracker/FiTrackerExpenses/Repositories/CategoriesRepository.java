package aj.FiTracker.FiTrackerExpenses.Repositories;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT app_data.categories.category_id as categoryId, app_data.categories.name, app_data.categories.description FROM app_data.categories WHERE app_data.categories.zone_id = :zoneId", nativeQuery = true)
    List<CategoryDb> findByZoneId(@Param("zoneId") String zoneId);

    List<Category> findByCategoryIdInAndZoneId(List<Long> categoriesId, String zoneId);
}
