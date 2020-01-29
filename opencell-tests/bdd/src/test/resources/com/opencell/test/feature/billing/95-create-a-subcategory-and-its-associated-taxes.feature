@ignore
Feature: Creation of a new subcategory

 
   Background:  System is configured.
   
Scenario: Create a new subcategory    

Given  Field "code" is filled in with a code
And    Field "Invoice Category" is filled in with a Name
When   I Press  "Save" button 
Then   The invoice sub category is created 
And    I see success  message 


Scenario Outline: Create a new subcategory

Given   Field "code" is filled in  with <Code>
And     Field "Invoice Category"  is filled in with <Invoice Category>
When   I Press  "Save" button 
Then   The invoice sub category is created  
And    This "<Message>" is displaying  


Examples:
| code         | Invoice Category | Message          |
| SubCat_Test  | Consumption      | Element created  |