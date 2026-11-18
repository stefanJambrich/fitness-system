package cz.unicorn.fitnesssystem.dto;

import cz.unicorn.fitnesssystem.entity.Trainer;
import cz.unicorn.fitnesssystem.exception.ApiException;

public sealed interface LoginResult {

    record Success(Trainer trainer, String token, boolean remembered) implements LoginResult {
    }

    /** Returned instead of thrown, so the failed attempt is committed before the error reaches the client. */
    record Rejected(ApiException error) implements LoginResult {
    }
}