package model.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceNodeDTOTest {
    private static Resource resource;

    @BeforeAll
    public static void setUp() {
        resource = new Resource("Water", 0.5, 100, 10.0);
    }

    @Test
    public void testValidResourceNodeDTO() {
        ResourceNodeDTO resourceNodeDTO = new ResourceNodeDTO(1, 100, 10.0, resource);
        assertEquals(1, resourceNodeDTO.tier());
        assertEquals(100, resourceNodeDTO.baseCapacity());
        assertEquals(10.0, resourceNodeDTO.productionCost());
        assertEquals(resource, resourceNodeDTO.resource());
    }

    @Test
    public void testInvalidTier() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ResourceNodeDTO(-1, 100, 10.0, resource));
        assertEquals("Tier cannot be negative.", exception.getMessage());
    }

    @Test
    public void testInvalidBaseCapacity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ResourceNodeDTO(1, 0, 10.0, resource));
        assertEquals("Base capacity must be positive.", exception.getMessage());
    }

    @Test
    public void testInvalidProductionCost() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new ResourceNodeDTO(1, 100, -10.0, resource));
        assertEquals("Production cost cannot be negative.", exception.getMessage());
    }
}