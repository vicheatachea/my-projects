package entity;

import jakarta.persistence.*;

/**
 * Entity class representing the metrics of a resource within a specific country on a given day.
 * This class is mapped to the "resource_metrics" table and contains details about
 * the resource quantity, value, and related entities (Country and Resource).
 */
@Entity
@Table(name = "resource_metrics")
public class ResourceMetricsEntity {

	/**
	 * The unique identifier for the Resource metrics.
	 * This is the primary key of the "resource_metrics" table.
	 * It is auto-generated using the IDENTITY strategy.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * The day for which the resource metrics are recorded.
	 * This field cannot be null.
	 */
	@Column(name = "day", nullable = false)
	private int day;

	/**
	 * The associated {@link CountryEntity} for which the resource metrics are tracked.
	 * Defines a many-to-one relationship with a cascading effect on all operations.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "country_id", nullable = false)
	private CountryEntity country;

	/**
	 * The associated {@link ResourceEntity} representing the resource being tracked.
	 * Defines a many-to-one relationship with a cascading effect on all operations.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "resource_id", nullable = false)
	private ResourceEntity resource;

	/**
	 * The quantity of the resource available or produced on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "quantity", nullable = false)
	private int quantity;

	/**
	 * The total value associated with the resource on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "value", nullable = false)
	private double value;

	/**
	 * Default constructor required by JPA.
	 */
	public ResourceMetricsEntity() {
	}

	/**
	 * Constructs a new ResourceMetricsEntity with the provided details.
	 *
	 * @param day      The day for which the metrics are tracked.
	 * @param country  The associated {@link CountryEntity}.
	 * @param resource The associated {@link ResourceEntity}.
	 * @param quantity The quantity of the resource.
	 * @param value    The total value of the resource.
	 */
	public ResourceMetricsEntity(int day, CountryEntity country, ResourceEntity resource, int quantity, double value) {
		this.day = day;
		this.country = country;
		this.resource = resource;
		this.quantity = quantity;
		this.value = value;
	}

	/**
	 * Returns the unique identifier of this entity.
	 *
	 * @return The ID of this entity.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this entity.
	 *
	 * @param id The new ID of the entity.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the day for which the resource metrics are recorded.
	 *
	 * @return The day of the metrics.
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Sets the day for which the resource metrics are recorded.
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
	 * Returns the quantity of the resource available or produced on the given day.
	 *
	 * @return The resource quantity.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity of the resource available or produced on the given day.
	 *
	 * @param quantity The new quantity of the resource.
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Returns the total value of the resource on the given day.
	 *
	 * @return The value of the resource.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the total value of the resource on the given day.
	 *
	 * @param value The new value of the resource.
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Returns a string representation of the ResourceMetricsEntity.
	 * Includes the ID, day, resource, country, quantity, and value.
	 *
	 * @return A string representation of the entity.
	 */
	@Override
	public String toString() {
		return "ResourceMetricsEntity {" +
		       "id=" + id +
		       ", day=" + day +
		       ", resource=" + resource +
		       ", country=" + country +
		       ", quantity=" + quantity +
		       ", value=" + value +
		       '}';
	}
}