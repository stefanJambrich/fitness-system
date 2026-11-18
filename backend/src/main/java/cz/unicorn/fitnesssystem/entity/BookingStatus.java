package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum BookingStatus {

    BOOKED("booked"),
    CANCELLED("cancelled"),
    ATTENDED("attended"),
    NO_SHOW("no_show"),
    RELEASED("released");

    @EnumeratedValue
    private final String dbValue;

    BookingStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}
