package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.Recipient;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.PrimaryTransactionDao;
import com.hendisantika.onlinebanking.repository.RecipientDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsTransactionDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PrimaryTransactionDao primaryTransactionDao;

    @Mock
    private SavingsTransactionDao savingsTransactionDao;

    @Mock
    private PrimaryAccountDao primaryAccountDao;

    @Mock
    private SavingsAccountDao savingsAccountDao;

    @Mock
    private RecipientDao recipientDao;

    @InjectMocks
    private com.hendisantika.onlinebanking.service.UserServiceImpl.TransactionServiceImpl transactionService;

    @Test
    void savePrimaryDepositTransaction_delegatesToDao() {
        PrimaryTransaction t = new PrimaryTransaction(new Date(), "desc", "type", "Finished", 10.0, new BigDecimal("10.0"), new PrimaryAccount());
        transactionService.savePrimaryDepositTransaction(t);
        verify(primaryTransactionDao).save(t);
    }

    @Test
    void saveSavingsDepositTransaction_delegatesToDao() {
        SavingsTransaction t = new SavingsTransaction(new Date(), "desc", "type", "Finished", 5.0, new BigDecimal("5.0"), new SavingsAccount());
        transactionService.saveSavingsDepositTransaction(t);
        verify(savingsTransactionDao).save(t);
    }

    @Test
    void betweenAccountsTransfer_primaryToSavings_updatesBalancesAndSavesTransactions() throws Exception {
        PrimaryAccount pa = new PrimaryAccount();
        pa.setAccountBalance(new BigDecimal("500.0"));
        SavingsAccount sa = new SavingsAccount();
        sa.setAccountBalance(new BigDecimal("200.0"));

        transactionService.betweenAccountsTransfer("Primary", "Savings", "100", pa, sa);

        assertEquals(new BigDecimal("400.0"), pa.getAccountBalance());
        assertEquals(new BigDecimal("300.0"), sa.getAccountBalance());
        verify(primaryAccountDao).save(pa);
        verify(savingsAccountDao).save(sa);
        verify(primaryTransactionDao).save(any(PrimaryTransaction.class));
    }

    @Test
    void betweenAccountsTransfer_savingsToPrimary_updatesBalancesAndSavesTransactions() throws Exception {
        PrimaryAccount pa = new PrimaryAccount();
        pa.setAccountBalance(new BigDecimal("100.0"));
        SavingsAccount sa = new SavingsAccount();
        sa.setAccountBalance(new BigDecimal("300.0"));

        transactionService.betweenAccountsTransfer("Savings", "Primary", "50", pa, sa);

        assertEquals(new BigDecimal("150.0"), pa.getAccountBalance());
        assertEquals(new BigDecimal("250.0"), sa.getAccountBalance());
        verify(primaryAccountDao).save(pa);
        verify(savingsAccountDao).save(sa);
        verify(savingsTransactionDao).save(any(SavingsTransaction.class));
    }

    @Test
    void betweenAccountsTransfer_invalid_throwsException() {
        PrimaryAccount pa = new PrimaryAccount();
        SavingsAccount sa = new SavingsAccount();
        assertThrows(Exception.class, () -> transactionService.betweenAccountsTransfer("X", "Y", "10", pa, sa));
    }

    @Test
    void toSomeoneElseTransfer_primary_decreasesPrimaryAndSavesTransaction() {
        Recipient recipient = new Recipient();
        recipient.setName("r1");

        PrimaryAccount pa = new PrimaryAccount();
        pa.setAccountBalance(new BigDecimal("1000.0"));

        transactionService.toSomeoneElseTransfer(recipient, "Primary", "250", pa, new SavingsAccount());

        assertEquals(new BigDecimal("750.0"), pa.getAccountBalance());
        verify(primaryAccountDao).save(pa);
        verify(primaryTransactionDao).save(any(PrimaryTransaction.class));
    }

    @Test
    void toSomeoneElseTransfer_savings_decreasesSavingsAndSavesTransaction() {
        Recipient recipient = new Recipient();
        recipient.setName("r2");

        SavingsAccount sa = new SavingsAccount();
        sa.setAccountBalance(new BigDecimal("800.0"));

        transactionService.toSomeoneElseTransfer(recipient, "Savings", "300", new PrimaryAccount(), sa);

        assertEquals(new BigDecimal("500.0"), sa.getAccountBalance());
        verify(savingsAccountDao).save(sa);
        verify(savingsTransactionDao).save(any(SavingsTransaction.class));
    }

    @Test
    void findPrimaryTransactionList_returnsUserPrimaryTransactions() {
        PrimaryTransaction t1 = new PrimaryTransaction();
        PrimaryAccount pa = new PrimaryAccount();
        pa.setPrimaryTransactionList(Arrays.asList(t1));

        User user = new User();
        user.setPrimaryAccount(pa);

        when(userDao.findByUsername("u1")).thenReturn(user);

        List<PrimaryTransaction> list = transactionService.findPrimaryTransactionList("u1");
        assertEquals(1, list.size());
        assertSame(t1, list.get(0));
    }

    @Test
    void recipient_operations_save_find_delete_and_list() {
        Recipient r1 = new Recipient();
        r1.setName("r1");
        User owner = new User();
        owner.setUsername("owner");
        r1.setUser(owner);

        Recipient r2 = new Recipient();
        r2.setName("r2");
        r2.setUser(new User());

        when(recipientDao.findAll()).thenReturn(Arrays.asList(r1, r2));

        Principal p = mock(Principal.class);
        when(p.getName()).thenReturn("owner");

        List<Recipient> list = transactionService.findRecipientList(p);
        assertEquals(1, list.size());
        assertEquals("r1", list.get(0).getName());

        when(recipientDao.save(r1)).thenReturn(r1);
        Recipient saved = transactionService.saveRecipient(r1);
        assertSame(r1, saved);
        verify(recipientDao).save(r1);

        when(recipientDao.findByName("r1")).thenReturn(r1);
        assertSame(r1, transactionService.findRecipientByName("r1"));

        transactionService.deleteRecipientByName("r1");
        verify(recipientDao).deleteByName("r1");
    }

}
