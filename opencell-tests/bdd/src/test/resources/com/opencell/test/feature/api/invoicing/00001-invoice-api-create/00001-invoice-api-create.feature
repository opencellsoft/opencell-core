@invoicing @review @ignore
Feature: Create Invoice by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> Invoice by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                     | dto        | api       | action | statusCode | status  | errorCode                        | message                               |
      | api/invoicing/00001-invoice-api-create/SuccessTest.json                      | InvoiceDto | /invoice/ | Create |        200 | SUCCESS |                                  |                                       |
      | api/invoicing/00001-invoice-api-create/SuccessTest1.json                     | InvoiceDto | /invoice/ | Update |        200 | SUCCESS |                                  |                                       |
      | api/invoicing/00001-invoice-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | InvoiceDto | /invoice/ | Create |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Seller with code=XXX does not exists. |
