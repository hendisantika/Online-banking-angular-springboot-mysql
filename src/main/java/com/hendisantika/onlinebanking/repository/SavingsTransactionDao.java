package com.hendisantika.onlinebanking.repository;

import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SavingsTransactionDao extends JpaRepository<SavingsTransaction, Long> {

    List<SavingsTransaction> findAll();

    List<SavingsTransaction> findBySavingsAccountOrderByDateDesc(SavingsAccount savingsAccount);

    List<SavingsTransaction> findBySavingsAccountAndDateBetweenOrderByDateDesc(
            SavingsAccount savingsAccount, Date startDate, Date endDate);

    List<SavingsTransaction> findBySavingsAccountAndTypeContainingIgnoreCaseOrderByDateDesc(
            SavingsAccount savingsAccount, String type);

    List<SavingsTransaction> findBySavingsAccountAndDescriptionContainingIgnoreCaseOrderByDateDesc(
            SavingsAccount savingsAccount, String description);

    @Query("SELECT st FROM SavingsTransaction st WHERE st.savingsAccount = :account " +
           "AND (:startDate IS NULL OR st.date >= :startDate) " +
           "AND (:endDate IS NULL OR st.date <= :endDate) " +
           "AND (:type IS NULL OR LOWER(st.type) LIKE LOWER(CONCAT('%', :type, '%'))) " +
           "AND (:description IS NULL OR LOWER(st.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
           "ORDER BY st.date DESC")
    List<SavingsTransaction> searchTransactions(
            @Param("account") SavingsAccount account,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("type") String type,
            @Param("description") String description);

    @Query("SELECT COALESCE(SUM(st.amount), 0) FROM SavingsTransaction st " +
           "WHERE st.savingsAccount = :account AND st.type = :type AND st.date >= :startDate")
    Double sumAmountByTypeAndDateAfter(
            @Param("account") SavingsAccount account,
            @Param("type") String type,
            @Param("startDate") Date startDate);
}