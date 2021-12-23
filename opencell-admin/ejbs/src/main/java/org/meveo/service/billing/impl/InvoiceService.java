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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.meveo.commons.utils.NumberUtils.round;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.hibernate.Session;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ConfigurationException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.admin.util.PdfWaterMark;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.billing.BasicInvoice;
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
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.DiscountPlanInstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.InvoiceLinesGroup;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.ReferenceDateEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.SubcategoryInvoiceAgregateAmount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
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
    public static final String INVOICE_ADJUSTMENT_SEQUENCE = "INVOICE_ADJUSTMENT_SEQUENCE";

    /** The Constant INVOICE_SEQUENCE. */
    public static final String INVOICE_SEQUENCE = "INVOICE_SEQUENCE";

    private static final BigDecimal HUNDRED = new BigDecimal("100");

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

    @Inject
    private WalletOperationService walletOperationService;

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

    /**
     * The billing run extension service.
     */
    @Inject
    private BillingRunExtensionService billingRunExtensionService;

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
    private AccountingArticleService accountingArticleService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    private InvoiceLineService invoiceLinesService;

    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    @Inject
    private CommercialOrderService commercialOrderService;

    /**
     * folder for pdf .
     */
    private String PDF_DIR_NAME = "pdf";

    /**
     * folder for adjustment pdf.
     */
    private String ADJUSTEMENT_DIR_NAME = "invoiceAdjustmentPdf";

    /**
     * template jasper name.
     */
    private String INVOICE_TEMPLATE_FILENAME = "invoice.jasper";

    /**
     * date format.
     */
    private String DATE_PATERN = "yyyy.MM.dd";

    /**
     * map used to store temporary jasper report.
     */
    private Map<String, JasperReport> jasperReportMap = new HashMap<>();

    /**
     * Description translation map.
     */
    private Map<String, String> descriptionMap = new HashMap<>();

    private static int rtPaginationSize = 30000;
    
    @Inject
    private CpqQuoteService cpqQuoteService;

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
            return Collections.emptyList();
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
            return Collections.emptyList();
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
        invoice.setInvoiceNumber((prefix == null ? "" : prefix) + invoiceNumber);

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
        return Collections.emptyList();
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
        return Collections.emptyList();
    }

    /**
     * Get rated transactions for entity grouped by billing account, seller and invoice type and payment method
     *
     * @param entityToInvoice entity to be billed
     * @param billingAccount Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each rated transaction.
     * @param billingRun billing run
     * @param defaultBillingCycle Billing cycle applicable to billable entity or to billing run
     * @param defaultInvoiceType Invoice type. A default invoice type for postpaid rated transactions. In case of prepaid RTs, a prepaid invoice type is used. Provided in case of Billing account or Subscription billable
     *        entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @param ratedTransactionFilter rated transaction filter
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param isDraft Is it a draft invoice
     * @param defaultPaymentMethod The default payment method
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicng date value
     * @return List of rated transaction groups for entity and a flag indicating if there are more Rated transactions to retrieve
     * @throws BusinessException BusinessException
     */
    protected RatedTransactionsToInvoice getRatedTransactionGroups(IBillableEntity entityToInvoice, BillingAccount billingAccount, BillingRun billingRun, BillingCycle defaultBillingCycle, InvoiceType defaultInvoiceType,
            Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft, PaymentMethod defaultPaymentMethod, Date invoiceUpToDate) throws BusinessException {

        List<RatedTransaction> ratedTransactions = getRatedTransactions(entityToInvoice, ratedTransactionFilter, firstTransactionDate, lastTransactionDate, invoiceUpToDate, isDraft);

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
        PaymentMethod paymentMethod;
        if (defaultPaymentMethod == null && billingAccount != null) {
            defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
        }

        EntityManager em = getEntityManager();

        for (RatedTransaction rt : ratedTransactions) {

            // Order can span multiple billing accounts and some Billing account-dependent values have to be recalculated
            // Retrieve BA and determine postpaid invoice type only if it has not changed from the last iteration
            if (entityToInvoice instanceof Order && (billingAccount == null || !billingAccount.getId().equals(rt.getBillingAccount().getId()))) {
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
            InvoiceType invoiceType = postPaidInvoiceType;
            boolean isPrepaid = rt.isPrepaid();
            if (isPrepaid) {
                invoiceType = determineInvoiceType(true, isDraft, null, null, null);
            }

            paymentMethod = resolvePaymentMethod(billingAccount, billingCycle, defaultPaymentMethod, rt);

            String invoiceKey = billingAccount.getId() + "_" + rt.getSeller().getId() + "_" + invoiceType.getId() + "_" + isPrepaid + ((paymentMethod == null) ? "" : "_" + paymentMethod.getId());
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

    private List<RatedTransaction> getRatedTransactions(IBillableEntity entityToInvoice, Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, boolean isDraft) {
        List<RatedTransaction> ratedTransactions = ratedTransactionService.listRTsToInvoice(entityToInvoice, firstTransactionDate, lastTransactionDate, invoiceUpToDate, ratedTransactionFilter, rtPaginationSize);
        // if draft add unrated wallet operation
        if (isDraft) {
            ratedTransactions.addAll(getDraftRatedTransactions(entityToInvoice, firstTransactionDate, lastTransactionDate, invoiceUpToDate));
        }
        return ratedTransactions;
    }

    private List<RatedTransaction> getDraftRatedTransactions(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate) {
        return walletOperationService.listToRate(entityToInvoice, invoiceUpToDate).stream()
            .filter(wo -> wo.getOperationDate().before(lastTransactionDate) && (wo.getOperationDate().after(firstTransactionDate) || wo.getOperationDate().equals(firstTransactionDate))).map(RatedTransaction::new)
            .collect(Collectors.toList());
    }

    private List<Long> getDrafWalletOperationIds(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate) {
        return walletOperationService.listToRate(entityToInvoice, invoiceUpToDate).stream()
            .filter(wo -> wo.getOperationDate().before(lastTransactionDate) && (wo.getOperationDate().after(firstTransactionDate) || wo.getOperationDate().equals(firstTransactionDate))).map(BaseEntity::getId)
            .collect(Collectors.toList());
    }

    private List<RatedTransaction> getDraftRatedTransactions(List<Long> walletOperationsIds) {
        return walletOperationService.findByIds(walletOperationsIds).stream().map(RatedTransaction::new).collect(Collectors.toList());
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
            Date lastTransactionDate, MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck) throws BusinessException {
        return createAgregatesAndInvoice(entityToInvoice, billingRun, ratedTransactionFilter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, automaticInvoiceCheck);
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
            MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck) throws BusinessException {

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
                ratedTransactionService.calculateAmountsAndCreateMinAmountTransactions(entityToInvoice, firstTransactionDate, lastTransactionDate, invoiceDate, false, minAmountForAccounts);
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
                balance, automaticInvoiceCheck);

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
        return Collections.emptyList();
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
     * @param defaultBillingCycle Billing cycle applicable to billable entity or to billing run. For Order, if not provided at order level, will have to be determined from Order's billing account.
     * @param billingAccount Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each rated transaction.
     * @param defaultPaymentMethod Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each billing
     *        account occurrence.
     * @param defaultInvoiceType Invoice type. A default invoice type for postpaid rated transactions. In case of prepaid RTs, a prepaid invoice type is used. Provided in case of Billing account or Subscription billable
     *        entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @param balance Balance due. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @return A list of invoices
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("unchecked")
    private List<Invoice> createAggregatesAndInvoiceFromRTs(IBillableEntity entityToInvoice, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            boolean isDraft, BillingCycle defaultBillingCycle, BillingAccount billingAccount, PaymentMethod defaultPaymentMethod, InvoiceType defaultInvoiceType, BigDecimal balance, boolean automaticInvoiceCheck)
            throws BusinessException {

        List<Invoice> invoiceList = new ArrayList<>();
        boolean moreRatedTransactionsExpected = true;
        // Contains distinct Invoice information - one for each invoice produced. Map key is billingAccount.id_seller.id_invoiceType.id_isPrepaid
        Map<String, InvoiceAggregateProcessingInfo> rtGroupToInvoiceMap = new HashMap<>();

        boolean allRTsInOneRun = true;

        EntityManager em = getEntityManager();

        while (moreRatedTransactionsExpected) {

            if (entityToInvoice instanceof Order) {
                billingAccount = null;
                defaultInvoiceType = null;
            }

            // Retrieve Rated transactions and split them into BA/seller combinations
            RatedTransactionsToInvoice rtsToInvoice = getRatedTransactionGroups(entityToInvoice, billingAccount, billingRun, defaultBillingCycle, defaultInvoiceType, ratedTransactionFilter, firstTransactionDate,
                lastTransactionDate, isDraft, defaultPaymentMethod, invoiceDate);

            List<RatedTransactionGroup> ratedTransactionGroupsPaged = rtsToInvoice.ratedTransactionGroups;
            moreRatedTransactionsExpected = rtsToInvoice.moreRatedTransactions;
            if (moreRatedTransactionsExpected) {
                allRTsInOneRun = false;
            }

            if (rtGroupToInvoiceMap.isEmpty() && ratedTransactionGroupsPaged.isEmpty()) {
                log.warn("Account {}/{} has no billable transactions", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());
                return new ArrayList<>();

            } else if (!ratedTransactionGroupsPaged.isEmpty()) {

                // Process each BA/seller/invoiceType combination separately, what corresponds to a separate invoice
                for (RatedTransactionGroup rtGroup : ratedTransactionGroupsPaged) {

                    // For order calculate for each BA
                    if ((entityToInvoice instanceof Order) && (billingAccount == null || !billingAccount.getId().equals(rtGroup.getBillingAccount().getId()))) {
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

                    String invoiceKey = rtGroup.getInvoiceKey();

                    InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo = rtGroupToInvoiceMap.get(invoiceKey);
                    if (invoiceAggregateProcessingInfo == null) {
                        invoiceAggregateProcessingInfo = new InvoiceAggregateProcessingInfo();
                        rtGroupToInvoiceMap.put(invoiceKey, invoiceAggregateProcessingInfo);
                    }

                    if (invoiceAggregateProcessingInfo.invoice == null) {
                        invoiceAggregateProcessingInfo.invoice = instantiateInvoice(entityToInvoice, rtGroup.getBillingAccount(), rtGroup.getSeller(), billingRun, invoiceDate, isDraft, rtGroup.getBillingCycle(),
                            rtGroup.getPaymentMethod(), rtGroup.getInvoiceType(), rtGroup.isPrepaid(), balance, automaticInvoiceCheck);
                        invoiceList.add(invoiceAggregateProcessingInfo.invoice);
                    }

                    Invoice invoice = invoiceAggregateProcessingInfo.invoice;

                    // Create aggregates.
                    // Indicate that no more RTs to process only in case when all RTs were retrieved for processing in a single query page.
                    // In other case - need to close invoices when all RTs are processed
                    appendInvoiceAgregates(entityToInvoice, rtGroup.getBillingAccount(), invoice, rtGroup.getRatedTransactions(), false, invoiceAggregateProcessingInfo, !allRTsInOneRun);

                    // Collect information needed to update RTs with invoice information

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

                    Date now = new Date();

                    // Mass update RT status

                    Session hibernateSession = em.unwrap(Session.class);
                    hibernateSession.doWork(connection -> {
                        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into  billing_rated_transaction_pending (id, aggregate_id_f, invoice_id, billing_run_id) values (?,?,?,?)")) {

//                            int i = 0;
                            for (Object[] aggregateAndRtIds : rtMassUpdates) {
                                SubCategoryInvoiceAgregate subCategoryAggregate = (SubCategoryInvoiceAgregate) aggregateAndRtIds[0];
                                List<Long> rtIds = (List<Long>) aggregateAndRtIds[1];
                                for (Long rtId : rtIds) {
                                    preparedStatement.setLong(1, rtId);
                                    preparedStatement.setLong(2, subCategoryAggregate.getId());
                                    preparedStatement.setLong(3, invoice.getId());
                                    if(billingRun != null) {
                                        preparedStatement.setLong(4, billingRun.getId());
                                    } else {
                                        preparedStatement.setObject(4, null);
                                    }
                                    preparedStatement.addBatch();

//                                    if (i > 0 && i % 500 == 0) {
//                                        preparedStatement.executeBatch();
//                                    }
//                                    i++;
                                }
                            }

                            preparedStatement.executeBatch();

                        } catch (SQLException e) {
                            log.error("Failed to insert into billing_rated_transaction_pending", e);
                            throw e;
                        }
                    });

                    em.flush(); // Need to flush, so RTs can be updated in mass

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
                }
            }

            // Mass update RTs with status and invoice info
            em.createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoFromPendingTable").executeUpdate();
            em.createNamedQuery("RatedTransaction.deletePendingTable").executeUpdate();
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
            applyAutomaticInvoiceCheck(invoiceAggregateProcessingInfo.invoice);
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
     * @param invoiceList
     */
    private void applyAutomaticInvoiceCheck(List<Invoice> invoiceList, boolean automaticInvoiceCheck) {
        if (automaticInvoiceCheck) {
            for (Invoice invoice : invoiceList) {
                applyAutomaticInvoiceCheck(invoice);
            }
        }
    }

    /**
     * @param invoice
     */
    private void applyAutomaticInvoiceCheck(Invoice invoice) {
        InvoiceType invoiceType = invoice.getInvoiceType();
        invoiceType = invoiceTypeService.retrieveIfNotManaged(invoiceType);
        if (invoiceType != null && invoiceType.getInvoiceValidationScript() != null) {
            ScriptInstance scriptInstance = invoiceType.getInvoiceValidationScript();
            if (scriptInstance != null) {
                ScriptInterface script = scriptInstanceService.getScriptInstance(scriptInstance.getCode());
                if (script != null) {
                    Map<String, Object> methodContext = new HashMap<String, Object>();
                    methodContext.put(Script.CONTEXT_ENTITY, invoice);
                    methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                    methodContext.put("billingRun", invoice.getBillingRun());
                    script.execute(methodContext);
                    Object status = methodContext.get(Script.INVOICE_VALIDATION_STATUS);
                    if (status instanceof InvoiceValidationStatusEnum) {
                        if (InvoiceValidationStatusEnum.REJECTED.equals((InvoiceValidationStatusEnum) status)) {
                            invoice.rebuildStatus(InvoiceStatusEnum.REJECTED);
                            invoice.setRejectReason((String) methodContext.get(Script.INVOICE_VALIDATION_REASON));
                        } else if (InvoiceValidationStatusEnum.SUSPECT.equals((InvoiceValidationStatusEnum) status)) {
                            invoice.rebuildStatus(InvoiceStatusEnum.SUSPECT);
                            invoice.setRejectReason((String) methodContext.get(Script.INVOICE_VALIDATION_REASON));
                        }
                    } else {
                        invoice.rebuildStatus(InvoiceStatusEnum.DRAFT);
                    }
                }
            }
        }
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
            return Collections.emptyList();
        }
    }

    /**
     * Produce invoice pdf in new transaction.
     *
     * @param invoiceId id of invoice
     * @param draftWalletOperationsId Wallet operations (ids) to include in a draft invoice
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceInvoicePdfInNewTransaction(Long invoiceId, List<Long> draftWalletOperationsId) throws BusinessException {
        Invoice invoice = findById(invoiceId);
        produceInvoicePdf(invoice, draftWalletOperationsId);
    }

    /**
     * Produce invoice's PDF file and update invoice record in DB.
     *
     *
     * @param invoice Invoice
     * @param draftWalletOperationsId Wallet operations (ids) to include in a draft invoice
     * @return Update invoice entity
     * @throws BusinessException business exception
     */
    public Invoice produceInvoicePdf(Invoice invoice, List<Long> draftWalletOperationsId) throws BusinessException {

        if (draftWalletOperationsId != null) {
            invoice.setDraftRatedTransactions(getDraftRatedTransactions(draftWalletOperationsId));
        }
        produceInvoicePdfNoUpdate(invoice);
        invoice.setPdfDate(new Date());

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

        boolean isInvoiceAdjustment = invoiceTypeService.getListAdjustementCode().contains(invoice.getInvoiceType().getCode());

        File invoiceXmlFile = new File(invoiceXmlFileName);
        if (!invoiceXmlFile.exists()) {
            produceInvoiceXmlNoUpdate(invoice, true);
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

                log.warn("PDF jasper report {} was not found. A default report will be used.", destDir.getAbsolutePath());

                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + File.separator + billingTemplateName + File.separator + "invoice";

                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    VirtualFile vfDir = VFS
                        .getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/" + billingTemplateName + File.separator + "invoice");
                    log.info("default jaspers path : {}", vfDir.getPathName());
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFile = new File(vfPath.getPath());
                    if (!sourceFile.exists()) {
                        throw new BusinessException("A default embedded jasper PDF report " + sourceFile.getAbsolutePath() + "for invoice is missing..");
                    }
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
    public Invoice deleteInvoicePdf(Invoice invoice) throws BusinessException, IOException {

        String pdfFilename = getFullPdfFilePath(invoice, false);

        invoice.setPdfFilename(null);
        invoice = update(invoice);

        Files.delete(Path.of(pdfFilename));
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
     * @param entity invoice
     * @return evaluated value
     * @throws BusinessException business exception
     */
    public static <T> String evaluatePrefixElExpression(String prefix, T entity) throws BusinessException {

        if (StringUtils.isBlank(prefix)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (prefix.indexOf("entity") >= 0) {
            userMap.put("entity", entity);
        }
        if (prefix.indexOf("invoice") >= 0) {
            userMap.put("invoice", entity);
        }
        if (prefix.indexOf("commercial") >= 0 || prefix.indexOf("commercialOrder") >= 0) {
            userMap.put("commercialOrder", entity);
        }
        if (prefix.indexOf("quote") >= 0) {
            userMap.put("quote", entity);
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
     * Return a XML filename that was assigned to invoice, or in case it was not assigned yet - generate a filename. A default XML filename is invoiceDateOrBillingRunId/invoiceNumber.pdf or
     * invoiceDateOrBillingRunId/_IA_invoiceNumber.pdf for adjustment invoice
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

            boolean isInvoiceAdjustment = invoiceTypeService.getListAdjustementCode().contains(invoice.getInvoiceType().getCode());

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
     * Return a pdf filename that was assigned to invoice, or in case it was not assigned yet - generate a filename. A default PDF filename is invoiceDate_invoiceNumber.pdf or invoiceDate_IA_invoiceNumber.pdf for
     * adjustment invoice
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

            boolean isInvoiceAdjustment = invoiceTypeService.getListAdjustementCode().contains(invoice.getInvoiceType().getCode());

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
        produceInvoiceXml(invoice, draftWalletOperationsId, true);
    }

    /**
     * Produce invoice's XML file and update invoice record in DB.
     *
     * @param invoice Invoice to produce XML for
     * @param draftWalletOperationsId Wallet operations (ids) to include in a draft invoice
     * @return Update invoice entity
     * @throws BusinessException business exception
     */
    public Invoice produceInvoiceXml(Invoice invoice, List<Long> draftWalletOperationsId, boolean rtBillingProcess) throws BusinessException {

        if (draftWalletOperationsId != null) {
            invoice.setDraftRatedTransactions(getDraftRatedTransactions(draftWalletOperationsId));
        }

        produceInvoiceXmlNoUpdate(invoice, rtBillingProcess);
        invoice.setXmlDate(new Date());
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
    public void produceInvoiceXmlNoUpdate(Invoice invoice, boolean rtBillingProcess) throws BusinessException {

        xmlInvoiceCreator.createXMLInvoice(invoice, false, rtBillingProcess);
        xmlGeneratedEventProducer.fire(invoice);
    }

    /**
     * Delete invoice's XML file.
     *
     * @param invoice Invoice
     * @return True if file was deleted
     * @throws BusinessException business exception
     */
    public Invoice deleteInvoiceXml(Invoice invoice) throws BusinessException, IOException {

        String xmlFilename = getFullXmlFilePath(invoice, false);

        invoice.setXmlFilename(null);
        invoice = update(invoice);

        Files.delete(Path.of(xmlFilename));
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
        if (InvoiceStatusEnum.CANCELED.equals(invoice.getStatus())) {
            throw new BusinessException("Cannot generate XML/PDF for a canceled inovice");
        }
        if (invoice.isPrepaid()) {
            return invoice;
        }

        if (regenerate || invoice.getXmlFilename() == null || !isInvoiceXmlExist(invoice)) {
            produceInvoiceXmlNoUpdate(invoice, true);
        }
        invoice = produceInvoicePdf(invoice, null);
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
        return Collections.emptyList();
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
    public List<Invoice> generateInvoice(IBillableEntity entityToInvoice, GenerateInvoiceRequestDto generateInvoiceRequestDto, Filter ratedTxFilter, boolean isDraft, CustomFieldValues customFieldValues,
            boolean useV11Process) throws BusinessException {
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
        if (!useV11Process)
            ratedTransactionService.createRatedTransactions(entityToInvoice, lastTransactionDate);
        List<Invoice> invoices = invoiceService.createInvoice(entityToInvoice, generateInvoiceRequestDto, ratedTxFilter, isDraft, useV11Process);

        List<Invoice> invoicesWNumber = new ArrayList<>();
        for (Invoice invoice : invoices) {
            if (customFieldValues != null) {
                invoice.setCfValues(customFieldValues);
            }
            try {
                if (invoice.getStatus() != InvoiceStatusEnum.REJECTED && invoice.getStatus() != InvoiceStatusEnum.SUSPECT) {
                    invoicesWNumber.add(serviceSingleton.assignInvoiceNumber(invoice));
                }
            } catch (Exception e) {
                log.error("Failed to assign invoice number for invoice {}/{}", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber(), e);
                continue;
            }
            try {
                List<Long> drafWalletOperationIds;
                if (isDraft)
                    drafWalletOperationIds = getDrafWalletOperationIds(entityToInvoice, generateInvoiceRequestDto.getFirstTransactionDate(), generateInvoiceRequestDto.getLastTransactionDate(),
                        generateInvoiceRequestDto.getLastTransactionDate());
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
     * Generate invoice for the billingAccount. Asumes tha all Rated transactions are created already. DOES NOT assign an invoice number NOR create XML/PDF files nor account operation. Use generateInvoice() instead.
     *
     * @param entity Entity to invoice
     * @param generateInvoiceRequestDto Generate invoice request
     * @param filter A filter to select rated transactions
     * @param isDraft Is it a draft invoice
     * @return A list of invoices
     * @throws BusinessException General business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Invoice> createInvoice(IBillableEntity entity, GenerateInvoiceRequestDto generateInvoiceRequestDto, Filter filter, boolean isDraft, boolean useV11Process) throws BusinessException {

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

        // Create missing rated transactions up to a last transaction date
        List<Invoice> invoices = Collections.emptyList();
        if (useV11Process) {
            MinAmountForAccounts minAmountForAccounts = invoiceLinesService.isMinAmountForAccountsActivated(entity, applyMinimumModeEnum);
            // Create invoice lines from grouped and filtered RT
            List<RatedTransaction> ratedTransactions = getRatedTransactions(entity, filter, firstTransactionDate, lastTransactionDate, new Date(), isDraft);
            List<Long> ratedTransactionIds = ratedTransactions.stream()
                    .map(RatedTransaction::getId)
                    .collect(toList());
            if (!ratedTransactionIds.isEmpty()) {
                List<Map<String, Object>> groupedRTs = ratedTransactionService.getGroupedRTs(ratedTransactionIds);
                AggregationConfiguration configuration = new AggregationConfiguration(appProvider.isEntreprise());
                invoiceLinesService.createInvoiceLines(groupedRTs, configuration, null);
                ratedTransactionService.makeAsProcessed(ratedTransactionIds);
                invoices = createAggregatesAndInvoiceWithIL(entity, null, filter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, !generateInvoiceRequestDto.getSkipValidation(), false);
            }
        } else {
            MinAmountForAccounts minAmountForAccounts = ratedTransactionService.isMinAmountForAccountsActivated(entity, applyMinimumModeEnum);
            invoices = createAgregatesAndInvoice(entity, null, filter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, !generateInvoiceRequestDto.getSkipValidation());
        }
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
        update(invoice);
    }

    /**
     * Cancel invoice and delete it.
     *
     * @param invoice invoice to cancel
     * @throws BusinessException business exception
     */
    public void cancelInvoice(Invoice invoice) throws BusinessException {
        cancelInvoice(invoice, true);
    }

    /**
     * Cancel invoice without delete.
     *
     * @param invoice invoice to cancel
     * @throws BusinessException business exception
     */
    public void cancelInvoiceWithoutDelete(Invoice invoice) throws BusinessException {
        cancelInvoice(invoice, false);
    }

    public void cancelInvoice(Invoice invoice, boolean remove) {
        cancelInvoiceAndRts(invoice);
        if (remove) {
            super.remove(invoice);
        } else {
            super.update(invoice);
        }
        log.debug("Invoice canceled {}", invoice.getTemporaryInvoiceNumber());
    }

    public void cancelInvoiceAndRts(Invoice invoice) {
        if (invoice.getRecordedInvoice() != null) {
            throw new BusinessException("Can't cancel an invoice that present in AR");
        }
        ratedTransactionService.deleteSupplementalRTs(invoice);
        ratedTransactionService.uninvoiceRTs(invoice);
        invoice.setStatus(InvoiceStatusEnum.CANCELED);
    }

    public void validateInvoice(Invoice invoice, boolean save) {
        if (InvoiceStatusEnum.REJECTED.equals(invoice.getStatus()) || InvoiceStatusEnum.SUSPECT.equals(invoice.getStatus())) {
            invoice.setStatus(InvoiceStatusEnum.DRAFT);
            if (save) {
                update(invoice);
            }
        }
    }

    /**
     * @param billingRunId
     * @param invoices
     */
    public void rebuildInvoices(Long billingRunId, List<Long> invoiceIds) throws BusinessException {
        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.REJECTED, InvoiceStatusEnum.SUSPECT), Arrays.asList(InvoiceStatusEnum.DRAFT));
        for (Invoice invoice : invoices) {
            rebuildInvoice(invoice, true);
        }
    }

    /**
     * @param billingRunId
     * @param invoices
     */
    public void rejectInvoices(Long billingRunId, List<Long> invoiceIds) {
        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.SUSPECT, InvoiceStatusEnum.DRAFT));
        for (Invoice invoice : invoices) {
            rejectInvoice(invoice);
        }
    }

    public void rejectInvoice(Invoice invoice) {
        InvoiceStatusEnum status = invoice.getStatus();
        if (!(InvoiceStatusEnum.SUSPECT.equals(status) || InvoiceStatusEnum.DRAFT.equals(status))) {
            throw new BusinessException("Can only reject invoices in statuses DRAFT/SUSPECT. current invoice status is :" + status.name());
        }
        invoice.setStatus(InvoiceStatusEnum.REJECTED);
    }

    /**
     * @param billingRunId
     * @param invoiceIds
     */
    public void validateInvoices(Long billingRunId, List<Long> invoiceIds) {
        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.REJECTED, InvoiceStatusEnum.SUSPECT));
        for (Invoice invoice : invoices) {
            validateInvoice(invoice, true);
        }
    }

    /**
     * @param billingRunId
     */
    public void deleteInvoices(Long billingRunId) {
        BillingRun br = getBrById(billingRunId);
        deleteInvoicesByStatus(br, Arrays.asList(InvoiceStatusEnum.CANCELED));
    }

    /**
     * @param billingRunId
     * @param invoices
     */
    public void cancelInvoices(Long billingRunId, List<Long> invoiceIds, Boolean deleteCanceledInvoices) {

        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.REJECTED));
        invoices.stream().forEach(invoice -> cancelInvoiceWithoutDelete(invoice));
        if (deleteCanceledInvoices) {
            deleteInvoices(billingRunId);
        }
    }

    /**
     * @param billingRunId
     * @param invoiceIds
     * @return billingRunId the id of the new billing run.
     */
    public Long moveInvoices(Long billingRunId, List<Long> invoiceIds) {
        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.REJECTED, InvoiceStatusEnum.SUSPECT));
        BillingRun nextBR = billingRunService.findOrCreateNextBR(billingRunId);
        if (CollectionUtils.isEmpty(invoiceIds) && !CollectionUtils.isEmpty(invoices)) {
            invoiceIds = invoices.stream().map(invoice -> invoice.getId()).collect(Collectors.toList());
        }
        getEntityManager().createNamedQuery("Invoice.moveToBRByIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
        return nextBR.getId();
    }

    /**
     * @param id
     * @param invoices
     * @return billingRunId the id of the new billing run.
     */
    public Long moveInvoices(List<Invoice> invoices, Long billingRunId) {
        return moveInvoices(billingRunId, invoices.stream().map(x -> x.getId()).collect(Collectors.toList()));
    }

    private List<Invoice> extractInvalidInvoiceList(Long billingRunId, List<Long> invoiceIds, List<InvoiceStatusEnum> statusList) throws BusinessException {
        return extractInvalidInvoiceList(billingRunId, invoiceIds, statusList, new ArrayList<InvoiceStatusEnum>());
    }

    private List<Invoice> extractInvalidInvoiceList(Long billingRunId, List<Long> invoiceIds, List<InvoiceStatusEnum> statusList, List<InvoiceStatusEnum> aditionalStatus) throws BusinessException {
        BillingRun br = null;
        List<Invoice> invoices = new ArrayList<Invoice>();
        if (billingRunId != null) {
            br = getBrById(billingRunId);
            final BillingRunStatusEnum brStatus = br.getStatus();
            if (brStatus != BillingRunStatusEnum.REJECTED && brStatus != BillingRunStatusEnum.POSTINVOICED) {
                throw new ActionForbiddenException("not possible to change invoice status because of billing run status:" + brStatus);
            }
        }
        if (CollectionUtils.isEmpty(invoiceIds)) {
            return br != null ? findInvoicesByStatusAndBR(billingRunId, statusList) : new ArrayList<Invoice>();
        }
        for (Long invoiceId : invoiceIds) {
            Invoice invoice = findById(invoiceId);
            if (invoice == null) {
                throw new ActionForbiddenException("Invoice with ID " + invoiceId + " does not exist ");
            } else if (br != null && invoice.getBillingRun() != br) {
                throw new ActionForbiddenException("Invoice with ID " + invoiceId + " is not associated to Billing Run with ID " + billingRunId);
            } else if (!statusList.contains(invoice.getStatus()) && !aditionalStatus.contains(invoice.getStatus())) {
                throw new ActionForbiddenException("Action forbidden for invoice with ID " + invoiceId + ": invoice status is " + invoice.getStatus());
            } else {
                invoices.add(invoice);
            }
        }
        return invoices;
    }

    /**
     * @param billingRunId
     * @param statusList
     * @return
     */
    private List<Invoice> findInvoicesByStatusAndBR(Long billingRunId, List<InvoiceStatusEnum> statusList) {
        return getEntityManager().createNamedQuery("Invoice.findByStatusAndBR", Invoice.class).setParameter("billingRunId", billingRunId).setParameter("statusList", statusList).getResultList();
    }

    private BillingRun getBrById(Long billingRunId) throws BusinessException {
        BillingRun br = billingRunService.findById(billingRunId);
        if (br == null) {
            throw new EntityDoesNotExistsException(BillingRun.class, billingRunId);
        }
        return br;
    }

    public void rebuildInvoice(Invoice invoice, boolean save) {
        applyAutomaticInvoiceCheck(Arrays.asList(invoice), true);
        if (save) {
            update(invoice);
        }
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
        Map<Object, Object> userMap = new HashMap<>();
        if (expression.contains(ValueExpressionWrapper.VAR_BILLING_ACCOUNT)) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.contains(ValueExpressionWrapper.VAR_INVOICE)) {
            userMap.put("invoice", invoice);
        }
        if (expression.contains("order")) {
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
        return determineInvoiceType(isPrepaid, isDraft, false, billingCycle, billingRun, billingAccount);
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
    private InvoiceType determineInvoiceType(boolean isPrepaid, boolean isDraft, boolean isDepositInvoice, BillingCycle billingCycle, BillingRun billingRun, BillingAccount billingAccount) throws BusinessException {
        InvoiceType invoiceType = null;

        if (billingRun != null && billingRun.getInvoiceType() != null) {
            return billingRun.getInvoiceType();
        }

        if (isPrepaid) {
            invoiceType = invoiceTypeService.getDefaultPrepaid();

        } else if (isDraft) {
            invoiceType = invoiceTypeService.getDefaultDraft();

        } else if (isDepositInvoice) {
            invoiceType = invoiceTypeService.getDefaultDeposit();

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

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Invoice updateStatus(Long invoiceId, InvoiceStatusEnum status) {
        Invoice invoice = findById(invoiceId);
        invoice.setStatus(status);
        return update(invoice);
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
        BillingAccount billingAccount = invoice.getBillingAccount();
        BillingCycle billingCycle = billingAccount.getBillingCycle();
        BillingRun billingRun = invoice.getBillingRun();
        if (billingRun != null && billingRun.getBillingCycle() != null) {
            billingCycle = billingRun.getBillingCycle();
        }
        billingCycle = PersistenceUtils.initializeAndUnproxy(billingCycle);
        if (billingRun == null) {
            return;
        }
        if (billingRun.getComputeDatesAtValidation() != null && !billingRun.getComputeDatesAtValidation()) {
            return;
        } else if (billingRun.getComputeDatesAtValidation() == null && !billingCycle.getComputeDatesAtValidation()) {
            return;
        }
        if (billingRun.getComputeDatesAtValidation() != null && billingRun.getComputeDatesAtValidation()) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
            update(invoice);
        } else if (billingRun.getComputeDatesAtValidation() == null && billingCycle.getComputeDatesAtValidation()) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
            update(invoice);
        }
    }

    private void recalculateDate(Invoice invoice, BillingRun billingRun, BillingAccount billingAccount, BillingCycle billingCycle) {

        int delay = billingCycle.getInvoiceDateDelayEL() == null ? 0 : InvoiceService.resolveImmediateInvoiceDateDelay(billingCycle.getInvoiceDateDelayEL(), invoice, billingAccount);
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
     * @param billingAccountId
     *
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementBAInvoiceDate(BillingRun billingRun, Long billingAccountId) throws BusinessException {
        if (!billingRun.isExceptionalBR()) {
            BillingAccount billingAccount = billingAccountService.findById(billingAccountId);
            incrementBAInvoiceDate(billingRun, billingAccount);
        }
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
            return Collections.emptyList();
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
    public List<Long> listUnpaidInvoicesIds() {
        return getEntityManager()
                .createNamedQuery("Invoice.listUnpaidInvoicesIds", Long.class)
                .getResultList();
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
                log.warn("No Pdf file exists for the invoice {}", invoice.getInvoiceNumber());
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
            if (!StringUtils.isBlank(billingAccount.getCcedEmails())) {
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
                if (!StringUtils.isBlank(subscription.getCcedEmails())) {
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
                if (!StringUtils.isBlank(order.getCcedEmails())) {
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
                entityUpdatedEventProducer.fire(invoice);
                invoice.setEmailSentDate(new Date());
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
     * @param billingCycleCodes Billing cycles. Optional.
     * @param invoiceDateRangeFrom Invoice date range - start. Optional.
     * @param invoiceDateRangeTo Invoice date range - end. Optional.
     * @param includeDraft True to include draft invoices
     * @return A list of invoices matching billing cycles and invoice dates
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findByNotAlreadySentAndDontSend(List<String> billingCycleCodes, Date invoiceDateRangeFrom, Date invoiceDateRangeTo, boolean includeDraft) throws BusinessException {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
        qb.addCriterionEntity("alreadySent", false);
        qb.addCriterionEntity("dontSend", false);
        if (billingCycleCodes != null) {
            qb.addCriterionEntityInList("billingRun.code", billingCycleCodes);
        }
        if (invoiceDateRangeFrom != null) {
            qb.addCriterionDateRangeFromTruncatedToDay("invoiceDate", invoiceDateRangeFrom);
        }
        if (invoiceDateRangeTo != null) {
            qb.addCriterionDateRangeToTruncatedToDay("invoiceDate", invoiceDateRangeTo, false, false);
        }
        if (!includeDraft) {
            qb.addSql("invoiceNumber IS NOT NULL");
            InvoiceType draftInvoiceType = invoiceTypeService.getDefaultDraft();
            if (draftInvoiceType != null) {
                qb.addCriterionEntity("invoiceType", draftInvoiceType, "<>", false);
            }
        }

        return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
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
     * @param invoiceAggregateProcessingInfo RT to invoice aggregation information when invoice is created with paged RT retrieval. NOTE: should pass NULL in non-paginated invoicing cases
     * @param moreRatedTransactionsExpected Indicates that there are more RTs to be retrieved and aggregated in invoice before invoice can be closed. NOTE: should pass FALSE in non-paginated invoicing cases
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
        // boolean calculateExternalTax = "YES".equalsIgnoreCase((String) appProvider.getCfValue("OPENCELL_ENABLE_TAX_CALCULATION"));

        // Tax change mapping. Key is ba.id_taxClass.id and value is an array of [Tax to apply, True/false if tax has changed]
        Map<String, Object[]> taxChangeMap = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.taxChangeMap : new HashMap<>();

        // Subcategory aggregates mapping. Key is ua.id_walletInstance.id_invoiceSubCategory.id_tax.id
        Map<String, SubCategoryInvoiceAgregate> subCategoryAggregates = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.subCategoryAggregates : new LinkedHashMap<>();

        Set<String> orderNumbers = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.orderNumbers : new HashSet<String>();

        String scaKey = null;

        if (log.isTraceEnabled()) {
            log.trace("ratedTransactions.totalAmountWithoutTax={}", ratedTransactions != null ? ratedTransactions.stream().mapToDouble(e -> e.getAmountWithoutTax().doubleValue()).sum() : "0");
        }

        boolean linkInvoiceToOrders = paramBeanFactory.getInstance().getPropertyAsBoolean("order.linkInvoiceToOrders", true);

        boolean taxWasRecalculated = false;
        for (RatedTransaction ratedTransaction : ratedTransactions) {

            InvoiceSubCategory invoiceSubCategory = ratedTransaction.getInvoiceSubCategory() != null ? ratedTransaction.getInvoiceSubCategory() : null;

            scaKey = invoiceSubCategory != null ? invoiceSubCategory.getId().toString() : "";
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
                    Object[] applicableTax = taxMappingService.getApplicableTax(tax, isExonerated, invoice.getSeller(), invoice.getBillingAccount(), invoice.getInvoiceDate(), taxClass, ratedTransaction.getUserAccount(), taxZero);

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
                    invoiceSubCategory != null ? invoiceSubCategory.getAccountingCode() : null);
                scAggregate.updateAudit(currentUser);

                String translationSCKey = "SC_" + (invoiceSubCategory != null ? invoiceSubCategory.getId() : "") + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationSCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory != null ? invoiceSubCategory.getDescriptionOrCode() : "";
                    if (invoiceSubCategory != null && (invoiceSubCategory.getDescriptionI18n() != null) && (invoiceSubCategory.getDescriptionI18n().get(languageCode) != null)) {
                        descTranslated = invoiceSubCategory.getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationSCKey, descTranslated);
                }
                scAggregate.setDescription(descTranslated);

                subCategoryAggregates.put(scaKey, scAggregate);
                invoice.addInvoiceAggregate(scAggregate);
            }

            if (!(entityToInvoice instanceof Order) && linkInvoiceToOrders && ratedTransaction.getOrderNumber() != null) {
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

        if (subscription != null && subscription.getDiscountPlanInstances() != null && !subscription.getDiscountPlanInstances().isEmpty()) {
            subscriptionApplicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, subscription.getDiscountPlanInstances(), invoice, customerAccount));
        }

        // Calculate derived aggregate amounts for subcategory aggregate, create category aggregates, discount aggregates and tax aggregates
        BigDecimal[] amounts = null;
        Map<String, CategoryInvoiceAgregate> categoryAggregates = new HashMap<>();
        List<SubCategoryInvoiceAgregate> discountAggregates = new ArrayList<>();
        Map<String, TaxInvoiceAgregate> taxAggregates = new HashMap<>();

        // Create category aggregates
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates) {
            List<InvoiceLine> erronedLines = scAggregate.getInvoiceLinesToAssociate().stream().filter(x -> x.getTax() == null).collect(Collectors.toList());
            if (!erronedLines.isEmpty()) {
                String message = erronedLines.stream().map(x -> x.getAccountingArticle().getCode()).collect(Collectors.joining(", "));
                throw new BusinessException("the articles " + message + " has no corresponding tax ");
            }
            // Calculate derived amounts
            scAggregate.computeDerivedAmounts(isEnterprise, rounding, roundingMode.getRoundingMode(), invoiceRounding, invoiceRoundingMode.getRoundingMode());

            InvoiceSubCategory invoiceSubCategory = scAggregate.getInvoiceSubCategory();

            // Create category aggregates or update their amounts
            String caKey = (scAggregate.getUserAccount() != null ? scAggregate.getUserAccount().getId() : "") + "_" + (invoiceSubCategory != null ? invoiceSubCategory.getInvoiceCategory().getId().toString() : "");

            CategoryInvoiceAgregate cAggregate = categoryAggregates.get(caKey);
            if (cAggregate == null) {
                cAggregate = new CategoryInvoiceAgregate(invoiceSubCategory != null ? invoiceSubCategory.getInvoiceCategory() : null, billingAccount, scAggregate.getUserAccount(), invoice);
                categoryAggregates.put(caKey, cAggregate);

                cAggregate.updateAudit(currentUser);

                String translationCKey = "C_" + (invoiceSubCategory != null ? invoiceSubCategory.getInvoiceCategory().getId() : "") + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory != null ? invoiceSubCategory.getInvoiceCategory().getDescriptionOrCode() : "";
                    if (invoiceSubCategory != null && (invoiceSubCategory.getInvoiceCategory().getDescriptionI18n() != null) && (invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode) != null)) {
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

        if (billingAccount.getDiscountPlanInstances() != null && !billingAccount.getDiscountPlanInstances().isEmpty()) {
            billingAccountApplicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, billingAccount.getDiscountPlanInstances(), invoice, customerAccount));
        }

        // Construct discount and tax aggregates
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates) {

            if (BigDecimal.ZERO.equals(isEnterprise ? scAggregate.getAmountWithoutTax() : scAggregate.getAmountWithTax())) {
                continue;
            }

            Map<Tax, SubcategoryInvoiceAgregateAmount> amountCumulativeForTax = new LinkedHashMap<>();
            scAggregate.getAmountsByTax().entrySet().stream().forEach(amountInfo -> amountCumulativeForTax.put(amountInfo.getKey(), amountInfo.getValue().clone()));

            CategoryInvoiceAgregate cAggregate = scAggregate.getCategoryInvoiceAgregate();

            Map<Tax, BigDecimal> amountAsDiscountBase = new LinkedHashMap<>();
            scAggregate.getAmountsByTax().entrySet().stream().forEach(amountInfo -> amountAsDiscountBase.put(amountInfo.getKey(), amountInfo.getValue().getAmount(!isEnterprise)));

            // Add discount aggregates defined on subscription level - ONLY when invoicing by subscription
            for (DiscountPlanItem discountPlanItem : subscriptionApplicableDiscountPlanItems) {
                SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, rounding, roundingMode, invoiceRounding, invoiceRoundingMode, scAggregate, amountAsDiscountBase,
                    cAggregate, discountPlanItem);
                if (discountAggregate != null) {
                    addAmountsToMap(amountCumulativeForTax, discountAggregate.getAmountsByTax());
                    discountAggregates.add(discountAggregate);
                }
            }

            for (DiscountPlanItem discountPlanItem : billingAccountApplicableDiscountPlanItems) {
                SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, rounding, roundingMode, invoiceRounding, invoiceRoundingMode, scAggregate, amountAsDiscountBase,
                    cAggregate, discountPlanItem);
                if (discountAggregate != null) {
                    addAmountsToMap(amountCumulativeForTax, discountAggregate.getAmountsByTax());
                    discountAggregates.add(discountAggregate);
                }
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
        if (!isExonerated && !calculateTaxOnSubCategoryLevel && (invoice.getInvoiceType() != null) && (invoice.getInvoiceType().getTaxScript() != null)) {
            taxAggregates = taxScriptService.createTaxAggregates(invoice.getInvoiceType().getTaxScript().getCode(), invoice);
            if (taxAggregates != null) {
                for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {
                    taxAggregate.setInvoice(invoice);
                    invoice.addInvoiceAggregate(taxAggregate);
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
            invoice.setHasTaxes(true);
            if (!discountAggregates.isEmpty()) {
                invoice.setHasDiscounts(true);
            }
            for (SubCategoryInvoiceAgregate discountAggregate : discountAggregates) {
                invoice.addAmountWithoutTax(discountAggregate.getAmountWithoutTax());
                invoice.addAmountWithTax(discountAggregate.getAmountWithTax());
                invoice.addAmountTax(isExonerated ? BigDecimal.ZERO : discountAggregate.getAmountTax());
            }
        }

        // If invoice is prepaid, skip threshold test
        /*
         * if (!invoice.isPrepaid()) { BigDecimal invoicingThreshold = billingAccount.getInvoicingThreshold() == null ? billingAccount.getBillingCycle().getInvoicingThreshold() : billingAccount.getInvoicingThreshold();
         * if ((invoicingThreshold != null) && (invoicingThreshold.compareTo(isEnterprise ? invoice.getAmountWithoutTax() : invoice.getAmountWithTax()) > 0)) { throw new
         * BusinessException("Invoice amount below the threshold"); } }
         */

        // Update net to pay amount
        final BigDecimal amountWithTax = invoice.getAmountWithTax() != null ? invoice.getAmountWithTax() : BigDecimal.ZERO;
        invoice.setNetToPay(amountWithTax.add(invoice.getDueBalance() != null ? invoice.getDueBalance() : BigDecimal.ZERO));
    }

    private List<DiscountPlanInstance> fromBillingAccount(BillingAccount billingAccount) {
        return billingAccount.getUsersAccounts().stream().map(userAccount -> userAccount.getSubscriptions()).map(this::addSubscriptionDiscountPlan).flatMap(Collection::stream).collect(toList());
    }

    private List<DiscountPlanInstance> addSubscriptionDiscountPlan(List<Subscription> subscriptions) {
        return subscriptions.stream().map(Subscription::getDiscountPlanInstances).flatMap(Collection::stream).collect(toList());
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
            if (allowToNegate(discountAmount, amountToApplyDiscountOn, discountPlanItem)) {

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

    private boolean allowToNegate(BigDecimal discountAmount, BigDecimal amountToApplyDiscountOn, DiscountPlanItem discountPlanItem) {
        if (!((discountAmount.compareTo(BigDecimal.ZERO) < 0 && amountToApplyDiscountOn.compareTo(BigDecimal.ZERO) < 0)
                || (discountAmount.compareTo(BigDecimal.ZERO) > 0 && amountToApplyDiscountOn.compareTo(BigDecimal.ZERO) > 0)) && (discountAmount.abs().compareTo(amountToApplyDiscountOn.abs()) > 0)) {
            return discountPlanItem.isAllowToNegate();
        }
        return false;
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
        InvoiceType invoiceType = invoiceTypeService.getDefaultDraft();
        for (DiscountPlanInstance dpi : discountPlanInstances) {
            if (!dpi.isEffective(invoice.getInvoiceDate()) || dpi.getStatus().equals(DiscountPlanInstanceStatusEnum.EXPIRED)) {
                continue;
            }
            if (dpi.getDiscountPlan().isActive()) {
                List<DiscountPlanItem> discountPlanItems = dpi.getDiscountPlan().getDiscountPlanItems();
                for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                    if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(), customerAccount, billingAccount, invoice, dpi)) {
                        applicableDiscountPlanItems.add(discountPlanItem);
                    }
                }
                if (!invoice.getInvoiceType().equals(invoiceType)) {
                    dpi.setApplicationCount(dpi.getApplicationCount() == null ? 1 : dpi.getApplicationCount() + 1);

                    if (dpi.getDiscountPlan().getApplicationLimit() != 0 && dpi.getApplicationCount() >= dpi.getDiscountPlan().getApplicationLimit()) {
                        dpi.setStatusDate(new Date());
                        dpi.setStatus(DiscountPlanInstanceStatusEnum.EXPIRED);
                    }
                    if (dpi.getStatus().equals(DiscountPlanInstanceStatusEnum.ACTIVE)) {
                        dpi.setStatusDate(new Date());
                        dpi.setStatus(DiscountPlanInstanceStatusEnum.IN_USE);
                    }
                    discountPlanInstanceService.update(dpi);
                }
            }
        }
        return applicableDiscountPlanItems;
    }

    private List<DiscountPlanInstance> findSubscriptionDPs(List<UserAccount> userAccounts) {
        if (!userAccounts.isEmpty()) {
            String query = "FROM DiscountPlanInstance dp WHERE dp.subscription.id IN " + "(SELECT rt.subscription.id FROM RatedTransaction rt LEFT JOIN rt.subscription WHERE rt.subscription.id IN( "
                    + "SELECT sub.id FROM Subscription sub WHERE sub.userAccount IN :userAccounts AND sub.status = :status))";
            return getEntityManager().createQuery(query, DiscountPlanInstance.class).setParameter("userAccounts", userAccounts).setParameter("status", SubscriptionStatusEnum.ACTIVE).getResultList();
        }
        return Collections.emptyList();
    }

    /**
     * @param expression EL expression
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
            PaymentMethod paymentMethod, InvoiceType invoiceType, boolean isPrepaid, BigDecimal balance, boolean automaticInvoiceCheck) throws BusinessException {

        Invoice invoice = new Invoice();

        invoice.setBillingAccount(billingAccount);
        invoice.setSeller(seller);
        invoice.setStatus(InvoiceStatusEnum.DRAFT);
        invoice.setInvoiceType(invoiceType);
        invoice.setPrepaid(isPrepaid);
        invoice.setInvoiceDate(invoiceDate);
        if (billingRun != null) {
            invoice.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));
        }
        Order order;
        if (entity instanceof Order) {
            order = (Order) entity;
            invoice.setOrder(order);

        } else if (entity instanceof Subscription) {
            invoice.setSubscription((Subscription) entity);
        } else if(entity instanceof CommercialOrder){
            CommercialOrder commercialOrder = (CommercialOrder) entity;
            invoice.setCommercialOrder(commercialOrder);
            invoice.setCpqQuote(commercialOrder.getQuote());
        }else if(entity instanceof CpqQuote){
            CpqQuote quote = (CpqQuote) entity;
            invoice.setCpqQuote(quote);
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

        Date dueDate = calculateDueDate(invoice, billingCycle, billingAccount, customerAccount, order);

        invoice.setDueDate(dueDate);
    }

    private Date calculateDueDate(Invoice invoice, BillingCycle billingCycle, BillingAccount billingAccount, CustomerAccount customerAccount, Order order) {
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
        return dueDate;
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
        Invoice invoice = this.initValidatedInvoice(invoiceDTO, billingAccount, invoiceType, seller);

        for (CategoryInvoiceAgregateDto catInvAgrDto : invoiceDTO.getCategoryInvoiceAgregates()) {
            UserAccount userAccount = extractUserAccount(billingAccount, catInvAgrDto.getUserAccountCode());
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
        for (Entry<InvoiceSubCategory, List<RatedTransaction>> invSubCat : existingRtsTolinkMap.entrySet()) {
            List<RatedTransaction> ratedTransactions = invSubCat.getValue();
            for (RatedTransaction rtToLink : rtsToLink) {
                ratedTransactions.remove(rtToLink);
            }
            if (ratedTransactions.isEmpty())
                invoicesToRemove.add(invSubCat.getKey());
        }
        for (InvoiceSubCategory invoiceSubCategory : invoicesToRemove)
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
            for (Entry<InvoiceCategory, List<InvoiceSubCategory>> invoiceCategory : subCategoryMap.entrySet()) {
                List<InvoiceSubCategory> subCategories = invoiceCategory.getValue();
                UserAccount userAccount = billingAccount.getUsersAccounts().get(0);
                InvoiceCategory invoiceCategorykey = invoiceCategory.getKey();
                CategoryInvoiceAgregate invoiceAgregateCat = initCategoryInvoiceAgregate(billingAccount, auditable, invoice, userAccount, invoiceCategorykey, subCategories.size(), invoiceCategorykey.getDescription());
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
        if (invoiceAgregateSubcat.getAmountsByTax() != null)
            for (Map.Entry<Tax, SubcategoryInvoiceAgregateAmount> amountByTax : invoiceAgregateSubcat.getAmountsByTax().entrySet()) {
                if (BigDecimal.ZERO.compareTo(amountByTax.getValue().getAmount(!isEnterprise)) != 0) {
                    Tax tax = amountByTax.getKey();
                    TaxInvoiceAgregate invoiceAgregateTax;
                    if (tax != null && taxInvoiceAgregateMap != null && taxInvoiceAgregateMap.containsKey(tax.getId())) {
                        invoiceAgregateTax = taxInvoiceAgregateMap.get(tax.getId());
                    } else {
                        invoiceAgregateTax = initTaxInvoiceAgregate(billingAccount, auditable, invoice, tax);
                    }
                    if (isEnterprise) {
                        invoiceAgregateTax.addAmountWithoutTax(amountByTax.getValue().getAmountWithoutTax());
                    } else {
                        invoiceAgregateTax.addAmountWithTax(amountByTax.getValue().getAmountWithTax());
                    }

                    BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(invoiceAgregateTax.getAmountWithoutTax(), invoiceAgregateTax.getAmountWithTax(), invoiceAgregateTax.getTaxPercent(), isEnterprise,
                        invoiceRounding, invoiceRoundingMode.getRoundingMode());
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
        if (tax != null) {
            invoiceAgregateTax.setTax(tax);
            invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
            invoiceAgregateTax.setTaxPercent(tax.getPercent());
        }
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
        invoiceAgregateSubcat.addRatedTransaction(rt, isEntreprise, true);
    }
    /*
     * private void linkRt(Invoice invoice, SubCategoryInvoiceAgregate invoiceAgregateSubcat, RatedTransaction rt, boolean isEntreprise) { rt.changeStatus(RatedTransactionStatusEnum.BILLED); rt.setInvoice(invoice);
     * rt.setInvoiceAgregateF(invoiceAgregateSubcat); invoiceAgregateSubcat.addRatedTransaction(rt, isEntreprise, false); addRTAmountsToSubcategoryInvoiceAggregate(invoiceAgregateSubcat, rt); }
     * 
     * private void addRTAmountsToSubcategoryInvoiceAggregate(SubCategoryInvoiceAgregate invoiceAgregateSubcat, RatedTransaction rt) { invoiceAgregateSubcat.addAmountWithoutTax(rt.getAmountWithoutTax());
     * invoiceAgregateSubcat.addAmountTax(rt.getAmountTax()); invoiceAgregateSubcat.addAmountWithTax(rt.getAmountWithTax()); }
     */

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
            ratedTransactionDto.getStartDate(), ratedTransactionDto.getEndDate(), seller, tax, tax.getPercent(), null, taxClass, null, null, null, null);

        rt.setWallet(userAccount != null ? userAccount.getWallet() : null);
        // #3355 : setting params 1,2,3
        if (isDetailledInvoiceMode) {
            rt.setParameter1(ratedTransactionDto.getParameter1());
            rt.setParameter2(ratedTransactionDto.getParameter2());
            rt.setParameter3(ratedTransactionDto.getParameter3());
        }
        return rt;
    }

    private UserAccount extractUserAccount(BillingAccount billingAccount, String userAccountCode) {
        UserAccount userAccount = null;
        if (userAccountCode != null) {
            userAccount = userAccountService.findByCode(userAccountCode);
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
            } else if (!userAccount.getBillingAccount().equals(billingAccount)) {
                throw new InvalidParameterException("User account code " + userAccountCode + " does not correspond to a Billing account " + billingAccount.getCode());
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

    private Invoice initValidatedInvoice(InvoiceDto invoiceDTO, BillingAccount billingAccount, InvoiceType invoiceType, Seller seller) throws BusinessException, EntityDoesNotExistsException, BusinessApiException {
        Invoice invoice = new Invoice();
        invoice.setBillingAccount(billingAccount);
        invoice.setSeller(seller);
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setDueDate(invoiceDTO.getDueDate());
        invoice.setDraft(invoiceDTO.isDraft());
        invoice.setAlreadySent(invoiceDTO.isCheckAlreadySent());
        if (invoiceDTO.isCheckAlreadySent()) {
            invoice.setEmailSentDate(invoiceDTO.getEmailSentDate());
        }
        invoice.setStatus(InvoiceStatusEnum.VALIDATED);
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

    private Invoice initValidatedInvoice(org.meveo.apiv2.billing.Invoice invoiceRessource, BillingAccount billingAccount, InvoiceType invoiceType, Seller seller, boolean isDraft)
            throws BusinessException, EntityDoesNotExistsException, BusinessApiException {
        Invoice invoice = new Invoice();
        invoice.setBillingAccount(billingAccount);
        invoice.setSeller(seller);
        invoice.setInvoiceDate(invoiceRessource.getInvoiceDate() != null ? invoiceRessource.getInvoiceDate() : new Date());
        invoice.setDueDate(invoiceRessource.getDueDate());
        invoice.setDraft(isDraft);
        boolean alreadySent = invoiceRessource.getEmailSentDate() != null;
        invoice.setAlreadySent(alreadySent);
        if (alreadySent) {
            invoice.setEmailSentDate(invoiceRessource.getEmailSentDate());
        }
        invoice.setStatus(InvoiceStatusEnum.DRAFT);
        invoice.setDontSend(alreadySent);
        PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            invoice.setPaymentMethodType(preferedPaymentMethod.getPaymentType());
        }
        invoice.setInvoiceType(invoiceType);
        if (invoiceRessource.getListLinkedInvoices() != null) {
            for (Long invoiceId : invoiceRessource.getListLinkedInvoices()) {
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

    /**
     * Delete invoices associated to a billing run matching status
     *
     * @param billingRun Billing run
     */
    public void deleteInvoicesByStatus(BillingRun billingRun, List<InvoiceStatusEnum> statusList) {
        getEntityManager().createNamedQuery("Invoice.deleteByStatusAndBR").setParameter("statusList", statusList).setParameter("billingRunId", billingRun.getId()).executeUpdate();
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
         * Tax change mapping. Key is ba.id_seller.id_invoiceType.id_ua.id_walletInstance.id_invoiceSubCategory.id_tax.id and value is an array of [Tax to apply, True/false if tax has changed]
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

        for (Entry<T, SubcategoryInvoiceAgregateAmount> entry : two.entrySet()) {
            T key = entry.getKey();
            if (result.containsKey(key)) {
                result.get(key).addAmounts(entry.getValue());
            } else {
                result.put(key, entry.getValue());
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

    /**
     * @param billingRun
     * @param toMove
     */
    public void moveInvoicesByStatus(BillingRun billingRun, List<InvoiceStatusEnum> toMove) {
        BillingRun nextBR = billingRunService.findOrCreateNextBR(billingRun.getId());
        getEntityManager().createNamedQuery("Invoice.moveToBR").setParameter("nextBR", nextBR).setParameter("billingRunId", billingRun.getId()).setParameter("statusList", toMove).executeUpdate();
    }

    /**
     * @param toCancel
     */
    public void cancelInvoicesByStatus(BillingRun billingRun, List<InvoiceStatusEnum> toCancel) {
        List<Invoice> invoices = findInvoicesByStatusAndBR(billingRun.getId(), toCancel);
        invoices.stream().forEach(invoice -> cancelInvoiceWithoutDelete(invoice));
    }

    /**
     * Find by invoice number and invoice type id.
     *
     * @param invoiceNumber invoice's number
     * @param invoiceType invoice's type
     * @return found invoice
     * @throws BusinessException business exception
     */
    public Invoice findByInvoiceTypeAndInvoiceNumber(String invoiceNumber, Long invoiceTypeId) throws BusinessException {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
        qb.addCriterion("i.invoiceNumber", "=", invoiceNumber, true);
        qb.addCriterion("i.invoiceType.id", "=", invoiceTypeId, true);
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

    public byte[] getInvoicePdf(Invoice invoice, boolean generatePdfIfNoExist) {
        invoice = retrieveIfNotManaged(invoice);
        if (invoice.isPrepaid()) {
            throw new BusinessException("Invoice PDF is disabled for prepaid invoice: " + invoice.getInvoiceNumber());
        }
        if (!invoiceService.isInvoicePdfExist(invoice)) {
            if (generatePdfIfNoExist) {
                produceInvoicePdf(invoice, Collections.EMPTY_LIST);
            } else {
                return null;
            }
        }
        return getInvoicePdf(invoice);
    }

    public Invoice createBasicInvoice(BasicInvoice resource) {
        final String billingAccountCode = resource.getBillingAccountCode();
        final BigDecimal amountWithTax = resource.getAmountWithTax();
        final Date invoiceDate = resource.getInvoiceDate() != null ? resource.getInvoiceDate() : new Date();

        Order order = (Order) tryToFindByEntityClassAndCode(Order.class, resource.getOrderCode());
        BillingAccount billingAccount = (BillingAccount) tryToFindByEntityClassAndCode(BillingAccount.class, billingAccountCode);
        final String invoiceTypeCode = resource.getInvoiceTypeCode() != null ? resource.getInvoiceTypeCode() : "ADV";
        InvoiceType advType = (InvoiceType) tryToFindByEntityClassAndCode(InvoiceType.class, invoiceTypeCode);

        Invoice invoice = initBasicInvoiceInvoice(amountWithTax, invoiceDate, order, billingAccount, advType);
        invoice.updateAudit(currentUser);
        getEntityManager().persist(invoice);
        postCreate(invoice);
        return invoice;
    }

    private Invoice initBasicInvoiceInvoice(final BigDecimal amountWithTax, final Date invoiceDate, Order order, BillingAccount billingAccount, InvoiceType advType) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(advType);
        invoice.setBillingAccount(billingAccount);
        invoice.setOrder(order);

        invoice.setPaymentStatus(InvoicePaymentStatusEnum.NONE);
        invoice.setStartDate(invoiceDate);
        invoice.setAmountWithTax(amountWithTax);
        invoice.setRawAmount(amountWithTax);
        invoice.setAmountWithoutTax(amountWithTax);
        invoice.setAmountTax(BigDecimal.ZERO);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setDetailedInvoice(true);
        invoice.setNetToPay(amountWithTax);
        Date dueDate = calculateDueDate(invoice, billingAccount.getBillingCycle(), billingAccount, billingAccount.getCustomerAccount(), order);
        invoice.setDueDate(dueDate);
        invoice.setSeller(billingAccount.getCustomerAccount().getCustomer().getSeller());
        invoice.setStatus(InvoiceStatusEnum.NEW);
        return invoice;
    }

    private InvoiceLine initInvoiceLineForBasicInvoice(BasicInvoice resource, final BigDecimal amountWithTax, Order order, BillingAccount billingAccount, AccountingArticle accountingArticle, Invoice invoice) {
        InvoiceLine line = new InvoiceLine();
        line.setInvoice(invoice);
        line.setBillingAccount(billingAccount);
        line.setAccountingArticle(accountingArticle);
        line.setOrderNumber(order.getOrderNumber());

        line.setQuantity(BigDecimal.ONE);
        line.setAmountWithTax(amountWithTax);
        line.setRawAmount(amountWithTax);
        line.setAmountWithoutTax(amountWithTax);
        line.setAmountTax(BigDecimal.ZERO);
        line.setUnitPrice(amountWithTax);
        line.setDiscountAmount(BigDecimal.ZERO);
        line.setLabel(resource.getLabel());
        return line;
    }

    /**
     * @param invoiceNumber
     * @param typeCode
     * @return
     */
    public Invoice findByInvoiceNumberAndTypeCode(String invoiceNumber, String typeCode) {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
        qb.addCriterion("i.invoiceNumber", "=", invoiceNumber, true);
        qb.addCriterion("i.invoiceType.code", "=", typeCode, true);
        try {
            return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.info("Invoice with invoice number {} and code {} was not found. Returning null.", invoiceNumber, typeCode);
            return null;
        } catch (NonUniqueResultException e) {
            log.info("Multiple invoices with invoice number {} and code {} was found. Returning null.", invoiceNumber, typeCode);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    protected InvoiceLinesToInvoice getInvoiceLinesGroups(IBillableEntity entityToInvoice, BillingAccount billingAccount, BillingRun billingRun, BillingCycle defaultBillingCycle, InvoiceType defaultInvoiceType,
            Filter filter, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft, PaymentMethod defaultPaymentMethod, Invoice existingInvoice) throws BusinessException {
        List<InvoiceLine> invoiceLines = existingInvoice != null ? invoiceLinesService.listInvoiceLinesByInvoice(existingInvoice.getId())
                : getInvoiceLines(entityToInvoice, filter, firstTransactionDate, lastTransactionDate, isDraft);
        boolean moreIls = invoiceLines.size() == rtPaginationSize;
        if (log.isDebugEnabled()) {
            log.debug("Split {} Invoice Lines for {}/{} in to billing account/seller/invoice type groups. {} invoice Lines to retrieve.", invoiceLines.size(), entityToInvoice.getClass().getSimpleName(),
                entityToInvoice.getId(), moreIls ? "More" : "No more");
        }
        Map<String, InvoiceLinesGroup> invoiceLinesGroup = new HashMap<>();

        BillingCycle billingCycle = defaultBillingCycle;
        InvoiceType postPaidInvoiceType = defaultInvoiceType;
        PaymentMethod paymentMethod;
        if (defaultPaymentMethod == null && billingAccount != null) {
            defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
        }
        EntityManager em = getEntityManager();
        for (InvoiceLine invoiceLine : invoiceLines) {
            // Order can span multiple billing accounts and some Billing account-dependent values have to be recalculated
        	if ((entityToInvoice instanceof Order || entityToInvoice instanceof CpqQuote) && (billingAccount == null || !billingAccount.getId().equals(invoiceLine.getBillingAccount().getId()))) {
                billingAccount = invoiceLine.getBillingAccount();
                if (defaultPaymentMethod == null && billingAccount != null) {
                    defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
                }
                if (defaultBillingCycle == null) {
                    billingCycle = billingAccount != null ? billingAccount.getBillingCycle() : null;
                }
                if (defaultInvoiceType == null) {
                    postPaidInvoiceType = determineInvoiceType(false, isDraft, billingCycle, billingRun, billingAccount);
                }
            }
            InvoiceType invoiceType = postPaidInvoiceType;
            paymentMethod = resolvePMethod(billingAccount, billingCycle, defaultPaymentMethod, invoiceLine);
            Seller seller = billingAccount.getCustomerAccount().getCustomer().getSeller();
            String invoiceKey = billingAccount.getId() + "_" + seller.getId() + "_" + invoiceType.getId() + "_" + paymentMethod.getId();
            InvoiceLinesGroup ilGroup = invoiceLinesGroup.get(invoiceKey);
            if (ilGroup == null) {
                ilGroup = new InvoiceLinesGroup(billingAccount, billingCycle != null ? billingCycle : billingAccount.getBillingCycle(), seller, invoiceType, false, invoiceKey, paymentMethod);
                invoiceLinesGroup.put(invoiceKey, ilGroup);
            }
            ilGroup.getInvoiceLines().add(invoiceLine);

            em.detach(invoiceLine);
        }

        List<InvoiceLinesGroup> convertedIlGroups = new ArrayList<>();
        for (InvoiceLinesGroup linesGroup : invoiceLinesGroup.values()) {

            if (linesGroup.getBillingCycle().getScriptInstance() != null) {
                convertedIlGroups.addAll(executeBCScriptWithInvoiceLines(billingRun, linesGroup.getInvoiceType(), linesGroup.getInvoiceLines(), entityToInvoice, linesGroup.getBillingCycle().getScriptInstance().getCode(),
                    linesGroup.getPaymentMethod()));
            } else {
                convertedIlGroups.add(linesGroup);
            }
        }
        return new InvoiceLinesToInvoice(moreIls, convertedIlGroups);

    }

    private PaymentMethod resolvePMethod(BillingAccount billingAccount, BillingCycle billingCycle, PaymentMethod defaultPaymentMethod, InvoiceLine invoiceLine) {
        if (BillingEntityTypeEnum.SUBSCRIPTION.equals(billingCycle.getType()) || (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && billingCycle.isSplitPerPaymentMethod())) {
            if (Objects.nonNull(invoiceLine.getSubscription().getPaymentMethod())) {
                return invoiceLine.getSubscription().getPaymentMethod();
            } else if (Objects.nonNull(billingAccount.getPaymentMethod())) {
                return billingAccount.getPaymentMethod();
            }
        }
        if (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && (!billingCycle.isSplitPerPaymentMethod() && Objects.nonNull(billingAccount.getPaymentMethod()))) {
            return billingAccount.getPaymentMethod();
        }
        return defaultPaymentMethod;
    }

    private List<InvoiceLine> getInvoiceLines(IBillableEntity entityToInvoice, Filter filter, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft) {
        return invoiceLinesService.listInvoiceLinesToInvoice(entityToInvoice, firstTransactionDate, lastTransactionDate, filter, rtPaginationSize);
    }

    /**
     * Creates invoices and their aggregates - IN new transaction
     *
     * @param entityToInvoice entity to be billed
     * @param billingRun billing run
     * @param filter invoice line filter
     * @param invoiceDate date of invoice
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param minAmountForAccounts Check if min amount is enabled in any account level
     * @param isDraft Is this a draft invoice
     * @param automaticInvoiceCheck automatic invoice check
     * @return A list of created invoices
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Invoice> createAggregatesAndInvoiceWithILInNewTransaction(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck, boolean isDepositInvoice) throws BusinessException {
        return createAggregatesAndInvoiceWithIL(entityToInvoice, billingRun, filter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, automaticInvoiceCheck, isDepositInvoice);
    }

    /**
     * Creates invoices and their aggregates
     *
     * @param entityToInvoice entity to be billed
     * @param billingRun billing run
     * @param filter invoice line filter
     * @param invoiceDate date of invoice
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param minAmountForAccounts Check if min amount is enabled in any account level
     * @param isDraft Is this a draft invoice
     * @param automaticInvoiceCheck automatic invoice check
     * @return A list of created invoices
     * @throws BusinessException business exception
     */
    public List<Invoice> createAggregatesAndInvoiceWithIL(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck, boolean isDepositInvoice) throws BusinessException {
        log.debug("Will create invoice and aggregates for {}/{}", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());

        if (billingRun == null) {
            if (invoiceDate == null) {
                throw new BusinessException("invoiceDate must be set if billingRun is null");
            }
            if (StringUtils.isBlank(lastTransactionDate) && filter == null) {
                throw new BusinessException("lastTransactionDate or filter must be set if billingRun is null");
            }
        }

        List<InvoiceLine> minAmountInvoiceLines = entityToInvoice.getMinInvoiceLines();

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
            } else if (entityToInvoice instanceof CommercialOrder) {
                entityToInvoice = commercialOrderService.retrieveIfNotManaged((CommercialOrder) entityToInvoice);
                ba = ((CommercialOrder) entityToInvoice).getBillingAccount();
            } else if (entityToInvoice instanceof CpqQuote) {
                entityToInvoice = cpqQuoteService.retrieveIfNotManaged((CpqQuote) entityToInvoice);
                ba = ((CpqQuote) entityToInvoice).getBillableAccount();
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

            if (minAmountForAccounts != null && minAmountForAccounts.isMinAmountCalculationActivated()) {
                invoiceLinesService.calculateAmountsAndCreateMinAmountLines(entityToInvoice, lastTransactionDate, invoiceDate, false, minAmountForAccounts);
                minAmountInvoiceLines = entityToInvoice.getMinInvoiceLines();
            }

            BillingCycle billingCycle = billingRun != null ? billingRun.getBillingCycle() : entityToInvoice.getBillingCycle();
            if (billingCycle == null && !(entityToInvoice instanceof Order)) {
                billingCycle = ba.getBillingCycle();
            }
            PaymentMethod paymentMethod = null;
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

                invoiceType = determineInvoiceType(false, isDraft, isDepositInvoice, billingCycle, billingRun, ba);
            }

            boolean hasMin = false;
            if (minAmountInvoiceLines != null && !minAmountInvoiceLines.isEmpty()) {
                for (InvoiceLine minInvoiceLine : minAmountInvoiceLines) {
                    minInvoiceLine.setBillingAccount(billingAccountService.retrieveIfNotManaged(minInvoiceLine.getBillingAccount()));
                    minInvoiceLine.setAccountingArticle(accountingArticleService.retrieveIfNotManaged(minInvoiceLine.getAccountingArticle()));
                    invoiceLinesService.create(minInvoiceLine);
                }
                hasMin = true;
                commit();
            }

            return createAggregatesAndInvoiceFromIls(entityToInvoice, billingRun, filter, invoiceDate, firstTransactionDate, lastTransactionDate, isDraft, billingCycle, ba, paymentMethod, invoiceType, balance,
                automaticInvoiceCheck, hasMin, null);
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
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Invoice> createAggregatesAndInvoiceFromIls(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft,
            BillingCycle defaultBillingCycle, BillingAccount billingAccount, PaymentMethod defaultPaymentMethod, InvoiceType defaultInvoiceType, BigDecimal balance, boolean automaticInvoiceCheck, boolean hasMin,
            Invoice existingInvoice) throws BusinessException {
        List<Invoice> invoiceList = new ArrayList<>();
        boolean moreInvoiceLinesExpected = true;
        Map<String, InvoiceAggregateProcessingInfo> invoiceLineGroupToInvoiceMap = new HashMap<>();

        boolean allIlsInOneRun = true;

        while (moreInvoiceLinesExpected) {

            if (entityToInvoice instanceof Order) {
                billingAccount = null;
                defaultInvoiceType = null;
            }

            InvoiceLinesToInvoice iLsToInvoice = getInvoiceLinesGroups(entityToInvoice, billingAccount, billingRun, defaultBillingCycle, defaultInvoiceType, filter, firstTransactionDate, lastTransactionDate, isDraft,
                defaultPaymentMethod, existingInvoice);
            List<InvoiceLinesGroup> invoiceLinesGroupsPaged = iLsToInvoice.invoiceLinesGroups;
            moreInvoiceLinesExpected = iLsToInvoice.moreInvoiceLines;
            if (moreInvoiceLinesExpected) {
                allIlsInOneRun = false;
            }

            if (invoiceLineGroupToInvoiceMap.isEmpty() && invoiceLinesGroupsPaged.isEmpty()) {
                log.warn("Account {}/{} has no billable transactions", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());
                return new ArrayList<>();
            } else if (!invoiceLinesGroupsPaged.isEmpty()) {
                for (InvoiceLinesGroup invoiceLinesGroup : invoiceLinesGroupsPaged) {

                    if (entityToInvoice instanceof Order) {
                        if (billingAccount == null || !billingAccount.getId().equals(invoiceLinesGroup.getBillingAccount().getId())) {
                            billingAccount = invoiceLinesGroup.getBillingAccount();
                            boolean isBalanceDue = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.due", true);
                            boolean isBalanceLitigation = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.litigation", false);
                            if (isBalanceLitigation) {
                                balance = customerAccountService.customerAccountBalanceDue(billingAccount.getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                            } else {
                                balance = customerAccountService.customerAccountBalanceDueWithoutLitigation(billingAccount.getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                            }
                        }
                    }

                    String invoiceKey = invoiceLinesGroup.getInvoiceKey();

                    InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo = invoiceLineGroupToInvoiceMap.get(invoiceKey);
                    if (invoiceAggregateProcessingInfo == null) {
                        invoiceAggregateProcessingInfo = new InvoiceAggregateProcessingInfo();
                        invoiceLineGroupToInvoiceMap.put(invoiceKey, invoiceAggregateProcessingInfo);
                    }

                    if (invoiceAggregateProcessingInfo.invoice == null) {
                        if (existingInvoice != null) {
                            cleanInvoiceAggregates(existingInvoice.getId());
                            invoiceAggregateProcessingInfo.invoice = existingInvoice;
                        } else {
                            invoiceAggregateProcessingInfo.invoice = instantiateInvoice(entityToInvoice, invoiceLinesGroup.getBillingAccount(), invoiceLinesGroup.getSeller(), billingRun, invoiceDate, isDraft,
                                invoiceLinesGroup.getBillingCycle(), invoiceLinesGroup.getPaymentMethod(), invoiceLinesGroup.getInvoiceType(), invoiceLinesGroup.isPrepaid(), balance, automaticInvoiceCheck);
                        }
                        invoiceList.add(invoiceAggregateProcessingInfo.invoice);
                    }

                    Invoice invoice = invoiceAggregateProcessingInfo.invoice;
                    invoice.setHasMinimum(hasMin);

                    appendInvoiceAggregatesIL(entityToInvoice, invoiceLinesGroup.getBillingAccount(), invoice, invoiceLinesGroup.getInvoiceLines(), false, invoiceAggregateProcessingInfo, !allIlsInOneRun);
                    List<Object[]> ilMassUpdates = new ArrayList<>();
                    List<Object[]> ilUpdates = new ArrayList<>();

                    for (SubCategoryInvoiceAgregate subAggregate : invoiceAggregateProcessingInfo.subCategoryAggregates.values()) {
                        if (subAggregate.getRatedtransactionsToAssociate() == null) {
                            continue;
                        }
                        List<Long> invoiceLineIds = new ArrayList<>();
                        List<InvoiceLine> invoiceLines = new ArrayList<>();

                        for (InvoiceLine invoiceLine : subAggregate.getInvoiceLinesToAssociate()) {
                            if (invoiceLine.isTaxRecalculated()) {
                                invoiceLines.add(invoiceLine);
                            } else {
                                invoiceLineIds.add(invoiceLine.getId());
                            }
                        }

                        if (!invoiceLineIds.isEmpty()) {
                            ilMassUpdates.add(new Object[] { subAggregate, invoiceLineIds });
                        } else if (!invoiceLines.isEmpty()) {
                            ilUpdates.add(new Object[] { subAggregate, invoiceLines });
                        }
                        subAggregate.setInvoiceLinesToAssociate(new ArrayList<>());
                    }

                    setInvoiceDueDate(invoice, invoiceLinesGroup.getBillingCycle());

                    EntityManager em = getEntityManager();
                    invoice.setNewInvoicingProcess(true);
                    invoice.setHasMinimum(true);
                    if (invoice.getId() == null) {
                        this.create(invoice);

                    } else {
                        for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
                            if (invoiceAggregate.getId() == null) {
                                em.persist(invoiceAggregate);
                            }
                        }
                    }

                    em.flush();

                    Date now = new Date();
                    for (Object[] aggregateAndILIds : ilMassUpdates) {
                        List<Long> ilIds = (List<Long>) aggregateAndILIds[1];
                        SubCategoryInvoiceAgregate subCategoryAggregate = (SubCategoryInvoiceAgregate) aggregateAndILIds[0];
                        em.createNamedQuery("InvoiceLine.updateWithInvoice").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("now", now)
                            .setParameter("invoiceAgregateF", subCategoryAggregate).setParameter("ids", ilIds).executeUpdate();
                        em.createNamedQuery("RatedTransaction.linkRTWithInvoice").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("now", now)
                            .setParameter("ids", ilIds).executeUpdate();
                    }

                    for (Object[] aggregateAndILs : ilUpdates) {
                        SubCategoryInvoiceAgregate subCategoryAggregate = (SubCategoryInvoiceAgregate) aggregateAndILs[0];
                        List<InvoiceLine> invoiceLines = (List<InvoiceLine>) aggregateAndILs[1];
                        for (InvoiceLine invoiceLine : invoiceLines) {
                            em.createNamedQuery("InvoiceLine.updateWithInvoiceInfo").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("now", now)
                                .setParameter("amountWithoutTax", invoiceLine.getAmountWithoutTax()).setParameter("amountWithTax", invoiceLine.getAmountWithTax()).setParameter("amountTax", invoiceLine.getAmountTax())
                                .setParameter("tax", invoiceLine.getTax()).setParameter("taxPercent", invoiceLine.getTaxRate()).setParameter("invoiceAgregateF", subCategoryAggregate)
                                .setParameter("id", invoiceLine.getId()).executeUpdate();
                        }
                    }
                }
            }
        }

        for (InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo : invoiceLineGroupToInvoiceMap.values()) {
            if (!allIlsInOneRun) {
                addDiscountCategoryAndTaxAggregates(invoiceAggregateProcessingInfo.invoice, invoiceAggregateProcessingInfo.subCategoryAggregates.values());
            }
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
            applyAutomaticInvoiceCheck(invoiceAggregateProcessingInfo.invoice);
            postCreate(invoiceAggregateProcessingInfo.invoice);
        }
        return invoiceList;

    }

    /**
     * delete invoice aggregates
     * 
     * @param invoiceId
     */
    public void cleanInvoiceAggregates(Long invoiceId) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteInvoiceSubCategoryAggrByInvoice").setParameter("invoiceId", invoiceId).executeUpdate();
        getEntityManager().createNamedQuery("InvoiceAgregate.deleteByInvoiceIds").setParameter("invoicesIds", Arrays.asList(invoiceId)).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private List<InvoiceLinesGroup> executeBCScriptWithInvoiceLines(BillingRun billingRun, InvoiceType invoiceType, List<InvoiceLine> invoiceLines, IBillableEntity entity, String scriptInstanceCode,
            PaymentMethod paymentMethod) throws BusinessException {
        HashMap<String, Object> context = new HashMap<>();
        context.put(Script.CONTEXT_ENTITY, entity);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        context.put("br", billingRun);
        context.put("invoiceType", invoiceType);
        context.put("invoiceLines", invoiceLines);
        context.put("paymentMethod", paymentMethod);
        scriptInstanceService.executeCached(scriptInstanceCode, context);
        return (List<InvoiceLinesGroup>) context.get(Script.RESULT_VALUE);
    }

    protected void appendInvoiceAggregatesIL(IBillableEntity entityToInvoice, BillingAccount billingAccount, Invoice invoice, List<InvoiceLine> invoiceLines, boolean isInvoiceAdjustment,
            InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo, boolean moreInvoiceLinesExpected) throws BusinessException {

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
        // boolean calculateExternalTax = "YES".equalsIgnoreCase((String) appProvider.getCfValue("OPENCELL_ENABLE_TAX_CALCULATION"));

        // Tax change mapping. Key is ba.id_taxClass.id and value is an array of [Tax to apply, True/false if tax has changed]
        Map<String, Object[]> taxChangeMap = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.taxChangeMap : new HashMap<>();

        // Subcategory aggregates mapping. Key is ua.id_walletInstance.id_invoiceSubCategory.id_tax.id
        Map<String, SubCategoryInvoiceAgregate> subCategoryAggregates = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.subCategoryAggregates : new LinkedHashMap<>();

        Set<String> orderNumbers = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.orderNumbers : new HashSet<>();

        String scaKey;

        if (log.isTraceEnabled()) {
            log.trace("ratedTransactions.totalAmountWithoutTax={}", invoiceLines != null ? invoiceLines.stream().mapToDouble(e -> e.getAmountWithoutTax().doubleValue()).sum() : "0");
        }

        boolean taxWasRecalculated = false;
        for (InvoiceLine invoiceLine : invoiceLines) {

            InvoiceSubCategory invoiceSubCategory = invoiceLine.getAccountingArticle().getInvoiceSubCategory();

            scaKey = invoiceSubCategory.getId().toString();
            if (isAggregateByUA && invoiceLine.getSubscription() != null) {
                scaKey = (invoiceLine.getSubscription().getUserAccount() != null ? invoiceLine.getSubscription().getUserAccount().getId() : "") + "_" + scaKey;
            }

            Tax tax = invoiceLine.getTax();
            UserAccount userAccount = invoiceLine.getSubscription() == null ? null : invoiceLine.getSubscription().getUserAccount();

            // Check if tax has to be recalculated. Does not apply to RatedTransactions that had tax explicitly set/overridden
            if (calculateTaxOnSubCategoryLevel && !invoiceLine.isTaxOverridden()) {

                TaxClass taxClass = invoiceLine.getAccountingArticle().getTaxClass();
                String taxChangeKey = billingAccount.getId() + "_" + taxClass.getId();

                Object[] changedToTax = taxChangeMap.get(taxChangeKey);
                if (changedToTax == null) {
                    taxZero = isExonerated && taxZero == null ? taxService.getZeroTax() : taxZero;
                    Object[] applicableTax = taxMappingService.getApplicableTax(tax, isExonerated, invoice.getSeller(),invoice.getBillingAccount(),invoice.getInvoiceDate(), taxClass, userAccount, taxZero);
                    changedToTax = applicableTax;
                    taxChangeMap.put(taxChangeKey, changedToTax);
                    if ((boolean) changedToTax[1]) {
                        log.debug("Will update rated transactions of Billing account {} and tax class {} with new tax from {}/{}% to {}/{}%", billingAccount.getId(), taxClass.getId(), tax == null ? null : tax.getId(),
                            tax == null ? null : tax.getPercent(), ((Tax) changedToTax[0]).getId(), ((Tax) changedToTax[0]).getPercent());
                    }
                }
                taxWasRecalculated = (boolean) changedToTax[1];
                if (taxWasRecalculated) {
                    tax = (Tax) changedToTax[0];
                    invoiceLine.setTaxRecalculated(true);
                }
            }

            SubCategoryInvoiceAgregate scAggregate = subCategoryAggregates.get(scaKey);
            if (scAggregate == null) {
                scAggregate = new SubCategoryInvoiceAgregate(invoiceSubCategory, billingAccount, isAggregateByUA ? userAccount : null, null, invoice, invoiceSubCategory.getAccountingCode());
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

            if (!(entityToInvoice instanceof Order) && invoiceLine.getOrderNumber() != null) {
                orderNumbers.add(invoiceLine.getOrderNumber());
            }

            if (taxWasRecalculated) {
                invoiceLine.setTax(tax);
                invoiceLine.setTaxRate(tax.getPercent());
                invoiceLine.computeDerivedAmounts(isEnterprise, rtRounding, rtRoundingMode);
            }

            scAggregate.addInvoiceLine(invoiceLine, isEnterprise, true);
        }

        if (moreInvoiceLinesExpected) {
            return;
        }

        addDiscountCategoryAndTaxAggregates(invoice, subCategoryAggregates.values());
    }

    protected class InvoiceLinesToInvoice {

        protected boolean moreInvoiceLines;

        protected List<InvoiceLinesGroup> invoiceLinesGroups;

        protected InvoiceLinesToInvoice(boolean moreInvoiceLines, List<InvoiceLinesGroup> invoiceLinesGroups) {
            super();
            this.moreInvoiceLines = moreInvoiceLines;
            this.invoiceLinesGroups = invoiceLinesGroups;
        }
    }

    /**
     * get list of invoices without generated XML files matching billing run and status list
     * 
     * @param billingRunId
     * @param statusList
     * @return
     */
    public List<Long> listInvoicesWithoutXml(Long billingRunId, List<InvoiceStatusEnum> statusList) {
        if (billingRunId == null) {
            return getEntityManager().createNamedQuery("Invoice.noXmlWithStatus", Long.class).setParameter("statusList", statusList).getResultList();
        } else
            return getEntityManager().createNamedQuery("Invoice.noXmlWithStatusAndBR", Long.class).setParameter("billingRunId", billingRunId).setParameter("statusList", statusList).getResultList();
    }

    /**
     * Create an invoice (V11 process)
     *
     * @param invoiceRessource
     * @param skipValidtion
     * @param isDraft
     * @return invoice
     * @param isIncludeBalance
     * @param isAutoValidation
     * @param isVirtual
     * @throws EntityDoesNotExistsException
     * @throws BusinessApiException
     * @throws BusinessException
     * @throws InvalidParameterException
     */
    public Invoice createInvoiceV11(org.meveo.apiv2.billing.Invoice invoiceRessource, boolean skipValidtion, boolean isDraft, boolean isVirtual, Boolean isIncludeBalance, Boolean isAutoValidation)
            throws EntityDoesNotExistsException, BusinessApiException, BusinessException, InvalidParameterException {

        Seller seller = (Seller) tryToFindByEntityClassAndCode(Seller.class, invoiceRessource.getSellerCode());
        BillingAccount billingAccount = (BillingAccount) tryToFindByEntityClassAndCode(BillingAccount.class, invoiceRessource.getBillingAccountCode());
        InvoiceType invoiceType = (InvoiceType) tryToFindByEntityClassAndCode(InvoiceType.class, invoiceRessource.getInvoiceTypeCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceRessource.getInvoiceTypeCode());
        }
        Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();
        boolean isEnterprise = appProvider.isEntreprise();
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        Auditable auditable = new Auditable(currentUser);
        Map<InvoiceSubCategory, List<InvoiceLine>> existinginvoiceLinesTolinkMap = extractMappedInvoiceLinesTolink(invoiceRessource, billingAccount);

        Map<InvoiceCategory, List<InvoiceSubCategory>> subCategoryMap = new HashMap<InvoiceCategory, List<InvoiceSubCategory>>();
        Invoice invoice = this.initValidatedInvoice(invoiceRessource, billingAccount, invoiceType, seller, isDraft);

        if (invoiceRessource.getDiscountPlan() != null) {
            final Long dpId = invoiceRessource.getDiscountPlan().getId();
            DiscountPlan discountPlan = (DiscountPlan) tryToFindByEntityClassAndId(DiscountPlan.class, dpId);
            invoice.setDiscountPlan(discountPlan);
        }
        validateInvoiceResourceAgregates(invoiceRessource);
        for (org.meveo.apiv2.billing.CategoryInvoiceAgregate catInvAgr : invoiceRessource.getCategoryInvoiceAgregates()) {
            UserAccount userAccount = extractUserAccount(billingAccount, catInvAgr.getUserAccountCode());
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(catInvAgr.getCategoryInvoiceCode());
            if (invoiceCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, catInvAgr.getCategoryInvoiceCode());
            }
            CategoryInvoiceAgregate invoiceAgregateCat = initCategoryInvoiceAgregate(billingAccount, auditable, invoice, userAccount, invoiceCategory, catInvAgr.getListSubCategoryInvoiceAgregate().size(),
                catInvAgr.getDescription());

            for (org.meveo.apiv2.billing.SubCategoryInvoiceAgregate subCatInvAgr : catInvAgr.getListSubCategoryInvoiceAgregate()) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubcategoryService.findByCode(subCatInvAgr.getInvoiceSubCategoryCode());
                if (invoiceSubCategory == null) {
                    throw new EntityDoesNotExistsException(InvoiceSubCategory.class, subCatInvAgr.getInvoiceSubCategoryCode());
                }
                SubCategoryInvoiceAgregate invoiceAgregateSubcat = initSubCategoryInvoiceAgregate(auditable, invoice, userAccount, invoiceAgregateCat, subCatInvAgr.getDescription(), invoiceSubCategory);
                createAndLinkILsFromDTO(seller, billingAccount, isEnterprise, invoiceRounding, invoiceRoundingMode, invoice, userAccount, subCatInvAgr, invoiceSubCategory, invoiceAgregateSubcat);
                linkExistingILs(invoiceRessource, existinginvoiceLinesTolinkMap, isEnterprise, invoice, userAccount, invoiceSubCategory, invoiceAgregateSubcat);
                saveInvoiceSubCatAndILs(invoice, invoiceAgregateSubcat, subCatInvAgr, billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoiceRounding, invoiceRoundingMode);
                addSubCategoryAmountsToCategory(invoiceAgregateCat, invoiceAgregateSubcat);
            }

            if (!existinginvoiceLinesTolinkMap.isEmpty() && subCategoryMap.containsKey(invoiceCategory)) {
                List<InvoiceSubCategory> subCategories = subCategoryMap.get(invoiceCategory);
                linkILsAndSubCats(billingAccount, taxInvoiceAgregateMap, isEnterprise, invoiceRounding, invoiceRoundingMode, auditable, existinginvoiceLinesTolinkMap, invoice, userAccount, invoiceAgregateCat,
                    subCategories);
            }
            getEntityManager().flush();
            addCategoryAmountsToInvoice(invoice, invoiceAgregateCat);
            subCategoryMap.remove(invoiceCategory);
        }

        linkILsHavingCategoryOutOfInput(billingAccount, isEnterprise, auditable, existinginvoiceLinesTolinkMap, subCategoryMap, invoice, taxInvoiceAgregateMap, invoiceRounding, invoiceRoundingMode);

        invoice = finaliseInvoiceCreation(invoiceRessource, isEnterprise, invoiceRounding, invoiceRoundingMode, invoice, isAutoValidation, isIncludeBalance);
        return invoice;
    }

    private void validateInvoiceResourceAgregates(org.meveo.apiv2.billing.Invoice invoiceResource) throws ValidationException {
        for (org.meveo.apiv2.billing.CategoryInvoiceAgregate catInvAgr : invoiceResource.getCategoryInvoiceAgregates()) {
            if (StringUtils.isBlank(catInvAgr.getCategoryInvoiceCode())) {
                throw new ValidationException("missing categoryInvoiceCode");
            }
            if (catInvAgr.getListSubCategoryInvoiceAgregate() == null || catInvAgr.getListSubCategoryInvoiceAgregate().isEmpty()) {
                throw new ValidationException("missing listSubCategoryInvoiceAgregate");
            }
            for (org.meveo.apiv2.billing.SubCategoryInvoiceAgregate subCatInvAgr : catInvAgr.getListSubCategoryInvoiceAgregate()) {
                if (StringUtils.isBlank(subCatInvAgr.getInvoiceSubCategoryCode())) {
                    throw new ValidationException("missing invoiceSubCategoryCode");
                }
            }
        }
    }

    /**
     * @param billingAccount
     * @param taxInvoiceAgregateMap
     * @param isEnterprise
     * @param invoiceRounding
     * @param invoiceRoundingMode
     * @param auditable
     * @param existinginvoiceLinesTolinkMap
     * @param invoice
     * @param userAccount
     * @param invoiceAgregateCat
     * @param subCategories
     */
    private void linkILsAndSubCats(BillingAccount billingAccount, Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, Auditable auditable,
            Map<InvoiceSubCategory, List<InvoiceLine>> existinginvoiceLinesTolinkMap, Invoice invoice, UserAccount userAccount, CategoryInvoiceAgregate invoiceAgregateCat, List<InvoiceSubCategory> subCategories) {
        for (InvoiceSubCategory invoiceSubCategory : subCategories) {
            if (existinginvoiceLinesTolinkMap.containsKey(invoiceSubCategory)) {
                List<InvoiceLine> ilsToLink = existinginvoiceLinesTolinkMap.remove(invoiceSubCategory);

                SubCategoryInvoiceAgregate invoiceAgregateSubcat = initSubCategoryInvoiceAgregate(auditable, invoice, userAccount, invoiceAgregateCat, invoiceSubCategory.getDescription(), invoiceSubCategory);
                for (InvoiceLine il : ilsToLink) {
                    linkIL(invoice, invoiceAgregateSubcat, il, isEnterprise);
                }
                addSubCategoryAmountsToCategory(invoiceAgregateCat, invoiceAgregateSubcat);
                saveInvoiceSubCatAndILs(invoice, invoiceAgregateSubcat, null, billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoiceRounding, invoiceRoundingMode);
            }
        }
    }

    /**
     * @param invoice
     * @param invoiceAgregateSubcat
     * @param subCatInvAgr
     * @param billingAccount
     * @param taxInvoiceAgregateMap
     * @param isEnterprise
     * @param auditable
     * @param invoiceRounding
     * @param invoiceRoundingMode
     */
    private void saveInvoiceSubCatAndILs(Invoice invoice, SubCategoryInvoiceAgregate invoiceAgregateSubcat, org.meveo.apiv2.billing.SubCategoryInvoiceAgregate subCatInvAgr, BillingAccount billingAccount,
            Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap, boolean isEnterprise, Auditable auditable, int invoiceRounding, RoundingModeEnum invoiceRoundingMode) {
        List<InvoiceLine> invoiceLines;
        invoiceAgregateSubcat.setItemNumber(invoiceAgregateSubcat.getInvoiceLinesToAssociate().size());
        putTaxInvoiceAgregate(billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoice, invoiceAgregateSubcat, invoiceRounding, invoiceRoundingMode);
        invoiceLines = invoiceAgregateSubcat.getInvoiceLinesToAssociate();

        if (invoice.getId() == null) {
            create(invoice);
        } else {
            getEntityManager().persist(invoiceAgregateSubcat);
        }
        for (InvoiceLine invoiceLine : invoiceLines) {
            if (invoiceLine.getId() == null) {
                getEntityManager().persist(invoiceLine);
            } else {
                getEntityManager().merge(invoiceLine);
            }
        }
    }

    private Invoice finaliseInvoiceCreation(org.meveo.apiv2.billing.Invoice ressource, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, Invoice invoice, Boolean isAutoValidation,
            Boolean isIncludeBalance) {
        invoice.setAmountWithoutTax(round(invoice.getAmountWithoutTax(), invoiceRounding, invoiceRoundingMode));
        invoice.setAmountTax(round(invoice.getAmountTax(), invoiceRounding, invoiceRoundingMode));
        invoice.setAmountWithTax(round(invoice.getAmountWithTax(), invoiceRounding, invoiceRoundingMode));

        BigDecimal netToPay = invoice.getAmountWithTax();
        if (!isEnterprise && isIncludeBalance != null && isIncludeBalance) {
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
        if (isAutoValidation == null || isAutoValidation) {
            invoice = serviceSingleton.assignInvoiceNumberVirtual(invoice);
        }
        this.postCreate(invoice);
        return invoice;
    }

    private Map<InvoiceSubCategory, List<InvoiceLine>> extractMappedInvoiceLinesTolink(org.meveo.apiv2.billing.Invoice invoiceRessource, BillingAccount billingAccount) {
        List<Long> invoiceLinesIdsTolink = invoiceRessource.getInvoiceLinesTolink();
        List<InvoiceLine> invoiceLinesTolink = null;
        if (CollectionUtils.isNotEmpty(invoiceLinesIdsTolink)) {
            Set<Long> uniqueIds = new HashSet<>();
            invoiceLinesIdsTolink.removeIf(id -> !uniqueIds.add(id));
            if (uniqueIds.size() != invoiceLinesIdsTolink.size()) {
                throw new BusinessException("duplicated values on list of invoiceLinesTolink: " + invoiceLinesIdsTolink.toString());
            }
            invoiceLinesTolink = invoiceLinesService.listByBillingAccountAndIDs(billingAccount.getId(), uniqueIds);
            if (invoiceLinesTolink == null || invoiceLinesTolink.size() != uniqueIds.size()) {
                Set<Long> matchedIds = invoiceLinesTolink.stream().map(x -> x.getId()).collect(Collectors.toSet());
                uniqueIds.removeIf(id -> !matchedIds.add(id));
                throw new BusinessException("invoiceLinesTolink contains invalid Ids: " + uniqueIds.toString());
            }
            return invoiceLinesTolink.stream().collect(Collectors.groupingBy(x -> x.getAccountingArticle().getInvoiceSubCategory()));
        }
        return new HashMap<InvoiceSubCategory, List<InvoiceLine>>();
    }

    private void createAndLinkILsFromDTO(Seller seller, BillingAccount billingAccount, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, Invoice invoice, UserAccount userAccount,
            org.meveo.apiv2.billing.SubCategoryInvoiceAgregate subCatInvAgr, InvoiceSubCategory invoiceSubCategory, SubCategoryInvoiceAgregate invoiceAgregateSubcat) {
        if (subCatInvAgr.getInvoiceLines() != null) {
            for (org.meveo.apiv2.billing.InvoiceLine invoiceLineRessource : subCatInvAgr.getInvoiceLines()) {
                InvoiceLine il = invoiceLinesService.initInvoiceLineFromRessource(invoiceLineRessource, null);
                linkIL(invoice, invoiceAgregateSubcat, il, isEnterprise);
            }
        }
    }

    private void linkIL(Invoice invoice, SubCategoryInvoiceAgregate invoiceAggregateSubCat, InvoiceLine il, boolean isEnterprise) {
        il.setStatus(InvoiceLineStatusEnum.BILLED);
        il.setInvoice(invoice);
        il.getAccountingArticle().setInvoiceSubCategory(invoiceAggregateSubCat.getInvoiceSubCategory());
        il.setInvoiceAggregateF(invoiceAggregateSubCat);
        invoiceAggregateSubCat.addInvoiceLine(il, isEnterprise, false);
        addILAmountsToSubcategoryInvoiceAggregate(invoiceAggregateSubCat, il);
    }

    private void addILAmountsToSubcategoryInvoiceAggregate(SubCategoryInvoiceAgregate invoiceAgregateSubcat, InvoiceLine il) {
        invoiceAgregateSubcat.addAmountWithoutTax(il.getAmountWithoutTax());
        invoiceAgregateSubcat.addAmountTax(il.getAmountTax());
        invoiceAgregateSubcat.addAmountWithTax(il.getAmountWithTax());
    }

    private void linkExistingILs(org.meveo.apiv2.billing.Invoice invoiceResource, Map<InvoiceSubCategory, List<InvoiceLine>> existingILsTolinkMap, boolean isEnterprise, Invoice invoice, UserAccount userAccount,
            InvoiceSubCategory invoiceSubCategory, SubCategoryInvoiceAgregate invoiceAgregateSubcat) {
        List<InvoiceLine> ilsToLink = new ArrayList<>();
        if (invoiceResource.getInvoiceTypeCode().equals(invoiceTypeService.getCommercialCode())) {
            ilsToLink = invoiceLinesService.findOpenILbySubCat(invoiceSubCategory);
            removeILsFromExistingILsToLink(existingILsTolinkMap, ilsToLink);
        } else if (!existingILsTolinkMap.isEmpty() && existingILsTolinkMap.containsKey(invoiceSubCategory)) {
            ilsToLink = existingILsTolinkMap.remove(invoiceSubCategory);
        }

        for (InvoiceLine il : ilsToLink) {
            linkIL(invoice, invoiceAgregateSubcat, il, isEnterprise);
        }
    }

    private void removeILsFromExistingILsToLink(Map<InvoiceSubCategory, List<InvoiceLine>> existingILsTolinkMap, List<InvoiceLine> ilsToLink) {
        List<InvoiceSubCategory> invoicesToRemove = new ArrayList<>();
        for (InvoiceSubCategory invSubCat : existingILsTolinkMap.keySet()) {
            List<InvoiceLine> invoiceLines = existingILsTolinkMap.get(invSubCat);
            for (InvoiceLine ilToLink : ilsToLink) {
                invoiceLines.remove(ilToLink);
            }
            if (invoiceLines.isEmpty())
                invoicesToRemove.add(invSubCat);
        }
        for (InvoiceSubCategory invoiceSubCategory : invoicesToRemove)
            existingILsTolinkMap.remove(invoiceSubCategory);
    }

    private void linkILsHavingCategoryOutOfInput(BillingAccount billingAccount, boolean isEnterprise, Auditable auditable, Map<InvoiceSubCategory, List<InvoiceLine>> existingRtsTolinkMap,
            Map<InvoiceCategory, List<InvoiceSubCategory>> subCategoryMap, Invoice invoice, Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap, int invoiceRounding, RoundingModeEnum invoiceRoundingMode) {
        if (!subCategoryMap.isEmpty()) {
            for (InvoiceCategory invoiceCategory : subCategoryMap.keySet()) {
                List<InvoiceSubCategory> subCategories = subCategoryMap.get(invoiceCategory);
                UserAccount userAccount = billingAccount.getUsersAccounts().get(0);
                CategoryInvoiceAgregate invoiceAgregateCat = initCategoryInvoiceAgregate(billingAccount, auditable, invoice, userAccount, invoiceCategory, subCategories.size(), invoiceCategory.getDescription());
                linkILsAndSubCats(billingAccount, taxInvoiceAgregateMap, isEnterprise, invoiceRounding, invoiceRoundingMode, auditable, existingRtsTolinkMap, invoice, userAccount, invoiceAgregateCat, subCategories);
                addCategoryAmountsToInvoice(invoice, invoiceAgregateCat);
            }
        }
    }

    /**
     * @param toUpdate invoice to update
     * @param input
     * @param invoiceResource invoice resource
     * @return Updated invoice
     */
    public Invoice update(Invoice toUpdate, Invoice input, org.meveo.apiv2.billing.Invoice invoiceResource) {
        toUpdate = refreshOrRetrieve(toUpdate);
        final InvoiceStatusEnum status = toUpdate.getStatus();
        if (!(InvoiceStatusEnum.REJECTED.equals(status) || InvoiceStatusEnum.SUSPECT.equals(status) || InvoiceStatusEnum.DRAFT.equals(status) || InvoiceStatusEnum.NEW.equals(status))) {
            throw new BusinessException("Can only update invoices in statuses NEW/DRAFT/SUSPECT/REJECTED");
        }
        if (input.getComment() != null) {
            toUpdate.setComment(input.getComment());
        }
        if (input.getExternalRef() != null) {
            toUpdate.setExternalRef(input.getExternalRef());
        }
        if (input.getInvoiceDate() != null) {
            toUpdate.setInvoiceDate(input.getInvoiceDate());
        }
        if (input.getDueDate() != null) {
            toUpdate.setDueDate(input.getDueDate());
        }
        if (invoiceResource.getPaymentMethod() != null) {
            final Long pmId = invoiceResource.getPaymentMethod().getId();
            PaymentMethod pm = (PaymentMethod) tryToFindByEntityClassAndId(PaymentMethod.class, pmId);
            toUpdate.setPaymentMethod(pm);
        }
        if (invoiceResource.getListLinkedInvoices() != null) {
            for (Long invoiceId : invoiceResource.getListLinkedInvoices()) {
                Invoice invoiceTmp = findById(invoiceId);
                if (invoiceTmp == null) {
                    throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
                }
                if (!toUpdate.getInvoiceType().getAppliesTo().contains(invoiceTmp.getInvoiceType())) {
                    throw new BusinessApiException("InvoiceId " + invoiceId + " cant be linked");
                }
                toUpdate.getLinkedInvoices().add(invoiceTmp);
            }
        }

        if (invoiceResource.getOrder() != null) {
            final Long orderId = invoiceResource.getOrder().getId();
            Order order = (Order) tryToFindByEntityClassAndId(Order.class, orderId);
            toUpdate.setOrder(order);
        }

        if (invoiceResource.getDiscountPlan() != null) {
            final Long dpId = invoiceResource.getDiscountPlan().getId();
            DiscountPlan discountPlan = (DiscountPlan) tryToFindByEntityClassAndId(DiscountPlan.class, dpId);
            toUpdate.setDiscountPlan(discountPlan);
        }

        if (invoiceResource.getCommercialOrder() != null) {
            final Long commercialOrderId = invoiceResource.getCommercialOrder().getId();
            CommercialOrder commercialOrder = (CommercialOrder) tryToFindByEntityClassAndId(CommercialOrder.class, commercialOrderId);
            toUpdate.setCommercialOrder(commercialOrder);
        }

        if (input.getCfValues() != null) {
            toUpdate.setCfValues(input.getCfValues());
        }
        return update(toUpdate);
    }

    /**
     * @param paginationConfiguration
     * @return
     */
    public List<Invoice> listWithlinkedInvoices(PaginationConfiguration paginationConfiguration) {
        List<String> fetchFields = paginationConfiguration.getFetchFields();
        if (fetchFields == null) {
            fetchFields = Arrays.asList("linkedInvoices");
        } else if (!fetchFields.contains("linkedInvoices")) {
            fetchFields.add("linkedInvoices");
        }
        paginationConfiguration.setFetchFields(fetchFields);
        return list(paginationConfiguration);
    }

    /**
     * Detach AO From invoice.
     *
     * @param ao account operation
     */
    public void detachAOFromInvoice(AccountOperation ao) {
        getEntityManager().createNamedQuery("Invoice.detachAOFromInvoice").setParameter("ri", ao).executeUpdate();
    }

    /**
     * @param invoice
     * @return
     */
    public Object calculateInvoice(Invoice invoice) {
        invoice = invoiceService.retrieveIfNotManaged(invoice);
        final BillingAccount billingAccount = billingAccountService.retrieveIfNotManaged(invoice.getBillingAccount());
        return createAggregatesAndInvoiceFromIls(billingAccount, billingAccount.getBillingRun(), null, invoice.getInvoiceDate(), null, null, invoice.isDraft(), billingAccount.getBillingCycle(), billingAccount,
            billingAccount.getPaymentMethod(), invoice.getInvoiceType(), null, false, false, invoice);
    }

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    private final String TAX_INVOICE_AGREGATE = "T";
    private final String CATEGORY_INVOICE_AGREGATE = "R";
    private final String SUBCATEGORY_INVOICE_AGREGATE = "F";

    public Invoice duplicate(Invoice invoice) {
        invoice = refreshOrRetrieve(invoice);

        invoice.getInvoiceAgregates().size();
        invoice.getOrders().size();
        if (invoice.getInvoiceLines() != null)
            invoice.getInvoiceLines().size();

        var invoiceAgregates = new ArrayList<>(invoice.getInvoiceAgregates());
        var invoiceLines = new ArrayList<>(invoice.getInvoiceLines());

        detach(invoice);

        var duplicateInvoice = new Invoice(invoice);
        this.create(duplicateInvoice);

        if (!invoiceAgregates.isEmpty()) {
            for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {

                invoiceAgregateService.detach(invoiceAgregate);

                switch (invoiceAgregate.getDescriminatorValue()) {
                case TAX_INVOICE_AGREGATE: {
                    var taxInvoiceAgregate = new TaxInvoiceAgregate((TaxInvoiceAgregate) invoiceAgregate);
                    taxInvoiceAgregate.setInvoice(duplicateInvoice);
                    invoiceAgregateService.create(taxInvoiceAgregate);
                    break;
                }
                case CATEGORY_INVOICE_AGREGATE: {
                    var categoryInvoiceAgregate = new CategoryInvoiceAgregate((CategoryInvoiceAgregate) invoiceAgregate);
                    categoryInvoiceAgregate.setInvoice(duplicateInvoice);
                    invoiceAgregateService.create(categoryInvoiceAgregate);
                    break;
                }
                case SUBCATEGORY_INVOICE_AGREGATE: {
                    var subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate((SubCategoryInvoiceAgregate) invoiceAgregate);
                    subCategoryInvoiceAgregate.setInvoice(duplicateInvoice);
                    invoiceAgregateService.create(subCategoryInvoiceAgregate);
                    break;
                }
                }
            }
        }

        if (invoiceLines != null) {
            for (InvoiceLine invoiceLine : invoiceLines) {
                invoiceLinesService.detach(invoiceLine);
                var duplicateInvoiceLine = new InvoiceLine(invoiceLine, duplicateInvoice);
                invoiceLinesService.create(duplicateInvoiceLine);
            }
        }

        return duplicateInvoice;
    }

    public IBillableEntity getBillableEntity(String targetCode, String targetType, String orderNumber, String billingAccountCode) {
        IBillableEntity entity = null;
        if (StringUtils.isBlank(billingAccountCode)) {
            if (BillingEntityTypeEnum.BILLINGACCOUNT.toString().equalsIgnoreCase(targetType)) {
                entity = billingAccountService.findByCode(targetCode, asList("billingRun"));
            } else if (BillingEntityTypeEnum.SUBSCRIPTION.toString().equalsIgnoreCase(targetType)) {
                entity = subscriptionService.findByCode(targetCode, asList("billingRun"));
            } else if (BillingEntityTypeEnum.ORDER.toString().equalsIgnoreCase(targetType)) {
                entity = orderService.findByCodeOrExternalId(targetCode);
            }
        } else {
            if (!StringUtils.isBlank(orderNumber)) {
                entity = orderService.findByCodeOrExternalId(orderNumber);
            } else {
                entity = billingAccountService.findByCode(billingAccountCode, asList("billingRun"));
            }
        }
        return entity;
    }
}