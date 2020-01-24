@accounts
Feature: Delete a User Account by API

  Background: The classic offer is already executed
              Create a User account by API is already executed


  @admin @superadmin
  Scenario Outline: Delete a UserAccount by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                      | dto            | api                   | statusCode | status  | errorCode                          | message                                          |
      | accounts/00004-userAccount-api-create/SuccessTest.json                        | UserAccountDto | /account/userAccount/ |        200 | SUCCESS |                                    |                                                  |
      | accounts/10001-userAccount-api-delete/ENTITY_DOES_NOT_EXIST.json              | UserAccountDto | /account/userAccount/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION   | UserAccount with code=NOT_EXIST does not exists. |
      | accounts/10001-userAccount-api-delete/DELETE_REFERENCED_ENTITY_EXCEPTION.json | UserAccountDto | /account/userAccount/ |        403 | FAIL    | DELETE_REFERENCED_ENTITY_EXCEPTION | UserAccount with code=ben.ohara is referenced.   |
