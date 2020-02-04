@ignore
Feature: Define Tax Rates so that they are used to calculate VAT for the respective customers 


   Background:  System is configured.
   
Scenario: Define Tax Rates

Given  Field "code" is filled in with a code
And    Field "percentage" is filled in with a percentage
And    Field "Accounting code" is filled in with a Accounting code
When   I Press  "Save" button 
Then   Tax Rates is created
And    I see success  message 

Scenario Outline: Update agreements

Given  Field "code" is filled in with a  <code> 
And    Field "Tax category" is filled in with an <Tax category>
And    Field "Accounting code" is filled in with a <Accounting code>
When   I Press  "Save" button 
Then   The provider is updated 
And    This "<Message>" is displaying 
 
Examples:
| code      | Tax category | Accounting code|
| Tax_rate  | TaxCat_Test  | 411000000      |
