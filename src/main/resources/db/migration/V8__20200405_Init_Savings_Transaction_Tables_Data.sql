--
-- Table structure for table savings_transaction
--

DROP TABLE IF EXISTS savings_transaction;
CREATE TABLE savings_transaction
(
    id                 bigint(20) NOT NULL AUTO_INCREMENT,
    amount             double NOT NULL,
    available_balance  decimal(19, 2) DEFAULT NULL,
    date               datetime       DEFAULT NULL,
    description        varchar(255)   DEFAULT NULL,
    status             varchar(255)   DEFAULT NULL,
    type               varchar(255)   DEFAULT NULL,
    savings_account_id bigint(20) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY                FK4bt1l2090882974glyn79q2s9(savings_account_id),
    CONSTRAINT FK4bt1l2090882974glyn79q2s9 FOREIGN KEY (savings_account_id) REFERENCES savings_account (id)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

--
-- Dumping data for table savings_transaction
--

LOCK TABLES savings_transaction WRITE;
INSERT INTO savings_transaction
VALUES (1, 1000, 1000.00, '2017-01-13 00:57:40', 'Deposit to savings Account', 'Finished', 'Account', 1),
       (2, 150, 2150.00, '2017-01-13 01:11:15', 'Withdraw from savings Account', 'Finished', 'Account', 1),
       (3, 400, 1750.00, '2017-01-13 01:11:23', 'Withdraw from savings Account', 'Finished', 'Account', 1),
       (4, 2000, 3750.00, '2017-01-13 01:11:30', 'Deposit to savings Account', 'Finished', 'Account', 1),
       (5, 1500, 2250.00, '2017-01-13 01:13:38', 'Between account transfer from Savings to Primary', 'Finished',
        'Transfer', 1),
       (6, 300, 4250.00, '2017-01-13 01:14:02', 'Transfer to recipient LtdFitness', 'Finished', 'Transfer', 1);
UNLOCK TABLES;

