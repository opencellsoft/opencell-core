@administration
Feature: Delete Job Instance by API

  Background: The classic offer is already executed
              Create Job Cycle by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> Job Instance by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                          | dto            | api           | action | statusCode | status  | errorCode                        | message                                          |
      | api/administration/00003-jobInstance-api-create/Success.json      | JobInstanceDto | /jobInstance/ | Delete |        200 | SUCCESS |                                  |                                                  |
      | api/administration/00003-jobInstance-api-create/DO_NOT_EXIST.json | JobInstanceDto | /jobInstance/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | JobInstance with code=NOT_EXIST does not exists. |
