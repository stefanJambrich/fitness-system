package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.MemberPrincipal;
import cz.unicorn.fitnesssystem.dto.SignedRequest;

public interface MemberAuthService {

    /**
     * Runs the server steps for a registered device and consumes the nonce.
     *
     * @param request the parsed signed request
     * @return the signed-in member and device
     */
    MemberPrincipal authenticate(SignedRequest request);
}