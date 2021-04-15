package de.itscope.databinding.events;

public class UpdateEvent extends Event {
    public UpdateEvent(EventData eventData) {
        super(eventData, EventType.UPDATE);
    }
}
