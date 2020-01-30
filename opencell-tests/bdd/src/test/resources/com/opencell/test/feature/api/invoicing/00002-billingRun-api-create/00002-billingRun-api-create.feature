@invoicing
Feature: Create Billing run by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> Billing run by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing run is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                        | dto                 | api                                 | action | statusCode | status  | errorCode                        | message                                                                                        |
      | api/invoicing/00002-billingRun-api-create/Success.json                          | CreateBillingRunDto | /billing/invoicing/createBillingRun | Create |        200 | SUCCESS |                                  |                                                                                                |
      | api/invoicing/00002-billingRun-api-create/Success1.json                         | CreateBillingRunDto | /billing/invoicing/createBillingRun | Create |        200 | SUCCESS |                                  |                                                                                                |
      | api/invoicing/00002-billingRun-api-create/INVALID_PARAMETER.json                | CreateBillingRunDto | /billing/invoicing/createBillingRun | Create |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.billing.BillingProcessTypesEnum` from String |
      | api/invoicing/00002-billingRun-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | CreateBillingRunDto | /billing/invoicing/createBillingRun | Create |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingCycle with code=NOT_EXIST does not exists.                                              |
      | api/invoicing/00002-billingRun-api-create/MISSING_PARAMETER.json                | CreateBillingRunDto | /billing/invoicing/createBillingRun | Create |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: billingRunType.               |
