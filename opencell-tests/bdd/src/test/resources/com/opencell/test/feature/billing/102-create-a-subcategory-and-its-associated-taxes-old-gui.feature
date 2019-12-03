Feature: Creation of a new subcategory with asociated taxes 

   Background:  System is configured.

Scenario: Create a new subcategory 

Given  Field "code" is filled in with a code
And    Field "Invoice Category" is filled in with a Name
When   I Press  "Save" button 
Then   The invoice sub category is created 
And    I see success  message 

Scenario: Add tax to subcategory 

Given  Field "Tax" is filled in with a tax
When   I Press  "Add this Tax" button  
Then   A tax line is added 
And    I see success  message 

Scenario Outline: Create a new subcategory 

Given  Field "code"  is filled in  with <code>
And    Field "Invoice Category" is filled in  with "< Invoice Category>"
When   I Press  "Save" button 
And    The invoice sub category is created 
And    I see success  message  

Scenario Outline: Add tax to subcategory 

Given  Field "Tax" is filled in  with <TAX>
When   I Press  "Add this Tax" button 
Then   A tax line is added 
And    I see success  message 
And    This message is displaying "Entity was created successfully!"

Examples:
| code     | Invoice Category | Message                                                                    |
| Value 1  | Consumption      | Entity was created successfully! Please add a tax under this sub category  |

Examples:
| TAX     | Message                           |
| TAX_00  | Entity was created successfully!  |