   # The objective of this functional scenario is to verify the amount with taxes created
   # by Opencell mediation process
   Feature: Testing mediation process

     Background:  Opencell dataset has been configured

     @MediationProcess1
     Scenario Outline: Testing mediation process

       Given  I create entity "Subscription", with field and value offerTemplate : OF_BASIC code : subCode
       When   I activate services "ActivateServicesRequestDto.json" on subscription
       And    I update service "UpdateServicesRequestDto.json" on subscription
       And    I import CDR "<CDR>"
       Then   amount with tax of wallet operation should be equal to <amount> euros

       Examples:
         |  |  |  | CDR      |  | order | amount |
         |  |  |  | CDR.json |  | 1     | 253.20 |
