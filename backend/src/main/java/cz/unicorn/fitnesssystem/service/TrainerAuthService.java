package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.LoginResult;
import cz.unicorn.fitnesssystem.entity.Trainer;
import java.util.UUID;

public interface TrainerAuthService {

    /**
     * Creates the trainer if the e-mail is new, then sends a login code like {@link #requestLoginCode}.
     * Throws {@code 429 RATE_LIMITED}.
     *
     * @param email       trainer e-mail
     * @param displayName name shown to members
     * @param clientIp    key for the rate limit
     */
    void register(String email, String displayName, String clientIp);

    /**
     * Sends a login code if the e-mail belongs to an active trainer, without revealing whether it does.
     * Throws {@code 429 RATE_LIMITED}.
     *
     * @param email    trainer e-mail
     * @param clientIp key for the rate limit
     */
    void requestLoginCode(String email, String clientIp);

    /**
     * Checks the code and opens a session; the first successful login marks the e-mail as verified.
     *
     * @param email     trainer e-mail
     * @param code      6-digit login code
     * @param remember  keep the session for 90 days
     * @param userAgent source of the device label
     * @return the session token, or the error to return
     */
    LoginResult verifyLogin(String email, String code, boolean remember, String userAgent);

    /**
     * Loads a trainer. Throws {@code 401 SESSION_EXPIRED} when the trainer no longer exists.
     *
     * @param trainerId trainer id
     * @return the trainer
     */
    Trainer getTrainer(UUID trainerId);
}