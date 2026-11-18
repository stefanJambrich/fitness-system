package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum BookingSource {

    MEMBER("member"),
    WALK_IN("walk_in");

    @EnumeratedValue
    private final String dbValue;

    BookingSource(String dbValue) {
        this.dbValue = dbValue;
    }
}
