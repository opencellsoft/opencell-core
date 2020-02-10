@billing
Feature: Delete invoice category by API

  Background: The classic offer is already executed
              Create invoice category by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> invoice category by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto                | api               | action | statusCode | status  | errorCode                        | message                                              |
      | api/billing/00001-invoiceCategory-api-create/SuccessTest.json  | InvoiceCategoryDto | /invoiceCategory/ | Delete |        200 | SUCCESS |                                  |                                                      |
      | api/billing/00001-invoiceCategory-api-create/DO_NOT_EXIST.json | InvoiceCategoryDto | /invoiceCategory/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceCategory with code=NOT_EXIST does not exists. |
