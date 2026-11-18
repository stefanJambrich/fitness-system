package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum MembershipEnd {

    LEFT("left"),
    REMOVED("removed");

    @EnumeratedValue
    private final String dbValue;

    MembershipEnd(String dbValue) {
        this.dbValue = dbValue;
    }
}
