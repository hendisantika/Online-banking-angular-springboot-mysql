package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private PrimaryAccountDao primaryAccountDao;

    @Mock
    private SavingsAccountDao savingsAccountDao;

    @Mock
    private UserDao userDao;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private com.hendisantika.onlinebanking.service.UserServiceImpl.AccountServiceImpl accountService;

    @Test
    void createPrimaryAccount_shouldReturnCreatedAccount() {
        when(primaryAccountDao.findByAccountNumber(anyInt())).thenAnswer(invocation -> {
            int accNum = invocation.getArgument(0);
            PrimaryAccount pa = new PrimaryAccount();
            pa.setAccountBalance(new BigDecimal("0.0"));
            pa.setAccountNumber(accNum);
            return pa;
        });

        PrimaryAccount result = accountService.createPrimaryAccount();

        assertNotNull(result);
        assertEquals(new BigDecimal("0.0"), result.getAccountBalance());
        assertTrue(result.getAccountNumber() > 0);
        verify(primaryAccountDao).save(any(PrimaryAccount.class));
        verify(primaryAccountDao).findByAccountNumber(result.getAccountNumber());
    }

    @Test
    void deposit_primary_updatesBalanceAndSavesTransaction() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        PrimaryAccount pa = new PrimaryAccount();
        pa.setAccountNumber(1234);
        pa.setAccountBalance(new BigDecimal("100.0"));

        User user = new User();
        user.setUsername("user1");
        user.setPrimaryAccount(pa);

        when(userDao.findByUsername("user1")).thenReturn(user);

        ArgumentCaptor<PrimaryAccount> captor = ArgumentCaptor.forClass(PrimaryAccount.class);

        accountService.deposit("Primary", 50.0, principal);

        verify(primaryAccountDao).save(captor.capture());
        PrimaryAccount saved = captor.getValue();
        assertEquals(new BigDecimal("150.0"), saved.getAccountBalance());
        verify(transactionService).savePrimaryDepositTransaction(any(PrimaryTransaction.class));
    }

}
