package com.hendisantika.onlinebanking.integration;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.PrimaryTransactionDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsTransactionDao;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class TransactionIntegrationTest {

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
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PrimaryAccountDao primaryAccountDao;

    @Autowired
    SavingsAccountDao savingsAccountDao;

    @Autowired
    PrimaryTransactionDao primaryTransactionDao;

    @Autowired
    SavingsTransactionDao savingsTransactionDao;

    @Test
    void betweenAccountsTransfer_primaryToSavings_persistsTransactionsAndBalances() throws Exception {
        PrimaryAccount pa = accountService.createPrimaryAccount();
        SavingsAccount sa = accountService.createSavingsAccount();

        // set initial balances and persist
        pa.setAccountBalance(new BigDecimal("500.00"));
        primaryAccountDao.save(pa);
        sa.setAccountBalance(new BigDecimal("200.00"));
        savingsAccountDao.save(sa);

        transactionService.betweenAccountsTransfer("Primary", "Savings", "100", pa, sa);

        PrimaryAccount updatedPa = primaryAccountDao.findByAccountNumber(pa.getAccountNumber());
        SavingsAccount updatedSa = savingsAccountDao.findByAccountNumber(sa.getAccountNumber());

        assertEquals(new BigDecimal("400.00"), updatedPa.getAccountBalance());
        assertEquals(new BigDecimal("300.00"), updatedSa.getAccountBalance());

        assertFalse(primaryTransactionDao.findAll().isEmpty());
    }
}
