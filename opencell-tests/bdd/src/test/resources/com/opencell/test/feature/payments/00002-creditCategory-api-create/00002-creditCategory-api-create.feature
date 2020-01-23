@administration
Feature: Create Credit Category by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: Create Credit Category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The credit category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                               | dto               | api                                    | statusCode | status  | errorCode             | message                                               |
      | payments/00002-creditCategory-api-create/Success.json  | CreditCategoryDto | /payment/creditCategory/createOrUpdate |        200 | SUCCESS |                       |                                                       |
      | payments/00002-creditCategory-api-create/Success.json  | CreditCategoryDto | /payment/creditCategory/               |        500 | FAIL    | GENERIC_API_EXCEPTION | ERROR: duplicate key value violates unique constraint |
      | payments/00002-creditCategory-api-create/Success1.json | CreditCategoryDto | /payment/creditCategory/createOrUpdate |        200 | SUCCESS |                       |                                                       |
