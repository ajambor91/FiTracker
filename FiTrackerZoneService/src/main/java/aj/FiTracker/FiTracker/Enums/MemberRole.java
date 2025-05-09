package aj.FiTracker.FiTracker.Enums;

public enum MemberRole {
    ADMIN("admin"),
    MEMBER("member"),
    GUEST("guest");

    private final String role;

    MemberRole(String role) {
        this.role = role;
    }

    public String getRoleName() {
        return this.role;
    }
}
