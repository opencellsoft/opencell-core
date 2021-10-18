   # The objective of this scenario is to verify whether an entity Seller
   # can be updated by API
   @Seller
   Feature: Testing method Update on entity Seller

      @UpdateSeller
      Scenario Outline: UpdateSeller

         Given  url "<url>"
         When   method "<method>"
         And    body request "<jsonObject>"
         Then   status is "<status>"
         And    assertions "<assertions>"

         Examples:
            | url                                                                            | method | jsonObject                                   | status | assertions                                                                   |
            | http://localhost:8080/opencell/api/rest/v1/accountManagement/sellers/ben.ohara | put    | data(Seller, UpdateSeller, JsonUpdateSeller) | pass   | data(Seller, UpdateSeller, UpdateSeller)/(assert (address[0].city, "Paris")) |














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
