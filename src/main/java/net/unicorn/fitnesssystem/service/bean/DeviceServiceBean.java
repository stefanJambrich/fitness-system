package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.annotations.ReadWriteTransaction;
import net.unicorn.fitnesssystem.entity.Device;
import net.unicorn.fitnesssystem.entity.User;
import net.unicorn.fitnesssystem.exceptions.ApplicationException;
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
}
