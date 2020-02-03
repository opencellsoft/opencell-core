@ignore
Feature: Creation of a new customer on Admin


   

Background:  System is configured.
Scenario: Create a new customer


 Given  Field "code" is filled in with a code
 And    Field "Category" is filled in with a Category
 And    Field "Title" is filled in with a Title
 And    Field "First Name" is filled in with a First Name
 And    Field "Last Name" is filled in with a Last Name
 And    Field "Seller" is filled in with a Seller
 And    Field  "Email" is filled in with a Email  in tab Contact
 And    Field "Adress 1" is filled in with a Adress  in Tab Adress
 And    Field "Postal code" is filled in with a Postal code  in  Tab Adress
 And    Field "City " is filled in with a City  in  Tab Adress
 And    Field "Country" is filled in with a Country  in Tab Adress
 When   I Press  "Save" button 
 Then   The customer is created 
 And    I see success  message 





Scenario Outline: Create a new customer 

Given  Field "code" is filled in with  <code>
And    Field "Category" is filled in with  <Category>
And    Field "Title" is filled in with  <Title>
And    Field "First Name" is filled in with  <First Name>
And    Field "Last Name" is filled in with <Last Name>
And    Field "Seller" is filled in with  <Seller>
And    Field "Email" is filled in  with <Email>  in tab Contact  
And    Field "Adress 1" is filled in with <Adress 1>  in Tab Adress
And    Field "Postal code" is filled in with <Postal code>  in Tab Adress
And    Field "City" is filled in with  <City>  in Tab Adress
And    Field "Country" is filled in with  <Country>  in Tab Adress
When   I Press "Save" button 
Then   The Customer is created 
And    This "<Message>" is displaying  
Examples:
 | Code                      | Category   | Title    |First Name | Last Name  | Seller    |Email                               |Adress 1               | Postal code|City | Country| message | 



