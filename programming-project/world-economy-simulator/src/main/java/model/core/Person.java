package model.core;

import model.simulation.SimulationConfig;

import java.util.*;

/**
 * Person represents a segment of individuals in a country, with preferences and demand for resources, and happiness.
 * It manages the preferences, demand, and interactions with the country's resources.
 */
public class Person {
    // Constants
    private final static double PREFERENCE_ADJUSTMENT_PROBABILITY = 0.2;
    private final static double PREFERENCE_ADJUSTMENT_RANGE = 0.1;
    private static final double MAX_HAPPINESS_CHANGE = 0.05;
    private static final double POPULATION_CHANGE_THRESHOLD = 0.6;
    private static final double POPULATION_CHANGE_PERCENTAGE = 0.01;

    // Variables immediately initialized
    private final Map<Resource, Double> preferences = new HashMap<>();
    private final Map<Resource, Integer> demand = new HashMap<>();

    // Variables initialized in the constructor
    private final Country country;
    private double happiness;

    /**
     * Constructs a new Person.
     * @param country the country this person belongs to
     * @param initialHappiness the initial happiness of the person
     * @param availableResources the set of resources available in the country
     */
    public Person(Country country, double initialHappiness, Set<Resource> availableResources) {
        this.country = country;
        this.happiness = initialHappiness;

        // Initialize the preferences map with random values
        Random random = new Random();
        for (Resource resource : availableResources) {
            preferences.put(resource, random.nextDouble());
        }
        generateDemand();
    }

    /**
     * Gets the happiness of the person.
     * @return the happiness
     */
    public double getHappiness() {
        return happiness;
    }

    /**
     * Gets the preferences of the person for resources.
     * @return the preferences
     */
    public Map<Resource, Double> getPreferences() {
        return preferences;
    }

    /**
     * Gets the demand of the person for resources.
     * @return the demand
     */
    public Map<Resource, Integer> getDemand() {
        return demand;
    }

    /**
     * Updates the person's preferences and demand.
     */
    void updatePerson() {
        adjustPreferences();
        generateDemand();
    }

    /**
     * Serves the person with the available budget, updating their happiness and the country's resources.
     * @param budget the budget available to serve the person
     */
    void servePerson(double budget) {
        int totalDemand = demand.size();
        Iterator<Map.Entry<Resource, Integer>> demandIterator = demand.entrySet().iterator();
        while (demandIterator.hasNext()) {
            Map.Entry<Resource, Integer> entry = demandIterator.next();
            Resource resource = entry.getKey();
            int quantity = entry.getValue();
            double totalCost = quantity * country.getResourceSellPrice(resource);

            if (country.getResourceQuantity(resource) >= quantity && budget >= totalCost) {
                country.removeResources(resource, quantity);
                country.addMoney(totalCost);
                budget -= totalCost;
                demandIterator.remove();
            }
        }

        double happinessChange = 0.0;
        if (totalDemand > 0) {
            double percentageFilled = (double) (totalDemand - demand.size()) / totalDemand;
            happinessChange = (percentageFilled * 2 - 1) * MAX_HAPPINESS_CHANGE;
        }

        happiness = Math.max(-1.0, Math.min(1.0, happiness + happinessChange));

        if (happiness > POPULATION_CHANGE_THRESHOLD) {
            country.addPopulation((long) (SimulationConfig.getPopulationSegmentSize() * POPULATION_CHANGE_PERCENTAGE));
        } else if (happiness < -POPULATION_CHANGE_THRESHOLD) {
            country.subtractPopulation((long) (SimulationConfig.getPopulationSegmentSize() * POPULATION_CHANGE_PERCENTAGE));
        }
    }

    /**
     * Adjusts the person's preferences for resources randomly.
     */
    private void adjustPreferences() {
        Random random = new Random();
        int totalPreferences = preferences.size();
        int preferencesToAdjust = (int) Math.ceil(PREFERENCE_ADJUSTMENT_PROBABILITY * totalPreferences);

        Set<Resource> resourceSet = preferences.keySet();
        List<Resource> resourceList = new ArrayList<>(resourceSet);
        Collections.shuffle(resourceList, random);

        for (int i = 0; i < preferencesToAdjust; i++) {
            Resource resource = resourceList.get(i);
            double currentPreference = preferences.get(resource);
            double adjustment = (random.nextDouble() - 0.5) * PREFERENCE_ADJUSTMENT_RANGE;
            double newPreference = Math.max(0.0, Math.min(1.0, currentPreference + adjustment));
            preferences.put(resource, newPreference);
        }
    }

    /**
     * Generates the person's demand for resources based on their preferences and happiness.
     */
    private void generateDemand() {
        int totalResources = preferences.size();
        int numberOfResources = Math.max(1, (int) Math.round((happiness + 1) / 2 * totalResources));

        demand.clear();
        Random random = new Random();
        Map<Resource, Double> weightedProbabilities = getWeighedProbabilities();

        List<Resource> resourceList = new ArrayList<>(weightedProbabilities.keySet());
        for (int i = 0; i < numberOfResources; i++) {
            double rand = random.nextDouble();
            double cumulativeProbability = 0.0;

            for (Resource resource : resourceList) {
                cumulativeProbability += weightedProbabilities.get(resource);
                if (rand <= cumulativeProbability) {
                    demand.put(resource, 1);
                    break;
                }
            }
        }
    }

    /**
     * Calculates the weighted probabilities of resources based on the person's preferences.
     * @return the weighted probabilities of resources
     */
    private Map<Resource, Double> getWeighedProbabilities() {
        Map<Resource, Double> weightedProbabilities = new HashMap<>();
        double totalWeight = 0.0;

        for (Map.Entry<Resource, Double> entry : preferences.entrySet()) {
            Resource resource = entry.getKey();
            double preference = entry.getValue();
            double weight = preference * resource.priority();
            weightedProbabilities.put(resource, weight);
            totalWeight += weight;
        }

        // Normalize the probabilities
        for (Map.Entry<Resource, Double> entry : weightedProbabilities.entrySet()) {
            weightedProbabilities.put(entry.getKey(), entry.getValue() / totalWeight);
        }

        return weightedProbabilities;
    }
}