@payments
Feature: Create/Update Credit Category by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: <status> <action> Credit Category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The credit category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                       | dto               | api                                    | action         | statusCode | status  | errorCode                        | message                                               |
      | api/payments/00002-creditCategory-api-create/Success.json      | CreditCategoryDto | /payment/creditCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                       |
      | api/payments/00002-creditCategory-api-create/Success.json      | CreditCategoryDto | /payment/creditCategory/               | Create         |        500 | FAIL    | GENERIC_API_EXCEPTION            | ERROR: duplicate key value violates unique constraint |
      | api/payments/00002-creditCategory-api-create/DO_NOT_EXIST.json | CreditCategoryDto | /payment/creditCategory/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CreditCategory with code=NOT_EXIST does not exists.   |
      | api/payments/00002-creditCategory-api-create/Success1.json     | CreditCategoryDto | /payment/creditCategory/               | Update         |        200 | SUCCESS |                                  |                                                       |
      | api/payments/00002-creditCategory-api-create/Success1.json     | CreditCategoryDto | /payment/creditCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                       |
