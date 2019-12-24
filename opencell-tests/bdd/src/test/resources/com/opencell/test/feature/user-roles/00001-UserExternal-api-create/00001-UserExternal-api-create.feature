@subscriptions
Feature: Create a User External by API

  Background: The classic offer is already executed
              The User is already cerated on keycloack
  @admin @superadmin
  Scenario Outline: Create User External by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The User External is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples:  
      | jsonFile                                                                       | dto     | api            | statusCode | status  | errorCode                        | message                                                                                              |
      | user-roles/00001-UserExternal-api-create/Success.json                          | UserDto | /user/external |        200 | SUCCESS |                                  | 81720f65-b0d0-4d04-9e43-bb938799c112                                                                 |                                     
      | user-roles/00001-UserExternal-api-create/Success1.json                         | UserDto | /user/external |        200 | SUCCESS |                                  | 4b6d14bc-b7c4-4682-a007-1eb976a87812                                                                 |                                   
      | user-roles/00001-UserExternal-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | UserDto | /user/external |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Role with code=PC does not exists.                                                                   |
      | user-roles/00001-UserExternal-api-create/Missing_Parameter.json                | UserDto | /user/external |        404 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: email.                              |
      | user-roles/00001-UserExternal-api-create/Missing_Parameter.json                | UserDto | /user/external |        404 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: email.                              |
      | user-roles/00001-UserExternal-api-create/ENTITY_ALREADY_EXISTS_EXCEPTION.json  | UserDto | /user/external |        404 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | User with username=test5 already exists.                                                             |


