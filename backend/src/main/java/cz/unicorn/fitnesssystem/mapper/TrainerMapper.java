package cz.unicorn.fitnesssystem.mapper;

import cz.unicorn.fitnesssystem.api.model.TrainerProfile;
import cz.unicorn.fitnesssystem.api.model.TrainerSessionInfo;
import cz.unicorn.fitnesssystem.entity.Trainer;
import cz.unicorn.fitnesssystem.entity.TrainerSession;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TimeMapper.class)
public interface TrainerMapper {

    @Mapping(target = "trainerId", source = "id")
    TrainerProfile toTrainerProfile(Trainer trainer);

    @Mapping(target = "current", expression = "java(session.getId().equals(currentSessionId))")
    TrainerSessionInfo toSessionInfo(TrainerSession session, @Context UUID currentSessionId);

    List<TrainerSessionInfo> toSessionInfos(List<TrainerSession> sessions, @Context UUID currentSessionId);
}