@invoicing
Feature: Generate Invoice by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Generate Invoice by API <errorCode>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The invoice is generated
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                  | dto                       | api                      | action | statusCode | status  | errorCode | message |
      | api/invoicing/01001-invoice-api-generate/SuccessTest.json | GenerateInvoiceRequestDto | /invoice/generateInvoice | POST   |        200 | SUCCESS |           |         |
