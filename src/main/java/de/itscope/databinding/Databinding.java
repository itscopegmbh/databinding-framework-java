package de.itscope.databinding;

import de.itscope.databinding.events.Event;
import de.itscope.databinding.events.EventData;
import de.itscope.databinding.mapper.IEventMapper;
import de.itscope.databinding.heartbeat.Heartbeat;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

public class Databinding<T> {
    private final IEventMapper<T> eventMapper;
    private final Flux<T> stream;
    long RETRY = 5;

    public Databinding(IEventMapper<T> eventMapper, Flux<T> stream) {
        this.eventMapper = eventMapper;
        this.stream = stream;
    }

    public Flux<ServerSentEvent<EventData>> createUpdateStream() {
        return Flux.merge(this.stream.map(eventMapper::mapToDatabindingEvent),
                Heartbeat.createHeartbeatStream()).map(this::mapEventToSSE);
    }

    private ServerSentEvent<EventData> mapEventToSSE(Event event) {
        return ServerSentEvent.<EventData>builder().id(String.valueOf(Instant.now().getEpochSecond()))
                .retry(Duration.ofSeconds(RETRY))
                .event(event.getType().getLabel())
                .data(event.getData())
                .build();
    }
}
