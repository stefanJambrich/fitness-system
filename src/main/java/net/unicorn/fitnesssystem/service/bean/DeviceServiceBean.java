package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.annotations.ReadWriteTransaction;
import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.entity.Device;
import net.unicorn.fitnesssystem.entity.User;
import net.unicorn.fitnesssystem.exceptions.ApplicationException;
import net.unicorn.fitnesssystem.exceptions.DeviceNotFoundException;
import net.unicorn.fitnesssystem.mapper.UserMapper;
import net.unicorn.fitnesssystem.repository.DeviceRepository;
import net.unicorn.fitnesssystem.repository.UserRepository;
import net.unicorn.fitnesssystem.service.DeviceService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class DeviceServiceBean implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //TODO: This is probably pretty bad and needs some form of refactor or total rewrite, as it currently stands I have no idea how to properly work with these device codes
    // Every time I send a login with new public hash key (occurs when deleting it on FE - logout) it registers every another login as a new device which doesnt really make sense
    // FIX THIS ASAP :(
    @Override
    @ReadWriteTransaction
    public void updateUserDevice(Long userId, String publicHashKey) {
        var existingDevice = deviceRepository.findByUserIdAndPublicKeyHash(userId, publicHashKey);

        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            device.setLastUsedAt(OffsetDateTime.now());
            deviceRepository.save(device);
            log.info("Updated last_used_at for existing device of user: {}", userId);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApplicationException("User not found with id: " + userId));

            Device device = new Device();
            device.setUser(user);
            device.setPublicKeyHash(publicHashKey);
            device.setLastUsedAt(OffsetDateTime.now());
            deviceRepository.save(device);
            log.info("Registered new device for user: {}", userId);
        }
    }

    @Override
    public boolean deviceKeyExists(String publicKeyHash) {
        return deviceRepository.existsByPublicKeyHash(publicKeyHash);
    }

    @Override
    public UserBaseDto getUserByDeviceKey(String deviceKey) {
        Device device = deviceRepository.findByPublicKeyHash(deviceKey)
                .orElseThrow(() -> new DeviceNotFoundException("No device found for the provided key"));

        return userMapper.mapToUserBaseDto(device.getUser());
    }
}
