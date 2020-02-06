@settings
Feature: Create/Update invoice Type by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> invoice Type by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice type is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                  | dto            | api                         | action         | statusCode | status  | errorCode                        | message                                                                |
      | api/settings/00011-invoiceType-api-create/SuccessTest.json                                | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/settings/00011-invoiceType-api-create/SuccessTest.json                                | InvoiceTypeDto | /invoiceType/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | InvoiceType with code=TEST already exists.                             |
      | api/settings/00011-invoiceType-api-create/DO_NOT_EXIST.json                               | InvoiceTypeDto | /invoiceType/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceType with code=NOT_EXIST does not exists.                       |
      | api/settings/00011-invoiceType-api-create/SuccessTest1.json                               | InvoiceTypeDto | /invoiceType/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/settings/00011-invoiceType-api-create/SuccessTest1.json                               | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS.json                     | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | EmailTemplate with code=NOT_EXIST does not exists.                     |
      | api/settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_OCCTemplate.json         | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists                        |
      | api/settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_InvoiceSequence.json     | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSequence with code=NOT_EXIST does not exists.                   |
      | api/settings/00011-invoiceType-api-create/ENTITY_DOES_NOT_EXISTS_oCCTemplateNegative.json | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | OCCTemplate with code=NOT_EXIST does not exists                        |
      | api/settings/00011-invoiceType-api-create/MISSING_PARAMETER.json                          | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | api/settings/00011-invoiceType-api-create/INVALID_PARAMETER.json                          | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `boolean` from String                 |
