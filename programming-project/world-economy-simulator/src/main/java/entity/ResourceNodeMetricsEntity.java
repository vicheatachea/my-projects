package entity;

import jakarta.persistence.*;

/**
 * Entity class representing metrics of a resource node in a specific country on a given day.
 * This class is mapped to the "resource_node_metrics" table and contains details
 * such as maximum capacity, production cost, and tier of the resource node.
 */
@Entity
@Table(name = "resource_node_metrics")
public class ResourceNodeMetricsEntity {

	/**
	 * The unique identifier for the resource node metric.
	 * This is the primary key of the "resource_node_metrics" table and is auto-generated.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * The day for which the resource node metrics are recorded.
	 * This field cannot be null.
	 */
	@Column(name = "day", nullable = false)
	private int day;

	/**
	 * The associated {@link CountryEntity} for the resource node metrics.
	 * Defines a many-to-one relationship.
	 */
	@ManyToOne
	@JoinColumn(name = "country_id", nullable = false)
	private CountryEntity country;

	/**
	 * The associated {@link ResourceEntity} for the resource node metrics.
	 * Defines a many-to-one relationship.
	 */
	@ManyToOne
	@JoinColumn(name = "resource_id", nullable = false)
	private ResourceEntity resource;

	/**
	 * The maximum capacity of the resource node on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "max_capacity", nullable = false)
	private int maxCapacity;

	/**
	 * The cost of producing the resource node on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "production_cost", nullable = false)
	private double productionCost;

	/**
	 * The tier of the resource node on the given day, representing its level or efficiency.
	 * This field cannot be null.
	 */
	@Column(name = "tier", nullable = false)
	private int tier;

	/**
	 * Default constructor required by JPA.
	 */
	public ResourceNodeMetricsEntity() {
	}

	/**
	 * Constructs a new ResourceNodeMetricsEntity with the specified details.
	 *
	 * @param day            The day for which the metrics are recorded.
	 * @param country        The associated {@link CountryEntity}.
	 * @param resource       The associated {@link ResourceEntity}.
	 * @param maxCapacity    The maximum capacity of the resource node.
	 * @param productionCost The cost of producing the resource node.
	 * @param tier           The tier of the resource node.
	 */
	public ResourceNodeMetricsEntity(int day, CountryEntity country, ResourceEntity resource, int maxCapacity,
	                                 double productionCost, int tier) {
		this.day = day;
		this.country = country;
		this.resource = resource;
		this.maxCapacity = maxCapacity;
		this.productionCost = productionCost;
		this.tier = tier;
	}

	/**
	 * Returns the unique identifier of the resource node metric.
	 *
	 * @return The ID of this entity.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the resource node metric.
	 *
	 * @param id The new identifier.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the day for which the metrics are recorded.
	 *
	 * @return The day of the metrics.
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Sets the day for which the metrics are recorded.
	 *
	 * @param day The new day of the metrics.
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * Returns the associated {@link CountryEntity}.
	 *
	 * @return The country associated with the metrics.
	 */
	public CountryEntity getCountry() {
		return country;
	}

	/**
	 * Sets the associated {@link CountryEntity}.
	 *
	 * @param country The new country for the metrics.
	 */
	public void setCountry(CountryEntity country) {
		this.country = country;
	}

	/**
	 * Returns the associated {@link ResourceEntity}.
	 *
	 * @return The resource associated with the metrics.
	 */
	public ResourceEntity getResource() {
		return resource;
	}

	/**
	 * Sets the associated {@link ResourceEntity}.
	 *
	 * @param resource The new resource for the metrics.
	 */
	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

	/**
	 * Returns the maximum capacity of the resource node on the given day.
	 *
	 * @return The maximum capacity of the resource node.
	 */
	public int getMaxCapacity() {
		return maxCapacity;
	}

	/**
	 * Sets the maximum capacity of the resource node on the given day.
	 *
	 * @param maxCapacity The new maximum capacity.
	 */
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	/**
	 * Returns the production cost of the resource node on the given day.
	 *
	 * @return The production cost of the resource node.
	 */
	public double getProductionCost() {
		return productionCost;
	}

	/**
	 * Sets the production cost of the resource node on the given day.
	 *
	 * @param productionCost The new production cost.
	 */
	public void setProductionCost(double productionCost) {
		this.productionCost = productionCost;
	}

	/**
	 * Returns the tier of the resource node on the given day.
	 *
	 * @return The tier of the resource node.
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * Sets the tier of the resource node on the given day.
	 *
	 * @param tier The new tier.
	 */
	public void setTier(int tier) {
		this.tier = tier;
	}

	/**
	 * Returns a string representation of the ResourceNodeMetricsEntity.
	 * Includes the ID, day, country, resource, maximum capacity, production cost, and tier.
	 *
	 * @return The string representation of this entity.
	 */
	@Override
	public String toString() {
		return "ResourceNodeMetricsEntity {" +
		       "id=" + id +
		       ", day=" + day +
		       ", country=" + country +
		       ", resource=" + resource +
		       ", maxCapacity=" + maxCapacity +
		       ", productionCost=" + productionCost +
		       ", tier=" + tier +
		       '}';
	}
}