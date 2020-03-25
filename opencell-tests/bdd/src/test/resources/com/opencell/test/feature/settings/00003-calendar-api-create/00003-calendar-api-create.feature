@customers
Feature: Create a Calendar by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create a User Account by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The User account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto             | api                          | statusCode | status  | errorCode                        | message                                                                                |
      | customers/00004-Calendar-api-create/SuccessTest.json               | CalendarDto     | /calendar/createOrUpdate     |     200    | SUCCESS |                                  |                                                                                        |
      | customers/00004-userAccount-api-create/SuccessTest.json            | CalendarDto     | /calendar/createOrUpdate     |     200    | SUCCESS |                                  |                                                                                        |
      | customers/00004-userAccount-api-create/MISSING_PARAMETER.json      | CalendarDto     | /calendar/createOrUpdate     |     400    | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: calendarType.         |
      | customers/00004-userAccount-api-create/INVALID_PARAMETER_Type.json | CalendarDto     | /calendar/createOrUpdate     |     400    | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.api.dto.CalendarTypeEnum` from String      |
      | customers/00004-userAccount-api-create/INVALID_PARAMETER_Month.json| CalendarDto     | /calendar/createOrUpdate     |     404    | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.catalog.MonthEnum                    |
