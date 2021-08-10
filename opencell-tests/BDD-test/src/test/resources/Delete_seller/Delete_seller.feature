@TestDeleteSeller
   # The objective of this scenario is to verify whether an entity Seller
   # can be updated by API
Feature: Testing method Delete on entity Seller

   Background:  System is configured.

   Scenario Outline: DeleteSeller

      Given  A seller on "<env>"
      When   Field id filled by following id "<id>"
      Then   The status is now "<status>"

      Examples:
         | env                   | id                | status |
         | http://localhost:8080 | ben.ohara.seller1 | 200    |
