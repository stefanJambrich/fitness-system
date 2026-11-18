package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EnumeratedValue;

public enum OtpPurpose {

    TRAINER_LOGIN("trainer_login"),
    MEMBER_EMAIL_VERIFY("member_email_verify"),
    MEMBER_RECOVERY("member_recovery");

    @EnumeratedValue
    private final String dbValue;

    OtpPurpose(String dbValue) {
        this.dbValue = dbValue;
    }
}