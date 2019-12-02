Feature: Create a tax by API

  Background: The classic offer is executed

  @admin @superadmin
  Scenario Outline: Create a tax by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The tax is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                | dto    | api                 | statusCode | status  | errorCode         | message                                                            |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/tax_SuccessTest.json       | TaxDto | /tax/createOrUpdate |        200 | SUCCESS |                   |                                                                    |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/tax_SuccessTest1.json      | TaxDto | /tax/createOrUpdate |        200 | SUCCESS |                   |                                                                    |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/tax_MISSING_PARAMETER.json | TaxDto | /tax/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values    |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/tax_INVALID_PARAMETER.json | TaxDto | /tax/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Can not deserialize value of type java.math.BigDecimal from String |

  @admin @superadmin
  Scenario Outline: Create an invoice subcategory by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The invoiceSubCategory is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                               | dto                   | api                                | statusCode | status  | errorCode                        | message                                                                                                                                   |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/isc_SuccessTest.json                      | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate |        200 | SUCCESS |                                  |                                                                                                                                           |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/isc_SuccessTest1.json                     | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate |        200 | SUCCESS |                                  |                                                                                                                                           |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/isc_MISSING_PARAMETER.json                | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values                                                                           |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/isc_INVALID_PARAMETER.json                | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Can not construct instance of org.meveo.api.dto.CustomFieldsDto: no String-argument constructor/factory method to deserialize from String |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/isc_ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | InvoiceCategory with code=NOT_EXIST does not exists.                                                                                      |

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
      | jsonFile                                                                                                | dto                          | api                                       | statusCode | status  | errorCode                        | message                                                         |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/iscc_SuccessTest.json                      | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        200 | SUCCESS |                                  |                                                                 |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/iscc_SuccessTest1.json                     | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        200 | SUCCESS |                                  |                                                                 |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/iscc_MISSING_PARAMETER.json                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/iscc_INVALID_PARAMETER.json                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Can not deserialize value of type int from String               |
      | billing/103-create-subcategory-and-its-associated-tax-by-api/iscc_ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Tax with code=NOT_EXIST does not exists.                        |
