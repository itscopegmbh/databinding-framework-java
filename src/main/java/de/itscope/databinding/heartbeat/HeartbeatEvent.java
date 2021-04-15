package de.itscope.databinding.heartbeat;

import de.itscope.databinding.events.Event;
import de.itscope.databinding.events.EventType;

class HeartbeatEvent extends Event {
    public HeartbeatEvent() {
        super(new HeartbeatEventData(), EventType.HEARTBEAT);
    }
}
