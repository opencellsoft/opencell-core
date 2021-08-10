@TestUpdateSeller
   # The objective of this scenario is to verify whether an entity Seller
   # can be updated by API
Feature: Testing method Update on entity Seller

   Background:  System is configured.

   Scenario Outline: UpdateSeller

      Given  Update seller on "<env>"
      When   Field id filled by "<id>"
      And    Field code filled by "<code>"
      And    Field description filled by "<description>"
      Then   The status is "<status>"

      Examples:
         | env                   | id                | code               | description     | status |
         | http://localhost:8080 | ben.ohara.seller1 | Seller_ThangNguNgu | new description | 200    |
#         | https://tnn.d2.opencell.work | 4  | Seller_ThangHoolaa | new description | -1                | 200    |
