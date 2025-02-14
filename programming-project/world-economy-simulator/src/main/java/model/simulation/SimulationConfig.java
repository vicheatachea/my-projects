package model.simulation;

/**
 * SimulationConfig provides configuration settings for the simulation, including simulation time, delay, supply archive time, and population segment size.
 * It offers getters and setters for these configuration parameters.
 */
public class SimulationConfig {
    private static int simulationTime = 100;
    private static int simulationDelay = 100;
    private static int supplyArchiveTime = 128;
    private static int populationSegmentSize = 100_000;

    /**
     * Gets the total simulation time.
     * @return the simulation time
     */
    public static int getSimulationTime() {
        return simulationTime;
    }

    /**
     * Gets the delay between simulation steps.
     * @return the simulation delay
     */
    public static int getSimulationDelay() {
        return simulationDelay;
    }

    /**
     * Gets the length of the supply archive.
     * @return the supply archive time
     */
    public static int getSupplyArchiveTime() {
        return supplyArchiveTime;
    }

    /**
     * Gets the size of a population segment.
     * @return the population segment size
     */
    public static int getPopulationSegmentSize() {
        return populationSegmentSize;
    }

    /**
     * Sets the total simulation time.
     * @param newSimulationTime the new simulation time, must be positive
     * @throws IllegalArgumentException if the new simulation time is not positive
     */
    public static void setSimulationTime(int newSimulationTime) {
        if (newSimulationTime <= 0) {
            throw new IllegalArgumentException("Simulation time must be positive");
        }
        simulationTime = newSimulationTime;
    }

    /**
     * Sets the delay between simulation steps.
     * @param newSimulationDelay the new simulation delay, must be positive
     * @throws IllegalArgumentException if the new simulation delay is not positive
     */
    public static void setSimulationDelay(int newSimulationDelay) {
        if (newSimulationDelay <= 0) {
            throw new IllegalArgumentException("Simulation delay must be positive");
        }
        simulationDelay = newSimulationDelay;
    }

    /**
     * Sets the length of the supply archive.
     * @param newSupplyArchiveTime the new supply archive time, must be positive
     * @throws IllegalArgumentException if the new supply archive time is not positive
     */
    public static void setSupplyArchiveTime(int newSupplyArchiveTime) {
        if (newSupplyArchiveTime <= 0) {
            throw new IllegalArgumentException("Supply archive time must be positive");
        }
        supplyArchiveTime = newSupplyArchiveTime;
    }

    /**
     * Sets the size of a population segment.
     * @param newPopulationSegmentSize the new population segment size, must be positive
     * @throws IllegalArgumentException if the new population segment size is not positive
     */
    public static void setPopulationSegmentSize(int newPopulationSegmentSize) {
        if (newPopulationSegmentSize <= 0) {
            throw new IllegalArgumentException("Population segment size must be positive");
        }
        populationSegmentSize = newPopulationSegmentSize;
    }
}