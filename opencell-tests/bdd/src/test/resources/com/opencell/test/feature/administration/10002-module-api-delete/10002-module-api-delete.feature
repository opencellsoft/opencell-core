@administration
Feature: Delete a module by API

  Background: The classic offer is already executed
              Create a module by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> a module by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                 | dto            | api      | action | statusCode | status  | errorCode                        | message                                     |
      | administration/00002-module-api-create/SuccessTest.json  | MeveoModuleDto | /module/ | Delete |        200 | SUCCESS |                                  |                                             |
      | administration/00002-module-api-create/DO_NOT_EXIST.json | MeveoModuleDto | /module/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Module with code=NOT_EXIST does not exists. |
