package dao;

import datasource.MariaDbConnection;
import entity.ResourceEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResourceDaoTest {

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
		em.createQuery("DELETE FROM ResourceEntity").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void testPersist() {
		ResourceDao resourceDao = new ResourceDao();

		ResourceEntity resource = new ResourceEntity("TestResource", 0.5, 100, 100.0);
		resourceDao.persist(resource);

		ResourceEntity foundResource = resourceDao.findByName("TestResource");
		assertEquals("TestResource", foundResource.getName());
		assertEquals(0.5, foundResource.getPriority());
		assertEquals(100, foundResource.getBaseCapacity());
		assertEquals(100.0, foundResource.getProductionCost());
	}

	@Test
	public void testFindByName() {
		ResourceDao resourceDao = new ResourceDao();

		ResourceEntity resource = new ResourceEntity("TestResource", 0.5, 100, 100.0);
		resourceDao.persist(resource);

		ResourceEntity foundResource = resourceDao.findByName("TestResource");
		assertEquals("TestResource", foundResource.getName());
		assertEquals(0.5, foundResource.getPriority());
		assertEquals(100, foundResource.getBaseCapacity());
		assertEquals(100.0, foundResource.getProductionCost());
	}

	@Test
	public void testFindAll() {
		ResourceDao resourceDao = new ResourceDao();

		ResourceEntity resource1 = new ResourceEntity("TestResource1", 0.5, 100, 100.0);
		ResourceEntity resource2 = new ResourceEntity("TestResource2", 0.6, 200, 200.0);
		ResourceEntity resource3 = new ResourceEntity("TestResource3", 0.7, 300, 300.0);

		resourceDao.persist(resource1);
		resourceDao.persist(resource2);
		resourceDao.persist(resource3);

		assertEquals(3, resourceDao.findAll().size());
	}

	@Test
	public void testFindByNameNoResult() {
		ResourceDao resourceDao = new ResourceDao();
		assertNull(resourceDao.findByName("TestResource"));
	}

	@Test
	public void testFindByNameException() {
		ResourceDao resourceDao = new ResourceDao();
		try {
			resourceDao.findByName("TestResource1");
		} catch (Exception e) {
			assertEquals("Error finding resource by name", e.getMessage());
		}
	}

	@Test
	public void testFindAllException() {
		ResourceDao resourceDao = new ResourceDao();
		try {
			resourceDao.findAll();
		} catch (Exception e) {
			assertEquals("Error finding all resources", e.getMessage());
		}
	}

	@Test
	public void testFindByNameNoResultException() {
		ResourceDao resourceDao = new ResourceDao();
		assertNull(resourceDao.findByName("TestResource"));
	}

	@Test
	public void testDeleteByName() {
		ResourceDao resourceDao = new ResourceDao();

		ResourceEntity resource = new ResourceEntity("TestResource", 0.5, 100, 100.0);
		resourceDao.persist(resource);

		resourceDao.deleteByName("TestResource");
		assertNull(resourceDao.findByName("TestResource"));
	}
}