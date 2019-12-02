Feature: Create a Customer Account by API

  Background: The classic offer is already executed
    Create a Customer is already executed


  @admin @superadmin
  Scenario Outline: Create a Customer Account by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The customer account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                  | dto                | api                                     | statusCode | status  | errorCode                        | message                                                                              |
      | customers/541-create-a-customer-account-by-api/SuccessTest.json           | CustomerAccountDto | /account/customerAccount/createOrUpdate |        200 | SUCCESS |                                  |                                                                                      |
      | customers/541-create-a-customer-account-by-api/SuccessTest1.json          | CustomerAccountDto | /account/customerAccount/createOrUpdate |        200 | SUCCESS |                                  |                                                                                      |
      | customers/541-create-a-customer-account-by-api/SuccessTest.json           | CustomerAccountDto | /account/customerAccount/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | CustomerAccount with code=TEST already exists.                                       |
      | customers/541-create-a-customer-account-by-api/MISSING_PARAMETER.json     | CustomerAccountDto | /account/customerAccount/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: currency, language. |
      | customers/541-create-a-customer-account-by-api/INVALID_PARAMETER.json     | CustomerAccountDto | /account/customerAccount/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Can not deserialize value of type java.util.Date from String                         |
      | customers/541-create-a-customer-account-by-api/ENTITY_DOES_NOT_EXIST.json | CustomerAccountDto | /account/customerAccount/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Customer with code=NOT_EXIST does not exists.                                        |
