@billing
Feature: Delete invoice subcategory by API

  Background: The classic offer is already executed
              Create invoice subcategory by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> invoice subcategory by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                      | dto                   | api                  | action | statusCode | status  | errorCode                        | message                                                 |
      | billing/00003-invoiceSubcategory-api-create/SuccessTest.json  | InvoiceSubCategoryDto | /invoiceSubCategory/ | Delete |        200 | SUCCESS |                                  |                                                         |
      | billing/00003-invoiceSubcategory-api-create/DO_NOT_EXIST.json | InvoiceSubCategoryDto | /invoiceSubCategory/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSubCategory with code=NOT_EXIST does not exists. |
