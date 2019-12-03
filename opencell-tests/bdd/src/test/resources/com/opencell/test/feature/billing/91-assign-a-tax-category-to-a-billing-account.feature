Feature: Assign a Tax Category to a Billing Account

   Background:  System is configured.
   
Scenario: Assign a Tax Category to a Billing Account

Given  A "Billing account" is configured
And    Field "Tax category" is filled in with a Tax category
When   I Press  "Save" button 
Then   The Billing account is updated with tax category assigned
And    I see success  message 

Scenario Outline: Update agreements

Given  a <Billing account> is configured
And    Field "Tax category" is filled in with an <Tax category>
When   I Press  "Save" button 
Then   The provider is updated 
And    This "<Message>" is displaying 
 
Examples:
| Billing account | Tax category           | 
| FTT_13902       | TaxCat_Test            | 
