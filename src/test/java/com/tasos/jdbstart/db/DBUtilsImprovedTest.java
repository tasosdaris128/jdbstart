package com.tasos.jdbstart.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariDataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

public class DBUtilsImprovedTest {

    private Properties properties;

    private HikariDataSource dataSource;

    @BeforeEach
    public void setup() {
        properties = new Properties();
        properties.setProperty("url", System.getenv("PGURL"));
        properties.setProperty("user", System.getenv("PGUSR"));
        properties.setProperty("password", System.getenv("PGPWD"));

        dataSource = DataSourceGenerator.generate(properties);
    }

    // The following two tests are just to ensure that we have properly
    // gained the environment variables for the test DB.

    @Test
    public void testThatPropertiesObjectIsNotNull() {
        assertNotNull(properties, () -> "Properties should not be null. Check .env file.");
    }

    @Test
    public void testThatPropertiesAreNotEmpty() {
        assertNotNull(properties.getProperty("url"), () -> "Url property should not be empty. Check .env file.");
        assertNotNull(properties.getProperty("user"), () ->"Username property should not be empty. Check .env file.");
        assertNotNull(properties.getProperty("password"), () -> "Password property should not be empty. Check .env file.");

        assertFalse(properties.getProperty("url").isEmpty(), "Url property should not be empty. Check .env file");
        assertFalse(properties.getProperty("user").isEmpty(), "Username property should not be empty. Check .env file");
        assertFalse(properties.getProperty("password").isEmpty(), "Password property should not be empty. Check .env file");
    }

    @Test
    public void doInTransaction_testThatTheConnectionIsNotNull() {
        DBUtilsImproved.doInTranstaction(dataSource, (conn) -> {
            assertNotNull(conn, () -> "Connection should not be null.");
        });
    }

    @Test
    public void doInTransaction_testThatTheConnectionIsValid() {
        DBUtilsImproved.doInTranstaction(dataSource, (conn) -> {
            assertTrue(conn.isValid(10), "Connection to DB is not valid.");
        });
    }
    
    @Test
    public void doInTransactionWithReturn_testThatTheConnectionIsNotNull() {
        DBUtilsImproved.doInTranstactionWithReturn(dataSource, (conn) -> {
            assertNotNull(conn, () -> "Connection should not be null.");

            return null;
        });
    }

    @Test
    public void doInTransactionWithReturn_testThatTheConnectionIsValid() {
        DBUtilsImproved.doInTranstactionWithReturn(dataSource, (conn) -> {
            assertTrue(conn.isValid(10), "Connection to DB is not valid.");

            return null;
        });
    }

    @Test
    public void doInTransactionWithReturn_testThatTheFunctionReturnsObject() {
        Object o = DBUtilsImproved.doInTranstactionWithReturn(dataSource, (conn) -> {
            return new Object();
        });

        assertNotNull(o, () -> "doInTransactionWithReturn() should return non null object.");
    }

}