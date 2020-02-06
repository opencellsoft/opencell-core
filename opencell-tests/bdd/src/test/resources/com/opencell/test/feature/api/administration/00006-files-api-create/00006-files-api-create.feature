@administration @ignore
Feature: Upload file by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: Upload file by API
    Given The entity has the following information "<bodyType>" 
    And   Content-Type as "<Content-Type>" 
    And   Uploaded file as "<uploadedFile>"
    And   filename as "<filename>"
    When I call the "<api>"
    Then The file is uploaded
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | bodyType     |  Content-Type                      |uploadedFile|filename             | api                | statusCode | status | errorCode                         | message                                                                            |
      | form-data    |  application/x-www-form-urlencoded | test test  |imports/test.csv     |/admin/files/upload |        200 | SUCCESS |                                  |                                                                                    |
      | form-data    |  application/x-www-form-urlencoded | test test  |                     |/admin/files/upload |        500 | FAIL    | BUSINESS_API_EXCEPTION           | Error uploading file: . ./opencelldata/DEMO (Is a directory)                       |
      | form-data    |  application/x-www-form-urlencoded | test test  |imports/test/test.csv|/admin/files/upload |        404 | FAIL    | BUSINESS_API_EXCEPTION           | Error uploading file: imports/test/test.csv. No such file or directory             |
