package de.itscope.databinding.events;

public enum EventType {
    HEARTBEAT("heartbeat"),
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
