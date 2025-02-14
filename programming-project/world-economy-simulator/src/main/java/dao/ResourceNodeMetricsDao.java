package dao;

import entity.CountryEntity;
import entity.ResourceEntity;
import entity.ResourceNodeMetricsEntity;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing operations on {@link ResourceNodeMetricsEntity}.
 * Provides methods to persist, retrieve, and query resource node metrics in the database
 * using JPA's {@link EntityManager}.
 */
public class ResourceNodeMetricsDao {

	/**
	 * Logger for logging debug and error messages.
	 */
	Logger logger = LoggerFactory.getLogger(ResourceNodeMetricsDao.class);

	/**
	 * Persists a {@link ResourceNodeMetricsEntity} object in the database.
	 *
	 * @param resourceNodeMetrics The {@link ResourceNodeMetricsEntity} to be persisted.
	 * @throws RuntimeException If an error occurs during the persistence operation.
	 */
	public void persist(ResourceNodeMetricsEntity resourceNodeMetrics) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(resourceNodeMetrics);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error persisting resource node metrics", e);
			throw e;
		}
	}

	/**
	 * Finds a list of {@link ResourceNodeMetricsEntity} records for a specific {@link CountryEntity}
	 * and {@link ResourceEntity}.
	 *
	 * @param countryEntity  The {@link CountryEntity} identifying the resource node location.
	 * @param resourceEntity The {@link ResourceEntity} identifying the resource.
	 * @return A list of {@link ResourceNodeMetricsEntity} objects for the specified country and resource.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceNodeMetricsEntity> findByResourceNode(CountryEntity countryEntity,
	                                                          ResourceEntity resourceEntity) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery(
					         "SELECT r FROM ResourceNodeMetricsEntity r WHERE r.country.id = :countryId AND r.resource.id = :resourceId",
					         ResourceNodeMetricsEntity.class)
			         .setParameter("countryId", countryEntity.getId())
			         .setParameter("resourceId", resourceEntity.getId())
			         .getResultList();
		} catch (Exception e) {
			logger.error("Error finding resource node metrics by resource node", e);
			throw e;
		}
	}

	/**
	 * Finds a {@link ResourceNodeMetricsEntity} record for a specific {@link CountryEntity},
	 * {@link ResourceEntity}, and day.
	 *
	 * @param countryEntity  The {@link CountryEntity} identifying the resource node location.
	 * @param resourceEntity The {@link ResourceEntity} identifying the resource.
	 * @param day            The specific day for which the metrics need to be retrieved.
	 * @return A {@link ResourceNodeMetricsEntity} object for the specified country, resource, and day.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public ResourceNodeMetricsEntity findByResourceNodeAndDay(CountryEntity countryEntity,
	                                                          ResourceEntity resourceEntity, int day) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery(
					         "SELECT r FROM ResourceNodeMetricsEntity r WHERE r.country.id = :country AND r.resource.id = :resource AND r.day = :day",
					         ResourceNodeMetricsEntity.class)
			         .setParameter("country", countryEntity.getId())
			         .setParameter("resource", resourceEntity.getId())
			         .setParameter("day", day)
			         .getSingleResult();
		} catch (Exception e) {
			logger.error("Error finding resource node metrics by resource node and day", e);
			throw e;
		}
	}

	/**
	 * Retrieves all {@link ResourceNodeMetricsEntity} records from the database.
	 *
	 * @return A list of all {@link ResourceNodeMetricsEntity} objects.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceNodeMetricsEntity> findAll() {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT r FROM ResourceNodeMetricsEntity r", ResourceNodeMetricsEntity.class)
			         .getResultList();
		} catch (Exception e) {
			logger.error("Error finding all resource node metrics", e);
			throw e;
		}
	}
}