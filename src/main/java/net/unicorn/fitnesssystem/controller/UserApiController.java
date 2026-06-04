package net.unicorn.fitnesssystem.controller;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.api.UserApi;
import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@CustomLog
@RestController
@RequiredArgsConstructor
public class UserApiController implements UserApi {

    private final DeviceService deviceService;

    @Override
    public ResponseEntity<UserBaseDto> getMe(String xDeviceKey) {
        log.info("Getting info about logged in user");
        UserBaseDto user = deviceService.getUserByDeviceKey(xDeviceKey);
        return ResponseEntity.ok(user);
    }
}
