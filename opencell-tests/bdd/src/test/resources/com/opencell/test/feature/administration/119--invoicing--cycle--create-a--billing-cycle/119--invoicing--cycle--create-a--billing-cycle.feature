Feature: Create Billing Cycle by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Billing Cycle by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The billing cycle is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                            | dto             | api                          | statusCode | status  | errorCode                        | message                                                                            |
      | administration/119--invoicing--cycle--create-a--billing-cycle/Success.json                          | BillingCycleDto | /billingCycle/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | administration/119--invoicing--cycle--create-a--billing-cycle/Success1.json                         | BillingCycleDto | /billingCycle/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | administration/119--invoicing--cycle--create-a--billing-cycle/MISSING_PARAMETER.json                | BillingCycleDto | /billingCycle/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: invoiceDateDelay. |
      | administration/119--invoicing--cycle--create-a--billing-cycle/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | BillingCycleDto | /billingCycle/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Calendar with code=XX does not exists.                                             |
