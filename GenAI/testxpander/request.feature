Feature: Validate Personal Information Page

Scenario: Fill out the personal information form
    Given open "onboarding" page
    When enter "first name" on "First Name"
    And enter "last name" on "Last Name"
    And select "gender" on "Gender"
    And enter "date of birth" on "Date of Birth"
    And enter "email address" on "Email"
    And enter "phone number" on "Phone Number"
    And enter "address" on "Address"
    And click on the submit button
    Then validate "success" message
    Then register as light user on application
    Then click save progress button
    Then verify Need some help text
    Then verify feedback section displayed
    Then run accessibility on feedback