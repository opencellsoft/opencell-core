@settings
Feature: Create/Update Calendar by API

  Background: System is configured.

  @admin @superadmin
  Scenario Outline: <status> <action> Calendar by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The calendar is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                          | dto         | api                      | action         | statusCode | status  | errorCode                        | message                                                                           |
      | api/settings/00003-calendar-api-create/SuccessTest.json           | CalendarDto | /calendar/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                   |
      | api/settings/00003-calendar-api-create/SuccessTest.json           | CalendarDto | /calendar/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Calendar with code=TEST already exists.                                           |
      | api/settings/00003-calendar-api-create/DO_NOT_EXIST.json          | CalendarDto | /calendar/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Calendar with code=NOT_EXIST does not exists.                                     |
      | api/settings/00003-calendar-api-create/SuccessTest1.json          | CalendarDto | /calendar/               | Update         |        200 | SUCCESS |                                  |                                                                                   |
      | api/settings/00003-calendar-api-create/SuccessTest1.json          | CalendarDto | /calendar/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                   |
      | api/settings/00003-calendar-api-create/MISSING_PARAMETER.json     | CalendarDto | /calendar/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: calendarType.    |
      | api/settings/00003-calendar-api-create/GENERIC_API_EXCEPTION.json | CalendarDto | /calendar/createOrUpdate | CreateOrUpdate |        500 | FAIL    | GENERIC_API_EXCEPTION            | query did not return a unique result                                              |
      | api/settings/00003-calendar-api-create/INVALID_PARAMETER.json     | CalendarDto | /calendar/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.api.dto.CalendarTypeEnum` from String |
