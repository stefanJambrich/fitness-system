package net.unicorn.fitnesssystem.mapper;

import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.entity.Role;
import net.unicorn.fitnesssystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "fullname", target = "name")
    @Mapping(source = "roles", target = "role", qualifiedByName = "rolesToRoleEnum")
    UserBaseDto toUserBaseDto(User user);

    @Named("rolesToRoleEnum")
    default UserBaseDto.RoleEnum rolesToRoleEnum(Set<Role> roles) {
        return roles.stream()
                .findFirst()
                .map(role -> UserBaseDto.RoleEnum.fromValue(role.getName()))
                .orElse(null);
    }
}
