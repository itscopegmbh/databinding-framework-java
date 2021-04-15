package de.itscope.databinding.events;

import java.io.Serializable;

public class DeleteEventData implements EventData {
    private final Serializable serial;

    public DeleteEventData(Serializable serial) {
        this.serial = serial;
    }

    public Serializable getSerial() {
        return serial;
    }
}
