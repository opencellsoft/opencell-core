@full
Feature: Setup base data - Test base data

  @admin @superadmin
  Scenario Outline: <entity>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                    | entity            | dto            | api               | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/test-base-data/list_occ_templates.json | List occ template | OccTemplateDto | /occTemplate/list | POST   |        200 | SUCCESS |           |         |
