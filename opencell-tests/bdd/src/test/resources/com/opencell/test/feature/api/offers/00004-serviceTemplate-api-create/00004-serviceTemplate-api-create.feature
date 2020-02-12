@offers
Feature: Create/Update service template plan by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <status> <action> service template by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The service template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto                | api                                     | action         | statusCode | status  | errorCode                        | message                                                                |
      | api/offers/00004-serviceTemplate-api-create/SuccessTest.json       | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/offers/00004-serviceTemplate-api-create/SuccessTest.json       | ServiceTemplateDto | /catalog/serviceTemplate/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | ServiceTemplateService with code=TEST already exists.                  |
      | api/offers/00004-serviceTemplate-api-create/DO_NOT_EXIST.json      | ServiceTemplateDto | /catalog/serviceTemplate/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ServiceTemplateService with code=NOT_EXIST does not exists.            |
      | api/offers/00004-serviceTemplate-api-create/SuccessTest1.json      | ServiceTemplateDto | /catalog/serviceTemplate/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/offers/00004-serviceTemplate-api-create/SuccessTest1.json      | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/offers/00004-serviceTemplate-api-create/MISSING_PARAMETER.json | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | api/offers/00004-serviceTemplate-api-create/INVALID_PARAMETER.json | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `boolean` from String                 |
