package model.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceTest {

    @Test
    public void testValidResource() {
        Resource resource = new Resource("Water", 0.5, 100, 10.0);
        assertEquals("Water", resource.name());
        assertEquals(0.5, resource.priority());
        assertEquals(100, resource.baseCapacity());
        assertEquals(10.0, resource.productionCost());
    }

    @Test
    public void testInvalidPriorityLow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Resource("Water", -0.1, 100, 10.0));
        assertEquals("Priority must be between 0 and 1.", exception.getMessage());
    }

    @Test
    public void testInvalidPriorityHigh() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Resource("Water", 1.1, 100, 10.0));
        assertEquals("Priority must be between 0 and 1.", exception.getMessage());
    }

    @Test
    public void testInvalidBaseCapacity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Resource("Water", 0.5, 0, 10.0));
        assertEquals("Base capacity must be positive.", exception.getMessage());
    }

    @Test
    public void testInvalidProductionCost() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Resource("Water", 0.5, 100, -10.0));
        assertEquals("Production cost cannot be negative.", exception.getMessage());
    }
}