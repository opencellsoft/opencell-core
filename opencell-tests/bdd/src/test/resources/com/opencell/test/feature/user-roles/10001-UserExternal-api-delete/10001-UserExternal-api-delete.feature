@user-roles
Feature: Delete User External by API

  Background: The classic offer is already executed
              Create User External by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Job Instance by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto     | api             | statusCode | status  | errorCode                        | message                                          |
      | user-roles/00001-UserExternal-api-create/Success.json               | UserDto | /user/external/ |        200 | SUCCESS |                                  |                                                  |
      | user-roles/10001-UserExternal-api-delete/ENTITY_DOES_NOT_EXIST.json | UserDto | /user/external/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION |User with username=NOT_EXIST does not exists.     |
