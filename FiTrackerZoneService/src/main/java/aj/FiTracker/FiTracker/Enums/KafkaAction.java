package aj.FiTracker.FiTracker.Enums;

public enum KafkaAction {
    ADD_MEMBER("add_member"),
    REMOVE_MEMBER("REMOVE_MEMBER");

    private final String action;

    KafkaAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}
