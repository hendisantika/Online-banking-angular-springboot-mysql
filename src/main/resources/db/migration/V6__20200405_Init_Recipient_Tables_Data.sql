--
-- Table structure for table recipient
--

DROP TABLE IF EXISTS recipient;
CREATE TABLE recipient
(
    id             bigint(20) NOT NULL AUTO_INCREMENT,
    account_number varchar(255) DEFAULT NULL,
    description    varchar(255) DEFAULT NULL,
    email          varchar(255) DEFAULT NULL,
    name           varchar(255) DEFAULT NULL,
    phone          varchar(255) DEFAULT NULL,
    user_id        bigint(20) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY            FK3041ks22uyyuuw441k5671ah9(user_id),
    CONSTRAINT FK3041ks22uyyuuw441k5671ah9 FOREIGN KEY (user_id) REFERENCES user (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table recipient
--

LOCK TABLES recipient WRITE;
INSERT INTO recipient
VALUES (1, '213425635454', 'Rent payment', 'tomson@gmail.com', 'Mr. Tomson', '1112223333', 1),
       (2, '453452341324', 'Gym payment', 'fitness@gmail.com', 'LtdFitness', '323245345', 1),
       (3, '5465464234542', 'Tax payment 20%', 'taxes@mail.fi', 'TaxSystem', '34254353', 1);
UNLOCK TABLES;

