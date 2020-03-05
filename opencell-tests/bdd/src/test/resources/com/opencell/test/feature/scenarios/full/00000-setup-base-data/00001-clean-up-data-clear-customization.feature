@full
Feature: Setup base data - clean up data - clear customizations

  @admin @superadmin
  Scenario Outline: Clear <entity> customizations
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The entity is cleared
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                                            | entity                | api                             | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_seller_customizations.json             | seller                | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_customer_customizations.json           | customer              | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_customer_account_customizations.json   | customer account      | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_billing_account_customizations.json    | account customization | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_user_account_customizations.json       | user account          | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_subscription_customizations.json       | subscription          | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_access_customizations.json             | access                | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_provider_customizations.json           | provider              | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_productTemplate_customizations.json    | productTemplate       | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_productInstance_customizations.json    | productInstance       | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_serviceTemplate_customizations.json    | serviceTemplate       | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_serviceInstance_customizations.json    | serviceInstance       | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_offerTemplate_customizations.json      | offerTemplate         | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_chargeTemplate_customizations.json     | chargeTemplate        | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_pricePlan_customizations.json          | pricePlan             | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_invoiceCategory_customizations.json    | invoiceCategory       | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_invoiceSubCategory_customizations.json | invoiceSubCategory    | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_accountOperations_customizations.json  | accountOperation      | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_invoice_customizations.json            | invoice               | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_tax_customizations.json                | tax                   | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_billingCycle_customizations.json       | billingCycle          | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/clean-up-data-clear-customization/clear_purgeJob_customizations.json           | purgeJob              | /entityCustomization/customize/ | PUT    |        200 | SUCCESS |           |         |
