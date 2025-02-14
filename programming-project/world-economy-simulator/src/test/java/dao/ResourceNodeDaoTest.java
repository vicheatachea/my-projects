package dao;

import datasource.MariaDbConnection;
import entity.CountryEntity;
import entity.ResourceEntity;
import entity.ResourceNodeEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceNodeDaoTest {

	@BeforeAll
	public static void setUpDatabase() throws SQLException {
		MariaDbConnection.resetDatabaseForTests();
	}

	@AfterAll
	public static void tearDownDatabase() throws SQLException {
		try (Connection conn = MariaDbConnection.getConnection()) {
			conn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS `simulation`");
		}
	}

	@BeforeEach
	public void setUp() {
		EntityManager em = MariaDbConnection.getEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM ResourceNodeEntity").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void testPersist() throws SQLException {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();

		ResourceEntity resource = resourceDao.findByName("Gold");

		CountryEntity country = countryDao.findByName("Finland");

		ResourceNodeEntity resourceNode = new ResourceNodeEntity(country, resource, 1, 1, 1, 1);
		resourceNodeDao.persist(resourceNode);

		assertEquals(1, resourceNodeDao.findAll().size());
		assertEquals("Gold", resourceNodeDao.findAll().getFirst().getResource().getName());
		assertEquals("Finland", resourceNodeDao.findAll().getFirst().getCountry().getName());
		assertEquals(1, resourceNodeDao.findAll().getFirst().getQuantity());
		assertEquals(1, resourceNodeDao.findAll().getFirst().getTier());
		assertEquals(1, resourceNodeDao.findAll().getFirst().getBaseCapacity());
		assertEquals(1, resourceNodeDao.findAll().getFirst().getBaseProductionCost());
	}

	@Test
	public void testFindAll() throws SQLException {
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();

		ResourceEntity resource1 = resourceDao.findByName("Gold");
		ResourceEntity resource2 = resourceDao.findByName("Wood");

		CountryEntity country = countryDao.findByName("Finland");

		ResourceNodeEntity resourceNode1 = new ResourceNodeEntity(country, resource1, 1, 1, 1, 1);
		ResourceNodeEntity resourceNode2 = new ResourceNodeEntity(country, resource2, 1, 1, 1, 1);

		resourceNodeDao.persist(resourceNode1);
		resourceNodeDao.persist(resourceNode2);

		assertEquals(2, resourceNodeDao.findAll().size());
	}

	@Test
	public void testFindByCountry() throws SQLException {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();

		ResourceEntity resource1 = resourceDao.findByName("Gold");
		ResourceEntity resource2 = resourceDao.findByName("Wood");
		ResourceEntity resource3 = resourceDao.findByName("Iron");
		ResourceEntity resource4 = resourceDao.findByName("Coal");

		CountryEntity country1 = countryDao.findByName("Finland");

		ResourceNodeEntity resourceNode1 = new ResourceNodeEntity(country1, resource1, 1, 1, 1, 1);
		ResourceNodeEntity resourceNode2 = new ResourceNodeEntity(country1, resource2, 1, 1, 1, 1);
		ResourceNodeEntity resourceNode3 = new ResourceNodeEntity(country1, resource3, 1, 1, 1, 1);
		ResourceNodeEntity resourceNode4 = new ResourceNodeEntity(country1, resource4, 1, 1, 1, 1);

		resourceNodeDao.persist(resourceNode1);
		resourceNodeDao.persist(resourceNode2);
		resourceNodeDao.persist(resourceNode3);
		resourceNodeDao.persist(resourceNode4);

		List<ResourceNodeEntity> foundResources = resourceNodeDao.findByCountry(country1);

		assertEquals(4, foundResources.size());
	}

	@Test
	public void testFindByCountryNoResult() throws SQLException {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();

		CountryEntity country = countryDao.findByName("Finland");
		CountryEntity country2 = countryDao.findByName("Sweden");

		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeEntity resourceNode = new ResourceNodeEntity(country, resource, 1, 1, 1, 1);
		resourceNodeDao.persist(resourceNode);

		assertEquals(0, resourceNodeDao.findByCountry(country2).size());
	}

	@Test
	public void testDeleteByCountry() throws SQLException {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();

		CountryEntity country = countryDao.findByName("Finland");

		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeEntity resourceNode = new ResourceNodeEntity(country, resource, 1, 1, 1, 1);
		resourceNodeDao.persist(resourceNode);

		resourceNodeDao.delete(country.getId(), resource.getId());
		assertEquals(0, resourceNodeDao.findAll().size());
	}

	@Test
	public void testDeleteByCountryNoResult() throws SQLException {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();

		CountryEntity country = countryDao.findByName("Finland");

		ResourceEntity resource = resourceDao.findByName("Gold");

		ResourceNodeEntity resourceNode = new ResourceNodeEntity(country, resource, 1, 1, 1, 1);
		resourceNodeDao.persist(resourceNode);

		resourceNodeDao.delete(country.getId(), 2L);
		assertEquals(1, resourceNodeDao.findAll().size());
	}

	@Test
	public void testDeleteByCountryException() throws SQLException {
		ResourceDao resourceDao = new ResourceDao();
		CountryDao countryDao = new CountryDao();
		ResourceNodeDao resourceNodeDao = new ResourceNodeDao();

		ResourceEntity resource = resourceDao.findByName("Gold");

		CountryEntity country = countryDao.findByName("Finland");

		ResourceNodeEntity resourceNode = new ResourceNodeEntity(country, resource, 1, 1, 1, 1);
		resourceNodeDao.persist(resourceNode);

		try {
			resourceNodeDao.delete(2L, resource.getId());
		} catch (Exception e) {
			assertEquals("Error deleting resource node", e.getMessage());
		}
	}
}