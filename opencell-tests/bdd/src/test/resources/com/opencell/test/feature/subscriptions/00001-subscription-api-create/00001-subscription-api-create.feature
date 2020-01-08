@subscriptions @ignore
Feature: Create subscription Plan by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create subscription by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The subscription is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples:  
      | jsonFile                                                                               | dto             | api                                  | statusCode | status  | errorCode                        | message                                                                                              |
      | subscription/billing/subscription/createOrUpdate/Success.json                          | SubscriptionDto | /billing/subscription/createOrUpdate |        200 | SUCCESS |                                  |                                                                                                      |
      | subscription/billing/subscription/createOrUpdate/Success1.json                         | SubscriptionDto | /billing/subscription/createOrUpdate |        200 | SUCCESS |                                  |                                                                                                      |
      | subscription/billing/subscription/createOrUpdate/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | SubscriptionDto | /billing/subscription/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | UserAccount with code=ben does not exists.                                                           |
      | subscription/billing/subscription/createOrUpdate/Missing_Parameter.json                | SubscriptionDto | /billing/subscription/createOrUpdate |        404 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: subscriptionDate.                   |
      | subscription/billing/subscription/createOrUpdate/Invalid_Parameter.json                | SubscriptionDto | /billing/subscription/createOrUpdate |        404 | FAIL    | INVALID_PARAMETER                | Can not change the parent account. Subscription's current parent account (user account) is ben.ohara.|


