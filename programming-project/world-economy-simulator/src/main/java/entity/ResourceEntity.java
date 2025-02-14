package entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a Resource within the system.
 * This class is mapped to the "resource" table and contains details about
 * a resource's priority, base capacity, production cost, and its associated resource nodes.
 */
@Entity
@Table(name = "resource")
public class ResourceEntity {

	/**
	 * The unique identifier for the Resource.
	 * This is the primary key of the "resource" table.
	 * It is auto-generated using the IDENTITY strategy.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * The name of the resource. This field is unique and cannot be null.
	 */
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	/**
	 * The priority level of the resource, used for determining its importance.
	 * This field cannot be null.
	 */
	@Column(name = "priority", nullable = false)
	private double priority;

	/**
	 * The base production capacity of the resource.
	 * This field cannot be null.
	 */
	@Column(name = "base_capacity", nullable = false)
	private int baseCapacity;

	/**
	 * The cost of producing the resource.
	 * This field cannot be null.
	 */
	@Column(name = "production_cost", nullable = false)
	private double productionCost;

	/**
	 * A collection of resource nodes associated with this resource.
	 * Defines a one-to-many relationship with {@link ResourceNodeEntity}.
	 * The "resource" field in {@link ResourceNodeEntity} is the mapped column.
	 * Changes to this collection will cascade to the related entities.
	 */
	@OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ResourceNodeEntity> resourceNodeEntities;

	/**
	 * Default constructor required by JPA.
	 */
	public ResourceEntity() {
	}

	/**
	 * Constructs a new ResourceEntity with the provided details.
	 *
	 * @param name           The name of the resource.
	 * @param priority       The priority of the resource.
	 * @param baseCapacity   The base production capacity of the resource.
	 * @param productionCost The cost of producing the resource.
	 */
	public ResourceEntity(String name, double priority, int baseCapacity, double productionCost) {
		this.name = name;
		this.priority = priority;
		this.baseCapacity = baseCapacity;
		this.productionCost = productionCost;
		this.resourceNodeEntities = new HashSet<>();
	}

	/**
	 * Returns the ID of the resource.
	 *
	 * @return The resource's ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of the resource.
	 *
	 * @param id The new ID for the resource.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the resource.
	 *
	 * @return The resource's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the resource.
	 *
	 * @param name The new name for the resource.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the priority of the resource.
	 *
	 * @return The priority of the resource.
	 */
	public double getPriority() {
		return priority;
	}

	/**
	 * Sets the priority level of the resource.
	 *
	 * @param priority The new priority level for the resource.
	 */
	public void setPriority(double priority) {
		this.priority = priority;
	}

	/**
	 * Returns the base production capacity of the resource.
	 *
	 * @return The base capacity of the resource.
	 */
	public int getBaseCapacity() {
		return baseCapacity;
	}

	/**
	 * Sets the base production capacity of the resource.
	 *
	 * @param baseCapacity The new base capacity of the resource.
	 */
	public void setBaseCapacity(int baseCapacity) {
		this.baseCapacity = baseCapacity;
	}

	/**
	 * Returns the production cost of the resource.
	 *
	 * @return The production cost of the resource.
	 */
	public double getProductionCost() {
		return productionCost;
	}

	/**
	 * Sets the production cost of the resource.
	 *
	 * @param productionCost The new production cost of the resource.
	 */
	public void setProductionCost(double productionCost) {
		this.productionCost = productionCost;
	}

	/**
	 * Returns the collection of associated resource nodes.
	 *
	 * @return A set of {@link ResourceNodeEntity} associated with the resource.
	 */
	public Set<ResourceNodeEntity> getResourceNodeEntities() {
		return resourceNodeEntities;
	}

	/**
	 * Sets the collection of resource nodes associated with this resource.
	 *
	 * @param resourceNodeEntities The new collection of resource nodes.
	 */
	public void setResourceNodeEntities(Set<ResourceNodeEntity> resourceNodeEntities) {
		this.resourceNodeEntities = resourceNodeEntities;
	}

	/**
	 * Returns a string representation of the ResourceEntity.
	 * Includes the ID, name, priority, base capacity, and production cost.
	 *
	 * @return A string representing the resource entity.
	 */
	@Override
	public String toString() {
		return "ResourceEntity [" + "id=" + id + ", name='" + name + '\'' + ", priority=" + priority +
		       ", baseCapacity=" + baseCapacity + ", productionCost=" + productionCost + ']';
	}

	/**
	 * Compares this ResourceEntity to another object for equality.
	 * Two ResourceEntity objects are considered equal if they have the same name.
	 *
	 * @param obj The object to compare.
	 * @return True if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ResourceEntity that = (ResourceEntity) obj;
		return name != null && name.equals(that.name);
	}

	/**
	 * Calculates the hash code for this ResourceEntity.
	 * The hash code is based on the resource's name.
	 *
	 * @return The hash code of the resource.
	 */
	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}