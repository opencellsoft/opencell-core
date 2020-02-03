@accounts
Feature: Delete Provider Contact by API

  Background: The system is configured
              Create Provider Contact by API is already executed


  @admin @superadmin
  Scenario Outline: <status> <action> Provider Contact by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                        | dto                | api                       | action | statusCode | status  | errorCode                        | message                                              |
      | api/accounts/00005-providerContact-api-create/SuccessTest.json  | ProviderContactDto | /account/providerContact/ | Delete |        200 | SUCCESS |                                  |                                                      |
      | api/accounts/00005-providerContact-api-create/DO_NOT_EXIST.json | ProviderContactDto | /account/providerContact/ | Delete |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ProviderContact with code=NOT_EXIST does not exists. |
