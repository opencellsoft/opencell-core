@settings
Feature: Delete Role by API

  Background: System is configured.
    Create Role by API already executed.


  @admin @superadmin
  Scenario Outline: <status> <action> Role by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>" with identifier "name"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                         | dto     | api    | action | statusCode | status  | errorCode                        | message                                   |
      | settings/00009-role-api-create/SuccessTest.json  | RoleDto | /role/ | Delete |        200 | SUCCESS |                                  |                                           |
      | settings/00009-role-api-create/DO_NOT_EXIST.json | RoleDto | /role/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Role with name=NOT_EXIST does not exists. |
