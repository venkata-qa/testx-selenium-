@backend
Feature: data length

    #Sample Script to create database,table. and insert record.
    #CREATE DATABASE `testX` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
    #CREATE TABLE `reviews` (`id` varchar(50) NOT NULL,`email` varchar(50) NOT NULL,`date` datetime DEFAULT NULL,`price` varchar(50) DEFAULT NULL,`id_number` varchar(50) DEFAULT NULL,`description` varchar(50) DEFAULT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    #INSERT INTO `testx`.`reviews`(`id`,`email`,`date`,`price`,`id_number`,`description`) VALUES(1,'example1user@email.com','2023-08-15 07:44:08',4.00,1,'First book is pretty good book overall');
    #INSERT INTO `testx`.`reviews`(`id`,`email`,`date`,`price`,`id_number`,`description`) VALUES(2,'example2user@email.com','2023-08-15 07:44:08',4.50,2,'Second books is pretty good book overall');

  Scenario Outline:S1-Validate the length of all columns data in a file
    When I validate the data length for all columns of feed file
      | fileName   | minLength   | maxLength   | fileColumnIndex   | valueType   |
      | <fileName> | <minLength> | <maxLength> | <fileColumnIndex> | <valueType> |
    When I validate the data length for all columns of feed file
      | fileName   | minLength   | maxLength   | fileColumnIndex   | valueType   |
      | <fileName> | <minLength> | <maxLength> | <fileColumnIndex> | <valueType> |
    Examples:
      | fileName | minLength | maxLength | fileColumnIndex | valueType |
      | reviews  | 5         | 30        | 1               | Required  |
      | reviews  | 5         | 30        | 2               | Optional  |

  Scenario Outline:S2-Validate the length of all columns data in a table
    When I validate the data length for all columns of table
      | tableName   | minLength   | maxLength   | columnName   | valueType   |
      | <tableName> | <minLength> | <maxLength> | <columnName> | <valueType> |

    Examples:
      | tableName     | minLength | maxLength | columnName | valueType |
      | testx.reviews | 1         | 30        | id         | Required  |
      | testx.reviews       | 1         | 30        | price      | Optional  |

