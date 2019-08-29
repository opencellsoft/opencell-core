# Overview of Opencell
Version 17, last updated by ethan.opencell at 2017-11-18

Opencell is a billing system for mediation, rating, invoicing, payment, dunning and composition.

### Opencell is adapted to the following situations :

* Batch process of thousands of invoices per month (or much more)
* Complex price plans, real time rating, re-rating
* Automatic provisionning through API
* Multi-format CDRs
* Pre/Post-Invoicing reports


### Opencell is not adapted for :

* Invoicing you could do with Excel : you need a server with Wildfly and to launch several standalone processes in order to produce even one invoice.
* Order entry: The catalog  do not cover complexity of commercial offers, with incompatibilities and upgrade or downgrade rules.
* Selfcare: Don't think to give the GUI to your customers. It is for people that know what they do.

--- 
 
# Main entities
### Catalog

* Charges: one-shot, recurring (and associated calendar), usage
* Services: subscription charges, termination charges, recurring charges, usage charges associated to an invoice subcategory and potentially prepaid wallets and counters for usage.
* Offers: List of services

### Account Hierarchy

* Provider: All accounts (and other entities) are associated to a provider
* Customer account :  account holding the balance and its operations (payments, invoices, refunds, etc.)
* Billing account :  account holding the invoices and the transactions (through wallets), can be associated to a billing cycle and discounts
* User account : account holding the information of the user of the services
* Subscription : entity holding instances of services and options, counters, access (that allow to route CDRs to account)

### Invoices

* Categories and subcategories
* Taxes (associated to an invoice subcategorie)
* Invoicing calendars

### AR

* Account operations
* Automating matching of payment and invoices
* SEPA payments

### Dunning

* Dunning plans (steps, actions)

### Composition

* Invoice and messages templates (electronic & paper)
* Messages and campaigns
* Communication policies (global and per account)

 
# Features

### Mediation 

* Multi-CDR transfer protocol (files, WS, DB, message queues, etc.)
* Multi-CDR file format (csv, asn1, txt, etc.)
* Deduplication
* Zoning, timing, numbering plan
* Rated and non-rated CDR management
* Multi-EDR production protocol (files, WS, DB, message queues, etc.)

### Rating

* Multi-EDR transfer protocol (files, WS, DB, message queues, etc.)
* One-shot, recurring & usage rating
* In memory compiled price plan and charging plan with configuration loaded from database
* Ability to split charges on distinct subscriptions.
* Arbitrary decimals calculations.
* Ability to re-rate transactions (in case of mistake in price plans or subscription).
* Ability to rate both with and without tax (for B2C and B2B)
* Multi-transaction production protocol (files, WS, DB, message queues, etc.)
* Multi-currency

### Invoicing

* Invoicing list of accounts or all accounts of a billing cycle
* Pre & post-Invoicing report (amount invoice, per customer category, per payment type, tax aggregates, etc.)
* Transaction modifications in order for total price with tax to match sum of price with tax of transactions (B2C case)
* Multi-language

### Provisionning & charging

* XML file and WS API for accounts, subscriptions and services provisioning
* Batch for recurring charge applications according to charge calendar (in advance or after period)
* Subscription and termination pro rata computation
* Service & charge cancellation : ability to cancel all non invoiced transactions
* Termination reason management : configuration of pro rata, reimbursement, contract duration charges depending on termination reasons

### Account receivable and dunning

* Automatic direct bebit payment (with bank files import)
* Automatic invoices<->payments matching
* Configurable dunning transitions (X days after first unpaid invoice, customer category, amount due, etc.)
* Configurable dunning actions (email, provisioning action, fee, etc.)

### Composition

* HTML/PDF Invoice production
* HTML/PDF dunning letters
* Email and paper channels
* Campaign management  

### Reporting and notification

* Measurable quantities (SQL query executed by a job)
* Measurable values (computed by the job, or entered manually)
* Graphs (bar, line, pie) displayed on the home page
* Internal, email and webhook notification

### Business intelligence 
* Requires deployment of Pentaho, a third-party software not included in Opencell. 
* Sales & accounting reports
* Multi-format generation (pdf, xml, csv, html, etc.)
* Scheduling
* Easy deploy of any custom transformation ot report

 
# Technical overview

 Opencell is coded in Java using JPA2 for persistence.
As for most of Opencell process rely on counting and aggregation, SQL database has been chosen over other kind of repository or Non SQL storage.
The solution has been deployed in production on Oracle, MySQL and PostgreSQL databes

Opencell core is a Java EE8 web application.
it is based on a generic batch processing framework.
This allow to concentrate development on business processes and lets the framework handle the usual tasks of error handling, transaction, file and other format processing.
They can be deployed on several distinct servers and run simultaneously.
The load-balancing can be performed per provider, customer accounts, etc.

---