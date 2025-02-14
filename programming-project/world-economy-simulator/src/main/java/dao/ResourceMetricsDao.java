package dao;

import entity.CountryEntity;
import entity.ResourceEntity;
import entity.ResourceMetricsEntity;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing operations on {@link ResourceMetricsEntity}.
 * This class provides methods to persist, retrieve, and query resource metrics data
 * in the database using JPA's {@link EntityManager}.
 */
public class ResourceMetricsDao {

	/**
	 * Logger for logging debug and error messages.
	 */
	Logger logger = LoggerFactory.getLogger(ResourceMetricsDao.class);

	/**
	 * Persists a {@link ResourceMetricsEntity} object in the database.
	 * If the entity already exists, its fields are updated using the merge operation.
	 *
	 * @param resourceMetrics The {@link ResourceMetricsEntity} object to be persisted or updated.
	 * @throws RuntimeException If an error occurs during the transaction.
	 */
	public void persist(ResourceMetricsEntity resourceMetrics) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(resourceMetrics);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error persisting resource metrics", e);
			throw e;
		}
	}

	/**
	 * Finds a list of {@link ResourceMetricsEntity} records for a specific {@link CountryEntity}
	 * and {@link ResourceEntity}.
	 *
	 * @param country The {@link CountryEntity} for which metrics are to be retrieved.
	 * @param resource The {@link ResourceEntity} for which metrics are to be retrieved.
	 * @return A list of {@link ResourceMetricsEntity} records matching the specified country and resource.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceMetricsEntity> findByResource(CountryEntity country, ResourceEntity resource) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery(
					         "SELECT r FROM ResourceMetricsEntity r WHERE r.country.id = :countryId AND r.resource.id = :resourceId",
					         ResourceMetricsEntity.class)
			         .setParameter("countryId", country.getId())
			         .setParameter("resourceId", resource.getId())
			         .getResultList();
		} catch (Exception e) {
			logger.error("Error finding resource metrics by resource", e);
			throw e;
		}
	}

	/**
	 * Finds a {@link ResourceMetricsEntity} record for a specific {@link CountryEntity},
	 * {@link ResourceEntity}, and day.
	 *
	 * @param country The {@link CountryEntity} for which metrics are to be retrieved.
	 * @param resource The {@link ResourceEntity} for which metrics are to be retrieved.
	 * @param day The specific day for which the metrics are to be retrieved.
	 * @return A {@link ResourceMetricsEntity} matching the provided country, resource, and day.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public ResourceMetricsEntity findByResourceAndDay(CountryEntity country, ResourceEntity resource, int day) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery(
					         "SELECT r FROM ResourceMetricsEntity r WHERE r.country.id = :countryId AND r.resource.id = :resourceId AND r.day = :day",
					         ResourceMetricsEntity.class)
			         .setParameter("countryId", country.getId())
			         .setParameter("resourceId", resource.getId())
			         .setParameter("day", day)
			         .getSingleResult();
		} catch (Exception e) {
			logger.error("Error finding resource metrics by resource and day", e);
			throw e;
		}
	}

	/**
	 * Retrieves all {@link ResourceMetricsEntity} records from the database.
	 *
	 * @return A list of all {@link ResourceMetricsEntity} records.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceMetricsEntity> findAll() {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT r FROM ResourceMetricsEntity r", ResourceMetricsEntity.class).getResultList();
		} catch (Exception e) {
			logger.error("Error finding all resource metrics", e);
			throw e;
		}
	}
}