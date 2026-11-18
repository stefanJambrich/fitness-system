package cz.unicorn.fitnesssystem.dto;

import cz.unicorn.fitnesssystem.entity.OtpPurpose;

public record OtpIssued(String email, String code, OtpPurpose purpose) {

    @Override
    public String toString() {
        return "OtpIssued[purpose=" + purpose + "]";
    }
}