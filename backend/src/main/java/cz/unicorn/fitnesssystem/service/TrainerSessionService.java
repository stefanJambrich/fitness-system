package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.TrainerPrincipal;
import cz.unicorn.fitnesssystem.entity.Trainer;
import cz.unicorn.fitnesssystem.entity.TrainerSession;
import java.util.List;
import java.util.UUID;

public interface TrainerSessionService {

    /**
     * Starts a session; only the hash of its token is stored.
     *
     * @param trainer    session owner
     * @param remembered keep it for 90 days instead of 12 hours
     * @param userAgent  source of the device label
     * @return the token for the session cookie
     */
    String open(Trainer trainer, boolean remembered, String userAgent);

    /**
     * Resolves a session token and extends remembered sessions. Throws {@code 401 SESSION_EXPIRED}.
     *
     * @param token value of the session cookie
     * @return the logged-in trainer and session
     */
    TrainerPrincipal authenticate(String token);

    /**
     * Lists the trainer's sessions that are neither revoked nor expired, most recently used first.
     *
     * @param trainerId session owner
     * @return the active sessions
     */
    List<TrainerSession> listActive(UUID trainerId);

    /**
     * Ends one of the trainer's active sessions. Throws {@code 404 NOT_FOUND}.
     *
     * @param trainerId session owner
     * @param sessionId session to end
     */
    void revoke(UUID trainerId, UUID sessionId);
}