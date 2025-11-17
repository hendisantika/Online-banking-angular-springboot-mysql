package com.hendisantika.onlinebanking.integration;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class AccountIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        // Ensure JDBC URL includes params that disable SSL for the test container
        registry.add("spring.datasource.url", () -> {
            String url = mysql.getJdbcUrl();
            // Append parameters using proper separator depending on whether the URL already has query params
            String params = "useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            if (url.contains("?")) {
                return url + "&" + params;
            } else {
                return url + "?" + params;
            }
        });
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        // enable Flyway so migrations from src/main/resources/db/migration are applied
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private AccountService accountService;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Test
    void createPrimaryAccount_persistsAccount() {
        PrimaryAccount pa = accountService.createPrimaryAccount();

        assertNotNull(pa);
        assertTrue(pa.getAccountNumber() > 0);

        PrimaryAccount persisted = primaryAccountDao.findByAccountNumber(pa.getAccountNumber());
        assertNotNull(persisted);
        assertEquals(pa.getAccountNumber(), persisted.getAccountNumber());
    }
}
