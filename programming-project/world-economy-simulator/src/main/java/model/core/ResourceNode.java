package model.core;

import model.simulation.SimulationConfig;

/**
 * ResourceNode represents a node that produces and stores resources for a country.
 * It manages the production, temporary storage, and upgrading of resources.
 */
public class ResourceNode {
    // Constants
    private static final double MAX_REDUCTION_PERCENTAGE = 0.5;
    private static final double TIER_UPGRADE_MULTIPLIER = 0.1;

    // Variables immediately initialized
    private int storedResources = 0;
    private int daysSinceLastProduction = 0;

    // Variables initialized in the constructor
    private final Country country;
    private final int baseCapacity;
    private final double baseProductionCost;
    private final Resource resource;
    private int tier;

    /**
     * Constructs a new ResourceNode.
     * @param country the country that owns this resource node
     * @param resource the resource produced by this node
     * @param resourceNodeDTO the data transfer object containing the node's initial properties
     */
    public ResourceNode(Country country, Resource resource, ResourceNodeDTO resourceNodeDTO) {
        this.country = country;
        this.baseCapacity = resourceNodeDTO.baseCapacity();
        this.resource = resource;
        this.baseProductionCost = resourceNodeDTO.productionCost();
        this.tier = resourceNodeDTO.tier();
    }

    /**
     * Gets the stored resources in this node.
     * @return the stored resources
     */
    public int getStoredResources() {
        return storedResources;
    }

    /**
     * Gets the country that owns this resource node.
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Gets the base capacity of this resource node.
     * @return the base capacity
     */
    public int getBaseCapacity() {
        return baseCapacity;
    }

    /**
     * Gets the resource produced by this node.
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Gets the base production cost of this resource node.
     * @return the base production cost
     */
    public double getBaseProductionCost() {
        return baseProductionCost;
    }

    /**
     * Gets the tier (upgrade level) of this resource node.
     * @return the tier
     */
    public int getTier() {
        return tier;
    }

    /**
     * Gets the maximum capacity of this resource node, considering its tier.
     * @return the maximum capacity
     */
    public int getMaxCapacity() {
        return (int) Math.round(baseCapacity * (1 + tier * TIER_UPGRADE_MULTIPLIER));
    }

    /**
     * Gets the cost to upgrade this resource node to the next tier.
     * @return the upgrade cost
     */
    double getUpgradeCost() {
        return (tier + 1) * baseProductionCost * SimulationConfig.getPopulationSegmentSize();
    }

    /**
     * Gets the production cost of this resource node, considering the reduction factor based on days since last production.
     * @return the production cost
     */
    public double getProductionCost() {
        double reductionFactor = Math.min(daysSinceLastProduction * 0.01, MAX_REDUCTION_PERCENTAGE);
        return baseProductionCost * (1 - reductionFactor);
    }

    /**
     * Collects the stored resources and adds them to the country's storage.
     */
    void collectResources() {
        if (storedResources != 0) {
            country.addResources(resource, storedResources);
        } else {
            daysSinceLastProduction++;
        }
    }

    /**
     * Produces a specified quantity of resources, considering the available money and maximum capacity.
     * @param quantity the quantity to produce
     */
    void produceResources(int quantity) {
        double availableMoney = country.getMoney();
        double actualProductionCost = getProductionCost() * SimulationConfig.getPopulationSegmentSize();
        int producedQuantity = (int) Math.min(quantity, Math.min(getMaxCapacity(), availableMoney / actualProductionCost));

        if (quantity > 0) {
            country.subtractMoney(producedQuantity * actualProductionCost);
            storedResources += producedQuantity;
            daysSinceLastProduction = 0;
        }
    }

    /**
     * Upgrades this resource node to the next tier, if the country has enough money.
     */
    void upgradeNode() {
        double upgradeCost = getUpgradeCost();
        if (country.getMoney() >= upgradeCost) {
            country.subtractMoney(upgradeCost);
            tier++;
        }
    }
}