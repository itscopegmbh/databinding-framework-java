package de.itscope.databinding.events;

import java.io.Serializable;

public class DeleteEvent extends Event {
    public DeleteEvent(Serializable serial) {
        super(new DeleteEventData(serial), EventType.DELETE);
    }
}
