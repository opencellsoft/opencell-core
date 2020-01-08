@settings @ignore
Feature: Delete Calendar by API

  Background: System is configured.
    Create Calendar by API already executed.


  @admin @superadmin
  Scenario Outline: Delete Calendar by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto         | api        | statusCode | status  | errorCode                        | message                                       |
      | settings/00003-calendar-api-create/SuccessTest.json           | CalendarDto | /calendar/ |        200 | SUCCESS |                                  |                                               |
      | settings/10003-calendar-api-delete/ENTITY_DOES_NOT_EXIST.json | CalendarDto | /calendar/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Calendar with code=NOT_EXIST does not exists. |
