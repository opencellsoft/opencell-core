@accounts
Feature: Delete Provider Contact by API

  Background: The system is configured
              Create Provider Contact by API is already executed


  @admin @superadmin
  Scenario Outline: DeleteProvider Contact by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto                | api                       | statusCode | status  | errorCode                        | message                                       |
      | accounts/00004-providerContact-api-create/SuccessTest.json           | ProviderContactDto | /account/providerContact/ |        200 | SUCCESS |                                  |                                               |
      | accounts/10004-providerContact-api-delete/ENTITY_DOES_NOT_EXIST.json | ProviderContactDto | /account/providerContact/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ProviderContact with code=NOT_EXIST does not exists. |
