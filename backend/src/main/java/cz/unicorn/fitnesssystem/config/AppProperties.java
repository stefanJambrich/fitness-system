package cz.unicorn.fitnesssystem.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties("app")
public record AppProperties(
        @DefaultValue Signature signature,
        @DefaultValue Member member,
        Otp otp,
        @DefaultValue TrainerSession trainerSession,
        Mail mail) {

    public record Signature(
            @DefaultValue("5m") Duration maxClockSkew,
            @DefaultValue("10m") Duration nonceTtl,
            @DefaultValue("1m") Duration lastSeenInterval,
            @DefaultValue("64KB") DataSize maxBodySize) {
    }

    public record Member(
            @DefaultValue("5") int createLimitPerIp,
            @DefaultValue("1h") Duration createLimitWindow) {
    }

    public record Otp(
            String pepper,
            @DefaultValue("10m") Duration ttl,
            @DefaultValue("5") int maxAttempts,
            @DefaultValue("3") int limitPerEmail,
            @DefaultValue("15m") Duration emailWindow,
            @DefaultValue("3") int limitPerIp,
            @DefaultValue("1h") Duration ipWindow) {
    }

    public record TrainerSession(
            @DefaultValue("TRAINER_SESSION") String cookieName,
            @DefaultValue("12h") Duration lifetime,
            @DefaultValue("90d") Duration rememberedLifetime,
            @DefaultValue("1m") Duration lastUsedInterval) {
    }

    public record Mail(String from) {
    }
}