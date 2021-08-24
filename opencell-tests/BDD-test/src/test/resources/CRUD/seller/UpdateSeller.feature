   # The objective of this scenario is to verify whether an entity Seller
   # can be updated by API
Feature: Testing method Update on entity Seller

   Background:  System is configured

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














#   @UpdateSeller
#   Scenario Outline: UpdateSeller
#
#      Given  Update seller on "<env>"
#      When   Field id filled by "<id>"
#      And    Field code filled by "<code>"
#      And    Field description filled by "<description>"
#      Then   The status is "<status>"
#
#      Examples:
#         | env                   | id                | code               | description     | status |
#         | http://localhost:8080 | ben.ohara.seller1 | Seller_ThangNguNgu | new description test | 200    |
#         | https://tnn.d2.opencell.work | 4  | Seller_ThangHoolaa | new description | -1                | 200    |
