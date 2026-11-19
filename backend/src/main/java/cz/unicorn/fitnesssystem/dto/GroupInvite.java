package cz.unicorn.fitnesssystem.dto;

import cz.unicorn.fitnesssystem.entity.TrainingGroup;
import java.util.List;

public record GroupInvite(TrainingGroup group, List<String> trainers) {
}
