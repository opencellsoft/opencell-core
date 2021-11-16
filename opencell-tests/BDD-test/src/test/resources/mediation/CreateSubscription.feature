   # The objective of this scenario is to create a subscription
   Feature: Create a subscription

     Background:  Opencell dataset has been configured

     @Subscription
     Scenario Outline: Create a subscription

       Given  I create entity "<entity>"
       And    with field "<field1>" and value "<value1>"
       And    with field "<field2>" and value "<value2>"
       And    with field "<field3>" and value "<value3>"

       Examples:
         | entity       | field1 | value1  | field2      | value2      | field3        | value3   |
         | Subscription | code   | subCode | userAccount | OPENSOFT-01 | offerTemplate | OF_BASIC |
