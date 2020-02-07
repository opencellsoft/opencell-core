@custom @ignore
Feature: Create Entity Customization by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Entity Customization by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The entity customization is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto                     | api                                        | statusCode | status  | errorCode                       | message                                                                |
      | api/custom/00001-entityCustomization-api-create/SuccessTest.json       | CustomEntityTemplateDto | /entityCustomization/entity/createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | api/custom/00001-entityCustomization-api-create/SuccessTest1.json      | CustomEntityTemplateDto | /entityCustomization/entity/createOrUpdate |        200 | SUCCESS |                                 |                                                                        |
      | api/custom/00001-entityCustomization-api-create/SuccessTest.json       | CustomEntityTemplateDto | /entityCustomization/entity/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | CustomEntityTemplate with code=TEST already exists.                    |
      | api/custom/00001-entityCustomization-api-create/MISSING_PARAMETER.json | CustomEntityTemplateDto | /entityCustomization/entity/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: code. |
      | api/custom/00001-entityCustomization-api-create/INVALID_PARAMETER.json | CustomEntityTemplateDto | /entityCustomization/entity/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `java.lang.Boolean` from String       |
