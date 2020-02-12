@ignore
Feature: Discount by filter
    Allow invoicing discounts to apply to a set of rated transactions, selected using a Filter entity on RT.
    Background:
        Given a new discount type "FILTERED" as been added to DiscountPlanItem.discountPlanItemType
        And a new field "filter" of type Filter has been added to DiscountPlanItem

    Scenario: Create a filtered discount
        Given a DiscountPlan has been created
        When I create a DiscountPlanItem with type "FILTERED" linked to DiscountPlan
        Then "invoiceCategory" is not mandatory
        And "invoiceCategory" is not mandatory
        And "filter" is mandatory

    Scenario: Evaluate a DiscountPlanItem of type FILTERED
        Given a DiscountPlan has been created
        And a DiscountPlanItem with type "FILTERED" is linked to the DiscountPlan
        When I evaluate the DiscountPlanItem
        Then "discountValue" is considered as a percentage
        And "filter" is is used to select the RatedTransactions on which we apply the discount
        And an InvoiceAggregate is created for the discount plan item
