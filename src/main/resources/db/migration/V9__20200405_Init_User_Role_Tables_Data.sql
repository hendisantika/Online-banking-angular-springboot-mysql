--
-- Table structure for table user_role
--

DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role
(
    user_role_id bigint(20) NOT NULL AUTO_INCREMENT,
    role_id      int(11) DEFAULT NULL,
    user_id      bigint(20) DEFAULT NULL,
    PRIMARY KEY (user_role_id),
    KEY          FKa68196081fvovjhkek5m97n3y(role_id),
    KEY          FK859n2jvi8ivhui0rl0esws6o(user_id),
    CONSTRAINT FK859n2jvi8ivhui0rl0esws6o FOREIGN KEY (user_id) REFERENCES user (user_id),
    CONSTRAINT FKa68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES role (role_id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table user_role
--

LOCK TABLES user_role WRITE;
INSERT INTO user_role
VALUES (1, 0, 1),
       (2, 1, 2);
UNLOCK TABLES;