@billing
Feature: Delete invoice subcategory by API

  Background: The classic offer is already executed
              Create invoice subcategory by API is already executed


  @admin @superadmin
  Scenario Outline: Delete invoice subcategory by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto                   | api                  | statusCode | status  | errorCode                        | message                                                 |
      | billing/00003-invoiceSubcategory-api-create/SuccessTest.json           | InvoiceSubCategoryDto | /invoiceSubCategory/ |        200 | SUCCESS |                                  |                                                         |
      | billing/10002-invoiceSubcategory-api-delete/ENTITY_DOES_NOT_EXIST.json | InvoiceSubCategoryDto | /invoiceSubCategory/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSubCategory with code=NOT_EXIST does not exists. |
