@settings
Feature: Create Billing run by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create Billing run by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The Billing run is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                    | dto                | api                                | statusCode | status  | errorCode                        | message                                                                                       |
      | invoicing/00002-billingRun-api-create/Success.json                          | createBillingRunDto| /billing/invoicing/createBillingRun|        200 | SUCCESS |                                  |                                                                                               |
      | invoicing/00002-billingRun-api-create/Success1.json                         | createBillingRunDto| /billing/invoicing/createBillingRun|        200 | SUCCESS |                                  |                                                                                               |
      | invoicing/00002-billingRun-api-create/INVALID_PARAMETER.json                | createBillingRunDto| /billing/invoicing/createBillingRun|        400 | FAIL    | INVALID_PARAMETER                | Can not deserialize value of type org.meveo.model.billing.BillingProcessTypesEnum from String.|
      | invoicing/00002-billingRun-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | createBillingRunDto| /billing/invoicing/createBillingRun|        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | BillingCycle with code=CYC_INV_MT_X does not exists.                                          |
      | invoicing/00002-billingRun-api-create/MISSING_PARAMETER.json                | createBillingRunDto| /billing/invoicing/createBillingRun|        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: billingRunType.              |
