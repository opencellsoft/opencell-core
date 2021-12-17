   # The objective of this functional scenario is to verify the amount with taxes created
   # by mediation process
   Feature: Testing mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1
     Scenario Outline: Testing mediation process

       Given AccessPoint with <accessCode>
       When  I charge following "<cdrLine>"
#       Then  "<order>" response has "<field>" whose value is "<value>"
#         | order | field                | value  |
#         | 1     | amountTax            | 253.20 |
#         | 2     | amountWithoutTax     | 180.00 |
#         | 6     | amountWithTax        | 253.20 |
#         | 4     | walletOperationCount | 1      |


      Examples:

        | accessCode | subscriptionCode | cdrLine                                                   |
        | code_ap_1  | OPENSOFT-01-SU   | 2020-10-05T03:15:45.000Z;1;{{accessCode}};UNIT;PS_SUPPORT |
        | code_ap_2  | OPENSOFT-01-SU   | 2015-10-05T03:15:46.000Z;1;{{accessCode}};ABC;PS_SUPPORT |
        | code_ap_1  | OPENSOFT-01-SU   | 2019-10-05T03:15:47.000Z;1;{{accessCode}};UNIT;PS_SUPPORT |
        | 4     | walletOperationCount | 1      |