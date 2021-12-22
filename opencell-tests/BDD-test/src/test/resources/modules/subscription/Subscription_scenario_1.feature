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
       When   I define
         | /name     | /code     | /doubleValue |
         | <nameCF1> | <codeCF1> | <valueCF1>   |
         | <nameCF2> | <codeCF2> | <valueCF2>   |
       And    I define following list
         | /name     | /customField        |
         | <listCFs> | <nameCF1>,<nameCF2> |
       And    I define
         | /name      | /code         | /quantity  | /subscriptionDate  | /customFields |
         | <nameSer>  | <codeService> | <quantity> | <subscriptionDate> | <listCFs>     |
       And    I define following list
         | /name      | /service  |
         | <listSers> | <nameSer> |
       ########### Now activate services on subscription
       And    I activate services on subscription
         | /subscription | /servicesToActivate |
         | <codeSub>     | <listSers>          |
       Then last response has field <field> whose value is <value>

       Examples:
         | codeSub        | description   | userAccount | offerTemplate | nameCF1 | codeCF1       | valueCF1 | nameCF2 | codeCF2       | valueCF2 | listCFs | listSers | nameSer | codeService | quantity | subscriptionDate         | field  | value |
         | subCodeThang_1 | a description | OPENSOFT-01 | OF_BASIC      | CF1     | CF_SE_DOUBLE  | 150      | CF2     | CF_SE_DOUBLE  | 180      | CFs     | SERS     | SER1    | SE_USG_UNIT | 1        | 2019-12-15T01:23:45.678Z | status | FAIL  |
         | subCodeThang_2 | a description | OPENSOFT-01 | OF_BASIC      | CF3     | CF_SE_DOUBLE  | 110      | CF4     | CF_SE_DOUBLE  | 60       | CFs     | SERS     | SER2    | SE_USG_UNIT | 1        | 2019-12-15T01:23:45.678Z | status | FAIL  |
         | subCodeThang_2 | a description | OPENSOFT-01 | OF_BASIC      | CF3     | CF_SE_DOUBLE  | 110      | CF4     | CF_SE_DOUBLE  | 60       | CFs     | SERS     | SER2    | SE_USG_UNIT | 1        | 2019-12-15T01:23:45.678Z | status | FAIL  |


