@full
Feature: Tests

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
      | jsonFile                                                                                                      | title                                       | dto                    | api                                                                | action         | statusCode | status  | errorCode | message | entity       | expected                                                                            |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/create-cft-for-userAccount.json                  | Create CFT for User Account                 | CustomFieldTemplateDto | /entityCustomization/field/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                     |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/create-cft-for-subscription_1.json               | Create CFT for Subscription                 | CustomFieldTemplateDto | /entityCustomization/field/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                     |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/create-cft-for-subscription_2-not-inherited.json | Create CFT for Subscription - Not Inherited | CustomFieldTemplateDto | /entityCustomization/field/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                     |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/create-userAccount_3-temp.json                   | Create User Account 3 - Temp                | UserAccountDto         | /account/userAccount                                               | Create         |        200 | SUCCESS |           |         |              |                                                                                     |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/create-subscription.json                         | Create Subscription                         | SubscriptionDto        | /billing/subscription/createOrUpdate                               | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                     |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/find-subscription.json                           | Find subscription                           |                        | /billing/subscription?subscriptionCode=RS_FULL_432_SUB_InheritedCF | GET            |        200 | SUCCESS |           |         | subscription | scenarios/full/00005-subscription-with-inherited-cf/01-tests/find-subscription.json |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/remove-cft-ua.json                               | Remove CFT UA                               |                        | /entityCustomization/field/RS_FULL_UA_CF_INHERITED/UserAccount       | DEL            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/remove-cft-sub_1.json                            | Remove CFT SUB 1                            |                        | /entityCustomization/field/RS_FULL_SUB_CF_INHERITED/Subscription     | DEL            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00005-subscription-with-inherited-cf/01-tests/remove-cft-sub_2.json                            | Remove CFT SUB 2                            |                        | /entityCustomization/field/RS_FULL_SUB_CF_NOT_INHERITED/Subscription | DEL            |        200 | SUCCESS |           |         |        |          |
