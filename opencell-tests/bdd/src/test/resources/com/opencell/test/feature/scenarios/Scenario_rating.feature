Feature: Rating process
   
   Background:  System is configured.
   
Scenario: Create a customer

Given  Field "customer" is filled in with a customer
And    Field "Customer category" is filled in with an customer category
When   I Press  "Save" button 
Then   The customer is created 
And    I see success  message 
And    I click on "Add customer account" button

Scenario: Create a customer account

Given  Field "Code " is filled in with a CACode
And    Field "Currency" is filled in with a Currency 
And    Field "Language" is filled in with a Language
And    Field "Payment method" is filled in with a Payment method
And    Field "Alias" is filled in with an Alias
When   I Press "Add/update payment information" button
And    I Press "save" button
Then   The customer account is created 
And    I see success  message 
And    I click on "Add billing account" button

Scenario: Create a billing account

Given  Field "Code" is filled in with a BACode 
And    Field "Country" is filled in with a Country 
And    Field "Language" is filled in with a Language
And    Field "Billing cycle" is filled in with a Billing cycle
When   I Press  "Save" button 
Then   The billing account is created 
And    I see success  message 
And    I click on "Add user account" button

Scenario: Create a user account

Given  Field "Code" is filled in with a UACode 
When   I Press  "Save" button 
Then   The user account is created 
And    I see success  message 
And    I click on "Add a new subscription" button


Scenario: Create a subscription

Given  Field "offer " is filled in with an offer 
And    Field "code" is filled in with a SubscriptionCode
And    Field "Seller" is filled in with a seller
And    Field "Initial agreement date" is filled in with a date
When   I Press "save" button 
Then   The Subscription is created 
And    I see success  message 
And    I click on "Services" tab

Scenario: Instantiate a service
Given  "Services" tab is opened
When   I Press "instantiate" button 
Then   The service is instantiated 

Scenario: Activate a service
Given  The service is instantiated 
When   I Press "activate" button
Then   The service is activated
And    I see success  message 

Scenario: Run RT_Job
Given  Wallet Operations are already created
When   I Press "Run" button
Then   The job is executed 

Scenario: Create a global Run
Given  Rated transactions are already created
And    Field "Billing cycle" is filled in with a Billing cycle
And    Field "Type" is filled in with a type
When   I Press "Create Billing Run" button
Then   The Billing run  is executed

Scenario: Run Inv_Job
Given  Inv_Job is created
When   I Press "Run" button
Then   The job is executed 

Scenario: Validate BR
Given  BR  is on PostInvoicing report status
When   I Press "validate" button
Then   The status papsses to Post report validated

Scenario: Re run Inv_Job
Given  Inv_Job is created
When   I Press "Run" button
Then   The job is executed 
And    the invoice is created

Scenario: Create a customer

Given  Field "customer" is filled in with a <customer>
And    Field "Customer category" is filled in with an <customer category>
When   I Press  "Save" button 
Then   The customer is created 
And    I see success  <message>
And    I click on "Add customer account" button

Scenario: Create a customer account

Given  Field "Code " is filled in with a <CACode>
And    Field "Currency" is filled in with a <Currency>> 
And    Field "Language" is filled in with a <Language>
And    Field "Payment method" is filled in with a <Payment method>
And    Field "Alias" is filled in with an <Alias>
When   I Press "Add/update payment information" button
And    I Press "save" button
Then   The customer account is created 
And    I see success  <message> 
And    I click on "Add billing account" button

Scenario: Create a billing account

Given  Field "Code" is filled in with a <BACode> 
And    Field "Country" is filled in with a <Country>
And    Field "Language" is filled in with a <Language>
And    Field "Billing cycle" is filled in with a <Billing cycle>
When   I Press  "Save" button 
Then   The billing account is created 
And    I see success  <message> 
And    I click on "Add user account" button

Scenario: Create a user account

Given  Field "Code" is filled in with a <UACode>
When   I Press  "Save" button 
Then   The user account is created 
And    I see success  <message> 
And    I click on "Add a new subscription" button


Scenario: Create a subscription

Given  Field "offer " is filled in with an <offer> 
And    Field "code" is filled in with a <SubscriptionCode>
And    Field "Seller" is filled in with a <seller>
And    Field "Initial agreement date" is filled in with a <date>
When   I Press "save" button 
Then   The Subscription is created 
And    I see success  message 
And    I click on "Services" tab

Scenario: Instantiate a service
Given  "Services" tab is opened
When   I Press "instantiate" button 
Then   The service is instantiated 

Scenario: Activate a service
Given  The service is instantiated 
When   I Press "activate" button
Then   The service is activated
And    I see success  <message> 

Scenario: Run RT_Job
Given  Wallet Operations are already created
When   I Press "Run" button
Then   The job is executed 

Scenario: Create a global Run
Given  Rated transactions are already created
And    Field "Billing cycle" is filled in with a <Billing cycle>
And    Field "Type" is filled in with a <type>
When   I Press "Create Billing Run" button
Then   The Billing run  is executed

Scenario: Run Inv_Job
Given  Inv_Job is created
When   I Press "Run" button
Then   The job is executed 

Scenario: Validate BR
Given  BR  is on PostInvoicing report status
When   I Press "validate" button
Then   The status papsses to Post report validated

Scenario Outline: Re run Inv_Job
Given  Inv_Job is created
When   I Press "Run" button
Then   The job is executed 
And    the invoice is created

Examples:
| customer  | customer category | message 1                     | CACode  | Currency |Language  | Payment method  | Alias  | message2              |BACode   |Country  |Language    |Billing cycle                       |message3 |UACode                           | message  |offer     |SubscriptionCode                 |seller      |date     |message  |Billing cycle  |type |
| C_TEST    | Client            | customer created successfully | CA_TEST | EUR      |FRA       |Check            | 123    | payment method added  |BA_TEST  |FRA      |CYC_INV_MT_1|Billing account created successfully|UA_TEST  |User account created successfully|CLASSIC   |01/04/2019|Subscription created successfully|CYC_INV_MT_1|Automatic|         |               |     |