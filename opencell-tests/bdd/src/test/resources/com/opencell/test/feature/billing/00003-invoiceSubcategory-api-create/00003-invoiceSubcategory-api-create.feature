@billing
Feature: Create/Update an invoice subcategory by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: <action> an invoice subcategory by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoiceSubCategory is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                          | dto                   | api                                | action         | statusCode | status  | errorCode                        | message                                                          |
      | billing/00003-invoiceSubcategory-api-create/SuccessTest.json                      | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                  |
      | billing/00003-invoiceSubcategory-api-create/SuccessTest.json                      | InvoiceSubCategoryDto | /invoiceSubCategory/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | InvoiceSubCategory with code=TEST_WITH_TAX already exists.                |
      | billing/00003-invoiceSubcategory-api-create/DO_NOT_EXIST.json                     | InvoiceSubCategoryDto | /invoiceSubCategory/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceSubCategory with code=NOT_EXIST does not exists.          |
      | billing/00003-invoiceSubcategory-api-create/SuccessTest1.json                     | InvoiceSubCategoryDto | /invoiceSubCategory/               | Update         |        200 | SUCCESS |                                  |                                                                  |
      | billing/00003-invoiceSubcategory-api-create/SuccessTest1.json                     | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                  |
      | billing/00003-invoiceSubcategory-api-create/MISSING_PARAMETER.json                | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values  |
      | billing/00003-invoiceSubcategory-api-create/INVALID_PARAMETER.json                | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot construct instance of `org.meveo.api.dto.CustomFieldsDto` |
      | billing/00003-invoiceSubcategory-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceCategory with code=NOT_EXIST does not exists.             |
