   # The objective of this functional scenario is to verify the amount with taxes created
   # by mediation process
   Feature: Testing mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1
     Scenario: Testing mediation process

       Given AccessPoint
         | code      |
         | code_ap_1 |
         | code_ap_2 |
#       And   I charge following list of CDR
#         | 2020-10-05T03:15:45.000Z;1;{{code}};UNIT;PS_SUPPORT |
#         | 2015-10-05T03:15:46.000Z;1;{{code}};ABC;PS_SUPPORT  |
#         | 2019-10-05T03:15:47.000Z;1;{{code}};UNIT;PS_SUPPORT |
#       Then  <order> <entity> with <code> has <field> whose value is <value>
#         | order | entity          | field         | value  |
#         | 1     | WalletOperation | amountWithTax | 253.20 |
#         | 2     | WalletOperation | amountWithTax | 253.20 |
#         | 3     | WalletOperation | amountWithTax | 253.20 |
#         | 4     | WalletOperation | amountWithTax | 253.20 |
#         | 5     | WalletOperation | amountWithTax | 253.20 |
#         | 6     | WalletOperation | amountWithTax | 253.20 |

