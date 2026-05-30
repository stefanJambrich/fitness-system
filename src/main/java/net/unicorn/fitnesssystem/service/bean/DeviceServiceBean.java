package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.service.DeviceService;
import org.springframework.stereotype.Service;

@CustomLog
@Service
@ReadOnlyTransaction
public class DeviceServiceBean implements DeviceService {

    @Override
    public void updateUserDevice(Long userId, String publicHashKey) {

    }
}
