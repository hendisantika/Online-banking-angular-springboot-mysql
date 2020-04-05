--
-- Table structure for table savings_account
--

DROP TABLE IF EXISTS savings_account;
CREATE TABLE savings_account
(
    id              bigint(20) NOT NULL AUTO_INCREMENT,
    account_balance decimal(19, 2) DEFAULT NULL,
    account_number  int(11) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table savings_account
--

LOCK TABLES savings_account WRITE;
INSERT INTO savings_account
VALUES (1, 4250.00, 11223147),
       (2, 0.00, 11223151);
UNLOCK TABLES;

