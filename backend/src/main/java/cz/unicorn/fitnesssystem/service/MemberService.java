package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.MemberWithDeviceCount;
import cz.unicorn.fitnesssystem.dto.NewMember;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.entity.Member;
import java.util.UUID;

public interface MemberService {

    /**
     * Creates a member with a unique {@code nickname#code} and its first device, proving the request
     * is signed by the registered key. Rate limited per client IP. Throws {@code 400 VALIDATION},
     * {@code 401 AUTH_INVALID}, {@code 409 KEY_ALREADY_REGISTERED} or {@code 429 RATE_LIMITED}.
     *
     * @param newMember nickname and device key
     * @param proof     the signed registration request
     * @param clientIp  caller IP for rate limiting
     * @return the created member
     */
    Member createMember(NewMember newMember, SignedRequest proof, String clientIp);

    /**
     * Loads the member with its number of active devices. Throws {@code 401 MEMBER_DELETED}.
     *
     * @param memberId member id
     * @return the member and device count
     */
    MemberWithDeviceCount getProfile(UUID memberId);

    /**
     * Changes the nickname and draws a new code when the current one is taken for that nickname.
     * Throws {@code 400 VALIDATION} or {@code 401 MEMBER_DELETED}.
     *
     * @param memberId member id
     * @param nickname new nickname
     * @return the member with its new identifier
     */
    Member changeNickname(UUID memberId, String nickname);
}