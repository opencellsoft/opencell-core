@adminUrl
Feature: Test all URLs of Admin

  @admin @superadmin
  Scenario Outline: Open Configuration pages
    When I go to this "<url>"
    Then I should be on "<pageName>"
    And Validate that the statusCode is "<statusCode>"

    Examples: 
      | url                                                                   | pageName                                 | statusCode |
      | /index.jsf                                                            | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/providers/providerSelfDetail.jsf?mode=appConfiguration   | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/providerContacts/providerContacts.jsf                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/meveoUsers/users.jsf                                     | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/userRoles/userRoles.jsf                                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/userGroupHierarchy/userGroupHierarchy.jsf                | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/sellers/sellers.jsf                                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/title/titles.jsf                                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/customerBrands/customerBrands.jsf                          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/customerCategories/customerCategories.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/creditCategories/creditCategories.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/occTemplates/occTemplates.jsf                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/tradingLanguages/tradingLanguages.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/tradingCurrencies/tradingCurrencies.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/tradingCountries/tradingCountries.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/multiLanguageField/multiLanguageFields.jsf               | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/communication/emailTemplates.jsf                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/calendars/calendars.jsf                                | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/billingCycles/billingCycles.jsf                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/taxes/taxes.jsf                                          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/invoiceCategories/invoiceCategories.jsf                | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/invoiceSubCategories/invoiceSubCategories.jsf          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoiceTypes/invoiceTypes.jsf                          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoiceSequences/invoiceSequences.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/terminationReason/terminationReasons.jsf                 | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/channels/channels.jsf                                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/offerTemplates/offerTemplates.jsf                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/offerTemplateCategories/offerTemplateCategories.jsf    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/serviceTemplates/serviceTemplates.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/chargeTemplates/chargeTemplates.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/recurringChargeTemplates/recurringChargeTemplates.jsf  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/oneShotChargeTemplates/oneShotChargeTemplates.jsf      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/usageChargeTemplates/usageChargeTemplates.jsf          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/counterTemplates/counterTemplates.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/productTemplates/productTemplates.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/productChargeTemplates/productChargeTemplates.jsf      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/bundleTemplates/bundleTemplates.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/triggeredEdrTemplates/triggeredEdrTemplates.jsf        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/walletTemplates/walletTemplates.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/businessOfferModels/businessOfferModels.jsf            | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/businessServiceModels/businessServiceModels.jsf        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/businessProductModels/businessProductModels.jsf        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/pricePlanMatrixes/pricePlanMatrixes.jsf                | Opencell \| Open Source Billing Platform |        200 |
      | /pages/catalog/discountPlans/discountPlans.jsf                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/customers/customerSearch.jsf                               | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/customers/customers.jsf                                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/customerAccounts/customerAccounts.jsf                 | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/billingAccounts/billingAccounts.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/userAccounts/userAccounts.jsf                          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/crmAccounts/crmAccounts.jsf                                | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/subscriptions/subscriptions.jsf                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/medina/access/access.jsf                                       | Opencell \| Open Source Billing Platform |        200 |
      | /pages/crm/businessAccountModels/businessAccountModels.jsf            | Opencell \| Open Source Billing Platform |        200 |
      | /pages/quote/quotes/quotes.jsf                                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/order/orders/orders.jsf                                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/rating/edr/edrList.jsf                                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/medina/importedFile/importedFiles.jsf                          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/walletOperations/walletOperations.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/ratedTransactions/ratedTransactions.jsf                | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoicing/billingRuns.jsf                              | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoicing/recurringInvoicing.jsf                       | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoicing/exceptionelInvoicing.jsf                     | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoices/invoices.jsf                                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoices/createInvoiceDetail.jsf?mode=agregated        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/invoices/createInvoiceDetail.jsf?mode=detailed         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/dDRequestBuilders/dDRequestBuilders.jsf               | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/ddrequestLotOp/ddrequestLotOps.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/ddrequestLot/ddrequestLots.jsf                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/paymentGateways/paymentGateways.jsf                   | Opencell \| Open Source Billing Platform |        200 |
      | /pages/payments/paymentScheduleTemplates/paymentScheduleTemplates.jsf | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/workflow/genericWorkflows.jsf                            | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/workflow/workflowInstanceHistories.jsf                   | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/accountingCodes/accountingCodes.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/billing/generalLedgers/generalLedgers.jsf                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/finance/revenueRecognitionRules/revenueRecognitionRules.jsf    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/finance/revenueSchedules/revenueSchedules.jsf                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/finance/reportExtracts/reportExtracts.jsf                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/finance/reportExtractHistories/reportExtractHistories.jsf      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/finance/accountingWriting/accountingWritings.jsf               | Opencell \| Open Source Billing Platform |        200 |
      | /pages/reporting/dwh/measurableQuantities.jsf                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/reporting/dwh/measuredValueDetail.jsf                          | Opencell \| Open Source Billing Platform |        200 |
      | /pages/reporting/dwh/charts.jsf                                       | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/job/jobInstances.jsf                                     | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/job/timerEntities.jsf                                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/meveoInstance/meveoInstances.jsf                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/module/modules.jsf                                       | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/customEntities/customizedEntities.jsf                    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/scriptInstanceCategories/scriptInstanceCategories.jsf    | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/scriptInstances/scriptInstances.jsf                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/filters/filters/filters.jsf                              | Opencell \| Open Source Billing Platform |        200 |
      | /pages/notification/notifications.jsf                                 | Opencell \| Open Source Billing Platform |        200 |
      | /pages/notification/webHooks.jsf                                      | Opencell \| Open Source Billing Platform |        200 |
      | /pages/notification/emailNotifications.jsf                            | Opencell \| Open Source Billing Platform |        200 |
      | /pages/notification/jobTriggers.jsf                                   | Opencell \| Open Source Billing Platform |        200 |
      | /pages/notification/notificationHistories.jsf                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/notification/inboundRequests.jsf                               | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/export/import.jsf                                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/export/export.jsf                                        | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/auditLogs/auditConfiguration.jsf                         | Opencell \| Open Source Billing Platform |        200 |
      | /pages/admin/auditLogs/auditLogs.jsf                                  | Opencell \| Open Source Billing Platform |        200 |
      | /pages/reporting/cache/caches.jsf                                     | Opencell \| Open Source Billing Platform |        200 |
      | /pages/index/fullTextSearch.jsf                                       | Opencell \| Open Source Billing Platform |        200 |
      | /pages/index/index.jsf                                                | Opencell \| Open Source Billing Platform |        200 |
