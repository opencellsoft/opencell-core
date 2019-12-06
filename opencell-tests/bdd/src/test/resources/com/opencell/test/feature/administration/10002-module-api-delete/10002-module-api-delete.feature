@administration
Feature: Delete a module by API

  Background: The classic offer is already executed
              Create a module by API is already executed


  @admin @superadmin
  Scenario Outline: Delete a module by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                          | dto            | api      | statusCode | status  | errorCode                        | message                                     |
      | administration/00002-module-api-create/SuccessTest.json           | MeveoModuleDto | /module/ |        200 | SUCCESS |                                  |                                             |
      | administration/10002-module-api-delete/ENTITY_DOES_NOT_EXIST.json | MeveoModuleDto | /module/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Module with code=NOT_EXIST does not exists. |
