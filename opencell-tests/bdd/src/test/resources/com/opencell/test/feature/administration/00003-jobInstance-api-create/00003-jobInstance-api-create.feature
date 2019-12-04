@administration
Feature: Create Job Instance by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Job Instance by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The jobInstance is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                          | dto            | api                         | statusCode | status  | errorCode                        | message                                                                                    |
      | administration/00003-jobInstance-api-create/Success.json                          | JobInstanceDto | /jobInstance/createOrUpdate |        200 | SUCCESS |                                  |                                                                                            |
      | administration/00003-jobInstance-api-create/Success1.json                         | JobInstanceDto | /jobInstance/createOrUpdate |        200 | SUCCESS |                                  |                                                                                            |
      | administration/00003-jobInstance-api-create/MISSING_PARAMETER.json                | JobInstanceDto | /jobInstance/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: SepaJob_ddRequestBuilder. |
      | administration/00003-jobInstance-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | JobInstanceDto | /jobInstance/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | JobTemplate with code 'XXX' doesn't exist                                                  |
