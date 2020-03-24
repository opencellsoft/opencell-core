@full
Feature: Charge filter expression

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
      | jsonFile                                                                                             | title                        | dto                        | api                                                             | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00007-el-expression-tests/02-charge-filter-expression/update-recurring-3.json         | Update recurring 3           | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/02-charge-filter-expression/create-subscription.json        | Create subscription          | SubscriptionDto            | /billing/subscription/createOrUpdate                            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/02-charge-filter-expression/activate-services.json          | Activate services            | ActivateServicesRequestDto | /billing/subscription/activateServices                          | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/02-charge-filter-expression/find-subscription.json          | Find subscription            |                            | /billing/subscription?subscriptionCode=RS_FULL_43_CHARGE_FILTER | GET            |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00007-el-expression-tests/02-charge-filter-expression/update-recurring-3-restore.json | Update recurring 3 - restore | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
