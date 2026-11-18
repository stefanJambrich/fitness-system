package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum GroupTrainerRole {

    OWNER("owner"),
    CO_TRAINER("co_trainer");

    @EnumeratedValue
    private final String dbValue;

    GroupTrainerRole(String dbValue) {
        this.dbValue = dbValue;
    }
}
