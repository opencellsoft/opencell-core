# First, testing subscription and services should be separated
# To test services we do not need many subscriptions

Feature: Test of service activation
  In order to activate a service
  As a customer care
  I want to activate service for a customer

  @SubscriptionCreation
  Scenario Outline: Create new subscriptions

    Given I create Subscription
      | code      | description   | userAccount   | offerTemplate   |
      | <codeSub> | <description> | <userAccount> | <offerTemplate> |
    Then  Subscription is successfully created

    Examples:
      | codeSub   | description     | userAccount | offerTemplate |
      | subCode_5 | a description 5 | OPENSOFT-01 | OF_BASIC      |
      | subCode_6 | a description 6 | OPENSOFT-01 | OF_BASIC      |


