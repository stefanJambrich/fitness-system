package cz.unicorn.fitnesssystem.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties("app")
public record AppProperties(@DefaultValue Signature signature, @DefaultValue Member member) {

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
}