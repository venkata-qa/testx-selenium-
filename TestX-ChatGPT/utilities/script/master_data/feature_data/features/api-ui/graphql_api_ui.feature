@test101
Feature: To Validate the API data to UI data

  Scenario Outline:S1-User Should be able to Validate the API data to UI data
    Given I have API "<API>"
    And  I set content-type as JSON
    And  I set request body for "<RequestBody>"
    When I call method 'POST'
    Then I get the response
    Then I save the initial response for "<API>"
    Then I verify the data of below element to actual "<API>" data on the "HomePage" page
      | apiPath                         | uiElement |
      | data.allFilms.films[0].director | uiData    |
    Examples:
      | API           | RequestBody |
      | get_all_films | api_data    |

