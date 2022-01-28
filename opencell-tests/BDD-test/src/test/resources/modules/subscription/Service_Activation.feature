# First, testing subscription and services should be separated
# To test services we do not need many subscriptions

Feature: Test of service activation
  In order to activate a service
  As a customer care
  I want to activate service for a customer

#  @SubscriptionCreation
#  Scenario: Create a new subscription
#
#    Given I create Subscription
#      | code      | description     | userAccount | offerTemplate |
#      | subCode_1 | a description 1 | OPENSOFT-01 | OF_BASIC      |
#      | subCode_2 | a description 2 | OPENSOFT-01 | OF_BASIC      |
#    Then  Subscription is successfully created


  @ServiceActivation
  Scenario Outline: Activate usage services

    Given Subscription <subCode>
    And   ServiceInstance SE_USG_UNIT,SE_OSS,SE_REC_ADV
    When  I activate services
      | ServiceCode | CF_Code         | value            | quantity | subscriptionDate         | attributeName | attributeValue           |
      | SE_USG_UNIT | CF_SE_DOUBLE    | 60               | 1        | 2019-12-15T01:23:45.678Z | rateUntilDate | 2020-01-15T01:23:45.678Z |
      | SE_USG_UNIT | CF_SE_STRING    | my string        |          |                          |               |                          |
      | SE_OSS      | CF_SE_DATATABLE | #{{data.value1}} | 1        | #{{TODAY}}               |               |                          |
      | SE_REC_ADV  | CF_SE_DOUBLE    | 40               |          |                          |               |                          |
    Then  these services are activated

    Examples:
      | subCode   |
      | subCode_1 |
      | subCode_2 |




  @ServiceActivation
  Scenario Outline: Activate usage services

#    We can have two possibilities in our language to execute the request GET :
#    1st way : Get an entity inline as in the example below
#    2nd way : Get an entity with help of an datatable? When use datatable : for example, need
#    to define many path or query parameters in the get request.
    Given Subscription <subCode>
    And   ServiceInstance SE_USG_UNIT,SE_OSS,SE_REC_ADV
    When  I activate services
      | ServiceCode | CF_Code         | value            | quantity | subscriptionDate         | attributeName | attributeValue           |
      | SE_USG_UNIT | CF_SE_DOUBLE    | 60               | 1        | 2019-12-15T01:23:45.678Z | rateUntilDate | 2020-01-15T01:23:45.678Z |
      | SE_USG_UNIT | CF_SE_STRING    | my string        |          |                          |               |                          |
      | SE_OSS      | CF_SE_DATATABLE | #{{data.value1}} | 1        | #{{TODAY}}               |               |                          |
      | SE_REC_ADV  | CF_SE_DOUBLE    | 40               |          |                          |               |                          |
    # check what to check to verify the correctness of activation
    # need to check that service is active
    # check the rating of CDRs
    Then I will be able to rate CDRs
    And  these services are terminated
    # add or sample to check

    Examples:
      | subCode   | ServiceCode | status    |
      | subCode_1 | SE_USG      | ACTIVATED |
      | subCode_2 | SE_OSS      | ACTIVATED |


  @CDRRating
  Scenario Outline: Rating a CDR line

    Given AccessPoint
      | accessCode   | subscriptionCode   |
      | <accessCode> | <subscriptionCode> |
    When  I charge following cdr <cdrLine>
    Then  <field> has a value of <value>

    Examples:
      | accessCode | subscriptionCode | field     | value | cdrLine                                              |
      | subCode_1  | SE_USG           | amountTax | 30.0  | 2020-10-05T03:15:45.000Z;1;code_ap_1;UNIT;PS_SUPPORT |


  @InvoiceGeneration
  Scenario Outline: Generating an invoice

    Given BillingAccount
      | code        |
      | OPENSOFT-01 |
    When  I create Invoice
      | billingAccountCode | level          | invoicingDate       | attributeName | attributeValue |
      | OPENSOFT-01        | BillingAccount | 2020-11-10T00:00:00 | generatePDF   | true           |
    Then  status is <statusCode>
    And  <field> has a value of <value>

    Examples:
      | field     | value |
      | amountTax | 180.0 |
      | netToPay  | 180.0 |
      | amountTax | 30.0  |


