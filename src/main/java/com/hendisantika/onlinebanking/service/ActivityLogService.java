package com.hendisantika.onlinebanking.service;

import com.hendisantika.onlinebanking.entity.ActivityLog;
import com.hendisantika.onlinebanking.entity.User;

import java.util.List;

public interface ActivityLogService {

    void logActivity(User user, String activityType, String description, String ipAddress, String status);

    void logActivity(String username, String activityType, String description, String ipAddress, String status);

    List<ActivityLog> getUserActivityLogs(User user);

    List<ActivityLog> getAllActivityLogs();

    List<ActivityLog> getActivityLogsByType(String activityType);
}
