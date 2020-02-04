@billing
Feature: Create/Update Invoice Category by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> Invoice Category by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto                | api                             | action         | statusCode | status  | errorCode                        | message                                                                |
      | api/billing/00001-invoiceCategory-api-create/SuccessTest.json       | InvoiceCategoryDto | /invoiceCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/billing/00001-invoiceCategory-api-create/SuccessTest.json       | InvoiceCategoryDto | /invoiceCategory/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | InvoiceCategory with code=TEST already exists.                         |
      | api/billing/00001-invoiceCategory-api-create/DO_NOT_EXIST.json      | InvoiceCategoryDto | /invoiceCategory/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceCategory with code=NOT_EXIST does not exists.                   |
      | api/billing/00001-invoiceCategory-api-create/SuccessTest1.json      | InvoiceCategoryDto | /invoiceCategory/               | Update         |        200 | SUCCESS |                                  |                                                                        |
      | api/billing/00001-invoiceCategory-api-create/SuccessTest1.json      | InvoiceCategoryDto | /invoiceCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                        |
      | api/billing/00001-invoiceCategory-api-create/MISSING_PARAMETER.json | InvoiceCategoryDto | /invoiceCategory/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code. |
      | api/billing/00001-invoiceCategory-api-create/INVALID_PARAMETER.json | InvoiceCategoryDto | /invoiceCategory/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot construct instance of `org.meveo.api.dto.CustomFieldsDto`       |
