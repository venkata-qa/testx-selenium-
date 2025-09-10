@setup
Feature: This test is a precondition, that will create DataBase, Table, and insert 2 records.

  Scenario: Create procedure
    When Create and execute procedure
    Then Delete the created procedure