package com.hendisantika.onlinebanking.service;

import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;

import java.util.Date;
import java.util.List;

public interface StatementService {

    byte[] generatePrimaryAccountStatement(User user, Date startDate, Date endDate);

    byte[] generateSavingsAccountStatement(User user, Date startDate, Date endDate);

    List<PrimaryTransaction> searchPrimaryTransactions(User user, Date startDate, Date endDate, String type, String description);

    List<SavingsTransaction> searchSavingsTransactions(User user, Date startDate, Date endDate, String type, String description);
}
