# Background in Cucumber is used to define a step or series of steps that are common to all the tests
# in the feature file. It allows you to add some context to the scenarios for a feature where it is defined.
# A Background is much like a scenario containing a number of steps. But it runs before each and every
# scenario were for a feature in which it is defined.


Feature: Define Background Feature
Description: As we know, before executing test scenarios, we create a preconfigured dataset so that
  scenarios can be executed based on this dataset. However, we also give a possibility for users to
  modify this preconfigured dataset through the usage of Background in Cucumber, which is used to
  define a step or series of steps that are common to all the tests in the feature file.

  Background: Update a seller
    Given Seller OPENSOFT
    When  I update Seller
      | code     | description        | languageCode | currencyCode |
      | OPENSOFT | OPENSOFT Levallois | ENG          | EUR          |
    Then  seller should be updated

#  Background: Create a seller
#    When  I create Seller
#    Then  seller should be created



