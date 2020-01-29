@administration
Feature: Create/Update Billing Cycle by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <action> Billing Cycle by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing cycle is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                           | dto             | api                          | action         | statusCode | status  | errorCode                        | message                                                                            |
      | administration/00001-billingCycle-api-create/Success.json                          | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | administration/00001-billingCycle-api-create/Success.json                          | BillingCycleDto | /billingCycle/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | BillingCycle with code=TEST already exists.                                        |
      | administration/00001-billingCycle-api-create/DO_NOT_EXIST.json                     | BillingCycleDto | /billingCycle/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingCycle with code=NOT_EXIST does not exists.                                  |
      | administration/00001-billingCycle-api-create/Success1.json                         | BillingCycleDto | /billingCycle/               | Update         |        200 | SUCCESS |                                  |                                                                                    |
      | administration/00001-billingCycle-api-create/Success1.json                         | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | administration/00001-billingCycle-api-create/MISSING_PARAMETER.json                | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: invoiceDateDelay. |
      | administration/00001-billingCycle-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Calendar with code=XX does not exists.                                             |
