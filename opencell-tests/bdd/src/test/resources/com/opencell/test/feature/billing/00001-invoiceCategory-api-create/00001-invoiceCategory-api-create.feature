@billing
Feature: Create Invoice Category by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create  Invoice Category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoice category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                        | dto                | api                             | statusCode | status  | errorCode         | message                                                                                         |
      | billing/00001-invoiceCategory-api-create/SuccessTest.json       | InvoiceCategoryDto | /invoiceCategory/createOrUpdate |        200 | SUCCESS |                   |                                                                                                 |
      | billing/00001-invoiceCategory-api-create/SuccessTest1.json      | InvoiceCategoryDto | /invoiceCategory/createOrUpdate |        200 | SUCCESS |                   |                                                                                                 |
      | billing/00001-invoiceCategory-api-create/MISSING_PARAMETER.json | InvoiceCategoryDto | /invoiceCategory/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values: code.                          |
      | billing/00001-invoiceCategory-api-create/INVALID_PARAMETER.json | InvoiceCategoryDto | /invoiceCategory/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Can not construct instance of org.meveo.api.dto.CustomFieldsDto: no String-argument constructor |
