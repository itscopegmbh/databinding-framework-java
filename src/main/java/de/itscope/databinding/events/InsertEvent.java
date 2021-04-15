package de.itscope.databinding.events;

public class InsertEvent extends Event {
    public InsertEvent(EventData eventData) {
        super(eventData, EventType.INSERT);
    }
}
