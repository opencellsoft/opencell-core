
Feature: API feature
Scenario: Create a new customer with API
Given  The API are up and running for  "https://api.opencellsoft.com/integration/"
When A user performs a post request to "/account/customer/createOrUpdate"
And perform the request 
Then Verify the  Response "<Code>"  and "<status>" and "<entityCode>"


Scenario Outline: Create a new customer with API
Given  The API are up and running for  "https://api.opencellsoft.com/integration/"
When A user performs a post request to "/account/customer/createOrUpdate"
And perform the request 
Then Verify the  Response Code status entityCode

Examples: 
    | Code |status  | entityCode |
    
    | 200 | SUCCESS  |CUST_CLIENT_00001|


