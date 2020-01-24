@subscriptions
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
      | jsonFile                                                                          | dto             | api                                  | statusCode | status  | errorCode                        | message                                                                            |
      | subscriptions/00001-subscription-api-create/Success.json                          | SubscriptionDto | /billing/subscription/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | subscriptions/00001-subscription-api-create/Success1.json                         | SubscriptionDto | /billing/subscription/createOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | subscriptions/00001-subscription-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | SubscriptionDto | /billing/subscription/createOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | UserAccount with code=ben does not exists.                                         |
      | subscriptions/00001-subscription-api-create/MISSING_PARAMETER.json                | SubscriptionDto | /billing/subscription/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: subscriptionDate. |
      | subscriptions/00001-subscription-api-create/INVALID_PARAMETER.json                | SubscriptionDto | /billing/subscription/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String                      |
