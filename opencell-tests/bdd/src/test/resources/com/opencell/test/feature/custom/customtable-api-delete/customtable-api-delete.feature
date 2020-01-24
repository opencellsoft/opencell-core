@custom @ignore
Feature: Delete Custom  Table  by API

  Background: The classic offer is already executed
              Create Custom  Table   by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Custom  Table  by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                          | dto                | api          | statusCode | status  | errorCode                        | message                                                   |
      | customtable-api-delete/SuccessDelete.json         | CustomTableDataDto | /customTable |        200 | SUCCESS |                                  |                                                           |
      | customtable-api-delete/ENTITY_DOES_NOT_EXIST.json | CustomTableDataDto | /customTable |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomEntityInstance with code=NOT_EXIST does not exists. |
