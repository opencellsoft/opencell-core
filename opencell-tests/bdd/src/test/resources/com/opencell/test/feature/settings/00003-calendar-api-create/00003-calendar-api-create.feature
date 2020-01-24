@settings
Feature: Create Calendar by API

  Background: System is configured.

  @admin @superadmin
  Scenario Outline: Create Calendar by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The calendar is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto         | api                      | statusCode | status  | errorCode                       | message                                                                           |
      | settings/00003-calendar-api-create/SuccessTest.json           | CalendarDto | /calendar/createOrUpdate |        200 | SUCCESS |                                 |                                                                                   |
      | settings/00003-calendar-api-create/SuccessTest.json           | CalendarDto | /calendar/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | Calendar with code=TEST already exists.                                           |
      | settings/00003-calendar-api-create/SuccessTest1.json          | CalendarDto | /calendar/createOrUpdate |        200 | SUCCESS |                                 |                                                                                   |
      | settings/00003-calendar-api-create/MISSING_PARAMETER.json     | CalendarDto | /calendar/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: calendarType.    |
      | settings/00003-calendar-api-create/GENERIC_API_EXCEPTION.json | CalendarDto | /calendar/createOrUpdate |        500 | FAIL    | GENERIC_API_EXCEPTION           | query did not return a unique result                                              |
      | settings/00003-calendar-api-create/INVALID_PARAMETER.json     | CalendarDto | /calendar/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `org.meveo.api.dto.CalendarTypeEnum` from String |
