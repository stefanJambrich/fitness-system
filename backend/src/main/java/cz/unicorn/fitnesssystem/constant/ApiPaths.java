package cz.unicorn.fitnesssystem.constant;

import java.util.List;

public final class ApiPaths {

    public static final String API = "/api";
    public static final String PING = API + "/ping";
    public static final String MEMBERS = API + "/members";
    public static final String MEMBER_API = API + "/me";
    public static final String INVITES = API + "/invites";
    public static final String TRAINER_API = API + "/trainer";

    /** Reachable without a session: permitted by the security chain and skipped by the trainer session filter. */
    public static final List<String> TRAINER_PUBLIC = List.of(
            TRAINER_API + "/register", TRAINER_API + "/login/request", TRAINER_API + "/login/verify");

    private ApiPaths() {
    }
}