package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.MemberWithDeviceCount;
import cz.unicorn.fitnesssystem.dto.NewMember;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.entity.Member;
import java.util.UUID;

public interface MemberService {

    /**
     * Creates a member with a unique {@code nickname#code} and its first device, proving the request
     * is signed by the registered key. Throws {@code 400 VALIDATION}, {@code 401 AUTH_INVALID}
     * or {@code 409 KEY_ALREADY_REGISTERED}.
     */
    Member createMember(NewMember newMember, SignedRequest proof);

    /** The member with its number of active devices. Throws {@code 401 MEMBER_DELETED}. */
    MemberWithDeviceCount getProfile(UUID memberId);

    /**
     * Changes the nickname and draws a new code when the current one is taken for that nickname.
     * Throws {@code 400 VALIDATION} or {@code 401 MEMBER_DELETED}.
     */
    Member changeNickname(UUID memberId, String nickname);
}