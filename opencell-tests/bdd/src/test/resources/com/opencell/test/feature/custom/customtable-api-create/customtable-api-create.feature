
Feature: Create Custom  Table   by API

  Background: The classic offer is already executed
            

  @admin @superadmin
  Scenario Outline: Create Custom  Table  by API
    Given The SuccessTest has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The custom  Table   is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                              | dto                      | api                                         | statusCode | status  | errorCode                        | message                                                                 |
      | custom/customtable-api-create/SuccessTest.json        | CustomEntityTemplateDto  | /entityCustomization/entity/createOrUpdate  |   200      | SUCCESS |                                  |                                                                         |
      | custom/customtable-api-create/SuccessTest1.json       | CustomEntityTemplateDto  | /entityCustomization/entity/createOrUpdate  |   200      | SUCCESS |                                  |                                                                         |
      | custom/customtable-api-create/MissingParameter.json   | CustomEntityTemplateDto  | /entityCustomization/entity/createOrUpdate  |   400      | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code.  |                                |
      | custom/customtable-api-create/EntityDoesNotExist.json | CustomTableDataDto       | /customTable/createOrUpdate                 |   404      | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomEntityTemplate with code=DOES_NOT_EXIST does not exists.          |                                                                              |
      | custom/customtable-api-create/AddRecord.json          | CustomTableDataDto       | /customTable/createOrUpdate                 |   200      | SUCCESS |                                  |                                                                         |                                                                              |
     