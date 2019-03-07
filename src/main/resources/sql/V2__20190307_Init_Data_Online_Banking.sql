--
-- Dumping data for table `appointment`
--

INSERT INTO `appointment` VALUES
(1,'','2019-01-25 14:01:00','Want to see someone','Jakarta',1),
(2,'\0','2019-01-30 15:01:00','Take credit','Bandung',1),
(3,'','2019-02-16 15:02:00','Consultation','Cimahi',1);
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `primary_account`
--

LOCK TABLES `primary_account` WRITE;
/*!40000 ALTER TABLE `primary_account` DISABLE KEYS */;
INSERT INTO `primary_account` VALUES (1,1700.00,11223146),(2,0.00,11223150);
/*!40000 ALTER TABLE `primary_account` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `primary_transaction`
--

LOCK TABLES `primary_transaction` WRITE;
/*!40000 ALTER TABLE `primary_transaction` DISABLE KEYS */;
INSERT INTO `primary_transaction` VALUES (1,5000,5000.00,'2019-01-13 00:57:16','Deposit to Primary Account','Finished','Account',1),
(2,1500,3500.00,'2019-01-13 00:57:31','Withdraw from Primary Account','Finished','Account',1),
(3,1300,2200.00,'2019-01-13 00:58:03','Between account transfer from Primary to Savings','Finished','Account',1),
(4,500,1700.00,'2019-01-13 00:59:08','Transfer to recipient Mr. Tomson','Finished','Transfer',1),
(5,1500,3200.00,'2019-01-13 01:11:38','Deposit to Primary Account','Finished','Account',1),
(6,400,2800.00,'2019-01-13 01:11:46','Withdraw from Primary Account','Finished','Account',1),
(7,2300,2000.00,'2019-01-13 01:13:48','Between account transfer from Primary to Savings','Finished','Account',1),
(8,300,1700.00,'2019-01-13 01:14:14','Transfer to recipient TaxSystem','Finished','Transfer',1);
/*!40000 ALTER TABLE `primary_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `recipient`
--

LOCK TABLES `recipient` WRITE;
/*!40000 ALTER TABLE `recipient` DISABLE KEYS */;
INSERT INTO `recipient` VALUES
(1,'213425635454','Rent payment','tomson@gmail.com','Mr. Tomson','1112223333',1),
(2,'453452341324','Gym payment','fitness@gmail.com','LtdFitness','323245345',1),
(3,'5465464234542','Tax payment 20%','taxes@mail.fi','TaxSystem','34254353',1);
/*!40000 ALTER TABLE `recipient` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (0,'ROLE_USER'),(1,'ROLE_ADMIN');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `savings_account`
--

LOCK TABLES `savings_account` WRITE;
/*!40000 ALTER TABLE `savings_account` DISABLE KEYS */;
INSERT INTO `savings_account` VALUES (1,4250.00,11223147),(2,0.00,11223151);
/*!40000 ALTER TABLE `savings_account` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Dumping data for table `savings_transaction`
--

LOCK TABLES `savings_transaction` WRITE;
/*!40000 ALTER TABLE `savings_transaction` DISABLE KEYS */;
INSERT INTO `savings_transaction` VALUES
(1,1000,1000.00,'2019-01-13 00:57:40','Deposit to savings Account','Finished','Account',1),
(2,150,2150.00,'2019-01-13 01:11:15','Withdraw from savings Account','Finished','Account',1),
(3,400,1750.00,'2019-01-13 01:11:23','Withdraw from savings Account','Finished','Account',1),
(4,2000,3750.00,'2019-01-13 01:11:30','Deposit to savings Account','Finished','Account',1),
(5,1500,2250.00,'2019-01-13 01:13:38','Between account transfer from Savings to Primary','Finished','Transfer',1),
(6,300,4250.00,'2019-01-13 01:14:02','Transfer to recipient LtdFitness','Finished','Transfer',1);
/*!40000 ALTER TABLE `savings_transaction` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'user@gmail.com','','User','Online','$2a$12$DWCryEwHwbTYCegib92tk.VST.GG1i2WAqfaSwyMdxX0cl0eBeSve','5551112345','User',1,1),
(2,'admin@gmail.com','','Admin','Online','$2a$12$hZR7pcSf0JU/OTXR3TOyuu8r6C6n.JZE8Ei47E4LZs1t0Aq1AO6oC','1111111111','Admin',2,2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
INSERT INTO `user_role` VALUES (1,0,1),(2,1,2);
UNLOCK TABLES;