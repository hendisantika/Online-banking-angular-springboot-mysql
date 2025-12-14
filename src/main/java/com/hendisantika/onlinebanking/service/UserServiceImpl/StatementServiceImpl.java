package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryTransactionDao;
import com.hendisantika.onlinebanking.repository.SavingsTransactionDao;
import com.hendisantika.onlinebanking.service.StatementService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class StatementServiceImpl implements StatementService {

    private static final Logger log = LoggerFactory.getLogger(StatementServiceImpl.class);

    private final PrimaryTransactionDao primaryTransactionDao;
    private final SavingsTransactionDao savingsTransactionDao;

    public StatementServiceImpl(PrimaryTransactionDao primaryTransactionDao, SavingsTransactionDao savingsTransactionDao) {
        this.primaryTransactionDao = primaryTransactionDao;
        this.savingsTransactionDao = savingsTransactionDao;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat STATEMENT_DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");

    @Override
    public byte[] generatePrimaryAccountStatement(User user, Date startDate, Date endDate) {
        PrimaryAccount account = user.getPrimaryAccount();
        List<PrimaryTransaction> transactions = primaryTransactionDao
                .findByPrimaryAccountAndDateBetweenOrderByDateDesc(account, startDate, endDate);

        return generatePdfStatement(user, "Primary", account.getAccountNumber(),
                account.getAccountBalance().doubleValue(), transactions, null, startDate, endDate);
    }

    @Override
    public byte[] generateSavingsAccountStatement(User user, Date startDate, Date endDate) {
        SavingsAccount account = user.getSavingsAccount();
        List<SavingsTransaction> transactions = savingsTransactionDao
                .findBySavingsAccountAndDateBetweenOrderByDateDesc(account, startDate, endDate);

        return generatePdfStatement(user, "Savings", account.getAccountNumber(),
                account.getAccountBalance().doubleValue(), null, transactions, startDate, endDate);
    }

    @Override
    public List<PrimaryTransaction> searchPrimaryTransactions(User user, Date startDate, Date endDate,
                                                               String type, String description) {
        return primaryTransactionDao.searchTransactions(
                user.getPrimaryAccount(),
                startDate,
                endDate,
                type != null && !type.isEmpty() ? type : null,
                description != null && !description.isEmpty() ? description : null
        );
    }

    @Override
    public List<SavingsTransaction> searchSavingsTransactions(User user, Date startDate, Date endDate,
                                                               String type, String description) {
        return savingsTransactionDao.searchTransactions(
                user.getSavingsAccount(),
                startDate,
                endDate,
                type != null && !type.isEmpty() ? type : null,
                description != null && !description.isEmpty() ? description : null
        );
    }

    private byte[] generatePdfStatement(User user, String accountType, int accountNumber,
                                        double balance, List<PrimaryTransaction> primaryTransactions,
                                        List<SavingsTransaction> savingsTransactions,
                                        Date startDate, Date endDate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            Paragraph title = new Paragraph("ACCOUNT STATEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph bankName = new Paragraph("Online Banking System", headerFont);
            bankName.setAlignment(Element.ALIGN_CENTER);
            bankName.setSpacingAfter(30);
            document.add(bankName);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            addInfoRow(infoTable, "Account Holder:", user.getFirstName() + " " + user.getLastName(), normalFont);
            addInfoRow(infoTable, "Account Type:", accountType + " Account", normalFont);
            addInfoRow(infoTable, "Account Number:", String.valueOf(accountNumber), normalFont);
            addInfoRow(infoTable, "Current Balance:", String.format("$%.2f", balance), normalFont);
            addInfoRow(infoTable, "Statement Period:",
                    STATEMENT_DATE_FORMAT.format(startDate) + " - " + STATEMENT_DATE_FORMAT.format(endDate), normalFont);
            addInfoRow(infoTable, "Generated On:", STATEMENT_DATE_FORMAT.format(new Date()), normalFont);

            document.add(infoTable);

            Paragraph transactionsTitle = new Paragraph("Transaction History", headerFont);
            transactionsTitle.setSpacingBefore(20);
            transactionsTitle.setSpacingAfter(10);
            document.add(transactionsTitle);

            PdfPTable transactionTable = new PdfPTable(5);
            transactionTable.setWidthPercentage(100);
            transactionTable.setWidths(new float[]{2, 3, 1.5f, 1.5f, 2});

            addTableHeader(transactionTable, "Date");
            addTableHeader(transactionTable, "Description");
            addTableHeader(transactionTable, "Type");
            addTableHeader(transactionTable, "Amount");
            addTableHeader(transactionTable, "Balance");

            if (primaryTransactions != null) {
                for (PrimaryTransaction t : primaryTransactions) {
                    addTableCell(transactionTable, DATE_FORMAT.format(t.getDate()), normalFont);
                    addTableCell(transactionTable, t.getDescription(), normalFont);
                    addTableCell(transactionTable, t.getType(), normalFont);
                    addTableCell(transactionTable, String.format("$%.2f", t.getAmount()), normalFont);
                    addTableCell(transactionTable, String.format("$%.2f", t.getAvailableBalance()), normalFont);
                }
            }

            if (savingsTransactions != null) {
                for (SavingsTransaction t : savingsTransactions) {
                    addTableCell(transactionTable, DATE_FORMAT.format(t.getDate()), normalFont);
                    addTableCell(transactionTable, t.getDescription(), normalFont);
                    addTableCell(transactionTable, t.getType(), normalFont);
                    addTableCell(transactionTable, String.format("$%.2f", t.getAmount()), normalFont);
                    addTableCell(transactionTable, String.format("$%.2f", t.getAvailableBalance()), normalFont);
                }
            }

            document.add(transactionTable);

            Paragraph footer = new Paragraph(
                    "\n\nThis is a computer-generated statement and does not require a signature.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating PDF statement: {}", e.getMessage());
            throw new RuntimeException("Failed to generate statement", e);
        } catch (Exception e) {
            log.error("Error generating PDF statement: {}", e.getMessage());
            throw new RuntimeException("Failed to generate statement", e);
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(0);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(0);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        cell.setBackgroundColor(new Color(51, 122, 183));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
