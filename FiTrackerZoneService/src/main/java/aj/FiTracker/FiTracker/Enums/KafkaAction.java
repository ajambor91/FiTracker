package aj.FiTracker.FiTracker.Enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KafkaAction {
    ADD_MEMBER("add_member"),
    DELETE_MEMBER("delete_member");

    private final String action;
    KafkaAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}
