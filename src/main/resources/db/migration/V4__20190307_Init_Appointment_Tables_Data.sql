-- CREATE DATABASE  IF NOT EXISTS onlinebanking
-- USE onlinebanking;
--
-- Table structure for table appointment
--

DROP TABLE IF EXISTS appointment;
CREATE TABLE appointment
(
    id          bigint(20) NOT NULL AUTO_INCREMENT,
    confirmed   bit(1) NOT NULL,
    date        datetime     DEFAULT NULL,
    description varchar(255) DEFAULT NULL,
    location    varchar(255) DEFAULT NULL,
    user_id     bigint(20) DEFAULT NULL,
    PRIMARY KEY (id),
    INDEX       appointment_id_idx(user_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;

ALTER TABLE appointment
    ADD CONSTRAINT user_id
        FOREIGN KEY (user_id) REFERENCES user (user_id)
            ON UPDATE CASCADE ON DELETE CASCADE;

--
-- Dumping data for table appointment
--

LOCK TABLES appointment WRITE;
INSERT INTO appointment
VALUES (1, '', '2017-01-25 14:01:00', 'Want to see someone', 'Indonesia', 1),
       (2, '\0', '2017-01-30 15:01:00', 'Take credit', 'Indonesia', 1),
       (3, '', '2017-02-16 15:02:00', 'Consultation', 'Indonesia', 1);
UNLOCK TABLES;