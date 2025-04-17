package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.ExpensesSum;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TopExpense;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByDate;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddExpenseRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.CannotFindCategoriesException;
import aj.FiTracker.FiTrackerExpenses.Repositories.CategoriesRepository;
import aj.FiTracker.FiTrackerExpenses.Repositories.ExpensesRepository;
import aj.FiTracker.FiTrackerExpenses.Security.JWTClaimsUtil;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final CategoriesRepository categoriesRepository;
    private final MembersService membersService;

    @Autowired
    public ExpensesService(
            ExpensesRepository expensesRepository,
            CategoriesRepository categoriesRepository,
            MembersService membersService
    ) {
        this.expensesRepository = expensesRepository;
        this.categoriesRepository = categoriesRepository;
        this.membersService = membersService;
    }

    @Transactional
    public Expense addExpense(AddExpenseRequest addExpenseRequest, String zoneId, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            List<Category> categories = new ArrayList<>();
            if (addExpenseRequest.getCategoriesIds() != null && !addExpenseRequest.getCategoriesIds().isEmpty()) {
                categories = this.categoriesRepository.findByCategoryIdInAndZoneId(addExpenseRequest.getCategoriesIds(), zoneId);
            }
            if (categories.isEmpty()) {
                throw new CannotFindCategoriesException("Cannot find categories: " + addExpenseRequest.getCategoriesIds());
            }
            Expense expense = new Expense(addExpenseRequest, user.getUserId(), categories);
            Expense finalExpense = this.expensesRepository.saveAndFlush(expense);
            categories.forEach(category -> {
                category.getExpenses().add(finalExpense);
            });
            this.categoriesRepository.saveAllAndFlush(categories);
            return finalExpense;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<TotalSummaryByCategory> getSummaryByCategory(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            return this.expensesRepository.getSummaryByCategory(zoneId, curr, from, end);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<ExpensesSum> getSummarySum(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            return this.expensesRepository.getExpensesSum(zoneId, curr, from, end);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<TopExpense> getSummaryTopExpenses(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            return this.expensesRepository.getTopExpenses(zoneId, curr, from, end);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<TotalSummaryByDate> getSummaryByDate(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            return this.expensesRepository.getSummaryByDay(zoneId, curr, from, end);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getCurrency(String currency) {
        if (currency == null) {
            return "PLN";
        } else {
            return currency;
        }
    }

    private LocalDateTime getPeriod(String period, boolean isLastDay) {
        if (period == null) {
            LocalDate now = LocalDate.now();
            if (!isLastDay) {
                return now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            } else {
                return now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
            }
        } else {
            LocalDate localDate = LocalDate.parse(period);
            if (!isLastDay) {
                return localDate.atStartOfDay();
            } else {
                return localDate.atTime(LocalTime.MAX);
            }
        }
    }

}
