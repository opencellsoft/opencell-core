@ignore
Feature: Creation of a new seller
   
   Background:  System is configured.
   
Scenario: Create a new seller    

Given  Field "code" is filled in with a code
And    Field "country code" is filled in with a country code
When   I Press  "Save" button 
Then   The seller is created 
And    I see success  message 
  
Scenario Outline: Create a new seller

Given   Field "code" is filled in  with <Code>
And     Field "country code"  is filled in with <country code">
When   I Press  "Save" button 
Then   The seller is created  
And    This "<Message>" is displaying  


Examples:
| code         | country code     | Message          |
| Seller_Test  | FR               | Element created  |