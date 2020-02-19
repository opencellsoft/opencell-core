@subscriptions
Feature: Subscribe And Activate Services by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Subscribe and Activate Services by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The subscription is created and activated
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                | dto                                         | api                                                | action | statusCode | errorCode                        | message                                                                    | status  |
      | api/subscriptions/00002-subscribeAndActivate-api/SuccessTest.json       | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices | POST   |        200 |                                  |                                                                            | SUCCESS |
      | api/subscriptions/00002-subscribeAndActivate-api/SuccessTest.json       | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices | POST   |        403 | ENTITY_ALREADY_EXISTS_EXCEPTION  | Subscription with code=SUB_TEST_1 already exists.                          | FAIL    |
      | api/subscriptions/00002-subscribeAndActivate-api/user_NOT_EXIST.json    | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices | POST   |        404 | ENTITY_DOES_NOT_EXISTS_EXCEPTION | UserAccount with code=NOT_EXIST does not exists                            | FAIL    |
      | api/subscriptions/00002-subscribeAndActivate-api/INVALID_PARAMETER.json | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices | POST   |        400 | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date`                          | FAIL    |
      | api/subscriptions/00002-subscribeAndActivate-api/MISSING_PARAMETER.json | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices | POST   |        400 | MISSING_PARAMETER                | The following parameters are required or contain invalid values: services. | FAIL    |
