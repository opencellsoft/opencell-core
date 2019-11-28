Feature: Create, modify and delete Invoice sequences 

   
   Background:  System is configured.
   
Scenario: Create a new Invoice sequence

Given  Field "code" is filled in with a code
And    Field "description" is filled in with a description
And    Field "Sequence size" is filled in with a sequence size
And    Field "Current invoice number" is filled in with a Current invoice number
When   I Press  "Save" button 
Then   The Invoice sequence is created 
And    I see success  message 

Scenario: Update an Invoice sequence

Given  Field "Sequence size" is updated with a new Sequence size
When   I Press  "Save" button 
Then   The Invoice sequence is updated
And    I see success  message 

Scenario: Delete a Invoice sequence 

Given  a Invoice sequence already created
When   I Press  "Delete" button 
And    I Press  "Confirm" button 
Then   The Invoice sequence is deleted
And    I see success  message 

Scenario Outline: Create a new Invoice sequence 

Given   Field "code" is filled in  with <Code>
And     Field "description"  is filled in with <description>
And     Field "Sequence size" is filled in with a <sequence size>
And     Field "Current invoice number" is filled in with a <Current invoice number>
When    I Press  "Save" button 
Then    The  Termination reason is created  
And     This "<Message>" is displaying  

Scenario Outline: Update an Invoice sequence

Given   Field "Sequence size" is updated with a new <Sequence size>
When    I Press  "Save" button 
Then    The Invoice sequence is updated  
And     This "<Message>" is displaying  

Scenario Outline: Delete an Invoice sequence

Given   an Invoice sequence already created
When    I Press  "Delete" button 
And     I Press  "Confirm" button 
Then    The  Invoice sequence is deleted   
And     This "<Message>" is displaying  

Examples:
| code              | description     | sequence size    |Current invoice number| Message        |
| InvSeq_Test       | InvSeq_Test     | 13               | invoiceNumber        |Element created |  

Examples:
| Sequence size       | Message          | 
| sequenceSize        | Element updated  | 

Examples:
| Message          | 
| Element deleted  |