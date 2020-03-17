@full
Feature: Activate Subscription

  @admin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The entity "<entity>" matches "<expected>"

    Examples: 
      | jsonFile                                                                                         | title                   | dto                           | api                                       | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/11-activate-subscription/create-subscription_1.json   | Create subscription 1   | SubscriptionDto               | /billing/subscription/createOrUpdate      | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/11-activate-subscription/instantiate-services_1.json  | Instanticate services 1 | InstantiateServicesRequestDto | /billing/subscription/instantiateServices | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/11-activate-subscription/create-subscription_2.json   | Create subscription 2   | SubscriptionDto               | /billing/subscription/createOrUpdate      | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/11-activate-subscription/instantiate-services_2.json  | Instanticate services 2 | InstantiateServicesRequestDto | /billing/subscription/instantiateServices | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/11-activate-subscription/activate-subscription_1.json | Activate subscription 1 | String                        | /billing/subscription/activate            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/11-activate-subscription/activate-subscription_2.json | Activate subscription 2 | String                        | /billing/subscription/activate            | POST           |        200 | SUCCESS |           |         |        |          |
