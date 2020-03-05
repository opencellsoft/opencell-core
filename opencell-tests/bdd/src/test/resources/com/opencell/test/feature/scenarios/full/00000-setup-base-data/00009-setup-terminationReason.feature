@full
Feature: Setup base data - Setup TerminationReason

  @admin @superadmin
  Scenario Outline: Update <entity>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                               | entity        | dto                  | api                | action | statusCode | status  | errorCode | message |
      | scenarios/full/00000-setup-base-data/setup-terminationReason/update_term_reason_1.json | TERM REASON 1 | TerminationReasonDto | /terminationReason | Update |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-terminationReason/update_term_reason_2.json | TERM REASON 2 | TerminationReasonDto | /terminationReason | Update |        200 | SUCCESS |           |         |
      | scenarios/full/00000-setup-base-data/setup-terminationReason/update_term_reason_3.json | TERM REASON 3 | TerminationReasonDto | /terminationReason | Update |        200 | SUCCESS |           |         |
