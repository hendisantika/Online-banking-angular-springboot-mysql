package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.PrimaryTransactionDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsTransactionDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.ActivityLogService;
import com.hendisantika.onlinebanking.service.EmailService;
import com.hendisantika.onlinebanking.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    private static int nextAccountNumber = 11223101;

    private final PrimaryAccountDao primaryAccountDao;
    private final SavingsAccountDao savingsAccountDao;
    private final UserDao userDao;
    private final TransactionService transactionService;
    private final PrimaryTransactionDao primaryTransactionDao;
    private final SavingsTransactionDao savingsTransactionDao;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    public AccountServiceImpl(PrimaryAccountDao primaryAccountDao, SavingsAccountDao savingsAccountDao,
                              UserDao userDao, TransactionService transactionService,
                              PrimaryTransactionDao primaryTransactionDao, SavingsTransactionDao savingsTransactionDao,
                              EmailService emailService, ActivityLogService activityLogService) {
        this.primaryAccountDao = primaryAccountDao;
        this.savingsAccountDao = savingsAccountDao;
        this.userDao = userDao;
        this.transactionService = transactionService;
        this.primaryTransactionDao = primaryTransactionDao;
        this.savingsTransactionDao = savingsTransactionDao;
        this.emailService = emailService;
        this.activityLogService = activityLogService;
    }

    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal("0.0"));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal("0.0"));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    public void deposit(String accountType, double amount, Principal principal) {
        User user = userDao.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Deposit", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);

            emailService.sendTransactionNotification(user, "Deposit", amount, "Primary");
            activityLogService.logActivity(user, "DEPOSIT", "Deposited $" + amount + " to Primary Account", null, "SUCCESS");

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to Savings Account", "Deposit", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);

            emailService.sendTransactionNotification(user, "Deposit", amount, "Savings");
            activityLogService.logActivity(user, "DEPOSIT", "Deposited $" + amount + " to Savings Account", null, "SUCCESS");
        }
    }

    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userDao.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Withdraw", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);

            emailService.sendTransactionNotification(user, "Withdrawal", amount, "Primary");
            activityLogService.logActivity(user, "WITHDRAW", "Withdrew $" + amount + " from Primary Account", null, "SUCCESS");

            if (primaryAccount.getAccountBalance().doubleValue() < 100) {
                emailService.sendLowBalanceAlert(user, "Primary", primaryAccount.getAccountBalance().doubleValue());
            }

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from Savings Account", "Withdraw", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsWithdrawTransaction(savingsTransaction);

            emailService.sendTransactionNotification(user, "Withdrawal", amount, "Savings");
            activityLogService.logActivity(user, "WITHDRAW", "Withdrew $" + amount + " from Savings Account", null, "SUCCESS");

            if (savingsAccount.getAccountBalance().doubleValue() < 100) {
                emailService.sendLowBalanceAlert(user, "Savings", savingsAccount.getAccountBalance().doubleValue());
            }
        }
    }

    @Override
    public boolean checkWithdrawLimit(User user, double amount) {
        double dailyTotal = getDailyWithdrawTotal(user);
        Double limit = user.getDailyWithdrawLimit();
        if (limit == null) {
            limit = 5000.0;
        }
        return (dailyTotal + amount) <= limit;
    }

    @Override
    public boolean checkTransferLimit(User user, double amount) {
        double dailyTotal = getDailyTransferTotal(user);
        Double limit = user.getDailyTransferLimit();
        if (limit == null) {
            limit = 10000.0;
        }
        return (dailyTotal + amount) <= limit;
    }

    @Override
    public double getDailyWithdrawTotal(User user) {
        Date startOfDay = getStartOfDay();
        Double primaryTotal = primaryTransactionDao.sumAmountByTypeAndDateAfter(
                user.getPrimaryAccount(), "Withdraw", startOfDay);
        Double savingsTotal = savingsTransactionDao.sumAmountByTypeAndDateAfter(
                user.getSavingsAccount(), "Withdraw", startOfDay);
        return (primaryTotal != null ? primaryTotal : 0) + (savingsTotal != null ? savingsTotal : 0);
    }

    @Override
    public double getDailyTransferTotal(User user) {
        Date startOfDay = getStartOfDay();
        Double primaryTotal = primaryTransactionDao.sumAmountByTypeAndDateAfter(
                user.getPrimaryAccount(), "Transfer", startOfDay);
        Double savingsTotal = savingsTransactionDao.sumAmountByTypeAndDateAfter(
                user.getSavingsAccount(), "Transfer", startOfDay);
        return (primaryTotal != null ? primaryTotal : 0) + (savingsTotal != null ? savingsTotal : 0);
    }

    @Override
    public void updateDailyLimits(User user, Double withdrawLimit, Double transferLimit) {
        if (withdrawLimit != null) {
            user.setDailyWithdrawLimit(withdrawLimit);
        }
        if (transferLimit != null) {
            user.setDailyTransferLimit(transferLimit);
        }
        userDao.save(user);
        activityLogService.logActivity(user, "SETTINGS_UPDATE", "Updated daily transaction limits", null, "SUCCESS");
    }

    private Date getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private int accountGen() {
        return ++nextAccountNumber;
    }
}
