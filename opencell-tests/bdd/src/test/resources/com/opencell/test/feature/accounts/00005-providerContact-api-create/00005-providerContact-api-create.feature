@accounts
Feature: Create/Update Provider Contact by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: <action> Provider Contact by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The provider contact is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto                | api                                     | action         | statusCode | status  | errorCode                        | message                                                                                              |
      | accounts/00005-providerContact-api-create/SuccessTest.json           | ProviderContactDto | /account/providerContact/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                      |
      | accounts/00005-providerContact-api-create/SuccessTest.json           | ProviderContactDto | /account/providerContact/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | ProviderContact with code=TEST already exists.                                                       |
      | accounts/00005-providerContact-api-create/DO_NOT_EXIST.json          | ProviderContactDto | /account/providerContact/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | ProviderContact with code=NOT_EXIST does not exists.                                                 |
      | accounts/00005-providerContact-api-create/SuccessTest1.json          | ProviderContactDto | /account/providerContact/               | Update         |        200 | SUCCESS |                                  |                                                                                                      |
      | accounts/00005-providerContact-api-create/SuccessTest1.json          | ProviderContactDto | /account/providerContact/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                      |
      | accounts/00005-providerContact-api-create/MISSING_PARAMETER.json     | ProviderContactDto | /account/providerContact/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: description.                        |
      | accounts/00005-providerContact-api-create/INTERNAL_SERVER_ERROR.json | ProviderContactDto | /account/providerContact/createOrUpdate | CreateOrUpdate |        500 | FAIL    |                                  | At least 1 of the field in Contact Information tab is required [email, genericEmail, phone, mobile]. |
      | accounts/00005-providerContact-api-create/INVALID_PARAMETER.json     | ProviderContactDto | /account/providerContact/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot construct instance of `org.meveo.api.dto.account.AddressDto`                                  |
