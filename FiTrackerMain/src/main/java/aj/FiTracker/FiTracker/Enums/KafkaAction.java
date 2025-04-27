package aj.FiTracker.FiTracker.Enums;

public enum KafkaAction {
    REMOVE_MEMBER("REMOVE_MEMBER");

    private final String action;

    KafkaAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}
