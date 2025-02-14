package entity;

import jakarta.persistence.*;

/**
 * Entity class representing a resource node within a country.
 * A resource node establishes a relationship between a {@link CountryEntity} and a {@link ResourceEntity},
 * defining details about the quantity, tier, base capacity, and base production cost for the resource in the country.
 * This entity is mapped to the "resource_node" table and uses a composite key defined by {@link ResourceNodeEntityId}.
 */
@Entity
@Table(name = "resource_node")
@IdClass(ResourceNodeEntityId.class)
public class ResourceNodeEntity {

	/**
	 * The ID of the country where the resource node exists.
	 * Part of the composite key for this entity.
	 */
	@Id
	@Column(name = "country_id", nullable = false)
	private Long countryId;

	/**
	 * The ID of the resource associated with this resource node.
	 * Part of the composite key for this entity.
	 */
	@Id
	@Column(name = "resource_id", nullable = false)
	private Long resourceId;

	/**
	 * The associated {@link CountryEntity}.
	 * Defines a many-to-one relationship with the "country_id" column as the foreign key.
	 */
	@ManyToOne
	@JoinColumn(name = "country_id", insertable = false, updatable = false, nullable = false)
	private CountryEntity country;

	/**
	 * The associated {@link ResourceEntity}.
	 * Defines a many-to-one relationship with the "resource_id" column as the foreign key.
	 */
	@ManyToOne
	@JoinColumn(name = "resource_id", insertable = false, updatable = false, nullable = false)
	private ResourceEntity resource;

	/**
	 * The quantity of the resource represented by the node.
	 * This field cannot be null.
	 */
	@Column(name = "quantity", nullable = false)
	private int quantity;

	/**
	 * The tier of the resource node, representing its level or efficiency.
	 * This field cannot be null.
	 */
	@Column(name = "tier", nullable = false)
	private int tier;

	/**
	 * The base capacity of the resource node, representing the production or storage capacity.
	 * This field cannot be null.
	 */
	@Column(name = "base_capacity", nullable = false)
	private int baseCapacity;

	/**
	 * The base production cost associated with the resource node.
	 * This field cannot be null.
	 */
	@Column(name = "base_production_cost", nullable = false)
	private double baseProductionCost;

	/**
	 * Default constructor required by JPA.
	 */
	public ResourceNodeEntity() {
	}

	/**
	 * Constructs a new ResourceNodeEntity with the provided details.
	 * Initializes the composite key fields from the provided {@link CountryEntity} and {@link ResourceEntity}.
	 *
	 * @param country            The associated {@link CountryEntity}.
	 * @param resource           The associated {@link ResourceEntity}.
	 * @param quantity           The quantity of the resource.
	 * @param tier               The tier of the resource node.
	 * @param baseCapacity       The base capacity of the resource node.
	 * @param baseProductionCost The base production cost of the resource node.
	 */
	public ResourceNodeEntity(CountryEntity country, ResourceEntity resource, int quantity, int tier, int baseCapacity,
	                          double baseProductionCost) {
		this.country = country;
		this.resource = resource;
		if (country != null) {
			this.countryId = country.getId();
		}
		if (resource != null) {
			this.resourceId = resource.getId();
		}
		this.quantity = quantity;
		this.baseCapacity = baseCapacity;
		this.tier = tier;
		this.baseProductionCost = baseProductionCost;
	}

	/**
	 * Returns the country ID where the resource node exists.
	 *
	 * @return The ID of the associated country.
	 */
	public Long getCountryId() {
		return countryId;
	}

	/**
	 * Sets the country ID where the resource node exists.
	 *
	 * @param countryId The new country ID.
	 */
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	/**
	 * Returns the resource ID associated with this resource node.
	 *
	 * @return The ID of the associated resource.
	 */
	public Long getResourceId() {
		return resourceId;
	}

	/**
	 * Sets the resource ID associated with this resource node.
	 *
	 * @param resourceId The new resource ID.
	 */
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * Returns the associated {@link CountryEntity}.
	 *
	 * @return The country associated with the resource node.
	 */
	public CountryEntity getCountry() {
		return country;
	}

	/**
	 * Sets the associated {@link CountryEntity}.
	 *
	 * @param country The new country associated with the resource node.
	 */
	public void setCountry(CountryEntity country) {
		this.country = country;
	}

	/**
	 * Returns the associated {@link ResourceEntity}.
	 *
	 * @return The resource associated with the resource node.
	 */
	public ResourceEntity getResource() {
		return resource;
	}

	/**
	 * Sets the associated {@link ResourceEntity}.
	 *
	 * @param resource The new resource associated with the resource node.
	 */
	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

	/**
	 * Returns the quantity of the resource represented by this node.
	 *
	 * @return The resource quantity.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity of the resource represented by this node.
	 *
	 * @param quantity The new resource quantity.
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Returns the tier of the resource node.
	 *
	 * @return The tier of the resource node.
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * Sets the tier of the resource node.
	 *
	 * @param tier The new tier for the resource node.
	 */
	public void setTier(int tier) {
		this.tier = tier;
	}

	/**
	 * Returns the base capacity of the resource node.
	 *
	 * @return The base capacity.
	 */
	public int getBaseCapacity() {
		return baseCapacity;
	}

	/**
	 * Sets the base capacity of the resource node.
	 *
	 * @param baseCapacity The new base capacity.
	 */
	public void setBaseCapacity(int baseCapacity) {
		this.baseCapacity = baseCapacity;
	}

	/**
	 * Returns the base production cost of the resource node.
	 *
	 * @return The base production cost.
	 */
	public double getBaseProductionCost() {
		return baseProductionCost;
	}

	/**
	 * Sets the base production cost of the resource node.
	 *
	 * @param baseProductionCost The new base production cost.
	 */
	public void setBaseProductionCost(double baseProductionCost) {
		this.baseProductionCost = baseProductionCost;
	}

	/**
	 * Returns a string representation of the ResourceNodeEntity.
	 * Includes the country ID, resource ID, quantity, tier, base capacity, and base production cost.
	 *
	 * @return A string representation of the entity.
	 */
	@Override
	public String toString() {
		return "ResourceNodeEntity{" + "countryId=" + countryId + ", resourceId=" + resourceId + ", quantity=" +
		       quantity + ", tier=" + tier + ", baseCapacity=" + baseCapacity + ", baseProductionCost=" +
		       baseProductionCost + '}';
	}
}