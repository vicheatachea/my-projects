package model.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceNodeTest {
    private Country country;
    private Resource water;
    private ResourceNode resourceNode;

    @BeforeEach
    public void setUp() {
        water = new Resource("Water", 0.5, 100, 10.0);
        ResourceNodeDTO waterNodeDTO = new ResourceNodeDTO(1, 100, 10.0, water);

        HashMap<Resource, Integer> starterResources = new HashMap<>();
        starterResources.put(water, 100);
        HashMap<Resource, ResourceNodeDTO> ownedResources = new HashMap<>();
        ownedResources.put(water, waterNodeDTO);

        country = new Country("TestCountry", 100_000_000.0, 1_000_000, starterResources, ownedResources);
        resourceNode = country.getResourceNodes().getFirst();
    }

    @Test
    public void testGetMaxCapacity() {
        // Max capacity is base capacity * (1 + tier * TIER_UPGRADE_MULTIPLIER)
        int expectedMaxCapacity = (int) Math.round(100 * (1 + 1 * 0.1));
        assertEquals(expectedMaxCapacity, resourceNode.getMaxCapacity());
    }

    @Test
    public void testGetUpgradeCost() {
        // Upgrade cost is (1 + tier) * baseCapacity * POPULATION_SEGMENT_SIZE
        double expectedUpgradeCost = (1 + 1) * 10.0 * 100_000;
        assertEquals(expectedUpgradeCost, resourceNode.getUpgradeCost());
    }

    @Test
    public void testGetProductionCost() {
        // Production cost is baseProductionCost * (1 - reductionPercentage)
        // Initially, reductionPercentage is 0.0
        double expectedProductionCost = 10.0 * (1 - 0.0);
        assertEquals(expectedProductionCost, resourceNode.getProductionCost());
    }

    @Test
    public void testCollectResources() {
        resourceNode.produceResources(50);
        resourceNode.collectResources();
        assertEquals(150, country.getResourceStorage().get(water).getQuantity());
    }

    @Test
    public void testProduceResources() {
        // Resource production cost is quantity * getProductionCost() * POPULATION_SEGMENT_SIZE
        resourceNode.produceResources(50);
        assertEquals(50, resourceNode.getStoredResources());
        assertEquals(50_000_000.0, country.getMoney());
    }

    @Test
    public void testUpgradeNode() {
        // Upgrade cost is (tier + 1) * baseProductionCost * POPULATION_SEGMENT_SIZE
        resourceNode.upgradeNode();
        assertEquals(2, resourceNode.getTier());
        assertEquals(98_000_000.0, country.getMoney());
    }
}