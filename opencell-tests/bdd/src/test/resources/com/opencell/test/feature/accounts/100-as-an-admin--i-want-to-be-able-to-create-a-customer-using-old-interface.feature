@ignore
Feature: Creation of a new customer on Admin

Background:  System is configured.

Scenario: Create a new customer 

Given  Field "code" is filled in with a code
And    Field "Description" is filled in with a Description
And    Field "Customercategory" is filled in with a Category
When   I Press  "Save" button 
Then   The customer is created 
And    I see success  message 




Scenario Outline: Create a new customer 



Given  Field "code" is filled in with  <code>
And    Field "Description" is filled in with <Description>
And    Field "Customercategory" is filled in with  <Customercategory>
When    I Press "Save" button 
Then    The Customer is created 
And     This "<Message>" is displaying  
Examples:
  | Code        | Description        |Customercategory | message                         |
  | NewCustomer | DesciptionCustomer | Client          | Entity was created successfully |


