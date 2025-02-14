package model.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryTest {
    private Country country;
    private Resource water;
    private Map<Resource, Integer> starterResources;
    private Map<Resource, ResourceNodeDTO> ownedResources;

    @BeforeEach
    public void setUp() {
        water = new Resource("Water", 0.5, 100, 10.0);
        ResourceNodeDTO waterNodeDTO = new ResourceNodeDTO(1, 100, 10.0, water);
        starterResources = new HashMap<>();
        starterResources.put(water, 100);
        ownedResources = new HashMap<>();
        ownedResources.put(water, waterNodeDTO);
        country = new Country("TestCountry", 100_000_000.0, 1_000_000, starterResources, ownedResources);
    }

    @Test
    public void testValidCountry() {
        assertEquals("TestCountry", country.getName());
        assertEquals(100_000_000.0, country.getMoney());
        assertEquals(1_000_000, country.getPopulation());
        assertEquals(1, country.getResourceNodes().size());
        assertEquals(100, country.getResourceStorage().get(water).getQuantity());
    }

    @Test
    public void testInvalidInitialMoney() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Country("TestCountry", -100.0, 1_000_000, starterResources, ownedResources));
        assertEquals("Initial money cannot be negative.", exception.getMessage());
    }

    @Test
    public void testInvalidInitialPopulation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Country("TestCountry", 100_000_000.0, 0, starterResources, ownedResources));
        assertEquals("Initial population must be positive.", exception.getMessage());
    }

    @Test
    public void testAddResources() {
        country.addResources(water, 50);
        assertEquals(150, country.getResourceStorage().get(water).getQuantity());
    }

    @Test
    public void testRemoveResources() {
        country.removeResources(water, 50);
        assertEquals(50, country.getResourceStorage().get(water).getQuantity());
    }

    @Test
    public void testAddMoney() {
        country.addMoney(5_000_000.0);
        assertEquals(105_000_000.0, country.getMoney());
    }

    @Test
    public void testSubtractMoney() {
        country.subtractMoney(5_000_000.0);
        assertEquals(95_000_000.0, country.getMoney());
    }

    @Test
    public void testAddPopulation() {
        country.addPopulation(5000);
        assertEquals(1_005_000, country.getPopulation());
    }

    @Test
    public void testSubtractPopulation() {
        country.subtractPopulation(500);
        assertEquals(999_500, country.getPopulation());
    }

    @Test
    public void testSubtractPopulationBelowZero() {
        country.subtractPopulation(2_000_000);
        assertEquals(1, country.getPopulation());
    }

    // More complex tests

    @Test
    public void testGetResourceSellPrice() {
        // Resource sell price is value / quantity * (1 + COUNTRY_PROFIT_MARGIN)
        // value = quantity * productionCost
        double expectedValue = 100 * 10.0 / 100 * (1 + 0.1);
        assertEquals(expectedValue, country.getResourceSellPrice(water));
    }

    @Test
    public void testGetSegmentBudget() {
        // Segment budget is max(1, totalTier) * PERSON_BASE_BUDGET * (1 - COUNTRY_INDIVIDUAL_TAX) * SimulationConfig.getPopulationSegmentSize()
        double expectedBudget = Math.max(1, 1) * 10 * (1 - 0.3) * 100_000;
        assertEquals(expectedBudget, country.getSegmentBudget());
    }

    @Test
    public void testGetAverageHappiness() {
        // Initial happiness of all people is 0
        double expectedHappiness = 0;
        assertEquals(expectedHappiness, country.getAverageHappiness());
    }

    @Test
    public void testRequestTrade() {
        Country otherCountry = new Country("OtherCountry", 2000.0, 2000, starterResources, ownedResources);
        country.addAllCountries(List.of(otherCountry));
        country.requestTrade(otherCountry, water, 50);
        assertEquals(50, country.getResourceStorage().get(water).getQuantity());
        assertEquals(150, otherCountry.getResourceStorage().get(water).getQuantity());
    }
}