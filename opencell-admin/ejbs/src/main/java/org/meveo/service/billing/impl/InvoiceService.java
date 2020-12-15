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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.meveo.commons.utils.NumberUtils.round;
import static java.util.stream.Collectors.toList;
import static java.util.Optional.ofNullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ConfigurationException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.admin.util.PdfWaterMark;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.InvoiceNumberAssigned;
import org.meveo.event.qualifier.PDFGenerated;
import org.meveo.event.qualifier.Updated;
import org.meveo.event.qualifier.XMLGenerated;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.ReferenceDateEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.SubcategoryInvoiceAgregateAmount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.billing.TaxScriptService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * The Class InvoiceService.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Wassim Drira
 * @author Said Ramli
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class InvoiceService extends PersistenceService<Invoice> {

    /** The Constant INVOICE_ADJUSTMENT_SEQUENCE. */
    public final static String INVOICE_ADJUSTMENT_SEQUENCE = "INVOICE_ADJUSTMENT_SEQUENCE";

    /** The Constant INVOICE_SEQUENCE. */
    public final static String INVOICE_SEQUENCE = "INVOICE_SEQUENCE";

    private final static BigDecimal HUNDRED = new BigDecimal("100");

    /** The p DF parameters construction. */
    @EJB
    private PDFParametersConstruction pDFParametersConstruction;

    /** The xml invoice creator. */
    @EJB
    private XMLInvoiceCreator xmlInvoiceCreator;

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The billing account service. */
    @Inject
    private BillingAccountService billingAccountService;

    /** The rated transaction service. */
    @Inject
    private RatedTransactionService ratedTransactionService;

    /** The rejected billing account service. */
    @Inject
    private RejectedBillingAccountService rejectedBillingAccountService;

    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;

    /** The order service. */
    @Inject
    private OrderService orderService;

    /** The recorded invoice service. */
    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    /** The service singleton. */
    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private EmailSender emailSender;

    @EJB
    private InvoiceService invoiceService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubcategoryService;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private TaxScriptService taxScriptService;

    @Inject
    private TaxService taxService;

    @Inject
    private TaxClassService taxClassService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    @PDFGenerated
    private Event<Invoice> pdfGeneratedEventProducer;

    @Inject
    @XMLGenerated
    private Event<Invoice> xmlGeneratedEventProducer;

    @Inject
    @Updated
    private Event<BaseEntity> entityUpdatedEventProducer;

    @Inject
    @InvoiceNumberAssigned
    private Event<Invoice> invoiceNumberAssignedEventProducer;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    /** folder for pdf . */
    private String PDF_DIR_NAME = "pdf";

    /** folder for adjustment pdf. */
    private String ADJUSTEMENT_DIR_NAME = "invoiceAdjustmentPdf";

    /** template jasper name. */
    private String INVOICE_TEMPLATE_FILENAME = "invoice.jasper";

    /** date format. */
    private String DATE_PATERN = "yyyy.MM.dd";

    /** map used to store temporary jasper report. */
    private Map<String, JasperReport> jasperReportMap = new HashMap<>();

    /**
     * Description translation map.
     */
    private Map<String, String> descriptionMap = new HashMap<>();

    private static int rtPaginationSize = 30000;

    @PostConstruct
    private void init() {
        ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

        rtPaginationSize = paramBean.getPropertyAsInteger("invoicing.rtPaginationSize", 30000);
    }

    /**
     * Gets the invoice.
     *
     * @param invoiceNumber invoice's number
     * @param customerAccount customer account
     * @return invoice
     * @throws BusinessException business exception
     */
    public Invoice getInvoice(String invoiceNumber, CustomerAccount customerAccount) throws BusinessException {
        try {
            Query q = getEntityManager().createQuery("from Invoice where invoiceNumber = :invoiceNumber and billingAccount.customerAccount=:customerAccount");
            q.setParameter("invoiceNumber", invoiceNumber).setParameter("customerAccount", customerAccount);
            Object invoiceObject = q.getSingleResult();
            return (Invoice) invoiceObject;
        } catch (NoResultException e) {
            log.info("Invoice with invoice number {} was not found. Returning null.", invoiceNumber);
            return null;
        } catch (NonUniqueResultException e) {
            log.info("Multiple invoices with invoice number {} was found. Returning null.", invoiceNumber);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the invoice by number.
     *
     * @param invoiceNumber invoice's number
     * @return found invoice.
     * @throws BusinessException business exception
     */
    public Invoice getInvoiceByNumber(String invoiceNumber) throws BusinessException {
        return getInvoiceByNumber(invoiceNumber, invoiceTypeService.getDefaultCommertial());
    }

    /**
     * Find by invoice number and type.
     *
     * @param invoiceNumber invoice's number
     * @param invoiceType invoice's type
     * @return found invoice
     * @throws BusinessException business exception
     */
    public Invoice findByInvoiceNumberAndType(String invoiceNumber, InvoiceType invoiceType) throws BusinessException {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
        qb.addCriterion("i.invoiceNumber", "=", invoiceNumber, true);
        qb.addCriterionEntity("i.invoiceType", invoiceType);
        try {
            return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.info("Invoice with invoice number {} was not found. Returning null.", invoiceNumber);
            return null;
        } catch (NonUniqueResultException e) {
            log.info("Multiple invoices with invoice number {} was found. Returning null.", invoiceNumber);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the invoice by number.
     *
     * @param invoiceNumber invoice's number
     * @param invoiceType invoice's type
     * @return found invoice
     * @throws BusinessException business exception
     */
    public Invoice getInvoiceByNumber(String invoiceNumber, InvoiceType invoiceType) throws BusinessException {
        return findByInvoiceNumberAndType(invoiceNumber, invoiceType);
    }

    /**
     * Gets the invoices.
     *
     * @param billingRun instance of billing run
     * @return list of invoices related to given billing run
     * @throws BusinessException business exception
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> getInvoices(BillingRun billingRun) throws BusinessException {
        try {
            Query q = getEntityManager().createQuery("from Invoice where billingRun = :billingRun");
            q.setParameter("billingRun", billingRun);
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the invoices.
     *
     * @param billingAccount billing account
     * @param invoiceType invoice's type
     * @return list of invoice
     * @throws BusinessException business exception
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> getInvoices(BillingAccount billingAccount, InvoiceType invoiceType) throws BusinessException {
        try {
            Query q = getEntityManager().createQuery("from Invoice where billingAccount = :billingAccount and invoiceType=:invoiceType");
            q.setParameter("billingAccount", billingAccount);
            q.setParameter("invoiceType", invoiceType);
            List<Invoice> invoices = q.getResultList();
            log.info("getInvoices: founds {} invoices with BA_code={} and type={} ", invoices.size(), billingAccount.getCode(), invoiceType);
            return invoices;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Assign invoice number from reserve.
     *
     * @param invoice invoice
     * @param invoicesToNumberInfo instance of InvoicesToNumberInfo
     * @throws BusinessException business exception
     */
    private void assignInvoiceNumberFromReserve(Invoice invoice, InvoicesToNumberInfo invoicesToNumberInfo) throws BusinessException {
        InvoiceType invoiceType = invoice.getInvoiceType();
        String prefix = invoiceType.getPrefixEL();

        // TODO: 3508
        Seller seller = null;
        if (invoice.getBillingAccount() != null && invoice.getBillingAccount().getCustomerAccount() != null && invoice.getBillingAccount().getCustomerAccount().getCustomer() != null
                && invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller() != null) {
            seller = invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }

        InvoiceTypeSellerSequence invoiceTypeSellerSequence = invoiceType.getSellerSequenceByType(seller);
        if (invoiceTypeSellerSequence != null) {
            prefix = invoiceTypeSellerSequence.getPrefixEL();
        }

        if (prefix != null && !StringUtils.isBlank(prefix)) {
            prefix = evaluatePrefixElExpression(prefix, invoice);
        } else {
            prefix = "";
        }

        String invoiceNumber = invoicesToNumberInfo.nextInvoiceNumber();
        // request to store invoiceNo in alias field
        invoice.setAlias(invoiceNumber);
        invoice.setInvoiceNumber(prefix + invoiceNumber);

        invoiceNumberAssignedEventProducer.fire(invoice);
    }

    /**
     * Get a list of invoices that are validated, but PDF was not yet generated.
     *
     * @param billingRunId An optional billing run identifier for filtering
     * @return A list of invoice ids
     */
    public List<Long> getInvoicesIdsValidatedWithNoPdf(Long billingRunId) {

        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.validatedNoPdf", Long.class).getResultList();

        } else {
            return getEntityManager().createNamedQuery("Invoice.validatedNoPdfByBR", Long.class).setParameter("billingRunId", billingRunId).getResultList();
        }
    }

    /**
     * Get list of Draft invoice Ids that belong to the given Billing Run and not having PDF generated yet.
     *
     * @param billingRunId An optional billing run identifier for filtering
     * @return A list of invoice ids
     */
    public List<Long> getDraftInvoiceIdsByBRWithNoPdf(Long billingRunId) {
        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.draftNoPdf", Long.class).getResultList();

        } else {
            return getEntityManager().createNamedQuery("Invoice.draftNoPdfByBR", Long.class).setParameter("billingRunId", billingRunId).getResultList();
        }
    }

    /**
     * Get list of Draft and validated invoice Ids that belong to the given Billing Run and not having PDF generated yet.
     *
     * @param billingRunId An optional billing run identifier for filtering
     * @return A list of invoice ids
     */
    public List<Long> getInvoiceIdsIncludeDraftByBRWithNoPdf(Long billingRunId) {
        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.allNoPdf", Long.class).getResultList();

        } else {
            return getEntityManager().createNamedQuery("Invoice.allNoPdfByBR", Long.class).setParameter("billingRunId", billingRunId).getResultList();
        }
    }

    /**
     * Gets the invoice ids with no account operation.
     *
     * @param br billing run
     * @return list of invoice's which doesn't have the account operation.
     */
    public List<Long> getInvoiceIdsWithNoAccountOperation(BillingRun br) {
        try {
            QueryBuilder qb = queryInvoiceIdsWithNoAccountOperation(br);
            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to get invoices with no account operation", ex);
        }
        return null;
    }

    /**
     * Query invoice ids with no account operation.
     *
     * @param br the br
     * @return the query builder
     */
    private QueryBuilder queryInvoiceIdsWithNoAccountOperation(BillingRun br) {
        QueryBuilder qb = new QueryBuilder(Invoice.class, " i");
        qb.addSql("i.invoiceNumber is not null");
        qb.addSql("i.recordedInvoice is null");
        if (br != null) {
            qb.addCriterionEntity("i.billingRun", br);
        }
        return qb;
    }

    /**
     * @param br billing run
     * @param excludeInvoicesWithoutAmount exclude invoices without amount.
     * @return list of invoice's which doesn't have the account operation, and have an amount
     */
    public List<Long> queryInvoiceIdsWithNoAccountOperation(BillingRun br, boolean excludeInvoicesWithoutAmount, Boolean invoiceAccountable) {
        try {
            QueryBuilder qb = queryInvoiceIdsWithNoAccountOperation(br);
            if (excludeInvoicesWithoutAmount) {
                qb.addSql("i.amountWithTax != 0 ");
            }
            if (invoiceAccountable != null) {
                qb.addSql("i.invoiceType.invoiceAccountable = ".concat(invoiceAccountable.toString()));
            }
            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to get invoices with amount and with no account operation", ex);
        }
        return null;
    }

    /**
     * Get rated transactions for entity grouped by billing account, seller and invoice type and payment method
     *
     * @param entityToInvoice entity to be billed
     * @param billingAccount Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will
     *        be determined for each rated transaction.
     * @param billingRun billing run
     * @param defaultBillingCycle Billing cycle applicable to billable entity or to billing run
     * @param defaultInvoiceType Invoice type. A default invoice type for postpaid rated transactions. In case of prepaid RTs, a prepaid invoice type is used. Provided in case of
     *        Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @param ratedTransactionFilter rated transaction filter
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param isDraft Is it a draft invoice
     * @param defaultPaymentMethod The default payment method
     * @return List of rated transaction groups for entity and a flag indicating if there are more Rated transactions to retrieve
     * @throws BusinessException BusinessException
     */
    protected RatedTransactionsToInvoice getRatedTransactionGroups(IBillableEntity entityToInvoice, BillingAccount billingAccount, BillingRun billingRun, BillingCycle defaultBillingCycle, InvoiceType defaultInvoiceType,
            Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft, PaymentMethod defaultPaymentMethod) throws BusinessException {

        List<RatedTransaction> ratedTransactions = getRatedTransactions(entityToInvoice, ratedTransactionFilter, firstTransactionDate, lastTransactionDate, isDraft);

        // If retrieved RT and pagination size does not match, it means no more RTs are pending to be processed and invoice can be closed
        boolean moreRts = ratedTransactions.size() == rtPaginationSize;

        // Split RTs billing account groups to billing account/seller groups
        if (log.isDebugEnabled()) {
            log.debug("Split {} RTs for {}/{} in to billing account/seller/invoice type groups. {} RTs to retrieve.", ratedTransactions.size(), entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId(),
                moreRts ? "More" : "No more");
        }
        // Instantiated invoices. Key ba.id_seller.id_invoiceType.id
        Map<String, RatedTransactionGroup> rtGroups = new HashMap<>();

        BillingCycle billingCycle = defaultBillingCycle;
        InvoiceType postPaidInvoiceType = defaultInvoiceType;
        PaymentMethod paymentMethod = defaultPaymentMethod;
        if (defaultPaymentMethod == null && billingAccount != null) {
            defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
        }

        EntityManager em = getEntityManager();

        for (RatedTransaction rt : ratedTransactions) {

            // Order can span multiple billing accounts and some Billing account-dependent values have to be recalculated
            if (entityToInvoice instanceof Order) {
                // Retrieve BA and determine postpaid invoice type only if it has not changed from the last iteration
                if (billingAccount == null || !billingAccount.getId().equals(rt.getBillingAccount().getId())) {
                    billingAccount = rt.getBillingAccount();
                    if (defaultPaymentMethod == null && billingAccount != null) {
                        defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
                    }
                    if (defaultBillingCycle == null) {
                        billingCycle = billingAccount.getBillingCycle();
                    }
                    if (defaultInvoiceType == null) {
                        postPaidInvoiceType = determineInvoiceType(false, isDraft, billingCycle, billingRun, billingAccount);
                    }
                }
            }
            InvoiceType invoiceType = postPaidInvoiceType;
            boolean isPrepaid = rt.isPrepaid();
            if (isPrepaid) {
                invoiceType = determineInvoiceType(true, isDraft, null, null, null);
            }

            paymentMethod = resolvePaymentMethod(billingAccount, billingCycle, defaultPaymentMethod, rt);

            String invoiceKey = billingAccount.getId() + "_" + rt.getSeller().getId() + "_" + invoiceType.getId() + "_" + isPrepaid + ((paymentMethod == null)?"":"_" + paymentMethod.getId());
            RatedTransactionGroup rtGroup = rtGroups.get(invoiceKey);

            if (rtGroup == null) {
                rtGroup = new RatedTransactionGroup(billingAccount, rt.getSeller(), billingCycle != null ? billingCycle : billingAccount.getBillingCycle(), invoiceType, isPrepaid, invoiceKey, paymentMethod);
                rtGroups.put(invoiceKey, rtGroup);
            }
            rtGroup.getRatedTransactions().add(rt);

            em.detach(rt);
        }

        List<RatedTransactionGroup> convertedRtGroups = new ArrayList<>();

        // Check if any script to run to group rated transactions by invoice type or other parameters. Script accepts a RatedTransaction list object as an input.
        for (RatedTransactionGroup rtGroup : rtGroups.values()) {

            if (rtGroup.getBillingCycle().getScriptInstance() != null) {
                convertedRtGroups
                    .addAll(executeBCScript(billingRun, rtGroup.getInvoiceType(), rtGroup.getRatedTransactions(), entityToInvoice, rtGroup.getBillingCycle().getScriptInstance().getCode(), rtGroup.getPaymentMethod()));
            } else {
                convertedRtGroups.add(rtGroup);
            }
        }

        return new RatedTransactionsToInvoice(moreRts, convertedRtGroups);

    }

    private PaymentMethod resolvePaymentMethod(BillingAccount billingAccount, BillingCycle billingCycle, PaymentMethod defaultPaymentMethod, RatedTransaction rt) {
        if (BillingEntityTypeEnum.SUBSCRIPTION.equals(billingCycle.getType()) || (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && billingCycle.isSplitPerPaymentMethod())) {
            if (Objects.nonNull(rt.getSubscription().getPaymentMethod())) {
                return rt.getSubscription().getPaymentMethod();
            } else if (Objects.nonNull(billingAccount.getPaymentMethod())) {
                return billingAccount.getPaymentMethod();
            }
        }
        if (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && (!billingCycle.isSplitPerPaymentMethod() && Objects.nonNull(billingAccount.getPaymentMethod()))) {
            return billingAccount.getPaymentMethod();
        }
        return defaultPaymentMethod;
    }

    private List<RatedTransaction> getRatedTransactions(IBillableEntity entityToInvoice, Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft) {
        List<RatedTransaction> ratedTransactions = ratedTransactionService.listRTsToInvoice(entityToInvoice, firstTransactionDate, lastTransactionDate, ratedTransactionFilter, rtPaginationSize);
        // if draft add unrated wallet operation
        if (isDraft) {
            ratedTransactions.addAll(getDraftRatedTransactions(entityToInvoice, firstTransactionDate, lastTransactionDate));
        }
        return ratedTransactions;
    }

    private List<RatedTransaction> getDraftRatedTransactions(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate) {
        return ratedTransactionService.getWalletOperations(entityToInvoice, lastTransactionDate).stream()
            .filter(wo -> wo.getOperationDate().before(lastTransactionDate) && (wo.getOperationDate().after(firstTransactionDate) || wo.getOperationDate().equals(firstTransactionDate))).map(RatedTransaction::new)
            .collect(Collectors.toList());
    }

    private List<Long> getDrafWalletOperationIds(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate) {
        return ratedTransactionService.getWalletOperations(entityToInvoice, lastTransactionDate).stream()
            .filter(wo -> wo.getOperationDate().before(lastTransactionDate) && (wo.getOperationDate().after(firstTransactionDate) || wo.getOperationDate().equals(firstTransactionDate))).map(BaseEntity::getId)
            .collect(Collectors.toList());
    }

    private List<RatedTransaction> getDraftRatedTransactions(List<Long> walletOperationsIds) {
        return ratedTransactionService.getWalletOperations(walletOperationsIds).stream().map(RatedTransaction::new).collect(Collectors.toList());
    }

    /**
     * Creates invoices and their aggregates - IN new transaction
     *
     * @param entityToInvoice entity to be billed
     * @param billingRun billing run
     * @param ratedTransactionFilter rated transaction filter
     * @param invoiceDate date of invoice
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param minAmountForAccounts Check if min amount is enabled in any account level
     * @param isDraft Is this a draft invoice
     * @return A list of created invoices
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Invoice> createAgregatesAndInvoiceInNewTransaction(IBillableEntity entityToInvoice, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate, Date firstTransactionDate,
            Date lastTransactionDate, MinAmountForAccounts minAmountForAccounts, boolean isDraft) throws BusinessException {
        // MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts(instantiateMinRtsForBA, false, instantiateMinRtsForSubscription, instantiateMinRtsForService);
        return createAgregatesAndInvoice(entityToInvoice, billingRun, ratedTransactionFilter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft);
    }

    /**
     * Creates invoices and their aggregates
     *
     * @param entityToInvoice entity to be billed
     * @param billingRun billing run
     * @param ratedTransactionFilter rated transaction filter
     * @param invoiceDate date of invoice
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param minAmountForAccounts Check if min amount is enabled in any account level
     * @param isDraft Is this a draft invoice
     * @return A list of created invoices
     * @throws BusinessException business exception
     */
    public List<Invoice> createAgregatesAndInvoice(IBillableEntity entityToInvoice, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            MinAmountForAccounts minAmountForAccounts, boolean isDraft) throws BusinessException {

        log.debug("Will create invoice and aggregates for {}/{}", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());

        if (billingRun == null) {
            if (invoiceDate == null) {
                throw new BusinessException("invoiceDate must be set if billingRun is null");
            }
            if (StringUtils.isBlank(lastTransactionDate) && ratedTransactionFilter == null) {
                throw new BusinessException("lastTransactionDate or ratedTransactionFilter must be set if billingRun is null");
            }
        }

        // First retrieve it here as not to loose it if billable entity is not managed and has to be retrieved
        List<RatedTransaction> minAmountTransactions = entityToInvoice.getMinRatedTransactions();

        try {
            BillingAccount ba = null;

            if (entityToInvoice instanceof Subscription) {
                entityToInvoice = subscriptionService.retrieveIfNotManaged((Subscription) entityToInvoice);
                ba = ((Subscription) entityToInvoice).getUserAccount().getBillingAccount();
            } else if (entityToInvoice instanceof BillingAccount) {
                entityToInvoice = billingAccountService.retrieveIfNotManaged((BillingAccount) entityToInvoice);
                ba = (BillingAccount) entityToInvoice;
            } else if (entityToInvoice instanceof Order) {
                entityToInvoice = orderService.retrieveIfNotManaged((Order) entityToInvoice);
            }

            if (billingRun != null) {
                billingRun = billingRunService.retrieveIfNotManaged(billingRun);
            }

            if (firstTransactionDate == null) {
                firstTransactionDate = new Date(0);
            }

            if (billingRun != null) {
                lastTransactionDate = billingRun.getLastTransactionDate();
                invoiceDate = billingRun.getInvoiceDate();
            }

            if (Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"))) {
                lastTransactionDate = DateUtils.setDateToEndOfDay(lastTransactionDate);
            } else {
                lastTransactionDate = DateUtils.setDateToStartOfDay(lastTransactionDate);
            }

            // Instantiate additional RTs to reach minimum amount to invoice on service, subscription or BA level if needed
            if (minAmountForAccounts.isMinAmountCalculationActivated()) {
                ratedTransactionService.calculateAmountsAndCreateMinAmountTransactions(entityToInvoice, firstTransactionDate, lastTransactionDate, false, minAmountForAccounts);
                minAmountTransactions = entityToInvoice.getMinRatedTransactions();
            }

            BillingCycle billingCycle = billingRun != null ? billingRun.getBillingCycle() : entityToInvoice.getBillingCycle();
            if (billingCycle == null && !(entityToInvoice instanceof Order)) {
                billingCycle = ba.getBillingCycle();
            }

            // Payment method is calculated on Order or Customer Account level and will be the same for all rated transactions
            PaymentMethod paymentMethod = null;

            // Due balance are calculated on CA level and will be the same for all rated transactions
            BigDecimal balance = null;
            InvoiceType invoiceType = null;

            if (entityToInvoice instanceof Order) {
                paymentMethod = ((Order) entityToInvoice).getPaymentMethod();
            } else {
                // Calculate customer account balance
                boolean isBalanceDue = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.limitByDueDate", true);
                boolean isBalanceLitigation = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.includeLitigation", false);
                if (isBalanceLitigation) {
                    balance = customerAccountService.customerAccountBalanceDue(ba.getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                } else {
                    balance = customerAccountService.customerAccountBalanceDueWithoutLitigation(ba.getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                }

                invoiceType = determineInvoiceType(false, isDraft, billingCycle, billingRun, ba);
            }

            // Store RTs, to reach minimum amount per invoice, to DB
            if (minAmountTransactions != null && !minAmountTransactions.isEmpty()) {
                for (RatedTransaction minRatedTransaction : minAmountTransactions) {
                    // This is needed, as even if ratedTransactionService.create() is called and then sql is called to retrieve RTs, these minAmountTransactions will contain
                    // unmanaged
                    // BA and invoiceSubcategory entities
                    minRatedTransaction.setBillingAccount(billingAccountService.retrieveIfNotManaged(minRatedTransaction.getBillingAccount()));
                    minRatedTransaction.setInvoiceSubCategory(invoiceSubcategoryService.retrieveIfNotManaged(minRatedTransaction.getInvoiceSubCategory()));

                    ratedTransactionService.create(minRatedTransaction);
                }
                // Flush RTs to DB as next interaction with RT table will be via sqls only.
                commit();
            }

            return createAggregatesAndInvoiceFromRTs(entityToInvoice, billingRun, ratedTransactionFilter, invoiceDate, firstTransactionDate, lastTransactionDate, isDraft, billingCycle, ba, paymentMethod, invoiceType,
                balance);

        } catch (Exception e) {
            log.error("Error for entity {}", entityToInvoice.getCode(), e);
            if (entityToInvoice instanceof BillingAccount) {
                BillingAccount ba = (BillingAccount) entityToInvoice;
                if (billingRun != null) {
                    rejectedBillingAccountService.create(ba, getEntityManager().getReference(BillingRun.class, billingRun.getId()), e.getMessage());
                } else {
                    throw e instanceof BusinessException ? (BusinessException) e : new BusinessException(e);
                }
            } else {
                throw e instanceof BusinessException ? (BusinessException) e : new BusinessException(e);
            }
        }
        return null;
    }

    /**
     * Create invoices and aggregates for a given entity to invoice and date interval.
     *
     * @param entityToInvoice Entity to invoice
     * @param billingRun Billing run
     * @param ratedTransactionFilter Filter returning a list of rated transactions
     * @param invoiceDate Invoice date
     * @param firstTransactionDate Transaction usage date filter - start date
     * @param lastTransactionDate Transaction usage date filter - end date
     * @param isDraft Is it a draft invoice
     * @param defaultBillingCycle Billing cycle applicable to billable entity or to billing run. For Order, if not provided at order level, will have to be determined from Order's
     *        billing account.
     * @param billingAccount Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will
     *        be determined for each rated transaction.
     * @param defaultPaymentMethod Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore
     *        will be determined for each billing account occurrence.
     * @param defaultInvoiceType Invoice type. A default invoice type for postpaid rated transactions. In case of prepaid RTs, a prepaid invoice type is used. Provided in case of
     *        Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @param balance Balance due. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be
     *        determined for each billing account occurrence.
     * @return A list of invoices
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("unchecked")
    private List<Invoice> createAggregatesAndInvoiceFromRTs(IBillableEntity entityToInvoice, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            boolean isDraft, BillingCycle defaultBillingCycle, BillingAccount billingAccount, PaymentMethod defaultPaymentMethod, InvoiceType defaultInvoiceType, BigDecimal balance) throws BusinessException {

        List<Invoice> invoiceList = new ArrayList<>();
        boolean moreRatedTransactionsExpected = true;
        // Contains distinct Invoice information - one for each invoice produced. Map key is billingAccount.id_seller.id_invoiceType.id_isPrepaid
        Map<String, InvoiceAggregateProcessingInfo> rtGroupToInvoiceMap = new HashMap<>();

        boolean allRTsInOneRun = true;

        while (moreRatedTransactionsExpected) {

            if (entityToInvoice instanceof Order) {
                billingAccount = null;
                defaultInvoiceType = null;
            }

            // Retrieve Rated transactions and split them into BA/seller combinations
            RatedTransactionsToInvoice rtsToInvoice = getRatedTransactionGroups(entityToInvoice, billingAccount, billingRun, defaultBillingCycle, defaultInvoiceType, ratedTransactionFilter, firstTransactionDate,
                lastTransactionDate, isDraft, defaultPaymentMethod);

            List<RatedTransactionGroup> ratedTransactionGroupsPaged = rtsToInvoice.ratedTransactionGroups;
            moreRatedTransactionsExpected = rtsToInvoice.moreRatedTransactions;
            if (moreRatedTransactionsExpected) {
                allRTsInOneRun = false;
            }

            if (rtGroupToInvoiceMap.isEmpty() && ratedTransactionGroupsPaged.isEmpty()) {
                log.warn("Account {}/{} has no billable transactions", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());
                return new ArrayList<>();
                // throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));

                // Process newly retrieved rated transactions

            } else if (!ratedTransactionGroupsPaged.isEmpty()) {

                // Process each BA/seller/invoiceType combination separately, what corresponds to a separate invoice
                for (RatedTransactionGroup rtGroup : ratedTransactionGroupsPaged) {

                    // For order calculate for each BA
                    if (entityToInvoice instanceof Order) {
                        if (billingAccount == null || !billingAccount.getId().equals(rtGroup.getBillingAccount().getId())) {
                            billingAccount = rtGroup.getBillingAccount();
                            // Balance are calculated on CA level and will be the same for all rated transactions of same order
                            boolean isBalanceDue = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.due", true);
                            boolean isBalanceLitigation = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.litigation", false);
                            if (isBalanceLitigation) {
                                balance = customerAccountService.customerAccountBalanceDue(billingAccount.getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                            } else {
                                balance = customerAccountService.customerAccountBalanceDueWithoutLitigation(billingAccount.getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                            }
                        }
                    }

                    String invoiceKey = rtGroup.getInvoiceKey();

                    InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo = rtGroupToInvoiceMap.get(invoiceKey);
                    if (invoiceAggregateProcessingInfo == null) {
                        invoiceAggregateProcessingInfo = new InvoiceAggregateProcessingInfo();
                        rtGroupToInvoiceMap.put(invoiceKey, invoiceAggregateProcessingInfo);
                    }

                    if (invoiceAggregateProcessingInfo.invoice == null) {
                        invoiceAggregateProcessingInfo.invoice = instantiateInvoice(entityToInvoice, rtGroup.getBillingAccount(), rtGroup.getSeller(), billingRun, invoiceDate, isDraft, rtGroup.getBillingCycle(),
                            rtGroup.getPaymentMethod(), rtGroup.getInvoiceType(), rtGroup.isPrepaid(), balance);
                        invoiceList.add(invoiceAggregateProcessingInfo.invoice);
                    }

                    Invoice invoice = invoiceAggregateProcessingInfo.invoice;

                    // Create aggregates.
                    // Indicate that no more RTs to process only in case when all RTs were retrieved for processing in a single query page.
                    // In other case - need to close invoices when all RTs are processed
                    appendInvoiceAgregates(entityToInvoice, rtGroup.getBillingAccount(), invoice, rtGroup.getRatedTransactions(), false, invoiceAggregateProcessingInfo, !allRTsInOneRun);

                    // Collect information needed to update RTs with invoice information

//          Start of alternative 1 for 4326 // TODO 4326 alternative
                    List<Object[]> rtMassUpdates = new ArrayList<>();
                    List<Object[]> rtUpdates = new ArrayList<>();

                    for (SubCategoryInvoiceAgregate subAggregate : invoiceAggregateProcessingInfo.subCategoryAggregates.values()) {
                        if (subAggregate.getRatedtransactionsToAssociate() == null) {
                            continue;
                        }
                        List<Long> rtIds = new ArrayList<>();
                        List<RatedTransaction> rts = new ArrayList<>();

                        for (RatedTransaction rt : subAggregate.getRatedtransactionsToAssociate()) {

                            // Check that tax was not overridden in WO and tax recalculation should be ignored
                            if (rt.isTaxRecalculated()) {
                                rts.add(rt);
                            } else {
                                rtIds.add(rt.getId());
                            }
                        }

                        if (!rtIds.isEmpty()) {
                            rtMassUpdates.add(new Object[] { subAggregate, rtIds });
                        } else if (!rts.isEmpty()) {
                            rtUpdates.add(new Object[] { subAggregate, rts });
                        }
                        subAggregate.setRatedtransactionsToAssociate(new ArrayList<>());
                    }

                    setInvoiceDueDate(invoice, rtGroup.getBillingCycle());
                    setInitialCollectionDate(invoice, rtGroup.getBillingCycle(), billingRun);

// End of alternative 1 for 4326   
// Start of alternative 2 for 4326       
//            List<RatedTransaction> rtsToUpdate = new ArrayList<>();            
//                for (SubCategoryInvoiceAgregate subAggregate : invoiceAggregateProcessingInfo.subCategoryAggregates.values()) {
//                    if (subAggregate.getRatedtransactionsToAssociate() == null) {
//                        continue;
//                    }
//                    rtsToUpdate.addAll(subAggregate.getRatedtransactionsToAssociate());
//                }
// End of alternative 2 for 4326             

                    EntityManager em = getEntityManager();

                    // Save invoice and its aggregates during the first pagination run, or save only newly created aggregates during later pagination runs
                    if (invoice.getId() == null) {
                        this.create(invoice);

                    } else {
                        for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
                            if (invoiceAggregate.getId() == null) {
                                em.persist(invoiceAggregate);
                            }
                        }
                    }

                    // Update RTs with invoice information

                    // AKK alternative 1 for 4326

                    em.flush(); // Need to flush, so RTs can be updated in mass

                    Date now = new Date();
                    for (Object[] aggregateAndRtIds : rtMassUpdates) {
                        SubCategoryInvoiceAgregate subCategoryAggregate = (SubCategoryInvoiceAgregate) aggregateAndRtIds[0];
                        List<Long> rtIds = (List<Long>) aggregateAndRtIds[1];
                        em.createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfo").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("invoiceAgregateF", subCategoryAggregate)
                            .setParameter("now", now).setParameter("ids", rtIds).executeUpdate();
                    }

                    for (Object[] aggregateAndRts : rtUpdates) {
                        SubCategoryInvoiceAgregate subCategoryAggregate = (SubCategoryInvoiceAgregate) aggregateAndRts[0];
                        List<RatedTransaction> rts = (List<RatedTransaction>) aggregateAndRts[1];
                        for (RatedTransaction rt : rts) {
                            em.createNamedQuery("RatedTransaction.updateWithInvoiceInfo").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("invoiceAgregateF", subCategoryAggregate)
                                .setParameter("now", now).setParameter("id", rt.getId()).setParameter("unitAmountWithoutTax", rt.getUnitAmountWithoutTax()).setParameter("unitAmountWithTax", rt.getUnitAmountWithTax())
                                .setParameter("unitAmountTax", rt.getUnitAmountTax()).setParameter("amountWithoutTax", rt.getAmountWithoutTax()).setParameter("amountWithTax", rt.getAmountWithTax())
                                .setParameter("amountTax", rt.getAmountTax()).setParameter("tax", rt.getTax()).setParameter("taxPercent", rt.getTaxPercent()).executeUpdate();
                        }
                    }
                    // End of alternative 1 for 4326
                    // Start of alternative 2 for 4326
                    // ratedTransactionService.updateViaDeleteAndInsert(rtsToUpdate);
                    // End of alternative 2 for 4326
                }
            }
        }

        // Finalize invoices

        for (InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo : rtGroupToInvoiceMap.values()) {

            // Create discount, category and tax aggregates if not all RTs were retrieved and processed in a single page
            if (!allRTsInOneRun) {
                addDiscountCategoryAndTaxAggregates(invoiceAggregateProcessingInfo.invoice, invoiceAggregateProcessingInfo.subCategoryAggregates.values());
            }

            // Link orders to invoice
            Set<String> orderNums = invoiceAggregateProcessingInfo.orderNumbers;
            if (entityToInvoice instanceof Order) {
                orderNums.add(((Order) entityToInvoice).getOrderNumber());
            }
            if (orderNums != null && !orderNums.isEmpty()) {
                List<Order> orders = orderService.findByCodeOrExternalId(orderNums);
                if (!orders.isEmpty()) {
                    invoiceAggregateProcessingInfo.invoice.setOrders(orders);
                }
            }

            invoiceAggregateProcessingInfo.invoice.assignTemporaryInvoiceNumber();
            postCreate(invoiceAggregateProcessingInfo.invoice);
        }

        return invoiceList;

    }

    private void setInitialCollectionDate(Invoice invoice, BillingCycle billingCycle, BillingRun billingRun) {

        if (billingCycle.getCollectionDateDelayEl() == null) {
            invoice.setInitialCollectionDate(invoice.getDueDate());
            return;
        }
        if (billingRun != null && billingRun.getCollectionDate() != null) {
            invoice.setInitialCollectionDate(billingRun.getCollectionDate());
            return;
        }
        BillingAccount billingAccount = invoice.getBillingAccount();
        Order order = invoice.getOrder();

        // Determine invoice due date delay either from Order, Customer account or Billing cycle
        Integer delay = 0;
        delay = evaluateCollectionDelayExpression(billingCycle.getCollectionDateDelayEl(), billingAccount, invoice, order);
        if (delay == null) {
            throw new BusinessException("collection date delay is null");
        }

        Date initailCollectionDate = DateUtils.addDaysToDate(invoice.getDueDate(), delay);

        invoice.setInitialCollectionDate(initailCollectionDate);

    }

    private Integer evaluateCollectionDelayExpression(String expression, BillingAccount billingAccount, Invoice invoice, Order order) {
        Integer result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE) >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("order") >= 0) {
            userMap.put("order", order);
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Integer.class);
        try {
            result = (Integer) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to Integer but " + res);
        }
        return result;
    }

    /**
     * Check if the electronic billing is enabled.
     *
     * @param invoice the invoice.
     * @return True if electronic billing is enabled for any Billable entity, false else.
     */
    private boolean isElectronicBillingEnabled(Invoice invoice) {
        boolean isElectronicBillingEnabled = false;

        if (invoice.getBillingAccount() != null) {
            isElectronicBillingEnabled = invoice.getBillingAccount().getElectronicBilling();
        }
        if (invoice.getSubscription() != null) {
            isElectronicBillingEnabled = invoice.getSubscription().getElectronicBilling();
        }
        if (invoice.getOrder() != null) {
            isElectronicBillingEnabled = invoice.getOrder().getElectronicBilling();
        }
        return isElectronicBillingEnabled;
    }

    /**
     * Execute a script to group rated transactions by invoice type
     *
     * @param billingRun Billing run
     * @param invoiceType Current Invoice type
     * @param ratedTransactions Rated transactions to group
     * @param entity Entity to invoice
     * @param scriptInstanceCode Script to execute
     * @return A list of rated transaction groups
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    private List<RatedTransactionGroup> executeBCScript(BillingRun billingRun, InvoiceType invoiceType, List<RatedTransaction> ratedTransactions, IBillableEntity entity, String scriptInstanceCode,
            PaymentMethod paymentMethod) throws BusinessException {

        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put(Script.CONTEXT_ENTITY, entity);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        context.put("br", billingRun);
        context.put("invoiceType", invoiceType);
        context.put("ratedTransactions", ratedTransactions);
        context.put("paymentMethod", paymentMethod);
        scriptInstanceService.executeCached(scriptInstanceCode, context);
        return (List<RatedTransactionGroup>) context.get(Script.RESULT_VALUE);
    }

    /**
     * Creates Invoice and its aggregates in memory.
     *
     * @param ratedTransactions list of rated transaction
     * @param billingAccount billing account
     * @param invoiceType type of invoice
     * @return invoice
     * @throws BusinessException business exception
     */
    public Invoice createAgregatesAndInvoiceVirtual(List<RatedTransaction> ratedTransactions, BillingAccount billingAccount, InvoiceType invoiceType) throws BusinessException {

        if (invoiceType == null) {
            invoiceType = invoiceTypeService.getDefaultCommertial();
        }
        Invoice invoice = new Invoice();
        invoice.setSeller(billingAccount.getCustomerAccount().getCustomer().getSeller());
        invoice.setInvoiceType(invoiceType);
        invoice.setBillingAccount(billingAccount);
        invoice.setInvoiceDate(new Date());
        serviceSingleton.assignInvoiceNumberVirtual(invoice);
        PaymentMethod preferedPaymentMethod = invoice.getBillingAccount().getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            invoice.setPaymentMethodType(preferedPaymentMethod.getPaymentType());
        }

        appendInvoiceAgregates(billingAccount, billingAccount, invoice, ratedTransactions, false, null, false);
        invoice.setTemporaryInvoiceNumber(UUID.randomUUID().toString());

        return invoice;
    }

    /**
     * Find by billing run.
     *
     * @param billingRun billing run
     * @return list of invoice for given billing run
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findByBillingRun(BillingRun billingRun) {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
        qb.addCriterionEntity("billingRun", billingRun);

        try {
            return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to find by billingRun", e);
            return null;
        }
    }

    /**
     * Produce invoice pdf in new transaction.
     *
     * @param invoiceId id of invoice
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceInvoicePdfInNewTransaction(Long invoiceId, List<Long> walletOperationIds) throws BusinessException {
        Invoice invoice = findById(invoiceId);
        invoice.setDraftRatedTransactions(getDraftRatedTransactions(walletOperationIds));
        produceInvoicePdf(invoice);
    }

    /**
     * Produce invoice's PDF file and update invoice record in DB.
     *
     *
     * @param invoice Invoice
     * @return Update invoice entity
     * @throws BusinessException business exception
     */
    public Invoice produceInvoicePdf(Invoice invoice) throws BusinessException {

        produceInvoicePdfNoUpdate(invoice);
        invoice.setStatus(InvoiceStatusEnum.GENERATED);

        pdfGeneratedEventProducer.fire(invoice);

        invoice = updateNoCheck(invoice);
        entityUpdatedEventProducer.fire(invoice);
        return invoice;
    }

    /**
     * Produce invoice. v5.0 Refresh jasper template without restarting wildfly
     *
     * @author akadid abdelmounaim
     * @param invoice invoice to generate pdf
     * @throws BusinessException business exception
     * @lastModifiedVersion 5.0
     */
    public void produceInvoicePdfNoUpdate(Invoice invoice) throws BusinessException {
        log.debug("Creating pdf for invoice id={} number={}", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber());

        ParamBean paramBean = paramBeanFactory.getInstance();
        String meveoDir = paramBean.getChrootDir(currentUser.getProviderCode()) + File.separator;
        String invoiceXmlFileName = getFullXmlFilePath(invoice, false);
        Map<String, Object> parameters = pDFParametersConstruction.constructParameters(invoice, currentUser.getProviderCode());

        String INVOICE_TAG_NAME = "invoice";

        boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());

        File invoiceXmlFile = new File(invoiceXmlFileName);
        if (!invoiceXmlFile.exists()) {
            produceInvoiceXmlNoUpdate(invoice);
        }

        BillingAccount billingAccount = invoice.getBillingAccount();

        BillingCycle billingCycle = null;
        if (billingAccount != null && billingAccount.getBillingCycle() != null) {
            billingCycle = billingAccount.getBillingCycle();
        }

        String billingTemplateName = getInvoiceTemplateName(invoice, billingCycle, invoice.getInvoiceType());

        String resDir = meveoDir + "jasper";

        String pdfFilename = getOrGeneratePdfFilename(invoice);
        invoice.setPdfFilename(pdfFilename);
        String pdfFullFilename = getFullPdfFilePath(invoice, true);
        InputStream reportTemplate = null;
        try {
            File destDir = new File(resDir + File.separator + billingTemplateName + File.separator + "pdf");

            if (!destDir.exists()) {

                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + File.separator + billingTemplateName + File.separator + "invoice";

                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    VirtualFile vfDir = VFS
                        .getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/" + billingTemplateName + File.separator + "invoice");
                    log.info("default jaspers path :" + vfDir.getPathName());
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFile = new File(vfPath.getPath());

                    // if (!sourceFile.exists()) {
                    //
                    // sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + "default/invoice";
                    // sourceFile = new File(sourcePath);

                    if (!sourceFile.exists()) {
                        throw new BusinessException("embedded jasper report for invoice is missing..");
                    }
                    // }
                }
                destDir.mkdirs();
                FileUtils.copyDirectory(sourceFile, destDir);
            }

            File destDirInvoiceAdjustment = new File(resDir + File.separator + billingTemplateName + File.separator + "invoiceAdjustmentPdf");
            if (!destDirInvoiceAdjustment.exists() && isInvoiceAdjustment) {
                destDirInvoiceAdjustment.mkdirs();
                String sourcePathInvoiceAdjustment = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + File.separator + billingTemplateName + "/invoiceAdjustment";
                File sourceFileInvoiceAdjustment = new File(sourcePathInvoiceAdjustment);
                if (!sourceFileInvoiceAdjustment.exists()) {
                    VirtualFile vfDir = VFS
                        .getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/" + billingTemplateName + "/invoiceAdjustment");
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFileInvoiceAdjustment = new File(vfPath.getPath());
                    if (!sourceFileInvoiceAdjustment.exists()) {

                        URL resource = Thread.currentThread().getContextClassLoader().getResource("./jasper/" + billingTemplateName + "/invoiceAdjustment");

                        if (resource == null) {
                            resource = Thread.currentThread().getContextClassLoader().getResource("./jasper/default/invoiceAdjustment");
                        }

                        if (resource == null) {
                            throw new BusinessException("embedded InvoiceAdjustment jasper report for invoice is missing!");
                        }

                        sourcePathInvoiceAdjustment = resource.getPath();

                        if (!sourceFileInvoiceAdjustment.exists()) {
                            throw new BusinessException("embedded jasper report for invoice is missing.");
                        }

                    }
                }
                FileUtils.copyDirectory(sourceFileInvoiceAdjustment, destDirInvoiceAdjustment);

            }

            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            PaymentMethod preferedPaymentMethod = customerAccount.getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;

            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }

            File jasperFile = getJasperTemplateFile(resDir, billingTemplateName, paymentMethodEnum, isInvoiceAdjustment);
            if (!jasperFile.exists()) {
                throw new InvoiceJasperNotFoundException("The jasper file doesn't exist.");
            }
            log.debug("Jasper template used: {}", jasperFile.getCanonicalPath());

            reportTemplate = new FileInputStream(jasperFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDocument = db.parse(invoiceXmlFile);
            xmlDocument.getDocumentElement().normalize();
            Node invoiceNode = xmlDocument.getElementsByTagName(INVOICE_TAG_NAME).item(0);
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            trans.transform(new DOMSource(xmlDocument), new StreamResult(writer));

            JRXmlDataSource dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(invoiceNode).getBytes(StandardCharsets.UTF_8)), "/invoice");

            String fileKey = jasperFile.getPath() + jasperFile.lastModified();
            JasperReport jasperReport = jasperReportMap.get(fileKey);
            if (jasperReport == null) {
                jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
                jasperReportMap.put(fileKey, jasperReport);
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
            JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.xpath.executer.factory", "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFullFilename);
            if ("true".equals(paramBeanFactory.getInstance().getProperty("invoice.pdf.addWaterMark", "true"))) {
                if (invoice.getInvoiceType().getCode().equals(paramBeanFactory.getInstance().getProperty("invoiceType.draft.code", "DRAFT")) || (invoice.isDraft() != null && invoice.isDraft())) {
                    PdfWaterMark.add(pdfFullFilename, paramBean.getProperty("invoice.pdf.waterMark", "PROFORMA"), null);
                }
            }
            invoice.setPdfFilename(pdfFilename);

            log.info("PDF file '{}' produced for invoice {}", pdfFullFilename, invoice.getInvoiceNumberOrTemporaryNumber());

        } catch (IOException | JRException | TransformerException | ParserConfigurationException | SAXException e) {
            throw new BusinessException("Failed to generate a PDF file for " + pdfFilename, e);
        } finally {
            IOUtils.closeQuietly(reportTemplate);
        }
    }

    /**
     * Delete invoice's PDF file.
     *
     * @param invoice Invoice
     * @return True if file was deleted
     * @throws BusinessException business exception
     */
    public Invoice deleteInvoicePdf(Invoice invoice) throws BusinessException {

        String pdfFilename = getFullPdfFilePath(invoice, false);

        invoice.setPdfFilename(null);
        invoice = update(invoice);

        File file = new File(pdfFilename);
        if (file.exists()) {
            file.delete();
        }
        return invoice;
    }

    /**
     * Gets the jasper template file.
     *
     * @param resDir resource directory
     * @param billingTemplate billing template
     * @param paymentMethod payment method
     * @param isInvoiceAdjustment true/false
     * @return jasper file
     */
    private File getJasperTemplateFile(String resDir, String billingTemplate, PaymentMethodEnum paymentMethod, boolean isInvoiceAdjustment) {
        String pdfDirName = new StringBuilder(resDir).append(File.separator).append(billingTemplate).append(File.separator).append(isInvoiceAdjustment ? ADJUSTEMENT_DIR_NAME : PDF_DIR_NAME).toString();

        File pdfDir = new File(pdfDirName);
        String paymentMethodFileName = new StringBuilder("invoice_").append(paymentMethod).append(".jasper").toString();
        File paymentMethodFile = new File(pdfDir, paymentMethodFileName);

        if (paymentMethodFile.exists()) {
            return paymentMethodFile;
        } else {
            File defaultTemplate = new File(pdfDir, INVOICE_TEMPLATE_FILENAME);
            return defaultTemplate;
        }
    }

    /**
     * Gets the node xml string.
     *
     * @param node instance of Node.
     * @return xml node as string
     */
    protected String getNodeXmlString(Node node) {
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(buffer));
            return buffer.toString();
        } catch (Exception e) {
            log.error("Error converting xml node to its string representation. {}", e);
            throw new ConfigurationException();
        }
    }

    /**
     * Format invoice date.
     *
     * @param invoiceDate invoice date
     * @return invoice date as string
     */
    public String formatInvoiceDate(Date invoiceDate) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATERN);
        return dateFormat.format(invoiceDate);
    }

    /**
     * Evaluate prefix el expression.
     *
     * @param prefix prefix of EL expression
     * @param invoice invoice
     * @return evaluated value
     * @throws BusinessException business exception
     */
    public static String evaluatePrefixElExpression(String prefix, Invoice invoice) throws BusinessException {

        if (StringUtils.isBlank(prefix)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (prefix.indexOf("entity") >= 0) {
            userMap.put("entity", invoice);
        }
        if (prefix.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }

        String result = ValueExpressionWrapper.evaluateExpression(prefix, userMap, String.class);

        return result;
    }
//
//    /**
//     * Recompute aggregates.
//     *
//     * @param invoice invoice
//     * @throws BusinessException business exception
//     */
//    public void recomputeAggregates(Invoice invoice) throws BusinessException {
//
//        boolean entreprise = appProvider.isEntreprise();
//        int invoiceRounding = appProvider.getInvoiceRounding();
//        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
//
//        BillingAccount billingAccount = billingAccountService.findById(invoice.getBillingAccount().getId());
//        BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;
//
//        Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();
//        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregate>();
//        invoice.setAmountTax(null);
//        invoice.setAmountWithoutTax(null);
//        invoice.setAmountWithTax(null);
//
//        // update the aggregated subcat of an invoice
//        for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
//            if (invoiceAggregate instanceof CategoryInvoiceAgregate) {
//                invoiceAggregate.resetAmounts();
//            } else if (invoiceAggregate instanceof TaxInvoiceAgregate) {
//                TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAggregate;
//                taxInvoiceAgregateMap.put(taxInvoiceAgregate.getTax().getId(), taxInvoiceAgregate);
//            } else if (invoiceAggregate instanceof SubCategoryInvoiceAgregate) {
//                subCategoryInvoiceAgregates.add((SubCategoryInvoiceAgregate) invoiceAggregate);
//            }
//        }
//
//        for (TaxInvoiceAgregate taxInvoiceAgregate : taxInvoiceAgregateMap.values()) {
//            taxInvoiceAgregate.setAmountWithoutTax(new BigDecimal(0));
//            for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : subCategoryInvoiceAgregates) {
//                if (subCategoryInvoiceAgregate.getQuantity().signum() != 0) {
//                    if (subCategoryInvoiceAgregate.getTax().equals(taxInvoiceAgregate.getTax())) {
//                        taxInvoiceAgregate.addAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
//                    }
//                }
//            }
//
//            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountWithoutTax().multiply(taxInvoiceAgregate.getTaxPercent()).divide(new BigDecimal("100")));
//            // then round the tax
//            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//
//            taxInvoiceAgregate.setAmountWithTax(taxInvoiceAgregate.getAmountWithoutTax().add(taxInvoiceAgregate.getAmountTax()));
//        }
//
//        // update the amount with and without tax of all the tax aggregates in each sub category aggregate
//        SubCategoryInvoiceAgregate biggestSubCat = null;
//        BigDecimal biggestAmount = new BigDecimal("-100000000");
//
//        for (InvoiceAgregate invoiceAgregate : subCategoryInvoiceAgregates) {
//            SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;
//
//            if (!entreprise) {
//                nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add(subCategoryInvoiceAgregate.getAmountWithTax());
//            }
//
//            BigDecimal amountWithoutTax = subCategoryInvoiceAgregate.getAmountWithoutTax();
//            subCategoryInvoiceAgregate.setAmountWithoutTax(amountWithoutTax != null ? amountWithoutTax.setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()) : BigDecimal.ZERO);
//
//            subCategoryInvoiceAgregate.getCategoryInvoiceAgregate().addAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
//
//            if (subCategoryInvoiceAgregate.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
//                biggestAmount = subCategoryInvoiceAgregate.getAmountWithoutTax();
//                biggestSubCat = subCategoryInvoiceAgregate;
//            }
//        }
//
//        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
//            if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
//                CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
//                invoice.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//            }
//
//            if (invoiceAgregate instanceof TaxInvoiceAgregate) {
//                TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
//                invoice.addAmountTax(taxInvoiceAgregate.getAmountTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//            }
//        }
//
//        if (invoice.getAmountWithoutTax() != null) {
//            invoice.setAmountWithTax(invoice.getAmountWithoutTax().add(invoice.getAmountTax() == null ? BigDecimal.ZERO : invoice.getAmountTax()));
//        }
//
//        if (!entreprise && biggestSubCat != null && !billingAccountService.isExonerated(billingAccount)) {
//            BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
//            log.debug("delta={}-{}={}", nonEnterprisePriceWithTax, invoice.getAmountWithTax(), delta);
//
//            biggestSubCat.setAmountWithoutTax(biggestSubCat.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//            Tax tax = biggestSubCat.getTax();
//            TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(tax.getId());
//            log.debug("tax3 ht={}", invoiceAgregateT.getAmountWithoutTax());
//
//            invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//            log.debug("tax4 ht={}", invoiceAgregateT.getAmountWithoutTax());
//
//            CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
//            invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//
//            invoice.setAmountWithoutTax(invoice.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//            invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//        }
//
//        // calculate discounts here
//        // no need to create discount aggregates we will use the one from
//        // adjustedInvoice
//
//        Object[] discountAmount = invoiceAgregateService.findTotalAmountsForDiscountAggregates(getLinkedInvoice(invoice));
//        BigDecimal discountAmountWithoutTax = (BigDecimal) discountAmount[0];
//        BigDecimal discountAmountTax = (BigDecimal) discountAmount[1];
//        BigDecimal discountAmountWithTax = (BigDecimal) discountAmount[2];
//
//        log.debug("discountAmountWithoutTax= {}, discountAmountTax={}, discountAmountWithTax={}", discountAmount[0], discountAmount[1], discountAmount[2]);
//
//        invoice.addAmountWithoutTax(round(discountAmountWithoutTax, invoiceRounding, invoiceRoundingMode));
//        invoice.addAmountTax(round(discountAmountTax, invoiceRounding, invoiceRoundingMode));
//        invoice.addAmountWithTax(round(discountAmountWithTax, invoiceRounding, invoiceRoundingMode));
//
//        // compute net to pay
//        BigDecimal netToPay = BigDecimal.ZERO;
//        if (entreprise) {
//            netToPay = invoice.getAmountWithTax();
//        } else {
//            BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount().getCustomerAccount().getCode(), invoice.getDueDate());
//
//            if (balance == null) {
//                throw new BusinessException("account balance calculation failed");
//            }
//            netToPay = invoice.getAmountWithTax().add(round(balance, invoiceRounding, invoiceRoundingMode));
//        }
//
//        invoice.setNetToPay(netToPay);
//    }
//
//    /**
//     * Recompute sub category aggregate.
//     *
//     * @param invoice invoice used to recompute
//     */
//    public void recomputeSubCategoryAggregate(Invoice invoice) {
//
//        int invoiceRounding = appProvider.getInvoiceRounding();
//        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
//
//        List<TaxInvoiceAgregate> taxInvoiceAgregates = new ArrayList<TaxInvoiceAgregate>();
//        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregate>();
//
//        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
//            if (invoiceAgregate instanceof TaxInvoiceAgregate) {
//                taxInvoiceAgregates.add((TaxInvoiceAgregate) invoiceAgregate);
//            } else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
//                subCategoryInvoiceAgregates.add((SubCategoryInvoiceAgregate) invoiceAgregate);
//            }
//        }
//
//        for (TaxInvoiceAgregate taxInvoiceAgregate : taxInvoiceAgregates) {
//            taxInvoiceAgregate.setAmountWithoutTax(new BigDecimal(0));
//            for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : subCategoryInvoiceAgregates) {
//                if (subCategoryInvoiceAgregate.getQuantity().signum() != 0) {
//                    if (subCategoryInvoiceAgregate.getTax().equals(taxInvoiceAgregate.getTax())) {
//                        taxInvoiceAgregate.addAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
//                    }
//                }
//            }
//
//            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountWithoutTax().multiply(taxInvoiceAgregate.getTaxPercent()).divide(new BigDecimal("100")));
//            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
//
//            taxInvoiceAgregate.setAmountWithTax(taxInvoiceAgregate.getAmountWithoutTax().add(taxInvoiceAgregate.getAmountTax()));
//        }
//    }

    /**
     * Find invoices by type.
     *
     * @param invoiceType invoice type
     * @param ba billing account
     * @return list of invoice for given type
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findInvoicesByType(InvoiceType invoiceType, BillingAccount ba) {
        List<Invoice> result = new ArrayList<Invoice>();
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
        qb.addCriterionEntity("billingAccount", ba);
        qb.addCriterionEntity("invoiceType", invoiceType);
        try {
            result = (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
        }
        return result;
    }

    /**
     * Get a full path to an invoice's XML file.
     *
     * @param invoice Invoice
     * @param createDirs Should missing directories be created
     * @return Absolute path to an XML file
     */
    public String getFullXmlFilePath(Invoice invoice, boolean createDirs) {

        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

        String xmlFilename = meveoDir + "invoices" + File.separator + "xml" + File.separator + getOrGenerateXmlFilename(invoice);

        if (createDirs) {
            int pos = Integer.max(xmlFilename.lastIndexOf("/"), xmlFilename.lastIndexOf("\\"));
            String dir = xmlFilename.substring(0, pos);
            (new File(dir)).mkdirs();
        }

        return xmlFilename;
    }

    /**
     * Return a XML filename that was assigned to invoice, or in case it was not assigned yet - generate a filename. A default XML filename is
     * invoiceDateOrBillingRunId/invoiceNumber.pdf or invoiceDateOrBillingRunId/_IA_invoiceNumber.pdf for adjustment invoice
     *
     * @param invoice Invoice
     * @return XML file name
     */
    public String getOrGenerateXmlFilename(Invoice invoice) {
        ParamBean paramBean = paramBeanFactory.getInstance();

        if (invoice.getXmlFilename() != null) {
            return invoice.getXmlFilename();
        }

        // Generate a name for xml file from EL expression
        String xmlFileName = null;
        String expression = invoice.getInvoiceType().getXmlFilenameEL();
        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<Object, Object>();
            contextMap.put("invoice", invoice);

            try {
                String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);
                if (value != null) {
                    xmlFileName = value;
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default XML filename will be used instead. Error is logged in EL evaluation
            }
        }

        // Default to invoiceDateOrBillingRunId/invoiceNumber.xml or invoiceDateOrBillingRunId/_IA_invoiceNumber.xml for adjustment invoice
        if (StringUtils.isBlank(xmlFileName)) {

            boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());

            BillingRun billingRun = invoice.getBillingRun();
            String brPath = billingRun == null ? DateUtils.formatDateWithPattern(invoice.getInvoiceDate(), paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss")) : billingRun.getId().toString();

            xmlFileName = brPath + File.separator + (isInvoiceAdjustment ? paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") : "")
                    + (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber());
        }

        if (xmlFileName != null && !xmlFileName.toLowerCase().endsWith(".xml")) {
            xmlFileName = xmlFileName + ".xml";
        }
        xmlFileName = StringUtils.normalizeFileName(xmlFileName);
        return xmlFileName;
    }

    /**
     * Get a full path to an invoice's PDF file.
     *
     *
     * @param invoice Invoice
     * @param createDirs Should missing directories be created
     * @return Absolute path to a PDF file
     */
    public String getFullPdfFilePath(Invoice invoice, boolean createDirs) {

        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

        String pdfFilename = meveoDir + "invoices" + File.separator + "pdf" + File.separator + getOrGeneratePdfFilename(invoice);

        if (createDirs) {
            int pos = Integer.max(pdfFilename.lastIndexOf("/"), pdfFilename.lastIndexOf("\\"));
            String dir = pdfFilename.substring(0, pos);
            (new File(dir)).mkdirs();
        }

        return pdfFilename;
    }

    /**
     * Return a pdf filename that was assigned to invoice, or in case it was not assigned yet - generate a filename. A default PDF filename is invoiceDate_invoiceNumber.pdf or
     * invoiceDate_IA_invoiceNumber.pdf for adjustment invoice
     *
     *
     * @param invoice Invoice
     * @return Pdf file name
     */
    public String getOrGeneratePdfFilename(Invoice invoice) {

        if (invoice.getPdfFilename() != null) {
            return invoice.getPdfFilename();
        }

        // Generate a name for pdf file from EL expression
        String pdfFileName = null;
        String expression = invoice.getInvoiceType().getPdfFilenameEL();
        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<Object, Object>();
            contextMap.put("invoice", invoice);

            try {
                String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);

                if (value != null) {
                    pdfFileName = value;
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
            }
        }

        // Default to invoiceDate_invoiceNumber.pdf or invoiceDate_IA_invoiceNumber.pdf for adjustment invoice
        if (StringUtils.isBlank(pdfFileName)) {

            boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());

            pdfFileName = formatInvoiceDate(invoice.getInvoiceDate()) + (isInvoiceAdjustment ? paramBeanFactory.getInstance().getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") : "_")
                    + (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber());
        }

        if (pdfFileName != null && !pdfFileName.toLowerCase().endsWith(".pdf")) {
            pdfFileName = pdfFileName + ".pdf";
        }
        pdfFileName = StringUtils.normalizeFileName(pdfFileName);
        return pdfFileName;
    }

    /**
     * Produce invoice xml in new transaction.
     *
     * @param invoiceId invoice's id
     * @param draftWalletOperationsId
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceInvoiceXmlInNewTransaction(Long invoiceId, List<Long> draftWalletOperationsId) throws BusinessException {
        Invoice invoice = findById(invoiceId);
        invoice.setDraftRatedTransactions(getDraftRatedTransactions(draftWalletOperationsId));
        produceInvoiceXml(invoice);
    }

    /**
     * Produce invoice's XML file and update invoice record in DB.
     *
     * @param invoice Invoice to produce XML for
     * @return Update invoice entity
     * @throws BusinessException business exception
     */
    public Invoice produceInvoiceXml(Invoice invoice) throws BusinessException {

        produceInvoiceXmlNoUpdate(invoice);
        invoice.setStatus(InvoiceStatusEnum.GENERATED);
        invoice = updateNoCheck(invoice);
        entityUpdatedEventProducer.fire(invoice);
        return invoice;
    }

    /**
     * Produce invoice's XML file.
     *
     * @param invoice Invoice
     * @throws BusinessException business exception
     */
    public void produceInvoiceXmlNoUpdate(Invoice invoice) throws BusinessException {

        xmlInvoiceCreator.createXMLInvoice(invoice, false);
        xmlGeneratedEventProducer.fire(invoice);
    }

    /**
     * Delete invoice's XML file.
     *
     * @param invoice Invoice
     * @return True if file was deleted
     * @throws BusinessException business exception
     */
    public Invoice deleteInvoiceXml(Invoice invoice) throws BusinessException {

        String xmlFilename = getFullXmlFilePath(invoice, false);

        invoice.setXmlFilename(null);
        invoice = update(invoice);

        File file = new File(xmlFilename);
        if (file.exists()) {
            file.delete();
        }
        return invoice;
    }

    /**
     * Check if invoice's XML file exists.
     *
     * @param invoice Invoice
     * @return True if invoice's XML file exists
     */
    public boolean isInvoiceXmlExist(Invoice invoice) {

        String xmlFileName = getFullXmlFilePath(invoice, false);
        File xmlFile = new File(xmlFileName);
        return xmlFile.exists();
    }

    /**
     * Retrieve invoice's XML file contents as a string.
     *
     * @param invoice Invoice
     * @return Invoice's XML file contents as a string
     * @throws BusinessException business exception
     */
    public String getInvoiceXml(Invoice invoice) throws BusinessException {

        if (invoice.isPrepaid()) {
            throw new BusinessException("Invoice XML is disabled for prepaid invoice: " + invoice.getInvoiceNumber());
        }

        String xmlFileName = getFullXmlFilePath(invoice, false);
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists()) {
            throw new BusinessException("Invoice XML was not produced yet for invoice " + invoice.getInvoiceNumberOrTemporaryNumber());
        }

        try {
            return new String(Files.readAllBytes(Paths.get(xmlFileName)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error reading invoice XML file {} contents", xmlFileName, e);
        }

        return null;
    }

    /**
     * Check if invoice's PDF file exists.
     *
     * @param invoice Invoice
     * @return True if invoice's PDF file exists
     */
    public boolean isInvoicePdfExist(Invoice invoice) {

        String pdfFileName = getFullPdfFilePath(invoice, false);
        File pdfFile = new File(pdfFileName);
        return pdfFile.exists();
    }

    /**
     * Retrieve invoice's PDF file contents as a byte array.
     *
     * @param invoice Invoice
     * @return Invoice's PDF file contents as a byte array
     * @throws BusinessException business exception
     */
    public byte[] getInvoicePdf(Invoice invoice) throws BusinessException {

        String pdfFileName = getFullPdfFilePath(invoice, false);
        File pdfFile = new File(pdfFileName);
        if (!pdfFile.exists()) {
            throw new BusinessException("Invoice PDF was not produced yet for invoice " + invoice.getInvoiceNumberOrTemporaryNumber());
        }

        FileInputStream fileInputStream = null;
        try {
            long fileSize = pdfFile.length();
            if (fileSize > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("File is too big to put it to buffer in memory.");
            }
            byte[] fileBytes = new byte[(int) fileSize];
            fileInputStream = new FileInputStream(pdfFile);
            fileInputStream.read(fileBytes);
            return fileBytes;

        } catch (Exception e) {
            log.error("Error reading invoice PDF file {} contents", pdfFileName, e);

        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("Error closing file input stream", e);
                }
            }
        }

        return null;
    }

    /**
     * Generate XML (if neeed) and PDF files for Invoice.
     *
     * @param invoice Invoice
     * @param regenerate Regenerate XML and PDF files ignoring id they exist already
     * @return invoice
     *
     * @throws BusinessException business exception
     */
    public Invoice generateXmlAndPdfInvoice(Invoice invoice, boolean regenerate) throws BusinessException {

        if (invoice.isPrepaid()) {
            return invoice;
        }

        if (regenerate || invoice.getXmlFilename() == null || !isInvoiceXmlExist(invoice)) {
            produceInvoiceXmlNoUpdate(invoice);
        }
        invoice = produceInvoicePdf(invoice);
        return invoice;
    }

    /**
     * Gets the linked invoice.
     *
     * @param invoice invoice used to find
     * @return linked invoice
     */
    public Invoice getLinkedInvoice(Invoice invoice) {
        if (invoice == null || invoice.getLinkedInvoices() == null || invoice.getLinkedInvoices().isEmpty()) {
            return null;
        }
        return invoice.getLinkedInvoices().iterator().next();
    }

    /**
     * Gets the invoices with account operation.
     *
     * @param billingAccount billing account
     * @return list of invoice which doesn't have account operation.
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> getInvoicesWithAccountOperation(BillingAccount billingAccount) {
        try {
            QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
            qb.addSql("i.recordedInvoice is not null");
            if (billingAccount != null) {
                qb.addCriterionEntity("i.billingAccount", billingAccount);
            }
            return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to get invoices with no account operation", ex);
        }
        return null;
    }

    /**
     * Create pending Rated transactions and generate invoice for the billingAccount. DOES assign an invoice number AND create XML/PDF files or account operation if requested.
     *
     * @param entityToInvoice Entity to invoice
     * @param generateInvoiceRequestDto Generate invoice request
     * @param ratedTxFilter A filter to select rated transactions
     * @param isDraft Is it a draft invoice
     * @return A list of generated invoices
     * @throws BusinessException General business exception
     */
    public List<Invoice> generateInvoice(IBillableEntity entityToInvoice, GenerateInvoiceRequestDto generateInvoiceRequestDto, Filter ratedTxFilter, boolean isDraft, CustomFieldValues customFieldValues)
            throws BusinessException {

        boolean produceXml = (generateInvoiceRequestDto.getGenerateXML() != null && generateInvoiceRequestDto.getGenerateXML())
                || (generateInvoiceRequestDto.getGeneratePDF() != null && generateInvoiceRequestDto.getGeneratePDF());
        boolean producePdf = (generateInvoiceRequestDto.getGeneratePDF() != null && generateInvoiceRequestDto.getGeneratePDF());
        boolean generateAO = generateInvoiceRequestDto.getGenerateAO() != null && generateInvoiceRequestDto.getGenerateAO();

        Date invoiceDate = generateInvoiceRequestDto.getInvoicingDate();
        Date lastTransactionDate = generateInvoiceRequestDto.getLastTransactionDate();
        if (lastTransactionDate == null) {
            lastTransactionDate = invoiceDate;
        }

        // Create missing rated transactions up to a last transaction date
        ratedTransactionService.createRatedTransactions(entityToInvoice, lastTransactionDate);

        List<Invoice> invoices = invoiceService.createInvoice(entityToInvoice, generateInvoiceRequestDto, ratedTxFilter, isDraft);

        List<Invoice> invoicesWNumber = new ArrayList<Invoice>();
        for (Invoice invoice : invoices) {
            if (customFieldValues != null) {
                invoice.setCfValues(customFieldValues);
            }
            try {
                invoicesWNumber.add(serviceSingleton.assignInvoiceNumber(invoice));
            } catch (Exception e) {
                log.error("Failed to assign invoice number for invoice {}/{}", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber(), e);
                continue;
            }
            try {
                List<Long> drafWalletOperationIds;
                if (isDraft)
                    drafWalletOperationIds = getDrafWalletOperationIds(entityToInvoice, generateInvoiceRequestDto.getFirstTransactionDate(), generateInvoiceRequestDto.getLastTransactionDate());
                else
                    drafWalletOperationIds = new ArrayList<>();
                produceFilesAndAO(produceXml, producePdf, generateAO, invoice.getId(), isDraft, drafWalletOperationIds);
            } catch (Exception e) {
                log.error("Failed to generate XML/PDF files or recorded invoice AO for invoice {}/{}", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber(), e);
            }
        }
        return refreshOrRetrieve(invoices);
    }

    /**
     * Generate invoice for the billingAccount. Asumes tha all Rated transactions are created already. DOES NOT assign an invoice number NOR create XML/PDF files nor account
     * operation. Use generateInvoice() instead.
     *
     * @param entity Entity to invoice
     * @param generateInvoiceRequestDto Generate invoice request
     * @param ratedTxFilter A filter to select rated transactions
     * @param isDraft Is it a draft invoice
     * @return A list of invoices
     * @throws BusinessException General business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Invoice> createInvoice(IBillableEntity entity, GenerateInvoiceRequestDto generateInvoiceRequestDto, Filter ratedTxFilter, boolean isDraft) throws BusinessException {

        Date invoiceDate = generateInvoiceRequestDto.getInvoicingDate();
        Date firstTransactionDate = generateInvoiceRequestDto.getFirstTransactionDate();
        Date lastTransactionDate = generateInvoiceRequestDto.getLastTransactionDate();
        ApplyMinimumModeEnum applyMinimumModeEnum = ApplyMinimumModeEnum.NO_PARENT;
        if (generateInvoiceRequestDto.getApplyMinimum() != null) {
            applyMinimumModeEnum = ApplyMinimumModeEnum.valueOf(generateInvoiceRequestDto.getApplyMinimum());
        }

        if (StringUtils.isBlank(entity)) {
            throw new BusinessException("entity is null");
        }
        if (StringUtils.isBlank(invoiceDate)) {
            throw new BusinessException("invoicingDate is null");
        }

        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }

        if (lastTransactionDate == null) {
            lastTransactionDate = invoiceDate;
        }

        if (entity.getBillingRun() != null && (entity.getBillingRun().getStatus().equals(BillingRunStatusEnum.NEW) || entity.getBillingRun().getStatus().equals(BillingRunStatusEnum.PREVALIDATED)
                || entity.getBillingRun().getStatus().equals(BillingRunStatusEnum.POSTVALIDATED))) {

            throw new BusinessException("The entity is already in an billing run with status " + entity.getBillingRun().getStatus());
        }

//        // Create missing rated transactions up to a last transaction date
//        ratedTransactionService.createRatedTransaction(entity, lastTransactionDate);

        MinAmountForAccounts minAmountForAccounts = ratedTransactionService.isMinAmountForAccountsActivated(entity, applyMinimumModeEnum);

        List<Invoice> invoices = createAgregatesAndInvoice(entity, null, ratedTxFilter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft);

        return invoices;
    }

    /**
     * Produce XML and PDF files and AO.
     *
     * @param produceXml To produce xml invoice file
     * @param producePdf To produce pdf invoice file
     * @param generateAO To generate Account operations
     * @param invoiceId id of Invoice to operate on
     * @param isDraft Is it a draft invoice
     * @throws BusinessException General business exception
     * @throws InvoiceExistException Invoice already exist exception
     * @throws ImportInvoiceException Import invoice exception
     */
    public void produceFilesAndAO(boolean produceXml, boolean producePdf, boolean generateAO, Long invoiceId, boolean isDraft, List<Long> draftWalletOperationIds)
            throws BusinessException, InvoiceExistException, ImportInvoiceException {

        if (produceXml) {
            invoiceService.produceInvoiceXmlInNewTransaction(invoiceId, draftWalletOperationIds);
        }
        if (producePdf) {
            invoiceService.produceInvoicePdfInNewTransaction(invoiceId, draftWalletOperationIds);
        }
        if (generateAO && !isDraft) {
            invoiceService.generateRecordedInvoiceAO(invoiceId);
        }
    }

    /**
     * Generate Recorded invoice account operation
     *
     * @param invoiceId Invoice identifier
     * @throws InvoiceExistException Invoice already exists exception
     * @throws ImportInvoiceException Failed to import invoice exception
     * @throws BusinessException General business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void generateRecordedInvoiceAO(Long invoiceId) throws InvoiceExistException, ImportInvoiceException, BusinessException {

        Invoice invoice = findById(invoiceId);
        recordedInvoiceService.generateRecordedInvoice(invoice);
        invoice = update(invoice);
    }

    /**
     * Cancel invoice.
     *
     * @param invoice invoice to cancel
     * @throws BusinessException business exception
     */
    public void cancelInvoice(Invoice invoice) throws BusinessException {
        if (invoice.getRecordedInvoice() != null) {
            throw new BusinessException("Can't cancel an invoice that present in AR");
        }

        ratedTransactionService.deleteSupplementalRTs(invoice);
        ratedTransactionService.uninvoiceRTs(invoice);

        super.remove(invoice);

        log.debug("Invoice canceled {}", invoice.getTemporaryInvoiceNumber());
    }

    /**
     * Evaluate integer expression.
     *
     * @param expression expression as string
     * @param billingAccount billing account
     * @param invoice which is used to evaluate
     * @param order order related to invoice.
     * @return result of evaluation
     * @throws BusinessException business exception.
     */
    public static Integer evaluateDueDelayExpression(String expression, BillingAccount billingAccount, Invoice invoice, Order order) throws BusinessException {
        Integer result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE) >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("order") >= 0) {
            userMap.put("order", order);
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Integer.class);
        try {
            result = (Integer) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to Integer but " + res);
        }
        return result;
    }

    /**
     * Evaluate billing template name.
     *
     * @param expression the expression
     * @param invoice the invoice
     * @return the string
     */
    public String evaluateBillingTemplateName(String expression, Invoice invoice) {

        try {
            String value = ValueExpressionWrapper.evaluateExpression(expression, String.class, invoice);

            if (value != null) {
                return StringUtils.normalizeFileName(value);
            }
        } catch (BusinessException e) {
            // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
        }

        return null;
    }

    /**
     * Determine invoice type given the following criteria
     *
     * If is a prepaid invoice, default prepaid type is used.<br/>
     * If is a draft invoice, default draft type is used.<br/>
     * Otherwise invoice type is determined in the following order:<br/>
     * 1. billingCycle.invoiceTypeEl expression evaluated with billingRun and billingAccount a parameters, <br/>
     * 2. bilingCycle.invoiceType, <br/>
     * 3. Default commercial invoice type
     *
     * @param isPrepaid Is it for prepaid invoice. If True, default prepaid type is used. Excludes other criteria.
     * @param isDraft Is it a draft invoice. If true, default draft type is used. Excludes other criteria.
     * @param billingCycle Billing cycle
     * @param billingRun Billing run
     * @param billingAccount Billing account
     * @return Applicable invoice type
     * @throws BusinessException General business exception
     */
    private InvoiceType determineInvoiceType(boolean isPrepaid, boolean isDraft, BillingCycle billingCycle, BillingRun billingRun, BillingAccount billingAccount) throws BusinessException {
        InvoiceType invoiceType = null;

        if (isPrepaid) {
            invoiceType = invoiceTypeService.getDefaultPrepaid();

        } else if (isDraft) {
            invoiceType = invoiceTypeService.getDefaultDraft();

        } else {
            if (!StringUtils.isBlank(billingCycle.getInvoiceTypeEl())) {
                String invoiceTypeCode = evaluateInvoiceType(billingCycle.getInvoiceTypeEl(), billingRun, billingAccount);
                invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
            }
            if (invoiceType == null) {
                invoiceType = billingCycle.getInvoiceType();
            }
            if (invoiceType == null) {
                invoiceType = invoiceTypeService.getDefaultCommertial();
            }
        }

        return invoiceType;
    }

    public String evaluateInvoiceType(String expression, BillingRun billingRun, BillingAccount billingAccount) {
        String invoiceTypeCode = null;

        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("br", billingRun);
            contextMap.put("ba", billingAccount);

            try {
                String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);
                if (value != null) {
                    invoiceTypeCode = (String) value;
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
            }
        }

        return invoiceTypeCode;
    }

    /**
     * Determine an invoice template to use. Rule for selecting an invoiceTemplate is: InvoiceType &gt; BillingCycle &gt; default.
     *
     * @param invoice invoice
     * @param billingCycle Billing cycle
     * @param invoiceType Invoice type
     * @return Invoice template name
     */
    public String getInvoiceTemplateName(Invoice invoice, BillingCycle billingCycle, InvoiceType invoiceType) {

        String billingTemplateName = null;
        if (invoiceType != null && !StringUtils.isBlank(invoiceType.getBillingTemplateNameEL())) {
            billingTemplateName = evaluateBillingTemplateName(invoiceType.getBillingTemplateNameEL(), invoice);

        } else if (billingCycle != null && !StringUtils.isBlank(billingCycle.getBillingTemplateNameEL())) {
            billingTemplateName = evaluateBillingTemplateName(billingCycle.getBillingTemplateNameEL(), invoice);
        }
        if (billingTemplateName == null) {
            billingTemplateName = "default";
        }
        return billingTemplateName;
    }

    /**
     * Determine a date to use in calendar to calculate the next invoice date
     *
     * @param billingRun Billing run
     * @param billingAccount Billing account
     * @return Reference date
     */
    private Date getReferenceDateForNextInvoiceDateCalculation(BillingRun billingRun, BillingAccount billingAccount) {
        Date referenceDate = new Date();
        ReferenceDateEnum referenceDateEnum = null;

        if (billingRun != null) {
            referenceDateEnum = billingRun.getReferenceDate();
        }

        if (referenceDateEnum == null && billingRun.getBillingCycle() != null) {
            referenceDateEnum = billingRun.getBillingCycle().getReferenceDate();
        }

        if (referenceDateEnum != null) {
            switch (referenceDateEnum) {
            case TODAY:
                referenceDate = new Date();
                break;
            case NEXT_INVOICE_DATE:
                referenceDate = billingAccount != null ? billingAccount.getNextInvoiceDate() : null;
                break;
            case LAST_TRANSACTION_DATE:
                referenceDate = billingRun.getLastTransactionDate();
                break;
            case END_DATE:
                referenceDate = billingRun.getEndDate();
                break;
            default:
                break;
            }
        }
        return referenceDate;
    }

    /**
     * Assign invoice number .
     *
     * @param invoiceId invoice id
     * @param invoicesToNumberInfo instance of InvoicesToNumberInfo
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void assignInvoiceNumber(Long invoiceId, InvoicesToNumberInfo invoicesToNumberInfo) throws BusinessException {
        Invoice invoice = findById(invoiceId);
        assignInvoiceNumberFromReserve(invoice, invoicesToNumberInfo);

        BillingAccount billingAccount = invoice.getBillingAccount();

        billingAccount = incrementBAInvoiceDate(invoice.getBillingRun(), billingAccount);
        // /!\ DO NOT REMOVE THIS LINE, A LasyInitializationException is throw and the invoice is not generated.
        billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
        invoice = update(invoice);
    }

    /**
     * Re-computed invoice date, due date and collection date when the invoice is validated.
     *
     * @param invoice
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void recalculateDates(Long invoiceId) {
        Invoice invoice = invoiceService.findById(invoiceId);
        BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(invoice.getBillingAccount());
        BillingCycle billingCycle = billingAccount.getBillingCycle();
        BillingRun billingRun = billingRunService.refreshOrRetrieve(invoice.getBillingRun());
        if (billingRun != null) {
            billingCycle = billingRun.getBillingCycle();
        }
        billingCycle = PersistenceUtils.initializeAndUnproxy(billingCycle);
        if (billingRun == null) {
            return;
        }
        if (billingRun.getComputeDatesAtValidation() != null && !billingRun.getComputeDatesAtValidation()) {
            return;
        }
        if (billingRun.getComputeDatesAtValidation() == null && !billingCycle.getComputeDatesAtValidation()) {
            return;
        }
        if (billingRun.getComputeDatesAtValidation() != null && billingRun.getComputeDatesAtValidation()) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
            update(invoice);
        }
        if (billingRun.getComputeDatesAtValidation() == null && billingCycle.getComputeDatesAtValidation()) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
            update(invoice);
        }
    }

    private void recalculateDate(Invoice invoice, BillingRun billingRun, BillingAccount billingAccount, BillingCycle billingCycle) {

        int delay =
                billingCycle.getInvoiceDateDelayEL() == null ? 0 : InvoiceService.resolveImmediateInvoiceDateDelay(billingCycle.getInvoiceDateDelayEL(), invoice, billingAccount);
        Date invoiceDate = DateUtils.addDaysToDate(new Date(), delay);
        invoiceDate = DateUtils.setTimeToZero(invoiceDate);
        invoice.setInvoiceDate(invoiceDate);
        setInvoiceDueDate(invoice, billingCycle);
        setInitialCollectionDate(invoice, billingCycle, billingRun);

    }

    /**
     * Increment BA invoice date.
     *
     * @param billingRun
     * @param billingAccount Billing account
     * @throws BusinessException business exception
     */
    private BillingAccount incrementBAInvoiceDate(BillingRun billingRun, BillingAccount billingAccount) throws BusinessException {

        Date initCalendarDate = billingAccount.getSubscriptionDate() != null ? billingAccount.getSubscriptionDate() : billingAccount.getAuditable().getCreated();
        Calendar bcCalendar = CalendarService.initializeCalendar(billingAccount.getBillingCycle().getCalendar(), initCalendarDate, billingAccount, billingRun);

        Date nextInvoiceDate = bcCalendar.nextCalendarDate(getReferenceDateForNextInvoiceDateCalculation(billingRun, billingAccount));
        if (nextInvoiceDate != null) {
            billingAccount.setNextInvoiceDate(nextInvoiceDate);
            billingAccount = billingAccountService.update(billingAccount);
        }
        return billingAccount;
    }

    /**
     * Increment BA invoice date.
     *
     * @param billingRun Billing run
     * @param billingAccountId Billing account identifier
     *
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementBAInvoiceDateInNewTx(BillingRun billingRun, Long billingAccountId) throws BusinessException {

        BillingAccount billingAccount = billingAccountService.findById(billingAccountId);
        incrementBAInvoiceDate(billingRun, billingAccount);
    }

    /**
     * Increment BA invoice date by ID.
     *
     * @param billingRun
     * @param billingAccount
     *
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementBAInvoiceDate(BillingRun billingRun, Long billingAccountId) throws BusinessException {
        BillingAccount billingAccount = billingAccountService.findById(billingAccountId);
        incrementBAInvoiceDate(billingRun, billingAccount);
    }

    /**
     * Get a list of invoice identifiers that belong to a given Billing run and that do not have XML generated yet.
     *
     *
     * @param billingRunId Billing run id
     * @return A list of invoice identifiers
     */
    public List<Long> getInvoiceIdsByBRWithNoXml(Long billingRunId) {
        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.validatedNoXml", Long.class).getResultList();
        }
        return getEntityManager().createNamedQuery("Invoice.validatedByBRNoXml", Long.class).setParameter("billingRunId", billingRunId).getResultList();
    }

    /**
     * Get list of Draft invoice Ids that belong to the given Billing Run and not having XML generated yet.
     *
     *
     * @param billingRunId Billing run id
     * @return A list of invoice identifiers
     */
    public List<Long> getDraftInvoiceIdsByBRWithNoXml(Long billingRunId) {
        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.draftNoXml", Long.class).getResultList();
        }
        return getEntityManager().createNamedQuery("Invoice.draftByBRNoXml", Long.class).setParameter("billingRunId", billingRunId).getResultList();
    }

    /**
     * Get list of Draft and validated invoice Ids that belong to the given Billing Run and not having XML generated yet.
     *
     *
     * @param billingRunId Billing run id
     * @return A list of invoice identifiers
     */
    public List<Long> getInvoiceIdsIncludeDraftByBRWithNoXml(Long billingRunId) {
        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.allNoXml", Long.class).getResultList();
        }
        return getEntityManager().createNamedQuery("Invoice.allByBRNoXml", Long.class).setParameter("billingRunId", billingRunId).getResultList();
    }

    /**
     * Get a summarized information for invoice numbering. Contains grouping by invoice type, seller, invoice date and a number of invoices.
     *
     * @param billingRunId Billing run id
     * @return A list of invoice identifiers
     */
    @SuppressWarnings("unchecked")
    public List<InvoicesToNumberInfo> getInvoicesToNumberSummary(Long billingRunId) {

        List<InvoicesToNumberInfo> invoiceSummaries = new ArrayList<>();
        List<Object[]> summary = getEntityManager().createNamedQuery("Invoice.invoicesToNumberSummary").setParameter("billingRunId", billingRunId).getResultList();

        for (Object[] summaryInfo : summary) {
            invoiceSummaries.add(new InvoicesToNumberInfo((Long) summaryInfo[0], (Long) summaryInfo[1], (Date) summaryInfo[2], (Long) summaryInfo[3]));
        }

        return invoiceSummaries;
    }

    /**
     * Retrieve invoice ids matching billing run, invoice type, seller and invoice date combination.
     *
     * @param billingRunId Billing run id
     * @param invoiceTypeId Invoice type id
     * @param sellerId Seller id
     * @param invoiceDate Invoice date
     * @return A list of invoice identifiers
     */
    public List<Long> getInvoiceIds(Long billingRunId, Long invoiceTypeId, Long sellerId, Date invoiceDate) {
        return getEntityManager().createNamedQuery("Invoice.byBrItSelDate", Long.class).setParameter("billingRunId", billingRunId).setParameter("invoiceTypeId", invoiceTypeId).setParameter("sellerId", sellerId)
            .setParameter("invoiceDate", invoiceDate).getResultList();
    }

    /**
     * Retrieve billingAccount ids matching billing run, invoice type, seller and invoice date combination.
     *
     * @param billingRunId Billing run id
     * @param invoiceTypeId Invoice type id
     * @param sellerId Seller id
     * @param invoiceDate Invoice date
     * @return A list of billingAccount identifiers
     */
    public List<Long> getBillingAccountIds(Long billingRunId, Long invoiceTypeId, Long sellerId, Date invoiceDate) {
        return getEntityManager().createNamedQuery("Invoice.billingAccountIdByBrItSelDate", Long.class).setParameter("billingRunId", billingRunId).setParameter("invoiceTypeId", invoiceTypeId)
            .setParameter("sellerId", sellerId).setParameter("invoiceDate", invoiceDate).getResultList();
    }

    /**
     * List by invoice.
     *
     * @param invoice invoice used to get subcategory
     * @return list of SubCategoryInvoiceAgregate
     */
    @SuppressWarnings("unchecked")
    public List<SubCategoryInvoiceAgregate> listByInvoice(Invoice invoice) {
        QueryBuilder qb = new QueryBuilder(SubCategoryInvoiceAgregate.class, "c");
        qb.addCriterionEntity("invoice", invoice);
        qb.addBooleanCriterion("discountAggregate", false);

        try {
            List<SubCategoryInvoiceAgregate> resultList = (List<SubCategoryInvoiceAgregate>) qb.getQuery(getEntityManager()).getResultList();
            return resultList;
        } catch (NoResultException e) {
            log.warn("error while getting user account list by billing account", e);
            return null;
        }
    }

    public List<String> listPdfInvoice(Customer cust) {
        List<String> result = new ArrayList<>();
        if (cust.getCustomerAccounts() != null && !cust.getCustomerAccounts().isEmpty()) {
            for (CustomerAccount ca : cust.getCustomerAccounts()) {
                if (ca.getBillingAccounts() != null && !ca.getBillingAccounts().isEmpty()) {
                    for (BillingAccount ba : ca.getBillingAccounts()) {
                        if (ba.getInvoices() != null && !ba.getInvoices().isEmpty()) {
                            for (Invoice inv : ba.getInvoices()) {
                                result.add(getFullPdfFilePath(inv, false));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Update unpaid invoices status
     */
    public void updateUnpaidInvoicesStatus() {
        getEntityManager().createNamedQuery("Invoice.updateUnpaidInvoicesStatus").executeUpdate();
    }

    /**
     * Return all invoices with invoiceDate date more than n years old
     *
     * @param nYear age of the invoices
     * @return Filtered list of invoices
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> listInactiveInvoice(int nYear) {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("invoiceDate", higherBound, true, false);

        return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Nullify BR's invoices file names (xml and pdf).
     *
     * @param billingRun the billing run
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void nullifyInvoiceFileNames(BillingRun billingRun) {
        getEntityManager().createNamedQuery("Invoice.nullifyInvoiceFileNames").setParameter("billingRun", billingRun).executeUpdate();
    }

    /**
     * A first part of invoiceService.create() method. Does not call PersistenceService.create(), Need to call InvoiceService.postCreate() separately
     *
     * @param invoice Invoice entity
     * @throws BusinessException General business exception
     */
    @Override
    public void create(Invoice invoice) throws BusinessException {

        invoice.updateAudit(currentUser);

        // Schedule end of period events
        // Be careful - if called after persistence might loose ability to determine new period as CustomFeldvalue.isNewPeriod is not serialized to json
        if (invoice instanceof ICustomFieldEntity) {
            customFieldInstanceService.scheduleEndPeriodEvents((ICustomFieldEntity) invoice);
        }
        // activate/deactivate sending invoice by Emails
        if (!isElectronicBillingEnabled(invoice)) {
            invoice.setDontSend(true);
        }

        getEntityManager().persist(invoice);

        log.trace("end of create {}. entity id={}.", invoice.getClass().getSimpleName(), invoice.getId());
    }

    /**
     * A second part of invoiceService.create() method.
     *
     * @param invoice Invoice entity
     * @throws BusinessException General business exception
     */
    public void postCreate(Invoice invoice) throws BusinessException {

        entityCreatedEventProducer.fire((BaseEntity) invoice);

        cfValueAccumulator.entityCreated(invoice);

        log.trace("end of post create {}. entity id={}.", invoice.getClass().getSimpleName(), invoice.getId());
    }

    /**
     * Send the invoice by email
     *
     * @param invoice the invoice
     * @param mailingTypeEnum : Mailing type
     * @param overrideEmail : override Email
     * @return true if the invoice is sent, false else.
     * @throws BusinessException
     */
    public boolean sendByEmail(Invoice invoice, MailingTypeEnum mailingTypeEnum, String overrideEmail) throws BusinessException {
        try {
            if (invoice == null) {
                log.error("The invoice to be sent by Email is null!!");
                return false;
            }
            invoice = refreshOrRetrieve(invoice);
            if (invoice.getPdfFilename() == null) {
                log.warn("The Pdf for the invoice is not generated!!");
                return false;
            }
            List<String> to = new ArrayList<>();
            List<String> cc = new ArrayList<>();
            List<File> files = new ArrayList<>();

            String fileName = getFullPdfFilePath(invoice, false);
            File attachment = new File(fileName);
            if (!attachment.exists()) {
                log.warn("No Pdf file exists for the invoice " + invoice.getInvoiceNumber());
                return false;
            }
            files.add(attachment);
            EmailTemplate emailTemplate = invoice.getInvoiceType().getEmailTemplate();
            MailingTypeEnum mailingType = invoice.getInvoiceType().getMailingType();
            BillingAccount billingAccount = invoice.getBillingAccount();
            Seller seller = invoice.getSeller();
            if (billingAccount.getContactInformation() != null) {
                to.add(billingAccount.getContactInformation().getEmail());
            }
            if (billingAccount.getCcedEmails() != null) {
                cc.addAll(Arrays.asList(billingAccount.getCcedEmails().split(",")));
            }
            if (billingAccount.getEmailTemplate() != null) {
                emailTemplate = billingAccount.getEmailTemplate();
            }
            if (billingAccount.getMailingType() != null) {
                mailingType = billingAccount.getMailingType();
            }

            Boolean electronicBilling = billingAccount.getElectronicBilling();
            Subscription subscription = invoice.getSubscription();
            if (subscription != null) {
                electronicBilling = subscription.getElectronicBilling();
                seller = (subscription.getSeller() != null) ? subscription.getSeller() : seller;
                to.clear();
                to.add(subscription.getEmail());
                cc.clear();
                if (subscription.getCcedEmails() != null) {
                    cc.addAll(Arrays.asList(subscription.getCcedEmails().split(",")));
                }
                emailTemplate = (subscription.getEmailTemplate() != null) ? subscription.getEmailTemplate() : emailTemplate;
                mailingType = (subscription.getMailingType() != null) ? subscription.getMailingType() : mailingType;

            }
            Order order = invoice.getOrder();
            if (order != null) {
                electronicBilling = order.getElectronicBilling();
                to.clear();
                to.add(order.getEmail());
                cc.clear();
                if (order.getCcedEmails() != null) {
                    cc.addAll(Arrays.asList(order.getCcedEmails().split(",")));
                }
                emailTemplate = (order.getEmailTemplate() != null) ? order.getEmailTemplate() : emailTemplate;
                mailingType = (order.getMailingType() != null) ? order.getMailingType() : mailingType;
            }
            if (overrideEmail != null) {
                to.clear();
                to.add(overrideEmail);
                cc.clear();
            }
            if (to.isEmpty() || emailTemplate == null) {
                log.warn("No Email or  EmailTemplate is configured to receive the invoice!!");
                return false;
            }
            if (seller == null || seller.getContactInformation() == null) {
                log.warn("The Seller or it's contact information is null!!");
                return false;
            }
            if (electronicBilling && mailingTypeEnum.equals(mailingType)) {
                Map<Object, Object> params = new HashMap<>();
                params.put("invoice", invoice);
                String subject = ValueExpressionWrapper.evaluateExpression(emailTemplate.getSubject(), params, String.class);
                String content = ValueExpressionWrapper.evaluateExpression(emailTemplate.getTextContent(), params, String.class);
                String contentHtml = ValueExpressionWrapper.evaluateExpression(emailTemplate.getHtmlContent(), params, String.class);
                String from = seller.getContactInformation().getEmail();
                emailSender.send(from, Arrays.asList(from), to, cc, null, subject, content, contentHtml, files, null, false);
                invoice.setStatus(InvoiceStatusEnum.SENT);
                entityUpdatedEventProducer.fire(invoice);
                invoice.setAlreadySent(true);
                update(invoice);

                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
    }

    /**
     * Return a list of invoices that not already sent and can be sent : dontsend:false.
     *
     * @return a list of invoices
     * @throws BusinessException
     * @param billingCycleCodes
     * @param invoiceDateRangeFrom
     * @param invoiceDateRangeTo
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findByNotAlreadySentAndDontSend(List<String> billingCycleCodes, Date invoiceDateRangeFrom, Date invoiceDateRangeTo) throws BusinessException {
        List<Invoice> result = new ArrayList<Invoice>();
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
        qb.addCriterionEntity("alreadySent", false);
        qb.addCriterionEntity("dontSend", false);
        if(billingCycleCodes != null)
            qb.addCriterionEntityInList("billingRun.code", billingCycleCodes);
        if(invoiceDateRangeFrom != null)
            qb.addCriterionDateRangeFromTruncatedToDay("invoiceDate", invoiceDateRangeFrom);
        if(invoiceDateRangeTo != null)
            qb.addCriterionDateRangeToTruncatedToDay("invoiceDate", invoiceDateRangeTo, false, false);
        try {
            result = (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Check if an invoice is draft.
     *
     * @param invoice the invoice
     * @return true if is draft else return false.
     * @throws BusinessException
     */
    public boolean isDraft(Invoice invoice) throws BusinessException {
        invoice = refreshOrRetrieve(invoice);
        InvoiceType invoiceType = invoice.getInvoiceType();
        InvoiceType draftInvoiceType = invoiceTypeService.getDefaultDraft();
        return invoiceType != null && (invoiceType.equals(draftInvoiceType) || invoice.getInvoiceNumber() == null);
    }

    /**
     * Evaluate the override Email EL
     *
     * @param overrideEmailEl override Email
     * @param userMap the userMap
     * @param invoice the invoice
     * @return the
     * @throws BusinessException
     */
    public String evaluateOverrideEmail(String overrideEmailEl, HashMap<Object, Object> userMap, Invoice invoice) throws BusinessException {
        invoice = refreshOrRetrieve(invoice);
        userMap.put("invoice", invoice);
        String result = ValueExpressionWrapper.evaluateExpression(overrideEmailEl, userMap, String.class);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        return result;
    }

    /**
     * Append invoice aggregates to an invoice. Retrieves all to-invoice Rated transactions for a given billing account
     *
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate Last transaction date
     * @throws BusinessException business exception
     */
    public void appendInvoiceAgregates(BillingAccount billingAccount, Invoice invoice, Date firstTransactionDate, Date lastTransactionDate) throws BusinessException {

        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }

        if (lastTransactionDate == null) {
            lastTransactionDate = new Date();
        }

        List<RatedTransaction> ratedTransactions = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccount", RatedTransaction.class).setParameter("billingAccount", billingAccount)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();

        appendInvoiceAgregates(billingAccount, billingAccount, invoice, ratedTransactions, false, null, false);
    }

    /**
     * Creates Invoice aggregates from given Rated transactions and appends them to an invoice
     *
     * @param entityToInvoice Entity to invoice
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param ratedTransactions A list of rated transactions
     * @param isInvoiceAdjustment Is this invoice adjustment
     * @param invoiceAggregateProcessingInfo RT to invoice aggregation information when invoice is created with paged RT retrieval. NOTE: should pass NULL in non-paginated
     *        invoicing cases
     * @param subCategoryAggregates Subcategory aggregates for invoice mapped by a key
     * @param moreRatedTransactionsExpected Indicates that there are more RTs to be retrieved and aggregated in invoice before invoice can be closed. NOTE: should pass FALSE in
     *        non-paginated invoicing cases
     * @throws BusinessException BusinessException
     */
    protected void appendInvoiceAgregates(IBillableEntity entityToInvoice, BillingAccount billingAccount, Invoice invoice, List<RatedTransaction> ratedTransactions, boolean isInvoiceAdjustment,
            InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo, boolean moreRatedTransactionsExpected) throws BusinessException {

        boolean isAggregateByUA = paramBeanFactory.getInstance().getPropertyAsBoolean("invoice.agregateByUA", true);

        boolean isEnterprise = appProvider.isEntreprise();
        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();
        Boolean isExonerated = billingAccount.isExoneratedFromtaxes();
        if (isExonerated == null) {
            isExonerated = billingAccountService.isExonerated(billingAccount);
        }
        int rtRounding = appProvider.getRounding();
        RoundingModeEnum rtRoundingMode = appProvider.getRoundingMode();
        Tax taxZero = isExonerated ? taxService.getZeroTax() : null;

        // InvoiceType.taxScript will calculate all tax aggregates at once.
        boolean calculateTaxOnSubCategoryLevel = invoice.getInvoiceType().getTaxScript() == null;

        // Should tax calculation on subcategory level be done externally
        boolean calculateExternalTax = "YES".equalsIgnoreCase((String) appProvider.getCfValue("OPENCELL_ENABLE_TAX_CALCULATION"));

        // Tax change mapping. Key is ba.id_taxClass.id and value is an array of [Tax to apply, True/false if tax has changed]
        Map<String, Object[]> taxChangeMap = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.taxChangeMap : new HashMap<>();

        // Subcategory aggregates mapping. Key is ua.id_walletInstance.id_invoiceSubCategory.id_tax.id
        Map<String, SubCategoryInvoiceAgregate> subCategoryAggregates = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.subCategoryAggregates : new LinkedHashMap<>();

        Set<String> orderNumbers = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.orderNumbers : new HashSet<String>();

        String scaKey = null;

        if (log.isTraceEnabled()) {
            log.trace("ratedTransactions.totalAmountWithoutTax={}", ratedTransactions != null ? ratedTransactions.stream().mapToDouble(e -> e.getAmountWithoutTax().doubleValue()).sum() : "0");
        }

        boolean taxWasRecalculated = false;
        for (RatedTransaction ratedTransaction : ratedTransactions) {

            InvoiceSubCategory invoiceSubCategory = ratedTransaction.getInvoiceSubCategory();

            scaKey = invoiceSubCategory.getId().toString();
            if (isAggregateByUA) {
                scaKey = (ratedTransaction.getUserAccount() != null ? ratedTransaction.getUserAccount().getId() : "") + "_" + (ratedTransaction.getWallet() != null ? ratedTransaction.getWallet().getId() : "") + "_"
                        + scaKey;
            }

            Tax tax = ratedTransaction.getTax();

            // Check if tax has to be recalculated. Does not apply to RatedTransactions that had tax explicitly set/overridden
            if (calculateTaxOnSubCategoryLevel && !ratedTransaction.isTaxOverriden()) {

                TaxClass taxClass = ratedTransaction.getTaxClass();
                String taxChangeKey = billingAccount.getId() + "_" + taxClass.getId();

                Object[] changedToTax = taxChangeMap.get(taxChangeKey);
                if (changedToTax == null) {
                    taxZero = isExonerated && taxZero == null ? taxService.getZeroTax() : taxZero;
                    Object[] applicableTax = getApplicableTax(tax, isExonerated, invoice, taxClass, ratedTransaction.getUserAccount(), taxZero, calculateExternalTax);
                    changedToTax = applicableTax;
                    taxChangeMap.put(taxChangeKey, changedToTax);
                    if ((boolean) changedToTax[1]) {
                        log.debug("Will update rated transactions of Billing account {} and tax class {} with new tax from {}/{}% to {}/{}%", billingAccount.getId(), taxClass.getId(), tax.getId(), tax.getPercent(),
                            ((Tax) changedToTax[0]).getId(), ((Tax) changedToTax[0]).getPercent());
                    }
                }
                taxWasRecalculated = (boolean) changedToTax[1];
                if (taxWasRecalculated) {
                    tax = (Tax) changedToTax[0];
                    ratedTransaction.setTaxRecalculated(true);
                }
            }

            SubCategoryInvoiceAgregate scAggregate = subCategoryAggregates.get(scaKey);
            if (scAggregate == null) {
                scAggregate = new SubCategoryInvoiceAgregate(invoiceSubCategory, billingAccount, isAggregateByUA ? ratedTransaction.getUserAccount() : null, isAggregateByUA ? ratedTransaction.getWallet() : null, invoice,
                    invoiceSubCategory.getAccountingCode());
                scAggregate.updateAudit(currentUser);

                String translationSCKey = "SC_" + invoiceSubCategory.getId() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationSCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory.getDescriptionOrCode();
                    if ((invoiceSubCategory.getDescriptionI18n() != null) && (invoiceSubCategory.getDescriptionI18n().get(languageCode) != null)) {
                        descTranslated = invoiceSubCategory.getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationSCKey, descTranslated);
                }
                scAggregate.setDescription(descTranslated);

                subCategoryAggregates.put(scaKey, scAggregate);
                invoice.addInvoiceAggregate(scAggregate);
            }

            if (!(entityToInvoice instanceof Order) && ratedTransaction.getOrderNumber() != null) {
                orderNumbers.add(ratedTransaction.getOrderNumber());
            }

            if (taxWasRecalculated) {
                ratedTransaction.setTax(tax);
                ratedTransaction.setTaxPercent(tax.getPercent());
                ratedTransaction.computeDerivedAmounts(isEnterprise, rtRounding, rtRoundingMode);
            }

            scAggregate.addRatedTransaction(ratedTransaction, isEnterprise, true);
        }

        // Postpone other aggregate calculation until the last RT is aggregated to invoice
        if (moreRatedTransactionsExpected) {
            return;
        }

        addDiscountCategoryAndTaxAggregates(invoice, subCategoryAggregates.values());
    }

    private void addDiscountCategoryAndTaxAggregates(Invoice invoice, Collection<SubCategoryInvoiceAgregate> subCategoryAggregates) throws BusinessException {

        Subscription subscription = invoice.getSubscription();
        BillingAccount billingAccount = invoice.getBillingAccount();
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();

        boolean isEnterprise = appProvider.isEntreprise();
        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();
        int rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        // InvoiceType.taxScript will calculate all tax aggregates at once.

        Boolean isExonerated = billingAccount.isExoneratedFromtaxes();
        if (isExonerated == null) {
            isExonerated = billingAccountService.isExonerated(billingAccount);
        }
        boolean calculateTaxOnSubCategoryLevel = invoice.getInvoiceType().getTaxScript() == null;

        // Determine which discount plan items apply to this invoice
        List<DiscountPlanItem> subscriptionApplicableDiscountPlanItems = new ArrayList<>();
        List<DiscountPlanItem> billingAccountApplicableDiscountPlanItems = new ArrayList<>();
        if (subscription == null && billingAccount != null) {
            List<DiscountPlanInstance> discountPlanInstances = fromBillingAccount(billingAccount);
            List<DiscountPlanItem> result = getApplicableDiscountPlanItems(billingAccount, discountPlanInstances, invoice, customerAccount);
            ofNullable(result).ifPresent(discountPlans -> subscriptionApplicableDiscountPlanItems.addAll(discountPlans));
        }

        if (subscription != null && subscription.getDiscountPlanInstances() != null && !subscription.getDiscountPlanInstances().isEmpty()) {
            subscriptionApplicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, subscription.getDiscountPlanInstances(), invoice, customerAccount));
        }
        if (billingAccount.getDiscountPlanInstances() != null && !billingAccount.getDiscountPlanInstances().isEmpty()) {
            billingAccountApplicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, billingAccount.getDiscountPlanInstances(), invoice, customerAccount));
        }

        // Calculate derived aggregate amounts for subcategory aggregate, create category aggregates, discount aggregates and tax aggregates
        BigDecimal[] amounts = null;
        Map<String, CategoryInvoiceAgregate> categoryAggregates = new HashMap<>();
        List<SubCategoryInvoiceAgregate> discountAggregates = new ArrayList<>();
        Map<String, TaxInvoiceAgregate> taxAggregates = new HashMap<>();

        // Create category aggregates
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates) {

            // Calculate derived amounts
            scAggregate.computeDerivedAmounts(isEnterprise, rounding, roundingMode.getRoundingMode(), invoiceRounding, invoiceRoundingMode.getRoundingMode());

            InvoiceSubCategory invoiceSubCategory = scAggregate.getInvoiceSubCategory();

            // Create category aggregates or update their amounts
            String caKey = (scAggregate.getUserAccount() != null ? scAggregate.getUserAccount().getId() : "") + "_" + invoiceSubCategory.getInvoiceCategory().getId().toString();

            CategoryInvoiceAgregate cAggregate = categoryAggregates.get(caKey);
            if (cAggregate == null) {
                cAggregate = new CategoryInvoiceAgregate(invoiceSubCategory.getInvoiceCategory(), billingAccount, scAggregate.getUserAccount(), invoice);
                categoryAggregates.put(caKey, cAggregate);

                cAggregate.updateAudit(currentUser);

                String translationCKey = "C_" + invoiceSubCategory.getInvoiceCategory().getId() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionOrCode();
                    if ((invoiceSubCategory.getInvoiceCategory().getDescriptionI18n() != null) && (invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode) != null)) {
                        descTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationCKey, descTranslated);
                }

                cAggregate.setDescription(descTranslated);

                invoice.addInvoiceAggregate(cAggregate);
            }

            cAggregate.addSubCategoryInvoiceAggregate(scAggregate);

            invoice.addAmountWithoutTax(scAggregate.getAmountWithoutTax());
            invoice.addAmountWithTax(scAggregate.getAmountWithTax());
            invoice.addAmountTax(isExonerated ? BigDecimal.ZERO : scAggregate.getAmountTax());
        }

        // Construct discount and tax aggregates
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates) {

            if (BigDecimal.ZERO.equals(isEnterprise ? scAggregate.getAmountWithoutTax() : scAggregate.getAmountWithTax())) {
                continue;
            }

            Map<Tax, SubcategoryInvoiceAgregateAmount> amountCumulativeForTax = new LinkedHashMap<Tax, SubcategoryInvoiceAgregateAmount>();
            scAggregate.getAmountsByTax().entrySet().stream().forEach(amountInfo -> amountCumulativeForTax.put(amountInfo.getKey(), amountInfo.getValue().clone()));

            CategoryInvoiceAgregate cAggregate = scAggregate.getCategoryInvoiceAgregate();

            Map<Tax, BigDecimal> amountAsDiscountBase = new LinkedHashMap<Tax, BigDecimal>();
            scAggregate.getAmountsByTax().entrySet().stream().forEach(amountInfo -> amountAsDiscountBase.put(amountInfo.getKey(), amountInfo.getValue().getAmount(!isEnterprise)));

            // Add discount aggregates defined on subscription level - ONLY when invoicing by subscription
            for (DiscountPlanItem discountPlanItem : subscriptionApplicableDiscountPlanItems) {
                SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, rounding, roundingMode, invoiceRounding, invoiceRoundingMode, scAggregate, amountAsDiscountBase,
                    cAggregate, discountPlanItem);
                if (discountAggregate != null) {
                    addAmountsToMap(amountCumulativeForTax, discountAggregate.getAmountsByTax());
                }
                discountAggregates.add(discountAggregate);
            }

            for (DiscountPlanItem discountPlanItem : billingAccountApplicableDiscountPlanItems) {
                SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, rounding, roundingMode, invoiceRounding, invoiceRoundingMode, scAggregate, amountAsDiscountBase,
                    cAggregate, discountPlanItem);
                if (discountAggregate != null) {
                    addAmountsToMap(amountCumulativeForTax, discountAggregate.getAmountsByTax());
                }
                discountAggregates.add(discountAggregate);
            }

            // Add tax aggregate or update its amounts

            if (calculateTaxOnSubCategoryLevel && !isExonerated && !amountCumulativeForTax.isEmpty()) {

                for (Map.Entry<Tax, SubcategoryInvoiceAgregateAmount> amountByTax : amountCumulativeForTax.entrySet()) {
                    Tax tax = amountByTax.getKey();
                    if (BigDecimal.ZERO.compareTo(amountByTax.getValue().getAmount(!isEnterprise)) == 0) {
                        continue;
                    }

                    TaxInvoiceAgregate taxAggregate = taxAggregates.get(tax.getCode());
                    if (taxAggregate == null) {
                        taxAggregate = new TaxInvoiceAgregate(billingAccount, tax, tax.getPercent(), invoice);
                        taxAggregate.updateAudit(currentUser);
                        taxAggregates.put(tax.getCode(), taxAggregate);

                        String translationCKey = "T_" + tax.getId() + "_" + languageCode;
                        String descTranslated = descriptionMap.get(translationCKey);
                        if (descTranslated == null) {
                            descTranslated = tax.getDescriptionOrCode();
                            if ((tax.getDescriptionI18n() != null) && (tax.getDescriptionI18n().get(languageCode) != null)) {
                                descTranslated = tax.getDescriptionI18n().get(languageCode);
                            }
                            descriptionMap.put(translationCKey, descTranslated);
                        }

                        taxAggregate.setDescription(descTranslated);

                        invoice.addInvoiceAggregate(taxAggregate);
                    }

                    if (isEnterprise) {
                        taxAggregate.addAmountWithoutTax(amountByTax.getValue().getAmountWithoutTax());

                    } else {
                        taxAggregate.addAmountWithTax(amountByTax.getValue().getAmountWithTax());
                    }
                }
            }

        }

        // Calculate derived tax aggregate amounts
        if (calculateTaxOnSubCategoryLevel && !isExonerated) {
            for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {

                amounts = NumberUtils.computeDerivedAmounts(taxAggregate.getAmountWithoutTax(), taxAggregate.getAmountWithTax(), taxAggregate.getTaxPercent(), isEnterprise, invoiceRounding,
                    invoiceRoundingMode.getRoundingMode());
                taxAggregate.setAmountWithoutTax(amounts[0]);
                taxAggregate.setAmountWithTax(amounts[1]);
                taxAggregate.setAmountTax(amounts[2]);

            }
        }

        // If tax calculation is not done at subcategory level, then call a global script to do calculation for the whole invoice
        if (!isExonerated && !calculateTaxOnSubCategoryLevel) {
            if ((invoice.getInvoiceType() != null) && (invoice.getInvoiceType().getTaxScript() != null)) {
                taxAggregates = taxScriptService.createTaxAggregates(invoice.getInvoiceType().getTaxScript().getCode(), invoice);
                if (taxAggregates != null) {
                    for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {
                        taxAggregate.setInvoice(invoice);
                        invoice.addInvoiceAggregate(taxAggregate);
                    }
                }
            }
        }

        // Calculate invoice total amounts by the sum of tax aggregates or category aggregates minus discount aggregates
        // Left here in case tax script modifies something
        if (!isExonerated && (taxAggregates != null) && !taxAggregates.isEmpty()) {
            invoice.setAmountWithoutTax(BigDecimal.ZERO);
            invoice.setAmountWithTax(BigDecimal.ZERO);
            invoice.setAmountTax(BigDecimal.ZERO);
            for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {
                invoice.addAmountWithoutTax(taxAggregate.getAmountWithoutTax());
                invoice.addAmountWithTax(taxAggregate.getAmountWithTax());
                invoice.addAmountTax(taxAggregate.getAmountTax());
            }
        } else {
            for (SubCategoryInvoiceAgregate discountAggregate : discountAggregates) {
                invoice.addAmountWithoutTax(discountAggregate.getAmountWithoutTax());
                invoice.addAmountWithTax(discountAggregate.getAmountWithTax());
                invoice.addAmountTax(isExonerated ? BigDecimal.ZERO : discountAggregate.getAmountTax());
            }
        }

        // If invoice is prepaid, skip threshold test
        /*
         * if (!invoice.isPrepaid()) { BigDecimal invoicingThreshold = billingAccount.getInvoicingThreshold() == null ? billingAccount.getBillingCycle().getInvoicingThreshold() :
         * billingAccount.getInvoicingThreshold(); if ((invoicingThreshold != null) && (invoicingThreshold.compareTo(isEnterprise ? invoice.getAmountWithoutTax() :
         * invoice.getAmountWithTax()) > 0)) { throw new BusinessException("Invoice amount below the threshold"); } }
         */

        // Update net to pay amount
        final BigDecimal amountWithTax = invoice.getAmountWithTax()!=null ? invoice.getAmountWithTax() : BigDecimal.ZERO;
		invoice.setNetToPay(amountWithTax.add(invoice.getDueBalance() != null ? invoice.getDueBalance() : BigDecimal.ZERO));
    }

    private List<DiscountPlanInstance> fromBillingAccount(BillingAccount billingAccount) {
        return billingAccount.getUsersAccounts().stream()
                .map(userAccount -> userAccount.getSubscriptions())
                .map(this::addSubscriptionDiscountPlan)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<DiscountPlanInstance> addSubscriptionDiscountPlan(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(Subscription::getDiscountPlanInstances)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private SubCategoryInvoiceAgregate getDiscountAggregates(BillingAccount billingAccount, Invoice invoice, boolean isEnterprise, int rounding, RoundingModeEnum roundingMode, int invoiceRounding,
            RoundingModeEnum invoiceRoundingMode, SubCategoryInvoiceAgregate scAggregate, Map<Tax, BigDecimal> amountsByTax, CategoryInvoiceAgregate cAggregate, DiscountPlanItem discountPlanItem)
            throws BusinessException {

        BigDecimal amountToApplyDiscountOn = sumMapValues(amountsByTax);

        if (BigDecimal.ZERO.compareTo(amountToApplyDiscountOn) == 0) {
            return null;
        }

        // Apply discount if matches the category, subcategory, or applies to any category
        if (!((discountPlanItem.getInvoiceCategory() == null && discountPlanItem.getInvoiceSubCategory() == null)
                || (discountPlanItem.getInvoiceSubCategory() != null && discountPlanItem.getInvoiceSubCategory().getId().equals(scAggregate.getInvoiceSubCategory().getId()))
                || (discountPlanItem.getInvoiceCategory() != null && discountPlanItem.getInvoiceSubCategory() == null
                        && discountPlanItem.getInvoiceCategory().getId().equals(scAggregate.getInvoiceSubCategory().getInvoiceCategory().getId())))) {
            return null;
        }

        BigDecimal discountValue = getDiscountAmountOrPercent(invoice, scAggregate, amountToApplyDiscountOn, discountPlanItem);

        if (BigDecimal.ZERO.compareTo(discountValue) == 0) {
            return null;
        }

        Map<Tax, BigDecimal> discountAmountsByTax = new HashMap<>();

        BigDecimal discountAmount = null;

        // Percent based discount
        if (discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.PERCENTAGE) {

            for (Entry<Tax, BigDecimal> amountInfo : amountsByTax.entrySet()) {
                discountAmountsByTax.put(amountInfo.getKey(), amountInfo.getValue().abs().multiply(discountValue.negate().divide(HUNDRED)).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
            }
            discountAmount = sumMapValues(discountAmountsByTax);

            // Amount based discount
        } else {

            discountAmount = discountValue.negate().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());

            // If the discount and the aggregate are of opposite signs, then the absolute value of the discount must not be greater than the absolute value of the
            // considered invoice aggregate
            if (!((discountAmount.compareTo(BigDecimal.ZERO) < 0 && amountToApplyDiscountOn.compareTo(BigDecimal.ZERO) < 0)
                    || (discountAmount.compareTo(BigDecimal.ZERO) > 0 && amountToApplyDiscountOn.compareTo(BigDecimal.ZERO) > 0)) && (discountAmount.abs().compareTo(amountToApplyDiscountOn.abs()) > 0)) {

                for (Entry<Tax, BigDecimal> amountInfo : amountsByTax.entrySet()) {
                    discountAmountsByTax.put(amountInfo.getKey(), amountInfo.getValue().negate());
                }
            } else {
                discountAmountsByTax = getFromMapValues(amountsByTax, discountAmount);
            }
        }

        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        SubCategoryInvoiceAgregate discountAggregate = new SubCategoryInvoiceAgregate(scAggregate.getInvoiceSubCategory(), billingAccount, scAggregate.getUserAccount(), scAggregate.getWallet(), invoice, null);

        discountAggregate.updateAudit(currentUser);
        discountAggregate.setItemNumber(scAggregate.getItemNumber());
        discountAggregate.setCategoryInvoiceAgregate(cAggregate);

        discountAggregate.setDiscountAggregate(true);
        if (discountPlanItem.getDiscountPlanItemType().equals(DiscountPlanItemTypeEnum.PERCENTAGE)) {
            discountAggregate.setDiscountPercent(discountValue);
        }
        discountAggregate.setDiscountPlanItem(discountPlanItem);
        discountAggregate.setDescription(discountPlanItem.getCode());

        discountAggregate.setAmountsByTax(discountAmountsByTax, isEnterprise);

        discountAggregate.computeDerivedAmounts(isEnterprise, rounding, roundingMode.getRoundingMode(), invoiceRounding, invoiceRoundingMode.getRoundingMode());

        invoice.addInvoiceAggregate(discountAggregate);
        return discountAggregate;

    }

    /**
     * Determine a discount amount or percent to apply
     *
     * @param invoice Invoice to apply discount on
     * @param scAggregate Subcategory aggregate to apply discount on
     * @param amount Amount to apply discount on
     * @param discountPlanItem Discount configuration
     * @return A discount percent (0-100)
     */
    private BigDecimal getDiscountAmountOrPercent(Invoice invoice, SubCategoryInvoiceAgregate scAggregate, BigDecimal amount, DiscountPlanItem discountPlanItem) {
        BigDecimal computedDiscount = discountPlanItem.getDiscountValue();

        final String dpValueEL = discountPlanItem.getDiscountValueEL();
        if (isNotBlank(dpValueEL)) {
            final BigDecimal evalDiscountValue = evaluateDiscountPercentExpression(dpValueEL, scAggregate.getBillingAccount(), scAggregate.getWallet(), invoice, amount);
            log.debug("for discountPlan {} percentEL -> {}  on amount={}", discountPlanItem.getCode(), computedDiscount, amount);
            if (computedDiscount != null) {
                computedDiscount = evalDiscountValue;
            }
        }
        if (computedDiscount == null || amount == null) {
            return BigDecimal.ZERO;
        }

        return computedDiscount;
    }

    private List<DiscountPlanItem> getApplicableDiscountPlanItems(BillingAccount billingAccount, List<DiscountPlanInstance> discountPlanInstances, Invoice invoice, CustomerAccount customerAccount)
            throws BusinessException {
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
        for (DiscountPlanInstance dpi : discountPlanInstances) {
            if (!dpi.isEffective(invoice.getInvoiceDate())) {
                continue;
            }
            if (dpi.getDiscountPlan().isActive()) {
                List<DiscountPlanItem> discountPlanItems = dpi.getDiscountPlan().getDiscountPlanItems();
                for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                    if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(), customerAccount, billingAccount, invoice, dpi)) {
                        applicableDiscountPlanItems.add(discountPlanItem);
                    }
                }
            }
        }
        return applicableDiscountPlanItems;
    }

    /**
     * @param expression EL exprestion
     * @param customerAccount customer account
     * @param billingAccount billing account
     * @param invoice invoice
     * @param dpi the discount plan instance
     * @return true/false
     * @throws BusinessException business exception.
     */
    private boolean matchDiscountPlanItemExpression(String expression, CustomerAccount customerAccount, BillingAccount billingAccount, Invoice invoice, DiscountPlanInstance dpi) throws BusinessException {
        Boolean result = true;

        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, customerAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE_SHORT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_INVOICE_SHORT, invoice);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_INVOICE, invoice);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_DISCOUNT_PLAN_INSTANCE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_DISCOUNT_PLAN_INSTANCE, dpi);
        }
        if (expression.indexOf("su") >= 0) {
            userMap.put("su", invoice.getSubscription());
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @param expression el expression
     * @param userAccount user account
     * @param wallet wallet
     * @param invoice invoice
     * @param subCatTotal total of sub category
     * @return amount
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateDiscountPercentExpression(String expression, BillingAccount billingAccount, WalletInstance wallet, Invoice invoice, BigDecimal subCatTotal) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, billingAccount.getCustomerAccount());
        userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        userMap.put("iv", invoice);
        userMap.put("invoice", invoice);
        userMap.put("wa", wallet);
        userMap.put("amount", subCatTotal);

        BigDecimal result = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        return result;
    }

    private Invoice instantiateInvoice(IBillableEntity entity, BillingAccount billingAccount, Seller seller, BillingRun billingRun, Date invoiceDate, boolean isDraft, BillingCycle billingCycle,
            PaymentMethod paymentMethod, InvoiceType invoiceType, boolean isPrepaid, BigDecimal balance) throws BusinessException {

        Invoice invoice = new Invoice();

        invoice.setBillingAccount(billingAccount);
        invoice.setSeller(seller);
        invoice.setStatus(InvoiceStatusEnum.CREATED);
        invoice.setInvoiceType(invoiceType);
        invoice.setPrepaid(isPrepaid);
        invoice.setInvoiceDate(invoiceDate);
        if (billingRun != null) {
            invoice.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));
        }
        Order order = null;
        if (entity instanceof Order) {
            order = (Order) entity;
            invoice.setOrder(order);

        } else if (entity instanceof Subscription) {
            invoice.setSubscription((Subscription) entity);
        }
        if (paymentMethod != null) {
            invoice.setPaymentMethodType(paymentMethod.getPaymentType());
            invoice.setPaymentMethod(paymentMethod);
        }

        // Set due balance
        invoice.setDueBalance(balance.setScale(appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode().getRoundingMode()));

        return invoice;
    }

    private void setInvoiceDueDate(Invoice invoice, BillingCycle billingCycle) {

        BillingAccount billingAccount = invoice.getBillingAccount();
        CustomerAccount customerAccount = invoice.getBillingAccount().getCustomerAccount();
        Order order = invoice.getOrder();

        // Determine invoice due date delay either from Order, Customer account or Billing cycle
        Integer delay = 0;
        if (order != null && !StringUtils.isBlank(order.getDueDateDelayEL())) {
            delay = evaluateDueDelayExpression(order.getDueDateDelayEL(), billingAccount, invoice, order);

        } else if (!StringUtils.isBlank(customerAccount.getDueDateDelayEL())) {
            delay = evaluateDueDelayExpression(customerAccount.getDueDateDelayEL(), billingAccount, invoice, order);

        } else if (!StringUtils.isBlank(billingCycle.getDueDateDelayEL())) {
            delay = evaluateDueDelayExpression(billingCycle.getDueDateDelayEL(), billingAccount, invoice, order);
        }
        if (delay == null) {
            throw new BusinessException("Due date delay is null");
        }

        Date dueDate = DateUtils.addDaysToDate(invoice.getInvoiceDate(), delay);

        invoice.setDueDate(dueDate);
    }

    /**
     * Resolve Invoice production date delay for a given billing run
     *
     * @param el EL expression to resolve
     * @param billingRun Billing run
     * @return An integer value
     */
    public static Integer resolveInvoiceProductionDateDelay(String el, BillingRun billingRun) {
        return ValueExpressionWrapper.evaluateExpression(el, Integer.class, billingRun);
    }

    /**
     * Recalculate tax to see if it has changed
     *
     * @param tax Previous tax
     * @param isExonerated Is Billing account exonerated from taxes
     * @param invoice Invoice in reference
     * @param taxClass Tax class
     * @param userAccount User account to calculate tax by external program
     * @param taxZero Zero tax to apply if Billing account is exonerated
     * @param calculateExternalTax Should tax be calculated by an external program if invoiceSubCategory has such script set
     * @return An array containing applicable tax and True/false if tax % has changed from a previous tax
     * @throws BusinessException Were not able to determine a tax
     */
    private Object[] getApplicableTax(Tax tax, boolean isExonerated, Invoice invoice, TaxClass taxClass, UserAccount userAccount, Tax taxZero, boolean calculateExternalTax) throws BusinessException {

        if (isExonerated) {
            return new Object[] { taxZero, false };

        } else {

            TaxInfo recalculatedTaxInfo = taxMappingService.determineTax(taxClass, invoice.getSeller(), invoice.getBillingAccount(), userAccount, invoice.getInvoiceDate(), false, false);

            Tax recalculatedTax = recalculatedTaxInfo.tax;

            return new Object[] { recalculatedTax, !tax.getId().equals(recalculatedTax.getId()) };
        }
    }

    /**
     * Create an invoice from an InvoiceDto
     *
     * @param invoiceDTO
     * @param seller
     * @param billingAccount
     * @param invoiceType
     * @return invoice
     * @throws EntityDoesNotExistsException
     * @throws BusinessApiException
     * @throws BusinessException
     * @throws InvalidParameterException
     */
    public Invoice createInvoice(InvoiceDto invoiceDTO, Seller seller, BillingAccount billingAccount, InvoiceType invoiceType)
            throws EntityDoesNotExistsException, BusinessApiException, BusinessException, InvalidParameterException {

        Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();
        boolean isEnterprise = appProvider.isEntreprise();
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        Auditable auditable = new Auditable(currentUser);

        boolean isDetailledInvoiceMode = InvoiceModeEnum.DETAILLED == invoiceDTO.getInvoiceMode();

        Map<InvoiceSubCategory, List<RatedTransaction>> existingRtsTolinkMap = extractMappedRatedTransactionsTolink(invoiceDTO, billingAccount);
        Map<InvoiceCategory, List<InvoiceSubCategory>> subCategoryMap = existingRtsTolinkMap.isEmpty() ? new HashMap<InvoiceCategory, List<InvoiceSubCategory>>()
                : existingRtsTolinkMap.keySet().stream().collect(Collectors.groupingBy(InvoiceSubCategory::getInvoiceCategory));
        Invoice invoice = this.initInvoice(invoiceDTO, billingAccount, invoiceType, seller);

        for (CategoryInvoiceAgregateDto catInvAgrDto : invoiceDTO.getCategoryInvoiceAgregates()) {
            UserAccount userAccount = extractUserAccount(billingAccount, catInvAgrDto);
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode());
            CategoryInvoiceAgregate invoiceAgregateCat = initCategoryInvoiceAgregate(billingAccount, auditable, invoice, userAccount, invoiceCategory, catInvAgrDto.getListSubCategoryInvoiceAgregateDto().size(),
                catInvAgrDto.getDescription());

            for (SubCategoryInvoiceAgregateDto subCatInvAgrDTO : catInvAgrDto.getListSubCategoryInvoiceAgregateDto()) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubcategoryService.findByCode(subCatInvAgrDTO.getInvoiceSubCategoryCode());
                SubCategoryInvoiceAgregate invoiceAgregateSubcat = initSubCategoryInvoiceAgregate(auditable, invoice, userAccount, invoiceAgregateCat, subCatInvAgrDTO.getDescription(), invoiceSubCategory);
                if (isDetailledInvoiceMode) {
                    createAndLinkRTsFromDTO(seller, billingAccount, isEnterprise, invoiceRounding, invoiceRoundingMode, isDetailledInvoiceMode, invoice, userAccount, subCatInvAgrDTO, invoiceSubCategory,
                        invoiceAgregateSubcat);
                }
                linkExistingRTs(invoiceDTO, existingRtsTolinkMap, isEnterprise, invoice, userAccount, invoiceSubCategory, invoiceAgregateSubcat, isDetailledInvoiceMode);
                saveInvoiceSubCatAndRts(invoice, invoiceAgregateSubcat, subCatInvAgrDTO, billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoiceRounding, invoiceRoundingMode, isDetailledInvoiceMode);
                addSubCategoryAmountsToCategory(invoiceAgregateCat, invoiceAgregateSubcat);
            }

            if (isDetailledInvoiceMode && !existingRtsTolinkMap.isEmpty() && subCategoryMap.containsKey(invoiceCategory)) {
                List<InvoiceSubCategory> subCategories = subCategoryMap.get(invoiceCategory);
                linkRtsAndSubCats(billingAccount, taxInvoiceAgregateMap, isEnterprise, invoiceRounding, invoiceRoundingMode, auditable, isDetailledInvoiceMode, existingRtsTolinkMap, invoice, userAccount,
                    invoiceAgregateCat, subCategories);
            }
            getEntityManager().flush();
            addCategoryAmountsToInvoice(invoice, invoiceAgregateCat);
            subCategoryMap.remove(invoiceCategory);
        }

        linkRtsHavingCategoryOutOfInput(billingAccount, isEnterprise, auditable, isDetailledInvoiceMode, existingRtsTolinkMap, subCategoryMap, invoice, taxInvoiceAgregateMap, invoiceRounding, invoiceRoundingMode);

        invoice = finaliseInvoiceCreation(invoiceDTO, isEnterprise, invoiceRounding, invoiceRoundingMode, invoice);
        return invoice;
    }

    private void linkRtsAndSubCats(BillingAccount billingAccount, Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, Auditable auditable,
            boolean isDetailledInvoiceMode, Map<InvoiceSubCategory, List<RatedTransaction>> existingRtsTolinkMap, Invoice invoice, UserAccount userAccount, CategoryInvoiceAgregate invoiceAgregateCat,
            List<InvoiceSubCategory> subCategories) {
        for (InvoiceSubCategory invoiceSubCategory : subCategories) {
            if (existingRtsTolinkMap.containsKey(invoiceSubCategory)) {
                List<RatedTransaction> rtsToLink = existingRtsTolinkMap.remove(invoiceSubCategory);

                SubCategoryInvoiceAgregate invoiceAgregateSubcat = initSubCategoryInvoiceAgregate(auditable, invoice, userAccount, invoiceAgregateCat, invoiceSubCategory.getDescription(), invoiceSubCategory);
                for (RatedTransaction rt : rtsToLink) {
                    linkRt(invoice, invoiceAgregateSubcat, rt, isEnterprise);
                }
                addSubCategoryAmountsToCategory(invoiceAgregateCat, invoiceAgregateSubcat);
                saveInvoiceSubCatAndRts(invoice, invoiceAgregateSubcat, null, billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoiceRounding, invoiceRoundingMode, isDetailledInvoiceMode);
            }
        }
    }

    private void linkExistingRTs(InvoiceDto invoiceDTO, Map<InvoiceSubCategory, List<RatedTransaction>> existingRtsTolinkMap, boolean isEnterprise, Invoice invoice, UserAccount userAccount,
            InvoiceSubCategory invoiceSubCategory, SubCategoryInvoiceAgregate invoiceAgregateSubcat, boolean isDetailledInvoiceMode) {
        List<RatedTransaction> rtsToLink = new ArrayList<>();
        if (invoiceDTO.getInvoiceType().equals(invoiceTypeService.getCommercialCode())) {
            rtsToLink = ratedTransactionService.openRTbySubCat(userAccount != null ? userAccount.getWallet() : null, invoiceSubCategory, null, null);
            if (isDetailledInvoiceMode)
                removeRtsFromExistingRtsToLink(existingRtsTolinkMap, rtsToLink);
        } else if (isDetailledInvoiceMode && !existingRtsTolinkMap.isEmpty() && existingRtsTolinkMap.containsKey(invoiceSubCategory)) {
            rtsToLink = existingRtsTolinkMap.remove(invoiceSubCategory);
        }

        for (RatedTransaction rt : rtsToLink) {
            linkRt(invoice, invoiceAgregateSubcat, rt, isEnterprise);
        }
    }

    private void removeRtsFromExistingRtsToLink(Map<InvoiceSubCategory, List<RatedTransaction>> existingRtsTolinkMap, List<RatedTransaction> rtsToLink) {
        List<InvoiceSubCategory> invoicesToRemove = new ArrayList<>();
        for (InvoiceSubCategory invSubCat : existingRtsTolinkMap.keySet()) {
            List<RatedTransaction> ratedTransactions = existingRtsTolinkMap.get(invSubCat);
            for (RatedTransaction rtToLink : rtsToLink) {
                ratedTransactions.remove(rtToLink);
            }
            if (ratedTransactions.isEmpty())
                invoicesToRemove.add(invSubCat);
        }
        for(InvoiceSubCategory invoiceSubCategory : invoicesToRemove)
            existingRtsTolinkMap.remove(invoiceSubCategory);
    }

    private void createAndLinkRTsFromDTO(Seller seller, BillingAccount billingAccount, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, boolean isDetailledInvoiceMode, Invoice invoice,
            UserAccount userAccount, SubCategoryInvoiceAgregateDto subCatInvAgrDTO, InvoiceSubCategory invoiceSubCategory, SubCategoryInvoiceAgregate invoiceAgregateSubcat) {
        if (subCatInvAgrDTO.getRatedTransactions() != null) {
            for (RatedTransactionDto ratedTransactionDto : subCatInvAgrDTO.getRatedTransactions()) {
                RatedTransaction rt = constructRatedTransaction(seller, billingAccount, isEnterprise, invoiceRounding, invoiceRoundingMode, userAccount, invoiceSubCategory, isDetailledInvoiceMode, ratedTransactionDto);
                linkRt(invoice, invoiceAgregateSubcat, rt, isEnterprise);
            }
        }
    }

    private void linkRtsHavingCategoryOutOfInput(BillingAccount billingAccount, boolean isEnterprise, Auditable auditable, boolean isDetailledInvoiceMode,
            Map<InvoiceSubCategory, List<RatedTransaction>> existingRtsTolinkMap, Map<InvoiceCategory, List<InvoiceSubCategory>> subCategoryMap, Invoice invoice, Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap,
            int invoiceRounding, RoundingModeEnum invoiceRoundingMode) {
        if (isDetailledInvoiceMode && !subCategoryMap.isEmpty()) {
            for (InvoiceCategory invoiceCategory : subCategoryMap.keySet()) {
                List<InvoiceSubCategory> subCategories = subCategoryMap.get(invoiceCategory);
                UserAccount userAccount = billingAccount.getUsersAccounts().get(0);
                CategoryInvoiceAgregate invoiceAgregateCat = initCategoryInvoiceAgregate(billingAccount, auditable, invoice, userAccount, invoiceCategory, subCategories.size(), invoiceCategory.getDescription());
                linkRtsAndSubCats(billingAccount, taxInvoiceAgregateMap, isEnterprise, invoiceRounding, invoiceRoundingMode, auditable, isDetailledInvoiceMode, existingRtsTolinkMap, invoice, userAccount,
                    invoiceAgregateCat, subCategories);
                addCategoryAmountsToInvoice(invoice, invoiceAgregateCat);
            }
        }
    }

    private Invoice finaliseInvoiceCreation(InvoiceDto invoiceDTO, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, Invoice invoice) {
        invoice.setAmountWithoutTax(round(invoice.getAmountWithoutTax(), invoiceRounding, invoiceRoundingMode));
        invoice.setAmountTax(round(invoice.getAmountTax(), invoiceRounding, invoiceRoundingMode));
        invoice.setAmountWithTax(round(invoice.getAmountWithTax(), invoiceRounding, invoiceRoundingMode));

        BigDecimal netToPay = invoice.getAmountWithTax();
        if (!isEnterprise && invoiceDTO.isIncludeBalance() != null && invoiceDTO.isIncludeBalance()) {
            // Calculate customer account balance
            boolean isBalanceDue = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.due", true);
            boolean isBalanceLitigation = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.litigation", false);
            BigDecimal balance = null;
            if (isBalanceLitigation) {
                balance = customerAccountService.customerAccountBalanceDue(invoice.getBillingAccount().getCustomerAccount(), isBalanceDue ? invoice.getDueDate() : null);
            } else {
                balance = customerAccountService.customerAccountBalanceDueWithoutLitigation(invoice.getBillingAccount().getCustomerAccount(), isBalanceDue ? invoice.getDueDate() : null);
            }
            if (balance == null) {
                throw new BusinessException("account balance calculation failed");
            }
            netToPay = invoice.getAmountWithTax().add(round(balance, invoiceRounding, invoiceRoundingMode));
        }
        invoice.setNetToPay(netToPay);
        if (invoiceDTO.isAutoValidation() == null || invoiceDTO.isAutoValidation()) {
            invoice = serviceSingleton.assignInvoiceNumberVirtual(invoice);
        }
        this.postCreate(invoice);
        return invoice;
    }

    private void addCategoryAmountsToInvoice(Invoice invoice, CategoryInvoiceAgregate invoiceAgregateCat) {
        invoice.addAmountTax(invoiceAgregateCat.getAmountTax());
        invoice.addAmountWithoutTax(invoiceAgregateCat.getAmountWithoutTax());
        invoice.addAmountWithTax(invoiceAgregateCat.getAmountWithTax());
    }

    private void addSubCategoryAmountsToCategory(CategoryInvoiceAgregate invoiceAgregateCat, SubCategoryInvoiceAgregate invoiceAgregateSubcat) {
        invoiceAgregateCat.addAmountTax(invoiceAgregateSubcat.getAmountTax());
        invoiceAgregateCat.addAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax());
        invoiceAgregateCat.addAmountWithTax(invoiceAgregateSubcat.getAmountWithTax());
    }

    private void saveInvoiceSubCatAndRts(Invoice invoice, SubCategoryInvoiceAgregate invoiceAgregateSubcat, SubCategoryInvoiceAgregateDto invAgrCatDTO, BillingAccount billingAccount,
            Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap, boolean isEnterprise, Auditable auditable, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, boolean isDetailledInvoiceMode) {
        List<RatedTransaction> ratedTransactions = new ArrayList<RatedTransaction>();
        if (isDetailledInvoiceMode) {
            invoiceAgregateSubcat.setItemNumber(invoiceAgregateSubcat.getRatedtransactionsToAssociate().size());
            putTaxInvoiceAgregate(billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoice, invoiceAgregateSubcat, invoiceRounding, invoiceRoundingMode);
            ratedTransactions = invoiceAgregateSubcat.getRatedtransactionsToAssociate();
        } else {
            if (isEnterprise) {
                if (invAgrCatDTO.getAmountWithoutTax() == null || invAgrCatDTO.getAmountTax() == null) {
                    throw new InvalidParameterException("For aggregated invoices, when provider is an entreprise, amount without tax and tax amount must be provided");
                }
            } else {
                if (invAgrCatDTO.getAmountWithTax() == null || invAgrCatDTO.getAmountTax() == null) {
                    throw new InvalidParameterException("For aggregated invoices, when provider is not an entreprise, tax amount and amount with tax must be provided ");
                }
            }

            // we add subCatAmountWithoutTax, in the case if there any opened RT to include
            BigDecimal[] amounts = NumberUtils.computeDerivedAmountsWoutTaxPercent(invAgrCatDTO.getAmountWithoutTax(), invAgrCatDTO.getAmountWithTax(), invAgrCatDTO.getAmountTax(), isEnterprise, invoiceRounding,
                invoiceRoundingMode.getRoundingMode());
            invoiceAgregateSubcat.setAmountWithoutTax(amounts[0]);
            invoiceAgregateSubcat.setAmountWithTax(amounts[1]);
            invoiceAgregateSubcat.setAmountTax(amounts[2]);
        }

        if (invoice.getId() == null) {
            create(invoice);
        } else {
            getEntityManager().persist(invoiceAgregateSubcat);
        }
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if (ratedTransaction.getId() == null) {
                getEntityManager().persist(ratedTransaction);
            } else {
                getEntityManager().merge(ratedTransaction);
            }
        }
    }

    private void putTaxInvoiceAgregate(BillingAccount billingAccount, Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap, boolean isEnterprise, Auditable auditable, Invoice invoice,
            SubCategoryInvoiceAgregate invoiceAgregateSubcat, int invoiceRounding, RoundingModeEnum invoiceRoundingMode) {
        for (Map.Entry<Tax, SubcategoryInvoiceAgregateAmount> amountByTax : invoiceAgregateSubcat.getAmountsByTax().entrySet()) {
            if (BigDecimal.ZERO.compareTo(amountByTax.getValue().getAmount(!isEnterprise)) != 0) {
                Tax tax = amountByTax.getKey();
                TaxInvoiceAgregate invoiceAgregateTax;
                if (taxInvoiceAgregateMap.containsKey(tax.getId())) {
                    invoiceAgregateTax = taxInvoiceAgregateMap.get(tax.getId());
                } else {
                    invoiceAgregateTax = initTaxInvoiceAgregate(billingAccount, auditable, invoice, tax);
                }
                if (isEnterprise) {
                    invoiceAgregateTax.addAmountWithoutTax(amountByTax.getValue().getAmountWithoutTax());
                } else {
                    invoiceAgregateTax.addAmountWithTax(amountByTax.getValue().getAmountWithTax());
                }

                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(invoiceAgregateTax.getAmountWithoutTax(), invoiceAgregateTax.getAmountWithTax(), invoiceAgregateTax.getTaxPercent(), isEnterprise, invoiceRounding,
                    invoiceRoundingMode.getRoundingMode());
                invoiceAgregateTax.setAmountWithoutTax(amounts[0]);
                invoiceAgregateTax.setAmountWithTax(amounts[1]);
                invoiceAgregateTax.setAmountTax(amounts[2]);

                taxInvoiceAgregateMap.put(tax.getId(), invoiceAgregateTax);
            }
        }
    }

    private TaxInvoiceAgregate initTaxInvoiceAgregate(BillingAccount billingAccount, Auditable auditable, Invoice invoice, Tax tax) {
        TaxInvoiceAgregate invoiceAgregateTax;
        invoiceAgregateTax = new TaxInvoiceAgregate();
        invoiceAgregateTax.setInvoice(invoice);
        invoiceAgregateTax.setBillingRun(null);
        invoiceAgregateTax.setTax(tax);
        invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
        invoiceAgregateTax.setTaxPercent(tax.getPercent());
        invoiceAgregateTax.setAmountWithoutTax(BigDecimal.ZERO);
        invoiceAgregateTax.setAmountWithTax(BigDecimal.ZERO);
        invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
        invoiceAgregateTax.setBillingAccount(billingAccount);
        invoiceAgregateTax.setAuditable(auditable);
        invoice.addInvoiceAggregate(invoiceAgregateTax);
        return invoiceAgregateTax;
    }

    private void linkRt(Invoice invoice, SubCategoryInvoiceAgregate invoiceAgregateSubcat, RatedTransaction rt, boolean isEntreprise) {
        rt.changeStatus(RatedTransactionStatusEnum.BILLED);
        rt.setInvoice(invoice);
        rt.setInvoiceAgregateF(invoiceAgregateSubcat);
        invoiceAgregateSubcat.addRatedTransaction(rt, isEntreprise, false);
        addRTAmountsToSubcategoryInvoiceAggregate(invoiceAgregateSubcat, rt);
    }

    private void addRTAmountsToSubcategoryInvoiceAggregate(SubCategoryInvoiceAgregate invoiceAgregateSubcat, RatedTransaction rt) {
        invoiceAgregateSubcat.addAmountWithoutTax(rt.getAmountWithoutTax());
        invoiceAgregateSubcat.addAmountTax(rt.getAmountTax());
        invoiceAgregateSubcat.addAmountWithTax(rt.getAmountWithTax());
    }

    private RatedTransaction constructRatedTransaction(Seller seller, BillingAccount billingAccount, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, UserAccount userAccount,
            InvoiceSubCategory invoiceSubCategory, boolean isDetailledInvoiceMode, RatedTransactionDto ratedTransactionDto) {
        BigDecimal tempAmountWithoutTax = BigDecimal.ZERO;
        if (ratedTransactionDto.getUnitAmountWithoutTax() != null) {
            tempAmountWithoutTax = ratedTransactionDto.getUnitAmountWithoutTax().multiply(ratedTransactionDto.getQuantity());
        }
        BigDecimal tempAmountWithTax = BigDecimal.ZERO;
        if (ratedTransactionDto.getUnitAmountWithTax() != null) {
            tempAmountWithTax = ratedTransactionDto.getUnitAmountWithTax().multiply(ratedTransactionDto.getQuantity());
        }

        if (ratedTransactionDto.getTaxCode() == null) {
            throw new BusinessException("Tax code not provided for a rated transaction");
        }
        Tax tax = taxService.findByCode(ratedTransactionDto.getTaxCode());
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, ratedTransactionDto.getTaxCode());
        }

        TaxClass taxClass = null;
        if (!StringUtils.isBlank(ratedTransactionDto.getTaxClassCode())) {
            taxClass = taxClassService.findByCode(ratedTransactionDto.getTaxClassCode());
            if (taxClass == null) {
                throw new EntityDoesNotExistsException(TaxClass.class, ratedTransactionDto.getTaxClassCode());
            }
        }

        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(tempAmountWithoutTax, tempAmountWithTax, tax.getPercent(), isEnterprise, invoiceRounding, invoiceRoundingMode.getRoundingMode());

        BigDecimal amountWithoutTax = amounts[0];
        BigDecimal amountWithTax = amounts[1];
        BigDecimal amountTax = amounts[2];

        RatedTransaction rt = new RatedTransaction(ratedTransactionDto.getUsageDate(), ratedTransactionDto.getUnitAmountWithoutTax(), ratedTransactionDto.getUnitAmountWithTax(), ratedTransactionDto.getUnitAmountTax(),
            ratedTransactionDto.getQuantity(), amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.BILLED, userAccount != null ? userAccount.getWallet() : null, billingAccount, userAccount,
            invoiceSubCategory, null, null, null, null, null, null, ratedTransactionDto.getUnityDescription(), null, null, null, null, ratedTransactionDto.getCode(), ratedTransactionDto.getDescription(),
            ratedTransactionDto.getStartDate(), ratedTransactionDto.getEndDate(), seller, tax, tax.getPercent(), null, taxClass, null, null);

        rt.setWallet(userAccount != null ? userAccount.getWallet() : null);
        // #3355 : setting params 1,2,3
        if (isDetailledInvoiceMode) {
            rt.setParameter1(ratedTransactionDto.getParameter1());
            rt.setParameter2(ratedTransactionDto.getParameter2());
            rt.setParameter3(ratedTransactionDto.getParameter3());
        }
        return rt;
    }

    private UserAccount extractUserAccount(BillingAccount billingAccount, CategoryInvoiceAgregateDto catInvAgrDto) {
        UserAccount userAccount = null;
        if (catInvAgrDto.getUserAccountCode() != null) {
            userAccount = userAccountService.findByCode(catInvAgrDto.getUserAccountCode());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, catInvAgrDto.getUserAccountCode());
            } else if (!userAccount.getBillingAccount().equals(billingAccount)) {
                throw new InvalidParameterException("User account code " + catInvAgrDto.getUserAccountCode() + " does not correspond to a Billing account " + billingAccount.getCode());
            }
        }
        return userAccount;
    }

    private CategoryInvoiceAgregate initCategoryInvoiceAgregate(BillingAccount billingAccount, Auditable auditable, Invoice invoice, UserAccount userAccount, InvoiceCategory invoiceCategory, Integer size,
            String description) {

        CategoryInvoiceAgregate invoiceAgregateCat = new CategoryInvoiceAgregate();
        invoiceAgregateCat.setAuditable(auditable);
        invoiceAgregateCat.setInvoice(invoice);
        invoiceAgregateCat.setBillingRun(null);

        invoiceAgregateCat.setDescription(description);

        invoiceAgregateCat.setItemNumber(size);
        invoiceAgregateCat.setUserAccount(userAccount);
        invoiceAgregateCat.setBillingAccount(billingAccount);
        invoiceAgregateCat.setInvoiceCategory(invoiceCategory);
        invoiceAgregateCat.setUserAccount(userAccount);
        invoice.addInvoiceAggregate(invoiceAgregateCat);
        return invoiceAgregateCat;
    }

    private SubCategoryInvoiceAgregate initSubCategoryInvoiceAgregate(Auditable auditable, Invoice invoice, UserAccount userAccount, CategoryInvoiceAgregate invoiceAgregateCat, String description,
            InvoiceSubCategory invoiceSubCategory) {
        SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
        invoiceAgregateSubcat.setCategoryInvoiceAgregate(invoiceAgregateCat);
        invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCategory);
        invoiceAgregateSubcat.setInvoice(invoice);
        invoiceAgregateSubcat.setDescription(description);
        invoiceAgregateSubcat.setBillingRun(null);
        if (userAccount != null) {
            invoiceAgregateSubcat.setWallet(userAccount.getWallet());
            invoiceAgregateSubcat.setUserAccount(userAccount);
        }
        invoiceAgregateSubcat.setAccountingCode(invoiceSubCategory.getAccountingCode());
        invoiceAgregateSubcat.setAuditable(auditable);
        invoice.addInvoiceAggregate(invoiceAgregateSubcat);
        return invoiceAgregateSubcat;
    }

    private Map<InvoiceSubCategory, List<RatedTransaction>> extractMappedRatedTransactionsTolink(InvoiceDto invoiceDTO, BillingAccount billingAccount) {
        List<Long> ratedTransactionsIdsTolink = invoiceDTO.getRatedTransactionsTolink();
        List<RatedTransaction> ratedTransactionsTolink = null;
        if (CollectionUtils.isNotEmpty(ratedTransactionsIdsTolink)) {
            if (!InvoiceModeEnum.DETAILLED.equals(invoiceDTO.getInvoiceMode())) {
                throw new BusinessException("use of ratedTransactionsTolink is only allowed if invoiceMode=='DETAILLED'");
            }
            Set<Long> uniqueIds = new HashSet<>();
            ratedTransactionsIdsTolink.removeIf(id -> !uniqueIds.add(id));
            if (uniqueIds.size() != ratedTransactionsIdsTolink.size()) {
                throw new BusinessException("duplicated values on list of ratedTransactionsTolink: " + ratedTransactionsIdsTolink.toString());
            }
            ratedTransactionsTolink = ratedTransactionService.listByBillingAccountAndIDs(billingAccount.getId(), uniqueIds);
            if (ratedTransactionsTolink == null || ratedTransactionsTolink.size() != uniqueIds.size()) {
                Set<Long> matchedIds = ratedTransactionsTolink.stream().map(x -> x.getId()).collect(Collectors.toSet());
                uniqueIds.removeIf(id -> !matchedIds.add(id));
                throw new BusinessException("ratedTransactionsTolink contains invalid Ids: " + uniqueIds.toString());
            }
            return ratedTransactionsTolink.stream().collect(Collectors.groupingBy(RatedTransaction::getInvoiceSubCategory));
        }
        return new HashMap<InvoiceSubCategory, List<RatedTransaction>>();
    }

    private Invoice initInvoice(InvoiceDto invoiceDTO, BillingAccount billingAccount, InvoiceType invoiceType, Seller seller) throws BusinessException, EntityDoesNotExistsException, BusinessApiException {
        Invoice invoice = new Invoice();
        invoice.setBillingAccount(billingAccount);
        invoice.setSeller(seller);
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setDueDate(invoiceDTO.getDueDate());
        invoice.setDraft(invoiceDTO.isDraft());
        invoice.setAlreadySent(invoiceDTO.isCheckAlreadySent());
        if (invoiceDTO.isCheckAlreadySent()) {
            invoice.setStatus(InvoiceStatusEnum.SENT);
        } else {
            invoice.setStatus(InvoiceStatusEnum.CREATED);
        }
        invoice.setDontSend(invoiceDTO.isSentByEmail());
        PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            invoice.setPaymentMethodType(preferedPaymentMethod.getPaymentType());
        }
        invoice.setInvoiceType(invoiceType);
        if (invoiceDTO.getListInvoiceIdToLink() != null) {
            for (Long invoiceId : invoiceDTO.getListInvoiceIdToLink()) {
                Invoice invoiceTmp = findById(invoiceId);
                if (invoiceTmp == null) {
                    throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
                }
                if (!invoiceType.getAppliesTo().contains(invoiceTmp.getInvoiceType())) {
                    throw new BusinessApiException("InvoiceId " + invoiceId + " cant be linked");
                }
                invoice.getLinkedInvoices().add(invoiceTmp);
            }
        }

        return invoice;
    }

    /**
     * Delete invoices associated to a billing run
     *
     * @param billingRun Billing run
     */
    public void deleteInvoices(BillingRun billingRun) {
        getEntityManager().createNamedQuery("Invoice.deleteByBR").setParameter("billingRunId", billingRun.getId()).executeUpdate();
    }

    public void deleteInvoices(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("Invoice.deleteByIds").setParameter("invoicesIds", invoicesIds).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<Long> excludePrepaidInvoices(Collection<Long> invoicesIds) {
        return getEntityManager().createNamedQuery("Invoice.excludePrpaidInvoices").setParameter("invoicesIds", invoicesIds).getResultList();

    }

    /**
     * Rated transactions to invoice
     */
    protected class RatedTransactionsToInvoice {

        /**
         * Indicates that there are more RTs to be retrieved and aggregated in invoice before invoice can be closed
         */
        protected boolean moreRatedTransactions;

        /**
         * Rated transactions split for invoicing based on Billing account, seller and invoice type
         */
        protected List<RatedTransactionGroup> ratedTransactionGroups;

        /**
         * Constructor
         *
         * @param moreRatedTransactions Indicates that there are more RTs to be retrieved and aggregated in invoice before invoice can be closed
         * @param ratedTransactionGroups Rated transactions split for invoicing based on Billing account, seller and invoice type
         */
        protected RatedTransactionsToInvoice(boolean moreRatedTransactions, List<RatedTransactionGroup> ratedTransactionGroups) {
            super();
            this.moreRatedTransactions = moreRatedTransactions;
            this.ratedTransactionGroups = ratedTransactionGroups;
        }
    }

    /**
     * Stores Invoice and invoice aggregate information between paginated RT invoicing
     */
    private class InvoiceAggregateProcessingInfo {

        /**
         * Invoice
         */
        private Invoice invoice = null;

        /**
         * Tax change mapping. Key is ba.id_seller.id_invoiceType.id_ua.id_walletInstance.id_invoiceSubCategory.id_tax.id and value is an array of [Tax to apply, True/false if tax
         * has changed]
         */
        private Map<String, Object[]> taxChangeMap = new HashMap<>();

        /**
         * Subcategory aggregates mapping. Key is ua.id_walletInstance.id_invoiceSubCategory.id_tax.id
         */
        private Map<String, SubCategoryInvoiceAgregate> subCategoryAggregates = new HashMap<>();

        /**
         * Orders (numbers) referenced from Rated transactions
         */
        private Set<String> orderNumbers = new HashSet<String>();
    }

    /**
     * Add two maps with BigDecimal values. In case number of keys don't match, a cumulative set of keys will be considered.
     *
     * @param <T> Map key
     * @param one One map of BigDecimal values
     * @param two Another map of BigDecimal values
     * @return A map containing a sum of two maps
     */
    private <T> Map<T, BigDecimal> addMapValues(Map<T, BigDecimal> one, Map<T, BigDecimal> two) {

        Map<T, BigDecimal> result = new HashMap<>(one);

        for (T key : two.keySet()) {
            if (result.containsKey(key)) {
                result.put(key, result.get(key).add(two.get(key)));
            } else {
                result.put(key, two.get(key));
            }
        }

        return result;
    }

    /**
     * Add values from one map to another one. In case number of keys don't match, a cumulative set of keys will be considered.
     *
     * @param <T> Map key
     * @param one A map of Amounts values to add to
     * @param two Another map of Amounts values to add
     */
    private <T> void addAmountsToMap(Map<T, SubcategoryInvoiceAgregateAmount> one, Map<T, SubcategoryInvoiceAgregateAmount> two) {

        Map<T, SubcategoryInvoiceAgregateAmount> result = new HashMap<>(one);

        for (T key : two.keySet()) {
            if (result.containsKey(key)) {
                result.get(key).addAmounts(two.get(key));
            } else {
                result.put(key, two.get(key));
            }
        }
    }

    /**
     * Sum up BigDecimal values from a map
     *
     * @param <T> Map key
     * @param values A map of BigDecimal values
     * @return A sum of values
     */
    private <T> BigDecimal sumMapValues(Map<T, BigDecimal> values) {

        BigDecimal result = BigDecimal.ZERO;

        for (BigDecimal value : values.values()) {
            result = result.add(value);
        }

        return result;
    }

    /**
     * Sum up BigDecimal values from a map
     *
     * @param <T> Map key
     * @param values A map of BigDecimal values
     * @param isWithTax True is should sum up amount with tax
     * @return A sum of values
     */
    private <T> BigDecimal sumMapValues(Map<T, SubcategoryInvoiceAgregateAmount> values, boolean isWithTax) {

        BigDecimal result = BigDecimal.ZERO;

        for (SubcategoryInvoiceAgregateAmount value : values.values()) {
            result = result.add(value.getAmount(isWithTax));
        }

        return result;
    }

    /**
     * Retrieve first BigDecimal values until exhausting a limit of sum of values while preserving a key
     *
     * @param <T> Map key
     * @param values A map of BigDecimal values
     * @param limitToGet A limit of sum of values to get
     * @return A map of values
     */
    private <T> Map<T, BigDecimal> getFromMapValues(Map<T, BigDecimal> values, BigDecimal limitToGet) {

        Map<T, BigDecimal> result = new HashMap<>();

        for (Entry<T, BigDecimal> amount : values.entrySet()) {
            if (limitToGet.signum() == amount.getValue().signum()) { // Same sign, so use up all amount on first entry
                result.put(amount.getKey(), limitToGet);
                break;
            } else {
                if (limitToGet.abs().compareTo(amount.getValue()) <= 0) { // Value to get is less than available, so use up all amount
                    result.put(amount.getKey(), limitToGet);
                    break;
                } else {
                    limitToGet = limitToGet.add(amount.getValue());
                    result.put(amount.getKey(), amount.getValue().negate());
                }
            }
        }

        return result;
    }

    /**
     * Return the total of positive rated transaction grouped by billing account for a billing run.
     *
     * @param billingRun the billing run
     * @return a map of positive rated transaction grouped by billing account.
     */
    public List<Object[]> getTotalInvoiceableAmountByBR(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("Invoice.sumInvoiceableAmountByBR").setParameter("billingRunId", billingRun.getId()).getResultList();
    }

    /**
     * Resolve Invoice production date delay for a given billing run
     *
     * @param el EL expression to resolve
     * @param billingRun Billing run
     * @return An integer value
     */
    public static Integer resolveInvoiceDateDelay(String el, BillingRun billingRun) {
        return ValueExpressionWrapper.evaluateExpression(el, Integer.class, billingRun);
    }

    /**
     * Resolve Invoice date delay for given parameters
     *
     * @param el EL expression to resolve
     * @param parameters A list of parameters
     * @return An integer value
     */
    public static Integer resolveImmediateInvoiceDateDelay(String el, Object... parameters) {
        return ValueExpressionWrapper.evaluateExpression(el, Integer.class, parameters);
    }
}