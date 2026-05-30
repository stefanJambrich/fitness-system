package net.unicorn.fitnesssystem.mapper;

import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;
import net.unicorn.fitnesssystem.entity.User;
import net.unicorn.fitnesssystem.enums.UserRoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface AuthMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "user", target = "authMethod", qualifiedByName = "userToAuthMethod")
    OtpVerificationUserResponseDto toExistingUserResponse(User user);

    @Named("userToAuthMethod")
    default OtpVerificationUserResponseDto.AuthMethodEnum userToAuthMethod(User user) {
        boolean isTrainer = user.getRoles().stream()
                .anyMatch(role -> UserRoleEnum.TRAINER.name().equals(role.getName()));
        return isTrainer
                ? OtpVerificationUserResponseDto.AuthMethodEnum.SESSION_COOKIE
                : OtpVerificationUserResponseDto.AuthMethodEnum.DEVICE_KEY;
    }
}
