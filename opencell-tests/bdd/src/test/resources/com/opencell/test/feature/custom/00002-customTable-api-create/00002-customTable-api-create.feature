@custom @ignore
Feature: Create Custom  Table   by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Custom  Table  by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The custom table is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                    | dto                | api                         | statusCode | status  | errorCode                        | message                                                        |
      | custom/00002-customTable-api-create/AddRecord.json          | CustomTableDataDto | /customTable/createOrUpdate |        200 | SUCCESS |                                  |                                                                |
      | custom/00002-customTable-api-create/EntityDoesNotExist.json | CustomTableDataDto | /customTable/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomEntityTemplate with code=NOT_EXIST does not exists. |
