@full
Feature: Setup base data - Setup Configuration

  @admin @superadmin
  Scenario Outline: Update Provider
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The provider is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                      | dto         | api        | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-configuration/update_provider.json | ProviderDto | /provider/ | Update |        200 | SUCCESS |           |         |

  Scenario Outline: Create InvoiceType
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice type is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                         | dto            | api                         | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-configuration/create_invoiceType.json | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create <entity>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing cycle is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                           | entity         | dto             | api                          | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-configuration/create_billingCycle.json  | BillingCycle   | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-configuration/create_billingCycle3.json | BillingCycle 3 | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create Seller
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The seller is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                    | dto       | api                    | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-configuration/create_seller.json | SellerDto | /seller/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
