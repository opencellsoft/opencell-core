@ignore
Feature: Create, modify and delete Tax categories

   
   Background:  System is configured.
   
Scenario: Create a new tax category 

Given  Field "code" is filled in with a code
And    Field "description" is filled in with a description
When   I Press  "Save" button 
Then   The tax category  is created 
And    I see success  message 

Scenario: Update a tax category 

Given  Field "description" is updated with a new description
When   I Press  "Save" button 
Then   The tax category  is updated
And    I see success  message 

Scenario: Delete a tax category 

Given  a tax category already created
When   I Press  "Delete" button 
And    I Press  "Confirm" button 
Then   The tax category  is deleted
And    I see success  message 

Scenario Outline: Create a new tax category 

Given   Field "code" is filled in  with <Code>
And     Field "description"  is filled in with <description>
When    I Press  "Save" button 
Then    The tax category is created  
And     This "<Message>" is displaying  

Scenario Outline: Update a tax category 

Given   Field "description" is updated with a new <description>
When    I Press  "Save" button 
Then    The tax category is updated  
And     This "<Message>" is displaying  

Scenario Outline: Delete a tax category 

Given   a tax category already created
When    I Press  "Delete" button 
And     I Press  "Confirm" button 
Then    The tax category is deleted   
And     This "<Message>" is displaying  

Examples:
| code         | description      | Message          |
| TaxCat_Test  | TaxCat_Test      | Element created  |

Examples:
| description | Message          | 
| TaxCat_Test | Element updated  | 

Examples:
| Message          | 
| Element deleted  |