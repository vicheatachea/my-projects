package model.core;

/**
 * Resource represents an abstract resource with a name, priority, base capacity, and production cost.
 * @param name the name of the resource
 * @param priority the priority (importance) of the resource, must be between 0 and 1
 * @param baseCapacity the base production capacity of the resource, must be positive
 * @param productionCost the production cost of the resource, cannot be negative
 */
public record Resource(String name, double priority, int baseCapacity, double productionCost) {
    /**
     * Constructs a new Resource.
     * @throws IllegalArgumentException if priority is not between 0 and 1,
     *                                  if baseCapacity is not positive,
     *                                  or if productionCost is negative
     */
    public Resource {
        if (priority < 0 || priority > 1) {
            throw new IllegalArgumentException("Priority must be between 0 and 1.");
        }
        if (baseCapacity <= 0) {
            throw new IllegalArgumentException("Base capacity must be positive.");
        }
        if (productionCost < 0) {
            throw new IllegalArgumentException("Production cost cannot be negative.");
        }
    }
}