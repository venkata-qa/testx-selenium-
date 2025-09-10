@backend
Feature: User Should be able to compare data in two DB tables

  @DBToDB
  Scenario:S1-E to E 3 -Target table to View table all records verification
    When I compare all records of target table with view table
      | sourceQuery                 | targetQuery                 |
      | select * from testx.reviews | select * from testx.reviews |

  @DBToDB
  Scenario:S2-Table soring based on column
    When I validated records are sorted on the basis of "id" in table "reviews"