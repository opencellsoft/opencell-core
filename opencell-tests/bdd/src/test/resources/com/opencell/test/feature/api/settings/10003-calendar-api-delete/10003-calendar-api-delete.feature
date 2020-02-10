@settings
Feature: Delete Calendar by API

  Background: System is configured.
    Create Calendar by API already executed.


  @admin @superadmin
  Scenario Outline: <status> <action> Calendar by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                 | dto         | api        | action | statusCode | status  | errorCode                        | message                                       |
      | api/settings/00003-calendar-api-create/SuccessTest.json  | CalendarDto | /calendar/ | Delete |        200 | SUCCESS |                                  |                                               |
      | api/settings/00003-calendar-api-create/DO_NOT_EXIST.json | CalendarDto | /calendar/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Calendar with code=NOT_EXIST does not exists. |
