package dao;

import datasource.MariaDbConnection;
import entity.CountryEntity;
import entity.ResourceEntity;
import entity.ResourceMetricsEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceMetricsDaoTest {

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
		em.createQuery("DELETE FROM ResourceMetricsEntity").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void testPersist() {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();

		CountryEntity country = countryDao.findByName("Finland");

		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceMetricsEntity resourceMetrics = new ResourceMetricsEntity(1, country, resource, 100, 100.0);
		resourceMetricsDao.persist(resourceMetrics);

		ResourceMetricsEntity foundResourceMetrics =
				resourceMetricsDao.findByResourceAndDay(country, resource, 1);

		assertEquals(1, foundResourceMetrics.getDay());
		assertEquals(country, foundResourceMetrics.getCountry());
		assertEquals(resource, foundResourceMetrics.getResource());
		assertEquals(100, foundResourceMetrics.getQuantity());
		assertEquals(100.0, foundResourceMetrics.getValue());
	}

	@Test
	public void testFindByResource() {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();

		CountryEntity country = countryDao.findByName("Finland");

		ResourceEntity resource1 = resourceDao.findByName("Gold");
		ResourceEntity resource2 = resourceDao.findByName("Wood");
		ResourceEntity resource3 = resourceDao.findByName("Food");

		ResourceMetricsEntity resourceMetrics1 = new ResourceMetricsEntity(1, country, resource1, 100, 100.0);
		ResourceMetricsEntity resourceMetrics2 = new ResourceMetricsEntity(2, country, resource1, 100, 100.0);
		ResourceMetricsEntity resourceMetrics3 = new ResourceMetricsEntity(1, country, resource2, 100, 100.0);
		ResourceMetricsEntity resourceMetrics4 = new ResourceMetricsEntity(1, country, resource3, 100, 100.0);

		resourceMetricsDao.persist(resourceMetrics1);
		resourceMetricsDao.persist(resourceMetrics2);
		resourceMetricsDao.persist(resourceMetrics3);
		resourceMetricsDao.persist(resourceMetrics4);

		List<ResourceMetricsEntity> foundResourceMetrics = resourceMetricsDao.findByResource(country, resource1);

		assertEquals(1, foundResourceMetrics.getFirst().getDay());
		assertEquals(country, foundResourceMetrics.getFirst().getCountry());
		assertEquals("Gold", foundResourceMetrics.getFirst().getResource().getName());
		assertEquals(100, foundResourceMetrics.getFirst().getQuantity());
		assertEquals(100.0, foundResourceMetrics.getFirst().getValue());
	}

	@Test
	public void testFindAll() {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();

		CountryEntity country1 = countryDao.findByName("Finland");
		CountryEntity country2 = countryDao.findByName("Sweden");
		CountryEntity country3 = countryDao.findByName("Norway");

		ResourceEntity resource1 = resourceDao.findByName("Gold");
		ResourceEntity resource2 = resourceDao.findByName("Wood");
		ResourceEntity resource3 = resourceDao.findByName("Food");

		ResourceMetricsEntity resourceMetrics1 = new ResourceMetricsEntity(1, country1, resource1, 100, 100.0);
		ResourceMetricsEntity resourceMetrics2 = new ResourceMetricsEntity(2, country2, resource2, 100, 100.0);
		ResourceMetricsEntity resourceMetrics3 = new ResourceMetricsEntity(3, country3, resource3, 100, 100.0);

		resourceMetricsDao.persist(resourceMetrics1);
		resourceMetricsDao.persist(resourceMetrics2);
		resourceMetricsDao.persist(resourceMetrics3);

		assertEquals(3, resourceMetricsDao.findAll().size());
	}

	@Test
	public void testFindByResourceAndDay() {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceMetricsEntity resourceMetrics1 = new ResourceMetricsEntity(1, country, resource, 100, 100.0);
		ResourceMetricsEntity resourceMetrics2 = new ResourceMetricsEntity(2, country, resource, 100, 100.0);
		ResourceMetricsEntity resourceMetrics3 = new ResourceMetricsEntity(3, country, resource, 100, 100.0);

		resourceMetricsDao.persist(resourceMetrics1);
		resourceMetricsDao.persist(resourceMetrics2);
		resourceMetricsDao.persist(resourceMetrics3);

		ResourceMetricsEntity foundResourceMetrics = resourceMetricsDao.findByResourceAndDay(country, resource, 2);

		assertEquals(2, foundResourceMetrics.getDay());
		assertEquals(country, foundResourceMetrics.getCountry());
		assertEquals(resource, foundResourceMetrics.getResource());
		assertEquals(100, foundResourceMetrics.getQuantity());
		assertEquals(100.0, foundResourceMetrics.getValue());
	}

	@Test
	public void testFindByResourceNoResult() {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceMetricsEntity resourceMetrics1 = new ResourceMetricsEntity(1, country, resource, 100, 100.0);
		ResourceMetricsEntity resourceMetrics2 = new ResourceMetricsEntity(2, country, resource, 100, 100.0);
		ResourceMetricsEntity resourceMetrics3 = new ResourceMetricsEntity(3, country, resource, 100, 100.0);

		resourceMetricsDao.persist(resourceMetrics1);
		resourceMetricsDao.persist(resourceMetrics2);
		resourceMetricsDao.persist(resourceMetrics3);

		List<ResourceMetricsEntity> foundResourceMetrics =
				resourceMetricsDao.findByResource(country, resourceDao.findByName("Wood"));

		assertEquals(0, foundResourceMetrics.size());
	}

	@Test
	public void testFindByResourceAndDayNoResult() {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();

		CountryEntity country = countryDao.findByName("Finland");
		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceMetricsEntity resourceMetrics1 = new ResourceMetricsEntity(1, country, resource, 100, 100.0);
		ResourceMetricsEntity resourceMetrics2 = new ResourceMetricsEntity(2, country, resource, 100, 100.0);
		ResourceMetricsEntity resourceMetrics3 = new ResourceMetricsEntity(3, country, resource, 100, 100.0);

		resourceMetricsDao.persist(resourceMetrics1);
		resourceMetricsDao.persist(resourceMetrics2);
		resourceMetricsDao.persist(resourceMetrics3);

		try {
			resourceMetricsDao.findByResourceAndDay(country, resource, 4);
		} catch (Exception e) {
			assertEquals("No result found for query", e.getMessage());
		}

	}

	@Test
	public void testFindAllException() {
		ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();
		try {
			resourceMetricsDao.findAll();
		} catch (Exception e) {
			assertEquals("Error finding all resource metrics", e.getMessage());
		}
	}
}