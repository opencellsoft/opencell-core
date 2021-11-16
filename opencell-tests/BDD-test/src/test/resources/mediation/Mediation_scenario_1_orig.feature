   # The objective of this functional scenario is to verify the amount with taxes created
   # by Opencell mediation process
   Feature: Testing method mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1Orig
     Scenario Outline: Testing mediation process

#       Given  I create entity "<entity>" from dto "<entityDto>"
#       When   I activate services "<ActivateServicesRequestDto>" on subscription
#       And    I update service "<UpdateServicesRequestDto>" on subscription
#       And    I import CDR "<CDR>"
#       Then   amount with tax of wallet operation should be equal to <amount> euros

       Examples:
         | feature                    | entity       | entityDto            | ActivateServicesRequestDto      | CDR      | UpdateServicesRequestDto      | order | amount |
         | CreateSubscription.feature | Subscription | SubscriptionDto.json | ActivateServicesRequestDto.json | CDR.json | UpdateServicesRequestDto.json | 1     | 253.20 |
