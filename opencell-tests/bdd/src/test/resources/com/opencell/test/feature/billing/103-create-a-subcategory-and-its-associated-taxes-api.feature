@ignore
Feature: Create a new subcategory and its associated taxe  by API
Background:  System is configured.

Scenario: Create a new subcategory  with API

Given   "body" is filled in with a body 
And     "HTTP Request Method" is a Httpmethod
When    A user performs a "request" to requestUrl
Then    Verify the  Response "Code"  and "status"

Scenario: Add a tax to subcategory  with API

Given  "body" is filled in with a body 
And     "HTTP Request Method" is a method
When    A user performs a "request" to requestUrl
Then    Verify the  Response "Code"  and "status"

Scenario Outline: Create a new subcategory  with API
Given   "body" is filled in with a <body> 
And     "HTTP Request Method" is a <Httpmethod>
When     A user performs a "request" to "<requestUrl>"
Then     Verify the  Response "<Code>"  and "<status>"
	Examples:
		| body     | Httpmethod | requestUrl | Code | status |
		| {	"code": "TEST_FR",	"description": "Vos abonnements et options",	"invoiceCategory": "CONSUMPTION",	"customFields": null} | POST    | /invoiceSubCategory/createOrUpdate    |  200    |  SUCCESS      |


	Scenario Outline: Add a tax to subcategory  with API
Given   "body" is filled in with a <body> 
And     "HTTP Request Method" is a <Httpmethod>
When     A user performs a "request" to "<requestUrl>"
Then     Verify the  Response "<Code>"  and "<status>"

Examples:
| body     | Httpmethod | requestUrl | Code | status |
| {	"code": "TEST_FR",	"description": "Vos abonnements et options",	"invoiceCategory": "CONSUMPTION",	"customFields": null} | POST    | /invoiceSubCategory/createOrUpdate    |  200    |  SUCCESS      |

Examples:
| body     | Httpmethod | requestUrl | Code | status |
| {  "invoiceSubCategory": "TEST_FR",  "country": "FR",  "tax": "TAX_05"} | POST    | /invoiceSubCategoryCountry/createOrUpdate    |  200    |  SUCCESS      |