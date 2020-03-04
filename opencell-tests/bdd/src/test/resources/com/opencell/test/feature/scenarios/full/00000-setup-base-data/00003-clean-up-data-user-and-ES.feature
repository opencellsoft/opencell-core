@full
Feature: Setup base data - Clean up data - Clean up data - user and ES

  @admin @superadmin
  Scenario Outline: Restore User to default
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is updated
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                         | dto     | api    | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/clean-up-data-user-and-ES/restore_user.json | UserDto | /user/ | PUT    |        200 | SUCCESS |           |         |

  Scenario Outline: Clean and reindex Full text - AUTHORIZATION_SUPERADMIN
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The action is completed
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                                 | api                   | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/clean-up-data-user-and-ES/clean-and-reindex-full-text-AUTHORIZATION_SUPERADMIN.json | /filteredList/reindex | GET    |        200 | SUCCESS |           |         |
