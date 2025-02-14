package dao;

import entity.ResourceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing operations on {@link ResourceEntity}.
 * Provides methods to persist, retrieve, delete, and query resources from the database
 * using JPA's {@link EntityManager}.
 */
public class ResourceDao {

	/**
	 * Logger for logging debug and error messages.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResourceDao.class);

	/**
	 * Persists a {@link ResourceEntity} in the database.
	 * If the entity already exists, its fields are updated.
	 *
	 * @param resourceEntity The {@link ResourceEntity} to be persisted or updated.
	 * @throws RuntimeException If an error occurs during the persistence operation.
	 */
	public void persist(ResourceEntity resourceEntity) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(resourceEntity);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error persisting resource: {}", resourceEntity.getName(), e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a {@link ResourceEntity} by its name.
	 *
	 * @param name The name of the resource to search for.
	 * @return The {@link ResourceEntity} with the specified name, or {@code null} if not found.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public ResourceEntity findByName(String name) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT r FROM ResourceEntity r WHERE r.name = :name", ResourceEntity.class)
			         .setParameter("name", name)
			         .getSingleResult();
		} catch (NoResultException e) {
			logger.debug("Resource not found: {}", name);
			return null;
		} catch (Exception e) {
			logger.error("Error finding resource by name: {}", name, e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Deletes a {@link ResourceEntity} from the database by its name.
	 * If no resource with the specified name exists, no action is taken.
	 *
	 * @param name The name of the resource to delete.
	 * @throws RuntimeException If an error occurs during the delete operation.
	 */
	public void deleteByName(String name) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			ResourceEntity resourceEntity = findByName(name);
			if (resourceEntity != null) {
				em.remove(em.contains(resourceEntity) ? resourceEntity : em.merge(resourceEntity));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error deleting resource by name: {}", name, e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all {@link ResourceEntity} records from the database.
	 *
	 * @return A list of all {@link ResourceEntity} instances.
	 * @throws RuntimeException If an error occurs during the query execution.
	 */
	public List<ResourceEntity> findAll() {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT r FROM ResourceEntity r", ResourceEntity.class).getResultList();
		} catch (Exception e) {
			logger.error("Error finding all resources", e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}