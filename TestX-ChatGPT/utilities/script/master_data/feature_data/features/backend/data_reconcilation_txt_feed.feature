@textTest
Feature: User Should be able to compare data

  Scenario:S1-verify
    When  I compare all records of feed files with db
      | fileName    | tableName |
      | reviews.txt | reviews   |

  Scenario:S2-verify avg
    When  Verify avg value of a specific column in feed file
      | fileName    | tableName | columnIndex | columnName |
      | reviews.txt | reviews   | 0           | id         |

  Scenario:S3-verify
    When  Verify sum value of a specific column in feed file
      | fileName    | tableName | columnIndex | columnName |
      | reviews.txt | reviews   | 0           | id         |

  Scenario:S4-verify
    When  Verify max value of a specific column in feed file
      | fileName    | tableName | columnIndex | columnName |
      | reviews.txt | reviews   | 0           | id         |

  Scenario:S5-verify
    When  Verify min value of a specific column in feed file
      | fileName    | tableName | columnIndex | columnName |
      | reviews.txt | reviews   | 0           | id         |

  Scenario:S6-verify
    When I verify the record count of feed file to db
      | fileName    | tableName |
      | reviews.txt | reviews   |

  Scenario:S7-User Should be able to find missing records from feed files to db or viceversa
    When I find missing and extra records in db or feed file
      | fileName    | tableName |
      | reviews.txt | reviews   |

  Scenario:S8-User Should be able to find missing records from feed files to db or viceversa
    When   I verify the columns name and count of feed files with db
      | fileName    | tableName |
      | reviews.txt | reviews   |

  Scenario:S9-User Should be able to validate specific records of a user from feed files to db or viceversa
    When I compare specific record in feed and db
      | fileName    | tableName | filterKey | filterValue |
      | reviews.txt | reviews   | id        | 1           |