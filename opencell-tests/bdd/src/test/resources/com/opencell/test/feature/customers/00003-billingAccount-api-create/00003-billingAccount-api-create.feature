@customers
Feature: Create a Billing Account by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create a Billing Account by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The billing account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto               | api                                    | statusCode | status  | errorCode                        | message                                                                             |  |
      | customers/00003-billingAccount-api-create/SuccessTest.json           | BillingAccountDto | /account/billingAccount/createOrUpdate |        200 | SUCCESS |                                  |                                                                                     |  |
      | customers/00003-billingAccount-api-create/SuccessTest1.json          | BillingAccountDto | /account/billingAccount/createOrUpdate |        200 | SUCCESS |                                  |                                                                                     |  |
      | customers/00003-billingAccount-api-create/SuccessTest.json           | BillingAccountDto | /account/billingAccount/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | BillingAccount with code=TEST already exists.                                       |  |
      | customers/00003-billingAccount-api-create/MISSING_PARAMETER.json     | BillingAccountDto | /account/billingAccount/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: country, language. |  |
      | customers/00003-billingAccount-api-create/INVALID_PARAMETER.json     | BillingAccountDto | /account/billingAccount/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Can not deserialize value of type java.util.Date from String                        |  |
      | customers/00003-billingAccount-api-create/ENTITY_DOES_NOT_EXIST.json | BillingAccountDto | /account/billingAccount/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerAccount with code=NOT_EXIST does not exists.                                |  |
