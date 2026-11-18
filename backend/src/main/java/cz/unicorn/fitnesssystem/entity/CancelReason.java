package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum CancelReason {

    MEMBER("member"),
    TRAINER("trainer"),
    TRAINING_CANCELLED("training_cancelled"),
    MEMBER_LEFT("member_left"),
    MEMBER_REMOVED("member_removed");

    @EnumeratedValue
    private final String dbValue;

    CancelReason(String dbValue) {
        this.dbValue = dbValue;
    }
}
