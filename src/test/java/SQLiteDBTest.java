import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLiteDBTest {

    private static final String DB_URL = "jdbc:sqlite:src/test/resources/testdb.sqlite";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";

    private IDatabaseTester databaseTester;

    @BeforeAll
    void setUpDatabase() throws Exception {
        databaseTester = new JdbcDatabaseTester(DRIVER_CLASS, DB_URL);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(
                getClass().getClassLoader().getResourceAsStream("users-dataset.xml")
        );

        databaseTester.setDataSet(dataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.onSetup();
    }

    @AfterAll
    void tearDownDatabase() throws Exception {
        if (databaseTester != null) {
            databaseTester.onTearDown();
        }
    }

    @Test
    void testUserCount() throws Exception {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             java.sql.Statement stmt = connection.createStatement();
             java.sql.ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM users")) {

            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(2, count, "User count does not match!");
        }
    }

    @Test
    void testUserExists() throws Exception {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             java.sql.Statement stmt = connection.createStatement();
             java.sql.ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'JohnDoe'")) {

            resultSet.next();
            int count = resultSet.getInt(1);
            assertTrue(count > 0, "JohnDoe is not in the database!");
        }
    }

    @Test
    void testUser2Exists() throws Exception {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             java.sql.Statement stmt = connection.createStatement();
             java.sql.ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'KevinDoe'")) {

            resultSet.next();
            int count = resultSet.getInt(1);
            assertTrue(count > 0, "KevinDoe is not in the database!");
        }
    }
}
