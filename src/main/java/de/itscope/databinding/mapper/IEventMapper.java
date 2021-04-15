package de.itscope.databinding.mapper;

import de.itscope.databinding.events.Event;

public interface IEventMapper<T> {
    Event mapToDatabindingEvent(T t);
}
