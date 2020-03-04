@full
Feature: Setup base data - Misc

  @admin @superadmin
  Scenario Outline: <entity>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                       | entity                          | dto            | api                         | action         | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/misc/update_invoiceType_com_sequence.json | Update InvoiceType COM Sequence | InvoiceTypeDto | /invoiceType/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |
