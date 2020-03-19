@full
Feature: Sub creation, Service Activation, Charging and invoicing - USE UnitOfMeasure

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
      | jsonFile                                                                                                                      | title                       | dto                        | api                                                         | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-usage-charge.json         | CREATE USAGE CHARGE         | UsageChargeTemplateDto     | /catalog/usageChargeTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-PP.json                   | CREATE PP                   | PricePlanMatrixDto         | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-service.json              | CREATE SERVICE              | ServiceTemplateDto         | /catalog/serviceTemplate/createOrUpdate                     | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-billingCycle.json         | CREATE BILLING CYCLE        | BillingCycleDto            | /billingCycle/createOrUpdate                                | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-offerTemplate.json        | CREATE OFFER TEMPLATE       | OfferTemplateDto           | /catalog/offerTemplate/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-account.json              | CREATE ACCOUNT              | CRMAccountHierarchyDto     | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/subscription-2019-01-01.json     | Subscription 2019-01-01     | SubscriptionDto            | /billing/subscription/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/create-accessPoint.json          | CREATE ACCESS POINT         | AccessDto                  | /account/access/createOrUpdate                              | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/activate-service-2019-01-01.json | activateServices 2019-01-01 | ActivateServicesRequestDto | /billing/subscription/activateServices                      | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/13-USE-unitOFMeasure/charge-cdr.json                  | chargeCDR                   | String                     | /billing/mediation/chargeCdr                                | POST           |        200 | SUCCESS |           |         |        |          |
