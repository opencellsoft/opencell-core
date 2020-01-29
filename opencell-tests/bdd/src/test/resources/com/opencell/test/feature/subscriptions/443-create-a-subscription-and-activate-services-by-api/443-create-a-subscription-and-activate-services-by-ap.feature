@ignore
Feature: Subscribe And Activate Services by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Subscribe And Activate Services by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The subscription is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                                      | dto                                         | api                                                | statusCode | errorCode                        | message                                                        | status  |
      | subscriptions/443-create-a-subscription-and-activate-services-by-api/INVALID_PARAMETER.json                                   | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices |        400 | INVALID_PARAMETER                | Can not construct instance of java.util.Date from String value | FAIL    |
      | subscriptions/443-create-a-subscription-and-activate-services-by-api/MissingParameter.json                                    | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices |        500 | GENERIC_API_EXCEPTION            | java.lang.NullPointerException                                 | FAIL    |
      | subscriptions/443-create-a-subscription-and-activate-services-by-api/subscribe and active services error - No price plan.json | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices |        404 | INVALID_PARAMETER                | FAIL                                                           | FAIL    |
      | subscriptions/443-create-a-subscription-and-activate-services-by-api/SubscribeAndActivateServices.json                        | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices |        200 |                                  |                                                                | SUCCESS |
      | subscriptions/443-create-a-subscription-and-activate-services-by-api/Subscriptionalreadyexists.json                           | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices |        403 | ENTITY_ALREADY_EXISTS_EXCEPTION  | Subscription with code=SUB_TEST_1 already exists.              | FAIL    |
      | subscriptions/443-create-a-subscription-and-activate-services-by-api/UserAccountDoesNotExist.json                             | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices |        404 | ENTITY_DOES_NOT_EXISTS_EXCEPTION | UserAccount with code=XXXX does not exists                     | FAIL    |
