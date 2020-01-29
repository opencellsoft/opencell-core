@ignore
Feature: Create and assigned a tax category to a billing account by API

  Background: The classic offer is already executed
    A Billing Account is configured


  @admin @superadmin
  Scenario Outline: Create and assigned a tax category to a billing account by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The tax subcategory is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                  | dto             | api                          | statusCode | status  | errorCode                        | message                                                                            |
      | billing/assigne-a-tax-category-to-a-billing-account/Success.json                          | BillingCycleDto | /billingCycle/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | billing/assigne-a-tax-category-to-a-billing-account/Success1.json                         | BillingCycleDto | /billingCycle/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | billing/assigne-a-tax-category-to-a-billing-account/MISSING_PARAMETER.json                | BillingCycleDto | /billingCycle/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: invoiceDateDelay. |
      | billing/assigne-a-tax-category-to-a-billing-account/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | BillingCycleDto | /billingCycle/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Calendar with code=XX does not exists.                                             |
