   # The objective of this scenario is to verify whether CRUD operations work well on an entity
@Seller
Feature: Testing method CRUD on entity Seller

   Background:  System is configured

   @CreateSeller
   Scenario Outline: CreateSeller

      Given  Actor wants to test create operation
      When   API version "<apiVer>"
      And    Business domain "<businessDomainPath>"
      And    Entity "<entity>"
      And    Body request given by "<jsonFile>"
      Then   The test is "<status>"

      Examples:
         | apiVer | businessDomainPath | entity | jsonFile                                          | status |
         | v1     | accountManagement  | seller | Data(Seller, UpdateSeller, CreateSellerTable)     | pass   |

   @Seller @UpdateSeller @CreateSellerTable

   @UpdateSeller
   Scenario Outline: UpdateSeller

      Given  Actor wants to test update operation
      When   API version "<apiVer>"
      And    Business domain "<businessDomainPath>"
      And    Entity "<entity>" with code or id "<codeOrId>"
      And    Body request given by "<jsonFile>"
      Then   The test is "<status>"

      Examples:
         | apiVer | businessDomainPath | entity | codeOrId          | jsonFile          | status |
         | v1     | accountManagement  | seller | ben.ohara.seller1 | UpdateSeller.json | pass   |


