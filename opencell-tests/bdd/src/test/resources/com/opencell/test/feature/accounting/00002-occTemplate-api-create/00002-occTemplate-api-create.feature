@accounting
Feature: Create OCC Template by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create OCC Template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The occ template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto            | api                         | statusCode | status  | errorCode                       | message                                                                                        |
      | accounting/00002-occTemplate-api-create/SuccessTest.json       | OccTemplateDto | /occTemplate/createOrUpdate |        200 | SUCCESS |                                 |                                                                                                |
      | accounting/00002-occTemplate-api-create/SuccessTest.json       | OccTemplateDto | /occTemplate/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | OCCTemplate with code=TEST already exists.                                                     |
      | accounting/00002-occTemplate-api-create/SuccessTest1.json      | OccTemplateDto | /occTemplate/createOrUpdate |        200 | SUCCESS |                                 |                                                                                                |
      | accounting/00002-occTemplate-api-create/MISSING_PARAMETER.json | OccTemplateDto | /occTemplate/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: accountCode / accountingCode. |
      | accounting/00002-occTemplate-api-create/INVALID_PARAMETER.json | OccTemplateDto | /occTemplate/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `org.meveo.model.payments.OperationCategoryEnum` from String  |
