package entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifier class for the {@link ResourceNodeEntity} entity.
 * This class defines a composite primary key used in the "resource_node" table.
 * The composite key consists of the `countryId` and `resourceId` fields.
 * <p>
 * Implements {@link Serializable} as required for composite key classes in JPA.
 */
public class ResourceNodeEntityId implements Serializable {

	/**
	 * The ID of the country associated with the resource node.
	 */
	private Long countryId;

	/**
	 * The ID of the resource associated with the resource node.
	 */
	private Long resourceId;

	/**
	 * Default constructor required by JPA.
	 */
	public ResourceNodeEntityId() {
	}

	/**
	 * Constructs a new ResourceNodeEntityId with the specified country and resource IDs.
	 *
	 * @param countryId  The ID of the country associated with the resource node.
	 * @param resourceId The ID of the resource associated with the resource node.
	 */
	public ResourceNodeEntityId(Long countryId, Long resourceId) {
		this.countryId = countryId;
		this.resourceId = resourceId;
	}

	/**
	 * Returns the country ID associated with this composite key.
	 *
	 * @return The country ID.
	 */
	public Long getCountryId() {
		return countryId;
	}

	/**
	 * Sets the country ID for this composite key.
	 *
	 * @param countryId The new country ID.
	 */
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	/**
	 * Returns the resource ID associated with this composite key.
	 *
	 * @return The resource ID.
	 */
	public Long getResourceId() {
		return resourceId;
	}

	/**
	 * Sets the resource ID for this composite key.
	 *
	 * @param resourceId The new resource ID.
	 */
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * Compares this ResourceNodeEntityId to another object for equality.
	 * Two ResourceNodeEntityId objects are considered equal if their `countryId`
	 * and `resourceId` fields are both equal.
	 *
	 * @param o The object to compare.
	 * @return True if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResourceNodeEntityId that = (ResourceNodeEntityId) o;
		return Objects.equals(countryId, that.countryId) && Objects.equals(resourceId, that.resourceId);
	}

	/**
	 * Computes the hash code for this ResourceNodeEntityId.
	 * The hash code is based on both the `countryId` and `resourceId` fields.
	 *
	 * @return The hash code of this composite key.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(countryId, resourceId);
	}
}