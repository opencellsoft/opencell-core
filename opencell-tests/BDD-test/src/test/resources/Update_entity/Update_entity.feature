@TestUpdateEntity
   # The objective of this scenario is to verify whether any entity can
   # be updated by API
Feature: Testing method Update on an entity

   Background:  System is configured.

   Scenario Outline: Update an entity

      Given  Update "<entity>" with "<id>"
      When   All fields tested
      And    Fields filled by "<jsonFile>"
      Then   The status is <status>

      Examples:
         | entity          | id  | jsonFile   |  status  |
#         | seller          |  3   | 200    | A description        |
#         | provider        |  1   | 200    | Provider.json        |
         | user            | 3   | User.json  |   200    |
#         | tradingCurrency | -2  | 200    | TradingCurrency.json |

