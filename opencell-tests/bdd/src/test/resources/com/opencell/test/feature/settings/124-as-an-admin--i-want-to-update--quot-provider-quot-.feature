Feature: Modify provider 

   
   Background:  System is configured.
   
Scenario: Update agreements

Given  Field "customer" is filled in with a customer
And    Field "offer" is filled in with an offer
And    Field "min term" is filled in with a min term
When   I Press  "Save" button 
Then   The provider is updated 
And    I see success  message 

Scenario: Update tax management

Given  Field "from validity date" is filled in with a validity date
And    Field "tax category" is filled in with a tax category
And    Field "tax classs" is filled in with a tax classs
And    Field "tax code" is filled in with a tax code
When   I Press  "Save" button 
Then   The provider is updated  
And    I see success  message 

Scenario: Update service migration

Given  Field "from validity date" is filled in with a validity date
And    Field "to validity date" is filled in with a validity date
And    Field "Ranking difference" is filled in with a ranking difference
And    Field "Event" is filled in with an event
When   I Press  "Save" button 
Then   The provider is updated  
And    I see success  message 

Scenario Outline: Update agreements

Given  Field "customer" is filled in with a <customer>
And    Field "offer" is filled in with an <offer>
And    Field "min term" is filled in with a <min term>
When   I Press  "Save" button 
Then   The provider is updated 
And    This "<Message>" is displaying 

Scenario Outline: Update tax management

Given  Field "from validity date" is filled in with a <validity date>
And    Field "tax category" is filled in with a <tax category>
And    Field "tax classs" is filled in with a <tax classs>
And    Field "tax code" is filled in with a <tax code>
When   I Press  "Save" button 
Then   The provider is updated  
And    This "<Message>" is displaying  

Scenario Outline: Update service migration

Given  Field "from validity date" is filled in with a <from validity date>
And    Field "to validity date" is filled in with a <to validity date>
And    Field "Ranking difference" is filled in with a <ranking difference>
And    Field "Event" is filled in with an <event>
When   I Press  "Save" button 
Then   The provider is updated  
And     This "<Message>" is displaying  

Examples:
| customer        | offer             | min term  | Message         |
| cust_test       | NGA fiver to home | 12        | Element updated |  

Examples:
| validity date| tax category| tax classs| tax code | Message        | 
| 01/04/2019   | taxCat3     |A1         | TAX_00   | Element updated| 

Examples:
|from validity date|to validity date| ranking difference| event | Message        | 
| 01/04/2019       | 05/05/2019     | =0                | change| Element updated| 