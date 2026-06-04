package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.UserBaseDto;

public interface DeviceService {

    void updateUserDevice(Long userId, String publicHashKey);

    boolean deviceKeyExists(String publicKeyHash);

    UserBaseDto getUserByDeviceKey(String deviceKey);
}
