   # The objective of this functional scenario is to verify the amount with taxes created
   # by mediation process
   Feature: Testing subscription module

     Background:  Opencell dataset has been configured

     @SubscriptionModule1
     Scenario Outline: Testing subscription module

       ########### Create a new subscription
       Given  I create Subscription
         | code      | description   | userAccount   | offerTemplate   |
         | <codeSub> | <description> | <userAccount> | <offerTemplate> |
       ########### Define fields before activating services
       # The objective is to, given API activate services, generate automatically
       # following phrases to create
       # Now, the scenario is very complicated to write for users, since they need to write every Json level
       When   I define for first customField for second service to activate services on subscription
       When   I define for first customField to activate services on subscription
#       When   I define for customField to activate services on subscription
         | name      | code      | doubleValue |
         | <nameCF1> | <codeCF1> | <valueCF1>  |
         | <nameCF2> | <codeCF2> | <valueCF2>  |
       When   I define for quantity for first service to activate services on subscription
         | name      | quantity   | customFields         |
         | <nameSer> | <quantity> | <nameCF1>, <nameCF2> |
       When   I define for subscriptionDate for first service to activate services on subscription
         | name      | subscriptionDate   | customFields         |
         | <nameSer> | <subscriptionDate> | <nameCF1>, <nameCF2> |
       ######### Now activate services on subscription
       And    I activate services
         | subscription | servicesToActivate | customField         | code          |
         | <codeSub>    | <listSers>         | <nameCF1>,<nameCF2> | <codeService> |
##       Then last response has field <field> whose value is <value>

       Examples:
         | codeSub        | description   | userAccount | offerTemplate | nameCF1 | codeCF1       | valueCF1 | nameCF2 | codeCF2       | valueCF2 | listSers | nameSer | codeService | quantity | subscriptionDate         | field  | value |
         | subCodeThang_1 | a description | OPENSOFT-01 | OF_BASIC      | CF1     | CF_SE_DOUBLE  | 150      | CF2     | CF_SE_DOUBLE  | 180      | SERS     | SER1    | SE_USG_UNIT | 1        | 2019-12-15T01:23:45.678Z | status | FAIL  |
#         | subCodeThang_2 | a description | OPENSOFT-01 | OF_BASIC      | CF3     | CF_SE_DOUBLE  | 110      | CF4     | CF_SE_DOUBLE  | 60       | SERS     | SER2    | SE_USG_UNIT | 1        | 2019-12-15T01:23:45.678Z | status | FAIL  |
#         | subCodeThang_2 | a description | OPENSOFT-01 | OF_BASIC      | CF3     | CF_SE_DOUBLE  | 110      | CF4     | CF_SE_DOUBLE  | 60       | SERS     | SER2    | SE_USG_UNIT | 1        | 2019-12-15T01:23:45.678Z | status | FAIL  |


