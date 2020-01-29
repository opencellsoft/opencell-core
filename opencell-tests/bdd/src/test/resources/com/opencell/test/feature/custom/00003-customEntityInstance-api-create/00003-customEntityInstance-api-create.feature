@custom @ignore
Feature: Create Custom  Entity Instance  by API

  Background: The classic offer is already executed
             Create Entity Customization by API is already executed


  @admin @superadmin
  Scenario Outline: Create Custom  Entity Instance by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The custom entity instance is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                | dto                     | api                                        | statusCode | status  | errorCode                        | message                                                                |
      | custom/00003-customEntityInstance-api-create/SuccessTest.json           | CustomEntityTemplateDto | /customEntityInstance/test/createOrUpdate  |        200 | SUCCESS |                                  |                                                                        |
      | custom/00003-customEntityInstance-api-create/SuccessTest1.json          | CustomEntityTemplateDto | /customEntityInstance/test/createOrUpdate  |        200 | SUCCESS |                                  |                                                                        |
      | custom/00003-customEntityInstance-api-create/ENTITY_DOES_NOT_EXIST.json | CustomEntityTemplateDto | /customEntityInstance/test3/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomEntityTemplate with code=test23 does not exists.                 |
      | custom/00003-customEntityInstance-api-create/MISSING_PARAMETER.json     | CustomEntityTemplateDto | /customEntityInstance/test/createOrUpdate  |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
