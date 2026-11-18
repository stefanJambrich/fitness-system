package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.OtpIssued;

public interface MailService {

    /**
     * E-mails a one-time code. Runs asynchronously after the transaction that issued the code commits;
     * a delivery failure is only logged, without the address or the code.
     *
     * @param issued recipient, code and purpose
     */
    void sendOtp(OtpIssued issued);
}