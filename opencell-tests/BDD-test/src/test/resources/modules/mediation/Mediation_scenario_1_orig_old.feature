   # The objective of this functional scenario is to verify the amount with taxes created
   # by mediation process
   Feature: Testing mediation process

     Background:  Opencell dataset has been configured

     @MediationProcessOrigOld1
     Scenario: Testing mediation process

       Given  I create entity "Subscription", with field and value
         | code           | description                    | userAccount | offerTemplate |
         | subCodeThang_4 | a description for Subscription | OPENSOFT-01 | OF_BASIC      |
       When   I activate services on subscription
         | subscription   | subscriptionDate         | code        | quantity |
         | subCodeThang_2 | 2019-12-15T01:23:45.678Z | SE_USG_UNIT | 1        |
       And    I update service on subscription
         | subscriptionCode | code        | quantity |
         | subCodeThang_2   | SE_USG_UNIT | 1        |
       And    I charge following CDR
         | 2020-10-05T03:15:45.000Z;1;subCodeThang_1;UNIT;PS_SUPPORT |
       Then   "1" "walletOperation" has "amountWithTax" of value "253.20" euros


       Given AccessPoint "codeAccessPoint"
         | code      | subscription |
         | code_cp_1 | code_sub_1   |
         | code_cp_2 | code_sub_2   |
       And    I charge following CDR for
         | 2020-10-05T03:15:45.000Z;1;{{codeAccessPoint}};UNIT;PS_SUPPORT |  |  |
         | 2020-10-05T03:15:46.000Z;1;{{codeAccessPoint}};UNIT;PS_SUPPORT |  |  |
         | 2020-10-05T03:15:47.000Z;1;{{codeAccessPoint}};UNIT;PS_SUPPORT |  |  |
       Then   "1" "walletOperation" has "amountWithTax" of value "253.20" euros
         | 2020-10-05T03:15:45.000Z;1;acp1;UNIT;PS_SUPPORT |  |  |
         | 2020-10-05T03:15:46.000Z;1;acp2;UNIT;PS_SUPPORT |  |  |
         | 2020-10-05T03:15:47.000Z;1;acp1;UNIT;PS_SUPPORT |  |  |

     # Voir sur Cucumber comment ils font les tests de combinaison


       Given  I create entity "Subscription", with field and value
         | code           | description                    | userAccount | offerTemplate |
         | subCodeThang_4 | a description for Subscription | OPENSOFT-01 | OF_BASIC      |
       # Define custom fields before activating services
       Given  I create entity "Subscription", with field and value
    | name |	value	| code |
     |CF1 |	150	| CF_SE_DOUBLE |
     |CF2 |	180	| CF_SE_DOUBLE |
       When   I activate services on subscription
         | subscription   | subscriptionDate         | customer    | quantity | customField |
         | subCodeThang_2 | 2019-12-15T01:23:45.678Z | SE_USG_UNIT | 1        | CF1         |
       And    I update service on subscription
         | subscriptionCode | code        | quantity |
         | subCodeThang_2   | SE_USG_UNIT | 1        |