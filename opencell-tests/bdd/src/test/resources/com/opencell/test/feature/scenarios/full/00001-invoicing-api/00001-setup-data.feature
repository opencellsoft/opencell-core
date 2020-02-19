@full
Feature: Invoicing - Setup Data

  # @admin @superadmin
  # Scenario Outline: Rated Transaction Job - Clear
  @admin @superadmin
  Scenario Outline: Create InvoiceType - ENG
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The invoice type is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                  | dto            | api                         | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/setup-data/create_invoiceType_eng.json | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  @admin @superadmin
  Scenario Outline: Create Billing Cycle by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The billing cycle is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                               | dto             | api                          | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/setup-data/create_billingCycle.json | BillingCycleDto | /billingCycle/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create Account Hierarchy by API
    Given The entity has the following information "<jsonFile>"
    When I call the "<action>" "<api>"
    Then The account hierarchy is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                       | dto                 | api                                      | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/setup-data/create_accountHierarchy_eng.json | AccountHierarchyDto | /account/accountHierarchy/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |

  Scenario Outline: Create service template by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The service template is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto                | api                                     | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/setup-data/create_service_1.json | ServiceTemplateDto | /catalog/serviceTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
