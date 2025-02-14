package model.core;

import model.simulation.SimulationConfig;

import java.util.*;

/**
 * Country represents a country with a name, money, population, resources, and people.
 * It manages resources, people, and interactions with other countries.
 */
public class Country {
    // Constants
    private static final int PERSON_INITIAL_HAPPINESS = 0;
    private static final int PERSON_BASE_BUDGET = 10;
    private static final double COUNTRY_PROFIT_MARGIN = 0.1;
    private static final double COUNTRY_INDIVIDUAL_TAX = 0.3;
    private static final double BASE_EXPORT_TAX = 0.2;

    // Variables immediately initialized
    private final Map<Resource, ResourceInfo> resourceStorage = new HashMap<>();
    private final List<ResourceNode> resourceNodes = new ArrayList<>();
    private final List<Person> peopleObjects = new ArrayList<>();

    // Variables initialized in the constructor
    private List<Country> allCountries;
    private final String name;
    private double money;
    private long population;

    /**
     * Constructs a new Country.
     * @param name the name of the country
     * @param initialMoney the initial amount of money, cannot be negative
     * @param initialPopulation the initial population, must be positive
     * @param starterResources the initial resources of the country
     * @param ownedResources the resources owned by the country
     * @throws IllegalArgumentException if initialMoney is negative or initialPopulation is not positive
     */
    public Country(String name, double initialMoney, long initialPopulation,
                   Map<Resource, Integer> starterResources, Map<Resource, ResourceNodeDTO> ownedResources) {
        if (initialMoney < 0) {
            throw new IllegalArgumentException("Initial money cannot be negative.");
        }
        if (initialPopulation <= 0) {
            throw new IllegalArgumentException("Initial population must be positive.");
        }

        this.name = name;
        this.money = initialMoney;
        this.population = initialPopulation;

        // Initialize the resource storage and supply changes
        for (Map.Entry<Resource, Integer> entry : starterResources.entrySet()) {
            Resource resource = entry.getKey();
            int quantity = entry.getValue();
            double baseProductionCost;

            if (ownedResources.get(resource) != null) {
                baseProductionCost = ownedResources.get(resource).productionCost();
            } else {
                baseProductionCost = resource.productionCost();
            }


            resourceStorage.put(resource, new ResourceInfo(quantity, quantity * baseProductionCost,
                    SimulationConfig.getSupplyArchiveTime()));
        }

        // Create resource nodes based on the owned resources
        for (Resource resource : ownedResources.keySet()) {
            resourceNodes.add(new ResourceNode(this, resource, ownedResources.get(resource)));
        }

        // Create people objects based on the initial population
        int numberOfPeople = (int) Math.ceil((double) initialPopulation / SimulationConfig.getPopulationSegmentSize());
        for (int i = 0; i < numberOfPeople; i++) {
            peopleObjects.add(new Person(this, PERSON_INITIAL_HAPPINESS, starterResources.keySet()));
        }
    }

    /**
     * Gets the name of the country.
     * @return the name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current amount of money the country has.
     * @return the current amount of money
     */
    public double getMoney() {
        return money;
    }

    /**
     * Gets the current population of the country.
     * @return the current population
     */
    public long getPopulation() {
        return population;
    }

    /**
     * Gets the list of resource nodes in the country.
     * @return the list of resource nodes
     */
    public List<ResourceNode> getResourceNodes() {
        return resourceNodes;
    }

    /**
     * Gets the list of people objects in the country.
     * @return the list of people objects
     */
    public List<Person> getPeopleObjects() {
        return peopleObjects;
    }

    /**
     * Gets the resource storage of the country.
     * @return the resource storage
     */
    public Map<Resource, ResourceInfo> getResourceStorage() {
        return resourceStorage;
    }

    /**
     * Adds all countries to the list of countries.
     * @param allCountries the list of all countries
     */
    public void addAllCountries(List<Country> allCountries) {
        this.allCountries = allCountries;
    }

    /**
     * Gets the sell price of a resource.
     * @param resource the resource to get the sell price for
     * @return the sell price of the resource
     */
    double getResourceSellPrice(Resource resource) {
        return resourceStorage.get(resource).getValuePerUnit() * (1 + COUNTRY_PROFIT_MARGIN);
    }

    /**
     * Gets the budget for a population segment.
     * @return the budget for a population segment
     */
    public double getSegmentBudget() {
        int totalTier = 0;

        for (ResourceNode resourceNode : resourceNodes) {
            totalTier += resourceNode.getTier();
        }

        double calculatedBudget = Math.max(1, totalTier) * PERSON_BASE_BUDGET * (1 - COUNTRY_INDIVIDUAL_TAX)
                * SimulationConfig.getPopulationSegmentSize();
        return Math.max(calculatedBudget, PERSON_BASE_BUDGET);
    }

    /**
     * Gets the average happiness of the people in the country.
     * @return the average happiness
     */
    public double getAverageHappiness() {
        double totalHappiness = 0;

        for (Person person : peopleObjects) {
            totalHappiness += person.getHappiness();
        }

        return totalHappiness / peopleObjects.size();
    }

    /**
     * Event type: Updates the people in the country.
     */
    public void updatePeople() {
        updateNumberOfPeople();

        for (Person person : peopleObjects) {
            person.updatePerson();
        }
    }

    /**
     * Event type: Obtains resources from the resource nodes.
     */
    public void obtainResources() {
        for (ResourceNode resourceNode : resourceNodes) {
            resourceNode.collectResources();
        }
    }

    /**
     * Event type: Serves the people in the country with the available budget.
     */
    public void servePeople() {
        double budget = this.getSegmentBudget();

        for (Person person : peopleObjects) {
            person.servePerson(budget);
        }
    }

    /**
     * Event type: Requests resources based on the demand and supply changes.
     */
    public void requestResources() {
        for (ResourceInfo resourceInfo : resourceStorage.values()) {
            resourceInfo.archiveSupply();
        }

        Map<Resource, Integer> totalDemand = new HashMap<>();
        Map<Resource, Double> totalSupplyChange = new HashMap<>();
        Set<ResourceNode> upgradedNodes = new HashSet<>();

        // Get the average supply change for each resource
        for (Map.Entry<Resource, ResourceInfo> entry : resourceStorage.entrySet()) {
            Resource resource = entry.getKey();
            ResourceInfo resourceInfo = entry.getValue();

            int[] supplyArchive = resourceInfo.getSupplyArchive();
//            System.out.println("Supply archive: " + Arrays.toString(supplyArchive));
            double totalChange = 0;

            if (resourceInfo.getCurrentSize() > 1) {
                totalChange = supplyArchive[0] - supplyArchive[resourceInfo.getCurrentSize() - 1];
            }

//            System.out.println("Total change: " + totalChange);
            double averageChange = resourceInfo.getCurrentSize() > 1 ? totalChange / (resourceInfo.getCurrentSize() - 1) : 0;
//            System.out.println("Average change: " + averageChange);
            totalSupplyChange.put(resource, averageChange);
        }

        // Get the combined demand of all people for each resource
        for (Person person : peopleObjects) {
            for (Map.Entry<Resource, Integer> demandEntry : person.getDemand().entrySet()) {
                Resource resource = demandEntry.getKey();
                int demand = demandEntry.getValue();
                totalDemand.put(resource, totalDemand.getOrDefault(resource, 0) + demand);
            }
        }
//        System.out.println("Total demand: " + totalDemand);

//        System.out.println("Country: " + this.name);
        // Periodic production based on supply change
        for (ResourceNode resourceNode : resourceNodes) {
            Resource resource = resourceNode.getResource();
            double supplyChange = totalSupplyChange.getOrDefault(resource, 0.0);

            int maxCapacity = resourceNode.getMaxCapacity();
//            System.out.println("Supply change: " + supplyChange);
            int quantityToProduce = (int) Math.ceil(-supplyChange);

            if (quantityToProduce <= maxCapacity) {
//                System.out.println("Producing " + quantityToProduce + " " + resource.name());
                resourceNode.produceResources(quantityToProduce);
            } else {
//                System.out.println("Producing " + maxCapacity + " " + resource.name());
                resourceNode.produceResources(maxCapacity);
                if (this.money >= resourceNode.getUpgradeCost() && !upgradedNodes.contains(resourceNode)) {
                    resourceNode.upgradeNode();
                    upgradedNodes.add(resourceNode);
                }
            }
        }


        // Decide on production, upgrades, and trading based on demand
        for (Map.Entry<Resource, Integer> demandEntry : totalDemand.entrySet()) {
            Resource resource = demandEntry.getKey();
            int demand = demandEntry.getValue();

            if (resourceStorage.get(resource).getQuantity() == 0) {
                // Resource is not in storage, try to produce it
                ResourceNode resourceNode = getNodeFromResource(resource);
                if (resourceNode != null) {
                    resourceNode.produceResources(resourceNode.getMaxCapacity());
                } else {
                    // Resource node not available, trade for the resource
                    int currentQuantity = resourceStorage.get(resource).getQuantity();
                    int targetQuantity = currentQuantity + demand;
                    for (Country otherCountry : this.allCountries) {
                        if (otherCountry != this && currentQuantity < targetQuantity) {
                            otherCountry.requestTrade(this, resource, demand);
                            currentQuantity = resourceStorage.get(resource).getQuantity();
                        }
                    }
                }
            } else {
                // People don't have enough money, upgrade an important resource node
                upgradeImportantResourceNode(totalDemand, totalSupplyChange, upgradedNodes);
            }
        }
    }

    /**
     * Adds a specified quantity of a resource to the country's storage.
     * @param resource the resource to add
     * @param quantity the quantity to add
     */
    void addResources(Resource resource, int quantity) {
        ResourceInfo resourceInfo = this.resourceStorage.get(resource);
        double productionCost;

        // Check if the country has a resource node of that resource type
        ResourceNode resourceNode = getNodeFromResource(resource);
        if (resourceNode != null) {
            productionCost = resourceNode.getProductionCost();
        } else {
            productionCost = resource.productionCost();
        }

        // Calculate the value of the resources
        double value = productionCost * quantity * SimulationConfig.getPopulationSegmentSize();
        resourceInfo.addQuantity(quantity);
        resourceInfo.addValue(value);
    }

    /**
     * Removes a specified quantity of a resource from the country's storage.
     * @param resource the resource to remove
     * @param quantity the quantity to remove
     */
    void removeResources(Resource resource, int quantity) {
        this.resourceStorage.get(resource).subtractQuantityAndValue(quantity);
    }

    /**
     * Gets the quantity of a specified resource in the country's storage.
     * @param resource the resource to get the quantity for
     * @return the quantity of the resource
     */
    double getResourceQuantity(Resource resource) {
        return this.resourceStorage.get(resource).getQuantity();
    }

    /**
     * Adds a specified amount of money to the country's funds.
     * @param amount the amount of money to add
     */
    void addMoney(double amount) {
        this.money += amount;
    }

    /**
     * Subtracts a specified amount of money from the country's funds.
     * @param amount the amount of money to subtract
     */
    void subtractMoney(double amount) {
        this.money -= amount;
    }

    /**
     * Adds a specified number of people to the country's population.
     * @param population the number of people to add
     */
    void addPopulation(long population) {
        this.population += population;
    }

    /**
     * Subtracts a specified number of people from the country's population.
     * @param population the number of people to subtract
     */
    void subtractPopulation(long population) {
        if (this.population < population) {
            this.population = 1;
            return;
        }
        this.population -= population;
    }

    /**
     * Updates the number of people in the country based on the population.
     */
    private void updateNumberOfPeople() {
        int numberOfPeople = (int) Math.ceil((double) this.population / SimulationConfig.getPopulationSegmentSize());

        if (numberOfPeople > this.peopleObjects.size()) {
            for (int i = this.peopleObjects.size(); i < numberOfPeople; i++) {
                this.peopleObjects.add(new Person(this, PERSON_INITIAL_HAPPINESS, this.resourceStorage.keySet()));
            }
        } else if (numberOfPeople < this.peopleObjects.size()) {
            this.peopleObjects.subList(numberOfPeople, this.peopleObjects.size()).clear();
        }
    }

    /**
     * Gets the resource node associated with a specified resource.
     * @param resource the resource to get the node for
     * @return the resource node associated with the resource, or null if not found
     */
    private ResourceNode getNodeFromResource(Resource resource) {
        for (ResourceNode resourceNode : resourceNodes) {
            if (resourceNode.getResource().equals(resource)) {
                return resourceNode;
            }
        }
        return null;
    }

    /**
     * Upgrades the most important resource node based on demand and supply changes.
     * @param totalDemand the total demand for resources
     * @param totalSupplyChange the total supply change for resources
     * @param upgradedNodes the set of already upgraded nodes
     */
    private void upgradeImportantResourceNode(Map<Resource, Integer> totalDemand, Map<Resource, Double> totalSupplyChange, Set<ResourceNode> upgradedNodes) {
        Resource mostImportantResource = null;
        double maxDifference = Double.MIN_VALUE;

        for (Map.Entry<Resource, Integer> entry : totalDemand.entrySet()) {
            Resource resource = entry.getKey();
            int demand = entry.getValue();
            double supplyChange = totalSupplyChange.getOrDefault(resource, 0.0);
            double difference = demand - supplyChange;

            if (difference > maxDifference) {
                maxDifference = difference;
                mostImportantResource = resource;
            }
        }

        if (mostImportantResource != null) {
            ResourceNode resourceNode = getNodeFromResource(mostImportantResource);
            if (resourceNode != null && this.money >= resourceNode.getUpgradeCost() && !upgradedNodes.contains(resourceNode)) {
                resourceNode.upgradeNode();
                upgradedNodes.add(resourceNode);
            }
        }
    }

    /**
     * Handles a trade request from another country.
     * @param requestingCountry the country requesting the trade
     * @param resource the resource to trade
     * @param quantity the quantity of the resource to trade
     */
    void requestTrade(Country requestingCountry, Resource resource, int quantity) {
        ResourceInfo resourceInfo = resourceStorage.get(resource);
        double exportPrice = resourceInfo.getValuePerUnit() * (1 + BASE_EXPORT_TAX);
        int availableQuantity = resourceStorage.get(resource).getQuantity();
        int quantityToTrade = (int) Math.min(quantity, Math.min(availableQuantity, requestingCountry.getMoney() / exportPrice));
        double totalCost = quantityToTrade * exportPrice;

        if (quantityToTrade > 0) {
            removeResources(resource, quantityToTrade);
            addMoney(totalCost);
            requestingCountry.addResources(resource, quantityToTrade);
            requestingCountry.subtractMoney(totalCost);
        }
    }
}