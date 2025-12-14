package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot")
    public String showForgotPasswordForm() {
        return "forgotPassword";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        passwordResetService.createPasswordResetTokenForEmail(email);
        redirectAttributes.addFlashAttribute("message",
                "If an account exists with that email, a password reset link has been sent.");
        return "redirect:/password/forgot";
    }

    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        if (!passwordResetService.validatePasswordResetToken(token)) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "resetPassword";
        }
        model.addAttribute("token", token);
        return "resetPassword";
    }

    @PostMapping("/reset")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/password/reset?token=" + token;
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long.");
            return "redirect:/password/reset?token=" + token;
        }

        try {
            passwordResetService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please log in.");
            return "redirect:/index";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/password/reset?token=" + token;
        }
    }
}
