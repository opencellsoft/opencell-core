@full
Feature: Counters

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
      | jsonFile                                                                                                                    | title                               | dto                        | api                                    | action         | statusCode | status  | errorCode     | message                   | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/create-subscription_3.json              | Create subscription 3               | SubscriptionDto            | /billing/subscription/createOrUpdate   | CreateOrUpdate |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/activate-services-sub_3-service_4.json  | Activate services sub 3 service 4   | ActivateServicesRequestDto | /billing/subscription/activateServices | POST           |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/create-accessPoint_3.json               | Create AccessPoint 3                | AccessDto                  | /account/access/createOrUpdate         | CreateOrUpdate |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/charge-cdr-counter-100-50eq50.json      | Charge cdr counter 100 -50 = 50     | String                     | /billing/mediation/chargeCdr           | POST           |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/charge-cdr-counter-50-(-30)eq80.json    | Charge cdr counter 50-(-30) = 80    | String                     | /billing/mediation/chargeCdr           | POST           |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/charge-cdr-counter-50-180eqfailure.json | Charge cdr counter 50-180 = Failure | String                     | /billing/mediation/chargeCdr           | POST           |        500 | FAIL    | RATING_REJECT | No charge matched for EDR |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/charge-cdr-counter-80-80eq0.json        | Charge cdr counter 80-80 = 0        | String                     | /billing/mediation/chargeCdr           | POST           |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/charge-cdr-counter-0-(-45)eq45.json     | Charge cdr counter 0-(-45)= 45      | String                     | /billing/mediation/chargeCdr           | POST           |        200 | SUCCESS |               |                           |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/09-counters/charge-cdr-counter-45-45eq0.json        | Charge cdr counter 45-45 = 0        | String                     | /billing/mediation/chargeCdr           | POST           |        200 | SUCCESS |               |                           |        |          |
