@settings
Feature: Delete invoice Type by API

  Background: The classic offer is already executed
              Create invoice Type by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> a invoice Type by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                    | dto            | api           | action | statusCode | status  | errorCode                        | message                                          |
      | api/settings/00011-invoiceType-api-create/SuccessTest1.json | InvoiceTypeDto | /invoiceType/ | Delete |        200 | SUCCESS |                                  |                                                  |
      | api/settings/00011-InvoiceType-api-create/DO_NOT_EXIST.json | InvoiceTypeDto | /invoiceType/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceType with code=NOT_EXIST does not exists. |
