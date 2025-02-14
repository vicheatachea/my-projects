package datasource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for managing database connections and executing SQL scripts in MariaDB.
 * This class provides functions to establish a JDBC connection with MariaDB, create an
 * EntityManager for use with JPA, execute SQL scripts, and terminate database resources.
 */
public class MariaDbConnection {

	/**
	 * Singleton connection to the MariaDB database.
	 */
	private static Connection conn = null;

	/**
	 * Singleton {@link EntityManagerFactory} for managing JPA entity managers.
	 */
	private static EntityManagerFactory emf = null;

	/**
	 * Credentials and connection URLs for the MariaDB database.
	 */
	private static final String USER = "simulation_user";
	private static final String PASSWORD = "password";
	private static final String BASE_URL = "jdbc:mariadb://localhost:3306/";
	private static final String URL = "jdbc:mariadb://localhost:3306/simulation";

	/**
	 * Logger for logging debug and error information.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MariaDbConnection.class);

	/**
	 * Retrieves a JDBC {@link Connection} to the MariaDB database. If the connection does not exist
	 * or is closed, a new connection is established. If the database does not exist, it is created.
	 *
	 * @return A {@link Connection} object connected to the MariaDB database.
	 * @throws SQLException If the connection cannot be established or if an SQL error occurs.
	 */
	public static Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			try {
				// Ensure the "simulation" database exists
				try (Connection baseConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD)) {
					baseConn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS `simulation`");
				}

				// Establish connection to the "simulation" database
				conn = DriverManager.getConnection(URL, USER, PASSWORD);
				logger.debug("Connected to MariaDB");
			} catch (SQLException e) {
				logger.error("Failed to connect to MariaDB!", e);
				throw new SQLException(e);
			}
		}
		return conn;
	}

	/**
	 * Resets the "simulation" database for testing purposes. Drops the existing database schema
	 * and recreates it using the SQL scripts provided in the "scripts" directory. The database
	 * is initialized with the necessary tables and procedures for the simulation.
	 *
	 * @throws SQLException If an error occurs while resetting the database.
	 */
	public static void resetDatabaseForTests() throws SQLException {
		try (Connection conn = getConnection()) {
			conn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS `simulation`");
			conn.createStatement().executeUpdate("CREATE DATABASE `simulation`");
			conn.createStatement().executeUpdate("USE `simulation`");
			executeSqlFile("scripts/simulationDb.sql");
			executeSqlFile("scripts/simulationMetrics.sql");
		} catch (SQLException e) {
			logger.error("Failed to reset database!", e);
			throw new SQLException(e);
		}
	}

	/**
	 * Retrieves a JPA {@link EntityManager}, which provides operations for querying
	 * and persisting database entities. If the {@link EntityManagerFactory} does not exist, it
	 * is initialized using the "Simulation" persistence unit.
	 *
	 * @return An {@link EntityManager} instance for managing database entities.
	 */
	public static EntityManager getEntityManager() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("Simulation");
		}
		return emf.createEntityManager();
	}

	/**
	 * Executes an SQL script file. The method processes the file's contents line by line
	 * and executes the SQL statements using the established {@link Connection}.
	 * Supports SQL procedures defined using the `DELIMITER` keyword.
	 *
	 * @param filePath The path to the SQL file to execute. The file is expected to be located
	 *                 in the application classpath.
	 * @throws SQLException If there is an error executing the SQL file.
	 */
	public static void executeSqlFile(String filePath) throws SQLException {
		try (InputStream input = MariaDbConnection.class.getClassLoader().getResourceAsStream(filePath)) {
			assert input != null;
			try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
				StringBuilder sb = new StringBuilder();
				String line;
				boolean inProcedure = false;

				while ((line = br.readLine()) != null) {
					if (line.trim().equalsIgnoreCase("DELIMITER //")) {
						inProcedure = true;
						continue;
					} else if (line.trim().equalsIgnoreCase("DELIMITER ;")) {
						inProcedure = false;
						continue;
					}

					if (inProcedure) {
						if (line.trim().endsWith("//")) {
							sb.append(line, 0, line.length() - 2).append("\n");
							try (Statement stmt = conn.createStatement()) {
								stmt.execute(sb.toString());
							}
							sb.setLength(0);
						} else {
							sb.append(line).append("\n");
						}
					} else {
						sb.append(line).append("\n");
						if (line.trim().endsWith(";")) {
							try (Statement stmt = conn.createStatement()) {
								stmt.execute(sb.toString());
							}
							sb.setLength(0);
						}
					}
				}

				logger.debug("Executed SQL file: {}", filePath);
			}
		} catch (SQLException | IOException ex) {
			logger.error("Failed to execute SQL file!", ex);
			throw new SQLException(ex);
		}
	}

	/**
	 * Terminates the database connection and the JPA {@link EntityManagerFactory}.
	 * Ensures proper cleanup of resources to prevent resource leaks.
	 *
	 * @throws SQLException If an error occurs while closing the database connection.
	 */
	public static void terminate() throws SQLException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				logger.debug("Connection closed");
			}
			if (emf != null) {
				emf.close();
				logger.debug("EntityManagerFactory closed");
			}
		} catch (SQLException e) {
			logger.error("Failed to close connection!", e);
			throw new SQLException(e);
		}
	}
}