/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.XMLInvoiceHeaderCategoryDTO;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.TipPaymentMethod;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

@Stateless
public class XMLInvoiceCreator extends PersistenceService<Invoice> {

	/** Configuration parameter bean. */
    private ParamBean paramBean = ParamBean.getInstance();

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private CountryService countryService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CounterPeriodService counterPeriodService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /** transformer factory.*/
    private TransformerFactory transfac = TransformerFactory.newInstance();

    /** list of service's id, order's id, price plan's id.*/
    private List<Long> serviceIds = null, offerIds = null, priceplanIds = null;

    /** description map .*/
    private Map<String, String> descriptionMap = new HashMap<String, String>();

    /** default date format.*/
    private static String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    /** default date time format.*/
    private static String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /** temporary map to store billing cycle.*/
    private Map<BillingCycle, String> billingCycleMap = new HashMap<>();

    /** all rated transaction for a invoice.*/
    //private List<RatedTransaction> ratedTransactions = null;

    /** list of sub category invoice agregates.*/
    //private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = null;

    /**
     * @param invoice invoice used to create xml
     * @param isVirtual true/false (true for Quote/order)
     * @return xml file
     * @throws BusinessException business exception
     */
    public File createXMLInvoice(Invoice invoice, boolean isVirtual) throws BusinessException {
        log.debug("Creating xml for invoice id={} number={}. {}", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber());

		String invoiceXmlScript = (String) customFieldInstanceService.getCFValue(appProvider,
				"PROV_CUSTOM_INV_XML_SCRIPT_CODE");

		if (invoiceXmlScript != null) {
			ScriptInterface script = scriptInstanceService.getScriptInstance(invoiceXmlScript);
			Map<String, Object> methodContext = new HashMap<String, Object>();
			methodContext.put(Script.CONTEXT_ENTITY, invoice);
			methodContext.put("isVirtual", Boolean.valueOf(isVirtual));
			methodContext.put("XMLInvoiceCreator", this);
			if (script == null) {
				log.debug("script is null");
			}
			script.execute(methodContext);
			return (File) methodContext.get(Script.RESULT_VALUE);
		}

        try {
            return createDocumentAndFile(invoice, isVirtual);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new BusinessException("Failed to create xml file for invoice id=" + invoice.getId() + " number=" + invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber()
                    : invoice.getTemporaryInvoiceNumber(),
                e);
        }

    }

    /**
     * @param invoice invoice used to create xml
     * @param isVirtual true/false
     * @return xml file
     * @throws BusinessException business exception
     * @throws ParserConfigurationException parsing exception
     * @throws SAXException sax exception
     * @throws IOException IO exception
     */
    private File createDocumentAndFile(Invoice invoice, boolean isVirtual) throws BusinessException, ParserConfigurationException, SAXException, IOException {

        Document doc = createDocument(invoice, isVirtual);
        File file = createFile(doc, invoice);
        return file;
    }

    /**
     * @param doc DOM invoice
     * @param invoice invoice used to build xml
     * @return xml file
     * @throws BusinessException business exception
     */
    public File createFile(Document doc, Invoice invoice) throws BusinessException {
        try {
            Transformer trans = transfac.newTransformer();
            // trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // create string from xml tree
            DOMSource source = new DOMSource(doc);

            File xmlFile = new File(invoiceService.getFullXmlFilePath(invoice, true));

            StreamResult result = new StreamResult(xmlFile);

            StringWriter writer = new StringWriter();
            trans.transform(new DOMSource(doc), new StreamResult(writer));
            log.trace("XML invoice: " + writer.getBuffer().toString().replaceAll("\n|\r", ""));

            trans.transform(source, result);

            invoice.setXmlFilename(invoiceService.getOrGenerateXmlFilename(invoice));
            
            return xmlFile;
            
        } catch (TransformerException e) {
            throw new BusinessException("Failed to create xml file for invoice id=" + invoice.getId() + " number=" + invoice.getInvoiceNumberOrTemporaryNumber(), e);
        }

    }

    /**
     * @param invoice invoice used to create xml
     * @param isVirtual true/false
     * @return xml document
     * @throws BusinessException business exception
     * @throws ParserConfigurationException parsing exception
     * @throws SAXException sax exception
     * @throws IOException IO exception
     */
    public Document createDocument(Invoice invoice, boolean isVirtual) throws BusinessException, ParserConfigurationException, SAXException, IOException {
        long startDate = System.currentTimeMillis();
    	Long id = invoice.getId();
        String alias = invoice.getAlias();
        String invoiceNumber = invoice.getInvoiceNumber();
        BillingAccount billingAccount = invoice.getBillingAccount();
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        Customer customer = customerAccount.getCustomer();
        String code = customerAccount.getCode();
        String customerCode = customer.getCode();
        InvoiceType invoiceType = invoice.getInvoiceType();
        String invoiceTypeCode = invoiceType.getCode();
        boolean isInvoiceAdjustment = invoiceTypeCode.equals(invoiceTypeService.getAdjustementCode());
        String billingAccountLanguage = billingAccount.getTradingLanguage().getLanguage().getLanguageCode();
        List<InvoiceAgregate> invoiceAgregates = invoice.getInvoiceAgregates();
        List<RatedTransaction> ratedTransactions = null;
        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = null;
        
        if (!isVirtual) {
        	ratedTransactions = ratedTransactionService.getRatedTransactionsForXmlInvoice(invoice);
            subCategoryInvoiceAgregates = invoiceService.listByInvoice(invoice);
        }

        // Session session = this.getEntityManager().unwrap(Session.class);
        // session.createCriteria(InvoiceAgregate.class).setFetchMode("InvoiceAgregates", FetchMode.EAGER).Add(Expression.("invoice", invoice)).List();

        boolean hasInvoiceAgregates = !invoiceAgregates.isEmpty();
        BillingRun billingRun = invoice.getBillingRun();

        log.debug("Creating xml for invoice id={} number={}. {}", id, invoiceNumber != null ? invoiceNumber : invoice.getTemporaryInvoiceNumber());

        String invoiceDateFormat = paramBean.getProperty("invoice.dateFormat", DEFAULT_DATE_PATTERN);
        serviceIds = new ArrayList<>();
        offerIds = new ArrayList<>();
        priceplanIds = new ArrayList<>();

        boolean entreprise = appProvider.isEntreprise();
        int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();

        if (!isInvoiceAdjustment && billingRun != null && BillingRunStatusEnum.VALIDATED.equals(billingRun.getStatus()) && invoiceNumber == null) {
            invoiceService.assignInvoiceNumber(invoice);
        }

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element invoiceTag = doc.createElement("invoice");
        Element header = doc.createElement("header");
        invoiceTag.setAttribute("number", invoiceNumber);
        invoiceTag.setAttribute("type", invoiceTypeCode);
        invoiceTag.setAttribute("invoiceCounter", alias);
        invoiceTag.setAttribute("id", id != null ? id.toString() : "");
        invoiceTag.setAttribute("customerId", customerCode + "");
        invoiceTag.setAttribute("customerAccountCode", code != null ? code : "");
        if (isInvoiceAdjustment) {
            Set<Invoice> linkedInvoices = invoice.getLinkedInvoices();
            invoiceTag.setAttribute("adjustedInvoiceNumber", getLinkedInvoicesnumberAsString(new ArrayList<Invoice>(linkedInvoices)));
        }

        BillingCycle billingCycle = null;

        Invoice linkedInvoice = invoiceService.getLinkedInvoice(invoice);
        if (isInvoiceAdjustment && linkedInvoice != null && linkedInvoice.getBillingRun() != null) {
            billingCycle = linkedInvoice.getBillingRun().getBillingCycle();
        } else {
            if (billingRun != null && billingRun.getBillingCycle() != null) {
                billingCycle = billingRun.getBillingCycle();
            }
        }

        String billingTemplateName = billingCycleMap.get(billingCycle);
        if (billingTemplateName == null) {
            billingTemplateName = InvoiceService.getInvoiceTemplateName(billingCycle, invoiceType);
            billingCycleMap.put(billingCycle, billingTemplateName);
        }

        invoiceTag.setAttribute("templateName", billingTemplateName);
        doc.appendChild(invoiceTag);
        invoiceTag.appendChild(header);
        // log.debug("creating provider");
        InvoiceConfiguration invoiceConfiguration = appProvider.getInvoiceConfiguration();
        if (invoiceConfiguration != null && invoiceConfiguration.getDisplayProvider() != null && invoiceConfiguration.getDisplayProvider()) {
            Element providerTag = doc.createElement("provider");
            providerTag.setAttribute("code", appProvider.getCode() + "");
            BankCoordinates appBankCoordinates = appProvider.getBankCoordinates();
            if (appBankCoordinates != null) {
                Element bankCoordinates = doc.createElement("bankCoordinates");
                Element ics = doc.createElement("ics");
                Element iban = doc.createElement("iban");
                Element bic = doc.createElement("bic");
                String bankIcs = appBankCoordinates.getIcs();
                Text icsTxt = doc.createTextNode(bankIcs != null ? bankIcs : "");
                ics.appendChild(icsTxt);
                String bankIban = appBankCoordinates.getIban();
                Text ibanTxt = doc.createTextNode(bankIban != null ? bankIban : "");
                iban.appendChild(ibanTxt);
                String bankBic = appBankCoordinates.getBic();
                Text bicTxt = doc.createTextNode(bankBic != null ? bankBic : "");
                bic.appendChild(bicTxt);
                bankCoordinates.appendChild(ics);
                bankCoordinates.appendChild(iban);
                bankCoordinates.appendChild(bic);
                providerTag.appendChild(bankCoordinates);
            }
            header.appendChild(providerTag);
        }

        String externalRef1 = customer.getExternalRef1();
        String externalRef2 = customer.getExternalRef2();
        CustomerBrand customerBrand = customer.getCustomerBrand();
        Seller seller = customer.getSeller();
        CustomerCategory customerCategory = customer.getCustomerCategory();
        Element customerTag = doc.createElement("customer");
        customerTag.setAttribute("id", customer.getId() + "");
        customerTag.setAttribute("code", customerCode + "");
        customerTag.setAttribute("externalRef1", externalRef1 != null ? externalRef1 : "");
        customerTag.setAttribute("externalRef2", externalRef2 != null ? externalRef2 : "");
        customerTag.setAttribute("sellerCode", seller.getCode() != null ? seller.getCode() : "");
        customerTag.setAttribute("brand", customerBrand != null ? customerBrand.getCode() : "");
        customerTag.setAttribute("category", customerCategory != null ? customerCategory.getCode() : "");

        PaymentMethod preferedPaymentMethod = customerAccount.getPreferredPaymentMethod();

        String mandateIdentification = null;
        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            mandateIdentification = ((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification();
            customerTag.setAttribute("mandateIdentification", mandateIdentification != null ? mandateIdentification : "");
        }

        addCustomFields(customer, doc, customerTag);

        addNameAndAdress(customer, doc, customerTag, billingAccountLanguage);

        // log.debug("creating ca");
        // CustomerAccount customerAccount = customerAccount;
        TradingCurrency tradingCurrency = customerAccount.getTradingCurrency();
        String currencyCode = tradingCurrency.getCurrencyCode();
        String externalRef12 = customerAccount.getExternalRef1();
        String externalRef22 = customerAccount.getExternalRef2();
        TradingLanguage tradingLanguage = customerAccount.getTradingLanguage();
        String prDescription = null;
        if (tradingLanguage != null) {
            prDescription = tradingLanguage.getPrDescription();
        }
        Element customerAccountTag = doc.createElement("customerAccount");
        customerAccountTag.setAttribute("id", customerAccount.getId() + "");
        customerAccountTag.setAttribute("code", customerAccount.getCode() + "");
        customerAccountTag.setAttribute("description", customerAccount.getDescription() + "");
        customerAccountTag.setAttribute("externalRef1", externalRef12 != null ? externalRef12 : "");
        customerAccountTag.setAttribute("externalRef2", externalRef22 != null ? externalRef22 : "");
        customerAccountTag.setAttribute("currency", currencyCode != null ? currencyCode : "");
        customerAccountTag.setAttribute("language", prDescription != null ? prDescription : "");

        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            customerAccountTag.setAttribute("mandateIdentification", mandateIdentification != null ? mandateIdentification : "");
        }
        addCustomFields(customerAccount, doc, customerAccountTag);
        header.appendChild(customerAccountTag);

        /*
         * EntityManager em = getEntityManager(); Query billingQuery = em .createQuery(
         * "select si from ServiceInstance si join si.subscription s join s.userAccount ua join ua.billingAccount ba join ba.customerAccount ca where ca.id = :customerAccountId" );
         * billingQuery.setParameter("customerAccountId", customerAccount.getId()); List<ServiceInstance> services = (List<ServiceInstance>) billingQuery .getResultList();
         * 
         * 
         * 
         * boolean terminated = services.size() > 0 ? isAllServiceInstancesTerminated(services) : false;
         */

        customerAccountTag.setAttribute("accountTerminated", customerAccount.getStatus().equals(CustomerAccountStatusEnum.CLOSE) + "");

        addPaymentInfo(customerAccount, doc, customerAccountTag);
        header.appendChild(customerAccountTag);

        addNameAndAdress(customerAccount, doc, customerAccountTag, billingAccountLanguage);
        addproviderContact(customerAccount, doc, customerAccountTag);

        String billingExternalRef2 = billingAccount.getExternalRef2();
        String billingExternalRef1 = billingAccount.getExternalRef1();
        Element billingAccountTag = doc.createElement("billingAccount");
        if (billingCycle == null) {
            billingCycle = billingAccount.getBillingCycle();
        }
        String billingCycleCode = billingCycle != null ? billingCycle.getCode() + "" : "";
        billingAccountTag.setAttribute("billingCycleCode", billingCycleCode);
        String billingAccountId = billingAccount.getId() + "";
        String billingAccountCode = billingAccount.getCode() + "";
        billingAccountTag.setAttribute("id", billingAccountId);
        billingAccountTag.setAttribute("code", billingAccountCode);
        billingAccountTag.setAttribute("description", billingAccount.getDescription() + "");
        billingAccountTag.setAttribute("externalRef1", billingExternalRef1 != null ? billingExternalRef1 : "");
        billingAccountTag.setAttribute("externalRef2", billingExternalRef2 != null ? billingExternalRef2 : "");

        if (invoiceConfiguration != null && invoiceConfiguration.getDisplayBillingCycle() != null && invoiceConfiguration.getDisplayBillingCycle()) {
            if (billingCycle == null) {
                billingCycle = billingAccount.getBillingCycle();
            }
            addBillingCyle(billingCycle, invoice, doc, billingAccountTag);
        }

        addCustomFields(billingAccount, doc, billingAccountTag);
        /*
         * if (billingAccount.getName() != null && billingAccount.getName().getTitle() != null) { // Element company = doc.createElement("company"); Text companyTxt =
         * doc.createTextNode (billingAccount.getName().getTitle().getIsCompany() + ""); billingAccountTag.appendChild(companyTxt); }
         */

        Element email = doc.createElement("email");
        String billingEmail = billingAccount.getEmail();
        Text emailTxt = doc.createTextNode(billingEmail != null ? billingEmail : "");
        email.appendChild(emailTxt);
        billingAccountTag.appendChild(email);

        addNameAndAdress(billingAccount, doc, billingAccountTag, billingAccountLanguage);

        header.appendChild(billingAccountTag);

        Date invoiceDateData = invoice.getInvoiceDate();
        if (invoiceDateData != null) {
            Element invoiceDate = doc.createElement("invoiceDate");
            Text invoiceDateTxt = doc.createTextNode(DateUtils.formatDateWithPattern(invoiceDateData, invoiceDateFormat));
            invoiceDate.appendChild(invoiceDateTxt);
            header.appendChild(invoiceDate);
        }

        Date dueDateData = invoice.getDueDate();
        if (dueDateData != null) {
            Element dueDate = doc.createElement("dueDate");
            Text dueDateTxt = doc.createTextNode(DateUtils.formatDateWithPattern(dueDateData, invoiceDateFormat));
            dueDate.appendChild(dueDateTxt);
            header.appendChild(dueDate);
        }

        PaymentMethodEnum paymentMethodData = invoice.getPaymentMethod();
        if (paymentMethodData != null) {
            Element paymentMethod = doc.createElement("paymentMethod");
            paymentMethod.appendChild(doc.createTextNode(paymentMethodData.name()));
            header.appendChild(paymentMethod);
        }

        // TradingCurrency tradingCurrency = customerAccount.getTradingCurrency();
        BigDecimal amountWithoutTax2 = invoice.getAmountWithoutTax();
        Element comment = doc.createElement("comment");
        String iComment = invoice.getComment();
        Comment commentText = doc.createComment(iComment != null ? iComment : "");
        comment.appendChild(commentText);
        header.appendChild(comment);

        addHeaderCategories(invoiceAgregates, doc, header, subCategoryInvoiceAgregates);

        addDiscounts(invoice, doc, header, isVirtual);
        
        Element amount = doc.createElement("amount");
        invoiceTag.appendChild(amount);
        Element currency = doc.createElement("currency");
        Text currencyTxt = doc.createTextNode(currencyCode);
        currency.appendChild(currencyTxt);
        amount.appendChild(currency);

        Element amountWithoutTax = doc.createElement("amountWithoutTax");

        Text amountWithoutTaxTxt = doc.createTextNode(round(amountWithoutTax2, rounding));
        amountWithoutTax.appendChild(amountWithoutTaxTxt);
        amount.appendChild(amountWithoutTax);

        Element amountWithTax = doc.createElement("amountWithTax");
        BigDecimal iAmountWithTax = invoice.getAmountWithTax();
        Text amountWithTaxTxt = doc.createTextNode(round(iAmountWithTax, rounding));
        amountWithTax.appendChild(amountWithTaxTxt);
        amount.appendChild(amountWithTax);

        BigDecimal netToPay = BigDecimal.ZERO;
        if (entreprise) {
            netToPay = iAmountWithTax;
        } else {
            netToPay = invoice.getNetToPay();
        }

        /*
         * Element balanceElement = doc.createElement("balance"); Text balanceTxt = doc.createTextNode(round(balance)); balanceElement.appendChild(balanceTxt);
         * amount.appendChild(balanceElement);
         */

        Element netToPayElement = doc.createElement("netToPay");
        Text netToPayTxt = doc.createTextNode(round(netToPay, rounding));
        netToPayElement.appendChild(netToPayTxt);
        amount.appendChild(netToPayElement);

        addTaxes(billingAccount, invoice.getAmountTax(), invoiceAgregates, doc, amount, hasInvoiceAgregates);

        Element detail = null;
        boolean displayDetail = false;
        if (invoiceConfiguration != null && invoiceConfiguration.getDisplayDetail() != null && invoiceConfiguration.getDisplayDetail() && invoice.isDetailedInvoice()) {
            displayDetail = true;

            detail = doc.createElement("detail");
            invoiceTag.appendChild(detail);
        }

		addUserAccounts(invoice, doc, detail, entreprise, invoiceTag, displayDetail, isVirtual, invoiceAgregates,
				hasInvoiceAgregates, ratedTransactions, subCategoryInvoiceAgregates);
        addCustomFields(invoice, doc, invoiceTag);

        if (invoiceConfiguration != null && invoiceConfiguration.getDisplayOrders() != null && invoiceConfiguration.getDisplayOrders()) {
            Element ordersTag = doc.createElement("orders");
            List<Order> orders = invoice.getOrders();
            for (Order order : orders) {
                Element orderTag = doc.createElement("order");
                orderTag.setAttribute("orderNumber", order.getCode());
                orderTag.setAttribute("externalId", order.getExternalId());
                orderTag.setAttribute("orderDate", DateUtils.formatDateWithPattern(order.getOrderDate(), DEFAULT_DATE_TIME_PATTERN));
                orderTag.setAttribute("orderStatus", order.getStatus().name());
                orderTag.setAttribute("deliveryInstructions", order.getDeliveryInstructions());
                Element orderItemsTag = doc.createElement("orderItems");
                for (OrderItem orderItem : order.getOrderItems()) {
                    String orderItemContent = orderItem.getSource().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
                    Element orderItemElement = docBuilder.parse(new ByteArrayInputStream(orderItemContent.getBytes())).getDocumentElement();
                    Element firstDocImportedNode = (Element) doc.importNode(orderItemElement, true);
                    orderItemsTag.appendChild(firstDocImportedNode);
                }
                orderTag.appendChild(orderItemsTag);
                addCustomFields(order, doc, orderTag);
                ordersTag.appendChild(orderTag);
            }
            invoiceTag.appendChild(ordersTag);
        }

        log.debug("Before  doc:" + (System.currentTimeMillis() - startDate));

        return doc;

    }

    /**
     * @param invoice invoice used to build xml
     * @param doc DOM xml
     * @param parent parent node
     * @param enterprise true/false
     * @param invoiceTag DOM invoice tag
     * @param displayDetail true/false
     * @param isVirtual true/false
     * @param invoiceAgregates list of invoice agregate
     * @param hasInvoiceAggre true/false
     * @param ratedTransactions list of rated transaction
     * @param subCategoryInvoiceAgregates list of sub category invoice
     * @throws BusinessException business exception
     */
	public void addUserAccounts(Invoice invoice, Document doc, Element parent, boolean enterprise, Element invoiceTag,
			boolean displayDetail, boolean isVirtual, List<InvoiceAgregate> invoiceAgregates, boolean hasInvoiceAggre,
			List<RatedTransaction> ratedTransactions, List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates)
			throws BusinessException {
        // log.debug("add user account");
		long startDate = System.currentTimeMillis();
        Element userAccountsTag = null;
        if (displayDetail) {
            userAccountsTag = doc.createElement("userAccounts");
            parent.appendChild(userAccountsTag);
        }
        BillingAccount billingAccount = invoice.getBillingAccount();
        TradingLanguage tradingLanguage = billingAccount.getTradingLanguage();
        Language language = tradingLanguage.getLanguage();
        String billingAccountLanguage = language.getLanguageCode();
        List<UserAccount> usersAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : usersAccounts) {
            List<Subscription> subscriptions = userAccount.getSubscriptions();
            Element userAccountTag = doc.createElement("userAccount");
            userAccountTag.setAttribute("id", userAccount.getId() + "");
            String code = userAccount.getCode();
            userAccountTag.setAttribute("code", code != null ? code : "");
            String description = userAccount.getDescription();
            userAccountTag.setAttribute("description", description != null ? description : "");
            addCustomFields(userAccount, doc, userAccountTag);
            List<ServiceInstance> allServiceInstances = new ArrayList<ServiceInstance>();
            if(!isVirtual) { // if it is not virtual (not quote) add all subscriptions to XML (DO NOT KNOW if required or NO)
            	allServiceInstances = addSubscriptions(userAccount, doc, userAccountTag, invoiceTag, subscriptions);
            }
            if (displayDetail) {
                userAccountsTag.appendChild(userAccountTag);
                addNameAndAdress(userAccount, doc, userAccountTag, billingAccountLanguage);
                addCategories(userAccount, invoice, doc, invoiceTag, userAccountTag, appProvider.getInvoiceConfiguration().getDisplayDetail(), enterprise, isVirtual,
                    invoiceAgregates, hasInvoiceAggre, allServiceInstances, ratedTransactions, subCategoryInvoiceAgregates);
            }

        }
    }

    /**
     * @param userAccount user account
     * @param doc DOM document
     * @param userAccountTag DOM tag for userAccount
     * @param invoiceTag DOM invoice tag
     * @param subscriptions list of subscription
     * @return list of service instance
     */
    private List<ServiceInstance> addSubscriptions(UserAccount userAccount, Document doc, Element userAccountTag, Element invoiceTag, List<Subscription> subscriptions) {
        long startDate = System.currentTimeMillis();
    	List<ServiceInstance> allServiceInstances = new ArrayList<>();
        // List<Subscription> subscriptions = userAccountService.listByUserAccount(userAccount);//userAccount.getSubscriptions();//
        if (subscriptions != null && subscriptions.size() > 0) {
            log.info(" :" + (System.currentTimeMillis() - startDate));
            String invoiceDateFormat = paramBean.getProperty("invoice.dateFormat", DEFAULT_DATE_PATTERN);
            String invoiceDateTimeFormat = paramBean.getProperty("invoice.dateTimeFormat", DEFAULT_DATE_TIME_PATTERN);

            Element subscriptionsTag = null;

            InvoiceConfiguration invoiceConfiguration = appProvider.getInvoiceConfiguration();
            boolean displaySubscription = invoiceConfiguration != null && invoiceConfiguration.getDisplaySubscriptions() != null && invoiceConfiguration.getDisplaySubscriptions();
            if (displaySubscription) {
                subscriptionsTag = doc.createElement("subscriptions");
                userAccountTag.appendChild(subscriptionsTag);
            }
            for (Subscription subscription : subscriptions) {
                OfferTemplate offer = subscription.getOffer();
                if (displaySubscription) {
                    Element subscriptionTag = doc.createElement("subscription");
                    subscriptionTag.setAttribute("id", subscription.getId() + "");
                    subscriptionTag.setAttribute("code", subscription.getCode() != null ? subscription.getCode() : "");
                    subscriptionTag.setAttribute("description", subscription.getDescription() != null ? subscription.getDescription() : "");
                    subscriptionTag.setAttribute("offerCode", offer != null ? offer.getCode() : "");
                    Element subscriptionDateTag = doc.createElement("subscriptionDate");
                    Text subscriptionDateText = doc.createTextNode(DateUtils.formatDateWithPattern(subscription.getSubscriptionDate(), invoiceDateFormat));
                    subscriptionDateTag.appendChild(subscriptionDateText);
                    subscriptionTag.appendChild(subscriptionDateTag);
                    Element endAgreementTag = doc.createElement("endAgreementDate");
                    Text endAgreementText = doc.createTextNode(DateUtils.formatDateWithPattern(subscription.getEndAgreementDate(), invoiceDateTimeFormat));
                    endAgreementTag.appendChild(endAgreementText);
                    subscriptionTag.appendChild(endAgreementTag);
                    addCustomFields(subscription, doc, subscriptionTag);
                    subscriptionsTag.appendChild(subscriptionTag);
                }
                if (offer != null) {

                    OfferTemplate offerTemplate = offer;
                    if (invoiceConfiguration != null && invoiceConfiguration.getDisplayOffers() != null && invoiceConfiguration.getDisplayOffers()
                            && !offerIds.contains(offerTemplate.getId())) {
                        addOffers(offerTemplate, doc, invoiceTag);
                        offerIds.add(offerTemplate.getId());
                    }
                    if (invoiceConfiguration != null && invoiceConfiguration.getDisplayServices() != null && invoiceConfiguration.getDisplayServices()) {
                        List<ServiceInstance> addServices = addServices(subscription, doc, invoiceTag);
                        for (ServiceInstance serviceInstance : addServices) {
                            if (!allServiceInstances.contains(serviceInstance)) {
                                allServiceInstances.add(serviceInstance);
                            }
                        }
                    }
                }
            }
        }

        return allServiceInstances;
    }

    /**
     * @param offerTemplate offer template
     * @param doc DOM document
     * @param invoiceTag invoice tage
     */
    private void addOffers(OfferTemplate offerTemplate, Document doc, Element invoiceTag) {
        Element offersTag = getCollectionTag(doc, invoiceTag, "offers");

        String id = offerTemplate.getId() + "";
        Element offerTag = null;
        offerTag = doc.createElement("offer");
        offerTag.setAttribute("id", id);
        offerTag.setAttribute("code", offerTemplate.getCode() != null ? offerTemplate.getCode() : "");
        offerTag.setAttribute("description", offerTemplate.getDescription() != null ? offerTemplate.getDescription() : "");
        addCustomFields(offerTemplate, doc, offerTag);
        offersTag.appendChild(offerTag);
    }

    /**
     * @param billingCycle instance of billing cycle
     * @param invoice instance of invoice
     * @param doc document
     * @param parent parent node
     */
    private void addBillingCyle(BillingCycle billingCycle, Invoice invoice, Document doc, Element parent) {
        String id = billingCycle.getId() + "";
        Element billingCycleTag = doc.createElement("billingCycle");
        parent.appendChild(billingCycleTag);
        billingCycleTag.setAttribute("id", id);
        billingCycleTag.setAttribute("code", billingCycle.getCode() != null ? billingCycle.getCode() : "");
        billingCycleTag.setAttribute("description", billingCycle.getDescription() != null ? billingCycle.getDescription() : "");
        addCustomFields(billingCycle, doc, billingCycleTag);
    }

    /**
     * @param subscription instance of subscription
     * @param doc document
     * @param invoiceTag invoice tag in DOM document
     * @return list of service instance
     */
    private List<ServiceInstance> addServices(Subscription subscription, Document doc, Element invoiceTag) {
        OfferTemplate offerTemplate = subscription.getOffer();
        String code = offerTemplate.getCode();
        List<ServiceInstance> serviceInstances = subscriptionService.listBySubscription(subscription);// subscription.getServiceInstances();
        if (serviceInstances != null && serviceInstances.size() > 0) {
            Element servicesTag = getCollectionTag(doc, invoiceTag, "services");

            for (ServiceInstance serviceInstance : serviceInstances) {
                ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();
                if (!serviceIds.contains(serviceTemplate.getId())) {
                    addService(serviceInstance, doc, code, servicesTag);
                    serviceIds.add(serviceTemplate.getId());
                }
            }
        }

        return serviceInstances;

    }

    /**
     * @param serviceInstance instance of service
     * @param doc document
     * @param offerCode code of offer
     * @param parentElement parent node of current building tag
     */
    private void addService(ServiceInstance serviceInstance, Document doc, String offerCode, Element parentElement) {
        ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();
        Element serviceTag = doc.createElement("service");
        String code = serviceTemplate.getCode();
        serviceTag.setAttribute("code", code != null ? code : "");
        serviceTag.setAttribute("offerCode", offerCode != null ? offerCode : "");
        serviceTag.setAttribute("description", serviceTemplate.getDescription() != null ? serviceTemplate.getDescription() : "");

        Element calendarTag = doc.createElement("calendar");
        Text calendarText = null;
        if (serviceTemplate.getInvoicingCalendar() != null) {
            calendarText = doc.createTextNode(serviceTemplate.getInvoicingCalendar().getCode());
        } else {
            calendarText = doc.createTextNode("");
        }
        calendarTag.appendChild(calendarText);
        addCustomFields(serviceInstance, doc, serviceTag, true);
        parentElement.appendChild(serviceTag);
    }

    /**
     * @param pricePlan instance of price plan
     * @param doc document
     * @param invoiceTag invoice tag
     */
    private void addPricePlans(PricePlanMatrix pricePlan, Document doc, Element invoiceTag) {
        Element pricePlansTag = getCollectionTag(doc, invoiceTag, "priceplans");
        Element pricePlanTag = null;
        pricePlanTag = doc.createElement("priceplan");
        String code = pricePlan.getCode();
        String description = pricePlan.getDescription();
        pricePlanTag.setAttribute("code", code != null ? code : "");
        pricePlanTag.setAttribute("description", description != null ? description : "");
        addCustomFields(pricePlan, doc, pricePlanTag);
        pricePlansTag.appendChild(pricePlanTag);

    }

    /**
     * @param doc document
     * @param parent parent node
     * @param tagName name o tag
     * @return collection tag
     */
    private Element getCollectionTag(Document doc, Element parent, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        Element collectionTag = null;
        if (nodeList != null && nodeList.getLength() > 0) {
            collectionTag = (Element) nodeList.item(0);
        } else {
            collectionTag = doc.createElement(tagName);
            parent.appendChild(collectionTag);
        }
        return collectionTag;
    }

    /**
     * @param entity given entity to find CFs
     * @param doc root DOM document
     * @param parent parent node
     */
    private void addCustomFields(ICustomFieldEntity entity, Document doc, Element parent) {
        addCustomFields(entity, doc, parent, false);
    }

    /**
     * @param entity given entity to find CFs
     * @param doc root DOM document
     * @param parent parent node
     * @param includeParentCFEntities true/false which defines the need for parent CFs
     */
    private void addCustomFields(ICustomFieldEntity entity, Document doc, Element parent, boolean includeParentCFEntities) {
    	InvoiceConfiguration invoiceConfiguration = appProvider.getInvoiceConfiguration();
    	if (invoiceConfiguration != null && invoiceConfiguration.getDisplayCfAsXML() != null && invoiceConfiguration.getDisplayCfAsXML()) {
    		Element customFieldsTag = customFieldInstanceService.getCFValuesAsDomElement(entity, doc, includeParentCFEntities);
    		if (customFieldsTag.hasChildNodes()) {
    			parent.appendChild(customFieldsTag);
    		}
    	} else {
    		String json = customFieldInstanceService.getCFValuesAsJson(entity, includeParentCFEntities);
    		if (json != null && json.length() > 0) {
    			parent.setAttribute("customFields", json);
    		}
    	}
    }

    /**
     * @param account instance of entity
     * @param doc document
     * @param parent parent node
     * @param languageCode code of language
     */
    public void addNameAndAdress(AccountEntity account, Document doc, Element parent, String languageCode) {
        log.debug("add name and address");

        if (!(account instanceof Customer)) {
            Element nameTag = doc.createElement("name");
            parent.appendChild(nameTag);

            Element quality = doc.createElement("quality");

            if (account.getName() != null && account.getName().getTitle() != null) {

                String translationKey = "T_" + account.getName().getTitle().getCode() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationKey);
                if (descTranslated == null) {
                    descTranslated = account.getName().getTitle().getDescriptionOrCode();
                    if (account.getName().getTitle().getDescriptionI18n() != null && account.getName().getTitle().getDescriptionI18n().get(languageCode) != null) {
                        descTranslated = account.getName().getTitle().getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationKey, descTranslated);
                }

                Text titleTxt = doc.createTextNode(descTranslated);
                quality.appendChild(titleTxt);
            }
            nameTag.appendChild(quality);
            if (account.getName() != null && account.getName().getFirstName() != null) {
                Element firstName = doc.createElement("firstName");
                Text firstNameTxt = doc.createTextNode(account.getName().getFirstName());
                firstName.appendChild(firstNameTxt);
                nameTag.appendChild(firstName);
            }

            Element name = doc.createElement("name");
            if (account.getName() != null && account.getName().getLastName() != null) {
                Text nameTxt = doc.createTextNode(account.getName().getLastName());
                name.appendChild(nameTxt);
            }
            nameTag.appendChild(name);
        }
        Element addressTag = doc.createElement("address");
        Element address1 = doc.createElement("address1");
        if (account.getAddress() != null && account.getAddress().getAddress1() != null) {
            Text adress1Txt = doc.createTextNode(account.getAddress().getAddress1());
            address1.appendChild(adress1Txt);
        }
        addressTag.appendChild(address1);

        Element address2 = doc.createElement("address2");
        if (account.getAddress() != null && account.getAddress().getAddress2() != null) {
            Text adress2Txt = doc.createTextNode(account.getAddress().getAddress2());
            address2.appendChild(adress2Txt);
        }
        addressTag.appendChild(address2);

        Element address3 = doc.createElement("address3");
        if (account.getAddress() != null && account.getAddress().getAddress3() != null) {
            Text adress3Txt = doc.createTextNode(account.getAddress().getAddress3() != null ? account.getAddress().getAddress3() : "");
            address3.appendChild(adress3Txt);
        }
        addressTag.appendChild(address3);

        Element city = doc.createElement("city");
        if (account.getAddress() != null && account.getAddress().getCity() != null) {
            Text cityTxt = doc.createTextNode(account.getAddress().getCity() != null ? account.getAddress().getCity() : "");
            city.appendChild(cityTxt);
        }
        addressTag.appendChild(city);

        Element postalCode = doc.createElement("postalCode");
        if (account.getAddress() != null && account.getAddress().getZipCode() != null) {
            Text postalCodeTxt = doc.createTextNode(account.getAddress().getZipCode() != null ? account.getAddress().getZipCode() : "");
            postalCode.appendChild(postalCodeTxt);
        }
        addressTag.appendChild(postalCode);

        Element state = doc.createElement("state");
        addressTag.appendChild(state);

        Element country = doc.createElement("country");
        Element countryName = doc.createElement("countryName");
        if (account.getAddress() != null && account.getAddress().getCountry() != null) {
            Text countryTxt = doc.createTextNode(account.getAddress().getCountry() != null ? account.getAddress().getCountry() : "");
            country.appendChild(countryTxt);

            Country countrybyCode = countryService.findByCode(account.getAddress().getCountry());
            Text countryNameTxt;
            if (countrybyCode != null && countrybyCode.getDescriptionI18n() != null && countrybyCode.getDescriptionI18n().get(languageCode) != null ) {
                // get country description by language code
                countryNameTxt = doc.createTextNode(countrybyCode.getDescriptionI18n().get(languageCode));
            } else if (countrybyCode != null) {
                countryNameTxt = doc.createTextNode(countrybyCode.getDescription());
            } else {
                countryNameTxt = doc.createTextNode("");
            }
            countryName.appendChild(countryNameTxt);
        }
        addressTag.appendChild(country);
        addressTag.appendChild(countryName);
        parent.appendChild(addressTag);
    }

    /**
     * Adds provider contact to DOM node.
     * @param account entity
     * @param doc document
     * @param parent parent node
     */
    public void addproviderContact(AccountEntity account, Document doc, Element parent) {

        // log.debug("add provider");

        if (account.getPrimaryContact() != null) {
            Element providerContactTag = doc.createElement("providerContact");
            parent.appendChild(providerContactTag);
            if (account.getPrimaryContact().getFirstName() != null) {
                Element firstName = doc.createElement("firstName");
                Text firstNameTxt = doc.createTextNode(account.getPrimaryContact().getFirstName());
                firstName.appendChild(firstNameTxt);
                providerContactTag.appendChild(firstName);
            }

            if (account.getPrimaryContact().getLastName() != null) {
                Element name = doc.createElement("lastname");
                Text nameTxt = doc.createTextNode(account.getPrimaryContact().getLastName());
                name.appendChild(nameTxt);
                providerContactTag.appendChild(name);
            }

            if (account.getPrimaryContact().getEmail() != null) {
                Element email = doc.createElement("email");
                Text emailTxt = doc.createTextNode(account.getPrimaryContact().getEmail());
                email.appendChild(emailTxt);
                providerContactTag.appendChild(email);
            }
            if (account.getPrimaryContact().getFax() != null) {
                Element fax = doc.createElement("fax");
                Text faxTxt = doc.createTextNode(account.getPrimaryContact().getFax());
                fax.appendChild(faxTxt);
                providerContactTag.appendChild(fax);

            }
            if (account.getPrimaryContact().getMobile() != null) {

                Element mobile = doc.createElement("mobile");
                Text mobileTxt = doc.createTextNode(account.getPrimaryContact().getMobile());
                mobile.appendChild(mobileTxt);
                providerContactTag.appendChild(mobile);
            }
            if (account.getPrimaryContact().getPhone() != null) {
                Element phone = doc.createElement("phone");
                Text phoneTxt = doc.createTextNode(account.getPrimaryContact().getPhone());
                phone.appendChild(phoneTxt);
                providerContactTag.appendChild(phone);
            }
        }

    }

    /**
     * @param customerAccount customer account
     * @param doc document
     * @param parent parent node
     */
    public void addPaymentInfo(CustomerAccount customerAccount, Document doc, Element parent) {

        Element paymentMethod = doc.createElement("paymentMethod");
        parent.appendChild(paymentMethod);

        PaymentMethod preferedPaymentMethod = customerAccount.getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            paymentMethod.setAttribute("type", preferedPaymentMethod.getPaymentType().name());
        }

        if (paymentMethod instanceof DDPaymentMethod || paymentMethod instanceof TipPaymentMethod) {
            BankCoordinates bankCoordinates = null;
            if (paymentMethod instanceof DDPaymentMethod) {
                bankCoordinates = ((DDPaymentMethod) paymentMethod).getBankCoordinates();
            } else if (paymentMethod instanceof TipPaymentMethod) {
                bankCoordinates = ((TipPaymentMethod) paymentMethod).getBankCoordinates();
            }

            if (bankCoordinates != null) {

                Element bankCoordinatesElement = doc.createElement("bankCoordinates");
                Element bankCode = doc.createElement("bankCode");
                Element branchCode = doc.createElement("branchCode");
                Element accountNumber = doc.createElement("accountNumber");
                Element accountOwner = doc.createElement("accountOwner");
                Element key = doc.createElement("key");
                Element iban = doc.createElement("IBAN");
                bankCoordinatesElement.appendChild(bankCode);
                bankCoordinatesElement.appendChild(branchCode);
                bankCoordinatesElement.appendChild(accountNumber);
                bankCoordinatesElement.appendChild(accountOwner);
                bankCoordinatesElement.appendChild(key);
                bankCoordinatesElement.appendChild(iban);
                paymentMethod.appendChild(bankCoordinatesElement);

                String bankCodeData = bankCoordinates.getBankCode();
                Text bankCodeTxt = doc.createTextNode(bankCodeData != null ? bankCodeData : "");
                bankCode.appendChild(bankCodeTxt);

                String branchCodeData = bankCoordinates.getBranchCode();
                Text branchCodeTxt = doc.createTextNode(branchCodeData != null ? branchCodeData : "");
                branchCode.appendChild(branchCodeTxt);

                String accountNumberData = bankCoordinates.getAccountNumber();
                Text accountNumberTxt = doc.createTextNode(accountNumberData != null ? accountNumberData : "");
                accountNumber.appendChild(accountNumberTxt);

                String accountOwnerData = bankCoordinates.getAccountOwner();
                Text accountOwnerTxt = doc.createTextNode(accountOwnerData != null ? accountOwnerData : "");
                accountOwner.appendChild(accountOwnerTxt);

                Text keyTxt = doc.createTextNode(bankCoordinates.getKey() != null ? bankCoordinates.getKey() : "");
                key.appendChild(keyTxt);

                String ibanData = bankCoordinates.getIban();
                Text ibanTxt = doc.createTextNode(ibanData != null ? ibanData : "");
                iban.appendChild(ibanTxt);
            }

        } else if (paymentMethod instanceof CardPaymentMethod) {

            Element cardInformationElement = doc.createElement("cardInformation");
            Element cardType = doc.createElement("cardType");
            Element owner = doc.createElement("owner");
            Element cardNumber = doc.createElement("cardNumber");
            Element expiration = doc.createElement("expiration");
            cardInformationElement.appendChild(cardType);
            cardInformationElement.appendChild(owner);
            cardInformationElement.appendChild(cardNumber);
            cardInformationElement.appendChild(expiration);
            paymentMethod.appendChild(cardInformationElement);

            Text cardTypeTxt = doc.createTextNode(((CardPaymentMethod) paymentMethod).getCardType().name());
            cardType.appendChild(cardTypeTxt);

            Text ownerTxt = doc.createTextNode(((CardPaymentMethod) paymentMethod).getOwner());
            owner.appendChild(ownerTxt);

            Text cardNumberTxt = doc.createTextNode(((CardPaymentMethod) paymentMethod).getHiddenCardNumber());
            cardNumber.appendChild(cardNumberTxt);

            Text expirationTxt = doc.createTextNode(((CardPaymentMethod) paymentMethod).getExpirationMonthAndYear());
            expiration.appendChild(expirationTxt);
        }
    }

    /**
     * @param userAccount user account
     * @param invoice invoice which create need to xml
     * @param doc Document
     * @param invoiceTag invoice tag
     * @param parent parent node
     * @param generateSubCat true/false
     * @param enterprise true/false
     * @param isVirtual true/false
     * @param invoiceAgregates agregates of invoice
     * @param hasInvoiceAgregates true/false
     * @param allServiceInstances list of service instances
     * @param ratedTransactions rated transactions
     * @param subCategoryInvoiceAgregates list of sub category invoice agregates
     * @throws BusinessException business exception
     */
    public void addCategories(UserAccount userAccount, Invoice invoice, Document doc, Element invoiceTag, Element parent, boolean generateSubCat, boolean enterprise,
            boolean isVirtual, List<InvoiceAgregate> invoiceAgregates, boolean hasInvoiceAgregates, List<ServiceInstance> allServiceInstances,
            List<RatedTransaction> ratedTransactions, List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) throws BusinessException {
        long startDate = System.currentTimeMillis();

        String invoiceDateFormat = paramBean.getProperty("invoice.dateFormat", DEFAULT_DATE_PATTERN);
        String invoiceDateTimeFormat = paramBean.getProperty("invoice.dateTimeFormat", DEFAULT_DATE_TIME_PATTERN);

        String languageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();

        Element categories = doc.createElement("categories");
        parent.appendChild(categories);
        boolean entreprise = appProvider.isEntreprise();
        int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();

        List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();

        Long id = userAccount.getId();

        if (hasInvoiceAgregates) {
            for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {
                if (invoiceAgregate.getUserAccount().getId() == id) {
                    if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                        CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
                        categoryInvoiceAgregates.add(categoryInvoiceAgregate);
                    }
                }
            }
        }

        Collections.sort(categoryInvoiceAgregates, new Comparator<CategoryInvoiceAgregate>() {
            public int compare(CategoryInvoiceAgregate c0, CategoryInvoiceAgregate c1) {
                if (c0.getInvoiceCategory() != null && c1.getInvoiceCategory() != null && c0.getInvoiceCategory().getSortIndex() != null
                        && c1.getInvoiceCategory().getSortIndex() != null) {
                    return c0.getInvoiceCategory().getSortIndex().compareTo(c1.getInvoiceCategory().getSortIndex());
                }
                return 0;
            }
        });

        for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
            InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();
            String invoiceCategoryLabel = categoryInvoiceAgregate.getDescription();
            Element category = doc.createElement("category");
            category.setAttribute("label", (invoiceCategoryLabel != null) ? invoiceCategoryLabel : "");
            category.setAttribute("code", invoiceCategory != null && invoiceCategory.getCode() != null ? invoiceCategory.getCode() : "");
            categories.appendChild(category);
            Element amountWithoutTax = doc.createElement("amountWithoutTax");
            Text amountWithoutTaxTxt = doc.createTextNode(round(categoryInvoiceAgregate.getAmountWithoutTax(), rounding));
            amountWithoutTax.appendChild(amountWithoutTaxTxt);
            category.appendChild(amountWithoutTax);
            addCustomFields(invoiceCategory, doc, category);

            // if (!entreprise) {
            // Element amountWithTax = doc.createElement("amountWithTax");
            // Text amountWithTaxTxt =
            // doc.createTextNode(round(categoryInvoiceAgregate.getAmountWithTax(),
            // rounding));
            // amountWithTax.appendChild(amountWithTaxTxt);
            // category.appendChild(amountWithTax);
            // }

            if (isVirtual) {
                Set<SubCategoryInvoiceAgregate> tmpSubCategoryInvoiceAgregates = categoryInvoiceAgregate.getSubCategoryInvoiceAgregates();
                subCategoryInvoiceAgregates = new ArrayList<>();
                for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : tmpSubCategoryInvoiceAgregates) {
                    subCategoryInvoiceAgregates.add(subCategoryInvoiceAgregate);
                }
            }

            if (generateSubCat) {
                Element subCategories = doc.createElement("subCategories");
                category.appendChild(subCategories);
                // List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates =
                // userAccountService.listByInvoice(invoice);//categoryInvoiceAgregate.getSubCategoryInvoiceAgregates();
                for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
                    CategoryInvoiceAgregate categoryInvoiceAgregate2 = subCatInvoiceAgregate.getCategoryInvoiceAgregate();
                    if (categoryInvoiceAgregate2 != null && categoryInvoiceAgregate != null && categoryInvoiceAgregate2.getId() != null && categoryInvoiceAgregate.getId() != null
                            && categoryInvoiceAgregate2.getId().longValue() != categoryInvoiceAgregate.getId().longValue()
                            || (categoryInvoiceAgregate2 == null || categoryInvoiceAgregate == null)) {
                        continue;
                    }

                    InvoiceSubCategory invoiceSubCat = subCatInvoiceAgregate.getInvoiceSubCategory();
                    // List<RatedTransaction> transactions = null;
                    WalletInstance wallet = subCatInvoiceAgregate.getWallet();

                    if (isVirtual) {
                        ratedTransactions = invoice.getRatedTransactionsForCategory(wallet, invoiceSubCat);
                    } /*
                       * else { transactions = ratedTransactionService.getRatedTransactionsForXmlInvoice( wallet, invoice, invoiceSubCat); }
                       */

                    String invoiceSubCategoryLabel = subCatInvoiceAgregate.getDescription();
                    Element subCategory = doc.createElement("subCategory");
                    subCategories.appendChild(subCategory);
                    subCategory.setAttribute("label", (invoiceSubCategoryLabel != null) ? invoiceSubCategoryLabel : "");
                    subCategory.setAttribute("code", invoiceSubCat.getCode());
                    subCategory.setAttribute("amountWithoutTax", round(subCatInvoiceAgregate.getAmountWithoutTax(), rounding));

                    if (!entreprise) {
                        subCategory.setAttribute("amountWithTax", round(subCatInvoiceAgregate.getAmountWithTax(), rounding));
                    }

                    String taxesCode = "";
                    String taxesPercent = "";
                    String sep = "";
                    for (Tax tax : subCatInvoiceAgregate.getSubCategoryTaxes()) {
                        taxesCode = taxesCode + sep + tax.getCode();
                        taxesPercent = taxesPercent + sep + round(tax.getPercent(), rounding);
                        sep = ";";
                    }
                    subCategory.setAttribute("taxCode", taxesCode);
                    subCategory.setAttribute("taxPercent", taxesPercent);

                    for (RatedTransaction ratedTransaction : ratedTransactions) {
                        if (!(ratedTransaction.getWallet().getId().longValue() == wallet.getId()
                                && ratedTransaction.getInvoiceSubCategory().getId().longValue() == invoiceSubCat.getId())) {
                            continue;
                        }
                        if (!isVirtual) {
                            getEntityManager().refresh(ratedTransaction);
                        }
                        BigDecimal transactionAmount = entreprise ? ratedTransaction.getAmountWithTax() : ratedTransaction.getAmountWithoutTax();
                        if (transactionAmount == null) {
                            transactionAmount = BigDecimal.ZERO;
                        }

                        Element line = doc.createElement("line");
                        String code = "", description = "";
                        Date periodStartDate = null;
                        Date periodEndDate = null;
                        WalletOperation walletOperation = ratedTransaction.getWalletOperation();
                        code = ratedTransaction.getCode();
                        description = ratedTransaction.getDescription();

                        if (ratedTransaction.getWalletOperationId() != null) {
                            walletOperation = getEntityManager().find(WalletOperation.class, ratedTransaction.getWalletOperationId());
                        }
                        if (walletOperation != null) {
                            if (StringUtils.isBlank(code)) {
                                code = walletOperation.getCode();
                            }
                            if (StringUtils.isBlank(description)) {
                                description = walletOperation.getDescription();
                            }
                            ChargeInstance chargeInstance = walletOperation.getChargeInstance();
                            if (appProvider.getInvoiceConfiguration().getDisplayChargesPeriods()) {

                                if (!isVirtual) {
                                    chargeInstance = (ChargeInstance) chargeInstanceService.findById(chargeInstance.getId(), false);
                                }

                                ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
                                // get periodStartDate and periodEndDate for recurrents
                                periodStartDate = walletOperation.getStartDate();
                                periodEndDate = walletOperation.getEndDate();
                                // get periodStartDate and periodEndDate for usages
                                // instanceof is not used in this control because chargeTemplate can never be instance of usageChargeTemplate according to model structure
                                Date operationDate = walletOperation.getOperationDate();
                                if (usageChargeTemplateService.findById(chargeTemplate.getId()) != null && operationDate != null) {
                                    CounterPeriod counterPeriod = null;
                                    CounterInstance counter = walletOperation.getCounter();
                                    if (!isVirtual) {
                                        counterPeriod = counterPeriodService.getCounterPeriod(counter, operationDate);
                                    } else {
                                        counterPeriod = counter.getCounterPeriod(operationDate);
                                    }
                                    if (counterPeriod != null) {
                                        periodStartDate = counterPeriod.getPeriodStartDate();
                                        periodEndDate = counterPeriod.getPeriodEndDate();
                                    }
                                }
                                line.setAttribute("periodEndDate", DateUtils.formatDateWithPattern(periodEndDate, invoiceDateFormat));
                                line.setAttribute("periodStartDate", DateUtils.formatDateWithPattern(periodStartDate, invoiceDateFormat));
                            }
                        }

                        line.setAttribute("code", code != null ? code : "");

                        if (ratedTransaction.getParameter1() != null) {
                            line.setAttribute("param1", ratedTransaction.getParameter1());
                        }
                        if (ratedTransaction.getParameter2() != null) {
                            line.setAttribute("param2", ratedTransaction.getParameter2());
                        }
                        if (ratedTransaction.getParameter3() != null) {
                            line.setAttribute("param3", ratedTransaction.getParameter3());
                        }
                        if (ratedTransaction.getParameterExtra() != null) {
                            Element paramExtra = doc.createElement("paramExtra");
                            paramExtra.appendChild(doc.createTextNode(ratedTransaction.getParameterExtra()));
                            line.appendChild(paramExtra);                            
                        }

                        if (ratedTransaction.getPriceplan() != null) {
                            Element pricePlan = doc.createElement("pricePlan");
                            pricePlan.setAttribute("code", ratedTransaction.getPriceplan().getCode());

                            String translationKey = "PP_" + ratedTransaction.getPriceplan().getCode() + "_" + languageCode;
                            String descTranslated = descriptionMap.get(translationKey);
                            if (descTranslated == null) {
                                descTranslated = ratedTransaction.getPriceplan().getDescriptionOrCode();
                                if (ratedTransaction.getPriceplan().getDescriptionI18n() != null
                                        && ratedTransaction.getPriceplan().getDescriptionI18n().get(languageCode) != null) {
                                    descTranslated = ratedTransaction.getPriceplan().getDescriptionI18n().get(languageCode);
                                }
                                descriptionMap.put(translationKey, descTranslated);
                            }

                            pricePlan.setAttribute("description", descTranslated);

                            line.appendChild(pricePlan);
                            if (!priceplanIds.contains(ratedTransaction.getPriceplan().getId())) {
                                addPricePlans(ratedTransaction.getPriceplan(), doc, invoiceTag);
                                priceplanIds.add(ratedTransaction.getPriceplan().getId());
                            }
                        }

                        Element lebel = doc.createElement("label");
                        Text lebelTxt = doc.createTextNode(description != null ? description : "");

                        lebel.appendChild(lebelTxt);
                        line.appendChild(lebel);

                        if (!StringUtils.isBlank(ratedTransaction.getUnityDescription())) {
                            Element lineUnit = doc.createElement("unit");
                            Text lineUnitTxt = doc.createTextNode(ratedTransaction.getUnityDescription());
                            lineUnit.appendChild(lineUnitTxt);
                            line.appendChild(lineUnit);
                        }
                        if (!StringUtils.isBlank(ratedTransaction.getRatingUnitDescription())) {
                            Element lineRatingUnit = doc.createElement("ratingUnit");
                            Text lineUnitTxt = doc.createTextNode(ratedTransaction.getRatingUnitDescription());
                            lineRatingUnit.appendChild(lineUnitTxt);
                            line.appendChild(lineRatingUnit);
                        }
                        Element lineUnitAmountWithoutTax = doc.createElement("unitAmountWithoutTax");
                        Text lineUnitAmountWithoutTaxTxt = doc.createTextNode(ratedTransaction.getUnitAmountWithoutTax().toPlainString());
                        lineUnitAmountWithoutTax.appendChild(lineUnitAmountWithoutTaxTxt);
                        line.appendChild(lineUnitAmountWithoutTax);

                        Element lineAmountWithoutTax = doc.createElement("amountWithoutTax");
                        Text lineAmountWithoutTaxTxt = doc.createTextNode(round(ratedTransaction.getAmountWithoutTax(), rounding));
                        lineAmountWithoutTax.appendChild(lineAmountWithoutTaxTxt);
                        line.appendChild(lineAmountWithoutTax);

                        if (!enterprise) {
                            Element lineAmountWithTax = doc.createElement("amountWithTax");
                            Text lineAmountWithTaxTxt = doc.createTextNode(round(ratedTransaction.getAmountWithTax(), rounding));
                            lineAmountWithTax.appendChild(lineAmountWithTaxTxt);
                            line.appendChild(lineAmountWithTax);
                        }

                        Element quantity = doc.createElement("quantity");
                        Text quantityTxt = doc.createTextNode(ratedTransaction.getQuantity() != null ? ratedTransaction.getQuantity().toPlainString() : "");
                        quantity.appendChild(quantityTxt);
                        line.appendChild(quantity);

                        Element usageDate = doc.createElement("usageDate");
                        Text usageDateTxt = doc.createTextNode(DateUtils.formatDateWithPattern(ratedTransaction.getUsageDate(), invoiceDateFormat));
                        usageDate.appendChild(usageDateTxt);
                        line.appendChild(usageDate);
                        EDR edr = ratedTransaction.getEdr();

                        if (appProvider.getInvoiceConfiguration() != null && appProvider.getInvoiceConfiguration().getDisplayEdrs() != null
                                && appProvider.getInvoiceConfiguration().getDisplayEdrs() && edr != null) {
                            Element edrInfo = doc.createElement("edr");
                            edrInfo.setAttribute("originRecord", edr.getOriginRecord() != null ? edr.getOriginRecord() : "");
                            edrInfo.setAttribute("originBatch", edr.getOriginBatch() != null ? edr.getOriginBatch() : "");
                            edrInfo.setAttribute("quantity", edr.getQuantity() != null ? edr.getQuantity().toPlainString() : "");
                            edrInfo.setAttribute("status", String.valueOf(edr.getStatus()) != null ? String.valueOf(edr.getStatus()) : "");
                            edrInfo.setAttribute("rejectReason", edr.getRejectReason() != null ? edr.getRejectReason() : "");
                            edrInfo.setAttribute("subscription", edr.getSubscription() != null ? edr.getSubscription().getDescription() : "");
                            edrInfo.setAttribute("eventDate", DateUtils.formatDateWithPattern(edr.getEventDate(), invoiceDateTimeFormat));
                            edrInfo.setAttribute("accessCode", edr.getAccessCode() != null ? edr.getAccessCode() : "");
                            edrInfo.setAttribute("parameter1", edr.getParameter1() != null ? edr.getParameter1() : "");
                            edrInfo.setAttribute("parameter2", edr.getParameter2() != null ? edr.getParameter2() : "");
                            edrInfo.setAttribute("parameter3", edr.getParameter3() != null ? edr.getParameter3() : "");
                            edrInfo.setAttribute("parameter4", edr.getParameter4() != null ? edr.getParameter4() : "");
                            edrInfo.setAttribute("parameter5", edr.getParameter5() != null ? edr.getParameter5() : "");
                            edrInfo.setAttribute("parameter6", edr.getParameter6() != null ? edr.getParameter6() : "");
                            edrInfo.setAttribute("parameter7", edr.getParameter7() != null ? edr.getParameter7() : "");
                            edrInfo.setAttribute("parameter8", edr.getParameter8() != null ? edr.getParameter8() : "");
                            edrInfo.setAttribute("parameter9", edr.getParameter9() != null ? edr.getParameter9() : "");
                            edrInfo.setAttribute("dateParam1", DateUtils.formatDateWithPattern(edr.getDateParam1(), invoiceDateFormat));
                            edrInfo.setAttribute("dateParam2", DateUtils.formatDateWithPattern(edr.getDateParam2(), invoiceDateTimeFormat));
                            edrInfo.setAttribute("dateParam3", DateUtils.formatDateWithPattern(edr.getDateParam3(), invoiceDateTimeFormat));
                            edrInfo.setAttribute("dateParam4", DateUtils.formatDateWithPattern(edr.getDateParam4(), invoiceDateTimeFormat));
                            edrInfo.setAttribute("dateParam5", DateUtils.formatDateWithPattern(edr.getDateParam5(), invoiceDateTimeFormat));
                            edrInfo.setAttribute("decimalParam1", edr.getDecimalParam1() != null ? edr.getDecimalParam1().toPlainString() : "");
                            edrInfo.setAttribute("decimalParam2", edr.getDecimalParam2() != null ? edr.getDecimalParam2().toPlainString() : "");
                            edrInfo.setAttribute("decimalParam3", edr.getDecimalParam3() != null ? edr.getDecimalParam3().toPlainString() : "");
                            edrInfo.setAttribute("decimalParam4", edr.getDecimalParam4() != null ? edr.getDecimalParam4().toPlainString() : "");
                            edrInfo.setAttribute("decimalParam5", edr.getDecimalParam5() != null ? edr.getDecimalParam5().toPlainString() : "");
                            line.appendChild(edrInfo);
                        }

                        if (!isVirtual) {
                            if (walletOperation != null) {
                                // Retrieve Service Instance
                                ChargeInstance chargeInstance = walletOperation.getChargeInstance();
                                ServiceInstance serviceInstance = null;
                                if (chargeInstance instanceof RecurringChargeInstance) {
                                    serviceInstance = ((RecurringChargeInstance) walletOperation.getChargeInstance()).getServiceInstance();
                                } else if (chargeInstance instanceof UsageChargeInstance) {
                                    serviceInstance = ((UsageChargeInstance) walletOperation.getChargeInstance()).getServiceInstance();
                                } else if (chargeInstance instanceof OneShotChargeInstance) {
                                    serviceInstance = ((OneShotChargeInstance) walletOperation.getChargeInstance()).getSubscriptionServiceInstance();
                                    if (serviceInstance == null) {
                                        ((OneShotChargeInstance) walletOperation.getChargeInstance()).getTerminationServiceInstance();
                                    }
                                }

                                log.debug("Before serviceInstance:" + (System.currentTimeMillis() - startDate));

                                if (serviceInstance != null) {
                                    addService(serviceInstance, doc, ratedTransaction.getOfferCode(), line);
                                }

                                log.debug("After serviceInstance:" + (System.currentTimeMillis() - startDate));

                            }
                        }
                        subCategory.appendChild(line);
                    }
                    addCustomFields(invoiceSubCat, doc, subCategory);
                }
            }
        }
    }

    /**
     * @param billingAccount billing account
     * @param amountTax amount of tax
     * @param invoiceAgregates list of agregate
     * @param doc DOM document
     * @param parent true/false
     * @param hasInvoiceAgregate true/false
     * @throws BusinessException business exception
     */
    private void addTaxes(BillingAccount billingAccount, BigDecimal amountTax, List<InvoiceAgregate> invoiceAgregates, Document doc, Element parent, boolean hasInvoiceAgregate)
            throws BusinessException {
        // log.info("adding taxes...");
        Element taxes = doc.createElement("taxes");
        boolean exoneratedFromTaxes = billingAccountService.isExonerated(billingAccount);

        if (exoneratedFromTaxes) {
            Element exoneratedElement = doc.createElement("exonerated");
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            Customer customer = customerAccount.getCustomer();
            CustomerCategory customerCategory = customer.getCustomerCategory();
            exoneratedElement.setAttribute("reason", customerCategory.getExonerationReason());
            taxes.appendChild(exoneratedElement);
        } else {
            int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
            taxes.setAttribute("total", round(amountTax, rounding));
            parent.appendChild(taxes);
            Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();
            if (hasInvoiceAgregate) {
                for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {
                    if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                        TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
                        TaxInvoiceAgregate taxAgregate = null;

                        if (taxInvoiceAgregateMap.containsKey(taxInvoiceAgregate.getTax().getId())) {
                            taxAgregate = taxInvoiceAgregateMap.get(taxInvoiceAgregate.getTax().getId());
                            taxAgregate.setAmountTax(taxAgregate.getAmountTax().add(taxInvoiceAgregate.getAmountTax()));
                            taxAgregate.setAmountWithoutTax(taxAgregate.getAmountWithoutTax().add(taxInvoiceAgregate.getAmountWithoutTax()));
                        } else {
                            taxAgregate = new TaxInvoiceAgregate();
                            taxAgregate.setTaxPercent(taxInvoiceAgregate.getTaxPercent());
                            taxAgregate.setTax(taxInvoiceAgregate.getTax());
                            taxAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax());
                            taxAgregate.setAmountWithoutTax(taxInvoiceAgregate.getAmountWithoutTax());
                            taxInvoiceAgregateMap.put(taxInvoiceAgregate.getTax().getId(), taxAgregate);
                        }
                    }
                }
            }

            int taxId = 0;
            Collection<TaxInvoiceAgregate> values = taxInvoiceAgregateMap.values();
            for (TaxInvoiceAgregate taxInvoiceAgregate : values) {
                Element tax = doc.createElement("tax");
                tax.setAttribute("id", ++taxId + "");
                Tax taxData = taxInvoiceAgregate.getTax();
                tax.setAttribute("code", taxData.getCode() + "");
                addCustomFields(taxData, doc, tax);
                String languageCode = "";
                try {
                    // log.info("ba={}, tradingLanguage={}",
                    // invoice.getBillingAccount(),
                    // invoice.getBillingAccount().getTradingLanguage());
                    languageCode = billingAccount.getTradingLanguage().getLanguage().getLanguageCode();
                } catch (NullPointerException e) {
                    log.error("Billing account must have a trading language.");
                    throw new BusinessException("Billing account must have a trading language.");
                }

                String translationKey = "T_" + taxInvoiceAgregate.getTax().getCode() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationKey);
                if (descTranslated == null) {
                    descTranslated = taxInvoiceAgregate.getTax().getDescriptionOrCode();
                    if (taxInvoiceAgregate.getTax().getDescriptionI18n() != null && taxInvoiceAgregate.getTax().getDescriptionI18n().get(languageCode) != null) {
                        descTranslated = taxInvoiceAgregate.getTax().getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationKey, descTranslated);
                }

                Element taxName = doc.createElement("name");
                Text taxNameTxt = doc.createTextNode(descTranslated);
                taxName.appendChild(taxNameTxt);
                tax.appendChild(taxName);

                Element percent = doc.createElement("percent");
                Text percentTxt = doc.createTextNode(round(taxInvoiceAgregate.getTaxPercent(), rounding));
                percent.appendChild(percentTxt);
                tax.appendChild(percent);

                Element taxAmount = doc.createElement("amount");
                Text amountTxt = doc.createTextNode(round(taxInvoiceAgregate.getAmountTax(), rounding));
                taxAmount.appendChild(amountTxt);
                tax.appendChild(taxAmount);

                Element amountHT = doc.createElement("amountHT");
                Text amountHTTxt = doc.createTextNode(round(taxInvoiceAgregate.getAmountWithoutTax(), rounding));
                amountHT.appendChild(amountHTTxt);
                tax.appendChild(amountHT);

                taxes.appendChild(tax);

            }
        }
    }

    /**
     * @param invoiceAgregates list of invoice agregate
     * @param doc DOM document
     * @param parent parent node
     * @param subCategoryInvoiceAgregates list of sub category invoice
     */
    private void addHeaderCategories(List<InvoiceAgregate> invoiceAgregates, Document doc, Element parent, List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
        boolean entreprise = appProvider.isEntreprise();
        LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO> headerCategories = new LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO>();
        List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();

        if (!invoiceAgregates.isEmpty()) {
            for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {

                if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                    CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
                    categoryInvoiceAgregates.add(categoryInvoiceAgregate);
                }
            }
        }

        Collections.sort(categoryInvoiceAgregates, new Comparator<CategoryInvoiceAgregate>() {
            public int compare(CategoryInvoiceAgregate c0, CategoryInvoiceAgregate c1) {
                if (c0.getInvoiceCategory() != null && c1.getInvoiceCategory() != null && c0.getInvoiceCategory().getSortIndex() != null
                        && c1.getInvoiceCategory().getSortIndex() != null) {
                    return c0.getInvoiceCategory().getSortIndex().compareTo(c1.getInvoiceCategory().getSortIndex());
                }
                return 0;
            }
        });

        for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
            InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();
            XMLInvoiceHeaderCategoryDTO headerCat = null;
            if (headerCategories.containsKey(invoiceCategory.getCode())) {
                headerCat = headerCategories.get(invoiceCategory.getCode());
                headerCat.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
                headerCat.addAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
            } else {
                headerCat = new XMLInvoiceHeaderCategoryDTO();
                headerCat.setDescription(invoiceCategory.getDescription());
                headerCat.setCode(invoiceCategory.getCode());
                headerCat.setAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
                headerCat.setAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
            }

            if (subCategoryInvoiceAgregates != null) {
            	for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
            		CategoryInvoiceAgregate categoryInvoiceAgregate2 = subCatInvoiceAgregate.getCategoryInvoiceAgregate();
					if (categoryInvoiceAgregate2 != null && categoryInvoiceAgregate != null
							&& categoryInvoiceAgregate2.getId() != null && categoryInvoiceAgregate.getId() != null
            				&& categoryInvoiceAgregate2.getId().longValue() != categoryInvoiceAgregate.getId().longValue()
            				|| (categoryInvoiceAgregate2 == null || categoryInvoiceAgregate == null)) {
            			continue;
            		}

            		headerCat.getSubCategoryInvoiceAgregates().add(subCatInvoiceAgregate);
            		headerCategories.put(invoiceCategory.getCode(), headerCat);
            	}
            } else {
            	Set<SubCategoryInvoiceAgregate> virtualSubCategoryInvoiceAgregates = categoryInvoiceAgregate.getSubCategoryInvoiceAgregates();

                for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : virtualSubCategoryInvoiceAgregates) {
                    headerCat.getSubCategoryInvoiceAgregates().add(subCatInvoiceAgregate);
                    headerCategories.put(invoiceCategory.getCode(), headerCat);
                }
            }
        }
        addHeaderCategories(headerCategories, doc, parent, entreprise);

    }

    /**
     * @param headerCategories map of invoice header category
     * @param doc DOM document
     * @param parent true/false
     * @param entreprise true/false
     */
    private void addHeaderCategories(LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO> headerCategories, Document doc, Element parent, boolean entreprise) {
        long startDate = System.currentTimeMillis();
        int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
        // log.debug("add header categories");

        Element categories = doc.createElement("categories");
        parent.appendChild(categories);
        for (XMLInvoiceHeaderCategoryDTO xmlInvoiceHeaderCategoryDTO : headerCategories.values()) {
            Element category = doc.createElement("category");
            category.setAttribute("label", xmlInvoiceHeaderCategoryDTO.getDescription());
            category.setAttribute("code", xmlInvoiceHeaderCategoryDTO != null && xmlInvoiceHeaderCategoryDTO.getCode() != null ? xmlInvoiceHeaderCategoryDTO.getCode() : "");
            categories.appendChild(category);

            Element amountWithoutTax = doc.createElement("amountWithoutTax");
            Text amountWithoutTaxTxt = doc.createTextNode(round(xmlInvoiceHeaderCategoryDTO.getAmountWithoutTax(), rounding));
            amountWithoutTax.appendChild(amountWithoutTaxTxt);
            category.appendChild(amountWithoutTax);
            if (xmlInvoiceHeaderCategoryDTO.getSubCategoryInvoiceAgregates() != null) {
                Element subCategories = doc.createElement("subCategories");
                category.appendChild(subCategories);

                for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : xmlInvoiceHeaderCategoryDTO.getSubCategoryInvoiceAgregates()) {
                    Element subCategory = doc.createElement("subCategory");
                    InvoiceSubCategory invoiceSubCat = subCatInvoiceAgregate.getInvoiceSubCategory();
                    // description translated is set on aggregate
                    String invoiceSubCategoryLabel = subCatInvoiceAgregate.getDescription() == null ? "" : subCatInvoiceAgregate.getDescription();
                    subCategories.appendChild(subCategory);
                    subCategory.setAttribute("label", (invoiceSubCategoryLabel != null) ? invoiceSubCategoryLabel : "");
                    subCategory.setAttribute("code", invoiceSubCat.getCode());
                    String taxesCode = "";
                    String taxesPercent = "";
                    String sep = "";
                    for (Tax tax : subCatInvoiceAgregate.getSubCategoryTaxes()) {
                        taxesCode = taxesCode + sep + tax.getCode();
                        taxesPercent = taxesPercent + sep + round(tax.getPercent(), rounding);
                        sep = ";";
                    }
                    subCategory.setAttribute("taxCode", taxesCode);
                    subCategory.setAttribute("taxPercent", taxesPercent);

                    if (!entreprise) {
                        subCategory.setAttribute("amountWithTax", round(subCatInvoiceAgregate.getAmountWithTax(), rounding));
                    }

                    subCategory.setAttribute("amountWithoutTax", round(subCatInvoiceAgregate.getAmountWithoutTax(), rounding));
                    // subCategory.setAttribute("taxAmount",
                    // round(subCatInvoiceAgregate.getAmountTax(), rounding));

                }
            }
        }
    }

    /**
     * @param invoice invoice used get discounts
     * @param doc DOM document
     * @param parent true/false
     * @param isVirtual true/false
     */
    private void addDiscounts(Invoice invoice, Document doc, Element parent, boolean isVirtual) {

        int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
        Element discounts = doc.createElement("discounts");

        parent.appendChild(discounts);

        List<SubCategoryInvoiceAgregate> discountInvoiceAgregates = new ArrayList<>();

        
        if (isVirtual) {
        	long startDate = System.currentTimeMillis();
            discountInvoiceAgregates = invoice.getDiscountAgregates();            

        } else {
            discountInvoiceAgregates = invoiceAgregateService.findDiscountAggregates(invoice);
        }

        for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : discountInvoiceAgregates) {

            Element discount = doc.createElement("discount");
            discount.setAttribute("discountPlanCode", subCategoryInvoiceAgregate.getDiscountPlanCode());
            discount.setAttribute("discountPlanItemCode", subCategoryInvoiceAgregate.getDiscountPlanItemCode());
            discount.setAttribute("invoiceSubCategoryCode", subCategoryInvoiceAgregate.getInvoiceSubCategory().getCode());
            discount.setAttribute("discountAmountWithoutTax", round(subCategoryInvoiceAgregate.getAmountWithoutTax(), rounding) + "");
            discount.setAttribute("discountPercent", round(subCategoryInvoiceAgregate.getDiscountPercent(), rounding) + "");

            discounts.appendChild(discount);

        }

    }

    /**
     * @param amount amount needs to be rounded
     * @param scale mode of scale
     * @return string represents the amount.
     */
    private String round(BigDecimal amount, Integer scale) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (scale == null) {
            scale = 2;
        }
        amount = amount.setScale(scale, RoundingMode.HALF_UP);
        return amount.toPlainString();
    }

    /**
     * @param serviceInstances
     * @return true/false
     */
    @SuppressWarnings("unused")
    private boolean isAllServiceInstancesTerminated(List<ServiceInstance> serviceInstances) {
        for (ServiceInstance service : serviceInstances) {
            boolean serviceActive = service.getStatus() == InstanceStatusEnum.ACTIVE;
            if (serviceActive) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param linkedInvoices list of invoice
     * @return linked invoice
     */
    private String getLinkedInvoicesnumberAsString(List<Invoice> linkedInvoices) {
        if (linkedInvoices == null || linkedInvoices.isEmpty()) {
            return "";
        }
        String result = "";
        for (Invoice inv : linkedInvoices) {
            result += inv.getInvoiceNumber() + " ";
        }
        return result;
    }
}
