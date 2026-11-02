package cz.unicorn.fitnesssystem.ratelimit;

import cz.unicorn.fitnesssystem.exception.ApiException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** In-memory sliding-window limiter; per instance and lost on restart, enough for one node. */
@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    /** Records a hit for {@code key}; {@code 429 RATE_LIMITED} once it has {@code limit} hits in the window. */
    public void acquire(String key, int limit, Duration window) {
        Instant now = clock.instant();
        boolean[] allowed = {false};
        windows.compute(key, (k, w) -> {
            Window current = w == null ? new Window(window, new ArrayDeque<>()) : w;
            current.prune(now);
            if (current.hits().size() < limit) {
                current.hits().addLast(now);
                allowed[0] = true;
            }
            return current;
        });
        if (!allowed[0]) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMITED", "Too many requests, try again later");
        }
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    void evictIdle() {
        Instant now = clock.instant();
        for (String key : windows.keySet()) {
            windows.computeIfPresent(key, (k, w) -> {
                w.prune(now);
                return w.hits().isEmpty() ? null : w;
            });
        }
    }

    private record Window(Duration length, Deque<Instant> hits) {

        void prune(Instant now) {
            Instant start = now.minus(length);
            while (!hits.isEmpty() && !hits.peekFirst().isAfter(start)) {
                hits.removeFirst();
            }
        }
    }
}