package com.hendisantika.onlinebanking.service;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;

import java.security.Principal;

public interface AccountService {

    PrimaryAccount createPrimaryAccount();

    SavingsAccount createSavingsAccount();

    void deposit(String accountType, double amount, Principal principal);

    void withdraw(String accountType, double amount, Principal principal);

    boolean checkWithdrawLimit(User user, double amount);

    boolean checkTransferLimit(User user, double amount);

    double getDailyWithdrawTotal(User user);

    double getDailyTransferTotal(User user);

    void updateDailyLimits(User user, Double withdrawLimit, Double transferLimit);
}
