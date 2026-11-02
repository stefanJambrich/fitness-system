package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.NewMember;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.entity.Member;

public interface MemberService {

    /**
     * Creates a member with a unique {@code nickname#code} and its first device, proving the request
     * is signed by the registered key. Throws {@code 400 VALIDATION}, {@code 401 AUTH_INVALID}
     * or {@code 409 KEY_ALREADY_REGISTERED}.
     */
    Member createMember(NewMember newMember, SignedRequest proof);
}