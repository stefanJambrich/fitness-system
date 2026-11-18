package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum TrainingStatus {

    SCHEDULED("scheduled"),
    CANCELLED("cancelled"),
    CLOSED("closed");

    @EnumeratedValue
    private final String dbValue;

    TrainingStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}
