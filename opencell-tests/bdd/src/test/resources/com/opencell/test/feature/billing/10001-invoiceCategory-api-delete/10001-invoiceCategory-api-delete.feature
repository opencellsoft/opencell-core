@billing
Feature: Delete invoice category by API

  Background: The classic offer is already executed
              Create invoice category by API is already executed


  @admin @superadmin
  Scenario Outline: Delete invoice category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto                | api               | statusCode | status  | errorCode                        | message                                              |
      | billing/00001-invoiceCategory-api-create/SuccessTest.json           | InvoiceCategoryDto | /invoiceCategory/ |        200 | SUCCESS |                                  |                                                      |
      | billing/10001-invoiceCategory-api-delete/ENTITY_DOES_NOT_EXIST.json | InvoiceCategoryDto | /invoiceCategory/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceCategory with code=NOT_EXIST does not exists. |
