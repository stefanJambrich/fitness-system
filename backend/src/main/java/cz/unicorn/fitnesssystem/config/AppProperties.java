package cz.unicorn.fitnesssystem.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app")
public record AppProperties(@DefaultValue Signature signature, @DefaultValue Member member) {

    public record Signature(
            @DefaultValue("5m") Duration maxClockSkew,
            @DefaultValue("10m") Duration nonceTtl) {
    }

    public record Member(
            @DefaultValue("5") int createLimitPerIp,
            @DefaultValue("1h") Duration createLimitWindow) {
    }
}