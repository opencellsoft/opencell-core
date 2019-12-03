Feature: Create a Customer by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create a Customer by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The customer is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                          | dto         | api                              | statusCode | status  | errorCode                        | message                                                                            |
      | customers/93-create-a-customer-by-api/SuccessTest.json            | CustomerDto | /account/customer/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | customers/93-create-a-customer-by-api/SuccessTest1.json           | CustomerDto | /account/customer/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | customers/93-create-a-customer-by-api/SuccessTest.json            | CustomerDto | /account/customer/               |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Customer with code=TEST already exists.                                            |
      | customers/93-create-a-customer-by-api/MISSING_PARAMETER.json      | CustomerDto | /account/customer/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: customerCategory. |
      | customers/93-create-a-customer-by-api/INVALID_PARAMETER.json      | CustomerDto | /account/customer/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Can not deserialize value of type java.util.Date from String                       |
      | customers/93-create-a-customer-by-api/ENTITY_DOES_NOT_EXIST1.json | CustomerDto | /account/customer/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=CLASSIC does not exists.                                |
      | customers/93-create-a-customer-by-api/ENTITY_DOES_NOT_EXIST2.json | CustomerDto | /account/customer/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=TEST does not exists.                                   |
      | customers/93-create-a-customer-by-api/ENTITY_DOES_NOT_EXIST3.json | CustomerDto | /account/customer/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=CLASSIC does not exists.                                |
