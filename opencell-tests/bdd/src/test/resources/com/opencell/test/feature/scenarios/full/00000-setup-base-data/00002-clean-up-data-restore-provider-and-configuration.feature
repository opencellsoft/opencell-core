@full
Feature: Setup base data - Clean up data - restore provider and configuration

  @admin @superadmin
  Scenario Outline: Update provider
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The provider is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                    | dto         | api        | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/clean-up-data-restore-provider-and-configuration/restore_provider.json | ProviderDto | /provider/ | PUT    |        200 | SUCCESS |           |         |

  Scenario Outline: Set securedEntities=false
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                             | dto         | api                         | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/clean-up-data-restore-provider-and-configuration/set_securedEntities=false.json | ProviderDto | /configurations/setProperty | POST   |        200 | SUCCESS |           |         |
