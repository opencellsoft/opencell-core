@ignore
Feature: Payment + Refund process
   
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
And    Field "Mandate identification" is filled in with a Mandate identification
And    Field "Bank name" is filled in with a Bank name
And    Field "Account owner" is filled in with an Account owner
And    Field "Iban" is filled in with an Iban
And    Field "Bic" is filled in with a Bic
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

Scenario: Generate the invoice

Given  The customer is subscribed to an offer and some Services are activated
When   I Press "Generate invoice" button on billing account view
Then   The invoice is generated 
And    I see success  message 

Scenario: Run AO_Job
Given  invoice already generated
When   I Press "Run" button
Then   The Account Operations are generated

Scenario: Create a SepaJobInstance 
Given  Field "code" is filled in with a jobCode 
And    Field "jobType" is filled in with a jobType
And    Field "jobCategory" is filled in with a jobCategory
And    Field "DDRequest builder" is filled in with a ddRequestBuilderCode
When   I Press "Save" button
Then   The job is created

Scenario: Create DDRequestState 
Given  Field "fromDueDate" is filled in with a fromDueDate 
And    Field "toDueDate" is filled in with a toDueDate
And    Field "ddrequestOp" is filled in with a ddrequestOp
And    Field "ddRequestBuilderCode" is filled in with a ddRequestBuilderCode
When   I Press "Save" button
Then   The DDRequestState is created with a "status"

Scenario: Run SepaJob
Given  SepaJob is configured
When   I Press "Run" button
Then   the job is executed

Scenario: Create aggregated negative invoice 
Given  Field "Type" is filled in with a type 
And    Field "seller" is filled in with a seller
And    Field "Billing Account" is filled in with a billingAccount
And    Field "Invoice Category / Sub Category" is filled in with a InvCat
And    Field "Amount without taxes" is filled in with a AmountWithoutTax
When   I Press "Add line" button
And    I Press "Save" button
Then   The negative invoice is generated

Scenario: Run AO_Job
Given  invoice already generated
When   I Press "Run" button
Then   The Account Operations are generated

Scenario: Create DDRequestState for refund
Given  Field "fromDueDate" is filled in with a fromDueDate 
And    Field "toDueDate" is filled in with a toDueDate
And    Field "ddrequestOp" is filled in with a ddrequestOp
And    Field "paymentOrRefundEnum" is filled in with a ElementofList
And    Field "ddRequestBuilderCode" is filled in with a ddRequestBuilderCode
When   I Press "Save" button
Then   The DDRequestState is created with a "status"

Scenario: Create a RefundSepaJobInstance 
Given  Field "code" is filled in with a RefundjobCode 
And    Field "jobType" is filled in with a jobType
And    Field "jobCategory" is filled in with a jobCategory
And    Field "DDRequest builder" is filled in with a ddRequestBuilderCode
And    Field "Payment or Refund" is filled in with a value
When   I Press "Save" button
Then   The job is created

Scenario: Run RefundSepaJobInstance
Given  RefundSepaJobInstance is configured
When   I Press "Run" button
Then   the job is executed

Scenario Outline: Create a customer

Given  Field "customer" is filled in with a <customer>
And    Field "Customer category" is filled in with an <customer category>
When   I Press  "Save" button 
Then   The customer is created 
And    I see success  <message1> 
And    I click on "Add customer account" button
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create a customer account

Given  Field "Code " is filled in with a <CACode>
And    Field "Currency" is filled in with a <Currency>
And    Field "Language" is filled in with a <Language>
And    Field "Payment method" is filled in with a <Payment method>
And    Field "Alias" is filled in with an <Alias>
And    Field "Mandate identification" is filled in with a <Mandate identification>
And    Field "Bank name" is filled in with a <Bank name>
And    Field "Account owner" is filled in with an <Account owner>
And    Field "Iban" is filled in with an <Iban>
And    Field "Bic" is filled in with a <Bic>
When   I Press "Add/update payment information" button
And    I Press "save" button
Then   The customer account is created 
And    I see success  <message2>
And    I click on "Add billing account" button
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create a billing account

Given  Field "Code" is filled in with a <BACode>
And    Field "Country" is filled in with a <Country> 
And    Field "Language" is filled in with a <Language>
And    Field "Billing cycle" is filled in with a <Billing cycle>
When   I Press  "Save" button 
Then   The billing account is created 
And    I see success <message3> 
And    I click on "Add user account" button
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create a user account

Given  Field "Code" is filled in with a <UACode>
When   I Press  "Save" button 
Then   The user account is created 
And    I see success  <message4> 
And    I click on "Add a new subscription" button
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|


   Scenario Outline: Create a subscription

Given  Field "offer " is filled in with an <offer>
And    Field "code" is filled in with a <SubscriptionCode>
And    Field "Seller" is filled in with a <seller>
And    Field "Initial agreement date" is filled in with a <date>
When   I Press "save" button 
Then   The Subscription is created 
And    I see success  <message5>
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Generate the invoice

Given  The customer is subscribed to an offer and some Services are activated
When   I Press "Generate invoice" button on billing account view
Then   The invoice is generated 
And    I see success  <message6>
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Run AO_Job
Given  invoice already generated
When   I Press "Run" button
Then   The Account Operations are generated
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create a SepaJobInstance
Given  Field "code" is filled in with a <jobCode>
And    Field "jobType" is filled in with a <jobType>
And    Field "jobCategory" is filled in with a <jobCategory>
And    Field "DDRequest builder" is filled in with a <ddRequestBuilderCode>
When   I Press "Save" button
Then   The job is created
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create DDRequestState
Given  Field "fromDueDate" is filled in with a <fromDueDate> 
And    Field "toDueDate" is filled in with a <toDueDate>
And    Field "ddRequestBuilderCode" is filled in with a <ddRequestBuilderCode>
When   I Press "Save" button
Then   The DDRequestState is created with a <status>
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Run SepaJob
Given  SepaJob is configured
When   I Press "Run" button
Then   the job is executed
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create aggregated negative invoice
Given  Field "Type" is filled in with a <type> 
And    Field "seller" is filled in with a <seller>
And    Field "Billing Account" is filled in with a <billingAccount>
And    Field "Invoice Category / Sub Category" is filled in with a <InvCat>
And    Field "Amount without taxes" is filled in with a <AmountWithoutTax>
When   I Press "Add line" button
And    I Press "Save" button
Then   The negative invoice is generated
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Run AO_Job
Given  invoice already generated
When   I Press "Run" button
Then   The Account Operations are generated
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create DDRequestState for refund
Given  Field "fromDueDate" is filled in with a <fromDueDate>
And    Field "toDueDate" is filled in with a <toDueDate>
And    Field "ddrequestOp" is filled in with a <ddrequestOp>
And    Field "paymentOrRefundEnum" is filled in with a <ElementofList>
And    Field "ddRequestBuilderCode" is filled in with a <ddRequestBuilderCode>
When   I Press "Save" button
Then   The DDRequestState is created with a <status>
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Create a RefundSepaJobInstance
Given  Field "code" is filled in with a <RefundjobCode>
And    Field "jobType" is filled in with a <jobType>
And    Field "jobCategory" is filled in with a <jobCategory>
And    Field "DDRequest builder" is filled in with a <ddRequestBuilderCode>
And    Field "Payment or Refund" is filled in with a <value>
When   I Press "Save" button
Then   The job is created
   Examples:
      |customer|customer category| message1 |
      |C_TEST  | CLIENT          |Customer created successfully|

   Scenario Outline: Run RefundSepaJobInstance
Given  RefundSepaJobInstance is configured
When   I Press "Run" button
Then   the job is executed

Examples:
|customer|customer category| message1 |
|C_TEST  | CLIENT          |Customer created successfully|
