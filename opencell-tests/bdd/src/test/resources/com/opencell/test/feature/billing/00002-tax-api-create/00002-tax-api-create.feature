@billing
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
      | jsonFile                                            | dto    | api                 | statusCode | status  | errorCode         | message                                                             |
      | billing/00002-tax-api-create/SuccessTest.json       | TaxDto | /tax/createOrUpdate |        200 | SUCCESS |                   |                                                                     |
      | billing/00002-tax-api-create/SuccessTest1.json      | TaxDto | /tax/createOrUpdate |        200 | SUCCESS |                   |                                                                     |
      | billing/00002-tax-api-create/MISSING_PARAMETER.json | TaxDto | /tax/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER | The following parameters are required or contain invalid values     |
      | billing/00002-tax-api-create/INVALID_PARAMETER.json | TaxDto | /tax/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER | Cannot deserialize value of type `java.math.BigDecimal` from String |
