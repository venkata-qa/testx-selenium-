@database_creation
Feature: Database steps

  Scenario:S1- Setting up of database,table,actions on the table(Pre-requisit for next scenario)
    Given I create database "Autodb"
    And I establish database connection
      | driverName               | db_url                             | username | password |
      | com.mysql.cj.jdbc.Driver | jdbc:mysql://localhost:3306/Autodb | root     | root     |
    When I create table "Student" in database
      | columnDetails                                                                                                       |
      | id varchar(50), StudentName varchar(50),Address varchar(50),FatherName varchar(50),Fee varchar(50),PRIMARY KEY (id) |
    Then I execute the query
      | queryType | selectedColumnName | tableName | condition | setColumnValue | columnName                               | columnValue                            | orderBy |
      | insert    |                    | Student  |           |                | id, StudentName, Address, FatherName,Fee | "101","test1","Noida","DJ","20000"     |         |
      | insert    |                    | Student  |           |                | id, StudentName, Address, FatherName,Fee | "102","test2","UK","Ram","22000"      |         |
      | insert    |                    | Student  |           |                | id, StudentName, Address, FatherName,Fee | "103","test3","Gurgaon","Dev","12000" |         |
      | insert    |                    | Student  |           |                | id, StudentName, Address, FatherName,Fee | "104","test4","Jaipur","Ravi","62000" |         |
    And I execute the query
      | queryType | selectedColumnName | tableName | condition | setColumnValue      | columnName | columnValue | orderBy |
      | update    |                    | Student  | id='104'  | StudentName="test4" |            |             |         |
    And I execute the query
      | queryType | selectedColumnName | tableName | condition | setColumnValue | columnName | columnValue | orderBy |
      | delete    |                    | Student  | id='102'  |                |            |             |         |
    And I close database connection


  Scenario:S2- Connect db and provide Database details here and hit database query
    Given I establish database connection
      | driverName               | db_url                                      | username | password |
      | com.mysql.cj.jdbc.Driver | jdbc:mysql://localhost:3306/Autodb | root     | root     |
#    When I select schema name as "Test"
    And I execute the query "select StudentName from Student where id='101'"
    Then I save database result
      | columnName  |
      | StudentName |
    And I close database connection
    And I collect "string" format data
      | keyName     |
      | StudentName |


  Scenario:S3- Connect db and provide Database details from properties file and hit database query
    Given I establish the database connection
#    And I select the schema name
    And I execute the query
      | queryType | selectedColumnName | tableName | condition | setColumnValue | columnName | columnValue | orderBy |
      | select    | StudentName        | Student   | id='101'  |                |            |             |         |
    And I save database result
      | columnName  |
      | StudentName |
    And I close database connection
    And I collect "string" format data
      | keyName     |
      | StudentName |

