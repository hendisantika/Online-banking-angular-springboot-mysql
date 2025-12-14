package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.ActivityLog;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.ActivityLogDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.service.ActivityLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogDao activityLogDao;
    private final UserDao userDao;

    public ActivityLogServiceImpl(ActivityLogDao activityLogDao, UserDao userDao) {
        this.activityLogDao = activityLogDao;
        this.userDao = userDao;
    }

    @Override
    public void logActivity(User user, String activityType, String description, String ipAddress, String status) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .activityType(activityType)
                .description(description)
                .ipAddress(ipAddress)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
        activityLogDao.save(log);
    }

    @Override
    public void logActivity(String username, String activityType, String description, String ipAddress, String status) {
        User user = userDao.findByUsername(username);
        if (user != null) {
            logActivity(user, activityType, description, ipAddress, status);
        }
    }

    @Override
    public List<ActivityLog> getUserActivityLogs(User user) {
        return activityLogDao.findByUserOrderByTimestampDesc(user);
    }

    @Override
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogDao.findAllByOrderByTimestampDesc();
    }

    @Override
    public List<ActivityLog> getActivityLogsByType(String activityType) {
        return activityLogDao.findByActivityTypeOrderByTimestampDesc(activityType);
    }
}
