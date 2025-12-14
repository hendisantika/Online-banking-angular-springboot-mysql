package com.hendisantika.onlinebanking.repository;

import com.hendisantika.onlinebanking.entity.ActivityLog;
import com.hendisantika.onlinebanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogDao extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserOrderByTimestampDesc(User user);

    List<ActivityLog> findByUserAndTimestampBetweenOrderByTimestampDesc(User user, LocalDateTime start, LocalDateTime end);

    List<ActivityLog> findByActivityTypeOrderByTimestampDesc(String activityType);

    List<ActivityLog> findAllByOrderByTimestampDesc();
}
