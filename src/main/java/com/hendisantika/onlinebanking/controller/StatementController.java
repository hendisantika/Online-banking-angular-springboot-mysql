package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.StatementService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/statement")
public class StatementController {

    private final StatementService statementService;
    private final UserService userService;

    public StatementController(StatementService statementService, UserService userService) {
        this.statementService = statementService;
        this.userService = userService;
    }

    @GetMapping("/primary")
    public String showPrimaryStatementForm(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("accountType", "Primary");
        model.addAttribute("accountNumber", user.getPrimaryAccount().getAccountNumber());
        model.addAttribute("accountBalance", user.getPrimaryAccount().getAccountBalance());
        return "statementForm";
    }

    @GetMapping("/savings")
    public String showSavingsStatementForm(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("accountType", "Savings");
        model.addAttribute("accountNumber", user.getSavingsAccount().getAccountNumber());
        model.addAttribute("accountBalance", user.getSavingsAccount().getAccountBalance());
        return "statementForm";
    }

    @GetMapping("/primary/download")
    public ResponseEntity<byte[]> downloadPrimaryStatement(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());
        byte[] pdfContent = statementService.generatePrimaryAccountStatement(user, startDate, endDate);

        String filename = "primary_statement_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping("/savings/download")
    public ResponseEntity<byte[]> downloadSavingsStatement(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());
        byte[] pdfContent = statementService.generateSavingsAccountStatement(user, startDate, endDate);

        String filename = "savings_statement_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @PostMapping("/primary/search")
    public String searchPrimaryTransactions(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String description,
            Model model,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());

        if (startDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            startDate = cal.getTime();
        }
        if (endDate == null) {
            endDate = new Date();
        }

        List<PrimaryTransaction> transactions = statementService.searchPrimaryTransactions(
                user, startDate, endDate, type, description);

        model.addAttribute("primaryTransactionList", transactions);
        model.addAttribute("primaryAccount", user.getPrimaryAccount());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("searchType", type);
        model.addAttribute("searchDescription", description);
        model.addAttribute("showSearch", true);

        return "primaryAccount";
    }

    @PostMapping("/savings/search")
    public String searchSavingsTransactions(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String description,
            Model model,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());

        if (startDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            startDate = cal.getTime();
        }
        if (endDate == null) {
            endDate = new Date();
        }

        List<SavingsTransaction> transactions = statementService.searchSavingsTransactions(
                user, startDate, endDate, type, description);

        model.addAttribute("savingsTransactionList", transactions);
        model.addAttribute("savingsAccount", user.getSavingsAccount());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("searchType", type);
        model.addAttribute("searchDescription", description);
        model.addAttribute("showSearch", true);

        return "savingsAccount";
    }
}
