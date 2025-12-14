-- Add daily limits columns to user table
ALTER TABLE user ADD COLUMN daily_transfer_limit DOUBLE DEFAULT 10000.0;
ALTER TABLE user ADD COLUMN daily_withdraw_limit DOUBLE DEFAULT 5000.0;

-- Create activity_log table
CREATE TABLE activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_user_id BIGINT,
    activity_type VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    ip_address VARCHAR(50),
    timestamp DATETIME NOT NULL,
    status VARCHAR(20),
    FOREIGN KEY (user_user_id) REFERENCES user(user_id)
);

-- Create password_reset_token table
CREATE TABLE password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_activity_log_user ON activity_log(user_user_id);
CREATE INDEX idx_activity_log_timestamp ON activity_log(timestamp);
CREATE INDEX idx_password_reset_token_token ON password_reset_token(token);
CREATE INDEX idx_primary_transaction_date ON primary_transaction(date);
CREATE INDEX idx_savings_transaction_date ON savings_transaction(date);
