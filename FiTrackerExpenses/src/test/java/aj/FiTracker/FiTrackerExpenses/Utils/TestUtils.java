package aj.FiTracker.FiTrackerExpenses.Utils;

import aj.FiTracker.FiTrackerExpenses.DTO.DB.CategoryDb;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.DTO.REST.UpdateCategoryRequest;
import aj.FiTracker.FiTrackerExpenses.Entities.Category;
import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;

import java.time.LocalDateTime;
import java.util.List;

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
}
