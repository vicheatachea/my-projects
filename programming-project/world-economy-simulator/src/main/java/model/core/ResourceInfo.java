package model.core;

/**
 * ResourceInfo represents the information about a resource, including its quantity, value, and supply archive.
 */
public class ResourceInfo {
    private int quantity;
    private double value;
    private final int[] supplyArchive;
    private int currentSize = 0;

    /**
     * Constructs a new ResourceInfo.
     * @param quantity the initial quantity of the resource
     * @param value the initial value of the resource
     * @param archiveTime the length of the supply archive
     */
    public ResourceInfo(int quantity, double value, int archiveTime) {
        this.quantity = quantity;
        this.value = value;
        this.supplyArchive = new int[archiveTime];
    }

    /**
     * Gets the current quantity of the resource.
     * @return the current quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the current value of the resource.
     * @return the current value
     */
    public double getValue() {
        return value;
    }

    /**
     * Gets the supply archive of the resource.
     * @return the supply archive
     */
    public int[] getSupplyArchive() {
        return supplyArchive;
    }

    /**
     * Gets the current size of the supply archive.
     * @return the current size
     */
    public int getCurrentSize() {
        return currentSize;
    }

    /**
     * Gets the value per unit of the resource.
     * @return the value per unit
     */
    public double getValuePerUnit() {
        return value / quantity;
    }

    /**
     * Adds a specified quantity to the resource.
     * @param quantity the quantity to add
     */
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    /**
     * Adds a specified value to the resource.
     * @param value the value to add
     */
    public void addValue(double value) {
        this.value += value;
    }

    /**
     * Subtracts a specified quantity and its corresponding value from the resource.
     * @param quantity the quantity to subtract
     */
    public void subtractQuantityAndValue(int quantity) {
        int initialQuantity = this.quantity;
        double valueDifference = this.value / initialQuantity * quantity;

        this.quantity -= quantity;
        this.value -= valueDifference;
    }

    /**
     * Archives the current supply of the resource.
     */
    public void archiveSupply() {
        if (currentSize < supplyArchive.length) {
            currentSize++;
        }
        for (int i = currentSize - 1; i > 0; i--) {
            supplyArchive[i] = supplyArchive[i - 1];
        }
        supplyArchive[0] = this.getQuantity();
    }
}