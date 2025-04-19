package aj.FiTracker.FiTrackerExpenses.Utils;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.DB.TotalSummaryByCategory;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddExpenseRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.UpdateCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Entities.Expense;
import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TestUtils {
    public static long MEMBER_TEST_ID = 0;
    public static long MEMBER_TEST_ID_SECOND = 1;
    public static String USER_TEST_NAME = "Test name";
    public static long USER_TEST_ID = 0;
    public static String ZONE_TEST_ID = "abc";
    public static long CATEGORY_TEST_ID = 0;
    public static String CATEGORY_TEST_NAME = "zone_test";
    public static String CATEGORY_TEST_DESCRIPTION = "Test description";
    public static long CATEGORY_TEST_ID_SECOND = 1;
    public static String CATEGORY_TEST_NAME_SECOND = "zone_test";
    public static String DEFAULT_CURRENCY = "PLN";
    public static String TEST_EXPENSE_NAME = "Test expense";
    public static BigDecimal TEST_EXPENSE_VALUE = BigDecimal.valueOf(100);
    public static long TEST_EXPENSE_ID = 1;
    public static BigDecimal CATEGORY_VALUE = BigDecimal.valueOf(100);
    public static BigDecimal OVERALL_VALUE = BigDecimal.valueOf(1000);
    public static String TEST_FROM_DATE_PARAM = "2025-04-01";
    public static String TEST_END_DATE_PARAM = "2025-04-30";

    public static MemberTemplate createMemberTemplateOneMember() {
        MemberTemplate memberTemplate = new MemberTemplate(ZONE_TEST_ID);
        memberTemplate.setMembersList(List.of(new MemberTemplate.Member(MEMBER_TEST_ID)));
        return memberTemplate;
    }


    public static MemberTemplate createMemberTemplate() {
        MemberTemplate memberTemplate = new MemberTemplate(ZONE_TEST_ID);
        memberTemplate.setMembersList(List.of(new MemberTemplate.Member(MEMBER_TEST_ID), new MemberTemplate.Member(MEMBER_TEST_ID_SECOND)));
        return memberTemplate;
    }

    public static User createTestUser() {
        return new User(MEMBER_TEST_ID, ZONE_TEST_ID);
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setCategoryId(CATEGORY_TEST_ID);
        category.setCreatedAt(LocalDateTime.now());
        category.setName(CATEGORY_TEST_NAME);
        category.setZoneId(ZONE_TEST_ID);
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    public static CategoryDb createTestCategoryDb() {
        return new CategoryDb(CATEGORY_TEST_ID, CATEGORY_TEST_NAME, CATEGORY_TEST_DESCRIPTION);
    }

    public static CategoryDb createTestCategoryDbSecond() {
        return new CategoryDb(CATEGORY_TEST_ID_SECOND, CATEGORY_TEST_NAME_SECOND, CATEGORY_TEST_DESCRIPTION);
    }


    public static Category createTestCategorySecond() {
        Category category = new Category();
        category.setCategoryId(CATEGORY_TEST_ID);
        category.setCreatedAt(LocalDateTime.now());
        category.setName(CATEGORY_TEST_NAME);
        category.setZoneId(ZONE_TEST_ID);
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    public static AddCategoryRequest createAddCategoryRequest() {
        AddCategoryRequest request = new AddCategoryRequest();
        request.setZoneId(ZONE_TEST_ID);
        request.setName(CATEGORY_TEST_NAME);
        request.setDescription(null);
        return request;
    }

    public static UpdateCategoryRequest createUpdateCategoryRequest() {
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName(CATEGORY_TEST_NAME);
        request.setDescription(null);
        return request;
    }

    public static AddExpenseRequest addExpenseRequest() {
        AddExpenseRequest addExpenseRequest = new AddExpenseRequest();
        addExpenseRequest.setCurrency(DEFAULT_CURRENCY);
        addExpenseRequest.setName(TEST_EXPENSE_NAME);
        addExpenseRequest.setZoneId(ZONE_TEST_ID);
        addExpenseRequest.setValue(TEST_EXPENSE_VALUE);
        addExpenseRequest.setCategoriesIds(List.of(CATEGORY_TEST_ID));
        return addExpenseRequest;
    }

    public static Expense addTestExpense() {
        Expense expense = new Expense();
        expense.setCategories(Set.of(createTestCategory()));
        expense.setExpenseId(TEST_EXPENSE_ID);
        expense.setCurrency(DEFAULT_CURRENCY);
        expense.setValue(TEST_EXPENSE_VALUE);
        expense.setName(TEST_EXPENSE_NAME);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        expense.setUser(USER_TEST_ID);
        return expense;
    }




}
