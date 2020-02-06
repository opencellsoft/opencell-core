@ignore
Feature: Generate invoice on CC 

   
   Background:  System is configured.
   
Scenario: Create a customer

Given  Field "customer" is filled in with a customer
And    Field "customer ID" is filled in with an customer ID
And    Field "seller" is filled in with a seller 
And    Field "category" is filled in with a category
When   I Press  "Save" button 
Then   The customer is created 
And    I see success  message 
And    I click on "Add New customer account" button

Scenario: Create a customer account

Given  Field "Account " is filled in with a Account 
When   I Press  "Save" button 
Then   The customer account is created 
And    I see success  message 
And    I click on "Add New billing account" button

Scenario: Create a billing account

Given  Field "Account " is filled in with a Account 
When   I Press  "Save" button 
Then   The billing account is created 
And    I see success  message 
And    I click on "Add New user account" button

Scenario: Create a user account

Given  Field "Account " is filled in with a Account 
When   I Press  "Save" button 
Then   The user account is created 
And    I see success  message 
And    I click on "Add a new subscription" button


Scenario: Create a subscription

Given  Field "offer " is filled in with an offer 
And    Field "Subscription date " is filled in with a date
When   I Press  "subscribe" button 
Then   The Subscription is created 
And    I see success  message 
And    I click on "Add a new service" button

Scenario: Activate a service
Given  "Service" tab is opened
And    Field "Quantity  " is filled in with a Quantity  
And    Field "One-off price" is filled in with a price
And    Field "Trigger commissioning" is filled in with a value
When   I Press "activate" button 
Then   The service is activate 
And    I Press "update" button
And    I see success  message 
And    I click on breadcrumbs of Billing account

Scenario: Generate pro-forma 
Given  billing account view opened
When   I Press "Generate pro-forma" button 
Then   A draft invoice is generated

Scenario: Launch exceptional invoicing
Given  billing account view opened
When   I Press "Launch exceptional invoicing" button
And    I fill in "Invoice date" with a date 
And    I fill in "Last transaction date" with a date 
Then   An invoice is generated


Scenario Outline: Create a customer

Given  Field "customer" is filled in with a <customer>
And    Field "customer ID" is filled in with an <customer ID>
And    Field "seller" is filled in with a <seller> 
And    Field "category" is filled in with a <category>
When   I Press  "Save" button 
Then   The customer is created 
And    I see success  <message> 
And    I click on "Add New customer account" button
   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |


   Scenario Outline: Create a customer account

Given  Field "Account " is filled in with a <customer Account>
When   I Press  "Save" button 
Then   The customer account is created 
And    I see success  <message>
And    I click on "Add New billing account" button
   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |


   Scenario Outline: Create a billing account

Given  Field "Account " is filled in with a <billing account>
When   I Press  "Save" button 
Then   The billing account is created 
And    I see success  message 
And    I click on "Add New user account" button
   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |


   Scenario Outline: Create a user account

Given  Field "Account " is filled in with a <user account> 
When   I Press  "Save" button 
Then   The user account is created 
And    I see success  message 
And    I click on "Add a new subscription" button
   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |



   Scenario Outline: Create a subscription

Given  Field "offer " is filled in with an <offer>
And    Field "Subscription date " is filled in with a <date>
When   I Press  "subscribe" button 
Then   The Subscription is created 
And    I see success  <message> 
And    I click on "Add a new service" button
   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |


   Scenario Outline: Activate a service
Given  <service> tab is opened  
And    Field "Quantity" is filled in with a <Quantity> 
And    Field "One-off price" is filled in with a <price>
And    Field "Trigger commissioning" is filled in with a <value>
When   I Press "activate" button 
Then   The srvice is activate 
And    I Press "update" button
And    I see success  message

   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |

   Scenario Outline: Generate pro-forma
Given  billing account view opened
When   I Press "Generate pro-forma" button 
Then   A draft invoice is generated

   Examples:
      | customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |


   Scenario Outline: Launch exceptional invoicing
Given  billing account view opened
When   I Press "Launch exceptional invoicing" button
And    I fill in "Invoice date" with a <Invoiedate> 
And    I fill in "Last transaction date" with a <transactionDate>
Then   An invoice is generated

Examples:
| customer | customer ID | seller       |category|customer Account|billing account|user account|offer|date|service|Quantity|price|value  |Invoiedate|transactionDate|      | Message                          |

