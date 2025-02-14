package dao;

import datasource.MariaDbConnection;
import entity.CountryEntity;
import entity.ResourceEntity;
import entity.ResourceNodeMetricsEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceNodeMetricsDaoTest {

	@BeforeAll
	public static void setUpDatabase() throws SQLException {
		MariaDbConnection.resetDatabaseForTests();
	}

	@AfterAll
	public static void tearDown() throws SQLException {
		try (Connection conn = MariaDbConnection.getConnection()) {
			conn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS `simulation`");
		}
	}

	@BeforeEach
	public void setUp() {
		EntityManager em = MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM ResourceNodeMetricsEntity").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void testPersist() {
		ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeMetricsEntity resourceNodeMetrics = new ResourceNodeMetricsEntity(1, country, resource, 1, 1, 1);
		resourceNodeMetricsDao.persist(resourceNodeMetrics);

		ResourceNodeMetricsEntity foundResourceNodeMetrics =
				resourceNodeMetricsDao.findByResourceNodeAndDay(country, resource, 1);
		assertEquals(1, foundResourceNodeMetrics.getDay());
		assertEquals(country, foundResourceNodeMetrics.getCountry());
		assertEquals(resource, foundResourceNodeMetrics.getResource());
		assertEquals(1, foundResourceNodeMetrics.getMaxCapacity());
		assertEquals(1, foundResourceNodeMetrics.getProductionCost());
		assertEquals(1, foundResourceNodeMetrics.getTier());
	}

	@Test
	public void testFindByResource() {
		ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeMetricsEntity resourceNodeMetrics1 = new ResourceNodeMetricsEntity(1, country, resource, 1, 1, 1);
		ResourceNodeMetricsEntity resourceNodeMetrics2 = new ResourceNodeMetricsEntity(2, country, resource, 2, 2, 2);
		ResourceNodeMetricsEntity resourceNodeMetrics3 = new ResourceNodeMetricsEntity(3, country, resource, 3, 3, 3);

		resourceNodeMetricsDao.persist(resourceNodeMetrics1);
		resourceNodeMetricsDao.persist(resourceNodeMetrics2);
		resourceNodeMetricsDao.persist(resourceNodeMetrics3);

		assertEquals(3, resourceNodeMetricsDao.findByResourceNode(country, resource).size());
	}

	@Test
	public void testFindByResourceNodeAndDay() {
		ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeMetricsEntity resourceNodeMetrics1 = new ResourceNodeMetricsEntity(1, country, resource, 1, 1, 1);
		ResourceNodeMetricsEntity resourceNodeMetrics2 = new ResourceNodeMetricsEntity(2, country, resource, 2, 2, 2);

		resourceNodeMetricsDao.persist(resourceNodeMetrics1);
		resourceNodeMetricsDao.persist(resourceNodeMetrics2);

		ResourceNodeMetricsEntity foundResourceNodeMetrics =
				resourceNodeMetricsDao.findByResourceNodeAndDay(country, resource, 2);
		assertEquals(2, foundResourceNodeMetrics.getDay());
		assertEquals(country, foundResourceNodeMetrics.getCountry());
		assertEquals(resource, foundResourceNodeMetrics.getResource());
		assertEquals(2, foundResourceNodeMetrics.getMaxCapacity());
		assertEquals(2, foundResourceNodeMetrics.getProductionCost());
		assertEquals(2, foundResourceNodeMetrics.getTier());
	}

	@Test
	public void testFindAll() {
		ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeMetricsEntity resourceNodeMetrics1 = new ResourceNodeMetricsEntity(1, country, resource, 1, 1, 1);
		ResourceNodeMetricsEntity resourceNodeMetrics2 = new ResourceNodeMetricsEntity(2, country, resource, 2, 2, 2);

		resourceNodeMetricsDao.persist(resourceNodeMetrics1);
		resourceNodeMetricsDao.persist(resourceNodeMetrics2);

		assertEquals(2, resourceNodeMetricsDao.findAll().size());
	}

	@Test
	public void testFindByResourceNodeNoResult() {
		ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeMetricsEntity resourceNodeMetrics1 = new ResourceNodeMetricsEntity(1, country, resource, 1, 1, 1);
		ResourceNodeMetricsEntity resourceNodeMetrics2 = new ResourceNodeMetricsEntity(2, country, resource, 2, 2, 2);

		resourceNodeMetricsDao.persist(resourceNodeMetrics1);
		resourceNodeMetricsDao.persist(resourceNodeMetrics2);

		assertEquals(0, resourceNodeMetricsDao.findByResourceNode(countryDao.findByName("Sweden"), resource).size());
	}

	@Test
	public void testFindByResourceNodeAndDayNoResult() {
		ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeMetricsEntity resourceNodeMetrics1 = new ResourceNodeMetricsEntity(1, country, resource, 1, 1, 1);
		ResourceNodeMetricsEntity resourceNodeMetrics2 = new ResourceNodeMetricsEntity(2, country, resource, 2, 2, 2);

		resourceNodeMetricsDao.persist(resourceNodeMetrics1);
		resourceNodeMetricsDao.persist(resourceNodeMetrics2);

		try {
			resourceNodeMetricsDao.findByResourceNodeAndDay(country, resource, 3);
		} catch (Exception e) {
			assertEquals("No result found for query", e.getMessage());
		}
	}
}