package cz.unicorn.fitnesssystem.dto;

import cz.unicorn.fitnesssystem.entity.Member;

public record MemberWithDeviceCount(Member member, int deviceCount) {
}