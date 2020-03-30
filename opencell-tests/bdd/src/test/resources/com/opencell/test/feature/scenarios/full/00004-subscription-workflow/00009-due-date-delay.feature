@full
Feature: Subscription workflow - Due date delay

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
      | jsonFile                                                                                 | title                    | dto                        | api                                                               | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/find-due-date-bc.json       | Find due date delay - bc |                            | /billing/subscription/dueDateDelay?subscriptionCode=RS_BASE_SUB   | GET            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/create-CA.json              | Create CA                | CustomerAccountDto         | /account/customerAccount/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/create-BA.json              | Create BA                | BillingAccountDto          | /account/billingAccount/createOrUpdate                            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/create-UA.json              | Create UA                | UserAccountDto             | /account/userAccount/createOrUpdate                               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/create-subscription.json    | Create subscription      | SubscriptionDto            | /billing/subscription/createOrUpdate                              | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/find-due-date-delay-ca.json | Find due date delay - ca |                            | /billing/subscription/dueDateDelay?subscriptionCode=DELAY_SUB_164 | GET            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/09-due-date-delay/activate-services.json      | Activate services        | ActivateServicesRequestDto | /billing/subscription/activateServices                            | POST           |        200 | SUCCESS |           |         |        |          |
  