package dao;

import datasource.MariaDbConnection;
import entity.CountryEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CountryDaoTest {

	@BeforeAll
	public static void setUpDatabase() throws SQLException {
		MariaDbConnection.resetDatabaseForTests();
	}

	@AfterAll
	public static void tearDownDatabase() throws SQLException {
		Connection conn = MariaDbConnection.getConnection();
		conn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS `simulation`");
	}

	@BeforeEach
	public void setUp() {
		EntityManager em = MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM CountryEntity").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void testPersist() {
		CountryDao countryDao = new CountryDao();

		CountryEntity country = new CountryEntity("TestCountry", 100_000_000.0, 1_000_000);
		countryDao.persist(country);

		CountryEntity foundCountry = countryDao.findByName("TestCountry");
		assertEquals("TestCountry", foundCountry.getName());
		assertEquals(100_000_000.0, foundCountry.getMoney());
		assertEquals(1_000_000, foundCountry.getPopulation());
	}

	@Test
	public void testFindByName() {
		CountryDao countryDao = new CountryDao();

		CountryEntity country = new CountryEntity("TestCountry", 100_000_000.0, 1_000_000);
		countryDao.persist(country);

		CountryEntity foundCountry = countryDao.findByName("TestCountry");
		assertEquals("TestCountry", foundCountry.getName());
		assertEquals(100_000_000.0, foundCountry.getMoney());
		assertEquals(1_000_000, foundCountry.getPopulation());
	}

	@Test
	public void testFindAll() {
		CountryDao countryDao = new CountryDao();

		CountryEntity country1 = new CountryEntity("TestCountry1", 100_000_000.0, 1_000_000);
		CountryEntity country2 = new CountryEntity("TestCountry2", 200_000_000.0, 2_000_000);
		CountryEntity country3 = new CountryEntity("TestCountry3", 300_000_000.0, 3_000_000);

		countryDao.persist(country1);
		countryDao.persist(country2);
		countryDao.persist(country3);

		assertEquals(3, countryDao.findAll().size());
	}

	@Test
	public void testDeleteByName() {
		CountryDao countryDao = new CountryDao();

		CountryEntity country = new CountryEntity("TestCountry", 100_000_000.0, 1_000_000);
		countryDao.persist(country);

		countryDao.deleteByName("TestCountry");
		assertNull(countryDao.findByName("TestCountry"));
	}

	@Test
	public void testFindByNameNoResult() {
		CountryDao countryDao = new CountryDao();
		assertNull(countryDao.findByName("TestCountry"));
	}

	@Test
	public void testFindByNameException() {
		CountryDao countryDao = new CountryDao();
		try {
			countryDao.findByName("TestCountry1");
		} catch (Exception e) {
			assertEquals("Error finding country by name", e.getMessage());
		}
	}

	@Test
	public void testFindAllException() {
		CountryDao countryDao = new CountryDao();
		try {
			countryDao.findAll();
		} catch (Exception e) {
			assertEquals("Error finding all countries", e.getMessage());
		}
	}

	@Test
	public void testDeleteByNameException() {
		CountryDao countryDao = new CountryDao();
		try {
			countryDao.deleteByName("TestCountry1");
		} catch (Exception e) {
			assertEquals("Error deleting country by name", e.getMessage());
		}
	}
}
