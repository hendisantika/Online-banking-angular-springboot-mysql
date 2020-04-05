-- CREATE DATABASE  IF NOT EXISTS onlinebanking
-- USE onlinebanking;
--
-- Table structure for table `primary_account`
--

DROP TABLE IF EXISTS primary_account;
CREATE TABLE primary_account
(
    id              bigint(20) NOT NULL AUTO_INCREMENT,
    account_balance decimal(19, 2) DEFAULT NULL,
    account_number  int(11) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table primary_account
--

LOCK TABLES primary_account WRITE;
INSERT INTO primary_account
VALUES (1, 1700.00, 11223146),
       (2, 0.00, 11223150);
UNLOCK TABLES;