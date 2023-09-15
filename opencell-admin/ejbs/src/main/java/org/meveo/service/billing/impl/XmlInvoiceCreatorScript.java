/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.NumberUtils.toPlainString;
import static org.meveo.commons.utils.StringUtils.getDefaultIfNull;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.commons.utils.InvoiceCategoryComparatorUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.contract.Contract;
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
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A default implementation of XML invoice creation.
 * To extend a default implementation:
 * <ul>
 * <li>Create java class type script that will extend XmlInvoiceCreatorScript</li>
 * <li>Override and supplement any necessary method. Return NULL to exclude a corresponding section from XML</li>
 * <li>Reference custom XML invoice creation script in Invoice type - field invoiceType.customInvoiceXmlScriptInstance</li>
 * </ul>
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Wassim Drira
 * @author Mounir Bahije
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author Mounir Bahije
 * @author Abdellatif BARI
 * @author Andrius Karpavicius
 * @lastModifiedVersion 11.0
 **/
@Stateless
@DefaultXmlInvoiceCreatorScript
public class XmlInvoiceCreatorScript implements IXmlInvoiceCreatorScript {

    private static final String INVOICE_DATE_FORMAT = "invoice.dateFormat";
    private static final String DEFAULT_VATEX_EU_O_VALUE = "VATEX-EU-O";
    private static final String YES = "Yes";
    private static final String NO = "No";

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    protected InvoiceService invoiceService;

    @Inject
    protected RatedTransactionService ratedTransactionService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    protected BillingAccountService billingAccountService;

    @Inject
    protected InvoiceTypeService invoiceTypeService;

    @Inject
    protected ServiceSingleton serviceSingleton;

    @Inject
    protected WalletOperationService walletOperationService;

    @Inject
    protected InvoiceLineService invoiceLineService;

    @Inject
    protected InvoiceSubTotalsService invoiceSubTotalsService;

    /**
     * transformer factory.
     */
    protected TransformerFactory transfac = TransformerFactory.newInstance();

    /**
     * description translation map .
     */
    protected Map<String, String> descriptionMap = new HashMap<>();

    /**
     * default date format.
     */
    protected static String DEFAULT_DATE_PATTERN = "yyyyMMdd";

    /**
     * default date time format.
     */
    protected static String DEFAULT_DATE_TIME_PATTERN = "yyyyMMdd'T'HH:mm:ss";

    protected Logger log = LoggerFactory.getLogger(getClass());
    @Inject
    private AccountEntitySearchService accountEntitySearchService;

    /**
     * Create XML invoice and store its content in a file. Note: Just creates a file - does not update invoice with file information
     *
     * @param invoice         Invoice to convert invoice used to create xml
     * @param isVirtual       Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param fullXmlFilePath Full xml file path
     * @param rtBillingProcess invoicing process : true old process using RT, false : new process using invoiceLines
     * @return DOM element xml file
     * @throws BusinessException            business exception
     * @throws ParserConfigurationException parsing exception
     */
    public File createDocumentAndFile(Invoice invoice, boolean isVirtual, String fullXmlFilePath,
                                      boolean rtBillingProcess) throws BusinessException, ParserConfigurationException {
        Document doc = createDocument(invoice, isVirtual, rtBillingProcess);
        return createFile(doc, invoice, fullXmlFilePath);
    }

    /**
     * Store XML DOM into a file.
     *
     * @param doc             XML invoice DOM
     * @param invoice         Invoice to convert invoice used to build xml
     * @param fullXmlFilePath Full xml file path
     * @return DOM element xml file
     * @throws BusinessException business exception
     */
    public File createFile(Document doc, Invoice invoice, String fullXmlFilePath) throws BusinessException {
        try {
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // create string from xml tree
            DOMSource source = new DOMSource(doc);
            File xmlFile = new File(fullXmlFilePath);
            OutputStream outStream = StorageFactory.getOutputStream(fullXmlFilePath);
            StreamResult result = new StreamResult(outStream);
            trans.transform(source, result);
            log.info("XML file '{}' produced for invoice {}", fullXmlFilePath, invoice.getInvoiceNumberOrTemporaryNumber());
            try {
                assert outStream != null;
                outStream.close();
            }
            catch (IOException e) {
                log.error("Transformer exception : {}", e.getMessage());
            }
            return xmlFile;
        } catch (TransformerException e) {
            throw new BusinessException("Failed to create xml file for invoice id=" + invoice.getId() + " number=" + invoice.getInvoiceNumberOrTemporaryNumber(), e);
        }
    }

    /**
     * Create Invoice XML document as DOM
     *
     * @param invoice   Invoice to convert Invoice used to create xml
     * @param isVirtual Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param rtBillingProcess invoicing process : true old process using RT, false : new process using invoiceLines
     * @return DOM element XML DOM document
     * @throws BusinessException            business exception
     * @throws ParserConfigurationException parsing exception
     */
    public Document createDocument(Invoice invoice, boolean isVirtual, boolean rtBillingProcess) throws BusinessException, ParserConfigurationException {

        invoice = invoiceService.retrieveIfNotManaged(invoice);
        boolean isInvoiceAdjustment = invoiceTypeService.getListAdjustementCode().contains(invoice.getInvoiceType().getCode());
        BillingRun billingRun = invoice.getBillingRun();
        if (!isInvoiceAdjustment && billingRun != null && BillingRunStatusEnum.VALIDATED.equals(billingRun.getStatus()) && invoice.getInvoiceNumber() == null) {
            invoice = serviceSingleton.assignInvoiceNumber(invoice);
        }

        // Build tax indexes : for jasper need, we nooed to link tax over two page with index, not by code. Example : for TAX_05, we muste display "1" in both page, "2" for second tax...
        Map<String, String> mapTaxesIndexes = buildTaxesIndexes(invoice);

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        rtBillingProcess = rtBillingProcess && !(invoice.getInvoiceLines()!=null && !invoice.getInvoiceLines().isEmpty());
        Element invoiceTag = rtBillingProcess ? createInvoiceSection(doc, invoice, isVirtual, isInvoiceAdjustment, docBuilder, mapTaxesIndexes)
                : createInvoiceSectionIL(doc, invoice, isVirtual, isInvoiceAdjustment, docBuilder, mapTaxesIndexes);
        doc.appendChild(invoiceTag);
        return doc;
    }

    /**
     * @param doc         XML invoice DOM
     * @param contactInfo Contact information
     * @return DOM element
     */
    protected Element toContactTag(Document doc, ContactInformation contactInfo) {
        Element contactTag = doc.createElement("contact");
        if (contactInfo != null) {
            contactTag.setAttribute("email", contactInfo.getEmail() == null ? "" : contactInfo.getEmail());
            contactTag.setAttribute("fax", contactInfo.getFax() == null ? "" : contactInfo.getFax());
            contactTag.setAttribute("mobile", contactInfo.getMobile() == null ? "" : contactInfo.getMobile());
            contactTag.setAttribute("phone", contactInfo.getPhone() == null ? "" : contactInfo.getPhone());
        }
        return contactTag;
    }

    /**
     * Get user accounts
     *
     * @param invoiceAgregates Invoice agregates
     * @return user accounts list
     */
    private Set<UserAccount> getUserAccounts(List<InvoiceAgregate> invoiceAgregates) {
        Set<UserAccount> userAccounts = new HashSet<>();
        if (invoiceAgregates != null && !invoiceAgregates.isEmpty()) {
            for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {
                if (invoiceAgregate instanceof CategoryInvoiceAgregate && ((CategoryInvoiceAgregate) invoiceAgregate).getUserAccount() != null) {
                    userAccounts.add(((CategoryInvoiceAgregate) invoiceAgregate).getUserAccount());
                }
            }
        }
        return userAccounts;
    }

    /**
     * Get user accounts
     *
     * @param invoice              Invoice to convert
     * @param invoiceConfiguration Invoice configuration
     * @return user accounts list
     */
    protected Set<UserAccount> getUserAccounts(Invoice invoice, InvoiceConfiguration invoiceConfiguration) {
        Set<UserAccount> userAccounts = new HashSet<>();
        if (invoice.getSubscription() != null) {
            userAccounts.add(invoice.getSubscription().getUserAccount());
        }
        if (userAccounts.isEmpty()) {
            if (invoiceConfiguration.isDisplayUserAccountHierarchy()) {
                userAccounts.addAll(invoice.getBillingAccount().getParentUserAccounts());
            } else {
                userAccounts.addAll(invoice.getBillingAccount().getUsersAccounts());
            }
        }
        userAccounts.addAll(getUserAccounts(invoice.getInvoiceAgregates()));
        return userAccounts;
    }


    /**
     * Get user accounts
     *
     * @param invoice Invoice to convert
     * @return user accounts list
     */
    protected Set<UserAccount> getUserAccounts(Invoice invoice) {
        Set<UserAccount> userAccounts = new HashSet<>();
        userAccounts.addAll(invoice.getBillingAccount().getUsersAccounts());
        userAccounts.addAll(getUserAccounts(invoice.getInvoiceAgregates()));
        return userAccounts;
    }


    /**
     * Create invoice/details/userAccounts DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param ratedTransactions    Rated transactions
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceConfiguration Invoice configuration
     * @return DOM element
     */
    protected Element createUserAccountsSection(Document doc, Invoice invoice, List<RatedTransaction> ratedTransactions, boolean isVirtual, InvoiceConfiguration invoiceConfiguration) {

        Element userAccountsTag = doc.createElement("userAccounts");
        String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
        Set<UserAccount> userAccounts = getUserAccounts(invoice);
        for (UserAccount userAccount : userAccounts) {
            if (!invoiceConfiguration.isDisplayUserAccountHierarchy() || userAccount.getParentUserAccount() == null) {
                Element userAccountTag = createUserAccountSection(doc, invoice, userAccount, ratedTransactions, isVirtual, false, invoiceLanguageCode, invoiceConfiguration);
                if (invoiceConfiguration.isDisplayUserAccountHierarchy()) {
                    createUserAccountChildSection(doc, invoice, userAccount, ratedTransactions, isVirtual, false, invoiceLanguageCode, invoiceConfiguration,
                            userAccounts.stream().collect(Collectors.toList()), userAccountTag);
                }
                if (userAccountTag != null) {
                    userAccountsTag.appendChild(userAccountTag);
                }
            }
        }

        // Generate invoice lines for Categories/RTs that are not linked to User account
        Element userAccountTag = createUserAccountSection(doc, invoice, null, ratedTransactions, isVirtual, userAccountsTag.getChildNodes().getLength() == 0, invoiceLanguageCode, invoiceConfiguration);
        if (userAccountTag != null) {
            userAccountsTag.appendChild(userAccountTag);
        }
        return userAccountsTag;
    }

    private void createUserAccountChildSection(Document doc, Invoice invoice, UserAccount parentUserAccount, List<RatedTransaction> ratedTransactions, boolean isVirtual, boolean ignoreUA, String invoiceLanguageCode,
            InvoiceConfiguration invoiceConfiguration, List<UserAccount> userAccounts, Element parentUserAccountTag) {

        Element childUserAccountsTag = doc.createElement("userAccounts");
        boolean existChild = false;
    	for(UserAccount childUserAccount : userAccounts) {
    		if(parentUserAccount.equals(childUserAccount.getParentUserAccount())) {
    			existChild = true;
    	    	Element childUserAccountTag = createUserAccountSection(doc, invoice, childUserAccount, ratedTransactions, isVirtual, false, invoiceLanguageCode, invoiceConfiguration);
    	    	if(childUserAccountTag != null) {
                    createUserAccountChildSection(doc, invoice, childUserAccount, ratedTransactions, isVirtual, false, invoiceLanguageCode, invoiceConfiguration, userAccounts, childUserAccountTag);
        	    	childUserAccountsTag.appendChild(childUserAccountTag);
    	    	}
    		}
    	}
    	if(existChild && childUserAccountsTag != null) {
    		parentUserAccountTag.appendChild(childUserAccountsTag);
    	}
    }

    private void createUserAccountChildSectionIL(Document doc, Invoice invoice, List<InvoiceLine> invoiceLines,
                                                 boolean isVirtual, String invoiceLanguageCode, InvoiceConfiguration invoiceConfiguration,
                                                 List<UserAccount> childUserAccounts, Element parentUserAccountTag, Map<String, String> mapTaxesIndexes) {
        Element childUserAccountsTag = doc.createElement("userAccounts");
        boolean existChild = false;
    	for(UserAccount childUserAccount : childUserAccounts) {
            existChild = true;
            Element childUserAccountTag = createUserAccountSectionIL(doc, invoice, childUserAccount, invoiceLines,
                    isVirtual, false, invoiceLanguageCode, invoiceConfiguration, mapTaxesIndexes);
            if (childUserAccountTag != null) {
                createUserAccountChildSectionIL(doc, invoice, invoiceLines, isVirtual, invoiceLanguageCode,
                        invoiceConfiguration, childUserAccount.getUserAccounts(), childUserAccountTag, mapTaxesIndexes);
                childUserAccountsTag.appendChild(childUserAccountTag);
            }
    	}
    	if(existChild && childUserAccountsTag != null) {
    		parentUserAccountTag.appendChild(childUserAccountsTag);
    	}
    }

    /**
     * Create invoice/details/userAccounts/userAccount DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param userAccount          User account
     * @param ratedTransactions    Rated transactions
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB * @param ignoreUA Shall UA be ignored and all
     *                             subscriptions be returned. Used as true when aggregation by user account is turned off - system property invoice.aggregateByUA=false
     * @param invoiceLanguageCode
     * @param invoiceConfiguration Invoice configuration
     * @return Element
     */
    public Element createUserAccountSection(Document doc, Invoice invoice, UserAccount userAccount, List<RatedTransaction> ratedTransactions, boolean isVirtual, boolean ignoreUA, String invoiceLanguageCode,
            InvoiceConfiguration invoiceConfiguration) {

        Element categoriesTag = createUAInvoiceCategories(doc, invoice, userAccount, ratedTransactions, isVirtual, invoiceConfiguration);
        if (categoriesTag == null) {
            return null;
        }
        Element userAccountTag = doc.createElement("userAccount");
        if (userAccount == null) {
            userAccountTag.setAttribute("description", "-");
        } else {
            userAccountTag.setAttribute("id", userAccount.getId() + "");
            userAccountTag.setAttribute("code", userAccount.getCode());
            userAccountTag.setAttribute("jobTitle", getDefaultIfNull(userAccount.getJobTitle(), ""));
            userAccountTag.setAttribute("description", getDefaultIfNull(userAccount.getDescription(), ""));
            userAccountTag.setAttribute("registrationNo", getDefaultIfNull(userAccount.getRegistrationNo(), ""));
            userAccountTag.setAttribute("vatNo", getDefaultIfNull(userAccount.getVatNo(), ""));
            addCustomFields(userAccount, doc, userAccountTag);
        }
        if (invoiceConfiguration.isDisplaySubscriptions()) {
            Element subscriptionsTag = createSubscriptionsSection(doc, userAccount, ratedTransactions, isVirtual, ignoreUA, null);
            if (subscriptionsTag != null) {
                userAccountTag.appendChild(subscriptionsTag);
            }
        }
        if (userAccount != null) {
            Element nameTag = createNameSection(doc, userAccount, invoiceLanguageCode);
            if (nameTag != null) {
                userAccountTag.appendChild(nameTag);
            }
            Element addressTag = createAddressSection(doc, userAccount, invoiceLanguageCode);
            if (addressTag != null) {
                userAccountTag.appendChild(addressTag);
            }
        }
        userAccountTag.appendChild(categoriesTag);
        return userAccountTag;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/subscriptions DOM element
     *
     * @param doc                  XML invoice DOM
     * @param userAccount          User account
     * @param ratedTransactions    Rated transactions
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param ignoreUA             Shall UA be ignored and all subscriptions be returned. Used as true when aggregation by user account is turned off - system property
     *                             invoice.aggregateByUA=false
     * @return DOM element
     */
    protected Element createSubscriptionsSection(Document doc, UserAccount userAccount, List<RatedTransaction> ratedTransactions, boolean isVirtual, boolean ignoreUA,
                                                 List<InvoiceLine> invoiceLines) {

        List<Subscription> subscriptions = CollectionUtils.isNotEmpty(ratedTransactions) ? getSubscriptions(userAccount, isVirtual, ratedTransactions)
                : getSubscriptionsFromIls(invoiceLines);
        if (subscriptions == null || subscriptions.isEmpty()) {
            return null;
        }
        ParamBean paramBean = paramBeanFactory.getInstance();
        String invoiceDateFormat = paramBean.getProperty(INVOICE_DATE_FORMAT, DEFAULT_DATE_PATTERN);
        String invoiceDateTimeFormat = paramBean.getProperty("invoice.dateTimeFormat", DEFAULT_DATE_TIME_PATTERN);
        Element subscriptionsTag = doc.createElement("subscriptions");
        for (Subscription subscription : subscriptions) {
            if(userAccount == null
                    || (userAccount.getId().equals(subscription.getUserAccount().getId())
                    && userAccount.getCode().equals(subscription.getUserAccount().getCode()))) {
                Element subscriptionTag = doc.createElement("subscription");
                subscriptionTag.setAttribute("id", subscription.getId() + "");
                subscriptionTag.setAttribute("code", subscription.getCode());
                subscriptionTag.setAttribute("description", getDefaultIfNull(subscription.getDescription(), ""));
                subscriptionTag.setAttribute("offerCode", subscription.getOffer().getCode());
                Element subscriptionDateTag = doc.createElement("subscriptionDate");
                subscriptionDateTag.appendChild(this.createTextNode(doc, DateUtils.formatDateWithPattern(subscription.getSubscriptionDate(), invoiceDateFormat)));
                subscriptionTag.appendChild(subscriptionDateTag);
                Element endAgreementTag = doc.createElement("endAgreementDate");
                endAgreementTag.appendChild(this.createTextNode(doc, DateUtils.formatDateWithPattern(subscription.getEndAgreementDate(), invoiceDateTimeFormat)));
                subscriptionTag.appendChild(endAgreementTag);
                addCustomFields(subscription, doc, subscriptionTag);
                subscriptionsTag.appendChild(subscriptionTag);
            }
        }
        return subscriptionsTag;
    }

    /**
     * Create invoice/offers DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param ratedTransactions    Rated transactions
     * @param invoiceLines         Invoice lines
     * @return DOM element
     */
    protected Element createOffersSection(Document doc, Invoice invoice, List<RatedTransaction> ratedTransactions, List<InvoiceLine> invoiceLines) {

        List<OfferTemplate> offers = ratedTransactions != null ? getOffers(ratedTransactions)
                : getOffersFromILs(invoiceLines);
        if (offers == null || offers.isEmpty()) {
            return null;
        }

        String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
        Element offersTag = doc.createElement("offers");
        for (OfferTemplate offerTemplate : offers) {
            Element offerTag = doc.createElement("offer");
            offerTag.setAttribute("id", offerTemplate.getId().toString());
            offerTag.setAttribute("code", offerTemplate.getCode());
            String offerTemplateDescription = offerTemplate.getDescription();
            if (offerTemplate.getDescriptionI18n() != null && offerTemplate.getDescriptionI18n().get(invoiceLanguageCode) != null) {
                offerTemplateDescription = offerTemplate.getDescriptionI18n().get(invoiceLanguageCode);
            }
            offerTag.setAttribute("description", getDefaultIfNull(offerTemplateDescription, ""));
            addCustomFields(offerTemplate, doc, offerTag);
            offersTag.appendChild(offerTag);
        }
        return offersTag;
    }

    /**
     * Create invoice/header/billingAccount/billingCycle DOM element
     *
     * @param doc          XML invoice DOM
     * @param billingCycle Billing cycle
     * @return DOM element
     */
    protected Element createBillingCycleSection(Document doc, BillingCycle billingCycle) {

        Element billingCycleTag = doc.createElement("billingCycle");
        billingCycleTag.setAttribute("id", billingCycle.getId().toString());
        billingCycleTag.setAttribute("code", billingCycle.getCode());
        billingCycleTag.setAttribute("description", getDefaultIfNull(billingCycle.getDescription(), ""));
        addCustomFields(billingCycle, doc, billingCycleTag);
        return billingCycleTag;
    }

    /**
     * Create invoice/services DOM element
     *
     * @param doc                  XML invoice DOM
     * @param ratedTransactions    Rated transactions
     * @param invoiceLines         invoice Lines
     * @return DOM element
     */
    protected Element createServicesSection(Document doc, List<RatedTransaction> ratedTransactions, List<InvoiceLine> invoiceLines) {

        Map<String, List<ServiceInstance>> services = ratedTransactions != null ? getServices(ratedTransactions) : getServicesFromILs(invoiceLines);
        if (services == null || services.isEmpty()) {
            return null;
        }
        Element servicesTag = doc.createElement("services");
        for (Entry<String, List<ServiceInstance>> serviceInfo : services.entrySet()) {
            for (ServiceInstance serviceInstance : serviceInfo.getValue()) {
                Element serviceTag = createServiceSection(doc, serviceInstance, serviceInfo.getKey(), false);
                if (serviceTag != null) {
                    servicesTag.appendChild(serviceTag);
                }
            }
        }
        return servicesTag;
    }

    /**
     * Create invoice/services/service (isShort=false) or invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory/line/service (isShort=true)DOM
     * element
     *
     * @param doc             XML invoice DOM
     * @param serviceInstance Service instance
     * @param offerCode       Offer code
     * @param isShort         If true will not include custom fields
     * @return DOM element
     */
    protected Element createServiceSection(Document doc, ServiceInstance serviceInstance, String offerCode, boolean isShort) {

        Element serviceTag = doc.createElement("service");
        serviceTag.setAttribute("id", serviceInstance.getId().toString());
        serviceTag.setAttribute("code", serviceInstance.getCode());
        serviceTag.setAttribute("offerCode", getDefaultIfNull(offerCode, ""));
        serviceTag.setAttribute("description", getDefaultIfNull(serviceInstance.getDescription(), ""));
        Element calendarTag = doc.createElement("calendar");
        Text calendarText = null;
        if (serviceInstance.getInvoicingCalendar() != null) {
            calendarText = this.createTextNode(doc, serviceInstance.getInvoicingCalendar().getCode());
        } else {
            calendarText = this.createTextNode(doc, "");
        }
        calendarTag.appendChild(calendarText);
        if (!isShort) {
            addCustomFields(serviceInstance, doc, serviceTag, true);
        }
        return serviceTag;
    }

    /**
     * Create invoice/priceplans DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param ratedTransactions    Rated transactions
     * @return DOM element
     */
    protected Element createPricePlansSection(Document doc, Invoice invoice, List<RatedTransaction> ratedTransactions) {

        List<PricePlanMatrix> pricePlans = getPricePlans(ratedTransactions);
        if (pricePlans == null || pricePlans.isEmpty()) {
            return null;
        }
        String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
        Element pricePlansTag = doc.createElement("priceplans");
        for (PricePlanMatrix pricePlan : pricePlans) {
            Element pricePlanTag = createPricePlanSection(doc, pricePlan, false, invoiceLanguageCode);
            if (pricePlanTag != null) {
                pricePlansTag.appendChild(pricePlanTag);
            }
        }
        return pricePlansTag;
    }

    /**
     * Create invoice/priceplans/priceplan (isShort=false) or invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory/line/priceplan (isShort=true)
     * DOM element
     *
     * @param doc                 XML invoice DOM
     * @param pricePlan           Price plan
     * @param isShort             If true will not include custom fields
     * @param invoiceLanguageCode Invoice language - language code
     * @return DOM element
     */
    protected Element createPricePlanSection(Document doc, PricePlanMatrix pricePlan, boolean isShort, String invoiceLanguageCode) {
        Element pricePlanTag = doc.createElement("pricePlan");
        pricePlanTag.setAttribute("code", pricePlan.getCode());
        String translationKey = "PP_" + pricePlan.getCode() + "_" + invoiceLanguageCode;
        String description = descriptionMap.get(translationKey);
        if (description == null) {
            description = pricePlan.getDescription();
            if (pricePlan.getDescriptionI18n() != null && pricePlan.getDescriptionI18n().get(invoiceLanguageCode) != null) {
                description = pricePlan.getDescriptionI18n().get(invoiceLanguageCode);
            }
            descriptionMap.put(translationKey, getDefaultIfNull(description, ""));
        }
        if (!isShort) {
            addCustomFields(pricePlan, doc, pricePlanTag);
        }
        pricePlanTag.setAttribute("description", description);
        return pricePlanTag;
    }

    /**
     * Supplement XML DOM element with custom field information
     *
     * @param entity Entity with custom fields
     * @param doc    XML invoice DOM
     * @param parent Parent XML DOM tag
     */
    protected void addCustomFields(ICustomFieldEntity entity, Document doc, Element parent) {
        addCustomFields(entity, doc, parent, false);
    }

    /**
     * Supplement XML DOM element with custom field information
     *
     * @param entity                  Entity with custom fields
     * @param doc                     XML invoice DOM
     * @param parent                  Parent XML DOM tag
     * @param includeParentCFEntities True to include CFs from parent entities
     */
    protected void addCustomFields(ICustomFieldEntity entity, Document doc, Element parent, boolean includeParentCFEntities) {
        if(entity != null) {
            InvoiceConfiguration invoiceConfiguration = appProvider.getInvoiceConfigurationOrDefault();
            if (invoiceConfiguration.isDisplayCfAsXML()) {
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
    }
    /**
     * Create invoice/header/[Seller,customer,customerAccount,billingAccount, userAccount]/address DOM element
     *
     * @param doc                 XML invoice DOM
     * @param account             Account entity
     * @param invoiceLanguageCode Invoice language - language code
     * @return DOM element
     */
    protected Element createAddressSection(Document doc, AccountEntity account, String invoiceLanguageCode) {

        Element addressTag = doc.createElement("address");
        Element address1 = doc.createElement("address1");
        if (account != null) {
            account = accountEntitySearchService.retrieveIfNotManaged(account);
            if (account.getAddress() != null && account.getAddress().getAddress1() != null) {
                Text adress1Txt = this.createTextNode(doc, account.getAddress().getAddress1());
                address1.appendChild(adress1Txt);
            }
            addressTag.appendChild(address1);
            Element address2 = doc.createElement("address2");
            if (account.getAddress() != null && account.getAddress().getAddress2() != null) {
                Text adress2Txt = this.createTextNode(doc, account.getAddress().getAddress2());
                address2.appendChild(adress2Txt);
            }
            addressTag.appendChild(address2);
            Element address3 = doc.createElement("address3");
            if (account.getAddress() != null && account.getAddress().getAddress3() != null) {
                Text adress3Txt = this.createTextNode(doc, account.getAddress().getAddress3() != null ? account.getAddress().getAddress3() : "");
                address3.appendChild(adress3Txt);
            }
            addressTag.appendChild(address3);
            Element city = doc.createElement("city");
            if (account.getAddress() != null && account.getAddress().getCity() != null) {
                Text cityTxt = this.createTextNode(doc, account.getAddress().getCity() != null ? account.getAddress().getCity() : "");
                city.appendChild(cityTxt);
            }
            addressTag.appendChild(city);
            Element postalCode = doc.createElement("postalCode");
            if (account.getAddress() != null && account.getAddress().getZipCode() != null) {
                Text postalCodeTxt = this.createTextNode(doc, account.getAddress().getZipCode() != null ? account.getAddress().getZipCode() : "");
                postalCode.appendChild(postalCodeTxt);
            }
            addressTag.appendChild(postalCode);
            Element state = doc.createElement("state");
            if (account.getAddress() != null && account.getAddress().getState() != null) {
                Text stateTxt = this.createTextNode(doc, account.getAddress().getState());
                state.appendChild(stateTxt);
            }
            addressTag.appendChild(state);
            Element country = doc.createElement("country");
            Element countryName = doc.createElement("countryName");
            if (account.getAddress() != null && account.getAddress().getCountry() != null) {
                Text countryTxt = this.createTextNode(doc, account.getAddress().getCountry() != null ? account.getAddress().getCountry().getCountryCode() : "");
                country.appendChild(countryTxt);
                String translationKey = "C_" + account.getAddress().getCountry() + "_" + invoiceLanguageCode;
                String descTranslated = descriptionMap.get(translationKey);
                if (descTranslated == null) {
                    Country countrybyCode = account.getAddress().getCountry();
                    if (countrybyCode != null && countrybyCode.getDescriptionI18n() != null && countrybyCode.getDescriptionI18n().get(invoiceLanguageCode) != null) {
                        // get country description by language code
                        descTranslated = countrybyCode.getDescriptionI18n().get(invoiceLanguageCode);
                    } else if (countrybyCode != null) {
                        descTranslated = countrybyCode.getDescription();
                    } else {
                        descTranslated = "";
                    }
                    descriptionMap.put(translationKey, descTranslated);
                }
                Text countryNameTxt = this.createTextNode(doc, descTranslated);
                countryName.appendChild(countryNameTxt);
            }
            addressTag.appendChild(country);
            addressTag.appendChild(countryName);
        }

        return addressTag;
    }

    protected Element createBATaxationCategorySection(Document doc, BillingAccount billingAccount) {
        if (billingAccount != null && billingAccount.getTaxCategory() != null) {
            Element taxationCategoryTag = doc.createElement("taxationCategory");

            if (billingAccount.getTaxCategory().getUntdidTaxationCategory() != null) {
                taxationCategoryTag.setAttribute("code", billingAccount.getTaxCategory().getUntdidTaxationCategory().getCode());
            }

            if (StringUtils.isNotBlank(billingAccount.getExemptionReason())) {
                taxationCategoryTag.setAttribute("taxationCategoryReason", billingAccount.getExemptionReason());
                // in case of exemption reason, add a hardcoded value for vatex attribute
                taxationCategoryTag.setAttribute("vatex", DEFAULT_VATEX_EU_O_VALUE);
            }

            return taxationCategoryTag;
        }

        return null;
    }

    /**
     * Create invoice/header/[customer,customerAccount,billingAccount, userAccount]/name DOM element
     *
     * @param doc                 XML invoice DOM
     * @param account             Account entity
     * @param invoiceLanguageCode Invoice language - language code
     * @return DOM element
     */
    protected Element createNameSection(Document doc, AccountEntity account, String invoiceLanguageCode) {

		Element nameTag = doc.createElement("name");
		if (account != null && account.getIsCompany() && account.getDescription() != null) {
			Element name = doc.createElement("name");
			Text nameTxt = this.createTextNode(doc, account.getDescription());
			name.appendChild(nameTxt);
			nameTag.appendChild(name);
		} else {
			Element quality = doc.createElement("quality");
			if (account != null && account.getName() != null && account.getName().getTitle() != null) {
				String translationKey = "T_" + account.getName().getTitle().getCode() + "_" + invoiceLanguageCode;
				String descTranslated = descriptionMap.get(translationKey);
				if (descTranslated == null) {
					descTranslated = account.getName().getTitle().getDescriptionOrCode();
					if (account.getName().getTitle().getDescriptionI18n() != null && account.getName().getTitle().getDescriptionI18n().get(invoiceLanguageCode) != null) {
						descTranslated = account.getName().getTitle().getDescriptionI18n().get(invoiceLanguageCode);
					}
					descriptionMap.put(translationKey, descTranslated);
				}
				Text titleTxt = this.createTextNode(doc, descTranslated);
				quality.appendChild(titleTxt);
			}
			nameTag.appendChild(quality);
			if (account != null && account.getName() != null && account.getName().getFirstName() != null) {
				Element firstName = doc.createElement("firstName");
				Text firstNameTxt = this.createTextNode(doc, account.getName().getFirstName());
				firstName.appendChild(firstNameTxt);
				nameTag.appendChild(firstName);
			}
			Element name = doc.createElement("name");
			if (account != null && account.getName() != null && account.getName().getLastName() != null) {
				Text nameTxt = this.createTextNode(doc, account.getName().getLastName());
				name.appendChild(nameTxt);
			}
			nameTag.appendChild(name);
		}
        return nameTag;
    }

    /**
     * Create invoice/header/customerAccount/providerContact DOM element
     *
     * @param doc     XML invoice DOM
     * @param account Account entity
     * @return DOM element
     */
    protected Element createProviderContactSection(Document doc, AccountEntity account) {

        if (account.getPrimaryContact() == null) {
            return null;
        }
        Element providerContactTag = doc.createElement("providerContact");
        if (account.getPrimaryContact().getFirstName() != null) {
            Element firstName = doc.createElement("firstName");
            Text firstNameTxt = this.createTextNode(doc, account.getPrimaryContact().getFirstName());
            firstName.appendChild(firstNameTxt);
            providerContactTag.appendChild(firstName);
        }
        if (account.getPrimaryContact().getLastName() != null) {
            Element name = doc.createElement("lastname");
            Text nameTxt = this.createTextNode(doc, account.getPrimaryContact().getLastName());
            name.appendChild(nameTxt);
            providerContactTag.appendChild(name);
        }
        if (account.getPrimaryContact().getEmail() != null) {
            Element email = doc.createElement("email");
            Text emailTxt = this.createTextNode(doc, account.getPrimaryContact().getEmail());
            email.appendChild(emailTxt);
            providerContactTag.appendChild(email);
        }
        if (account.getPrimaryContact().getFax() != null) {
            Element fax = doc.createElement("fax");
            Text faxTxt = this.createTextNode(doc, account.getPrimaryContact().getFax());
            fax.appendChild(faxTxt);
            providerContactTag.appendChild(fax);
        }
        if (account.getPrimaryContact().getMobile() != null) {
            Element mobile = doc.createElement("mobile");
            Text mobileTxt = this.createTextNode(doc, account.getPrimaryContact().getMobile());
            mobile.appendChild(mobileTxt);
            providerContactTag.appendChild(mobile);
        }
        if (account.getPrimaryContact().getPhone() != null) {
            Element phone = doc.createElement("phone");
            Text phoneTxt = this.createTextNode(doc, account.getPrimaryContact().getPhone());
            phone.appendChild(phoneTxt);
            providerContactTag.appendChild(phone);
        }
        return providerContactTag;
    }

    /**
     * Create invoice/header/customerAccount/paymentMethod DOM element
     *
     * @param doc             XML invoice DOM
     * @param customerAccount
     * @return DOM element
     */
    protected Element createPaymentMethodSection(Document doc, CustomerAccount customerAccount) {

        Element paymentMethodTag = doc.createElement("paymentMethod");
        PaymentMethod preferredPaymentMethod = PersistenceUtils.initializeAndUnproxy(customerAccount.getPreferredPaymentMethod());
        if (preferredPaymentMethod != null) {
            paymentMethodTag.setAttribute("type", preferredPaymentMethod.getPaymentType().name());
            paymentMethodTag.setAttribute("paymentMeans", (preferredPaymentMethod.getPaymentMeans() !=null) ? preferredPaymentMethod.getPaymentMeans().getCode() : "");
            paymentMethodTag.setAttribute("paymentMeansLabel", (preferredPaymentMethod.getPaymentMeans() !=null) ? preferredPaymentMethod.getPaymentMeans().getCodeName() : "");

        }
        if (preferredPaymentMethod != null && PaymentMethodEnum.DIRECTDEBIT.equals(preferredPaymentMethod.getPaymentType())) {
            DDPaymentMethod directDebitPayment = (DDPaymentMethod) preferredPaymentMethod;
            BankCoordinates bankCoordinates = directDebitPayment.getBankCoordinates();
            if (bankCoordinates != null) {
                Element bankCoordinatesElement = doc.createElement("bankCoordinates");
                Element bankCode = doc.createElement("bankCode");
                Element branchCode = doc.createElement("branchCode");
                Element accountNumber = doc.createElement("accountNumber");
                Element accountOwner = doc.createElement("accountOwner");
                Element key = doc.createElement("key");
                Element iban = doc.createElement("iban");
                Element bic = doc.createElement("bic");
                Element mandateIdentification = doc.createElement("mandateIdentification");
                Element mandateDate = doc.createElement("mandateDate");
                Element bankName = doc.createElement("bankName");
                bankCoordinatesElement.appendChild(bankCode);
                bankCoordinatesElement.appendChild(branchCode);
                bankCoordinatesElement.appendChild(accountNumber);
                bankCoordinatesElement.appendChild(accountOwner);
                bankCoordinatesElement.appendChild(key);
                bankCoordinatesElement.appendChild(iban);
                bankCoordinatesElement.appendChild(bic);
                bankCoordinatesElement.appendChild(mandateIdentification);
                bankCoordinatesElement.appendChild(mandateDate);
                bankCoordinatesElement.appendChild(bankName);
                paymentMethodTag.appendChild(bankCoordinatesElement);
                String bankCodeData = bankCoordinates.getBankCode();
                Text bankCodeTxt = this.createTextNode(doc, bankCodeData != null ? bankCodeData : "");
                bankCode.appendChild(bankCodeTxt);
                String branchCodeData = bankCoordinates.getBranchCode();
                Text branchCodeTxt = this.createTextNode(doc, branchCodeData != null ? branchCodeData : "");
                branchCode.appendChild(branchCodeTxt);
                String accountNumberData = bankCoordinates.getAccountNumber();
                Text accountNumberTxt = this.createTextNode(doc, accountNumberData != null ? accountNumberData : "");
                accountNumber.appendChild(accountNumberTxt);
                String accountOwnerData = bankCoordinates.getAccountOwner();
                Text accountOwnerTxt = this.createTextNode(doc, accountOwnerData != null ? accountOwnerData : "");
                accountOwner.appendChild(accountOwnerTxt);
                Text keyTxt = this.createTextNode(doc, bankCoordinates.getKey() != null ? bankCoordinates.getKey() : "");
                key.appendChild(keyTxt);
                String ibanData = bankCoordinates.getIban();
                Text ibanTxt = this.createTextNode(doc, ibanData != null ? ibanData : "");
                iban.appendChild(ibanTxt);
                String bicData = bankCoordinates.getBic();
                Text bicTxt = this.createTextNode(doc, bicData != null ? bicData : "");
                bic.appendChild(bicTxt);
                String bankNameData = bankCoordinates.getBankName();
                Text bankNameTxt = this.createTextNode(doc, bankNameData != null ? bankNameData : "");
                bankName.appendChild(bankNameTxt);
                String mandateIdentificationData = directDebitPayment.getMandateIdentification();
                Text mandateIdentificationTxt = this.createTextNode(doc, mandateIdentificationData != null ? mandateIdentificationData : "");
                mandateIdentification.appendChild(mandateIdentificationTxt);
                String mandateDateData = DateUtils.formatDateWithPattern(directDebitPayment.getMandateDate(), DEFAULT_DATE_TIME_PATTERN);
                Text mandateDateTxt = this.createTextNode(doc, mandateDateData != null ? mandateDateData : "");
                mandateDate.appendChild(mandateDateTxt);
            }
        } else if (preferredPaymentMethod != null && PaymentMethodEnum.CARD.equals(preferredPaymentMethod.getPaymentType())) {
            Element cardInformationElement = doc.createElement("cardInformation");
            Element cardType = doc.createElement("cardType");
            Element owner = doc.createElement("owner");
            Element cardNumber = doc.createElement("cardNumber");
            Element expiration = doc.createElement("expiration");
            cardInformationElement.appendChild(cardType);
            cardInformationElement.appendChild(owner);
            cardInformationElement.appendChild(cardNumber);
            cardInformationElement.appendChild(expiration);
            paymentMethodTag.appendChild(cardInformationElement);
            Text cardTypeTxt = this.createTextNode(doc, ((CardPaymentMethod) preferredPaymentMethod).getCardType().name());
            cardType.appendChild(cardTypeTxt);
            Text ownerTxt = this.createTextNode(doc, ((CardPaymentMethod) preferredPaymentMethod).getOwner());
            owner.appendChild(ownerTxt);
            Text cardNumberTxt = this.createTextNode(doc, ((CardPaymentMethod) preferredPaymentMethod).getHiddenCardNumber());
            cardNumber.appendChild(cardNumberTxt);
            Text expirationTxt = this.createTextNode(doc, ((CardPaymentMethod) preferredPaymentMethod).getExpirationMonthAndYear());
            expiration.appendChild(expirationTxt);
        }
        return paymentMethodTag;
    }
//
//    /**
//     * Provide categories elements for min amount transactions
//     *
//     * @param doc XML invoice DOM  dom document
//     * @param ratedTransactions Rated transactions rated transactions
//     * @param enterprise true/false
//     * @return DOM element category element
//     * @throws BusinessException business exception
//     */
//    protected Element getMinAmountRTCategories(Document doc, final List<RatedTransaction> ratedTransactions, final boolean enterprise, String invoiceLanguageCode) throws BusinessException {
//
//        ParamBean paramBean = paramBeanFactory.getInstance();
//        String invoiceDateFormat = paramBean.getProperty("invoice.dateFormat", DEFAULT_DATE_PATTERN);
//        String invoiceDateTimeFormat = paramBean.getProperty("invoice.dateTimeFormat", DEFAULT_DATE_TIME_PATTERN);
//        LinkedHashMap<InvoiceSubCategory, Element> subCategoriesMap = new LinkedHashMap<>();
//        if (ratedTransactions != null) {
//            for (RatedTransaction ratedTransaction : ratedTransactions) {
//                if (ratedTransaction.getWallet() == null) {
//
//                    Element subCategory = null;
//                    InvoiceSubCategory invoiceSubCategory = ratedTransaction.getInvoiceSubCategory();
//                    if (subCategoriesMap.get(invoiceSubCategory) == null) {
//                        subCategoriesMap.put(invoiceSubCategory, doc.createElement("subCategory"));
//                    }
//
//                    subCategory = subCategoriesMap.get(invoiceSubCategory);
//
//                    String subCategoryLabel = invoiceSubCategory.getDescription();
//                    if (invoiceSubCategory.getDescriptionI18n() != null && ratedTransaction.getInvoiceSubCategory().getDescriptionI18n().get(invoiceLanguageCode) != null) {
//                        subCategoryLabel = invoiceSubCategory.getDescriptionI18n().get(invoiceLanguageCode);
//                    }
//
//                    subCategory.setAttribute("label", subCategoryLabel);
//                    subCategory.setAttribute("code", invoiceSubCategory.getCode());
//                    subCategory.setAttribute("amountWithoutTax", toPlainString(ratedTransaction.getAmountWithoutTax()));
//                    subCategory.setAttribute("sortIndex", (invoiceSubCategory.getSortIndex() != null) ? invoiceSubCategory.getSortIndex() + "" : "");
//                    Element line = doc.createElement("line");
//                    Element lebel = doc.createElement("label");
//                    Text lebelTxt = this.createTextNode(doc, ratedTransaction.getDescription());
//                    lebel.appendChild(lebelTxt);
//                    Date periodStartDateRT = ratedTransaction.getStartDate();
//                    Date periodEndDateRT = ratedTransaction.getEndDate();
//
//                    line.setAttribute("periodEndDate", DateUtils.formatDateWithPattern(periodEndDateRT, invoiceDateFormat));
//                    line.setAttribute("periodStartDate", DateUtils.formatDateWithPattern(periodStartDateRT, invoiceDateFormat));
//                    line.setAttribute("sortIndex", ratedTransaction.getSortIndex() != null ? ratedTransaction.getSortIndex() + "" : "");
//
//                    Element lineUnitAmountWithoutTax = doc.createElement("unitAmountWithoutTax");
//                    Text lineUnitAmountWithoutTaxTxt = this.createTextNode(doc, ratedTransaction.getUnitAmountWithoutTax().toPlainString());
//                    lineUnitAmountWithoutTax.appendChild(lineUnitAmountWithoutTaxTxt);
//                    line.appendChild(lineUnitAmountWithoutTax);
//
//                    Element lineAmountWithoutTax = doc.createElement("amountWithoutTax");
//                    Text lineAmountWithoutTaxTxt = this.createTextNode(doc, toPlainString(ratedTransaction.getAmountWithoutTax()));
//                    lineAmountWithoutTax.appendChild(lineAmountWithoutTaxTxt);
//                    line.appendChild(lineAmountWithoutTax);
//
//                    if (!enterprise) {
//                        Element lineAmountWithTax = doc.createElement("amountWithTax");
//                        Text lineAmountWithTaxTxt = this.createTextNode(doc, toPlainString(ratedTransaction.getAmountWithTax()));
//                        lineAmountWithTax.appendChild(lineAmountWithTaxTxt);
//                        line.appendChild(lineAmountWithTax);
//                    }
//
//                    Element quantity = doc.createElement("quantity");
//                    Text quantityTxt = this.createTextNode(doc, ratedTransaction.getQuantity() != null ? ratedTransaction.getQuantity().toPlainString() : "");
//                    quantity.appendChild(quantityTxt);
//                    line.appendChild(quantity);
//                    line.appendChild(lebel);
//                    subCategory.appendChild(line);
//
//                    subCategoriesMap.put(invoiceSubCategory, subCategory);
//                }
//            }
//        }
//
//        LinkedHashMap<InvoiceCategory, Element> categoriesMap = new LinkedHashMap<>();
//        for (Map.Entry<InvoiceSubCategory, Element> entry : subCategoriesMap.entrySet()) {
//            InvoiceSubCategory invoiceSubCategory = entry.getKey();
//            InvoiceCategory invoiceCategory = invoiceSubCategory.getInvoiceCategory();
//            if (categoriesMap.get(invoiceCategory) == null) {
//                Element category = doc.createElement("category");
//                String invoiceCategoryLabel = "";
//                if (invoiceCategory != null) {
//                    invoiceCategoryLabel = invoiceCategory.getDescription();
//                    if (invoiceCategory.getDescriptionI18n() != null && invoiceCategory.getDescriptionI18n().get(invoiceLanguageCode) != null) {
//                        invoiceCategoryLabel = invoiceCategory.getDescriptionI18n().get(invoiceLanguageCode);
//                    }
//                }
//                category.setAttribute("label", invoiceCategoryLabel);
//                category.setAttribute("code", invoiceCategory.getCode());
//                // here
//                category.setAttribute("sortIndex", (invoiceCategory.getSortIndex() != null) ? invoiceCategory.getSortIndex() + "" : "");
//                Element subCategories = doc.createElement("subCategories");
//                category.appendChild(subCategories);
//                categoriesMap.put(invoiceCategory, category);
//            }
//
//            categoriesMap.get(invoiceCategory).getFirstChild().appendChild(entry.getValue());
//        }
//
//        Element categories = doc.createElement("categories");
//        for (Map.Entry<InvoiceCategory, Element> entry : categoriesMap.entrySet()) {
//            categories.appendChild(entry.getValue());
//        }
//        return categories;
//    }

    /**
     * Determine if invoice category aggregate if for a given user account.
     *
     * @param userAccount             User account or null to select categories not associated to a user account
     * @param categoryInvoiceAgregate Category invoice aggregate
     * @return True if user account match
     */
    private boolean isValidCategoryInvoiceAgregate(final UserAccount userAccount, final CategoryInvoiceAgregate categoryInvoiceAgregate) {
        if (userAccount == null) {
            return categoryInvoiceAgregate.getUserAccount() == null;
        } else {
            Long uaId = userAccount.getId();
            return categoryInvoiceAgregate.getUserAccount() != null && uaId != null && uaId.equals(categoryInvoiceAgregate.getUserAccount().getId());
        }
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param userAccount          User account
     * @param ratedTransactions    Rated transactions
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceConfiguration Invoice configuration
     * @return DOM element
     */
    protected Element createUAInvoiceCategories(Document doc, Invoice invoice, UserAccount userAccount, List<RatedTransaction> ratedTransactions, boolean isVirtual, InvoiceConfiguration invoiceConfiguration) {

        ParamBean paramBean = paramBeanFactory.getInstance();
        String invoiceDateFormat = paramBean.getProperty(INVOICE_DATE_FORMAT, DEFAULT_DATE_PATTERN);
        String invoiceDateTimeFormat = paramBean.getProperty("invoice.dateTimeFormat", DEFAULT_DATE_TIME_PATTERN);
        String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
        List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<>();
        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof CategoryInvoiceAgregate && isValidCategoryInvoiceAgregate(userAccount, (CategoryInvoiceAgregate) invoiceAgregate)) {
                CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
                categoryInvoiceAgregates.add(categoryInvoiceAgregate);
            }
        }
        Collections.sort(categoryInvoiceAgregates, InvoiceCategoryComparatorUtils.getInvoiceCategoryComparator());
        Element categoriesTag = doc.createElement("categories");
        Element categoryTag=null;
        for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
        	if(categoryInvoiceAgregate.getInvoiceCategory()!=null) {
        		categoryTag = createDetailsUAInvoiceCategorySection(doc, categoryInvoiceAgregate, ratedTransactions, isVirtual, invoiceDateFormat, invoiceDateTimeFormat, invoiceLanguageCode,
        				invoiceConfiguration);
        	}
            if (categoryTag != null) {
                categoriesTag.appendChild(categoryTag);
            }
        }
        return categoriesTag.getChildNodes().getLength() > 0 ? categoriesTag : null;
    }

    private Map<String, String> buildTaxesIndexes(Invoice invoice) {
        Map<String, String> mapTaxesIndexes = new HashMap<>();
        int i = 1;
        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                if (mapTaxesIndexes.get(((TaxInvoiceAgregate) invoiceAgregate).getTax().getCode()) == null) {
                    mapTaxesIndexes.put(((TaxInvoiceAgregate) invoiceAgregate).getTax().getCode(), String.valueOf(i++));
                }
            }
        }

        return mapTaxesIndexes;
    }

    /**
     * Create invoice/amount/taxes DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @return DOM element
     */
    protected Element createAmountTaxSection(Document doc, Invoice invoice, Map<String, String> mapTaxesIndexes, Set<String> vatexReasons) {

        Element taxes = doc.createElement("taxes");
        if (CollectionUtils.isNotEmpty(vatexReasons)) {
            taxes.setAttribute("vatexReasons", String.join(",", vatexReasons));
        }
        boolean exoneratedFromTaxes = billingAccountService.isExonerated(invoice.getBillingAccount());
        if (exoneratedFromTaxes) {
            Element exoneratedElement = doc.createElement("exonerated");
            exoneratedElement.setAttribute("reason", invoice.getBillingAccount().getCustomerAccount().getCustomer().getCustomerCategory().getExonerationReason());
            taxes.appendChild(exoneratedElement);
        } else {
            taxes.setAttribute("total", toPlainString(invoice.getAmountTax()));
            String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
            List<TaxInvoiceAgregate> taxInvoiceAgregates = new ArrayList<>();
            for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
                if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                    taxInvoiceAgregates.add((TaxInvoiceAgregate) invoiceAgregate);
                }
            }
            taxInvoiceAgregates.sort(Comparator.comparing(ta -> (ta != null && ta.getTax() != null ? ta.getTax().getCode() : "")));
            for (TaxInvoiceAgregate taxInvoiceAgregate : taxInvoiceAgregates) {
                Element tax = doc.createElement("tax");
                Tax taxData = taxInvoiceAgregate.getTax();
                tax.setAttribute("id", taxData != null ? taxData.getId().toString() : "");
                tax.setAttribute("code", taxData != null ? taxData.getCode() : "");
                tax.setAttribute("index", taxData != null ? mapTaxesIndexes.get(taxData.getCode()) : "");
                addCustomFields(taxData, doc, tax);
                String translationKey = "TX_" + (taxData != null ? taxData.getCode() : "") + "_" + invoiceLanguageCode;
                String descTranslated = descriptionMap.get(translationKey);
                if (descTranslated == null) {
                    descTranslated = taxData != null ? taxData.getDescriptionOrCode() : "";
                    if (taxData != null && taxData.getDescriptionI18n() != null && taxData.getDescriptionI18n().get(invoiceLanguageCode) != null) {
                        descTranslated = taxData.getDescriptionI18n().get(invoiceLanguageCode);
                    }
                    descriptionMap.put(translationKey, descTranslated);
                }
                Element taxName = doc.createElement("name");
                taxName.appendChild(this.createTextNode(doc, descTranslated));
                tax.appendChild(taxName);
                Element percent = doc.createElement("percent");
                percent.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getTaxPercent())));
                tax.appendChild(percent);

                Element taxAmount = doc.createElement("amount");
                taxAmount.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getAmountTax())));
                tax.appendChild(taxAmount);

                Element transactionalAmount = doc.createElement("transactionalAmount");
                transactionalAmount.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getTransactionalAmountTax())));
                tax.appendChild(transactionalAmount);

                Element amountWithoutTax = doc.createElement("amountWithoutTax");
                amountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getAmountWithoutTax())));
                tax.appendChild(amountWithoutTax);

                Element transactionalAmountWithoutTax = doc.createElement("transactionalAmountWithoutTax");
                transactionalAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getTransactionalAmountWithoutTax())));
                tax.appendChild(transactionalAmountWithoutTax);

                Element amountWithTax = doc.createElement("amountWithTax");
                amountWithTax.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getAmountWithTax())));
                tax.appendChild(amountWithTax);

                Element transactionalAmountWithTax = doc.createElement("transactionalAmountWithTax");
                transactionalAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(taxInvoiceAgregate.getTransactionalAmountWithTax())));
                tax.appendChild(transactionalAmountWithTax);

                taxes.appendChild(tax);
            }
        }
        return taxes;
    }

    /**
     * Create invoice/header/categories DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @return DOM element
     */
    protected Element createHeaderCategoriesSection(Document doc, Invoice invoice, Map<String, String> mapTaxesIndexes,
                                                    Set<String> vatexReasons) {

    	String billingAccountLanguage = invoice.getBillingAccount().getTradingLanguage().getLanguageCode();
        LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO> headerCategories = new LinkedHashMap<>();
        List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<>();
        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
                categoryInvoiceAgregates.add(categoryInvoiceAgregate);
            }
        }
        Collections.sort(categoryInvoiceAgregates, InvoiceCategoryComparatorUtils.getInvoiceCategoryComparator());
        for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
            InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();
            XMLInvoiceHeaderCategoryDTO headerCat = null;
            if (invoiceCategory != null && headerCategories.containsKey(invoiceCategory.getCode())) {
                headerCat = headerCategories.get(invoiceCategory.getCode());
                headerCat.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
                headerCat.addAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
                headerCat.addAmountTax(categoryInvoiceAgregate.getAmountTax());
                headerCat.addTransactionalAmountWithoutTax(categoryInvoiceAgregate.getTransactionalAmountWithoutTax());
                headerCat.addTransactionalAmountWithTax(categoryInvoiceAgregate.getTransactionalAmountWithTax());
                headerCat.addTransactionalAmountTax(categoryInvoiceAgregate.getTransactionalAmountTax());
            } else {
                headerCat = new XMLInvoiceHeaderCategoryDTO();
                if (invoiceCategory != null
                        && (invoiceCategory.getDescriptionI18n() == null || invoiceCategory.getDescriptionI18n().get(billingAccountLanguage) == null)) {
                    headerCat.setDescription(invoiceCategory.getDescription());
                } else {
                    headerCat.setDescription(invoiceCategory != null ? invoiceCategory.getDescriptionI18n().get(billingAccountLanguage) : "");
                }
                headerCat.setCode(invoiceCategory != null ? invoiceCategory.getCode() : "");
                headerCat.setAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
                headerCat.setAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
                headerCat.setAmountTax(categoryInvoiceAgregate.getAmountTax());
                headerCat.setTransactionalAmountWithoutTax(categoryInvoiceAgregate.getTransactionalAmountWithoutTax());
                headerCat.setTransactionalAmountWithTax(categoryInvoiceAgregate.getTransactionalAmountWithTax());
                headerCat.setTransactionalAmountTax(categoryInvoiceAgregate.getTransactionalAmountTax());
                headerCat.setSortIndex(categoryInvoiceAgregate.getInvoiceCategory() != null
                        && categoryInvoiceAgregate.getInvoiceCategory().getSortIndex() != null
                        ? categoryInvoiceAgregate.getInvoiceCategory().getSortIndex() : 0);
                headerCategories.put(invoiceCategory != null ? invoiceCategory.getCode() : invoice.getCode(), headerCat);
            }
            List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<>(categoryInvoiceAgregate.getSubCategoryInvoiceAgregates());
            Collections.sort(subCategoryInvoiceAgregates, InvoiceCategoryComparatorUtils.getInvoiceSubCategoryComparator());
            for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
                if (!subCatInvoiceAgregate.isDiscountAggregate()) {
                    headerCat.getSubCategoryInvoiceAgregates().add(subCatInvoiceAgregate);
                }
            }
        }
        Element categories = doc.createElement("categories");
        for (XMLInvoiceHeaderCategoryDTO xmlInvoiceHeaderCategoryDTO : headerCategories.values()) {
            Element category = doc.createElement("category");
            category.setAttribute("label", xmlInvoiceHeaderCategoryDTO.getDescription());
            category.setAttribute("code", xmlInvoiceHeaderCategoryDTO.getCode() != null ? xmlInvoiceHeaderCategoryDTO.getCode() : "");
            category.setAttribute("sortIndex", xmlInvoiceHeaderCategoryDTO.getSortIndex() != null ? xmlInvoiceHeaderCategoryDTO.getSortIndex() + "" : "");

            Element amountWithoutTax = doc.createElement("amountWithoutTax");
            amountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(xmlInvoiceHeaderCategoryDTO.getAmountWithoutTax())));
            category.appendChild(amountWithoutTax);

            Element transactionalAmountWithoutTax = doc.createElement("transactionalAmountWithoutTax");
            transactionalAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(xmlInvoiceHeaderCategoryDTO.getTransactionalAmountWithoutTax())));
            category.appendChild(transactionalAmountWithoutTax);

            Element amountWithTax = doc.createElement("amountWithTax");
            amountWithTax.appendChild(this.createTextNode(doc, toPlainString(xmlInvoiceHeaderCategoryDTO.getAmountWithTax())));
            category.appendChild(amountWithTax);

            Element transactionalAmountWithTax = doc.createElement("transactionalAmountWithTax");
            transactionalAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(xmlInvoiceHeaderCategoryDTO.getTransactionalAmountWithTax())));
            category.appendChild(transactionalAmountWithTax);

            Element amountTax = doc.createElement("amountTax");
            amountTax.appendChild(this.createTextNode(doc, toPlainString(xmlInvoiceHeaderCategoryDTO.getAmountTax())));
            category.appendChild(amountTax);

            Element transactionalAmountTax = doc.createElement("transactionalAmountTax");
            transactionalAmountTax.appendChild(this.createTextNode(doc, toPlainString(xmlInvoiceHeaderCategoryDTO.getTransactionalAmountTax())));
            category.appendChild(transactionalAmountTax);

            if (xmlInvoiceHeaderCategoryDTO.getSubCategoryInvoiceAgregates() != null) {
                Element subCategories = doc.createElement("subCategories");
                for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : xmlInvoiceHeaderCategoryDTO.getSubCategoryInvoiceAgregates()) {
                    Element subCategory = doc.createElement("subCategory");
                    InvoiceSubCategory invoiceSubCat = subCatInvoiceAgregate.getInvoiceSubCategory();
                    // description translated is set on aggregate
                    String invoiceSubCategoryLabel = subCatInvoiceAgregate.getDescription();
                    if (invoiceSubCat != null && invoiceSubCat.getDescriptionI18n() != null
                            && invoiceSubCat.getDescriptionI18n().get(billingAccountLanguage) != null) {
                        // get label description by language code
                        invoiceSubCategoryLabel = invoiceSubCat.getDescriptionI18n().get(billingAccountLanguage);
                    }
                    subCategory.setAttribute("label", getDefaultIfNull(invoiceSubCategoryLabel, ""));
                    subCategory.setAttribute("code", invoiceSubCat != null ? invoiceSubCat.getCode() : "");
                    subCategory.setAttribute("amountWithTax", toPlainString(subCatInvoiceAgregate.getAmountWithTax()));
                    subCategory.setAttribute("transactionalAmountWithTax", toPlainString(subCatInvoiceAgregate.getTransactionalAmountWithTax()));
                    subCategory.setAttribute("amountWithoutTax", toPlainString(subCatInvoiceAgregate.getAmountWithoutTax()));
                    subCategory.setAttribute("transactionalAmountWithoutTax", toPlainString(subCatInvoiceAgregate.getTransactionalAmountWithoutTax()));
                    subCategory.setAttribute("amountTax", toPlainString(subCatInvoiceAgregate.getAmountTax()));
                    subCategory.setAttribute("transactionalAmountTax", toPlainString(subCatInvoiceAgregate.getTransactionalAmountTax()));
                    subCategory.setAttribute("sortIndex",
                            subCatInvoiceAgregate.getInvoiceSubCategory() != null && subCatInvoiceAgregate.getInvoiceSubCategory().getSortIndex() != null
                                    ? subCatInvoiceAgregate.getInvoiceSubCategory().getSortIndex().toString()
                                    : "");
                    if (subCatInvoiceAgregate.getAmountsByTax() != null && !subCatInvoiceAgregate.getAmountsByTax().isEmpty()) {
                        Element amountsByTaxXml = doc.createElement("amountsByTax");
                        subCategory.appendChild(amountsByTaxXml);
                        for (Entry<Tax, SubcategoryInvoiceAgregateAmount> amountByTax : subCatInvoiceAgregate.getAmountsByTax().entrySet()) {
                            Element amountByTaxXml = doc.createElement("amountByTax");
                            amountByTaxXml.setAttribute("amountWithoutTax", toPlainString(amountByTax.getValue().getAmountWithoutTax()));
                            amountByTaxXml.setAttribute("amountWithTax", toPlainString(amountByTax.getValue().getAmountWithTax()));
                            amountByTaxXml.setAttribute("amountTax", toPlainString(amountByTax.getValue().getAmountTax()));
                            amountByTaxXml.setAttribute("taxCode", amountByTax.getKey().getCode());
                            amountByTaxXml.setAttribute("taxIndex", mapTaxesIndexes.get(amountByTax.getKey().getCode()));
                            amountByTaxXml.setAttribute("taxDescription", amountByTax.getKey().getDescription());
                            amountByTaxXml.setAttribute("taxationCategory", amountByTax.getKey().getUntdidTaxationCategory() != null ? amountByTax.getKey().getUntdidTaxationCategory().getCode() : "");
                            amountByTaxXml.setAttribute("taxationCategoryLabel", amountByTax.getKey().getUntdidTaxationCategory() != null ? amountByTax.getKey().getUntdidTaxationCategory().getName() : "");
                            if (amountByTax.getKey().getUntdidVatex() != null) {
                                vatexReasons.add(amountByTax.getKey().getUntdidVatex().getCode());
                                amountByTaxXml.setAttribute("vatex", amountByTax.getKey().getUntdidVatex().getCode());
                            } else {
                                amountByTaxXml.setAttribute("vatex", "");
                            }
                            amountByTaxXml.setAttribute("taxPercent", toPlainString(amountByTax.getKey().getPercent()));
                            amountsByTaxXml.appendChild(amountByTaxXml);
                        }
                    }
                    subCategories.appendChild(subCategory);
                }
                category.appendChild(subCategories);
            }
            categories.appendChild(category);
        }
        return categories;
    }

    /**
     * Create invoice/header/discounts DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @return DOM element
     */
    protected Element createDiscountsSection(Document doc, Invoice invoice) {

        Element discounts = doc.createElement("discounts");
        List<SubCategoryInvoiceAgregate> discountInvoiceAgregates = invoice.getDiscountAgregates();

        for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : discountInvoiceAgregates) {
            Element discount = doc.createElement("discount");
            discount.setAttribute("discountPlanCode", subCategoryInvoiceAgregate.getDiscountPlanItem().getDiscountPlan().getCode());
            discount.setAttribute("discountPlanDescription", getDefaultIfNull(subCategoryInvoiceAgregate.getDiscountPlanItem().getDiscountPlan().getDescription(), ""));
            discount.setAttribute("discountPlanItemCode", subCategoryInvoiceAgregate.getDiscountPlanItem().getCode());
            discount.setAttribute("invoiceSubCategoryCode", subCategoryInvoiceAgregate.getInvoiceSubCategory().getCode());
            discount.setAttribute("discountAmountWithoutTax", toPlainString(subCategoryInvoiceAgregate.getAmountWithoutTax()));
            discount.setAttribute("discountTransactionalAmountWithoutTax", toPlainString(subCategoryInvoiceAgregate.getTransactionalAmountWithoutTax()));
            discount.setAttribute("discountAmountWithTax", toPlainString(subCategoryInvoiceAgregate.getAmountWithTax()));
            discount.setAttribute("discountTransactionalAmountWithTax", toPlainString(subCategoryInvoiceAgregate.getTransactionalAmountWithTax()));
            discount.setAttribute("discountAmountTax", toPlainString(subCategoryInvoiceAgregate.getAmountTax()));
            discount.setAttribute("discountTransactionalAmountTax", toPlainString(subCategoryInvoiceAgregate.getTransactionalAmountTax()));
            discount.setAttribute("discountPercent", toPlainString(subCategoryInvoiceAgregate.getDiscountPercent()));
            discounts.appendChild(discount);
        }
        return discounts;
    }

    /**
     * Get linked invoices as a space separated list of invoice numbers
     *
     * @param linkedInvoices Linked invoices
     * @return A space separated list of invoice numbers
     */
    private String getLinkedInvoicesnumberAsString(List<LinkedInvoice> linkedInvoices) {
        if (linkedInvoices == null || linkedInvoices.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (LinkedInvoice inv : linkedInvoices) {
            result.append(inv.getLinkedInvoiceValue().getInvoiceNumber()).append(" ");
        }
        return result.toString();
    }

    private String getLinkedInvoicesNumberAndDateAsString(List<LinkedInvoice> linkedInvoices) {
        if (CollectionUtils.isEmpty(linkedInvoices)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder result = new StringBuilder();

        String prefix = "";
        for (LinkedInvoice inv : linkedInvoices) {
            result.append(prefix);
            prefix = ",";
            String dateFormat = "MM/dd/yyyy";
            if (inv.getInvoice().getTradingCountry() != null) {
                dateFormat = "US".equalsIgnoreCase(inv.getInvoice().getTradingCountry().getCode()) ? "MM/dd/yyyy" : "dd/MM/yyyy";
            }
            result.append(inv.getInvoice().getStatus() == InvoiceStatusEnum.VALIDATED ? inv.getInvoice().getInvoiceNumber() : "Draft")
                    .append(" (invoiced on ")
                    .append(DateUtils.formatDateWithPattern(inv.getInvoice().getInvoiceDate(), dateFormat))
                    .append(")");
        }
        return result.toString();
    }

    private String getTaxCodes(Tax tax) {
        if (CollectionUtils.isEmpty(tax.getSubTaxes())) {
            return tax.getCode().replaceAll(",", "\n");
        } else {
            return getSubTaxesCodes(tax.getSubTaxes());
        }
    }

    private String getTaxIndexes(Tax tax, Map<String, String> mapTaxesIndexes) {
        if (CollectionUtils.isEmpty(tax.getSubTaxes())) {
            String taxIndex = mapTaxesIndexes.get(tax.getCode());
            return StringUtils.isNotBlank(taxIndex) ? taxIndex.replaceAll(",", "\n") : StringUtils.EMPTY;
        } else {
            return getSubTaxesIndexes(tax.getSubTaxes(), mapTaxesIndexes);
        }
    }

    private String getSubTaxesCodes(List<Tax> subTaxes) {
        if (CollectionUtils.isEmpty(subTaxes)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder result = new StringBuilder();

        String prefix = "";
        for (Tax subTax : subTaxes) {
            result.append(prefix);
            prefix = ",";

            if (CollectionUtils.isNotEmpty(subTax.getSubTaxes())) {
                result.append(getSubTaxesCodes(subTax.getSubTaxes()));
            } else {
                result.append(subTax.getCode());
            }
        }
        return result.toString();
    }

    private String getSubTaxesIndexes(List<Tax> subTaxes, Map<String, String> mapTaxesIndexes) {
        if (CollectionUtils.isEmpty(subTaxes)) {
            return StringUtils.EMPTY;
        }

        List<String> results = new ArrayList<>();

        for (Tax subTax : subTaxes) {
            if (CollectionUtils.isNotEmpty(subTax.getSubTaxes())) {
                results.add(getSubTaxesIndexes(subTax.getSubTaxes(), mapTaxesIndexes));
            } else {
                results.add(mapTaxesIndexes.get(subTax.getCode()));
            }
        }
        return results.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Creates a root element of xml - Invoice tag
     *
     * @param doc                 XML invoice DOM
     * @param invoice             Invoice to convert
     * @param isVirtual           Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param isInvoiceAdjustment Is this an adjustment invoice
     * @return DOM element DOM element
     */
    protected Element createInvoiceSection(Document doc, Invoice invoice, boolean isVirtual, boolean isInvoiceAdjustment,
                                           DocumentBuilder docBuilder, Map<String, String> mapTaxesIndexes) {
        InvoiceConfiguration invoiceConfiguration = appProvider.getInvoiceConfigurationOrDefault();
        Element invoiceTag = initInvoiceTag(doc, invoice, isInvoiceAdjustment, docBuilder, invoiceConfiguration, mapTaxesIndexes);
        List<RatedTransaction> ratedTransactions = null;
        if (invoiceConfiguration.isDisplayOffers() || invoiceConfiguration.isDisplayServices() || invoiceConfiguration.isDisplayPricePlans() || invoiceConfiguration.isDisplayDetail()) {
            ratedTransactions = getRatedTransactions(invoice, isVirtual);
        }
        if (invoiceConfiguration.isDisplayOffers()) {
            Element offersTag = createOffersSection(doc, invoice, ratedTransactions, null);
            if (offersTag != null) {
                invoiceTag.appendChild(offersTag);
            }
        }
        if (invoiceConfiguration.isDisplayServices()) {
            Element servicesTag = createServicesSection(doc, ratedTransactions, null);
            if (servicesTag != null) {
                invoiceTag.appendChild(servicesTag);
            }
        }
        if (invoiceConfiguration.isDisplayPricePlans()) {
            Element pricePlansTag = createPricePlansSection(doc, invoice, ratedTransactions);
            if (pricePlansTag != null) {
                invoiceTag.appendChild(pricePlansTag);
            }
        }
        if (invoiceConfiguration.isDisplayDetail()) {
            Element details = createDetailsSection(doc, invoice, ratedTransactions, isVirtual, invoiceConfiguration, null, isInvoiceAdjustment, mapTaxesIndexes);
            if (details != null) {
                invoiceTag.appendChild(details);
            }
        }
        return invoiceTag;
    }

    private Element initInvoiceTag(Document doc, Invoice invoice, boolean isInvoiceAdjustment,
                                   DocumentBuilder docBuilder, InvoiceConfiguration invoiceConfiguration,
                                   Map<String, String> mapTaxesIndexes) {
        String invoiceDateFormat = paramBeanFactory.getInstance().getProperty(INVOICE_DATE_FORMAT, DEFAULT_DATE_PATTERN);

        Element invoiceTag = doc.createElement("invoice");
        invoiceTag.setAttribute("number", invoice.getInvoiceNumber());
        invoiceTag.setAttribute("type", invoice.getInvoiceType().getCode());
        invoiceTag.setAttribute("invoiceCounter", invoice.getAlias());
        invoiceTag.setAttribute("id", invoice.getId() != null ? invoice.getId().toString() : "");
        invoiceTag.setAttribute("invoiceCodeType", invoice.getInvoiceType().getUntdidInvoiceCodeType() != null ? invoice.getInvoiceType().getUntdidInvoiceCodeType().getCode() : "");
        invoiceTag.setAttribute("invoiceCodeTypeLabel", invoice.getInvoiceType().getUntdidInvoiceCodeType() != null ? invoice.getInvoiceType().getUntdidInvoiceCodeType().getInterpretation16931() : "");
        invoiceTag.setAttribute("invoiceSubjectCode", invoice.getInvoiceType().getInvoiceSubjectCode() != null ? invoice.getInvoiceType().getInvoiceSubjectCode().getCode() : "");
        invoiceTag.setAttribute("country", invoice.getTradingCountry() != null ? invoice.getTradingCountry().getCode() : "");
        invoiceTag.setAttribute("currency", (invoice.getTradingCurrency() != null && invoice.getTradingCurrency().getCurrency() != null) ? invoice.getTradingCurrency().getCurrency().getCurrencyCode() : "");
        invoiceTag.setAttribute("customerId", invoice.getBillingAccount().getCustomerAccount().getCustomer().getCode());
        invoiceTag.setAttribute("customerAccountCode", invoice.getBillingAccount().getCustomerAccount().getCode());
        invoiceTag.setAttribute("status", invoice.getStatus() != null ? invoice.getStatus().name() : "");
        invoiceTag.setAttribute("paymentStatus", invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().name() : "");
        invoiceTag.setAttribute("paymentStatusDate", invoice.getPaymentStatusDate() != null ? DateUtils.formatDateWithPattern(invoice.getPaymentStatusDate(), invoiceDateFormat) : "");
        ofNullable(invoice.getOpenOrderNumber()).ifPresent(oon -> invoiceTag.setAttribute("openOrderNumber", oon));
        ofNullable(invoice.getOrder()).ifPresent(order -> invoiceTag.setAttribute("orderNumber", order.getOrderNumber()));
        ofNullable(invoice.getExternalRef()).ifPresent(externalRef
                -> invoiceTag.setAttribute("externalReference", externalRef));
        if (isInvoiceAdjustment) {
            Set<LinkedInvoice> linkedInvoices = invoice.getLinkedInvoices();
            invoiceTag.setAttribute("adjustedInvoiceNumber", getLinkedInvoicesnumberAsString(new ArrayList<>(linkedInvoices)));
            invoiceTag.setAttribute("previousInvoices", getLinkedInvoicesNumberAndDateAsString(new ArrayList<>(linkedInvoices)));
        }
        BillingCycle billingCycle = null;
        LinkedInvoice linkedInvoice = invoiceService.getLinkedInvoice(invoice);
        if (isInvoiceAdjustment && linkedInvoice != null && linkedInvoice.getLinkedInvoiceValue().getBillingRun() != null) {
            billingCycle = linkedInvoice.getLinkedInvoiceValue().getBillingRun().getBillingCycle();
        } else {
            if (invoice.getBillingRun() != null && invoice.getBillingRun().getBillingCycle() != null) {
                billingCycle = invoice.getBillingRun().getBillingCycle();
            }
        }
        String billingTemplateName = invoiceService.getInvoiceTemplateName(invoice, billingCycle, invoice.getInvoiceType());
        invoiceTag.setAttribute("templateName", billingTemplateName);
        addCustomFields(invoice, doc, invoiceTag);
        Set<String> vatexReasons = new HashSet<>(); // Set of all untdidVatex grouped to ease way of displaying in Jasper
        Element header = createHeaderSection(doc, invoice, isInvoiceAdjustment, invoiceConfiguration,
                invoiceDateFormat, mapTaxesIndexes, vatexReasons);
        if (header != null) {
            invoiceTag.appendChild(header);
        }
        Element amountTag = createAmountSection(doc, invoice, mapTaxesIndexes, vatexReasons);
        if (amountTag != null) {
            invoiceTag.appendChild(amountTag);
        }
        if (invoiceConfiguration.isDisplayOrders()) {
            Element ordersTag = createOrdersSection(doc, invoice, docBuilder);
            if (ordersTag != null) {
                invoiceTag.appendChild(ordersTag);
            }
        }
        return invoiceTag;
    }

    /**
     * Create invoice/header/billingAccount DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param linkedInvoice        Linked invoice
     * @param isInvoiceAdjustment  Is this adjustment invoice
     * @param invoiceConfiguration Invoice configuration
     * @return DOM element
     */
    protected Element createBillingAccountSection(Document doc, Invoice invoice, Invoice linkedInvoice, boolean isInvoiceAdjustment, InvoiceConfiguration invoiceConfiguration) {

        Element billingAccountTag = doc.createElement("billingAccount");
        BillingAccount billingAccount = invoice.getBillingAccount();
        BillingCycle billingCycle = null;
        if (isInvoiceAdjustment && linkedInvoice != null && linkedInvoice.getBillingRun() != null) {
            billingCycle = linkedInvoice.getBillingRun().getBillingCycle();
        } else {
            if (invoice.getBillingRun() != null && invoice.getBillingRun().getBillingCycle() != null) {
                billingCycle = invoice.getBillingRun().getBillingCycle();
            }
        }
        if (billingCycle == null) {
            billingCycle = billingAccount.getBillingCycle();
        }
        billingAccountTag.setAttribute("billingCycleCode", billingCycle != null ? billingCycle.getCode() : "");
        billingAccountTag.setAttribute("id", billingAccount.getId().toString());
        billingAccountTag.setAttribute("code", billingAccount.getCode());
        billingAccountTag.setAttribute("description", getDefaultIfNull(billingAccount.getDescription(), ""));
        billingAccountTag.setAttribute("externalRef1", getDefaultIfNull(billingAccount.getExternalRef1(), ""));
        billingAccountTag.setAttribute("externalRef2", getDefaultIfNull(billingAccount.getExternalRef2(), ""));
        billingAccountTag.setAttribute("jobTitle", getDefaultIfNull(billingAccount.getJobTitle(), ""));
        billingAccountTag.setAttribute("registrationNo", getDefaultIfNull(billingAccount.getRegistrationNo(), ""));
        billingAccountTag.setAttribute("vatPaymentOption", invoice.getInvoiceType().getUntdidVatPaymentOption() != null ? invoice.getInvoiceType().getUntdidVatPaymentOption().getCode2475() : "");
        billingAccountTag.setAttribute("icd", billingAccount.getIcdId() != null ? billingAccount.getIcdId().getCode() : "");
        billingAccountTag.setAttribute("vatNo", getDefaultIfNull(billingAccount.getVatNo(), ""));
        if (invoiceConfiguration.isDisplayBillingCycle()) {
            Element bcTag = createBillingCycleSection(doc, billingCycle);
            if (bcTag != null) {
                billingAccountTag.appendChild(bcTag);
            }
        }
        addCustomFields(billingAccount, doc, billingAccountTag);
        Element email = doc.createElement("email");
        Element phone = doc.createElement("phone");
        Element mobile = doc.createElement("mobile");
        if (billingAccount.getContactInformation() != null) {
            // Email
            String billingEmail = billingAccount.getContactInformation().getEmail();
            Text emailTxt = this.createTextNode(doc, billingEmail != null ? billingEmail : "");
            email.appendChild(emailTxt);
            billingAccountTag.appendChild(email);

            // Phone
            String billingPhone = billingAccount.getContactInformation().getPhone();
            Text phoneTxt = this.createTextNode(doc, billingPhone != null ? billingPhone : "");
            phone.appendChild(phoneTxt);
            billingAccountTag.appendChild(phone);

            // Mobile
            String billingMobile = billingAccount.getContactInformation().getMobile();
            Text mobileTxt = this.createTextNode(doc, billingMobile != null ? billingMobile : "");
            mobile.appendChild(mobileTxt);
            billingAccountTag.appendChild(mobile);
        }

        Element nameTag = createNameSection(doc, billingAccount, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (nameTag != null) {
            billingAccountTag.appendChild(nameTag);
        }
        Element addressTag = createAddressSection(doc, billingAccount, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (addressTag != null) {
            billingAccountTag.appendChild(addressTag);
        }
        Element taxationCategorySection = createBATaxationCategorySection(doc, billingAccount);
        if (taxationCategorySection != null) {
            billingAccountTag.appendChild(taxationCategorySection);
        }
        return billingAccountTag;
    }

    /**
     * Create invoice/header/customerAccount DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @return DOM element
     */
    protected Element createCustomerAccountSection(Document doc, Invoice invoice) {

        Element customerAccountTag = doc.createElement("customerAccount");
        CustomerAccount customerAccount = invoice.getBillingAccount().getCustomerAccount();
        TradingLanguage tradingLanguage = customerAccount.getTradingLanguage();
        String languageDescription = null;
        if (tradingLanguage != null) {
            languageDescription = tradingLanguage.getLanguage().getDescriptionEn();
        }
        customerAccountTag.setAttribute("id", customerAccount.getId().toString());
        customerAccountTag.setAttribute("code", customerAccount.getCode());
        customerAccountTag.setAttribute("description", customerAccount.getDescription());
        customerAccountTag.setAttribute("externalRef1", getDefaultIfNull(customerAccount.getExternalRef1(), ""));
        customerAccountTag.setAttribute("externalRef2", getDefaultIfNull(customerAccount.getExternalRef2(), ""));
//        customerAccountTag.setAttribute("currency", getDefaultIfNull(invoice.getBillingAccount().getTradingCurrency().getCurrencyCode(), ""));
//        customerAccountTag.setAttribute("currencySymbol", getDefaultIfNull(invoice.getBillingAccount().getTradingCurrency().getSymbol(), ""));
//        customerAccountTag.setAttribute("currencyLabel", getDefaultIfNull(invoice.getBillingAccount().getTradingCurrency().getPrDescription(), ""));
        customerAccountTag.setAttribute("language", getDefaultIfNull(languageDescription, ""));
        customerAccountTag.setAttribute("jobTitle", getDefaultIfNull(customerAccount.getJobTitle(), ""));
//        customerAccountTag.setAttribute("registrationNo", getDefaultIfNull(customerAccount.getRegistrationNo(), ""));
//        customerAccountTag.setAttribute("vatNo", getDefaultIfNull(customerAccount.getVatNo(), ""));
        addCustomFields(customerAccount, doc, customerAccountTag);
        customerAccountTag.appendChild(toContactTag(doc, customerAccount.getContactInformation()));
        customerAccountTag.setAttribute("accountTerminated", customerAccount.getStatus().equals(CustomerAccountStatusEnum.CLOSE) + "");
        Element paymentMethodTag = createPaymentMethodSection(doc, customerAccount);
        if (paymentMethodTag != null) {
            customerAccountTag.appendChild(paymentMethodTag);
        }
        Element nameTag = createNameSection(doc, customerAccount, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (nameTag != null) {
            customerAccountTag.appendChild(nameTag);
        }
        Element addressTag = createAddressSection(doc, customerAccount, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (addressTag != null) {
            customerAccountTag.appendChild(addressTag);
        }
        Element contactTag = createProviderContactSection(doc, customerAccount);
        if (contactTag != null) {
            customerAccountTag.appendChild(contactTag);
        }
        return customerAccountTag;
    }

    /**
     * Create invoice/header/seller DOM element
     *
     * @param doc     XML invoice DOM
     * @param invoice Invoice to convert
     * @return DOM element
     */
    protected Element createSellerSection(Document doc, Invoice invoice) {

        Seller seller = invoice.getSeller();
        if (seller == null) {
            return null;
        }
        Element sellerTag = doc.createElement("seller");
        sellerTag.setAttribute("code", seller.getCode());
        sellerTag.setAttribute("description", getDefaultIfNull(seller.getDescription(), ""));
        sellerTag.setAttribute("legalType", getDefaultIfNull(seller.getLegalType(), ""));
        sellerTag.setAttribute("vatNo", getDefaultIfNull(seller.getVatNo(), ""));
        sellerTag.setAttribute("registrationNo", getDefaultIfNull(seller.getRegistrationNo(), ""));
        addCustomFields(seller, doc, sellerTag);
        Element addressTag = createAddressSection(doc, seller, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (addressTag != null) {
            sellerTag.appendChild(addressTag);
        }
        sellerTag.appendChild(toContactTag(doc, seller.getContactInformation()));
        return sellerTag;
    }

    /**
     * Create invoice/header/customer DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @return DOM element
     */
    protected Element createCustomerSection(Document doc, Invoice invoice) {

    	Customer customer = invoice.getBillingAccount().getCustomerAccount().getCustomer();
        CustomerBrand customerBrand = customer.getCustomerBrand();
        Seller customerSeller = customer.getSeller();
        CustomerCategory customerCategory = customer.getCustomerCategory();
        Element customerTag = doc.createElement("customer");
        customerTag.setAttribute("id", customer.getId() + "");
        customerTag.setAttribute("code", customer.getCode());
        customerTag.setAttribute("externalRef1", getDefaultIfNull(customer.getExternalRef1(), ""));
        customerTag.setAttribute("externalRef2", getDefaultIfNull(customer.getExternalRef2(), ""));
        if (customerSeller != null) {
            customerTag.setAttribute("sellerCode", customerSeller.getCode());
        }
        customerTag.setAttribute("brand", customerBrand != null ? customerBrand.getCode() : "");
        customerTag.setAttribute("category", customerCategory != null ? customerCategory.getCode() : "");
//        customerTag.setAttribute("vatNo", getDefaultIfNull(customer.getVatNo(), ""));
//        customerTag.setAttribute("registrationNo", getDefaultIfNull(customer.getRegistrationNo(), ""));
        customerTag.setAttribute("jobTitle", getDefaultIfNull(customer.getJobTitle(), ""));
        customerTag.setAttribute("contracts", getDefaultIfNull(buildContactsCodes(customer.getContracts()), ""));
        addCustomFields(customer, doc, customerTag);
        Element nameTag = createNameSection(doc, customer, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (nameTag != null) {
            customerTag.appendChild(nameTag);
        }
        Element addressTag = createAddressSection(doc, customer, invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode());
        if (addressTag != null) {
            customerTag.appendChild(addressTag);
        }
        customerTag.appendChild(toContactTag(doc, customer.getContactInformation()));
        return customerTag;
    }

    private String buildContactsCodes(List<Contract> contracts) {
        if(CollectionUtils.isEmpty(contracts)){
            return StringUtils.EMPTY;
        }

        final StringBuilder result = new StringBuilder();

        String prefix = "";
        for (Contract contract : contracts) {
            result.append(prefix);
            prefix = ",";
            result.append(contract.getCode());
        }

        return result.toString();
    }

    /**
     * Create invoice/header/provider DOM element
     *
     * @param doc                  XML invoice DOM
     * @param provider             Provider
     * @return DOM element
     */
    protected Element createProviderSection(Document doc, Provider provider) {

        Element providerTag = doc.createElement("provider");
        providerTag.setAttribute("code", provider.getCode());
        providerTag.setAttribute("description", provider.getDescription());
        BankCoordinates appBankCoordinates = provider.getBankCoordinates();
        if (appBankCoordinates != null) {
            Element bankCoordinates = doc.createElement("bankCoordinates");
            Element ics = doc.createElement("ics");
            Element iban = doc.createElement("iban");
            Element bic = doc.createElement("bic");
            String bankIcs = appBankCoordinates.getIcs();
            Text icsTxt = this.createTextNode(doc, bankIcs != null ? bankIcs : "");
            ics.appendChild(icsTxt);
            String bankIban = appBankCoordinates.getIban();
            Text ibanTxt = this.createTextNode(doc, bankIban != null ? bankIban : "");
            iban.appendChild(ibanTxt);
            String bankBic = appBankCoordinates.getBic();
            Text bicTxt = this.createTextNode(doc, bankBic != null ? bankBic : "");
            bic.appendChild(bicTxt);
            bankCoordinates.appendChild(ics);
            bankCoordinates.appendChild(iban);
            bankCoordinates.appendChild(bic);
            providerTag.appendChild(bankCoordinates);
        }
        return providerTag;
    }

    /**
     * Create invoice/orders DOM element
     * @param invoice              Invoice to convert
     * @param doc                  XML invoice DOM Builder
     * @return DOM element
     */
    protected Element createOrdersSection(Document doc, Invoice invoice, DocumentBuilder docBuilder) {

        Element ordersTag = doc.createElement("orders");
        List<Order> orders = invoice.getOrders();
        for (Order order : orders) {
            Element orderTag = doc.createElement("order");
            orderTag.setAttribute("orderNumber", order.getCode());
            orderTag.setAttribute("externalId", getDefaultIfNull(order.getExternalId(), ""));
            orderTag.setAttribute("orderDate", DateUtils.formatDateWithPattern(order.getOrderDate(), DEFAULT_DATE_TIME_PATTERN));
            orderTag.setAttribute("orderStatus", order.getStatus().name());
            orderTag.setAttribute("deliveryInstructions", order.getDeliveryInstructions());
            Element orderItemsTag = doc.createElement("orderItems");
            for (OrderItem orderItem : order.getOrderItems()) {
                String orderItemContent = orderItem.getSource().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
                try {
                    Element orderItemElement = docBuilder.parse(new ByteArrayInputStream(orderItemContent.getBytes())).getDocumentElement();
                    Element firstDocImportedNode = (Element) doc.importNode(orderItemElement, true);
                    orderItemsTag.appendChild(firstDocImportedNode);
                } catch (SAXException | IOException e) {
                    log.error("Failed to parse an order xml for order {}", order.getId(), e);
                }
            }
            orderTag.appendChild(orderItemsTag);
            addCustomFields(order, doc, orderTag);
            ordersTag.appendChild(orderTag);
        }
        return ordersTag;
    }

    /**
     * Create invoice/amount DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @return DOM element
     */
    protected Element createAmountSection(Document doc, Invoice invoice, Map<String, String> mapTaxesIndexes, Set<String> vatexReasons) {

        Element amount = doc.createElement("amount");

        Element currency = doc.createElement("currency");
        Element currencySymbol = doc.createElement("currencySymbol");
        Element currencyRate = doc.createElement("currencyRate");
        Text currencyCodeValue = this.createTextNode(doc, invoice.getBillingAccount().getTradingCurrency().getCurrencyCode());
        Text currencySymbolValue = this.createTextNode(doc, StringUtils.isNotBlank(invoice.getBillingAccount().getTradingCurrency().getSymbol())
                        ? invoice.getBillingAccount().getTradingCurrency().getSymbol()
                        : invoice.getBillingAccount().getTradingCurrency().getCurrencyCode());
        Text currencyRateValue = this.createTextNode(doc, invoice.getBillingAccount().getTradingCurrency().getCurrentRate() == null
                ? "1.00"
                : new DecimalFormat("0.00").format(invoice.getBillingAccount().getTradingCurrency().getCurrentRate()));
        currency.appendChild(currencyCodeValue);
        currencySymbol.appendChild(currencySymbolValue);
        currencyRate.appendChild(currencyRateValue);
        amount.appendChild(currency);
        amount.appendChild(currencySymbol);
        amount.appendChild(currencyRate);

        Element amountWithoutTax = doc.createElement("amountWithoutTax");
        amountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(invoice.getAmountWithoutTax())));
        amount.appendChild(amountWithoutTax);

        Element transactionalAmountWithoutTax = doc.createElement("transactionalAmountWithoutTax");
        transactionalAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(invoice.getTransactionalAmountWithoutTax())));
        amount.appendChild(transactionalAmountWithoutTax);

        Element amountWithTax = doc.createElement("amountWithTax");
        Text amountWithTaxTxt = this.createTextNode(doc, toPlainString(invoice.getAmountWithTax()));
        amountWithTax.appendChild(amountWithTaxTxt);
        amount.appendChild(amountWithTax);

        Element transactionalAmountWithTax = doc.createElement("transactionalAmountWithTax");
        transactionalAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(invoice.getTransactionalAmountWithTax())));
        amount.appendChild(transactionalAmountWithTax);

        Element balance = doc.createElement("balance");
        balance.appendChild(this.createTextNode(doc, toPlainString(invoice.getDueBalance())));
        amount.appendChild(balance);

        Element advances = createAndPopulateAdvancesSection(doc, invoice);
        if (advances != null) {
            amount.appendChild(advances);
        }

        Element netToPayElement = doc.createElement("netToPay");
        netToPayElement.appendChild(this.createTextNode(doc, toPlainString(invoice.getNetToPay())));
        amount.appendChild(netToPayElement);

        Element taxes = createAmountTaxSection(doc, invoice, mapTaxesIndexes, vatexReasons);
        if (taxes != null) {
            amount.appendChild(taxes);
        }

        return amount;
    }

    private Element createAndPopulateAdvancesSection(Document doc, Invoice invoice) {

        List<Object[]> advanceLinkedInvoices = invoiceService.findLinkedInvoicesByIdAndType(invoice.getId(), "ADV");

        if (advanceLinkedInvoices != null && !advanceLinkedInvoices.isEmpty()) {

            return populateAdvancesSection(doc, advanceLinkedInvoices);
        }
        return null;
    }

    private Element populateAdvancesSection(Document doc, List<Object[]> advanceLinkedInvoices) {

        Element advances = doc.createElement("advances");

        for (Object[] advancedLinkedInvoice : advanceLinkedInvoices) {

            Element advance = doc.createElement("advance");

            advance.setAttribute("invoiceNumber", advancedLinkedInvoice[0] != null ? advancedLinkedInvoice[0].toString() : "");
            advance.setAttribute("invoiceDate", advancedLinkedInvoice[1] != null ? DateUtils.formatDateWithPattern((Date) advancedLinkedInvoice[1],
                    paramBeanFactory.getInstance().getProperty(INVOICE_DATE_FORMAT, DEFAULT_DATE_PATTERN)) : "");
            advance.setAttribute("amountWithTax", advancedLinkedInvoice[2] != null ? advancedLinkedInvoice[2].toString() : "");

            advances.appendChild(advance);
        }
        return advances;
    }

    /**
     * Create invoice/header DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param isInvoiceAdjustment  Is this an adjustment invoice
     * @param invoiceConfiguration Invoice configuration
     * @return DOM element
     */
    protected Element createHeaderSection(Document doc, Invoice invoice, boolean isInvoiceAdjustment, InvoiceConfiguration invoiceConfiguration, String invoiceDateFormat,
                                          Map<String, String> mapTaxesIndexes, Set<String> vatexReasons) {
        Element header = doc.createElement("header");
        if (invoiceConfiguration.isDisplayProvider()) {
            Element providerTag = createProviderSection(doc, appProvider);
            if (providerTag != null) {
                header.appendChild(providerTag);
            }
        }
        Element customerTag = createCustomerSection(doc, invoice);
        if (customerTag != null) {
            header.appendChild(customerTag);
        }
        Element sellerTag = createSellerSection(doc, invoice);
        if (sellerTag != null) {
            header.appendChild(sellerTag);
        }
        LinkedInvoice linkedInvoice = invoiceService.getLinkedInvoice(invoice);
        Element customerAccountTag = createCustomerAccountSection(doc, invoice);
        if (customerAccountTag != null) {
            header.appendChild(customerAccountTag);
        }
        Element billingAccountTag = createBillingAccountSection(doc, invoice, linkedInvoice != null ? linkedInvoice.getLinkedInvoiceValue() : null, isInvoiceAdjustment, invoiceConfiguration);
        if (billingAccountTag != null) {
            header.appendChild(billingAccountTag);
        }
        Date invoiceDateData = invoice.getInvoiceDate();
        if (invoiceDateData != null) {
            buildInvoiceDates(doc, header, invoice, invoiceDateFormat);
        }
        Date dueDateData = invoice.getDueDate();
        if (dueDateData != null) {
            Element dueDate = doc.createElement("dueDate");
            dueDate.appendChild(this.createTextNode(doc, DateUtils.formatDateWithPattern(dueDateData, invoiceDateFormat)));
            header.appendChild(dueDate);
        }
        PaymentMethodEnum paymentMethodData = invoice.getPaymentMethodType();
        if (paymentMethodData != null) {
            Element paymentMethod = doc.createElement("paymentMethod");
            paymentMethod.appendChild(this.createTextNode(doc, paymentMethodData.name()));
            paymentMethod.setAttribute("paymentMeans", (invoice.getPaymentMethod() != null && invoice.getPaymentMethod().getPaymentMeans() !=null) ? invoice.getPaymentMethod().getPaymentMeans().getCode() : "");
            paymentMethod.setAttribute("paymentMeansLabel", (invoice.getPaymentMethod() != null && invoice.getPaymentMethod().getPaymentMeans() !=null) ? invoice.getPaymentMethod().getPaymentMeans().getCodeName() : "");
            header.appendChild(paymentMethod);
        }
        Element comment = doc.createElement("comment");
        comment.appendChild(doc.createCDATASection(getDefaultIfNull(invoice.getComment(), " ")));
        header.appendChild(comment);
        Element categoriesTag = createHeaderCategoriesSection(doc, invoice, mapTaxesIndexes, vatexReasons);
        if (categoriesTag != null) {
            header.appendChild(categoriesTag);
        }
        Element discountsTag = createDiscountsSection(doc, invoice);
        if (discountsTag != null) {
            header.appendChild(discountsTag);
        }

        Element externalPurchaseOrder = doc.createElement("externalPurchaseOrder");
        externalPurchaseOrder.appendChild(createTextNode(doc, invoice.getExternalPurchaseOrderNumber()));
        header.appendChild(externalPurchaseOrder);

        ofNullable(createSubTotals(doc, invoice.getInvoiceType(),
                invoice.getInvoiceLines(), invoice.getBillingAccount().getTradingLanguage()))
                .ifPresent(header::appendChild);
        return header;
    }

    // value from https://opencellsoft.atlassian.net/browse/INTRD-10546
    private void buildInvoiceDates(Document doc, Element header, Invoice invoice, String invoiceDateFormat) {

        Element invoiceDate = doc.createElement("invoiceDate");
        invoiceDate.setAttribute("format", "102"); // hardcoded value, related to default pattern yyyyMMdd
        invoiceDate.setAttribute("eventTimeCode2005", "3");
        invoiceDate.setAttribute("eventTimeCode2475", "5");
        invoiceDate.appendChild(this.createTextNode(doc, DateUtils.formatDateWithPattern(invoice.getInvoiceDate(), invoiceDateFormat)));

        header.appendChild(invoiceDate);

        if (invoice.getStatus() == InvoiceStatusEnum.VALIDATED) {
            if (invoice.getPaymentStatus() == InvoicePaymentStatusEnum.PAID) {
                Element invoiceDatePaid = doc.createElement("invoiceDatePaid");
                invoiceDatePaid.setAttribute("eventTimeCode2005", "432");
                invoiceDatePaid.setAttribute("eventTimeCode2475", "72");
                invoiceDatePaid.setAttribute("format", "102");
                invoiceDatePaid.appendChild(this.createTextNode(doc, DateUtils.formatDateWithPattern(invoice.getPaymentStatusDate(), invoiceDateFormat)));

                header.appendChild(invoiceDatePaid);
            }

            if (invoice.getCommercialOrder() != null && invoice.getCommercialOrder().getInvoicingPlan() != null) {
                Element invoiceDatePaid = doc.createElement("invoiceDateDelivery");
                invoiceDatePaid.setAttribute("eventTimeCode2005", "35");
                invoiceDatePaid.setAttribute("eventTimeCode2475", "29");
                invoiceDatePaid.setAttribute("format", "102");
                invoiceDatePaid.appendChild(this.createTextNode(doc, DateUtils.formatDateWithPattern(invoice.getInvoiceDate(), invoiceDateFormat)));

                header.appendChild(invoiceDatePaid);
            }
        }
    }


    private String buildEventTimeCode2005(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatusEnum.VALIDATED) {
            if (invoice.getPaymentStatus() == InvoicePaymentStatusEnum.PAID) {
                return "432";
            }

            if (invoice.getCommercialOrder() != null && invoice.getCommercialOrder().getInvoicingPlan() != null) {
                return "35";
            } else {
                return "3";
            }
        }
        return null;
    }

    // value from https://opencellsoft.atlassian.net/browse/INTRD-10546
    private String buildEventTimeCode2475(Invoice invoice){
        if (invoice.getStatus() == InvoiceStatusEnum.VALIDATED) {
            if (invoice.getPaymentStatus() == InvoicePaymentStatusEnum.PAID) {
                return "72";
            }

            if (invoice.getCommercialOrder() != null && invoice.getCommercialOrder().getInvoicingPlan() != null) {
                return "39";
            } else {
                return "5";
            }
        }
        return null;
    }

    private Element createSubTotals(Document doc, InvoiceType invoiceType,
                                    List<InvoiceLine> invoiceLines, TradingLanguage tradingLanguage) {
        Element subTotals = null;
        List<InvoiceSubTotals> invoiceSubTotals =
                invoiceSubTotalsService.calculateSubTotals(invoiceType, ofNullable(invoiceLines).orElse(emptyList()));
        if(invoiceSubTotals != null && !invoiceSubTotals.isEmpty()) {
            subTotals = doc.createElement("subTotals");
            String languageCode;
            if(tradingLanguage != null && tradingLanguage.getLanguage() != null
                    && tradingLanguage.getLanguage().getLanguageCode() != null) {
                languageCode = tradingLanguage.getLanguage().getLanguageCode();
            } else {
                languageCode = "ENG";
            }
            for (InvoiceSubTotals subTotal : invoiceSubTotals) {
                Element subTotalTag = doc.createElement("subTotal");
                subTotalTag.setAttribute("amountWithoutTax", subTotal.getAmountWithoutTax() != null
                        ? subTotal.getAmountWithoutTax().toPlainString() : "");
                subTotalTag.setAttribute("amountWithTax",
                        subTotal.getAmountWithTax() != null ? subTotal.getAmountWithTax().toPlainString() : null);
                subTotals.appendChild(subTotalTag);
                subTotalTag.setAttribute("description",
                        subTotal.getLabelI18n() != null
                                && !subTotal.getLabelI18n().isEmpty() ? subTotal.getLabelI18n().get(languageCode) : "");
                subTotalTag.setAttribute("id", subTotal.getId().toString());
            }
        }
        return subTotals;
    }

    /**
     * Create invoice/details DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param ratedTransactions    Rated transactions
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceConfiguration Invoice configuration
     * @param invoiceLines         Invoice lines
     * @return DOM element
     */
    protected Element createDetailsSection(Document doc, Invoice invoice, List<RatedTransaction> ratedTransactions,
                                           boolean isVirtual, InvoiceConfiguration invoiceConfiguration, List<InvoiceLine> invoiceLines,
                                           boolean isInvoiceAdjustment, Map<String, String> mapTaxesIndexes) {
        Element detail = doc.createElement("detail");
        Element userAccountsTag = ratedTransactions != null ? createUserAccountsSection(doc, invoice, ratedTransactions, isVirtual, invoiceConfiguration)
                :  createUserAccountsSectionIL(doc, invoice, invoiceLines, isVirtual, invoiceConfiguration, mapTaxesIndexes);
        if (userAccountsTag != null) {
            detail.appendChild(userAccountsTag);
        }
        buildLinkedInvoicesData(doc, detail, invoice.getLinkedInvoices(), isInvoiceAdjustment);

        return detail;
    }

    private void buildLinkedInvoicesData(Document doc, Element detailTag, Set<LinkedInvoice> linkedInvoices, boolean isInvoiceAdjustment) {
        if(!isInvoiceAdjustment) {
            return;
        }
        if (CollectionUtils.isEmpty(linkedInvoices)) {
            return;
        }
        Element invoicesRelatedsTag = doc.createElement("invoiceRelateds");
        linkedInvoices.forEach(linkedInvoice -> {
            Element childInvoicesRelatedsTag = doc.createElement("invoiceRelated");
            childInvoicesRelatedsTag.setAttribute("date", DateUtils.formatDateWithPattern(linkedInvoice.getInvoice().getInvoiceDate(), paramBeanFactory.getInstance().getProperty(INVOICE_DATE_FORMAT, DEFAULT_DATE_PATTERN)));
            childInvoicesRelatedsTag.setAttribute("format", "102"); // hardcoded value
            childInvoicesRelatedsTag.setAttribute("status", linkedInvoice.getInvoice().getStatus().name());

            childInvoicesRelatedsTag.appendChild(this.createTextNode(doc, linkedInvoice.getInvoice().getStatus() == InvoiceStatusEnum.VALIDATED ? linkedInvoice.getInvoice().getInvoiceNumber() : "Draft"));

            invoicesRelatedsTag.appendChild(childInvoicesRelatedsTag);
        });

        detailTag.appendChild(invoicesRelatedsTag);
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory/edr DOM element
     *
     * @param doc                   XML invoice DOM
     * @param edr                   EDR
     * @param invoiceDateFormat     Date format
     * @param invoiceDateTimeFormat Timestamp format
     * @return DOM element
     */
    protected Element createEDRSection(Document doc, EDR edr, String invoiceDateFormat, String invoiceDateTimeFormat) {

        Element edrInfo = doc.createElement("edr");
        edrInfo.setAttribute("originRecord", edr.getOriginRecord() != null ? edr.getOriginRecord() : "");
        edrInfo.setAttribute("originBatch", edr.getOriginBatch() != null ? edr.getOriginBatch() : "");
        edrInfo.setAttribute("quantity", edr.getQuantity() != null ? edr.getQuantity().toPlainString() : "");
        edrInfo.setAttribute("subscription", edr.getSubscription() != null ? edr.getSubscription().getDescription() : "");
        edrInfo.setAttribute("eventDate", DateUtils.formatDateWithPattern(edr.getEventDate(), invoiceDateTimeFormat));
        edrInfo.setAttribute("accessCode", getDefaultIfNull(edr.getAccessCode(), ""));
        edrInfo.setAttribute("parameter1", getDefaultIfNull(edr.getParameter1(), ""));
        edrInfo.setAttribute("parameter2", getDefaultIfNull(edr.getParameter2(), ""));
        edrInfo.setAttribute("parameter3", getDefaultIfNull(edr.getParameter3(), ""));
        edrInfo.setAttribute("parameter4", getDefaultIfNull(edr.getParameter4(), ""));
        edrInfo.setAttribute("parameter5", getDefaultIfNull(edr.getParameter5(), ""));
        edrInfo.setAttribute("parameter6", getDefaultIfNull(edr.getParameter6(), ""));
        edrInfo.setAttribute("parameter7", getDefaultIfNull(edr.getParameter7(), ""));
        edrInfo.setAttribute("parameter8", getDefaultIfNull(edr.getParameter8(), ""));
        edrInfo.setAttribute("parameter9", getDefaultIfNull(edr.getParameter9(), ""));
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
        return edrInfo;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory/line/walletOperation DOM element
     *
     * @param doc               XML invoice DOM
     * @param walletOperation   Wallet operation
     * @param invoiceDateFormat Date format
     * @return DOM element
     */
    protected Element createWOSection(Document doc, WalletOperation walletOperation, String invoiceDateFormat) {
        Element woLine = doc.createElement("walletOperation");
        woLine.setAttribute("code", walletOperation.getCode());
        woLine.setAttribute("description", getDefaultIfNull(walletOperation.getDescription(), ""));
        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        // get periodStartDate and periodEndDate for recurrents
        Date periodStartDate = walletOperation.getStartDate();
        Date periodEndDate = walletOperation.getEndDate();
        // get periodStartDate and periodEndDate for usages
        // instanceof is not used in this control because chargeTemplate can never be
        // instance of usageChargeTemplate according to model structure
        Date operationDate = walletOperation.getOperationDate();
        if (chargeTemplate instanceof UsageChargeTemplate && operationDate != null) {
            CounterPeriod counterPeriod = null;
            CounterInstance counter = walletOperation.getCounter();
            counterPeriod = counter.getCounterPeriod(operationDate);
            if (counterPeriod != null) {
                periodStartDate = counterPeriod.getPeriodStartDate();
                periodEndDate = counterPeriod.getPeriodEndDate();
            }
        }
        woLine.setAttribute("periodEndDate", DateUtils.formatDateWithPattern(periodEndDate, invoiceDateFormat));
        woLine.setAttribute("periodStartDate", DateUtils.formatDateWithPattern(periodStartDate, invoiceDateFormat));
        addCustomFields(walletOperation, doc, woLine);
        return woLine;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory/line DOM element
     *
     * @param doc                   XML invoice DOM
     * @param ratedTransaction      Rated transaction
     * @param invoiceDateFormat     Date format
     * @param invoiceDateTimeFormat Timestamp format
     * @param invoiceConfiguration  Invoice configuration
     * @param invoiceLanguageCode   Invoice language - language code
     * @return DOM element
     */
    protected Element createRTSection(Document doc, RatedTransaction ratedTransaction, String invoiceDateFormat, String invoiceDateTimeFormat, InvoiceConfiguration invoiceConfiguration,
                                      String invoiceLanguageCode) {

        Element line = doc.createElement("line");
        Date periodStartDateRT = ratedTransaction.getStartDate();
        Date periodEndDateRT = ratedTransaction.getEndDate();
        line.setAttribute("periodEndDate", DateUtils.formatDateWithPattern(periodEndDateRT, invoiceDateFormat));
        line.setAttribute("periodStartDate", DateUtils.formatDateWithPattern(periodStartDateRT, invoiceDateFormat));
        line.setAttribute("taxPercent", ratedTransaction.getTaxPercent().toPlainString());
        line.setAttribute("sortIndex", ratedTransaction.getSortIndex() != null ? ratedTransaction.getSortIndex() + "" : "");
        line.setAttribute("code", ratedTransaction.getCode());
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
            paramExtra.appendChild(this.createTextNode(doc, ratedTransaction.getParameterExtra()));
            line.appendChild(paramExtra);
        }
        Element lebel = doc.createElement("label");
        lebel.appendChild(this.createTextNode(doc, getDefaultIfNull(ratedTransaction.getDescription(), "")));
        line.appendChild(lebel);
        if (!StringUtils.isBlank(ratedTransaction.getUnityDescription())) {
            Element lineUnit = doc.createElement("unit");
            lineUnit.appendChild(this.createTextNode(doc, ratedTransaction.getUnityDescription()));
            line.appendChild(lineUnit);
        }
        if (!StringUtils.isBlank(ratedTransaction.getRatingUnitDescription())) {
            Element lineRatingUnit = doc.createElement("ratingUnit");
            lineRatingUnit.appendChild(this.createTextNode(doc, ratedTransaction.getRatingUnitDescription()));
            line.appendChild(lineRatingUnit);
        }
        if (ratedTransaction.getUnitAmountWithoutTax() != null) {
            Element lineUnitAmountWithoutTax = doc.createElement("unitAmountWithoutTax");
            lineUnitAmountWithoutTax.appendChild(this.createTextNode(doc, ratedTransaction.getUnitAmountWithoutTax().toPlainString()));
            line.appendChild(lineUnitAmountWithoutTax);
        }
        Element lineAmountWithoutTax = doc.createElement("amountWithoutTax");
        lineAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(ratedTransaction.getAmountWithoutTax())));
        line.appendChild(lineAmountWithoutTax);
        Element lineAmountWithTax = doc.createElement("amountWithTax");
        lineAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(ratedTransaction.getAmountWithTax())));
        line.appendChild(lineAmountWithTax);
        Element lineAmountTax = doc.createElement("amountTax");
        lineAmountTax.appendChild(this.createTextNode(doc, toPlainString(ratedTransaction.getAmountTax())));
        line.appendChild(lineAmountTax);
        Element quantity = doc.createElement("quantity");
        Text quantityTxt = this.createTextNode(doc, ratedTransaction.getQuantity() != null ? ratedTransaction.getQuantity().toPlainString() : "");
        quantity.appendChild(quantityTxt);
        line.appendChild(quantity);
        Element usageDate = doc.createElement("usageDate");
        Text usageDateTxt = this.createTextNode(doc, DateUtils.formatDateWithPattern(ratedTransaction.getUsageDate(), invoiceDateFormat));
        usageDate.appendChild(usageDateTxt);
        line.appendChild(usageDate);
        addCustomFields(ratedTransaction, doc, line);
        if (ratedTransaction.getPriceplan() != null) {
            Element ppTag = createPricePlanSection(doc, ratedTransaction.getPriceplan(), true, invoiceLanguageCode);
            if (ppTag != null) {
                line.appendChild(ppTag);
            }
        }
        if (invoiceConfiguration.isDisplayWalletOperations()) {
            List<WalletOperation> walletOperations = walletOperationService.listByRatedTransactionId(ratedTransaction.getId());
            if (walletOperations != null && !walletOperations.isEmpty()) {
                for (WalletOperation walletOperation : walletOperations) {
                    Element woTag = createWOSection(doc, walletOperation, invoiceDateFormat);
                    if (woTag != null) {
                        line.appendChild(woTag);
                    }
                }
            }
        }
        EDR edr = ratedTransaction.getEdr();
        if (invoiceConfiguration.isDisplayEdrs() && edr != null) {
            Element edrTag = createEDRSection(doc, edr, invoiceDateFormat, invoiceDateTimeFormat);
            if (edrTag != null) {
                line.appendChild(edrTag);
            }
        }
        ServiceInstance serviceInstance = ratedTransaction.getServiceInstance();
        if (serviceInstance != null) {
            String offerCode = ratedTransaction.getOfferTemplate() != null ? ratedTransaction.getOfferTemplate().getCode() : null;
            Element serviceTag = createServiceSection(doc, serviceInstance, offerCode, true);
            if (serviceTag != null) {
                line.appendChild(serviceTag);
            }
        }
        return line;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory DOM element
     *
     * @param doc                   XML invoice DOM
     * @param subCatInvoiceAgregate Invoice subcategory aggregate
     * @param ratedTransactions     Rated transactions
     * @param isVirtual             Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceDateFormat     Date format
     * @param invoiceDateTimeFormat Timestamp format
     * @param invoiceLanguageCode   Invoice language - language code
     * @param invoiceConfiguration  Invoice configuration
     * @return DOM element
     */
    protected Element createUAInvoiceSubcategorySection(Document doc, SubCategoryInvoiceAgregate subCatInvoiceAgregate, List<RatedTransaction> ratedTransactions, boolean isVirtual,
            String invoiceDateFormat, String invoiceDateTimeFormat, String invoiceLanguageCode, InvoiceConfiguration invoiceConfiguration) {

        InvoiceSubCategory invoiceSubCat = subCatInvoiceAgregate.getInvoiceSubCategory();
        WalletInstance wallet = subCatInvoiceAgregate.getWallet();
        Long walletId = wallet != null ? wallet.getId() : null;
        if (isVirtual) {
            ratedTransactions = (List<RatedTransaction>)subCatInvoiceAgregate.getInvoiceablesToAssociate();
        }
        String invoiceSubCategoryLabel = subCatInvoiceAgregate.getDescription();
        if (invoiceSubCat.getDescriptionI18n() != null && invoiceSubCat.getDescriptionI18n().get(invoiceLanguageCode) != null) {
            // get label description by language code
            invoiceSubCategoryLabel = invoiceSubCat.getDescriptionI18n().get(invoiceLanguageCode);
        }
        Element subCategory = doc.createElement("subCategory");
        subCategory.setAttribute("label", getDefaultIfNull(invoiceSubCategoryLabel, ""));
        subCategory.setAttribute("code", invoiceSubCat.getCode());
        subCategory.setAttribute("amountWithoutTax", toPlainString(subCatInvoiceAgregate.getAmountWithoutTax()));
        subCategory.setAttribute("transactionalAmountWithoutTax", toPlainString(subCatInvoiceAgregate.getTransactionalAmountWithoutTax()));
        subCategory.setAttribute("amountWithTax", toPlainString(subCatInvoiceAgregate.getAmountWithTax()));
        subCategory.setAttribute("transactionalAmountWithTax", toPlainString(subCatInvoiceAgregate.getTransactionalAmountWithTax()));
        subCategory.setAttribute("amountTax", toPlainString(subCatInvoiceAgregate.getAmountTax()));
        subCategory.setAttribute("transactionalAmountTax", toPlainString(subCatInvoiceAgregate.getTransactionalAmountTax()));
        subCategory.setAttribute("sortIndex", (invoiceSubCat.getSortIndex() != null) ? invoiceSubCat.getSortIndex() + "" : "");
        Collections.sort(ratedTransactions, InvoiceCategoryComparatorUtils.getRatedTransactionComparator());
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if ((ratedTransaction.getInvoiceAgregateF().getId() != null && !ratedTransaction.getInvoiceAgregateF().getId().equals(subCatInvoiceAgregate.getId()))
                    || (ratedTransaction.getInvoiceAgregateF().getId() == null && !ratedTransaction.getInvoiceSubCategory().getId().equals(invoiceSubCat.getId())
                    && !((ratedTransaction.getWallet() == null && walletId == null) || (walletId != null && walletId.equals(ratedTransaction.getWallet().getId()))))) {
                continue;
            }
            Element rtTag = createRTSection(doc, ratedTransaction, invoiceDateFormat, invoiceDateTimeFormat, invoiceConfiguration, invoiceLanguageCode);
            if (rtTag != null) {
                subCategory.appendChild(rtTag);
            }
        }
        addCustomFields(invoiceSubCat, doc, subCategory);
        return subCategory;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category DOM element
     *
     * @param doc                     XML invoice DOM
     * @param categoryInvoiceAgregate Invoice category aggregate
     * @param ratedTransactions       Rated transactions
     * @param isVirtual               Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceDateFormat       Date format
     * @param invoiceDateTimeFormat   Timestamp format
     * @param invoiceLanguageCode     Invoice language - language code
     * @param invoiceConfiguration    Invoice configuration
     * @return DOM element
     */
    protected Element createDetailsUAInvoiceCategorySection(Document doc, CategoryInvoiceAgregate categoryInvoiceAgregate, List<RatedTransaction> ratedTransactions, boolean isVirtual,
            String invoiceDateFormat, String invoiceDateTimeFormat, String invoiceLanguageCode, InvoiceConfiguration invoiceConfiguration) {

        InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();
        String invoiceCategoryLabel = categoryInvoiceAgregate.getDescription();
        if (invoiceCategory.getDescriptionI18n() != null && invoiceCategory.getDescriptionI18n().get(invoiceLanguageCode) != null) {
            invoiceCategoryLabel = invoiceCategory.getDescriptionI18n().get(invoiceLanguageCode);
        }
        Element categoryTag = doc.createElement("category");
        categoryTag.setAttribute("label", getDefaultIfNull(invoiceCategoryLabel, ""));
        categoryTag.setAttribute("code", invoiceCategory.getCode());
        categoryTag.setAttribute("sortIndex", (invoiceCategory.getSortIndex() != null) ? invoiceCategory.getSortIndex() + "" : "");

        Element amountWithoutTax = doc.createElement("amountWithoutTax");
        amountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAgregate.getAmountWithoutTax())));
        categoryTag.appendChild(amountWithoutTax);

        Element transactionalAmountWithoutTax = doc.createElement("transactionalAmountWithoutTax");
        transactionalAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAgregate.getTransactionalAmountTax())));
        categoryTag.appendChild(transactionalAmountWithoutTax);

        Element amountWithTax = doc.createElement("amountWithTax");
        amountWithTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAgregate.getAmountWithTax())));
        categoryTag.appendChild(amountWithTax);

        Element transactionalAmountWithTax = doc.createElement("transactionalAmountWithTax");
        transactionalAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAgregate.getTransactionalAmountWithTax())));
        categoryTag.appendChild(transactionalAmountWithTax);

        Element amountTax = doc.createElement("amountTax");
        amountTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAgregate.getAmountTax())));
        categoryTag.appendChild(amountTax);

        Element transactionalAmountTax = doc.createElement("transactionalAmountTax");
        transactionalAmountTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAgregate.getTransactionalAmountTax())));
        categoryTag.appendChild(transactionalAmountTax);

        addCustomFields(invoiceCategory, doc, categoryTag);

        Element subCategories = doc.createElement("subCategories");
        categoryTag.appendChild(subCategories);

        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<>(categoryInvoiceAgregate.getSubCategoryInvoiceAgregates());
        Collections.sort(subCategoryInvoiceAgregates, InvoiceCategoryComparatorUtils.getInvoiceSubCategoryComparator());
        for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
            if (subCatInvoiceAgregate.isDiscountAggregate()) {
                continue;
            }
            Element subCategory = createUAInvoiceSubcategorySection(doc, subCatInvoiceAgregate, ratedTransactions, isVirtual, invoiceDateFormat, invoiceDateTimeFormat, invoiceLanguageCode, invoiceConfiguration);
            if (subCategory != null) {
                subCategories.appendChild(subCategory);
            }
        }
        return categoryTag;
    }

    /**
     * Get a list of rated transactions included in the invoice
     *
     * @param invoice   Invoice to convert
     * @param isVirtual Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @return A list of rated transactions included in the invoice
     */
    protected List<RatedTransaction> getRatedTransactions(Invoice invoice, boolean isVirtual) {
        List<RatedTransaction> ratedTransactions = invoice.getDraftRatedTransactions();
        if (!isVirtual) {
            ratedTransactions.addAll(ratedTransactionService.getRatedTransactionsByInvoice(invoice, appProvider.isDisplayFreeTransacInInvoice()));
        }
        return ratedTransactions;
    }

    /**
     * Get a list of offers referenced from RTs
     *
     * @param ratedTransactions Rated transactions
     * @return A list of price plans referenced from RTs
     */
    protected List<OfferTemplate> getOffers(List<RatedTransaction> ratedTransactions) {

        // TODO Need to check performance, maybe its quick enough to avoid searching DB in non-virtual cases
        List<OfferTemplate> offers = new ArrayList<>();
        Set<Long> offerIds = new HashSet<>();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if (ratedTransaction.getOfferTemplate() != null && !offerIds.contains(ratedTransaction.getOfferTemplate().getId())) {
                offers.add(ratedTransaction.getOfferTemplate());
                offerIds.add(ratedTransaction.getOfferTemplate().getId());
            }
        }
        Collections.sort(offers, Comparator.comparing(OfferTemplate::getCode));
        return offers;
    }

    /**
     * Get a list of service instances referenced from RTs
     *
     * @param ratedTransactions Rated transactions
     * @return list of service instances referenced from RTs
     */
    protected Map<String, List<ServiceInstance>> getServices(List<RatedTransaction> ratedTransactions) {

        // TODO Need to check performance, maybe its quick enough to avoid searching DB in non-virtual cases
        Map<String, List<ServiceInstance>> servicesByOffer = new HashMap<>();
        Set<Long> serviceInstanceIds = new HashSet<>();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if (ratedTransaction.getServiceInstance() != null && ratedTransaction.getOfferTemplate() != null && !serviceInstanceIds.contains(ratedTransaction.getServiceInstance().getId())) {
                List<ServiceInstance> services = servicesByOffer.computeIfAbsent(ratedTransaction.getOfferTemplate().getCode(), k -> new ArrayList<>());
                services.add(ratedTransaction.getServiceInstance());
                serviceInstanceIds.add(ratedTransaction.getServiceInstance().getId());
            }
        }
        return servicesByOffer;
    }

    /**
     * Get a list of price plans referenced from RTs
     *
     * @param ratedTransactions Rated transactions
     * @return A list of price plans referenced from RTs
     */
    protected List<PricePlanMatrix> getPricePlans(List<RatedTransaction> ratedTransactions) {

        // TODO Need to check performance, maybe its quick enough to avoid searching DB in non-virtual cases
        List<PricePlanMatrix> pps = new ArrayList<>();
        Set<Long> ppIds = new HashSet<>();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if (ratedTransaction.getPriceplan() != null && !ppIds.contains(ratedTransaction.getPriceplan().getId())) {
                pps.add(ratedTransaction.getPriceplan());
                ppIds.add(ratedTransaction.getPriceplan().getId());
            }
        }
        Collections.sort(pps, Comparator.comparing(PricePlanMatrix::getCode));
        return pps;
    }

    /**
     * Get a list of subscriptions that are being invoiced - referenced from RTs
     *
     * @param userAccount       User account
     * @param ignoreUA          Shall UA be ignored and all subscriptions be returned. Used as true when aggregation by user account is turned off - system property
     *                          invoice.aggregateByUA=false
     * @param ratedTransactions Rated transactions
     * @return A list of subscription that are being invoiced - referenced from RTs
     */
    protected List<Subscription> getSubscriptions(UserAccount userAccount, boolean ignoreUA, List<RatedTransaction> ratedTransactions) {

        // TODO Need to check performance, maybe its quick enough to avoid searching DB in non-virtual cases
        List<Subscription> subscriptions = new ArrayList<>();
        Set<Long> subIds = new HashSet<>();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if (ratedTransaction.getSubscription() != null && !subIds.contains(ratedTransaction.getSubscription().getId()) && (ignoreUA || (ratedTransaction.getUserAccount() == null && userAccount == null)
                    || (userAccount != null && ratedTransaction.getUserAccount() != null && userAccount.getId().equals(ratedTransaction.getUserAccount().getId())))) {
                subscriptions.add(ratedTransaction.getSubscription());
                subIds.add(ratedTransaction.getSubscription().getId());
            }
        }
        Collections.sort(subscriptions, Comparator.comparing(Subscription::getCode));
        return subscriptions;
    }

    /**
     * Creates a root element of xml - Invoice tag
     *
     * @param doc                 XML invoice DOM
     * @param invoice             Invoice to convert
     * @param isVirtual           Is this a virtual invoice. If true, no invoice, invoice aggregate nor IL information is persisted in DB
     * @param isInvoiceAdjustment Is this an adjustment invoice
     * @param doc                 XML invoice DOM Builder
     * @return DOM element DOM element
     */
    protected Element createInvoiceSectionIL(Document doc, Invoice invoice, boolean isVirtual, boolean isInvoiceAdjustment, DocumentBuilder docBuilder, Map<String, String> mapTaxesIndexes) {
        InvoiceConfiguration invoiceConfiguration = appProvider.getInvoiceConfigurationOrDefault();
        Element invoiceTag = initInvoiceTag(doc, invoice, isInvoiceAdjustment, docBuilder, invoiceConfiguration, mapTaxesIndexes);
        List<InvoiceLine> invoiceLines = null;
        if (invoiceConfiguration.isDisplayOffers() || invoiceConfiguration.isDisplayServices()
                || invoiceConfiguration.isDisplayPricePlans() || invoiceConfiguration.isDisplayDetail()) {
            invoiceLines = invoice.getInvoiceLines();
        }
        if (invoiceConfiguration.isDisplayOffers()) {
            Element offersTag = createOffersSection(doc, invoice, null, invoiceLines);
            if (offersTag != null) {
                invoiceTag.appendChild(offersTag);
            }
        }
        if (invoiceConfiguration.isDisplayServices()) {
            Element servicesTag = createServicesSection(doc, null, invoiceLines);
            if (servicesTag != null) {
                invoiceTag.appendChild(servicesTag);
            }
        }
        if (invoiceConfiguration.isDisplayDetail()) {
            Element details = createDetailsSection(doc, invoice, null, isVirtual, invoiceConfiguration, invoiceLines, isInvoiceAdjustment, mapTaxesIndexes);
            if (details != null) {
                invoiceTag.appendChild(details);
            }
        }
        return invoiceTag;
    }

    /**
     * Get a list of offers referenced from invoiceLines
     * @param invoiceLines invoice Lines
     * @return A list of price plans referenced from invoiceLines
     */
    protected List<OfferTemplate> getOffersFromILs(List<InvoiceLine> invoiceLines) {
        List<OfferTemplate> offers = new ArrayList<>();
        Set<Long> offerIds = new HashSet<>();
        for (InvoiceLine invoiceLine : invoiceLines) {
            if (invoiceLine.getOfferTemplate() != null && !offerIds.contains(invoiceLine.getOfferTemplate().getId())) {
                offers.add(invoiceLine.getOfferTemplate());
                offerIds.add(invoiceLine.getOfferTemplate().getId());
            }
        }
        Collections.sort(offers, Comparator.comparing(OfferTemplate::getCode));
        return offers;
    }

    /**
     * Get a list of service instances referenced from invoiceLines
     *
     * @param invoiceLines invoice Lines
     * @return list of service instances referenced from invoiceLines
     */
    protected Map<String, List<ServiceInstance>> getServicesFromILs(List<InvoiceLine> invoiceLines) {
        Map<String, List<ServiceInstance>> servicesByOffer = new HashMap<>();
        Set<Long> serviceInstanceIds = new HashSet<>();
        for (InvoiceLine invoiceLine : invoiceLines) {
            if (invoiceLine.getServiceInstance() != null && invoiceLine.getOfferTemplate() != null
                    && !serviceInstanceIds.contains(invoiceLine.getServiceInstance().getId())) {
                List<ServiceInstance> services = servicesByOffer.computeIfAbsent(invoiceLine.getOfferTemplate().getCode(), k -> new ArrayList<>());
                services.add(invoiceLine.getServiceInstance());
                serviceInstanceIds.add(invoiceLine.getServiceInstance().getId());
            }
        }
        return servicesByOffer;
    }

    /**
     * Get a list of subscriptions that are being invoiced - referenced from invoiceLines
     *
     * @param invoiceLines invoice Lines
     * @return A list of subscription that are being invoiced - referenced from invoiceLines
     */
    protected List<Subscription> getSubscriptionsFromIls(List<InvoiceLine> invoiceLines) {
        List<Subscription> subscriptions = new ArrayList<>();

        if (CollectionUtils.isEmpty(invoiceLines)) {
            return subscriptions;
        }

        Set<Long> subIds = new HashSet<>();
        for (InvoiceLine invoiceLine : invoiceLines) {
            if (invoiceLine.getSubscription() != null && !subIds.contains(invoiceLine.getSubscription().getId())) {
                subscriptions.add(invoiceLine.getSubscription());
                subIds.add(invoiceLine.getSubscription().getId());
            }
        }
        Collections.sort(subscriptions, Comparator.comparing(Subscription::getCode));
        return subscriptions;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory/line DOM element
     *
     * @param doc                   XML invoice DOM
     * @param invoiceLine           invoice Line
     * @param invoiceDateFormat     Date format
     * @return DOM element
     */
    protected Element createILSection(Document doc, InvoiceLine invoiceLine, String invoiceDateFormat,
                                      InvoiceConfiguration invoiceConfiguration, Map<String, String> mapTaxesIndexes) {
        Element line;
        line = doc.createElement("line");
        Date periodStartDate = invoiceLine != null
                && invoiceLine.getValidity() != null ? invoiceLine.getValidity().getFrom() : null;
        Date periodEndDate = invoiceLine != null
                && invoiceLine.getValidity() != null ? invoiceLine.getValidity().getTo() : null;
        line.setAttribute("periodEndDate", DateUtils.formatDateWithPattern(periodEndDate, invoiceDateFormat));
        line.setAttribute("periodStartDate", DateUtils.formatDateWithPattern(periodStartDate, invoiceDateFormat));
        line.setAttribute("taxPercent", invoiceLine.getTaxRate() != null ? invoiceLine.getTaxRate().toPlainString() : "");
        line.setAttribute("taxCode", getTaxCodes(invoiceLine.getTax()));
        line.setAttribute("taxIndex", getTaxIndexes(invoiceLine.getTax(), mapTaxesIndexes));
        line.setAttribute("sortIndex", "");
        line.setAttribute("code", invoiceLine.getOrderRef());
        Element label = doc.createElement("label");
        label.appendChild(this.createTextNode(doc, getDefaultIfNull(invoiceLine.getLabel(), "")));
        line.appendChild(label);
        Element article = doc.createElement("article");
        article.appendChild(this.createTextNode(doc, ""));
        article.setAttribute("code", invoiceLine.getAccountingArticle() != null ? invoiceLine.getAccountingArticle().getCode() : "");
        article.setAttribute("label", invoiceLine.getAccountingArticle() != null ? invoiceLine.getAccountingArticle().getDescription() : "");
        article.setAttribute("allowanceCode", invoiceLine.getAccountingArticle() != null && invoiceLine.getAccountingArticle().getAllowanceCode() != null ? invoiceLine.getAccountingArticle().getAllowanceCode().getCode() : "");
        article.setAttribute("isPhysical", invoiceLine.getAccountingArticle() != null && invoiceLine.getAccountingArticle().isPhysical() ? YES : NO);
        line.appendChild(article);
        if (invoiceLine.getUnitPrice() != null) {
            Element lineUnitAmountWithoutTax = doc.createElement("unitAmountWithoutTax");
            lineUnitAmountWithoutTax.appendChild(this.createTextNode(doc, invoiceLine.getUnitPrice().toPlainString()));
            line.appendChild(lineUnitAmountWithoutTax);

            Element lineUnitTransactionalAmountWithoutTax = doc.createElement("unitTransactionalAmountWithoutTax");
            lineUnitTransactionalAmountWithoutTax.appendChild(this.createTextNode(doc, invoiceLine.getTransactionalUnitPrice().toPlainString()));
            line.appendChild(lineUnitTransactionalAmountWithoutTax);
        }

        Element lineAmountWithoutTax = doc.createElement("amountWithoutTax");
        lineAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(invoiceLine.getAmountWithoutTax())));
        line.appendChild(lineAmountWithoutTax);

        Element lineTransactionalAmountWithoutTax = doc.createElement("transactionalAmountWithoutTax");
        lineTransactionalAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(invoiceLine.getTransactionalAmountWithoutTax())));
        line.appendChild(lineTransactionalAmountWithoutTax);

        Element lineAmountWithTax = doc.createElement("amountWithTax");
        lineAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(invoiceLine.getAmountWithTax())));
        line.appendChild(lineAmountWithTax);

        Element lineTransactionalAmountWithTax = doc.createElement("transactionalAmountWithTax");
        lineTransactionalAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(invoiceLine.getTransactionalAmountWithTax())));
        line.appendChild(lineTransactionalAmountWithTax);

        Element lineAmountTax = doc.createElement("amountTax");
        lineAmountTax.appendChild(this.createTextNode(doc, toPlainString(invoiceLine.getAmountTax())));
        line.appendChild(lineAmountTax);

        Element lineTransactionalAmountTax = doc.createElement("transactionalAmountTax");
        lineTransactionalAmountTax.appendChild(this.createTextNode(doc, toPlainString(invoiceLine.getTransactionalAmountTax())));
        line.appendChild(lineTransactionalAmountTax);

        Element quantity = doc.createElement("quantity");
        Text quantityTxt = this.createTextNode(doc, invoiceLine.getQuantity() != null ? invoiceLine.getQuantity().toPlainString() : "");
        quantity.appendChild(quantityTxt);

        Element unitPrice = doc.createElement("unitPrice");
        Text unitPriceTxt = this.createTextNode(doc, invoiceLine.getUnitPrice() != null ? invoiceLine.getUnitPrice().toPlainString() : "");
        unitPrice.appendChild(unitPriceTxt);

        Element transactionalUnitPrice = doc.createElement("transactionalUnitPrice");
        Text transactionalUnitPriceTxt = this.createTextNode(doc, invoiceLine.getTransactionalUnitPrice() != null ? invoiceLine.getTransactionalUnitPrice().toPlainString() : "");
        unitPrice.appendChild(transactionalUnitPriceTxt);

        line.appendChild(unitPrice);
        line.appendChild(transactionalUnitPrice);
        line.appendChild(quantity);

        Element usageDate = doc.createElement("usageDate");
        Text usageDateTxt = this.createTextNode(doc, DateUtils.formatDateWithPattern(invoiceLine.getValueDate(), invoiceDateFormat));
        usageDate.appendChild(usageDateTxt);
        line.appendChild(usageDate);

        ServiceInstance serviceInstance = invoiceLine.getServiceInstance();
        if (serviceInstance != null) {
            String offerCode = invoiceLine.getOfferTemplate() != null ? invoiceLine.getOfferTemplate().getCode() : null;
            Element serviceTag = createServiceSection(doc, serviceInstance, offerCode, true);
            if (serviceTag != null) {
                line.appendChild(serviceTag);
            }
        }

        if (invoiceConfiguration.isDisplayTaxDetails()) {
            Optional<TaxDetails> oTaxDetails = invoiceLineService.getTaxDetails(invoiceLine.getTax(),
                    invoiceLine.getAmountTax(), invoiceLine.getTransactionalAmountTax());
            if (oTaxDetails.isPresent()) {
                TaxDetails taxDetails = oTaxDetails.get();
                Element taxDetailsTag = doc.createElement("taxDetails");
                if(taxDetails.getComposite()) {
                    if (taxDetails.getSubTaxes() != null && !taxDetails.getSubTaxes().isEmpty()) {
                        for (TaxDetails subTaxDetails : taxDetails.getSubTaxes()) {
                            taxDetailsTag.appendChild(taxDetailsTag.appendChild(createTaxDetailTag(subTaxDetails, doc, mapTaxesIndexes)));
                        }
                    }
                } else {
                    taxDetailsTag.appendChild(createTaxDetailTag(taxDetails, doc, mapTaxesIndexes));
                }
                line.appendChild(taxDetailsTag);
            }
        }

        if (invoiceConfiguration.isDisplayRatedItems()) {
            List<RatedTransaction> ratedItems = invoiceLine.getRatedTransactions();

            if (ratedItems != null && !ratedItems.isEmpty()) {
                Element ratedItemsElement = doc.createElement("ratedItems");

                for (RatedTransaction ratedItem : ratedItems) {

                    Element ratedItemNode = doc.createElement("ratedItem");

                    buildRatedItemNodeAttributes(invoiceDateFormat, ratedItem, ratedItemNode);

                    buildProductNode(doc, ratedItem, ratedItemNode);

                    ChargeInstance chargeInstance = ratedItem.getChargeInstance();
                    if (chargeInstance != null) {
                        buildSubscriptionNode(doc, ratedItemNode, chargeInstance);
                    }
                    buildParametersNode(doc, ratedItem, ratedItemNode);

                    if (ratedItem.getParameterExtra() != null) {
                        buildExtraParamterNode(doc, ratedItem, ratedItemNode);
                    }
                    ratedItemsElement.appendChild(ratedItemNode);
                }
                line.appendChild(ratedItemsElement);
            }

        }
        return line;
    }

    private void buildExtraParamterNode(Document doc, RatedTransaction ratedItem, Element ratedItemNode) {
        Element parameterExtraNode = doc.createElement("parameterExtra");
        parameterExtraNode.appendChild(doc.createCDATASection(getDefaultIfNull(ratedItem.getParameterExtra(), " ")));
        ratedItemNode.appendChild(parameterExtraNode);
    }

    private static void buildProductNode(Document doc, RatedTransaction ratedItem, Element ratedItemNode) {
        if(ratedItem.getServiceInstance() != null && ratedItem.getServiceInstance().getProductVersion() != null){
           Product product = ratedItem.getServiceInstance().getProductVersion().getProduct();
           if(product != null){
               Element productNode = doc.createElement("product");
               productNode.setAttribute("code",product.getCode());
               productNode.setAttribute("description",product.getDescription());
               ratedItemNode.appendChild(productNode);
           }
        }
    }

    private static void buildRatedItemNodeAttributes(String invoiceDateFormat, RatedTransaction ratedItem, Element ratedItemNode) {
        if(ratedItem.getUsageDate() != null){
            ratedItemNode.setAttribute("usageDate", DateUtils.formatDateWithPattern(ratedItem.getUsageDate(), invoiceDateFormat));
        }
        if(ratedItem.getCode() != null){
            ratedItemNode.setAttribute("code", ratedItem.getCode());
        }
        if(ratedItem.getDescription() != null){
            ratedItemNode.setAttribute("description", ratedItem.getDescription());
        }
        if(ratedItem.getQuantity() != null){
            ratedItemNode.setAttribute("quantity", ratedItem.getQuantity().toString());
        }
        if(ratedItem.getUnitAmountWithoutTax() != null){
            ratedItemNode.setAttribute("unitAmountWithoutTax", ratedItem.getUnitAmountWithoutTax().toString());
        }
        if(ratedItem.getAmountWithoutTax() != null){
            ratedItemNode.setAttribute("amountWithoutTax", ratedItem.getAmountWithoutTax().toString());
        }
    }

    private void buildParametersNode(Document doc, RatedTransaction ratedItem, Element ratedItemNode) {
        if(ratedItem.getParameter1() != null){
            Element parameter1Node = doc.createElement("parameter1");
            Text parameter1Text = this.createTextNode(doc, ratedItem.getParameter1());
            parameter1Node.appendChild(parameter1Text);
            ratedItemNode.appendChild(parameter1Node);
        }
        if(ratedItem.getParameter2() != null){
            Element parameter2Node = doc.createElement("parameter2");
            Text parameter2Text = this.createTextNode(doc, ratedItem.getParameter2());
            parameter2Node.appendChild(parameter2Text);
            ratedItemNode.appendChild(parameter2Node);
        }
        if(ratedItem.getParameter1() != null){
            Element parameter3Node = doc.createElement("parameter3");
            Text parameter3Text = this.createTextNode(doc, ratedItem.getParameter3());
            parameter3Node.appendChild(parameter3Text);
            ratedItemNode.appendChild(parameter3Node);
        }
    }

    private static void buildSubscriptionNode(Document doc, Element ratedItemNode, ChargeInstance chargeInstance) {
        Subscription subscription = chargeInstance.getSubscription();
        if(subscription != null){
            Element subscriptionNode = doc.createElement("subscription");
            subscriptionNode.setAttribute("code",subscription.getCode());
            subscriptionNode.setAttribute("description",subscription.getDescription());
            ratedItemNode.appendChild(subscriptionNode);
        }
    }

    private Element createTaxDetailTag(TaxDetails taxDetails, Document doc, Map<String, String> mapTaxesIndexes) {
        Element taxDetailTag = doc.createElement("taxDetail");
        taxDetailTag.setAttribute("code", taxDetails.getTaxCode());
        taxDetailTag.setAttribute("index", mapTaxesIndexes.get(taxDetails.getTaxCode()));
        taxDetailTag.setAttribute("description",
                getDefaultIfNull(taxDetails.getTaxDescription(), ""));
        taxDetailTag.setAttribute("taxPercent", taxDetails.getPercent().toPlainString());
        taxDetailTag.setAttribute("taxCode", taxDetails.getTaxCode());
        taxDetailTag.setAttribute("taxAmount", taxDetails.getTaxAmount().toPlainString());
        return taxDetailTag;
    }

    /**
     * Create invoice/details/userAccounts DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param invoiceLines          invoice lines
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor IL information is persisted in DB
     * @param invoiceConfiguration Invoice configuration
     * @return DOM element
     */
    protected Element createUserAccountsSectionIL(Document doc, Invoice invoice, List<InvoiceLine> invoiceLines,
                                                  boolean isVirtual, InvoiceConfiguration invoiceConfiguration,
                                                  Map<String, String> mapTaxesIndexes) {

        Element userAccountsTag = doc.createElement("userAccounts");
        String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
        Set<UserAccount> userAccounts = getUserAccounts(invoice, invoiceConfiguration);
        for (UserAccount userAccount : userAccounts) {
            Element userAccountTag = createUserAccountSectionIL(doc, invoice, userAccount,
                    invoiceLines, isVirtual, false, invoiceLanguageCode, invoiceConfiguration, mapTaxesIndexes);
            if (userAccountTag != null) {
                if (invoiceConfiguration.isDisplayUserAccountHierarchy()) {
                    createUserAccountChildSectionIL(doc, invoice, invoiceLines, isVirtual,
                            invoiceLanguageCode, invoiceConfiguration, userAccount.getUserAccounts(), userAccountTag, mapTaxesIndexes);
                }
                userAccountsTag.appendChild(userAccountTag);
            }
        }
        Element userAccountTag = createUserAccountSectionIL(doc, invoice, null, invoiceLines, isVirtual,
                userAccountsTag.getChildNodes().getLength() == 0, invoiceLanguageCode, invoiceConfiguration, mapTaxesIndexes);
        if (userAccountTag != null) {
            userAccountsTag.appendChild(userAccountTag);
        }
        return userAccountsTag;
    }

    /**
     * Create invoice/details/userAccounts/userAccount DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param userAccount          User account
     * @param invoiceLines          invoice lines
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB * @param ignoreUA Shall UA be ignored and all
     *                             subscriptions be returned. Used as true when aggregation by user account is turned off - system property invoice.aggregateByUA=false
     * @param invoiceLanguageCode
     * @param invoiceConfiguration Invoice configuration
     * @return
     */
    public Element createUserAccountSectionIL(Document doc, Invoice invoice, UserAccount userAccount,
                                               List<InvoiceLine> invoiceLines, boolean isVirtual, boolean ignoreUA,
                                               String invoiceLanguageCode, InvoiceConfiguration invoiceConfiguration,
                                              Map<String, String> mapTaxesIndexes) {
        Element categoriesTag = createUAInvoiceCategoriesIL(doc, invoice, userAccount, invoiceLines, isVirtual, invoiceConfiguration, mapTaxesIndexes);
        if (categoriesTag == null) {
            return null;
        }
        Element userAccountTag = doc.createElement("userAccount");
        if (userAccount == null) {
        	 userAccountTag.setAttribute("description", "-");
        } else {
            userAccountTag.setAttribute("id", userAccount.getId() + "");
            userAccountTag.setAttribute("code", userAccount.getCode());
            userAccountTag.setAttribute("jobTitle", getDefaultIfNull(userAccount.getJobTitle(), ""));
            userAccountTag.setAttribute("description", getDefaultIfNull(userAccount.getDescription(), ""));
            userAccountTag.setAttribute("registrationNo", getDefaultIfNull(userAccount.getRegistrationNo(), ""));
            userAccountTag.setAttribute("vatNo", getDefaultIfNull(userAccount.getVatNo(), ""));
            userAccountTag.setAttribute("ICD", userAccount.getIcdId() != null ? userAccount.getIcdId().getCode() : "");
            addCustomFields(userAccount, doc, userAccountTag);
        }
        if (invoiceConfiguration.isDisplaySubscriptions()) {
        	List<RatedTransaction> ratedTransactions = ratedTransactionService.listRatedTransactionsByInvoice(invoice);
            Element subscriptionsTag = createSubscriptionsSection(doc, userAccount, ratedTransactions, isVirtual, ignoreUA, invoiceLines);
            if (subscriptionsTag != null
                    && subscriptionsTag.getChildNodes() != null && subscriptionsTag.getChildNodes().getLength() != 0) {
                userAccountTag.appendChild(subscriptionsTag);
            }
        }
        Element nameTag = createNameSection(doc, userAccount, invoiceLanguageCode);
        if (nameTag != null) {
            userAccountTag.appendChild(nameTag);
        }
        Element addressTag = createAddressSection(doc, userAccount, invoiceLanguageCode);
        if (addressTag != null) {
            userAccountTag.appendChild(addressTag);
        }
        userAccountTag.appendChild(categoriesTag);
        return userAccountTag;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories DOM element
     *
     * @param doc                  XML invoice DOM
     * @param invoice              Invoice to convert
     * @param userAccount          User account
     * @param invoiceLines         invoice lines
     * @param isVirtual            Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @return DOM element
     */
    protected Element createUAInvoiceCategoriesIL(Document doc, Invoice invoice,
                                                  UserAccount userAccount, List<InvoiceLine> invoiceLines, boolean isVirtual,
                                                  InvoiceConfiguration invoiceConfiguration, Map<String, String> mapTaxesIndexes) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String invoiceDateFormat = paramBean.getProperty(INVOICE_DATE_FORMAT, DEFAULT_DATE_PATTERN);
        String invoiceDateTimeFormat = paramBean.getProperty("invoice.dateTimeFormat", DEFAULT_DATE_TIME_PATTERN);
        String invoiceLanguageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
        List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<>();
        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof CategoryInvoiceAgregate && isValidCategoryInvoiceAgregate(userAccount, (CategoryInvoiceAgregate) invoiceAgregate)) {
                CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
                categoryInvoiceAgregates.add(categoryInvoiceAgregate);
            }
        }
        Collections.sort(categoryInvoiceAgregates, InvoiceCategoryComparatorUtils.getInvoiceCategoryComparator());
        Element categoriesTag = doc.createElement("categories");
        for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
            Element categoryTag = createDetailsUAInvoiceCategorySectionIL(doc, invoice, categoryInvoiceAgregate,
                    invoiceLines.stream()
                            .filter(invoiceLine -> categoryInvoiceAgregate.getInvoiceCategory() != null &&
                                    invoiceLine.getAccountingArticle().getInvoiceSubCategory().getInvoiceCategory().getId().equals(categoryInvoiceAgregate.getInvoiceCategory().getId()))
                            .collect(Collectors.toList()),
                    isVirtual, invoiceDateFormat, invoiceDateTimeFormat, invoiceLanguageCode, invoiceConfiguration, mapTaxesIndexes);
            if (categoryTag != null) {
                categoriesTag.appendChild(categoryTag);
            }
        }
        return categoriesTag;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category DOM element
     *
     * @param doc                     XML invoice DOM
     * @param invoice                 Invoice to convert
     * @param categoryInvoiceAggregate Invoice category aggregate
     * @param invoiceLines            invoice lines
     * @param isVirtual               Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceDateFormat       Date format
     * @param invoiceLanguageCode     Invoice language - language code
     * @return DOM element
     */
    protected Element createDetailsUAInvoiceCategorySectionIL(Document doc, Invoice invoice, CategoryInvoiceAgregate categoryInvoiceAggregate,
                                                              List<InvoiceLine> invoiceLines, boolean isVirtual, String invoiceDateFormat,
                                                              String invoiceDateTimeFormat, String invoiceLanguageCode, InvoiceConfiguration invoiceConfiguration,
                                                              Map<String, String> mapTaxesIndexes) {
        InvoiceCategory invoiceCategory = categoryInvoiceAggregate.getInvoiceCategory();
        String invoiceCategoryLabel = categoryInvoiceAggregate.getDescription();
        if (invoiceCategory != null && invoiceCategory.getDescriptionI18n() != null
                && invoiceCategory.getDescriptionI18n().get(invoiceLanguageCode) != null) {
            invoiceCategoryLabel = invoiceCategory.getDescriptionI18n().get(invoiceLanguageCode);
        }
        Element categoryTag = doc.createElement("category");
        categoryTag.setAttribute("label", getDefaultIfNull(invoiceCategoryLabel, ""));
        categoryTag.setAttribute("code", invoiceCategory != null ? invoiceCategory.getCode() : "");
        categoryTag.setAttribute("sortIndex", (invoiceCategory != null && invoiceCategory.getSortIndex() != null)
                ? invoiceCategory.getSortIndex() + "" : "");

        Element amountWithoutTax = doc.createElement("amountWithoutTax");
        amountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAggregate.getAmountWithoutTax())));
        categoryTag.appendChild(amountWithoutTax);

        Element transactionalAmountWithoutTax = doc.createElement("transactionalAmountWithoutTax");
        transactionalAmountWithoutTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAggregate.getTransactionalAmountWithoutTax())));
        categoryTag.appendChild(transactionalAmountWithoutTax);

        Element amountWithTax = doc.createElement("amountWithTax");
        amountWithTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAggregate.getAmountWithTax())));
        categoryTag.appendChild(amountWithTax);

        Element transactionalAmountWithTax = doc.createElement("transactionalAmountWithTax");
        transactionalAmountWithTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAggregate.getTransactionalAmountWithTax())));
        categoryTag.appendChild(transactionalAmountWithTax);

        Element amountTax = doc.createElement("amountTax");
        amountTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAggregate.getAmountTax())));
        categoryTag.appendChild(amountTax);

        Element transactionalAmountTax = doc.createElement("transactionalAmountTax");
        transactionalAmountTax.appendChild(this.createTextNode(doc, toPlainString(categoryInvoiceAggregate.getTransactionalAmountTax())));
        categoryTag.appendChild(transactionalAmountTax);

        addCustomFields(invoiceCategory, doc, categoryTag);

        Element subCategories = doc.createElement("subCategories");
        categoryTag.appendChild(subCategories);
        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<>(categoryInvoiceAggregate.getSubCategoryInvoiceAgregates());
        Collections.sort(subCategoryInvoiceAgregates, InvoiceCategoryComparatorUtils.getInvoiceSubCategoryComparator());
        for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
            if (subCatInvoiceAgregate.isDiscountAggregate()) {
                continue;
            }
            Element subCategory = createUAInvoiceSubcategorySectionIL(doc, subCatInvoiceAgregate, invoiceLines,
                    isVirtual, invoiceDateFormat, invoiceDateTimeFormat , invoiceConfiguration, mapTaxesIndexes);
            if (subCategory != null) {
                subCategories.appendChild(subCategory);
            }
        }
        return categoryTag;
    }

    /**
     * Create invoice/details/userAccounts/userAccount/categories/category/subcategories/subcategory DOM element
     *
     *
     * @param doc                   XML invoice DOM
     * @param subCatInvoiceAggregate Invoice subcategory aggregate
     * @param invoiceLines          invoice lines
     * @param isVirtual             Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param invoiceDateFormat     Date format
     * @param invoiceLanguageCode   Invoice language - language code
     * @return DOM element
     */
    protected Element createUAInvoiceSubcategorySectionIL(Document doc, SubCategoryInvoiceAgregate subCatInvoiceAggregate,
                                                          List<InvoiceLine> invoiceLines, boolean isVirtual, String invoiceDateFormat,
                                                          String invoiceLanguageCode, InvoiceConfiguration invoiceConfiguration,
                                                          Map<String, String> mapTaxesIndexes) {
        InvoiceSubCategory invoiceSubCat = subCatInvoiceAggregate.getInvoiceSubCategory();
        if (isVirtual) {
            invoiceLines = subCatInvoiceAggregate.getInvoiceLinesToAssociate();
        }
        String invoiceSubCategoryLabel = subCatInvoiceAggregate.getDescription();
        if (invoiceSubCat != null && invoiceSubCat.getDescriptionI18n() != null
                && invoiceSubCat.getDescriptionI18n().get(invoiceLanguageCode) != null) {
            invoiceSubCategoryLabel = invoiceSubCat.getDescriptionI18n().get(invoiceLanguageCode);
        }
        Element subCategory = doc.createElement("subCategory");
        subCategory.setAttribute("label", getDefaultIfNull(invoiceSubCategoryLabel, ""));
        subCategory.setAttribute("code", invoiceSubCat != null ? invoiceSubCat.getCode() : "");
        subCategory.setAttribute("amountWithoutTax", toPlainString(subCatInvoiceAggregate.getAmountWithoutTax()));
        subCategory.setAttribute("transactionalAmountWithoutTax", toPlainString(subCatInvoiceAggregate.getTransactionalAmountWithoutTax()));
        subCategory.setAttribute("amountWithTax", toPlainString(subCatInvoiceAggregate.getAmountWithTax()));
        subCategory.setAttribute("transactionalAmountWithTax", toPlainString(subCatInvoiceAggregate.getTransactionalAmountWithTax()));
        subCategory.setAttribute("amountTax", toPlainString(subCatInvoiceAggregate.getAmountTax()));
        subCategory.setAttribute("transactionalAmountTax", toPlainString(subCatInvoiceAggregate.getTransactionalAmountTax()));
        subCategory.setAttribute("sortIndex", (invoiceSubCat!= null && invoiceSubCat.getSortIndex() != null)
                ? invoiceSubCat.getSortIndex() + "" : "");
        for (InvoiceLine invoiceLine : invoiceLines) {
            if ((invoiceLine.getInvoiceAggregateF() != null && invoiceLine.getInvoiceAggregateF().getId() != null
                    && !invoiceLine.getInvoiceAggregateF().getId().equals(subCatInvoiceAggregate.getId()))
                    || (invoiceLine.getInvoiceAggregateF() != null && invoiceLine.getInvoiceAggregateF().getId() == null
                    && invoiceLine.getAccountingArticle() != null && !invoiceLine.getAccountingArticle().getInvoiceSubCategory().getId().equals(invoiceSubCat.getId()))) {
                continue;
            }
            Element ilTag = createILSection(doc, invoiceLine, invoiceDateFormat, invoiceConfiguration, mapTaxesIndexes);
            if (ilTag != null) {
                subCategory.appendChild(ilTag);
            }
        }
        addCustomFields(invoiceSubCat, doc, subCategory);
        return subCategory;
    }

    private Text createTextNode(Document doc, String data) {
        return doc.createTextNode(Objects.requireNonNullElse(data, ""));
    }

}