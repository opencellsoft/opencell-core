@ignore
Feature: Generate invoice on SC

Background:  System is configured.
   
Scenario: Self-register on SC module
Given  Field "First name" is filled in with a First name
And    Field "Last name" is filled in with a Last name
And    Field "Email" is filled in with an Email 
And    Field "Username" is filled in with a Username
And    Field "Password" is filled in with a password
And    Field "Confirm password" is filled in with a password
When   I Press  "Register" button 
Then   The user is created 
And    I see home page 
And    I click on "shop" menu


#Scenario: Subscribe to an offer on SC module 1
#Given  An  "offer" is present on the shop
#When   I Press  "Get started" button
#And    I Press  "confirm" button
#And    I Press  "OK" button
#Then   The contract is created

#Scenario: Resiliate a subscription on SC module
#Given  A  contract is present on "my contracts" menu
#When   I Press  "Cancellation" menu
#And    I Press  "confirm" button
#Then   The contract is resiliated

#Scenario: Subscribe to an offer on SC module 2
#Given  An  "offer" is present on the shop
#When   I Press  "Get started" button
#And    I Press  "confirm" button
#And    I Press  "OK" button
#Then   The contract is created

#Scenario: Launch invoicing process from CC module
#Given  The user created from sc module is present on customer's list
#When   I Press  "billing account" created from SC module
#And    I Press  "Launch an exceptional invoicing " button
#And    I fill in  "Invoice date"  with a InvoiceDate
#And    I fill in  "Last transaction date"  with a LastTransactionDate
#And    I Press  "Launch" button
#Then   The Invoice is generated
#And    I see success  message


#Scenario : Self-register on SC module 2
#Given  Field "First name" is filled in with a <First name>
#And    Field "Last name" is filled in with a <Last name>
#And    Field "Email" is filled in with an <Email>
#And    Field "Username" is filled in with a <Username>
#And    Field "Password" is filled in with a <password>
#And    Field "Confirm password" is filled in with a <password>
#When   I Press  "Register" button
#Then   The user is created
#And    I see home page
#And    I click on "shop" menu


#Scenario : Subscribe to an offer on SC module 3
#Given  An  "offer" is present on the shop
#When   I Press  "Get started" button
#And    I Press  "confirm" button
#And    I Press  "OK" button
#Then   The contract is created

#Scenario : Resiliate a subscription on SC module 2
#Given  A  contract is present on "my contracts" menu
#When   I Press  "Cancellation" menu
#And    I Press  "confirm" button
#Then   The contract is resiliated

#Scenario : Subscribe to an offer on SC module 4
#Given  An  "offer" is present on the shop
#When   I Press  "Get started" button
#And    I Press  "confirm" button
#And    I Press  "OK" button
#Then   The contract is created

#Scenario : Launch invoicing process from CC module 2
#Given  The user created from sc module is present on customer's list
#When   I Press  "billing account" created from SC module
#And    I Press  "Launch an exceptional invoicing " button
#And    I fill in  "Invoice date"  with a <InvoiceDate>
#And    I fill in  "Last transaction date"  with a <LastTransactionDate>
#And    I Press  "Launch" button
#Then   The Invoice is generated
#And    I see success  <message>
