@accounts
Feature: Create/Update a Billing Account by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> a Billing Account by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto               | api                                    | action         | statusCode | status  | errorCode                        | message                                                                             |
      | accounts/00003-billingAccount-api-create/SuccessTest.json           | BillingAccountDto | /account/billingAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                     |
      | accounts/00003-billingAccount-api-create/SuccessTest.json           | BillingAccountDto | /account/billingAccount/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | BillingAccount with code=TEST already exists.                                       |
      | accounts/00003-billingAccount-api-create/DO_NOT_EXIST.json          | BillingAccountDto | /account/billingAccount/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingAccount with code=NOT_EXIST does not exists.                                 |
      | accounts/00003-billingAccount-api-create/SuccessTest1.json          | BillingAccountDto | /account/billingAccount/               | Update         |        200 | SUCCESS |                                  |                                                                                     |
      | accounts/00003-billingAccount-api-create/SuccessTest1.json          | BillingAccountDto | /account/billingAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                     |
      | accounts/00003-billingAccount-api-create/MISSING_PARAMETER.json     | BillingAccountDto | /account/billingAccount/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: country, language. |
      | accounts/00003-billingAccount-api-create/INVALID_PARAMETER.json     | BillingAccountDto | /account/billingAccount/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String                       |
      | accounts/00003-billingAccount-api-create/ENTITY_DOES_NOT_EXIST.json | BillingAccountDto | /account/billingAccount/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerAccount with code=NOT_EXIST does not exists.                                |
