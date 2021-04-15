package de.itscope.databinding.events;

public abstract class Event {
    private final EventData data;
    private final EventType type;

    public Event(EventData data, EventType type) {
        this.data = data;
        this.type = type;
    }

    public EventData getData() {
        return data;
    }

    public EventType getType() {
        return type;
    }
}
