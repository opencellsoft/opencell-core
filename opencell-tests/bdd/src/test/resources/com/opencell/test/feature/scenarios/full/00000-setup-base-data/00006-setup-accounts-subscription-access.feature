@full
Feature: Setup base data - Setup accounts/subscription/access

  @admin @superadmin
  Scenario Outline: Create <entity>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                                               | entity             | dto                | api                                     | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_customer_1.json         | Customer 1         | CustomerDto        | /account/customer/createOrUpdate        | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_customer_2.json         | Customer 2         | CustomerDto        | /account/customer/createOrUpdate        | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_customer_3.json         | Customer 2         | CustomerDto        | /account/customer/createOrUpdate        | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_customer_account_1.json | Customer Account 1 | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_customer_account_2.json | Customer Account 2 | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_customer_account_3.json | Customer Account 3 | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_billing_account_1.json  | Billing Account 1  | BillingAccountDto  | /account/billingAccount/createOrUpdate  | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_billing_account_2.json  | Billing Account 2  | BillingAccountDto  | /account/billingAccount/createOrUpdate  | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_billing_account_3.json  | Billing Account 3  | BillingAccountDto  | /account/billingAccount/createOrUpdate  | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_user_account_1.json     | User Account 1     | UserAccountDto     | /account/userAccount/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_user_account_2.json     | User Account 2     | UserAccountDto     | /account/userAccount/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_user_account_3.json     | User Account 3     | UserAccountDto     | /account/userAccount/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_subscription.json       | Subscription       | SubscriptionDto    | /billing/subscription/createOrUpdate    | CreateOrUpdate |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-accounts-subscription-access/create_access.json             | Access             | AccessDto          | /account/access/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |
