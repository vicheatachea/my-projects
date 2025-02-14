package entity;

import jakarta.persistence.*;

/**
 * Entity class representing the metrics of a Country for a given day.
 * This class is mapped to the "country_metrics" table and contains details
 * about a country's population, money, happiness, and individual budget
 * for a specific day.
 */
@Entity
@Table(name = "country_metrics")
public class CountryMetricsEntity {

	/**
	 * The unique identifier for the Country metrics.
	 * This is the primary key of the "country_metrics" table.
	 * It is auto-generated using the IDENTITY strategy.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * The day for which the metrics are recorded.
	 * This field cannot be null.
	 */
	@Column(name = "day", nullable = false)
	private int day;

	/**
	 * The associated {@link CountryEntity} for which the metrics are tracked.
	 * Defines a many-to-one relationship.
	 */
	@ManyToOne
	@JoinColumn(name = "country_id", nullable = false)
	private CountryEntity country;

	/**
	 * The population of the country on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "population", nullable = false)
	private long population;

	/**
	 * The total amount of money the country has on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "money", nullable = false)
	private double money;

	/**
	 * The average happiness of the country's population on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "average_happiness", nullable = false)
	private double averageHappiness;

	/**
	 * The budget allocated to individuals on the given day.
	 * This field cannot be null.
	 */
	@Column(name = "individual_budget", nullable = false)
	private double individualBudget;

	/**
	 * Default constructor required by JPA.
	 */
	public CountryMetricsEntity() {
	}

	/**
	 * Constructs a new CountryMetricsEntity with the provided values.
	 *
	 * @param day              The day for which the metrics are tracked.
	 * @param country          The associated CountryEntity.
	 * @param population       The population of the country.
	 * @param money            The total money of the country.
	 * @param averageHappiness The average happiness of the country's population.
	 * @param individualBudget The budget allocated to individuals.
	 */
	public CountryMetricsEntity(int day, CountryEntity country, long population, double money,
	                            double averageHappiness, double individualBudget) {
		this.day = day;
		this.country = country;
		this.population = population;
		this.money = money;
		this.averageHappiness = averageHappiness;
		this.individualBudget = individualBudget;
	}

	/**
	 * Returns the ID of this entity.
	 *
	 * @return The entity's ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of this entity.
	 *
	 * @param id The new ID of the entity.
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
	 * @param country The new country associated with the metrics.
	 */
	public void setCountry(CountryEntity country) {
		this.country = country;
	}

	/**
	 * Returns the population of the country on the given day.
	 *
	 * @return The population of the country.
	 */
	public long getPopulation() {
		return population;
	}

	/**
	 * Sets the population of the country on the given day.
	 *
	 * @param population The new population value.
	 */
	public void setPopulation(long population) {
		this.population = population;
	}

	/**
	 * Returns the total amount of money the country has on the given day.
	 *
	 * @return The country's total money.
	 */
	public double getMoney() {
		return money;
	}

	/**
	 * Sets the total amount of money the country has on the given day.
	 *
	 * @param money The new money value.
	 */
	public void setMoney(double money) {
		this.money = money;
	}

	/**
	 * Returns the average happiness of the country's population.
	 *
	 * @return The average happiness of the population.
	 */
	public double getAverageHappiness() {
		return averageHappiness;
	}

	/**
	 * Sets the average happiness of the country's population.
	 *
	 * @param averageHappiness The new average happiness value.
	 */
	public void setAverageHappiness(double averageHappiness) {
		this.averageHappiness = averageHappiness;
	}

	/**
	 * Returns the budget allocated to individuals on the given day.
	 *
	 * @return The individual budget.
	 */
	public double getIndividualBudget() {
		return individualBudget;
	}

	/**
	 * Sets the budget allocated to individuals on the given day.
	 *
	 * @param individualBudget The new individual budget value.
	 */
	public void setIndividualBudget(double individualBudget) {
		this.individualBudget = individualBudget;
	}

	/**
	 * Returns a string representation of the CountryMetricsEntity.
	 * Includes the ID, associated country, population, money, average happiness,
	 * and individual budget.
	 *
	 * @return A string representing the entity.
	 */
	@Override
	public String toString() {
		return "CountryMetricsEntity {" +
		       "id=" + id +
		       ", country=" + country +
		       ", population=" + population +
		       ", money=" + money +
		       ", averageHappiness=" + averageHappiness +
		       ", individualBudget=" + individualBudget +
		       '}';
	}
}