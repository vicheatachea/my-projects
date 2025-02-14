package model.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {
    private Country country;
    private Resource water;
    private Person person;

    @BeforeEach
    public void setUp() {
        water = new Resource("Water", 0.5, 100, 10.0);
        ResourceNodeDTO waterNodeDTO = new ResourceNodeDTO(1, 100, 10.0, water);
        Map<Resource, Integer> starterResources = new HashMap<>();
        starterResources.put(water, 100);
        Map<Resource, ResourceNodeDTO> ownedResources = new HashMap<>();
        ownedResources.put(water, waterNodeDTO);
        country = new Country("TestCountry", 100_000_000.0, 1_000_000, starterResources, ownedResources);
        person = country.getPeopleObjects().getFirst();
    }

    @Test
    public void testInitialHappiness() {
        assertEquals(0.0, person.getHappiness());
    }

    @Test
    public void testInitialPreferences() {
        assertTrue(person.getPreferences().containsKey(water));
    }

    @Test
    public void testInitialDemand() {
        assertTrue(person.getDemand().containsKey(water));
    }

    @Test
    public void testUpdatePerson() {
        person.updatePerson();
        assertTrue(person.getDemand().containsKey(water));
    }

    @Test
    public void testServePerson() {
        double initialMoney = country.getMoney();
        person.servePerson(100.0);
        assertTrue(person.getHappiness() >= -1.0 && person.getHappiness() <= 1.0);
        assertTrue(country.getMoney() > initialMoney);
    }
}