package de.itscope.databinding.heartbeat;

import reactor.core.publisher.Flux;

import java.time.Duration;

public interface Heartbeat {
    long DELAY = 15;

    static Flux<HeartbeatEvent> createHeartbeatStream() {
        return Flux.interval(Duration.ofSeconds(DELAY)).startWith(0L).map(heartbeat -> new HeartbeatEvent());
    }
}
