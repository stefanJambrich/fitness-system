package cz.unicorn.fitnesssystem.mapper;

import cz.unicorn.fitnesssystem.api.model.CreateGroupRequest;
import cz.unicorn.fitnesssystem.api.model.TrainerGroup;
import cz.unicorn.fitnesssystem.api.model.UpdateGroupRequest;
import cz.unicorn.fitnesssystem.dto.GroupDetails;
import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TimeMapper.class)
public interface GroupMapper {

    GroupDetails toGroupDetails(CreateGroupRequest request);

    GroupDetails toGroupDetails(UpdateGroupRequest request);

    @Mapping(target = ".", source = "group")
    @Mapping(target = "id", source = "group.id")
    TrainerGroup toTrainerGroup(GroupTrainer groupTrainer);

    List<TrainerGroup> toTrainerGroups(List<GroupTrainer> groupTrainers);
}