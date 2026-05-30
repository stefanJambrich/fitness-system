package net.unicorn.fitnesssystem.controller;

import net.unicorn.fitnesssystem.api.PingApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingApiController implements PingApi {

    @Override
    public ResponseEntity<String> pingGet() {
        return ResponseEntity.ok("pong");
    }
}
