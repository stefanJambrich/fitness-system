package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.OtpCheck;
import cz.unicorn.fitnesssystem.entity.OtpPurpose;
import cz.unicorn.fitnesssystem.entity.Trainer;

public interface OtpService {

    /**
     * Issues a login code for the trainer and invalidates older unused ones. The code is e-mailed after commit.
     *
     * @param trainer code recipient
     */
    void issueTrainerLogin(Trainer trainer);

    /**
     * Checks a code against the latest live one. A wrong guess counts as an attempt, a match consumes the code.
     *
     * @param email   address the code was sent to
     * @param purpose what the code is for
     * @param code    6-digit code to check
     * @return the outcome of the check
     */
    OtpCheck check(String email, OtpPurpose purpose, String code);
}