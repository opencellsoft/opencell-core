@settings @ignore
Feature: Create invoice Type by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create invoice Type by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoice Type is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                               | dto            | api                         | statusCode | status  | errorCode                        | message                                       |
      | settings/000011-invoiceType-api-create/Success.json                                    | InvoiceTypeDto | /invoiceType/createOrUpdate |        200 | SUCCESS |                                  |                                               |
      | settings/000011-invoiceType-api-create/Success1.json                                   | InvoiceTypeDto | /invoiceType/createOrUpdate |        200 | SUCCESS |                                  |                                               |
      | settings/000011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS.json                     | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | EmailTemplate with code=test does not exists. |
      | settings/000011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_OCCTemplate.json         | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=XX does not exists      |
      | settings/000011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_InvoiceSequence.json     | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSequence with code=XX does not exists. |
      | settings/000011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_oCCTemplateNegative.json | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=XX does not exists      |
