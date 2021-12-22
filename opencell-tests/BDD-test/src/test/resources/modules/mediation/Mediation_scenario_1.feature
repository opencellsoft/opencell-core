   # The objective of this functional scenario is to verify the amount with taxes created
   # by mediation process
   Feature: Testing mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1
     Scenario Outline: Testing mediation process

       Given AccessPoint
        | accessCode   | subscriptionCode   |
        | <accessCode> | <subscriptionCode> |
       When  I charge following cdr <cdrLine>
       Then  last response has field <field> whose value is <value>

      Examples:
        | accessCode | subscriptionCode | field         | value | cdrLine                                              |
        | code_ap_1  | OPENSOFT-01-SU   | amountTax     | 30.0  | 2020-10-05T03:15:45.000Z;1;code_ap_1;UNIT;PS_SUPPORT |
        | code_ap_2  | OPENSOFT-01-SU   | amountWithTax | 180.0 | 2015-10-05T03:15:46.000Z;1;code_ap_2;UNIT;PS_SUPPORT |
        | code_ap_3  | OPENSOFT-01-SU   | status        | FAIL  | 2015-10-05T03:15:46.000Z;1;code_ap_2;ABC;PS_SUPPORT  |
#        | code_ap_3  | OPENSOFT-01-SU   | walletOperationCount | 1      | 2019-10-05T03:15:47.000Z;1;{{accessCode}};UNIT;PS_SUPPORT |
#        | code_ap_4  | OPENSOFT-01-SU   | amountWithTax        | 253.20 | 2019-10-05T03:15:47.000Z;1;{{accessCode}};UNIT;PS_SUPPORT |
