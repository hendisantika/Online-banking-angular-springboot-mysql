package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.ActivityLog;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.ActivityLogService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/activity")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final UserService userService;

    public ActivityLogController(ActivityLogService activityLogService, UserService userService) {
        this.activityLogService = activityLogService;
        this.userService = userService;
    }

    @GetMapping
    public String viewActivityLog(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<ActivityLog> activityLogs = activityLogService.getUserActivityLogs(user);
        model.addAttribute("activityLogs", activityLogs);
        return "activityLog";
    }
}
