@invoicing @review @ignore
Feature: Create Invoice by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Invoice by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoice is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                     | dto        | api       | statusCode | status  | errorCode                        | message                               |
      | api/invoicing/00001-invoice-api-create/Success.json                          | InvoiceDto | /invoice/ |        200 | SUCCESS |                                  |                                       |
      | api/invoicing/00001-invoice-api-create/Success1.json                         | InvoiceDto | /invoice/ |        200 | SUCCESS |                                  |                                       |
      | api/invoicing/00001-invoice-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | InvoiceDto | /invoice/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Seller with code=XXX does not exists. |
