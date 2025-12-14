package com.hendisantika.onlinebanking.repository;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PrimaryTransactionDao extends JpaRepository<PrimaryTransaction, Long> {

    List<PrimaryTransaction> findAll();

    List<PrimaryTransaction> findByPrimaryAccountOrderByDateDesc(PrimaryAccount primaryAccount);

    List<PrimaryTransaction> findByPrimaryAccountAndDateBetweenOrderByDateDesc(
            PrimaryAccount primaryAccount, Date startDate, Date endDate);

    List<PrimaryTransaction> findByPrimaryAccountAndTypeContainingIgnoreCaseOrderByDateDesc(
            PrimaryAccount primaryAccount, String type);

    List<PrimaryTransaction> findByPrimaryAccountAndDescriptionContainingIgnoreCaseOrderByDateDesc(
            PrimaryAccount primaryAccount, String description);

    @Query("SELECT pt FROM PrimaryTransaction pt WHERE pt.primaryAccount = :account " +
           "AND (:startDate IS NULL OR pt.date >= :startDate) " +
           "AND (:endDate IS NULL OR pt.date <= :endDate) " +
           "AND (:type IS NULL OR LOWER(pt.type) LIKE LOWER(CONCAT('%', :type, '%'))) " +
           "AND (:description IS NULL OR LOWER(pt.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
           "ORDER BY pt.date DESC")
    List<PrimaryTransaction> searchTransactions(
            @Param("account") PrimaryAccount account,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("type") String type,
            @Param("description") String description);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PrimaryTransaction pt " +
           "WHERE pt.primaryAccount = :account AND pt.type = :type AND pt.date >= :startDate")
    Double sumAmountByTypeAndDateAfter(
            @Param("account") PrimaryAccount account,
            @Param("type") String type,
            @Param("startDate") Date startDate);
}