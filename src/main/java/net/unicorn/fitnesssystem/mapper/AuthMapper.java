package net.unicorn.fitnesssystem.mapper;

import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface AuthMapper {

    UserBaseDto toUserBaseDto(User user);
}
