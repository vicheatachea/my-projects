package dao;

import entity.CountryEntity;
import entity.CountryMetricsEntity;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing operations on {@link CountryMetricsEntity}.
 * This class provides methods to persist, retrieve, and query country metrics data
 * in the database using JPA's {@link EntityManager}.
 */
public class CountryMetricsDao {

	/**
	 * Logger for logging debug and error messages.
	 */
	Logger logger = LoggerFactory.getLogger(CountryMetricsDao.class);

	/**
	 * Persists a {@link CountryMetricsEntity} instance into the database.
	 *
	 * @param countryMetrics The {@link CountryMetricsEntity} to be persisted.
	 * @throws RuntimeException If an error occurs during the persistence operation.
	 */
	public void persist(CountryMetricsEntity countryMetrics) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();

		em.getTransaction().begin();
		try {
			em.persist(countryMetrics);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error persisting country metrics", e);
			throw e;
		}
	}

	/**
	 * Finds all {@link CountryMetricsEntity} instances associated with a specific {@link CountryEntity}.
	 *
	 * @param country The {@link CountryEntity} for which metrics will be retrieved.
	 * @return A list of {@link CountryMetricsEntity} objects associated with the given country.
	 * @throws RuntimeException If an error occurs during the query operation.
	 */
	public List<CountryMetricsEntity> findByCountry(CountryEntity country) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();

		try {
			return em.createQuery("SELECT c FROM CountryMetricsEntity c WHERE c.country.id = :countryId",
			                      CountryMetricsEntity.class)
			         .setParameter("countryId", country.getId())
			         .getResultList();
		} catch (Exception e) {
			logger.error("Error finding country metrics by country", e);
			throw e;
		}
	}

	/**
	 * Finds a {@link CountryMetricsEntity} instance for a specific {@link CountryEntity} and day.
	 *
	 * @param country The {@link CountryEntity} for which metrics will be retrieved.
	 * @param day     The day for which the metrics will be retrieved.
	 * @return A {@link CountryMetricsEntity} object matching the specified country and day.
	 * @throws RuntimeException If an error occurs during the query operation.
	 */
	public CountryMetricsEntity findByCountryAndDay(CountryEntity country, int day) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();

		try {
			return em.createQuery(
					         "SELECT c FROM CountryMetricsEntity c WHERE c.country.id = :countryId AND c.day = :day",
					         CountryMetricsEntity.class)
			         .setParameter("countryId", country.getId())
			         .setParameter("day", day)
			         .getSingleResult();
		} catch (Exception e) {
			logger.error("Error finding country metrics by country and day", e);
			throw e;
		}
	}

	/**
	 * Retrieves all {@link CountryMetricsEntity} instances from the database.
	 *
	 * @return A list of all {@link CountryMetricsEntity} objects.
	 * @throws RuntimeException If an error occurs during the query operation.
	 */
	public List<CountryMetricsEntity> findAll() {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT c FROM CountryMetricsEntity c", CountryMetricsEntity.class)
			         .getResultList();
		} catch (Exception e) {
			logger.error("Error finding all country metrics", e);
			throw e;
		}
	}
}