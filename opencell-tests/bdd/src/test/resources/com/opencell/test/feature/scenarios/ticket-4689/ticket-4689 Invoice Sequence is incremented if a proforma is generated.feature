@ticket-4689 @scenarios
Feature: Invoice Sequence is incremented if a proforma is generated

  @admin @superadmin
  Scenario Outline: Create a Customer
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                     | dto         | api                              | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-4689/C.json | CustomerDto | /account/customer/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  @admin @superadmin
  Scenario Outline: Create a Customer Account
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                      | dto                | api                                     | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-4689/CA.json | CustomerAccountDto | /account/customerAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  @admin @superadmin
  Scenario Outline: Create a Billing Account
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                      | dto               | api                                    | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-4689/BA.json | BillingAccountDto | /account/billingAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  @admin @superadmin
  Scenario Outline: Create a user Account
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The user account is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                      | dto            | api                                 | action         | statusCode | status  | errorCode | message |
      | scenarios/ticket-4689/UA.json | UserAccountDto | /account/userAccount/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  @admin @superadmin
  Scenario Outline: Subscribe and Activate Services by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The subscription is created and activated
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                      | dto                                         | api                                                | action | statusCode | errorCode | message | status  |
      | scenarios/ticket-4689/SA.json | SubscriptionAndServicesToActivateRequestDto | /billing/subscription/subscribeAndActivateServices | POST   |        200 |           |         | SUCCESS |

  @admin @superadmin
  Scenario Outline: Create wallet operation
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The wallet operation is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                      | dto                | api                       | action | statusCode | status  | errorCode | message |
      | scenarios/ticket-4689/WO.json | WalletOperationDto | /billing/wallet/operation | POST   |        200 | SUCCESS |           |         |

  @admin @superadmin
  Scenario Outline: Generate Invoice by API
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The invoice is generated
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The field "<field>" exists
    And The field "<field>" is equal to "<value>"

    Examples: 
      | jsonFile                                    | dto                       | api                           | action | statusCode | status  | errorCode | message | field                                     | value                 |
      | scenarios/ticket-4689/generateInvoice1.json | GenerateInvoiceRequestDto | /invoice/generateInvoice      | POST   |        200 | SUCCESS |           |         | generateInvoiceResultDto[0].invoiceNumber | INV-CLASSIC-000000009 |
      | scenarios/ticket-4689/generateProforma.json | GenerateInvoiceRequestDto | /invoice/generateDraftInvoice | POST   |        200 | SUCCESS |           |         | generateInvoiceResultDto[0].invoiceNumber | DRAFT_00001           |
      | scenarios/ticket-4689/generateInvoice2.json | GenerateInvoiceRequestDto | /invoice/generateInvoice      | POST   |        200 | SUCCESS |           |         | generateInvoiceResultDto[0].invoiceNumber | INV-CLASSIC-000000010 |
