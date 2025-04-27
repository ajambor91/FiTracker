package aj.FiTracker.FiTrackerExpenses.Enums;

public enum KafkaAction {
    ADD_MEMBER("ADD_MEMBER"),
    REMOVE_MEMBER("REMOVE_MEMBER");

    private final String action;

    KafkaAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public static KafkaAction setAction(String value) {
        for (KafkaAction enumValue : KafkaAction.values()) {
            if (enumValue.getAction().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }
}
