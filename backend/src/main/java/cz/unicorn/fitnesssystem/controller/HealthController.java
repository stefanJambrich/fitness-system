package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.HealthApi;
import cz.unicorn.fitnesssystem.api.model.PingResponse;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController implements HealthApi {

    private final Clock clock;

    public HealthController(Clock clock) {
        this.clock = clock;
    }

    @Override
    public ResponseEntity<PingResponse> ping(String name) {
        String message = name == null ? "pong" : "pong, " + name;
        return ResponseEntity.ok(new PingResponse(message, OffsetDateTime.now(clock)));
    }
}