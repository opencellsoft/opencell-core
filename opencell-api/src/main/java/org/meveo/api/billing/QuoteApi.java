package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.order.OrderProductCharacteristicEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteItem;
import org.meveo.model.quote.QuoteItemProductOffering;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.quote.QuoteInvoiceInfo;
import org.meveo.service.quote.QuoteService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.wf.WorkflowService;
import org.meveo.util.EntityCustomizationUtils;
import org.slf4j.Logger;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;
import org.tmf.dsmapi.catalog.resource.order.ProductOrderItem;
import org.tmf.dsmapi.catalog.resource.order.ProductRelationship;
import org.tmf.dsmapi.catalog.resource.product.BundledProductReference;
import org.tmf.dsmapi.quote.Characteristic;
import org.tmf.dsmapi.quote.ProductQuote;
import org.tmf.dsmapi.quote.ProductQuoteItem;

@Stateless
public class QuoteApi extends BaseApi {

    @Inject
    private Logger log;

    @Inject
    private ProductOfferingService productOfferingService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ProductInstanceService productInstanceService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private QuoteService quoteService;

    @Inject
    private WorkflowService workflowService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private OrderApi orderApi;

    @Inject
    private InvoiceApi invoiceApi;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private TerminationReasonService terminationReasonService;

    /**
     * Register a quote from TMForumApi
     * 
     * @param productQuote Quote
     * 
     * @return Quote updated
     * @throws MissingParameterException
     * @throws IncorrectSusbcriptionException
     * @throws IncorrectServiceInstanceException
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public ProductQuote createQuote(ProductQuote productQuote) throws MeveoApiException, BusinessException {
    	
        List<ProductQuoteItem> quoteItem1 = productQuote.getQuoteItem();
		if (quoteItem1 == null || quoteItem1.isEmpty()) {
            missingParameters.add("quoteItem");
        }
        if (productQuote.getQuoteDate() == null) {
            missingParameters.add("quoteDate");
        }

        handleMissingParameters();
        
        List<Characteristic> characteristic = productQuote.getCharacteristic();
        
		if (characteristic.size() > 0) {
            for (Characteristic quoteCharacteristic : characteristic) {
                if (quoteCharacteristic.getName().equals(OrderProductCharacteristicEnum.PRE_QUOTE_SCRIPT.getCharacteristicName())) {
                    String scriptCode = quoteCharacteristic.getValue();
                    Map<String, Object> context = new HashMap<>();
                    context.put("productQuote", productQuote);
                    scriptInstanceService.execute(scriptCode, context);
                    productQuote = (ProductQuote) context.get(Script.RESULT_VALUE);
                    break;
                }
            }
        }

        Quote quote = new Quote();
        quote.setCode(UUID.randomUUID().toString());
        quote.setCategory(productQuote.getCategory());
        quote.setNotificationContact(productQuote.getNotificationContact());
        quote.setDescription(productQuote.getDescription());
        quote.setExternalId(productQuote.getExternalId());
        quote.setReceivedFromApp("API");
        quote.setQuoteDate(productQuote.getQuoteDate() != null ? productQuote.getQuoteDate() : new Date());
        quote.setRequestedCompletionDate(productQuote.getQuoteCompletionDate());
        quote.setFulfillmentStartDate(productQuote.getFulfillmentStartDate());
        if (productQuote.getValidFor() != null) {
            quote.setValidity(productQuote.getValidFor().toDatePeriod());
        }

        if (productQuote.getState() != null) {
            quote.setStatus(QuoteStatusEnum.valueByApiState(productQuote.getState()));
        } else {
            quote.setStatus(QuoteStatusEnum.IN_PROGRESS);
        }

        UserAccount quoteLevelUserAccount = null;
        org.meveo.model.billing.BillingAccount billingAccount = null; // used for validation only

        if (productQuote.getBillingAccount() != null && !productQuote.getBillingAccount().isEmpty()) {
            String billingAccountId = productQuote.getBillingAccount().get(0).getId();
            if (!StringUtils.isEmpty(billingAccountId)) {
                //quoteLevelUserAccount = userAccountService.findByCode(billingAccountId);
            	quoteLevelUserAccount = (UserAccount) userAccountService.getEntityManager()
            			.createNamedQuery("UserAccount.findByCode")
            			.setParameter("code", billingAccountId).getSingleResult();
                if (quoteLevelUserAccount == null) {
                    throw new EntityDoesNotExistsException(UserAccount.class, billingAccountId);
                }
                billingAccount = quoteLevelUserAccount.getBillingAccount();
            }
        }

        for (ProductQuoteItem productQuoteItem : quoteItem1) {
            UserAccount itemLevelUserAccount = null;

            if (productQuoteItem.getBillingAccount() != null && !productQuoteItem.getBillingAccount().isEmpty()) {
                String billingAccountId = productQuoteItem.getBillingAccount().get(0).getId();
                if (!StringUtils.isEmpty(billingAccountId)) {
                    itemLevelUserAccount = userAccountService.findByCode(billingAccountId);
                    if (itemLevelUserAccount == null) {
                        throw new EntityDoesNotExistsException(UserAccount.class, billingAccountId);
                    }

                    if (billingAccount != null && !billingAccount.equals(itemLevelUserAccount.getBillingAccount())) {
                        throw new InvalidParameterException("Accounts declared on quote level and item levels don't belong to the same billing account");
                    }
                }
            }

            if (itemLevelUserAccount == null && quoteLevelUserAccount == null) {
                missingParameters.add("billingAccount");

            } else if (itemLevelUserAccount == null && quoteLevelUserAccount != null) {
                productQuoteItem.addBillingAccount(quoteLevelUserAccount.getCode());
            }

            handleMissingParameters();

            QuoteItem quoteItem = new QuoteItem();
            List<QuoteItemProductOffering> productOfferings = new ArrayList<>();
            ProductOffering mainProductOffering = null;

            // For modify and delete actions, product offering might not be specified
            if (productQuoteItem.getProductOffering() != null) {
                Date subscriptionDate = ((Date) getProductCharacteristic(productQuoteItem.getProduct(), OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(),
                    Date.class, DateUtils.setTimeToZero(quote.getQuoteDate())));

                mainProductOffering = productOfferingService.findByCode(productQuoteItem.getProductOffering().getId(), subscriptionDate);
                if (mainProductOffering == null) {
                    throw new EntityDoesNotExistsException(ProductOffering.class,
                        productQuoteItem.getProductOffering().getId() + " / " + DateUtils.formatDateWithPattern(subscriptionDate, ParamBean.getInstance().getDateTimeFormat()));
                }
                productOfferings.add(new QuoteItemProductOffering(quoteItem, mainProductOffering, 0));

                if (productQuoteItem.getProductOffering().getBundledProductOffering() != null) {
                    for (BundledProductReference bundledProductOffering : productQuoteItem.getProductOffering().getBundledProductOffering()) {
                        ProductOffering productOfferingInDB = productOfferingService.findByCode(bundledProductOffering.getReferencedId(), subscriptionDate);
                        if (productOfferingInDB == null) {
                            throw new EntityDoesNotExistsException(ProductOffering.class,
                                bundledProductOffering.getReferencedId() + " / " + DateUtils.formatDateWithPattern(subscriptionDate, ParamBean.getInstance().getDateTimeFormat()));
                        }
                        productOfferings.add(new QuoteItemProductOffering(quoteItem, productOfferingInDB, productOfferings.size()));
                    }
                }
            } else {
                // We need productOffering so we know if product is subscription or productInstance - NEED TO FIX IT
                throw new MissingParameterException("productOffering");
            }

            // Validate or supplement if not provided subscription renewal fields
            if (mainProductOffering instanceof OfferTemplate) {
                orderApi.validateOrSupplementSubscriptionRenewalFields(productQuoteItem.getProduct(), (OfferTemplate) mainProductOffering);
            }

            quoteItem.setItemId(productQuoteItem.getId());

            quoteItem.setQuote(quote);
            quoteItem.setSource(ProductQuoteItem.serializeQuoteItem(productQuoteItem));
            quoteItem.setQuoteItemProductOfferings(productOfferings);
            quoteItem.setUserAccount(itemLevelUserAccount != null ? itemLevelUserAccount : quoteLevelUserAccount);

            if (productQuoteItem.getState() != null) {
                quoteItem.setStatus(QuoteStatusEnum.valueByApiState(productQuoteItem.getState()));
            } else {
                quoteItem.setStatus(QuoteStatusEnum.IN_PROGRESS);
            }

            // Extract products that are not services. For each product offering there must be a product. Products that exceed the number of product offerings are treated as
            // services.
            //
            // Sample of ordering a single product:
            // productOffering
            // product with product characteristics
            //
            // Sample of ordering two products bundled under an offer template:
            // productOffering bundle (offer template)
            // ...productOffering (product1)
            // ...productOffering (product2)
            // product with subscription characteristics
            // ...product with product1 characteristics
            // ...product with product2 characteristics
            // ...product for service with service1 characteristics - not considered as product/does not required ID for modify/delete opperation
            // ...product for service with service2 characteristics - not considered as product/does not required ID for modify/delete opperation

            List<Product> products = new ArrayList<>();
            products.add(productQuoteItem.getProduct());
            if (productOfferings.size() > 1 && productQuoteItem.getProduct().getProductRelationship() != null
                    && !productQuoteItem.getProduct().getProductRelationship().isEmpty()) {
                for (ProductRelationship productRelationship : productQuoteItem.getProduct().getProductRelationship()) {
                    products.add(productRelationship.getProduct());
                    if (productOfferings.size() >= products.size()) {
                        break;
                    }
                }
            }

            quote.addQuoteItem(quoteItem);
        }

        // populate customFields
        try {
            populateCustomFields(productQuote.getCustomFields(), quote, true);

        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        quoteService.create(quote);

        if (characteristic.size() > 0) {
            for (Characteristic quoteCharacteristic : characteristic) {
                if (quoteCharacteristic.getName().equals(OrderProductCharacteristicEnum.POST_QUOTE_SCRIPT.getCharacteristicName())) {
                    String scriptCode = quoteCharacteristic.getValue();
                    Map<String, Object> context = new HashMap<>();
                    context.put("productQuote", productQuote);
                    context.put("quote", quote);
                    scriptInstanceService.execute(scriptCode, context);
                    break;
                }
            }
        }

        // Commit before initiating workflow/quote processing
        quoteService.commit();

        quote = initiateWorkflow(quote);

        return quoteToDto(quote);
    }

    /**
     * Initiate workflow on quote. If workflow is enabled on Quote class, then execute workflow. If workflow is not enabled - then process the quote right away.
     * 
     * @param quote Quote
     * 
     * @return
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public Quote initiateWorkflow(Quote quote) throws BusinessException {

        if (workflowService.isWorkflowSetup(Quote.class)) {
            quote = (Quote) workflowService.executeMatchingWorkflows(quote);

        } else {
            try {
                quote = processQuote(quote);
            } catch (MeveoApiException e) {
                throw new BusinessException(e);
            }
        }

        return quote;

    }

    /**
     * Process the quote for workflow
     * 
     * @param quote
     * 
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public Quote processQuote(Quote quote) throws BusinessException, MeveoApiException {

        // Nothing to process in final state
        if (quote.getStatus() == QuoteStatusEnum.CANCELLED || quote.getStatus() == QuoteStatusEnum.ACCEPTED || quote.getStatus() == QuoteStatusEnum.REJECTED) {
            return quote;
        }

        log.info("Processing quote {}", quote.getCode());

        for (QuoteItem quoteItem : quote.getQuoteItems()) {
            processQuoteItem(quote, quoteItem);
        }

        quote.setStatus(QuoteStatusEnum.PENDING);
        for (QuoteItem quoteItem : quote.getQuoteItems()) {
            quoteItem.setStatus(QuoteStatusEnum.PENDING);
        }

        quote = invoiceQuote(quote);

        quote = quoteService.update(quote);

        log.trace("Finished processing quote {}", quote.getCode());

        return quote;
    }

    /**
     * Process quote item for workflow
     * 
     * @param quote Quote
     * @param quoteItem Quote item
     * 
     * @throws BusinessException
     * @throws MeveoApiException
     */
    private void processQuoteItem(Quote quote, QuoteItem quoteItem) throws BusinessException, MeveoApiException {

        log.info("Processing quote item {} {}", quote.getCode(), quoteItem.getItemId());

        log.info("Finished processing quote item {} {}", quote.getCode(), quoteItem.getItemId());
    }

    /**
     * Create invoices for the quote
     * 
     * @param quote Quote
     * 
     * @throws BusinessException
     * @throws MeveoApiException
     */
    public Quote invoiceQuote(Quote quote) throws BusinessException {

        log.info("Creating invoices for quote {}", quote.getCode());

        try {

            Map<String, List<QuoteInvoiceInfo>> quoteInvoiceInfos = new HashMap<>();

            for (QuoteItem quoteItem : quote.getQuoteItems()) {
                String baCode = quoteItem.getUserAccount().getBillingAccount().getCode();
                if (!quoteInvoiceInfos.containsKey(baCode)) {
                    quoteInvoiceInfos.put(baCode, new ArrayList<QuoteInvoiceInfo>());
                }
                quoteInvoiceInfos.get(baCode).add(preInvoiceQuoteItem(quote, quoteItem));
            }

            List<Invoice> invoices = quoteService.provideQuote(quoteInvoiceInfos);

            List<QuoteInvoiceInfo> quoteInvoiceInfosAll = new ArrayList<>();

            for (List<QuoteInvoiceInfo> quoteInvoiceInfo : quoteInvoiceInfos.values()) {
                quoteInvoiceInfosAll.addAll(quoteInvoiceInfo);
            }

            destroyInvoiceQuoteItems(quoteInvoiceInfosAll);

            for (Invoice invoice : invoices) {
                invoice.setQuote(quote);
                invoice = invoiceService.update(invoice);
                quote.getInvoices().add(invoice);
            }
            quote = quoteService.update(quote);

        } catch (MeveoApiException e) {
            throw new BusinessException(e.getMessage());
        }

        log.trace("Finished creating invoices for quote {}", quote.getCode());

        return quote;
    }

    /**
     * Destroy any temporary entities created for invoicing - remove CF values
     * 
     * @param quoteInvoiceInfo Instantiated product instances and subscriptions and other grouped information of quote item ready for invoicing
     * @throws BusinessException
     */
    private void destroyInvoiceQuoteItems(List<QuoteInvoiceInfo> quoteInvoiceInfos) throws BusinessException {

        for (QuoteInvoiceInfo quoteInvoiceInfo : quoteInvoiceInfos) {

            // Remove CF values for product instances
            if (quoteInvoiceInfo.getProductInstances() != null) {
                for (ProductInstance productInstance : quoteInvoiceInfo.getProductInstances()) {
                    customFieldInstanceService.removeCFValues(productInstance);
                }
            }

            // Remove CF values for subscription and service instances
            if (quoteInvoiceInfo.getSubscription() != null) {
                customFieldInstanceService.removeCFValues(quoteInvoiceInfo.getSubscription());

                for (ServiceInstance serviceInstance : quoteInvoiceInfo.getSubscription().getServiceInstances()) {
                    customFieldInstanceService.removeCFValues(serviceInstance);
                }
            }
        }

    }

    /**
     * Prepare info for invoicing for quote item
     * 
     * @param quote Quote
     * @param quoteItem Quote item
     * 
     * @return Instantiated product instances and subscriptions and other grouped information of quote item ready for invoicing
     * @throws BusinessException
     * @throws MeveoApiException
     */
    private QuoteInvoiceInfo preInvoiceQuoteItem(Quote quote, QuoteItem quoteItem) throws BusinessException, MeveoApiException {

        log.info("Processing quote item {} {}", quote.getCode(), quoteItem.getItemId());

        List<ProductInstance> productInstances = new ArrayList<>();
        Subscription subscription = null;

        ProductQuoteItem productQuoteItem = ProductQuoteItem.deserializeQuoteItem(quoteItem.getSource());

        // Ordering a new product
        ProductOffering primaryOffering = quoteItem.getMainOffering();

        // Just a simple case of ordering a single product
        if (primaryOffering instanceof ProductTemplate) {

            ProductInstance productInstance = instantiateVirtualProduct((ProductTemplate) primaryOffering, productQuoteItem.getProduct(), quoteItem, productQuoteItem, null);
            productInstances.add(productInstance);

            // A complex case of ordering from offer template with services and optional products
        } else {

            // Distinguish bundled products which could be either services or products

            List<Product> products = new ArrayList<>();
            List<Product> services = new ArrayList<>();
            int index = 1;
            if (productQuoteItem.getProduct().getProductRelationship() != null && !productQuoteItem.getProduct().getProductRelationship().isEmpty()) {
                for (ProductRelationship productRelationship : productQuoteItem.getProduct().getProductRelationship()) {
                    if (index < quoteItem.getQuoteItemProductOfferings().size()) {
                        products.add(productRelationship.getProduct());
                    } else {
                        services.add(productRelationship.getProduct());
                    }
                    index++;
                }
            }

            // Instantiate a service
            subscription = instantiateVirtualSubscription((OfferTemplate) primaryOffering, services, quoteItem, productQuoteItem);

            // Instantiate products - find a matching product offering. The order of products must match the order of productOfferings
            index = 1;
            for (Product product : products) {
                ProductTemplate productOffering = (ProductTemplate) quoteItem.getQuoteItemProductOfferings().get(index).getProductOffering();
                ProductInstance productInstance = instantiateVirtualProduct(productOffering, product, quoteItem, productQuoteItem, subscription);
                productInstances.add(productInstance);
                index++;
            }
        }

        // Use either subscription start/end dates from subscription/products or subscription start/end value from quote item
        // TODO does not support if dates in subscription, services and products differ one from another one.
        Date fromDate = null;
        Date toDate = null;
        if (subscription != null) {
            fromDate = subscription.getSubscriptionDate();
            toDate = subscription.getEndAgreementDate();
        }
        // No toDate for products
        for (ProductInstance productInstance : productInstances) {
            if (fromDate == null) {
                fromDate = productInstance.getApplicationDate();
            } else if (productInstance.getApplicationDate().before(fromDate)) {
                fromDate = productInstance.getApplicationDate();
            }
        }
        if (productQuoteItem.getSubscriptionPeriod() != null && productQuoteItem.getSubscriptionPeriod().getStartDateTime() != null
                && productQuoteItem.getSubscriptionPeriod().getStartDateTime().before(fromDate)) {
            fromDate = productQuoteItem.getSubscriptionPeriod().getStartDateTime();
        }
        if (toDate == null && productQuoteItem.getSubscriptionPeriod() != null) {
            toDate = productQuoteItem.getSubscriptionPeriod().getEndDateTime();
        }
        if (toDate == null) {
            toDate = fromDate;
        }

        // log.error("AKK date from {} to {}", fromDate, toDate);

        QuoteInvoiceInfo quoteInvoiceInfo = new org.meveo.service.quote.QuoteInvoiceInfo(quote.getCode(), productQuoteItem.getConsumptionCdr(), subscription, productInstances,
            fromDate, toDate);

        // Serialize back the productOrderItem with updated invoice attachments
        quoteItem.setSource(ProductQuoteItem.serializeQuoteItem(productQuoteItem));

        log.info("Finished processing quote item {} {}", quote.getCode(), quoteItem.getItemId());

        return quoteInvoiceInfo;
    }

    private Subscription instantiateVirtualSubscription(OfferTemplate offerTemplate, List<Product> services, QuoteItem quoteItem, ProductQuoteItem productQuoteItem)
            throws BusinessException, MissingParameterException, InvalidParameterException {

        log.debug("Instantiating virtual subscription from offer template {} for quote {} line {}", offerTemplate.getCode(), quoteItem.getQuote().getCode(), quoteItem.getItemId());

        Product product = productQuoteItem.getProduct();

        String subscriptionCode = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_CODE.getCharacteristicName(), String.class,
            UUID.randomUUID().toString());

        Subscription subscription = new Subscription();
        subscription.setCode(subscriptionCode);
        subscription.setUserAccount(quoteItem.getUserAccount());
        subscription.setOffer(offerTemplate);
        subscription.setSubscriptionDate((Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(), Date.class,
            DateUtils.setTimeToZero(quoteItem.getQuote().getQuoteDate())));
        subscription.setEndAgreementDate((Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_END_DATE.getCharacteristicName(), Date.class, null));

        String terminationReasonCode = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.TERMINATION_REASON.getCharacteristicName(), String.class, null);

        Date terminationDate = (Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.TERMINATION_DATE.getCharacteristicName(), Date.class, null);

        if (terminationDate == null && terminationReasonCode != null) {
            throw new MissingParameterException("terminationDate");
        } else if (terminationDate != null && terminationReasonCode == null) {
            throw new MissingParameterException("terminationReason");
        }

        if (terminationReasonCode != null) {
            subscription.setTerminationDate(terminationDate);

            SubscriptionTerminationReason terminationReason = terminationReasonService.findByCode(terminationReasonCode);
            if (terminationReason != null) {
                subscription.setSubscriptionTerminationReason(terminationReason);
            } else {
                throw new InvalidParameterException("terminationReason", terminationReasonCode);
            }
        }

        // Validate and populate customFields
        CustomFieldsDto customFields = extractCustomFields(product, Subscription.class);
        try {
            populateCustomFields(customFields, subscription, true, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new BusinessException("Failed to associate custom field instance to an entity", e);
        }

        // instantiate and activate services
        processServices(subscription, services);

        return subscription;
    }

    private ProductInstance instantiateVirtualProduct(ProductTemplate productTemplate, Product product, QuoteItem quoteItem, ProductQuoteItem productQuoteItem,
            Subscription subscription) throws BusinessException, MissingParameterException {

        log.debug("Instantiating virtual product from product template {} for quote {} line {}", productTemplate.getCode(), quoteItem.getQuote().getCode(), quoteItem.getItemId());

        BigDecimal quantity = ((BigDecimal) getProductCharacteristic(product, OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY.getCharacteristicName(), BigDecimal.class,
            new BigDecimal(1)));
        Date chargeDate = ((Date) getProductCharacteristic(product, OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(), Date.class,
            DateUtils.setTimeToZero(quoteItem.getQuote().getQuoteDate())));

        String code = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE.getCharacteristicName(), String.class,
            UUID.randomUUID().toString());

        String criteria1 = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.CRITERIA_1.getCharacteristicName(), String.class, null);
        String criteria2 = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.CRITERIA_2.getCharacteristicName(), String.class, null);
        String criteria3 = (String) getProductCharacteristic(product, OrderProductCharacteristicEnum.CRITERIA_3.getCharacteristicName(), String.class, null);

        ProductInstance productInstance = new ProductInstance(quoteItem.getUserAccount(), subscription, productTemplate, quantity, chargeDate, code,
            productTemplate.getDescription(), null);
        productInstance.setOrderNumber(quoteItem.getQuote().getCode());
        try {
            CustomFieldsDto customFields = extractCustomFields(product, ProductInstance.class);
            populateCustomFields(customFields, productInstance, true, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new BusinessException("Failed to associate custom field instance to an entity", e);
        }
        productInstanceService.instantiateProductInstance(productInstance, criteria1, criteria2, criteria3, true);

        return productInstance;
    }

    @SuppressWarnings({ "rawtypes" })
    private CustomFieldsDto extractCustomFields(Product product, Class appliesToClass) {

        if (product.getProductCharacteristic() == null || product.getProductCharacteristic().isEmpty()) {
            return null;
        }

        CustomFieldsDto customFieldsDto = new CustomFieldsDto();

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(EntityCustomizationUtils.getAppliesTo(appliesToClass, null));

        for (ProductCharacteristic characteristic : product.getProductCharacteristic()) {
            if (characteristic.getName() != null && cfts.containsKey(characteristic.getName())) {

                CustomFieldTemplate cft = cfts.get(characteristic.getName());
                CustomFieldDto cftDto = entityToDtoConverter.customFieldToDTO(characteristic.getName(), CustomFieldValue.parseValueFromString(cft, characteristic.getValue()),
                    cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY);
                customFieldsDto.getCustomField().add(cftDto);
            }
        }

        return customFieldsDto;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getProductCharacteristic(Product product, String code, Class valueClass, Object defaultValue) {

        if (product.getProductCharacteristic() == null || product.getProductCharacteristic().isEmpty()) {
            return defaultValue;
        }

        Object value = null;
        for (ProductCharacteristic productCharacteristic : product.getProductCharacteristic()) {
            if (productCharacteristic.getName().equals(code)) {
                value = productCharacteristic.getValue();
                break;
            }
        }

        if (value != null) {

            // Need to perform conversion
            if (!valueClass.isAssignableFrom(value.getClass())) {

                if (valueClass == BigDecimal.class) {
                    value = new BigDecimal((String) value);

                }
                if (valueClass == Date.class) {
                    value = DateUtils.parseDateWithPattern((String) value, DateUtils.DATE_PATTERN);
                }
            }

        } else {
            value = defaultValue;
        }

        return value;
    }

    private void processServices(Subscription subscription, List<Product> services)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, MissingParameterException, InvalidParameterException {

        for (Product serviceProduct : services) {

            String serviceCode = (String) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SERVICE_CODE.getCharacteristicName(), String.class, null);

            if (StringUtils.isBlank(serviceCode)) {
                throw new MissingParameterException("serviceCode");
            }

            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setCode(serviceCode);
            serviceInstance.setQuantity((BigDecimal) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SERVICE_PRODUCT_QUANTITY.getCharacteristicName(),
                BigDecimal.class, new BigDecimal(1)));
            serviceInstance.setSubscriptionDate((Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SUBSCRIPTION_DATE.getCharacteristicName(),
                Date.class, subscription.getSubscriptionDate()));
            serviceInstance.setEndAgreementDate((Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.SUBSCRIPTION_END_DATE.getCharacteristicName(),
                Date.class, subscription.getEndAgreementDate()));
            serviceInstance.setRateUntilDate((Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.RATE_UNTIL_DATE.getCharacteristicName(),
                    Date.class, subscription.getEndAgreementDate()));

            String terminationReasonCode = (String) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.TERMINATION_REASON.getCharacteristicName(),
                String.class, null);

            Date terminationDate = (Date) getProductCharacteristic(serviceProduct, OrderProductCharacteristicEnum.TERMINATION_DATE.getCharacteristicName(), Date.class, null);

            if (terminationDate == null && terminationReasonCode != null) {
                throw new MissingParameterException("terminationDate");
            } else if (terminationDate != null && terminationReasonCode == null) {
                throw new MissingParameterException("terminationReason");
            }

            if (terminationReasonCode != null) {
                serviceInstance.setTerminationDate(terminationDate);

                SubscriptionTerminationReason terminationReason = terminationReasonService.findByCode(terminationReasonCode);
                if (terminationReason != null) {
                    serviceInstance.setSubscriptionTerminationReason(terminationReason);
                } else {
                    throw new InvalidParameterException("terminationReason", terminationReasonCode);
                }
            }

            if (serviceInstance.getTerminationDate() == null && subscription.getTerminationDate() != null) {
                serviceInstance.setTerminationDate(subscription.getTerminationDate());
                serviceInstance.setSubscriptionTerminationReason(subscription.getSubscriptionTerminationReason());
            }

            serviceInstance.setSubscription(subscription);
            serviceInstance.setServiceTemplate(serviceTemplateService.findByCode(serviceCode));

            // Validate and populate customFields
            CustomFieldsDto customFields = extractCustomFields(serviceProduct, ServiceInstance.class);
            try {
                populateCustomFields(customFields, serviceInstance, true, true);
            } catch (MissingParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new BusinessException("Failed to associate custom field instance to an entity", e);
            }

            serviceInstanceService.serviceInstanciation(serviceInstance, null, null, true);
        }
    }

    /**
     * @param quoteId quote id
     * @return product quote
     * @throws EntityDoesNotExistsException entity not exist exception
     * @throws BusinessException business exception.
     */
    public ProductQuote getQuote(String quoteId) throws EntityDoesNotExistsException, BusinessException {

        Quote quote = quoteService.findByCode(quoteId);

        if (quote == null) {
            throw new EntityDoesNotExistsException(ProductQuote.class, quoteId);
        }

        return quoteToDto(quote);
    }

    /**
     * @param filterCriteria filter criteria
     * @return list of product quote.
     * @throws BusinessException business exception
     */
    public List<ProductQuote> findQuotes(Map<String, List<String>> filterCriteria) throws BusinessException {

        List<Quote> quotes = quoteService.list();

        List<ProductQuote> productQuotes = new ArrayList<>();
        for (Quote quote : quotes) {
            productQuotes.add(quoteToDto(quote));
        }

        return productQuotes;
    }

    /**
     * @param quoteId quote id
     * @param productQuote product quote.
     * @return product quote
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api
     */
    public ProductQuote updatePartiallyQuote(String quoteId, ProductQuote productQuote) throws BusinessException, MeveoApiException {

        Quote quote = quoteService.findByCode(quoteId);
        if (quote == null) {
            throw new EntityDoesNotExistsException(ProductQuote.class, quoteId);
        }

        // populate customFields
        try {
            populateCustomFields(productQuote.getCustomFields(), quote, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        // TODO Need to initiate workflow if there is one

        quote = quoteService.refreshOrRetrieve(quote);

        return quoteToDto(quote);

    }

    /**
     * @param quoteId quote id
     * @throws EntityDoesNotExistsException exception when entity is not existed.
     * @throws ActionForbiddenException forbidden exception
     * @throws BusinessException business exception
     */
    public void deleteQuote(String quoteId) throws EntityDoesNotExistsException, ActionForbiddenException, BusinessException {

        Quote quote = quoteService.findByCode(quoteId);

        if (quote.getStatus() == QuoteStatusEnum.IN_PROGRESS || quote.getStatus() == QuoteStatusEnum.PENDING) {
            quoteService.remove(quote);
        }
    }

    /**
     * Convert quote stored in DB to quote DTO expected by tmForum api.
     * @param quote Quote to convert
     * @return Quote DTO object
     * @throws BusinessException business exception
     */
    private ProductQuote quoteToDto(Quote quote) throws BusinessException {

        ProductQuote productQuote = new ProductQuote();

        productQuote.setId(quote.getCode().toString());
        productQuote.setCategory(quote.getCategory());
        productQuote.setDescription(quote.getDescription());
        productQuote.setNotificationContact(quote.getNotificationContact());
        productQuote.setExternalId(quote.getExternalId());
        productQuote.setQuoteDate(quote.getQuoteDate());
        productQuote.setEffectiveQuoteCompletionDate(quote.getCompletionDate());
        productQuote.setFulfillmentStartDate(quote.getFulfillmentStartDate());
        productQuote.setQuoteCompletionDate(quote.getRequestedCompletionDate());
        productQuote.setState(quote.getStatus().getApiState());

        List<ProductQuoteItem> productQuoteItems = new ArrayList<>();
        productQuote.setQuoteItem(productQuoteItems);

        for (QuoteItem quoteItem : quote.getQuoteItems()) {
            productQuoteItems.add(quoteItemToDto(quoteItem));
        }

        productQuote.setCustomFields(entityToDtoConverter.getCustomFieldsWithInheritedDTO(quote, true));

        if (quote.getInvoices() != null && !quote.getInvoices().isEmpty()) {
            productQuote.setInvoices(new ArrayList<GenerateInvoiceResultDto>());
            for (Invoice invoice : quote.getInvoices()) {

                GenerateInvoiceResultDto invoiceDto = invoiceApi.createGenerateInvoiceResultDto(invoice, false, false);
                productQuote.getInvoices().add(invoiceDto);
            }
        }

        return productQuote;
    }

    /**
     * Convert quote item stored in DB to quoteItem dto expected by tmForum api. As actual dto was serialized earlier, all need to do is to deserialize it and update the status.
     * @param quoteItem Quote item to convert to dto
     * @return Quote item Dto
     * @throws BusinessException business exception
     */
    private ProductQuoteItem quoteItemToDto(QuoteItem quoteItem) throws BusinessException {

        ProductQuoteItem productQuoteItem = ProductQuoteItem.deserializeQuoteItem(quoteItem.getSource());

        productQuoteItem.setState(quoteItem.getQuote().getStatus().getApiState());

        return productQuoteItem;
    }

    /**
     * Distinguish bundled products which could be either services or products.
     * @param productQuoteItem Product order item DTO
     * @param quoteItem Order item entity
     * @return An array of List<Product> elements, first being list of products, and second - list of services
     */
    @SuppressWarnings("unchecked")
    public List<Product>[] getProductsAndServices(ProductQuoteItem productQuoteItem, QuoteItem quoteItem) {

        List<Product> products = new ArrayList<>();
        List<Product> services = new ArrayList<>();
        if (productQuoteItem != null) {
            int index = 1;
            if (productQuoteItem.getProduct().getProductRelationship() != null && !productQuoteItem.getProduct().getProductRelationship().isEmpty()) {
                for (ProductRelationship productRelationship : productQuoteItem.getProduct().getProductRelationship()) {
                    if (index < quoteItem.getQuoteItemProductOfferings().size()) {
                        products.add(productRelationship.getProduct());
                    } else {
                        services.add(productRelationship.getProduct());
                    }
                    index++;
                }
            }
        }
        return new List[] {products, services};
    }

    /**
     * Place an order from a quote.
     * @param quoteCode code of quote to convert to an order
     * @return Product order DTO object
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public ProductOrder placeOrder(String quoteCode) throws BusinessException, MeveoApiException {

        if (StringUtils.isEmpty(quoteCode)) {
            missingParameters.add("quoteCode");
        }

        handleMissingParameters();

        Quote quote = quoteService.findByCode(quoteCode);
        ProductOrder productOrder = new ProductOrder();
        productOrder.setOrderDate(new Date());
        productOrder.setRequestedStartDate(quote.getFulfillmentStartDate());
        productOrder.setDescription(quote.getDescription());
        productOrder.setOrderItem(new ArrayList<ProductOrderItem>());

        for (QuoteItem quoteItem : quote.getQuoteItems()) {
            ProductQuoteItem productQuoteItem = ProductQuoteItem.deserializeQuoteItem(quoteItem.getSource());

            ProductOrderItem orderItem = new ProductOrderItem();
            orderItem.setId(productQuoteItem.getId());
            orderItem.setAction(OrderItemActionEnum.ADD.toString().toLowerCase());
            orderItem.setBillingAccount(productQuoteItem.getBillingAccount());
            orderItem.setProduct(productQuoteItem.getProduct());
            orderItem.setProductOffering(productQuoteItem.getProductOffering());

            productOrder.getOrderItem().add(orderItem);
        }

        productOrder = orderApi.createProductOrder(productOrder, quote.getId());
        quote.setStatus(QuoteStatusEnum.ACCEPTED);
        quoteService.update(quote);

        return productOrder;
    }
}
