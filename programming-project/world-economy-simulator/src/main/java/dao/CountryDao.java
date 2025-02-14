package dao;

import entity.CountryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Data Access Object (DAO) class for managing operations on {@link CountryEntity}.
 * Provides methods to persist, retrieve, and delete entities in the database
 * using JPA's {@link EntityManager}.
 */
public class CountryDao {

	/**
	 * Logger for logging debug and error messages.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CountryDao.class);

	/**
	 * Persists a {@link CountryEntity} in the database.
	 * If the country already exists (based on its name), updates its properties.
	 * If it does not exist, inserts it as a new record.
	 *
	 * @param country The {@link CountryEntity} to be persisted or updated.
	 */
	public void persist(CountryEntity country) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			CountryEntity existingCountry = findByName(country.getName());
			if (existingCountry != null) {
				// Update the existing country's fields
				existingCountry.setMoney(country.getMoney());
				existingCountry.setPopulation(country.getPopulation());
				em.merge(existingCountry);
			} else {
				em.persist(country); // Insert new country
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error persisting country: {}", country.getName(), e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a {@link CountryEntity} by its name.
	 *
	 * @param name The name of the country to search for.
	 * @return The {@link CountryEntity} with the given name, or {@code null} if not found.
	 */
	public CountryEntity findByName(String name) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT c FROM CountryEntity c WHERE c.name = :name", CountryEntity.class)
			         .setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			logger.debug("Country not found: {}", name);
			return null;
		} catch (Exception e) {
			logger.error("Error finding country by name: {}", name, e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all {@link CountryEntity} records from the database.
	 *
	 * @return A list of all {@link CountryEntity} instances.
	 */
	public List<CountryEntity> findAll() {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		try {
			return em.createQuery("SELECT c FROM CountryEntity c", CountryEntity.class).getResultList();
		} catch (Exception e) {
			logger.error("Error finding all countries", e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Deletes a {@link CountryEntity} from the database by its name.
	 * If no country with the specified name exists, no action is taken.
	 *
	 * @param name The name of the country to delete.
	 */
	public void deleteByName(String name) {
		EntityManager em = datasource.MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			CountryEntity countryEntity = findByName(name);
			if (countryEntity != null) {
				em.remove(em.contains(countryEntity) ? countryEntity : em.merge(countryEntity));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.error("Error deleting country by name: {}", name, e);
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}