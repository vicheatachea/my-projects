package model.core;

/**
 * ResourceNodeDTO represents a Data Transfer Object (DTO) for a resource node, containing information
 * about its tier, base capacity, production cost, and associated resource.
 * @param tier the tier (upgrade level) of the resource node, cannot be negative
 * @param baseCapacity the base production capacity of the resource node, must be positive
 * @param productionCost the production cost of the resource node, cannot be negative
 * @param resource the resource associated with this node
 */
public record ResourceNodeDTO(int tier, int baseCapacity, double productionCost, Resource resource) {
    /**
     * Constructs a new ResourceNodeDTO.
     * @throws IllegalArgumentException if tier is negative,
     *                                  if baseCapacity is not positive,
     *                                  or if productionCost is negative
     */
    public ResourceNodeDTO {
        if (tier < 0) {
            throw new IllegalArgumentException("Tier cannot be negative.");
        }
        if (baseCapacity <= 0) {
            throw new IllegalArgumentException("Base capacity must be positive.");
        }
        if (productionCost < 0) {
            throw new IllegalArgumentException("Production cost cannot be negative.");
        }
    }
}