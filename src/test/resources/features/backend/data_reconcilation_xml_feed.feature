@backend
Feature: User Should be able to compare data b/w xml file and DB

  Scenario Outline:S1-Data compare b/w xml file and data base for all records
    When I verify all record of downstream xml feed file to database
      | fileName   | tableName   |
      | <fileName> | <tableName> |
    Examples:
      | fileName    | tableName |
      | reviews.xml | testx.reviews   |

  Scenario:S2-Specific column comparison b/w xml file and DB
    When Verify specific column data for all records for xml downstream
      | fileName    | tableName | dbColumnName | fileColumnName |
      | reviews.xml | testx.reviews   | id           | id             |

  Scenario:S3-Compare sum of specific column b/w xml file and DB
    When Verify sum value of a specific column with xml feed for downstream
      | fileName    | tableName | dbColumnName | fileColumnName |
      | reviews.xml | testx.reviews   | id           | id             |

  Scenario:S4-Compare avg of specific column b/w xml file and DB
    When Verify avg value of a specific column with xml feed for downstream
      | fileName    | tableName | dbColumnName | fileColumnName |
      | reviews.xml | testx.reviews   | id           | id             |

  Scenario:S5-Compare min of specific column b/w xml file and DB
    When Verify min value of a specific column with xml feed for downstream
      | fileName    | tableName | dbColumnName | fileColumnName |
      | reviews.xml | testx.reviews   | id           | id             |

  Scenario:S6-Compare max of specific column b/w xml file and DB
    When Verify max value of a specific column with xml feed for downstream
      | fileName    | tableName | dbColumnName | fileColumnName |
      | reviews.xml | testx.reviews   | id           | id             |