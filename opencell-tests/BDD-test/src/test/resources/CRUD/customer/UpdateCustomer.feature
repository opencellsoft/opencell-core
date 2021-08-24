   # The objective of this scenario is to verify whether an entity Customer
   # can be updated by API
   Feature: Testing method Update on entity Customer

     Background:  System is configured

     @UpdateCustomer
     Scenario Outline: UpdateCustomer

       Given  Actor wants to test update operation
       When   API version "<apiVer>"
       And    Business domain "<businessDomainPath>"
       And   Entity "<entity>" with code or id "<codeOrId>"
       And    Body request given by "<jsonFile>"
       Then   The test is "<status>"

       Examples:
#         | apiVer | businessDomainPath | entity   | codeOrId            | jsonFile            | status |
#         | v1     | accountManagement  | customer | customer.ben.ohara1 | UpdateCustomer.json | pass   |
         | setting        | value |
         | name/title     | Hello  |
