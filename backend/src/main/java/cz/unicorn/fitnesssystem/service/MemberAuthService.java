package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.MemberPrincipal;
import cz.unicorn.fitnesssystem.dto.SignedRequest;

public interface MemberAuthService {

    /**
     * Runs the server steps for a registered device and consumes the nonce.
     */
    MemberPrincipal authenticate(SignedRequest request);
}