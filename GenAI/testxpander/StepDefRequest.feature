Scenario Outline: Fill out the personal information form
    Given I am on the "onboarding" page
    When I enter my first name as "<First Name>"
    And I enter my last name as "<Last Name>"
    And I select my gender as "<Gender>"
    And I enter my date of birth as "<Date of Birth>"
    And I enter my email address as "<Email>"
    And I enter my phone number as "<Phone Number>"
    And I enter my address as "<Address>"
    And I click on the submit button
    Then I should see a "success" message
    Then register as light user on application
    Then click save progress button
    Then verify Need some help text
    Then verify feedback section displayed
    Then run accessibility on feedback 

  Examples:
    | First Name | Last Name | Gender | Date of Birth | Email                | Phone Number | Address          |
    | John       | Doe       | Male   | 01/01/1990    | john.doe@example.com | 1234567890   | 123 Main Street  |
    | Jane       | Smith     | Female | 02/02/1995    | jane.smith@example.com| 9876543210   | 456 Elm Avenue   |