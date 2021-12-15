   # The objective of this functional scenario is to verify the amount with taxes created
   # by mediation process
   Feature: Testing subscription module

     Background:  Opencell dataset has been configured

     @SubscriptionModule
     Scenario: Testing subscription module

       Given  I create entity "Subscription", with field and value
         | code           | description                    | userAccount | offerTemplate |
         | subCodeThang_1 | a description for Subscription | OPENSOFT-01 | OF_BASIC      |
       When   I activate services on subscription
         | subscription   | subscriptionDate         | code        | quantity |
         | subCodeThang_1 | 2019-12-15T01:23:45.678Z | SE_USG_UNIT | 1        |
       And    I update service on subscription
         | subscriptionCode | code        | quantity |
         | subCodeThang_1   | SE_USG_UNIT | 1        |
       Then   <entity> with <code> has <field> of value <value>
         | entity       | code           | field       | value         |
         | subscription | subCodeThang_1 | description | a description |

