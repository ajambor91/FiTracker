package aj.FiTracker.FiTrackerExpenses.Repositories;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpensesRepository extends JpaRepository<Expense, Long> {

    @Query(value = "SELECT app_data.expenses.name AS expenseName, CAST(app_data.expenses.value as DECIMAL) as expenseValue, app_data.categories.name as categoryName, CAST(app_data.expenses.created_at as DATE) as date " +
            "FROM app_data.expenses " +
            "INNER JOIN app_data.exp_categories ON app_data.exp_categories.expense_id = app_data.expenses.expense_id " +
            "INNER JOIN app_data.categories ON app_data.categories.category_id = app_data.exp_categories.category_id " +
            "WHERE app_data.categories.zone_id = :zoneId AND " +
            "app_data.expenses.currency = :currency AND " +
            "app_data.expenses.created_at BETWEEN :from AND :to " +
            "ORDER BY expenseValue DESC LIMIT 10",
            nativeQuery = true)
    List<TopExpense> getTopExpenses(@Param("zoneId") String zoneId, @Param("currency") String currency, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT app_data.categories.name as category_name, SUM(app_data.expenses.value) as expenses_value " +
            "FROM app_data.expenses " +
            "INNER JOIN app_data.exp_categories ON app_data.exp_categories.expense_id = app_data.expenses.expense_id " +
            "INNER JOIN app_data.categories ON app_data.categories.category_id = app_data.exp_categories.category_id " +
            "WHERE app_data.categories.zone_id = :zoneId AND " +
            "app_data.expenses.currency = :currency AND " +
            "app_data.expenses.created_at BETWEEN :from AND :to " +
            "GROUP BY app_data.categories.category_id ",
            nativeQuery = true)
    List<TotalSummaryByCategory> getSummaryByCategory(@Param("zoneId") String zoneId, @Param("currency") String currency, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT " +
            "   CAST(app_data.expenses.created_at AS DATE) as date, " +
            "   CAST(SUM(app_data.expenses.value) AS DECIMAL) as expensesValue " +
            "FROM app_data.expenses " +
            "INNER JOIN app_data.exp_categories ON app_data.exp_categories.expense_id = app_data.expenses.expense_id " +
            "INNER JOIN app_data.categories ON app_data.categories.category_id = app_data.exp_categories.category_id " +
            "WHERE app_data.categories.zone_id = :zoneId AND " +
            "   app_data.expenses.currency = :currency AND " +
            "   app_data.expenses.created_at BETWEEN :from AND :to " +
            "GROUP BY date " +
            "ORDER BY date",
            nativeQuery = true)
    List<TotalSummaryByDate> getSummaryByDay(@Param("zoneId") String zoneId, @Param("currency") String currency, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT " +
            "app_data.categories.name as categoryName, " +
            "CAST (SUM(app_data.expenses.value) as DECIMAL) as categoryValue, " +
            "CAST (SUM(SUM(app_data.expenses.value)) OVER () as DECIMAL) as overallSum " +
            "FROM app_data.expenses " +
            "INNER JOIN app_data.exp_categories ON app_data.exp_categories.expense_id = app_data.expenses.expense_id " +
            "INNER JOIN app_data.categories ON app_data.categories.category_id = app_data.exp_categories.category_id " +
            "WHERE app_data.categories.zone_id = :zoneId AND " +
            "app_data.expenses.currency = :currency AND " +
            "app_data.expenses.created_at BETWEEN :from AND :to " +
            "GROUP BY app_data.categories.category_id "
            , nativeQuery = true)
    List<ExpensesSum> getExpensesSum(@Param("zoneId") String zoneId, @Param("currency") String currency, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
