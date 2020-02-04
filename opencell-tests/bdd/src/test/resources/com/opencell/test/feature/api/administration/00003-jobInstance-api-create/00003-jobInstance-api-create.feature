@administration
Feature: Create/Update Job Instance by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> Job Instance by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The jobInstance is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                              | dto            | api                         | action         | statusCode | status  | errorCode                        | message                                                                                    |
      | api/administration/00003-jobInstance-api-create/Success.json                          | JobInstanceDto | /jobInstance/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                            |
      | api/administration/00003-jobInstance-api-create/Success.json                          | JobInstanceDto | /jobInstance/create         | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | JobInstance with code=TEST already exists.                                                 |
      | api/administration/00003-jobInstance-api-create/DO_NOT_EXIST.json                     | JobInstanceDto | /jobInstance/update         | Post           |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | JobInstance with code=NOT_EXIST does not exists.                                           |
      | api/administration/00003-jobInstance-api-create/Success1.json                         | JobInstanceDto | /jobInstance/update         | Post           |        200 | SUCCESS |                                  |                                                                                            |
      | api/administration/00003-jobInstance-api-create/Success1.json                         | JobInstanceDto | /jobInstance/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                            |
      | api/administration/00003-jobInstance-api-create/MISSING_PARAMETER.json                | JobInstanceDto | /jobInstance/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: SepaJob_ddRequestBuilder. |
      | api/administration/00003-jobInstance-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | JobInstanceDto | /jobInstance/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | JobTemplate with code 'XXX' doesn't exist                                                  |
