   # The objective of this functional scenario is to verify the amount with taxes created
   # by Opencell mediation process
   Feature: Testing mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1
     Scenario: Testing mediation process

       Given  I create entity "Subscription", with field and value
         | /code          | /description                   | /userAccount | /offerTemplate |
         | subCodeThang_1 | a description for Subscription | OPENSOFT-01  | OF_BASIC       |
       When   I activate services on subscription
         | /subscription  | /servicesToActivate/service/0/subscriptionDate | /servicesToActivate/service/0/code | /servicesToActivate/service/0/quantity |
         | subCodeThang_1 | 2019-12-15T01:23:45.678Z                       | SE_USG_UNIT                        | 1                                      |
       And    I update service on subscription
         | /subscriptionCode | /serviceToUpdate/0/code | /serviceToUpdate/0/quantity |
         | subCodeThang_1    | SE_USG_UNIT             | 1                           |
       And    I charge following CDR
         | 2020-10-05T03:15:45.000Z;1;subCodeThang_1;UNIT;PS_SUPPORT |
       Then   "1" "walletOperation" has "amountWithTax" of value "253.20" euros

#       Examples:
#         |  |  |  | CDR      |  | order | amount |
#         |  |  |  | CDR.json |  | 1     | 253.20 |
