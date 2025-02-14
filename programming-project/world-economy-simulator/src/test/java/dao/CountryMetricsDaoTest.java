package dao;

import datasource.MariaDbConnection;
import entity.CountryEntity;
import entity.CountryMetricsEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryMetricsDaoTest {

	@BeforeAll
	public static void setUpDatabase() throws SQLException {
		MariaDbConnection.resetDatabaseForTests();
	}

	@AfterAll
	public static void tearDown() throws SQLException {
		Connection conn = MariaDbConnection.getConnection();
		conn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS `simulation`");
	}

	@BeforeEach
	public void setUp() {
		EntityManager em = MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM CountryMetricsEntity").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void testPersist() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");

		countryMetricsDao.persist(
				new CountryMetricsEntity(1, country, country.getPopulation(), country.getMoney(), 0.5, 100.0));

		CountryMetricsEntity foundCountryMetrics = countryMetricsDao.findByCountry(country).getFirst();
		assertEquals(1, foundCountryMetrics.getDay());
		assertEquals(country, foundCountryMetrics.getCountry());
		assertEquals(country.getPopulation(), foundCountryMetrics.getPopulation());
		assertEquals(country.getMoney(), foundCountryMetrics.getMoney());
		assertEquals(0.5, foundCountryMetrics.getAverageHappiness());
		assertEquals(100.0, foundCountryMetrics.getIndividualBudget());
	}

	@Test
	public void testFindByCountry() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");

		CountryMetricsEntity countryMetrics1 =
				new CountryMetricsEntity(1, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics2 =
				new CountryMetricsEntity(2, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics3 =
				new CountryMetricsEntity(3, country, 1_000_000, 100_000_000.0, 0.5, 100.0);

		countryMetricsDao.persist(countryMetrics1);
		countryMetricsDao.persist(countryMetrics2);
		countryMetricsDao.persist(countryMetrics3);

		List<CountryMetricsEntity> foundCountryMetrics = countryMetricsDao.findByCountry(country);
		assertEquals(3, foundCountryMetrics.size());
	}

	@Test
	public void testFindAll() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country1 = countryDao.findByName("Finland");
		CountryEntity country2 = countryDao.findByName("Sweden");
		CountryEntity country3 = countryDao.findByName("Norway");

		CountryMetricsEntity countryMetrics1 =
				new CountryMetricsEntity(1, country1, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics2 =
				new CountryMetricsEntity(2, country2, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics3 =
				new CountryMetricsEntity(3, country3, 1_000_000, 100_000_000.0, 0.5, 100.0);

		countryMetricsDao.persist(countryMetrics1);
		countryMetricsDao.persist(countryMetrics2);
		countryMetricsDao.persist(countryMetrics3);

		assertEquals(3, countryMetricsDao.findAll().size());
	}

	@Test
	public void testFindByCountryAndDay() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");

		CountryMetricsEntity countryMetrics1 =
				new CountryMetricsEntity(1, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics2 =
				new CountryMetricsEntity(2, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics3 =
				new CountryMetricsEntity(3, country, 1_000_000, 100_000_000.0, 0.5, 100.0);

		countryMetricsDao.persist(countryMetrics1);
		countryMetricsDao.persist(countryMetrics2);
		countryMetricsDao.persist(countryMetrics3);

		CountryMetricsEntity foundCountryMetrics = countryMetricsDao.findByCountryAndDay(country, 2);
		assertEquals(2, foundCountryMetrics.getDay());
		assertEquals(country, foundCountryMetrics.getCountry());
		assertEquals(1_000_000, foundCountryMetrics.getPopulation());
		assertEquals(100_000_000.0, foundCountryMetrics.getMoney());
		assertEquals(0.5, foundCountryMetrics.getAverageHappiness());
		assertEquals(100.0, foundCountryMetrics.getIndividualBudget());
	}

	@Test
	public void testFindByCountryNoResult() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");

		CountryMetricsEntity countryMetrics1 =
				new CountryMetricsEntity(1, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics2 =
				new CountryMetricsEntity(2, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics3 =
				new CountryMetricsEntity(3, country, 1_000_000, 100_000_000.0, 0.5, 100.0);

		countryMetricsDao.persist(countryMetrics1);
		countryMetricsDao.persist(countryMetrics2);
		countryMetricsDao.persist(countryMetrics3);

		assertEquals(0, countryMetricsDao.findByCountry(countryDao.findByName("Sweden")).size());
	}

	@Test
	public void testFindByCountryAndDayNoResult() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");

		CountryMetricsEntity countryMetrics1 =
				new CountryMetricsEntity(1, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics2 =
				new CountryMetricsEntity(2, country, 1_000_000, 100_000_000.0, 0.5, 100.0);
		CountryMetricsEntity countryMetrics3 =
				new CountryMetricsEntity(3, country, 1_000_000, 100_000_000.0, 0.5, 100.0);

		countryMetricsDao.persist(countryMetrics1);
		countryMetricsDao.persist(countryMetrics2);
		countryMetricsDao.persist(countryMetrics3);

		try {
			countryMetricsDao.findByCountryAndDay(countryDao.findByName("Sweden"), 2);
		} catch (Exception e) {
			assertEquals("No result found for query", e.getMessage());
		}
	}

	@Test
	public void testFindAllNoResult() {
		CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
		assertEquals(0, countryMetricsDao.findAll().size());
	}
}