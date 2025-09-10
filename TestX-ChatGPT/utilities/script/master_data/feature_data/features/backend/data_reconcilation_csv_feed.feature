@CSVTest
Feature: User Should be able to compare data

  @CSVTest
  Scenario:S1-E to E-User Should be able validate every record from CSV  files to db
    When I verify all record of csv feed file to db
      | fileName    | tableName |
      | reviews.csv | reviews   |

  @CSVTest
  Scenario:S2-E to E-User Should be able validate every record count
    When I verify the record count of csv feed file to db
      | fileName    | tableName |
      | reviews.csv | reviews   |

  @CSVTest
  Scenario:S3-E to E-User Should be able validate specific record count
    When I verify specific record of csv feed file to db
      | fileName    | tableName | filterKey | filterValue |
      | reviews.csv | reviews   | id        | 1           |

  @CSVTest
  Scenario:S4-E to E-User Should be able validate misiing and extra
    When   I find missing and extra records in db or csv feed files
      | fileName    | tableName |
      | reviews.csv | reviews   |

  @CSVTest
  Scenario:S5-E to E-User Should be able validate max
    When   Verify avg value of a specific column with csv feed
      | fileName    | tableName | columnName | columnIndex |
      | reviews.csv | reviews   | id         | 0           |

  @CSVTest
  Scenario:S6-E to E-User Should be able validate max
    When   Verify max value of a specific column with csv feed
      | fileName    | tableName | columnName | columnIndex |
      | reviews.csv | reviews   | id         | 0           |

  @CSVTest
  Scenario:S7-E to E-User Should be able validate max
    When   Verify min value of a specific column with csv feed
      | fileName    | tableName | columnName | columnIndex |
      | reviews.csv | reviews   | id         | 0           |

  @CSVTest
  Scenario:S8-E to E-User Should be able validate max
    When   Verify sum value of a specific column with csv feed
      | fileName    | tableName | columnName | columnIndex |
      | reviews.csv | reviews   | id         | 0           |




