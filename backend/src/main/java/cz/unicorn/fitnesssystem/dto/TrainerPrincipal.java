package cz.unicorn.fitnesssystem.dto;

import java.util.UUID;

public record TrainerPrincipal(UUID trainerId, UUID sessionId, boolean remembered) {
}