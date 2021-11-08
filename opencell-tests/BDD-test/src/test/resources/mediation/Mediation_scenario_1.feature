   # The objective of this functional scenario is to verify the amount with taxes created
   # by Opencell mediation process
   Feature: Testing method mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1
     Scenario Outline: Testing mediation process

       Given  Thang creates or updates entity "<SubscriptionDto>"
       When   Thang activates services "<ActivateServicesRequestDto>" on subscription
       And    Thang imports CDR "<CDR>"
       And    Thang updates service "<UpdateServicesRequestDto>" on subscription
       Then   amount with tax of <order> wallet operation should be equal to <amount> euros

       Examples:
         | SubscriptionDto                                                                                                                         | ActivateServicesRequestDto                                                                                                                                                                                                                             | CDR                                                | UpdateServicesRequestDto                                                                                                                                                    | order | amount |
         | {\"code\":\"subCode\",\"userAccount\":\"OPENSOFT-01\",\"offerTemplate\":\"OF_BASIC\",\"subscriptionDate\":\"2019-12-15T01:23:45.678Z\"} | {\"subscription\":\"subCode\",\"servicesToActivate\":{\"service\":[{\"code\":\"SE_USG_UNIT\",\"quantity\":1,\"subscriptionDate\":\"2020-10-05T03:15:45.000Z\",\"customFields\":{\"customField\":[{\"code\":\"CF_SE_DOUBLE\",\"doubleValue\":150}]}}]}} | 2020-10-05T03:15:45.000Z;1;subCode;UNIT;PS_SUPPORT | {\"subscriptionCode\":\"subCode\",\"serviceToUpdate\":[{\"code\":\"SE_USG_UNIT\",\"customFields\":{\"customField\":[{\"code\":\"CF_SE_DOUBLE\",\"doubleValue\":277.00}]}}]} | 1     | 253.20 |
