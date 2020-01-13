@settings
Feature: Create/Update Provider by API

  Background: The system is configured.

  @admin @superadmin
  Scenario Outline: <action> Provider by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The provider is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                             | dto         | api        | action | statusCode | status  | errorCode              | message                                                          |
      | settings/00008-provider-api-create/SuccessTest.json                  | ProviderDto | /provider/ | Create |        500 | FAIL    | BUSINESS_API_EXCEPTION | There should already be a provider setup                         |
      | settings/00008-provider-api-create/SuccessTest.json                  | ProviderDto | /provider/ | Update |        200 | SUCCESS |                        |                                                                  |
      | settings/00008-provider-api-create/SuccessTest1.json                 | ProviderDto | /provider/ | Update |        200 | SUCCESS |                        |                                                                  |
      | settings/00008-provider-api-create/INVALID_PARAMETER_entreprise.json | ProviderDto | /provider/ | Update |        400 | FAIL    | INVALID_PARAMETER      | Cannot deserialize value of type `java.lang.Boolean` from String |
      | settings/00008-provider-api-create/INVALID_PARAMETER_rounding.json   | ProviderDto | /provider/ | Update |        400 | FAIL    | INVALID_PARAMETER      | Cannot deserialize value of type `java.lang.Integer` from String |
