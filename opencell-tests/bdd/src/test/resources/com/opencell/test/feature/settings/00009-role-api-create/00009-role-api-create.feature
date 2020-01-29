@settings
Feature: Create/Update Role by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: <action> Role by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The role is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto     | api                  | action         | statusCode | status  | errorCode                        | message                                                                  |
      | settings/00009-role-api-create/SuccessTest.json                      | RoleDto | /role/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                          |
      | settings/00009-role-api-create/SuccessTest.json                      | RoleDto | /role/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Role with role name=TEST already exists.                                 |
      | settings/00009-role-api-create/DO_NOT_EXIST.json                     | RoleDto | /role/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Role with name=NOT_EXIST does not exists.                                |
      | settings/00009-role-api-create/SuccessTest1.json                     | RoleDto | /role/               | Update         |        200 | SUCCESS |                                  |                                                                          |
      | settings/00009-role-api-create/SuccessTest1.json                     | RoleDto | /role/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                          |
      | settings/00009-role-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | RoleDto | /role/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Permission with name=NOT_EXIST does not exists.                          |
      | settings/00009-role-api-create/MISSING_PARAMETER.json                | RoleDto | /role/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: name.   |
      | settings/00009-role-api-create/INVALID_PARAMETER.json                | RoleDto | /role/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize instance of `java.util.ArrayList` out of VALUE_STRING |
