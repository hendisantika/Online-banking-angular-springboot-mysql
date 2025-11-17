package com.hendisantika.onlinebanking.integration;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.service.UserService;
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
class UserIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> {
            String url = mysql.getJdbcUrl();
            String params = "useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            if (url.contains("?")) return url + "&" + params; else return url + "?" + params;
        });
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Test
    void createUser_persistsUserAndAccounts() {
        User user = new User();
        user.setUsername("intuser");
        user.setPassword("pass");
        user.setEmail("intuser@example.com");

        User created = userService.createUser(user, java.util.Collections.emptySet());

        assertNotNull(created);
        User fetched = userDao.findByUsername("intuser");
        assertNotNull(fetched);
        assertNotNull(fetched.getPrimaryAccount());
        assertNotNull(fetched.getSavingsAccount());
    }
}
