package com.hendisantika.onlinebanking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String activityType;

    @Column(length = 500)
    private String description;

    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String status;

    public ActivityLog() {}

    public ActivityLog(User user, String activityType, String description, String ipAddress, LocalDateTime timestamp, String status) {
        this.user = user;
        this.activityType = activityType;
        this.description = description;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static ActivityLogBuilder builder() {
        return new ActivityLogBuilder();
    }

    public static class ActivityLogBuilder {
        private User user;
        private String activityType;
        private String description;
        private String ipAddress;
        private LocalDateTime timestamp;
        private String status;

        public ActivityLogBuilder user(User user) { this.user = user; return this; }
        public ActivityLogBuilder activityType(String activityType) { this.activityType = activityType; return this; }
        public ActivityLogBuilder description(String description) { this.description = description; return this; }
        public ActivityLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public ActivityLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public ActivityLogBuilder status(String status) { this.status = status; return this; }
        public ActivityLog build() {
            return new ActivityLog(user, activityType, description, ipAddress, timestamp, status);
        }
    }
}
