package entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a Country in the database.
 * This class is mapped to the table "country" and contains details about a country's
 * name, money, population, and associated resource nodes.
 */
@Entity
@Table(name = "country")
public class CountryEntity {

	/**
	 * Primary key of the "country" table.
	 * The ID is auto-generated using the IDENTITY strategy.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * Name of the country. This field is unique and cannot be null.
	 */
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	/**
	 * The amount of money available to the country.
	 * This field cannot be null.
	 */
	@Column(name = "money", nullable = false)
	private double money;

	/**
	 * The population of the country.
	 * This field cannot be null.
	 */
	@Column(name = "population", nullable = false)
	private Long population;

	/**
	 * A collection of resource nodes associated with this country.
	 * Defines a one-to-many relationship with {@link ResourceNodeEntity}.
	 * The "country" field in {@link ResourceNodeEntity} is the mapped column.
	 * Any changes to this collection will cascade to the related entities.
	 */
	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ResourceNodeEntity> resourceNodes;

	/**
	 * Default constructor required by JPA.
	 */
	public CountryEntity() {
	}

	/**
	 * Constructs a new CountryEntity with the given details.
	 *
	 * @param name       The name of the country.
	 * @param money      The amount of money the country has.
	 * @param population The population of the country.
	 */
	public CountryEntity(String name, double money, long population) {
		this.name = name;
		this.money = money;
		this.population = population;
		this.resourceNodes = new HashSet<>();
	}

	/**
	 * Returns the ID of the country.
	 *
	 * @return The country's ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of the country.
	 *
	 * @param id The new ID for the country.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the country.
	 *
	 * @return The country's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the country.
	 *
	 * @param name The new name for the country.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the amount of money the country has.
	 *
	 * @return The country's money.
	 */
	public double getMoney() {
		return money;
	}

	/**
	 * Sets the amount of money the country has.
	 *
	 * @param money The new amount of money for the country.
	 */
	public void setMoney(double money) {
		this.money = money;
	}

	/**
	 * Returns the population of the country.
	 *
	 * @return The country's population.
	 */
	public long getPopulation() {
		return population;
	}

	/**
	 * Sets the population of the country.
	 *
	 * @param population The new population for the country.
	 */
	public void setPopulation(long population) {
		this.population = population;
	}

	/**
	 * Adds a resource node to the country.
	 * Updates the bidirectional relationship by setting the country field
	 * on the given {@link ResourceNodeEntity}.
	 *
	 * @param resourceNode The resource node to add.
	 */
	public void addResourceNode(ResourceNodeEntity resourceNode) {
		resourceNodes.add(resourceNode);
		resourceNode.setCountry(this);
	}

	/**
	 * Returns the collection of resource nodes associated with this country.
	 *
	 * @return A set of {@link ResourceNodeEntity} objects representing the resource nodes.
	 */
	public Set<ResourceNodeEntity> getResourceNodes() {
		return resourceNodes;
	}

	/**
	 * Returns a string representation of the CountryEntity.
	 * Includes country ID, name, money, and population.
	 *
	 * @return A string representation of the country.
	 */
	@Override
	public String toString() {
		return "CountryEntity [" + "id=" + id + ", name='" + name + '\'' + ", money=" + money + ", population=" +
		       population + ']';
	}

	/**
	 * Compares this CountryEntity to another object for equality.
	 * Two CountryEntity objects are considered equal if they have the same name.
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
		CountryEntity country = (CountryEntity) obj;
		return name != null && name.equals(country.name);
	}

	/**
	 * Calculates the hash code for this CountryEntity.
	 * The hash code is based on the country's name.
	 *
	 * @return The hash code of the country.
	 */
	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}