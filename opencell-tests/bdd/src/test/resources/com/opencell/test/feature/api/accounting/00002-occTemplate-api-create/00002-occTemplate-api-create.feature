@accounting
Feature: Create OCC Template by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> OCC Template by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The occ template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto            | api                         | action         | statusCode | status  | errorCode                        | message                                                                                        |
      | api/accounting/00002-occTemplate-api-create/SuccessTest.json       | OccTemplateDto | /occTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                |
      | api/accounting/00002-occTemplate-api-create/SuccessTest.json       | OccTemplateDto | /occTemplate/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | OCCTemplate with code=TEST already exists.                                                     |
      | api/accounting/00002-occTemplate-api-create/DO_NOT_EXIST.json      | OccTemplateDto | /occTemplate/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists.                                               |
      | api/accounting/00002-occTemplate-api-create/SuccessTest1.json      | OccTemplateDto | /occTemplate/               | Update         |        200 | SUCCESS |                                  |                                                                                                |
      | api/accounting/00002-occTemplate-api-create/SuccessTest1.json      | OccTemplateDto | /occTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                |
      | api/accounting/00002-occTemplate-api-create/MISSING_PARAMETER.json | OccTemplateDto | /occTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: accountCode / accountingCode. |
      | api/accounting/00002-occTemplate-api-create/INVALID_PARAMETER.json | OccTemplateDto | /occTemplate/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.payments.OperationCategoryEnum` from String  |
