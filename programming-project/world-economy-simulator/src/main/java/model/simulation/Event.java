package model.simulation;

/**
 * Event represents an event in the simulation with a specific type and time.
 * It is used to schedule and manage events within the simulation.
 */
public class Event implements Comparable<Event> {
    private final EventType type;
    private final int time;

    /**
     * Constructs a new Event.
     * @param type the type of the event
     * @param time the time at which the event occurs
     */
    public Event(EventType type, int time) {
        this.type = type;
        this.time = time;
    }

    /**
     * Gets the type of the event.
     * @return the type of the event
     */
    public EventType getType() {
        return type;
    }

    /**
     * Gets the time at which the event occurs.
     * @return the time of the event
     */
    public int getTime() {
        return time;
    }

    /**
     * Compares this event to another event based on time and type.
     * @param other the other event to compare to
     * @return a negative integer, zero, or a positive integer as this event is less than, equal to, or greater than the specified event, respectively
     */
    @Override
    public int compareTo(Event other) {
        int timeComparison = Integer.compare(this.time, other.time);
        if (timeComparison != 0) {
            return timeComparison;
        }
        return Integer.compare(this.type.ordinal(), other.type.ordinal());
    }
}