@settings @ignore
Feature: Delete invoice Type by API

  Background: The classic offer is already executed
              Create invoice Type by API is already executed


  @admin @superadmin
  Scenario Outline: Delete a invoice Type by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                         | dto            | api           | statusCode | status  | errorCode                        | message                                          |
      | settings/000011-invoiceType-api-create/Success.json              | InvoiceTypeDto | /invoiceType/ |        200 | SUCCESS |                                  |                                                  |
      | settings/100011-InvoiceType-api-delete/ENTITY_DOES_NOT_EXIST.json| InvoiceTypeDto | /invoiceType/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceType with code=NOT_EXIST does not exists. |
