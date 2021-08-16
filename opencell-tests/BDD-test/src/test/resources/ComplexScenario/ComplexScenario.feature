@Main
   # The objective of this scenario is to verify a complex scenario of entity Seller
Feature: Testing complex scenario

   Background:  System is configured.

   Scenario Outline: Complex Scenario

      Given  complex scenario composed of "<scenario>"
      Then   execute a complex scenario

      Examples:
         | scenario                 |
         | UpdateSeller and DeleteSeller |
#         | UpdateSeller CreateSubscriptionForSeller CreateBilling DeleteSeller |

      #Execute @Main, ensuite ce scénario sera exécuté, qui déclenche les autres features
