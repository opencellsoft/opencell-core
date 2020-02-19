@ticket-2878 @scenarios @ignore
Feature: rateUntilDate X should not produce WO with date X

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Create calendar by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The calendar is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                            | dto         | api                      | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/calendar.json | CalendarDto | /calendar/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create tax by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The tax is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                       | dto    | api                 | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/tax.json | TaxDto | /tax/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create invoice category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                            | dto                | api                             | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/icat_rec.json | InvoiceCategoryDto | /invoiceCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/icat_usg.json | InvoiceCategoryDto | /invoiceCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create invoice subcategory by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoiceSubCategory is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                             | dto                   | api                                | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/iscat_rec.json | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/iscat_usg.json | InvoiceSubCategoryDto | /invoiceSubCategory/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create invoice subcategory country by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoiceSubCategoryCountry is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                              | dto                          | api                                       | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/iscatc_rec.json | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/iscatc_usg.json | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create Billing Cycle by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing cycle is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                | dto             | api                          | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/billingCycle.json | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create recurring charge by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The recurring charge is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                              | dto                        | api                                             | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/ch_rec_adv.json | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/ch_rec_arr.json | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create usage charge by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                               | dto                    | api                                         | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/ch_usg_unit.json | UsageChargeTemplateDto | /catalog/usageChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create price plan by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The price plan is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                               | dto                | api                               | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/pp_rec_adv.json  | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/pp_rec_arr.json  | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/pp_usg_unit.json | PricePlanMatrixDto | /catalog/pricePlan/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create service template
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The service template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                               | dto                | api                                     | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/se_rec_adv.json  | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/se_rec_arr.json  | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/ticket-2878/se_usg_unit.json | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create offer template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The offer template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                 | dto              | api                                   | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/offerTemplate.json | OfferTemplateDto | /catalog/offerTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create seller by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The seller is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                          | dto       | api                    | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/seller.json | SellerDto | /seller/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create Customer Category by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer category is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                               | dto                 | api                                      | action | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/customercat.json | CustomerCategoryDto | /account/customer/createOrUpdateCategory | Create |        200 | SUCCESS |           |         |

  Scenario Outline: Create CrM account hierarchy by API
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                        | api                                                         | action | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/c_ua.json | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | Create |        200 | SUCCESS |           |         |

  Scenario Outline: Create subscriptions
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The subscription is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                | dto             | api                                  | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/subscription.json | SubscriptionDto | /billing/subscription/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Activate subscriptions
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The subscription is activated
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                               | dto                        | api                                    | action | statusCode | status  | errorCode | message |
      | scenarios/ticket-2878/activateSub.json | ActivateServicesRequestDto | /billing/subscription/activateServices | POST   |        200 | SUCCESS |           |         |

  Scenario Outline: Find wallet and check if <fieldName> is equal to <value>
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then I get a generic response
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The field "<fieldName>" exists
    And The field "<fieldName>" is less than "<value>"

    Examples: 
      | jsonFile                            | api                            | action | statusCode | status  | errorCode | message | fieldName                         | value         |
      | scenarios/ticket-2878/walletOp.json | /billing/wallet/operation/find | POST   |        200 | SUCCESS |           |         | walletOperations[0].operationDate | 1496275200000 | 
      | scenarios/ticket-2878/walletOp.json | /billing/wallet/operation/find | POST   |        200 | SUCCESS |           |         | walletOperations[1].operationDate | 1496275200000 | 
      | scenarios/ticket-2878/walletOp.json | /billing/wallet/operation/find | POST   |        200 | SUCCESS |           |         | walletOperations[2].operationDate | 1496275200000 | 
      | scenarios/ticket-2878/walletOp.json | /billing/wallet/operation/find | POST   |        200 | SUCCESS |           |         | walletOperations[3].operationDate | 1496275200000 |
      | scenarios/ticket-2878/walletOp.json | /billing/wallet/operation/find | POST   |        200 | SUCCESS |           |         | walletOperations[4].operationDate | 1496275200000 |
#  1496275200000 = 2017/06/01, Date of rateUntilDate from activate subscription (activeSub.json)