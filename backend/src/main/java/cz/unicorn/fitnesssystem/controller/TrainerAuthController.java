package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.TrainerAuthApi;
import cz.unicorn.fitnesssystem.api.model.RegisterTrainerRequest;
import cz.unicorn.fitnesssystem.api.model.RequestLoginCodeRequest;
import cz.unicorn.fitnesssystem.api.model.TrainerProfile;
import cz.unicorn.fitnesssystem.api.model.TrainerSessionInfo;
import cz.unicorn.fitnesssystem.api.model.VerifyLoginCodeRequest;
import cz.unicorn.fitnesssystem.auth.TrainerSessionCookie;
import cz.unicorn.fitnesssystem.dto.LoginResult;
import cz.unicorn.fitnesssystem.dto.TrainerPrincipal;
import cz.unicorn.fitnesssystem.helper.AuthHelper;
import cz.unicorn.fitnesssystem.mapper.TrainerMapper;
import cz.unicorn.fitnesssystem.service.TrainerAuthService;
import cz.unicorn.fitnesssystem.service.TrainerSessionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TrainerAuthController implements TrainerAuthApi {

    private final TrainerAuthService trainerAuthService;
    private final TrainerSessionService sessionService;
    private final TrainerMapper trainerMapper;
    private final TrainerSessionCookie sessionCookie;
    private final AuthHelper authHelper;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<Void> registerTrainer(RegisterTrainerRequest registerTrainerRequest) {
        trainerAuthService.register(registerTrainerRequest.getEmail(), registerTrainerRequest.getDisplayName(),
                request.getRemoteAddr());
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> requestLoginCode(RequestLoginCodeRequest requestLoginCodeRequest) {
        trainerAuthService.requestLoginCode(requestLoginCodeRequest.getEmail(), request.getRemoteAddr());
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<TrainerProfile> verifyLoginCode(VerifyLoginCodeRequest verifyLoginCodeRequest) {
        LoginResult result = trainerAuthService.verifyLogin(verifyLoginCodeRequest.getEmail(),
                verifyLoginCodeRequest.getCode(), Boolean.TRUE.equals(verifyLoginCodeRequest.getRememberDevice()),
                request.getHeader(HttpHeaders.USER_AGENT));
        return switch (result) {
            case LoginResult.Rejected rejected -> throw rejected.error();
            case LoginResult.Success success -> ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, sessionCookie.issue(success.token(), success.remembered()).toString())
                    .body(trainerMapper.toTrainerProfile(success.trainer()));
        };
    }

    @Override
    public ResponseEntity<Void> logout() {
        TrainerPrincipal trainer = authHelper.currentTrainer();
        sessionService.revoke(trainer.trainerId(), trainer.sessionId());
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, sessionCookie.clear().toString()).build();
    }

    @Override
    public ResponseEntity<TrainerProfile> getTrainerProfile() {
        return ResponseEntity.ok(trainerMapper.toTrainerProfile(trainerAuthService.getTrainer(authHelper.currentTrainer().trainerId())));
    }

    @Override
    public ResponseEntity<List<TrainerSessionInfo>> listSessions() {
        TrainerPrincipal trainer = authHelper.currentTrainer();
        return ResponseEntity.ok(trainerMapper.toSessionInfos(sessionService.listActive(trainer.trainerId()),
                trainer.sessionId()));
    }

    @Override
    public ResponseEntity<Void> revokeSession(UUID sessionId) {
        TrainerPrincipal trainer = authHelper.currentTrainer();
        sessionService.revoke(trainer.trainerId(), sessionId);
        if (sessionId.equals(trainer.sessionId())) {
            return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, sessionCookie.clear().toString()).build();
        }
        return ResponseEntity.noContent().build();
    }
}