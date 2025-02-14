package dao;

import datasource.MariaDbConnection;
import entity.CountryEntity;
import entity.ResourceNodeEntity;
import entity.ResourceNodeEntityId;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing operations on {@link ResourceNodeEntity}.
 * Provides methods to persist, retrieve, and delete resource nodes in the database
 * using JPA's {@link EntityManager}.
 */
public class ResourceNodeDao {

	/**
	 * Logger for logging debug and error messages.
	 */
	Logger logger = LoggerFactory.getLogger(ResourceNodeDao.class);

	/**
	 * Persists a {@link ResourceNodeEntity} object in the database.
	 * If the entity already exists, it updates its fields; otherwise, it inserts a new record.
	 *
	 * @param resourceNode The {@link ResourceNodeEntity} to be persisted or updated.
	 * @throws RuntimeException If an error occurs during the transaction.
	 */
	public void persist(ResourceNodeEntity resourceNode) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(resourceNode);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error persisting resource node", e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all {@link ResourceNodeEntity} instances associated with a specific {@link CountryEntity}.
	 *
	 * @param country The {@link CountryEntity} for which resource nodes will be retrieved.
	 * @return A list of {@link ResourceNodeEntity} objects matching the specified country.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceNodeEntity> findByCountry(CountryEntity country) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery(
					"SELECT r FROM ResourceNodeEntity r WHERE r.country.id = :countryId",
					ResourceNodeEntity.class
			).setParameter("countryId", country.getId()).getResultList();
		} catch (Exception e) {
			logger.error("Error finding resource nodes by country", e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all {@link ResourceNodeEntity} records from the database.
	 *
	 * @return A list of all {@link ResourceNodeEntity} objects.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceNodeEntity> findAll() {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT r FROM ResourceNodeEntity r", ResourceNodeEntity.class).getResultList();
		} catch (Exception e) {
			logger.error("Error finding all resource nodes", e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Deletes a {@link ResourceNodeEntity} based on the specified country and resource IDs.
	 * If a matching entity exists, it is removed from the database.
	 *
	 * @param countryId  The ID of the {@link CountryEntity} to which the resource node belongs.
	 * @param resourceId The ID of the resource associated with the resource node.
	 * @throws RuntimeException If an error occurs during the delete operation.
	 */
	public void delete(Long countryId, Long resourceId) {
		EntityManager em = MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			ResourceNodeEntityId id = new ResourceNodeEntityId();
			id.setCountryId(countryId);
			id.setResourceId(resourceId);
			ResourceNodeEntity resourceNodeEntity = em.find(ResourceNodeEntity.class, id);
			if (resourceNodeEntity != null) {
				em.remove(em.contains(resourceNodeEntity) ? resourceNodeEntity : em.merge(resourceNodeEntity));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error deleting resource node by country and resource", e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}