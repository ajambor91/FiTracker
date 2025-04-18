package aj.FiTracker.FiTrackerExpenses.Enums;

public enum KafkaAction {
    ADD_MEMBER("add_member"),
    REMOVE_MEMBER("remove_member");

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
