--
-- Table structure for table role
--

DROP TABLE IF EXISTS role;
CREATE TABLE role
(
    role_id int(11) NOT NULL,
    name    varchar(255) DEFAULT NULL,
    PRIMARY KEY (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table role
--

LOCK TABLES ROLE WRITE;
INSERT INTO role
VALUES (0, 'ROLE_USER'),
       (1, 'ROLE_ADMIN');
UNLOCK TABLES;

