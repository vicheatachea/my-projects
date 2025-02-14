package model.simulation;

import java.util.PriorityQueue;

/**
 * EventList represents a list of events in the simulation, managed as a priority queue.
 * It provides methods to add, retrieve, and check events in the queue.
 */
public class EventList {
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();

    /**
     * Adds an event to the event queue.
     * @param event the event to add
     */
    public void addEvent(Event event) {
        eventQueue.add(event);
    }

    /**
     * Retrieves and removes the next event from the event queue.
     * @return the next event, or null if the queue is empty
     */
    public Event getNextEvent() {
        return eventQueue.poll();
    }

    /**
     * Checks if there are more events in the event queue.
     * @return true if there are more events, false otherwise
     */
    public boolean hasMoreEvents() {
        return !eventQueue.isEmpty();
    }

    /**
     * Peeks at the next event in the event queue without removing it.
     * @return the next event, or null if the queue is empty
     */
    public Event peekNextEvent() {
        return eventQueue.peek();
    }

    /**
     * Gets the event queue.
     * @return the event queue
     */
    public PriorityQueue<Event> getEventQueue() {
        return eventQueue;
    }
}