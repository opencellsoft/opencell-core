@settings
Feature: Create, modify and delete Title and civility

  Background: System is configured.
    Titles API create is already executed.


  @admin @superadmin
  Scenario Outline: <status> <action> title and civilities by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                               | dto      | api             | action | statusCode | status  | errorCode                        | message                                    |
      | api/settings/00002-titles-api-create/SuccessTest.json  | TitleDto | /account/title/ | Delete |        200 | SUCCESS |                                  |                                            |
      | api/settings/00002-titles-api-create/DO_NOT_EXIST.json | TitleDto | /account/title/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Title with code=NOT_EXIST does not exists. |
