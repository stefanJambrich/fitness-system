package cz.unicorn.fitnesssystem.dto;

import java.util.List;

public record MemberGroups(List<MembershipOverview> active, List<MembershipOverview> former) {
}
