   # The objective of this functional scenario is to verify the amount with taxes created
   # by Opencell mediation process
   Feature: Testing method mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess2Old
     Scenario Outline: Testing mediation process

#       Given  Thang creates entity "<SubscriptionDto>"
#       And    Thang updates service "<UpdateServicesRequestDto>" on subscription
#       And    Thang imports CDR "<CDR>"
#       When   Thang activates services "<ActivateServicesRequestDto>" on subscription
#       Then   amount with tax of <order> wallet operation should be equal to <amount> euros

       Examples:
         | SubscriptionDto                                                                                                                         | ActivateServicesRequestDto                                                                                                                                                            | CDR                                                            | UpdateServicesRequestDto                                                                                                                                                           | order | amount |
         | {\"code\":\"subCode\",\"userAccount\":\"OPENSOFT-01\",\"offerTemplate\":\"OF_BASIC\",\"subscriptionDate\":\"2019-12-15T01:23:45.678Z\"} | {\"subscription\":\"subCode\",\"servicesToActivate\":{\"service\":[{\"code\":\"SE_USG_UNIT\",\"customFields\":{\"customField\":[{\"code\":\"CF_SE_DOUBLE\",\"doubleValue\":150}]}}]}} | 2020-10-05T00:00:00.000Z;1;{{subscription.code}};{{p1}};{{p2}} | {\"subscriptionCode\":\"OPENSOFT-01-SU\",\"serviceToUpdate\":[{\"code\":\"SE_USG_UNIT\",\"customFields\":{\"customField\":[{\"code\":\"CF_SE_DOUBLE\",\"doubleValue\":225.00}]}}]} | 3     | 253.10 |
