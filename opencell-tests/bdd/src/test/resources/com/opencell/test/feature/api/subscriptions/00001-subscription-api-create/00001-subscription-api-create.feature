@subscriptions
Feature: Create/Update subscription Plan by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> subscription by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The subscription is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                              | dto             | api                                  | action         | statusCode | status  | errorCode                        | message                                                                            |
      | api/subscriptions/00001-subscription-api-create/SuccessTest.json                      | SubscriptionDto | /billing/subscription/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | api/subscriptions/00001-subscription-api-create/SuccessTest.json                      | SubscriptionDto | /billing/subscription/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Subscription with code=SUB_TEST already exists.                                    |
      | api/subscriptions/00001-subscription-api-create/DO_NOT_EXIST.json                     | SubscriptionDto | /billing/subscription/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Subscription with code=NOT_EXIST does not exists.                                  |
      | api/subscriptions/00001-subscription-api-create/SuccessTest1.json                     | SubscriptionDto | /billing/subscription/               | Update         |        200 | SUCCESS |                                  |                                                                                    |
      | api/subscriptions/00001-subscription-api-create/SuccessTest1.json                     | SubscriptionDto | /billing/subscription/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | api/subscriptions/00001-subscription-api-create/ENTITY_DOES_NOT_EXISTS_EXCEPTION.json | SubscriptionDto | /billing/subscription/createOrUpdate | CreateOrUpdate |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | UserAccount with code=ben does not exists.                                         |
      | api/subscriptions/00001-subscription-api-create/MISSING_PARAMETER.json                | SubscriptionDto | /billing/subscription/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: subscriptionDate. |
      | api/subscriptions/00001-subscription-api-create/INVALID_PARAMETER.json                | SubscriptionDto | /billing/subscription/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String                      |
