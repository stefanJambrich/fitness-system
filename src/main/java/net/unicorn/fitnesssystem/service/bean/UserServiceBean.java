package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.annotations.ReadWriteTransaction;
import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.entity.Role;
import net.unicorn.fitnesssystem.entity.User;
import net.unicorn.fitnesssystem.exceptions.ApplicationException;
import net.unicorn.fitnesssystem.enums.UserRoleEnum;
import net.unicorn.fitnesssystem.mapper.AuthMapper;
import net.unicorn.fitnesssystem.repository.RoleRepository;
import net.unicorn.fitnesssystem.repository.UserRepository;
import net.unicorn.fitnesssystem.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class UserServiceBean implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;

    private final Random random = new Random();

    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserBaseDto getExistingUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("User not found for email: " + email));

        return authMapper.toUserBaseDto(user);
    }

    @Override
    @ReadWriteTransaction
    public User createUser(String email, String fullname, boolean registerAsTrainer) {
        var role = registerAsTrainer ? UserRoleEnum.TRAINER.name() : UserRoleEnum.MEMBER.name();
        Role memberRole = roleRepository.getByName(role);

        User user = new User();
        user.setEmail(email);
        user.setFullname(fullname);
        user.setUniqueCode(generateUniqueUserCode(fullname));
        user.setRoles(Set.of(memberRole));

        User savedUser = userRepository.save(user);
        log.info("Created new user with id: {} for email: {}", savedUser.getId(), email);
        return savedUser;
    }

    private String generateUniqueUserCode(String fullname) {
        log.info("Generating unique code for user with fullname: {}", fullname);
        String baseName = fullname.replaceAll("\\s+", "");

        if (baseName.length() > 10) {
            baseName = baseName.substring(0, 10);
        }

        String generatedCode = "";
        boolean isUnique = false;
        int maxAttempts = 10;
        int attempts = 0;

        while (!isUnique && attempts < maxAttempts) {
            int randomTag = 1000 + random.nextInt(9000);
            generatedCode = baseName + "#" + randomTag;

            if (!userRepository.existsByUniqueCode(generatedCode)) {
                isUnique = true;
            }
            attempts++;
        }

        if (!isUnique) {
            generatedCode = baseName + "#" + UUID.randomUUID().toString().substring(0, 6);
        }

        log.info("Finished generating unique code for user");
        return generatedCode;
    }
}
