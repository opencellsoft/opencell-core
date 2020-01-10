@settings
Feature: Create invoice Type by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create invoice Type by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoice type is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                              | dto            | api                         | statusCode | status  | errorCode                        | message                                                                |
      | settings/00011-invoiceType-api-create/SuccessTest.json                                | InvoiceTypeDto | /invoiceType/createOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | settings/00011-invoiceType-api-create/SuccessTest1.json                               | InvoiceTypeDto | /invoiceType/createOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS.json                     | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | EmailTemplate with code=NOT_EXIST does not exists.                     |
      | settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_OCCTemplate.json         | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists                        |
      | settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_InvoiceSequence.json     | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSequence with code=NOT_EXIST does not exists.                   |
      | settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_oCCTemplateNegative.json | InvoiceTypeDto | /invoiceType/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists                        |
      | settings/00011-invoiceType-api-create/MISSING_PARAMETER.json                          | InvoiceTypeDto | /invoiceType/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | settings/00011-invoiceType-api-create/INVALID_PARAMETER.json                          | InvoiceTypeDto | /invoiceType/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `boolean` from String                 |
