@full @ignore
Feature: Sub creation, Service Activation, Charging and invoicing - Charge and rate triggered EDR

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
      | jsonFile                                                                                                                                                          | title                                                  | dto                        | api                                                                                                                                                                                                      | action         | statusCode | status  | errorCode | message | entity              | expected                                                                                                                                                                |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/create-subscription.json                                 | Create subscription                                    | SubscriptionDto            | /billing/subscription/createOrUpdate                                                                                                                                                                     | CreateOrUpdate |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/activate-service-on-subscription.json                    | Activate service on subscription                       | ActivateServicesRequestDto | /billing/subscription/activateServices                                                                                                                                                                   | POST           |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/create-accessPoint.json                                  | Create AccessPoint                                     | AccessDto                  | /account/access/createOrUpdate                                                                                                                                                                           | CreateOrUpdate |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/charge-cdr-all-params-enabled.json                       | charge CDR all params enabled                          | String                     | /billing/mediation/chargeCdr?returnWalletOperations=true&isVirtual=true&rateTriggeredEdr=true&maxDepth=3                                                                                                 | POST           |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/find-wallet-operations--no-wallet-operation.json         | Find wallet operations - no wallet operation           |                            | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_BASE_UA\|chargeInstance.subscription.code:RS_FULL_200_SUB_CDR\|chargeInstance.code:RS_BASE_USAGE1&limit=50&sortBy=id&sortOrder=ASCENDING | GET            |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/charge-cdr-virtual&no-wallet-operation.json              | charge CDR - virual & no wallet operation              | String                     | /billing/mediation/chargeCdr?returnWalletOperations=false&isVirtual=true&rateTriggeredEdr=true&maxDepth=3                                                                                                | POST           |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/find-wallet-operations--virtual-no-wallet-operation.json | Find wallet operations - virtual - no wallet operation |                            | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_BASE_UA\|chargeInstance.subscription.code:RS_FULL_200_SUB_CDR\|chargeInstance.code:RS_BASE_USAGE1&limit=50&sortBy=id&sortOrder=ASCENDING | GET            |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/charge-cdr-create-wallet-operation.json                  | charge CDR - create wallet operation                   | String                     | /billing/mediation/chargeCdr?returnWalletOperations=false&isVirtual=false&rateTriggeredEdr=false&maxDepth=3                                                                                              | POST           |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/find-wallet-operations--create-wallet-operation.json     | Find wallet operations - created wallet operation      |                            | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_BASE_UA\|chargeInstance.subscription.code:RS_FULL_200_SUB_CDR\|chargeInstance.code:RS_BASE_USAGE1&limit=50&sortBy=id&sortOrder=ASCENDING | GET            |        200 | SUCCESS |           |         | walletOperations[0] | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/find-wallet-operations--create-wallet-operation--expected.json |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/12-charge-and-rate-triggered-EDR/charge-cdr-rateTriggeredEDR-maxDepth.json                | charge CDR - rateTriggered EDR - maxDepth              | String                     | /billing/mediation/chargeCdr?returnWalletOperations=true&isVirtual=true&rateTriggeredEdr=true&maxDepth=2                                                                                                 | POST           |        200 | SUCCESS |           |         |                     |                                                                                                                                                                         |
