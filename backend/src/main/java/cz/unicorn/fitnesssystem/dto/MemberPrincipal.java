package cz.unicorn.fitnesssystem.dto;

import java.util.UUID;

public record MemberPrincipal(UUID memberId, UUID deviceId) {
}