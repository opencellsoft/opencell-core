@full
Feature: Invoicing - Draft Invoice

  @admin @superadmin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                     | title                  | dto    | api                           | action | statusCode | status  | errorCode | message |
      | scenarios/full/00001-invoicing-api/draft-invoice/charge_cdr_in_range.json    | Charge CDR - in range  | String | /billing/mediation/chargeCdr  | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/draft-invoice/rated_transaction_job.json  | Rated Transaction Job  |        | /job/execute                  | POST   |        200 | SUCCESS |           |         |
      | scenarios/full/00001-invoicing-api/draft-invoice/generate_draft_invoice.json | Generate draft invoice |        | /invoice/generateDraftInvoice | POST   |        200 | SUCCESS |           |         |
