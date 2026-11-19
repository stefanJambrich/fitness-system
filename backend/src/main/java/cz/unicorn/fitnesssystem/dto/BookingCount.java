package cz.unicorn.fitnesssystem.dto;

import java.util.UUID;

public record BookingCount(UUID membershipId, long count) {
}
