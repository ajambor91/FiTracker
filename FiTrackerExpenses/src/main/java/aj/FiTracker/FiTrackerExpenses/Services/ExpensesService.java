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
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Repositories.CategoriesRepository;
import aj.FiTracker.FiTrackerExpenses.Repositories.ExpensesRepository;
import aj.FiTracker.FiTrackerExpenses.Security.JWTClaimsUtil;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(ExpensesService.class);
    private final ExpensesRepository expensesRepository;
    private final CategoriesRepository categoriesRepository;
    private final MembersService membersService;

    @Autowired
    public ExpensesService(
            ExpensesRepository expensesRepository,
            CategoriesRepository categoriesRepository,
            MembersService membersService
    ) {
        logger.info("Initializing ExpensesService.");
        this.expensesRepository = expensesRepository;
        this.categoriesRepository = categoriesRepository;
        this.membersService = membersService;
    }

    @Transactional
    public Expense addExpense(AddExpenseRequest addExpenseRequest, String zoneId, Authentication authentication) {
        logger.info("Attempting to add a new expense to zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Add expense request: {}", addExpenseRequest);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            logger.debug("Extracted claims for user ID: {}", jwtClaims.userId());

            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User found for zone ID {} and user ID {}.", zoneId, jwtClaims.userId());

            List<Category> categories = new ArrayList<>();
            if (addExpenseRequest.getCategoriesIds() != null && !addExpenseRequest.getCategoriesIds().isEmpty()) {
                logger.debug("Fetching categories with IDs: {} for zone ID: {}", addExpenseRequest.getCategoriesIds(), zoneId);
                categories = this.categoriesRepository.findByCategoryIdInAndZoneId(addExpenseRequest.getCategoriesIds(), zoneId);
                logger.debug("Found {} categories.", categories.size());
            }
            if (categories.isEmpty() && addExpenseRequest.getCategoriesIds() != null && !addExpenseRequest.getCategoriesIds().isEmpty()) {
                logger.warn("Cannot find any categories with IDs: {} for zone ID: {}", addExpenseRequest.getCategoriesIds(), zoneId);
                throw new CannotFindCategoriesException("Cannot find categories: " + addExpenseRequest.getCategoriesIds());
            }

            Expense expense = new Expense(addExpenseRequest, user.getUserId(), categories);
            logger.debug("Created new expense object: {}", expense);

            Expense finalExpense = this.expensesRepository.saveAndFlush(expense);
            logger.info("Successfully added expense with ID: {}", finalExpense.getExpenseId());
            logger.debug("Saved expense: {}", finalExpense);

            categories.forEach(category -> {
                category.getExpenses().add(finalExpense);
            });
            if (!categories.isEmpty()) {
                this.categoriesRepository.saveAllAndFlush(categories);
                logger.debug("Updated {} categories with the new expense.", categories.size());
            }

            return finalExpense;
        } catch (CannotFindCategoriesException e) {
            logger.warn("Failed to add expense due to missing categories: {}", e.getMessage());
            throw e;
        } catch (UserUnauthorizedException e) {
            logger.warn("User unauthorized to add expense in zone {}: {}", zoneId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while adding expense to zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<TotalSummaryByCategory> getSummaryByCategory(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        logger.info("Getting summary by category for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, periodStart={}, periodEnd={}", currency, periodStart, periodEnd);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);

            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            logger.debug("Calculated period: from={} to={}. Currency: {}", from, end, curr);

            List<TotalSummaryByCategory> summary = this.expensesRepository.getSummaryByCategory(zoneId, curr, from, end);
            logger.info("Successfully retrieved summary by category for zone {}. Found {} entries.", zoneId, summary.size());
            logger.debug("Summary by category: {}", summary);
            return summary;
        } catch (UserUnauthorizedException e) {
            logger.warn("User unauthorized to get summary by category in zone {}: {}", zoneId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while getting summary by category for zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<ExpensesSum> getSummarySum(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        logger.info("Getting total summary sum for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, periodStart={}, periodEnd={}", currency, periodStart, periodEnd);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);

            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            logger.debug("Calculated period: from={} to={}. Currency: {}", from, end, curr);

            List<ExpensesSum> summary = this.expensesRepository.getExpensesSum(zoneId, curr, from, end);
            logger.info("Successfully retrieved total summary sum for zone {}. Found {} entries.", zoneId, summary.size());
            logger.debug("Total summary sum: {}", summary);
            return summary;
        } catch (UserUnauthorizedException userUnauthorizedException) {
            logger.warn("User unauthorized to get total summary sum in zone {}: {}", zoneId, userUnauthorizedException.getMessage());
            throw userUnauthorizedException;
        } catch (Exception e) {
            logger.error("An internal server error occurred while getting total summary sum for zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<TopExpense> getSummaryTopExpenses(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        logger.info("Getting top expenses summary for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, periodStart={}, periodEnd={}", currency, periodStart, periodEnd);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);

            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            logger.debug("Calculated period: from={} to={}. Currency: {}", from, end, curr);

            List<TopExpense> summary = this.expensesRepository.getTopExpenses(zoneId, curr, from, end);
            logger.info("Successfully retrieved top expenses summary for zone {}. Found {} entries.", zoneId, summary.size());
            logger.debug("Top expenses summary: {}", summary);
            return summary;
        } catch (UserUnauthorizedException e) {
            logger.warn("User unauthorized to get top expenses summary in zone {}: {}", zoneId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while getting top expenses summary for zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<TotalSummaryByDate> getSummaryByDate(
            String zoneId,
            @Nullable String currency,
            @Nullable String periodStart,
            @Nullable String periodEnd,
            Authentication authentication) {
        logger.info("Getting summary by date for zone {}. User: {}", zoneId, authentication.getName());
        logger.debug("Parameters: currency={}, periodStart={}, periodEnd={}", currency, periodStart, periodEnd);
        try {
            JWTClaimsUtil.JWTClaims jwtClaims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            User user = this.membersService.getUserByZoneIdAndId(jwtClaims.userId(), zoneId);
            logger.debug("User authorized for zone {}.", zoneId);

            String curr = this.getCurrency(currency);
            LocalDateTime from = this.getPeriod(periodStart, false);
            LocalDateTime end = this.getPeriod(periodEnd, true);
            logger.debug("Calculated period: from={} to={}. Currency: {}", from, end, curr);

            List<TotalSummaryByDate> summary = this.expensesRepository.getSummaryByDay(zoneId, curr, from, end);
            logger.info("Successfully retrieved summary by date for zone {}. Found {} entries.", zoneId, summary.size());
            logger.debug("Summary by date: {}", summary);
            return summary;
        } catch (UserUnauthorizedException e) {
            logger.warn("User unauthorized to get summary by date in zone {}: {}", zoneId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while getting summary by date for zone {}.", zoneId, e);
            throw new InternalServerException(e);
        }
    }

    private String getCurrency(String currency) {
        logger.debug("Determining currency. Input: {}", currency);
        if (currency == null) {
            logger.debug("Input currency is null, defaulting to PLN.");
            return "PLN";
        } else {
            logger.debug("Using specified currency: {}", currency);
            return currency;
        }
    }

    private LocalDateTime getPeriod(String period, boolean isLastDay) {
        logger.debug("Calculating period. Input: {}, isLastDay: {}", period, isLastDay);
        if (period == null) {
            LocalDate now = LocalDate.now();
            if (!isLastDay) {
                LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                logger.debug("Period is null and !isLastDay, defaulting to start of current month: {}", startOfMonth);
                return startOfMonth;
            } else {
                LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
                logger.debug("Period is null and isLastDay, defaulting to end of current month: {}", endOfMonth);
                return endOfMonth;
            }
        } else {
            LocalDate localDate = LocalDate.parse(period);
            if (!isLastDay) {
                LocalDateTime startOfDay = localDate.atStartOfDay();
                logger.debug("Period specified and !isLastDay, using start of day: {}", startOfDay);
                return startOfDay;
            } else {
                LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
                logger.debug("Period specified and isLastDay, using end of day: {}", endOfDay);
                return endOfDay;
            }
        }
    }
}