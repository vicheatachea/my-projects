package model.simulation;

/**
 * EventType represents the types of events that can occur in the simulation.
 * Each event type corresponds to a specific action or process within the simulation.
 */
public enum EventType {
    /**
     * Event type for updating the people in the country.
     */
    UPDATE_PEOPLE,

    /**
     * Event type for obtaining resources from resource nodes.
     */
    OBTAIN_RESOURCES,

    /**
     * Event type for serving the people in the country with available resources.
     */
    SERVE_PEOPLE,

    /**
     * Event type for requesting resources based on demand and supply changes.
     */
    REQUEST_RESOURCES
}