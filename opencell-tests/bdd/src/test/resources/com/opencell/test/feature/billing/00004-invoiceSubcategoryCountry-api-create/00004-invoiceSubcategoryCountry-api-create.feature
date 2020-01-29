@billing
Feature: Create an invoice subcategory country by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: Create an invoice subcategory country by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoiceSubCategoryCountry is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                 | dto                          | api                                       | statusCode | status  | errorCode                        | message                                                         |
      | billing/00004-invoiceSubcategoryCountry-api-create/SuccessTest.json                      | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        200 | SUCCESS |                                  |                                                                 |
      | billing/00004-invoiceSubcategoryCountry-api-create/SuccessTest1.json                     | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        200 | SUCCESS |                                  |                                                                 |
      | billing/00004-invoiceSubcategoryCountry-api-create/MISSING_PARAMETER.json                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values |
      | billing/00004-invoiceSubcategoryCountry-api-create/INVALID_PARAMETER.json                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `int` from String              |
      | billing/00004-invoiceSubcategoryCountry-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Tax with code=NOT_EXIST does not exists.                        |
