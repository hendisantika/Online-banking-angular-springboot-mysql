package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String profilePost(@ModelAttribute("user") User newUser, Model model) {
        User user = userService.findByUsername(newUser.getUsername());
        user.setUsername(newUser.getUsername());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPhone(newUser.getPhone());

        model.addAttribute("user", user);

        userService.saveUser(user);

        return "profile";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("dailyWithdrawTotal", accountService.getDailyWithdrawTotal(user));
        model.addAttribute("dailyTransferTotal", accountService.getDailyTransferTotal(user));
        return "settings";
    }

    @PostMapping("/settings")
    public String updateSettings(
            @RequestParam(required = false) Double dailyWithdrawLimit,
            @RequestParam(required = false) Double dailyTransferLimit,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(principal.getName());
        accountService.updateDailyLimits(user, dailyWithdrawLimit, dailyTransferLimit);
        redirectAttributes.addFlashAttribute("message", "Settings updated successfully.");
        return "redirect:/user/settings";
    }
}
