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

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static java.util.Comparator.comparingInt;
import static java.util.Optional.ofNullable;
import static java.util.Set.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.meveo.commons.utils.NumberUtils.round;
import static org.meveo.service.base.ValueExpressionWrapper.VAR_BILLING_ACCOUNT;
import static org.meveo.service.base.ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT;
import static org.meveo.service.base.ValueExpressionWrapper.VAR_DISCOUNT_PLAN_INSTANCE;
import static org.meveo.service.base.ValueExpressionWrapper.VAR_INVOICE;
import static org.meveo.service.base.ValueExpressionWrapper.VAR_INVOICE_SHORT;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jakarta.xml.bind.JAXBException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ConfigurationException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.admin.util.PdfWaterMark;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.billing.QuarantineBillingRunDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.generics.GenericRequestMapper;
import org.meveo.api.generics.PersistenceServiceHelper;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.InvoiceLineRTs;
import org.meveo.apiv2.billing.InvoiceLinesToReplicate;
import org.meveo.apiv2.billing.RejectReasonInput;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.InvoiceNumberAssigned;
import org.meveo.event.qualifier.InvoicePaymentStatusUpdated;
import org.meveo.event.qualifier.PDFGenerated;
import org.meveo.event.qualifier.Updated;
import org.meveo.event.qualifier.XMLGenerated;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AdjustmentStatusEnum;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.DiscountPlanInstanceStatusEnum;
import org.meveo.model.billing.EvaluationModeEnum;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.IInvoiceable;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.InvoiceLineTaxModeEnum;
import org.meveo.model.billing.InvoiceLinesGroup;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceProcessTypeEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeEnum;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.model.billing.LinkedInvoice;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionAction;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.ReferenceDateEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.SubcategoryInvoiceAgregateAmount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.ValidationRuleTypeEnum;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.model.order.Order;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceLineService.InvoiceLineCreationStatistics;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.InternationalSettingsService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.order.OpenOrderService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.script.billing.TaxScriptService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.service.tax.TaxMappingService;
import org.w3c.dom.Node;

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
    @InvoicePaymentStatusUpdated
    protected Event<Invoice> invoicePaymentStatusUpdated;
    
    @Inject
    private FilterService filterService;

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

    @Inject
    private OpenOrderService openOrderService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private MatchingCodeService matchingCodeService;

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
    private static Map<String, JasperReport> jasperReportMap = new HashMap<>();

    /**
     * Description translation map.
     */
    private Map<String, String> descriptionMap = new HashMap<>();

    private int rtPaginationSize = 30000;

    @Inject
    private CpqQuoteService cpqQuoteService;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private DiscountPlanItemService discountPlanItemService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobInstanceService jobInstanceService;
    
    @Inject 
    private LinkedInvoiceService linkedInvoiceService;
    
    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private InternationalSettingsService internationalSettingsService;

    @Inject
    private FinanceSettingsService financeSettingsService;
    
    private enum RuleValidationEnum {
        RULE_TRUE, RULE_FALSE, RULE_IGNORE
    }

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
            return emptyList();
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
            return emptyList();
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
        return emptyList();
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
            qb.addSql("i.status = 'VALIDATED' ");
            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to get invoices with amount and with no account operation", ex);
        }
        return emptyList();
    }

    /**
     * Get rated transactions for entity grouped by billing account, seller, invoice type and payment method
     *
     * @param entityToInvoice entity to be billed
     * @param billingAccount Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each rated transaction.
     * @param billingRun billing run
     * @param defaultBillingCycle Billing cycle applicable to billable entity or to billing run
     * @param defaultInvoiceType Invoice type. A default invoice type for postpaid rated transactions. In case of prepaid RTs, a prepaid invoice type is used. Provided in case of Billing account or Subscription billable
     *        entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @param isDraft Is it a draft invoice
     * @param defaultPaymentMethod The default payment method
     * @param dataToInvoice A scrollable resultset of data to invoice
     * @return List of rated transaction groups for entity and a flag indicating if there are more Rated transactions to retrieve
     * @throws BusinessException BusinessException
     */
    protected RatedTransactionsToInvoice getRatedTransactionGroups(IBillableEntity entityToInvoice, BillingAccount billingAccount, BillingRun billingRun, BillingCycle defaultBillingCycle, InvoiceType defaultInvoiceType,
            boolean isDraft, PaymentMethod defaultPaymentMethod, Iterator<? extends IInvoiceable> dataToInvoice) throws BusinessException {

        // Instantiated invoices. Key ba.id_seller.id_invoiceType.id
        Map<String, RatedTransactionGroup> rtGroups = new HashMap<>();

        BillingCycle billingCycle = defaultBillingCycle;
        InvoiceType postPaidInvoiceType = defaultInvoiceType;
        PaymentMethod paymentMethod = defaultPaymentMethod;
        if (defaultPaymentMethod == null && billingAccount != null) {
            defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
        }

        // Split RTs billing account groups to billing account/seller groups
        int i = 0;
        while (i < rtPaginationSize && dataToInvoice.hasNext()) {
            i++;
            IInvoiceable invoiceable = dataToInvoice.next();

            // Order can span multiple billing accounts and some Billing account-dependent values have to be recalculated
            if (entityToInvoice instanceof Order) {
                // Retrieve BA and determine postpaid invoice type only if it has not changed from the last iteration
                if (billingAccount == null || !billingAccount.getId().equals(invoiceable.getBillingAccountId())) {
                    billingAccount = billingAccountService.findById(invoiceable.getBillingAccountId(), Arrays.asList("customerAccount"));
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
            boolean isPrepaid = invoiceable.isPrepaid();
            if (isPrepaid) {
                invoiceType = determineInvoiceType(true, isDraft, null, null, null);
            }

            paymentMethod = resolvePaymentMethod(billingAccount, billingCycle, defaultPaymentMethod, invoiceable);

            String invoiceKey = billingAccount.getId() + "_" + invoiceable.getSellerId() + "_" + invoiceType.getId() + "_" + isPrepaid + ((paymentMethod == null) ? "" : "_" + paymentMethod.getId());
            RatedTransactionGroup rtGroup = rtGroups.get(invoiceKey);

            if (rtGroup == null) {
                rtGroup = new RatedTransactionGroup(billingAccount, invoiceable.getSellerId(), billingCycle != null ? billingCycle : billingAccount.getBillingCycle(), invoiceType, isPrepaid, invoiceKey, paymentMethod);
                rtGroups.put(invoiceKey, rtGroup);
            }
            rtGroup.getInvoiceables().add(invoiceable);

        }

        boolean moreRts = i == rtPaginationSize; // Check if rtScrollResultset.isLast() is not a better option

        if (log.isDebugEnabled()) {
            log.debug("Split {} RTs for {}/{} in to billing account/seller/invoice type groups. {} RTs to retrieve.", i, entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId(), moreRts ? "More" : "No more");
        }
        List<RatedTransactionGroup> convertedRtGroups = new ArrayList<>();

        // Check if any script to run to group rated transactions by invoice type or other parameters. Script accepts a RatedTransaction list object as an input.
        for (RatedTransactionGroup rtGroup : rtGroups.values()) {

            if (rtGroup.getBillingCycle().getScriptInstance() != null) {
                convertedRtGroups
                    .addAll(executeBCScript(billingRun, rtGroup.getInvoiceType(), rtGroup.getInvoiceables(), entityToInvoice, rtGroup.getBillingCycle().getScriptInstance().getCode(), rtGroup.getPaymentMethod()));
            } else {
                convertedRtGroups.add(rtGroup);
            }
        }
        return new RatedTransactionsToInvoice(moreRts, convertedRtGroups);
    }

    private PaymentMethod resolvePaymentMethod(BillingAccount billingAccount, BillingCycle billingCycle, PaymentMethod defaultPaymentMethod, IInvoiceable dataToInvoice) {
        if (BillingEntityTypeEnum.SUBSCRIPTION.equals(billingCycle.getType()) || (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && billingCycle.isSplitPerPaymentMethod())) {
            Subscription subscription = subscriptionService.findById(dataToInvoice.getSubscriptionId(), Arrays.asList("paymentMethod"));

            if (subscription.getPaymentMethod() != null) {
                return subscription.getPaymentMethod();
            } else if (billingAccount.getPaymentMethod() != null) {
                return billingAccount.getPaymentMethod();
            }
        }
        if (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && (!billingCycle.isSplitPerPaymentMethod() && Objects.nonNull(billingAccount.getPaymentMethod()))) {
            return billingAccount.getPaymentMethod();
        }
        return defaultPaymentMethod;
    }

    /**
     * Get a scrollable resultset of rated transactions to invoice
     *
     * @param entityToInvoice Entity to invoice
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Invoice up to date
     * @return A scrollable hibernate resultset of InvoiceableData objects (short version of Rates transaction)
     */
    private ScrollableResults getDataToInvoiceFromRTs(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate) {

        org.hibernate.query.Query<InvoiceableData> query = null;
        if (entityToInvoice instanceof Subscription) {
            query = getEntityManager().unwrap(Session.class).createNamedQuery("RatedTransaction.listToInvoiceBySubscriptionInvoiceableData", InvoiceableData.class).setParameter("subscriptionId", entityToInvoice.getId())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("invoiceUpToDate", invoiceUpToDate);

        } else if (entityToInvoice instanceof BillingAccount) {
            query = getEntityManager().unwrap(Session.class).createNamedQuery("RatedTransaction.listToInvoiceByBillingAccountInvoiceableData", InvoiceableData.class).setParameter("billingAccountId", entityToInvoice.getId())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("invoiceUpToDate", invoiceUpToDate);

        } else if (entityToInvoice instanceof Order) {
            query = getEntityManager().unwrap(Session.class).createNamedQuery("RatedTransaction.listToInvoiceByOrderNumberInvoiceableData", InvoiceableData.class).setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("invoiceUpToDate", invoiceUpToDate);
        }

        query.setFetchSize(rtPaginationSize);
        query.setReadOnly(true);
        query.setLockMode("a", LockMode.NONE);
        ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

        return scroll;
    }

    /**
     * Get a list of rated transactions to invoice
     *
     * @param entityToInvoice Entity to invoice
     * @param ratedTransactionFilter Filter to retrieve rated transactions
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Invoice up to date
     * @param isDraft Shall OPEN wallet operations to be included - in case of proforma/draft invoice
     * @return A list of rated transactions
     */
    @SuppressWarnings("unchecked")
    private List<RatedTransaction> getDataToInvoiceFromFilterOrDraft(IBillableEntity entityToInvoice, Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate,
            boolean isDraft) {

        List<RatedTransaction> rts = null;
        if (ratedTransactionFilter != null) {
            rts = (List<RatedTransaction>) filterService.filteredListAsObjects(ratedTransactionFilter, null);
        }

        // Add unrated wallet operations if in draft mode. No need to change WO status to billed as invoice will be canceled afterwards
        if (isDraft) {
            if (rts == null) {
                rts = new ArrayList<>();
            }
            rts.addAll(walletOperationService.listToRate(entityToInvoice, invoiceUpToDate).stream()
                .filter(wo -> wo.getOperationDate().before(lastTransactionDate) && (wo.getOperationDate().after(firstTransactionDate) || wo.getOperationDate().equals(firstTransactionDate))).map(RatedTransaction::new)
                .collect(Collectors.toList()));
        }

        return rts;
    }

    private List<RatedTransaction> getRatedTransactions(IBillableEntity entityToInvoice, Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate, boolean isDraft) {
        List<RatedTransaction> ratedTransactions = ratedTransactionService.listRTsToInvoice(entityToInvoice, firstTransactionDate, lastTransactionDate, invoiceUpToDate, ratedTransactionFilter, rtPaginationSize);
        // Seen with the PO and Architect, this boolean should only be taken into account to change the status of the invoice (draft or validated). more detail on https://opencellsoft.atlassian.net/browse/INTRD-178
        // The recovery of OPEN walletOperation should only be done by API with a specific boolean: to be treated in possibly another issue
        // For the moment, we avoid recovering the Open WO which distorts the calculation of the subscription to be linked for the future invoice (seee getSubscriptionFromRT)
        /*// if draft add unrated wallet operation
        if (isDraft) {
            ratedTransactions.addAll(getDraftRatedTransactions(entityToInvoice, firstTransactionDate, lastTransactionDate, invoiceUpToDate));
        }*/
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
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createAgregatesAndInvoiceInNewTransaction(IBillableEntity entityToInvoice, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate, Date firstTransactionDate,
            Date lastTransactionDate, MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck) throws BusinessException {
        createAgregatesAndInvoice(entityToInvoice, billingRun, ratedTransactionFilter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, automaticInvoiceCheck);
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
            InvoiceType invoiceType = null;

            if (entityToInvoice instanceof Order) {
                paymentMethod = ((Order) entityToInvoice).getPaymentMethod();

            } else {
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
                automaticInvoiceCheck);

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
     * @param defaultBillingCycle Billing cycle applicable to billable entity or to billing run. For Order, if not provided at order level, will have to be determined from Order's billing account.
     * @param billingAccount Billing account. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each rated
     *        transaction.
     * @param defaultPaymentMethod Payment method. Provided in case of Billing account or Subscription billable entity type. Order can span multiple billing accounts and therefore will be determined for each billing
     *        account occurrence.
     * @param defaultInvoiceType Invoice type. A default invoice type for postpaid rated transactions. In case of prepaid RTs, a prepaid invoice type is used. Provided in case of Billing account or Subscription billable
     *        entity type. Order can span multiple billing accounts and therefore will be determined for each billing account occurrence.
     * @return A list of invoices
     * @throws BusinessException General business exception
     */
    private List<Invoice> createAggregatesAndInvoiceFromRTs(IBillableEntity entityToInvoice, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            boolean isDraft, BillingCycle defaultBillingCycle, BillingAccount billingAccount, PaymentMethod defaultPaymentMethod, InvoiceType defaultInvoiceType, boolean automaticInvoiceCheck) throws BusinessException {

        // Contains distinct Invoice information - one for each invoice produced. Map key is billingAccount.id_seller.id_invoiceType.id_isPrepaid
        Map<String, InvoiceAggregateProcessingInfo> rtGroupToInvoiceMap = new HashMap<>();

        if (entityToInvoice instanceof Order) {
            billingAccount = null;
            defaultInvoiceType = null;
        }

        // Invoice from rated transactions
        if (ratedTransactionFilter == null) {
            ScrollableResults rtScrollResultset = getDataToInvoiceFromRTs(entityToInvoice, firstTransactionDate, lastTransactionDate, invoiceDate);

            Iterator<IInvoiceable> dataToInvoiceIterator = new Iterator<IInvoiceable>() {
                @Override
                public boolean hasNext() {
                    return rtScrollResultset.next();
                }

                public IInvoiceable next() {
                    return (IInvoiceable) rtScrollResultset.get(0);
                }

            };

            while (true) {

                // Retrieve Rated transactions and split them into BA/seller combinations
                RatedTransactionsToInvoice rtsToInvoice = getRatedTransactionGroups(entityToInvoice, billingAccount, billingRun, defaultBillingCycle, defaultInvoiceType, isDraft, defaultPaymentMethod,
                    dataToInvoiceIterator);

                aggregateInvoiceables(rtGroupToInvoiceMap, rtsToInvoice.ratedTransactionGroups, entityToInvoice, billingRun, invoiceDate, isDraft, automaticInvoiceCheck);

                if (!rtsToInvoice.moreRatedTransactions) {
                    rtScrollResultset.close();
                    break;
                }
            }
        }

        // Invoice from filter and/or wallet operations in a draft mode
        if (ratedTransactionFilter != null || isDraft) {

            // Retrieve Rated transactions and split them into BA/seller combinations
            List<RatedTransaction> rts = getDataToInvoiceFromFilterOrDraft(entityToInvoice, ratedTransactionFilter, firstTransactionDate, lastTransactionDate, invoiceDate, isDraft);
            if (!rts.isEmpty()) {
                if (billingRun.isExceptionalBR()) {
                    rts = rts.stream().filter(rt -> (rt.getStatus() == RatedTransactionStatusEnum.OPEN && rt.getBillingRun() == null)).collect(toList());
                }
                RatedTransactionsToInvoice rtsToInvoice = getRatedTransactionGroups(entityToInvoice, billingAccount, billingRun, defaultBillingCycle, defaultInvoiceType, isDraft, defaultPaymentMethod, rts.iterator());
                aggregateInvoiceables(rtGroupToInvoiceMap, rtsToInvoice.ratedTransactionGroups, entityToInvoice, billingRun, invoiceDate, isDraft, automaticInvoiceCheck);
            }
        }

        if (rtGroupToInvoiceMap.isEmpty()) {
            log.warn("Account {}/{} has no billable transactions", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());
            return new ArrayList<>();
        }

        EntityManager em = getEntityManager();
        em.flush(); // Need to flush, so RTs can be updated in mass

        // Mass update RTs with status and invoice info
        em.createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoFromPendingTable" + (EntityManagerProvider.isDBOracle() ? "Oracle" : "")).executeUpdate();
        em.createNamedQuery("RatedTransaction.deletePendingTable").executeUpdate();

        // Finalize invoices
        Map<Long, BigDecimal> customerBalances = new HashMap<>();

        boolean isBalanceDue = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.due", true);
        boolean isBalanceLitigation = ParamBean.getInstance().getPropertyAsBoolean("invoice.balance.litigation", false);

        List<Invoice> invoiceList = new ArrayList<>();
        for (InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo : rtGroupToInvoiceMap.values()) {
            Invoice invoice = invoiceAggregateProcessingInfo.invoice;

            invoiceList.add(invoice);

            // Create discount, category and tax aggregates
            addDiscountCategoryAndTaxAggregates(invoice, invoiceAggregateProcessingInfo.subCategoryAggregates.values());

            // Link orders to invoice
            Set<String> orderNums = invoiceAggregateProcessingInfo.orderNumbers;

            if (entityToInvoice instanceof Order) {
                orderNums.add(((Order) entityToInvoice).getOrderNumber());

                if (orderNums != null && !orderNums.isEmpty()) {
                List<Order> orders = orderService.findByCodeOrExternalId(orderNums);
                if (!orders.isEmpty()) {
                    invoice.setOrders(orders);
                }
            }

            } else if (entityToInvoice instanceof CommercialOrder)
            {
                orderNums.add(((CommercialOrder) entityToInvoice).getOrderNumber());

                if (orderNums != null && !orderNums.isEmpty()) {
                List<CommercialOrder> orders = commercialOrderService.findByCodeOrExternalId(orderNums);
                if (!orders.isEmpty()) {
                    invoice.setCommercialOrder(orders.get(0));
                }
            }
            }


            BigDecimal balance = customerBalances.get(billingAccount.getCustomerAccount().getId());
            if (balance == null) {

                // Balance are calculated on CA level and will be the same for all rated transactions of same order
                if (isBalanceLitigation) {
                    balance = customerAccountService.customerAccountBalanceDue(invoice.getBillingAccount().getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                } else {
                    balance = customerAccountService.customerAccountBalanceDueWithoutLitigation(invoice.getBillingAccount().getCustomerAccount(), isBalanceDue ? invoiceDate : null);
                }

                customerBalances.put(billingAccount.getCustomerAccount().getId(), balance);
            }

            // Set due balance
            invoice.setDueBalance(balance.setScale(appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode().getRoundingMode()));

            invoice.assignTemporaryInvoiceNumber();

            applyAutomaticInvoiceCheck(invoice, automaticInvoiceCheck);

            postCreate(invoice);
        }
        return invoiceList;

    }

    private void aggregateInvoiceables(Map<String, InvoiceAggregateProcessingInfo> rtGroupToInvoiceMap, List<RatedTransactionGroup> ratedTransactionGroupsPaged, IBillableEntity entityToInvoice, BillingRun billingRun,
            Date invoiceDate, boolean isDraft, boolean automaticInvoiceCheck) {

        // Process invoiceables

        EntityManager em = getEntityManager();

        // Process each BA/seller/invoiceType combination separately, what corresponds to a separate invoice
        for (RatedTransactionGroup rtGroup : ratedTransactionGroupsPaged) {

            String invoiceKey = rtGroup.getInvoiceKey();

            InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo = rtGroupToInvoiceMap.get(invoiceKey);
            if (invoiceAggregateProcessingInfo == null) {

                invoiceAggregateProcessingInfo = new InvoiceAggregateProcessingInfo();

                invoiceAggregateProcessingInfo.invoice = instantiateInvoice(entityToInvoice, rtGroup.getBillingAccount(), rtGroup.getSellerId(), billingRun, invoiceDate, isDraft, rtGroup.getBillingCycle(),
                    rtGroup.getPaymentMethod(), rtGroup.getInvoiceType(), rtGroup.isPrepaid(), automaticInvoiceCheck);

                rtGroupToInvoiceMap.put(invoiceKey, invoiceAggregateProcessingInfo);
            }

            Invoice invoice = invoiceAggregateProcessingInfo.invoice;

            // Create aggregates.
            // Indicate that no more RTs to process only in case when all RTs were retrieved for processing in a single query page.
            // In other case - need to close invoices when all RTs are processed
            appendInvoiceAgregates(entityToInvoice, rtGroup.getBillingAccount(), invoice, rtGroup.getInvoiceables(), false, invoiceAggregateProcessingInfo, false);

            // Save invoice and its aggregates during the first pagination run, or save only newly created aggregates during later pagination runs
            if (invoice.getId() == null) {
                setInvoiceDueDate(invoice, rtGroup.getBillingCycle());
                setInitialCollectionDate(invoice, rtGroup.getBillingCycle(), billingRun);
                this.create(invoice);

            } else {
                for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
                    if (invoiceAggregate.getId() == null) {
                        em.persist(invoiceAggregate);
                    }
                }
            }

            String rtPendingTableName = NativePersistenceService.addCurrentSchema("billing_rated_transaction_pending", currentUser.getProviderCode());
            String rtPendingInsertSql = "insert into  " + rtPendingTableName
                    + "(id,aggregate_id_f,invoice_id,billing_run_id,unit_amount_without_tax,unit_amount_with_tax,unit_amount_tax,amount_without_tax,amount_with_tax,amount_tax,tax_id,tax_percent)values(?,?,?,?,?,?,?,?,?,?,?,?)";

            // Prepare to mass update RT with status, recalculated amounts and invoice information
            for (SubCategoryInvoiceAgregate subAggregate : invoiceAggregateProcessingInfo.subCategoryAggregates.values()) {
                if (subAggregate.getInvoiceablesToAssociate() == null) {
                    continue;
                }

                Session hibernateSession = em.unwrap(Session.class);
                hibernateSession.doWork(connection -> {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(rtPendingInsertSql)) {

                        for (IInvoiceable invoiceable : subAggregate.getInvoiceablesToAssociate()) {
                            // Dont change status of wallet operations that were converted to transactions in case of draft invoice
                            if (invoiceable.getId() == null) {
                                continue;
                            }
                            preparedStatement.setLong(1, invoiceable.getId());
                            preparedStatement.setLong(2, subAggregate.getId());
                            preparedStatement.setLong(3, invoice.getId());
                            if (billingRun != null) {
                                preparedStatement.setLong(4, billingRun.getId());
                            } else {
                                preparedStatement.setNull(4, Types.BIGINT);
                            }
                            preparedStatement.setBigDecimal(5, invoiceable.getUnitAmountWithoutTax());
                            preparedStatement.setBigDecimal(6, invoiceable.getUnitAmountWithTax());
                            preparedStatement.setBigDecimal(7, invoiceable.getUnitAmountTax());
                            preparedStatement.setBigDecimal(8, invoiceable.getAmountWithoutTax());
                            preparedStatement.setBigDecimal(9, invoiceable.getAmountWithTax());
                            preparedStatement.setBigDecimal(10, invoiceable.getAmountTax());
                            preparedStatement.setLong(11, invoiceable.getTaxId());
                            preparedStatement.setBigDecimal(12, invoiceable.getTaxPercent());

                            preparedStatement.addBatch();

                        }

                        preparedStatement.executeBatch();

                    } catch (SQLException e) {
                        log.error("Failed to insert into billing_rated_transaction_pending", e);
                        throw e;
                    }

                });

                subAggregate.setInvoiceablesToAssociate(new ArrayList<>());

            }
        }
    }


    public void setInitialCollectionDate(Invoice invoice, BillingCycle billingCycle, BillingRun billingRun) {

        if (billingCycle != null && isBlank(billingCycle.getCollectionDateDelayEl())) {
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
        if(billingCycle != null) {
            delay = evaluateCollectionDelayExpression(billingCycle.getCollectionDateDelayEl(), billingAccount, invoice, order);
        }
        if (delay == null) {
            throw new BusinessException("collection date delay is null");
        }

        Date initialCollectionDate = DateUtils.addDaysToDate(invoice.getDueDate(), delay);

        invoice.setInitialCollectionDate(initialCollectionDate);

    }

    private Integer evaluateCollectionDelayExpression(String expression, BillingAccount billingAccount, Invoice invoice, Order order) {
        Integer result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (expression.indexOf(VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(VAR_INVOICE) >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("order") >= 0) {
            userMap.put("order", order);
        }
        Object res = evaluateExpression(expression, userMap, Integer.class);
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
    public void applyAutomaticInvoiceCheck(List<Invoice> invoiceList, boolean automaticInvoiceCheck) {
    	applyAutomaticInvoiceCheck(invoiceList, automaticInvoiceCheck, true);
    }
    
    /**
     * @param invoiceList
     */
    public void applyAutomaticInvoiceCheck(List<Invoice> invoiceList, boolean automaticInvoiceCheck, boolean save) {
        if (automaticInvoiceCheck) {
            for (Invoice invoice : invoiceList) {
                applyAutomaticInvoiceCheck(invoice, automaticInvoiceCheck, save);
            }
        }
    }

    /**
     * @param invoice
     * @param automaticInvoiceCheck
     */
    private void applyAutomaticInvoiceCheck(Invoice invoice, boolean automaticInvoiceCheck) {
    	applyAutomaticInvoiceCheck(invoice, automaticInvoiceCheck, true);
    }
    	
    /**
     * @param invoice
     * @param automaticInvoiceCheck
     */
    private void applyAutomaticInvoiceCheck(Invoice invoice, boolean automaticInvoiceCheck, boolean save) {
    	invoice = invoiceService.refreshOrRetrieve(invoice);
        if (automaticInvoiceCheck && invoice.getInvoiceType() != null &&
                (invoice.getInvoiceType().getInvoiceValidationScript() != null
                        || invoice.getInvoiceType().getInvoiceValidationRules() != null)) {
            InvoiceType invoiceType = invoiceTypeService.refreshOrRetrieve(invoice.getInvoiceType());
            if(invoice.getInvoiceType().getInvoiceValidationScript() != null) {
                ScriptInstance scriptInstance = invoice.getInvoiceType().getInvoiceValidationScript();
                if (scriptInstance != null) {
                    ScriptInterface script = scriptInstanceService.getScriptInstance(scriptInstance.getCode());
                    if (script != null) {
                        Map<String, Object> methodContext = initContext(invoice);
                        scriptInstanceService.execute(scriptInstance.getCode(), methodContext);
                        Object status = methodContext.get(Script.INVOICE_VALIDATION_STATUS);
                        if (status != null && status instanceof InvoiceValidationStatusEnum) {
                            if (InvoiceValidationStatusEnum.REJECTED.equals((InvoiceValidationStatusEnum) status)) {
                                invoice.setStatus(InvoiceStatusEnum.REJECTED);
                                invoice.setRejectReason((String) methodContext.get(Script.INVOICE_VALIDATION_REASON));

                            } else if (InvoiceValidationStatusEnum.SUSPECT.equals((InvoiceValidationStatusEnum) status)) {
                                invoice.setStatus(InvoiceStatusEnum.SUSPECT);
                                invoice.setRejectReason((String) methodContext.get(Script.INVOICE_VALIDATION_REASON));
                            }
                        }
                    }
                }
            } else if (invoiceType.getInvoiceValidationRules() != null
                    && !invoiceType.getInvoiceValidationRules().isEmpty()) {
				List<InvoiceValidationRule> invoiceValidationRules = invoice.getInvoiceType().getInvoiceValidationRules()
						.stream().filter(rule -> Objects.isNull(rule.getParentRule())).collect(Collectors.toList());
                sort(invoiceValidationRules, comparingInt(InvoiceValidationRule::getPriority));
                Iterator<InvoiceValidationRule> validationRuleIterator = invoiceValidationRules.iterator();
                RuleValidationEnum currentRuleValidation = RuleValidationEnum.RULE_TRUE;
                Map<String, Object> methodContext = initContext(invoice);
                while (validationRuleIterator.hasNext() && (currentRuleValidation != RuleValidationEnum.RULE_FALSE)) {
                    InvoiceValidationRule validationRule = validationRuleIterator.next();
                    if (DateUtils.isWithinDateWithoutTime(invoice.getInvoiceDate(), validationRule.getValidFrom(), validationRule.getValidTo())) {
                    	currentRuleValidation = evaluateRule(invoice, methodContext, validationRule);
                    	if (currentRuleValidation == RuleValidationEnum.RULE_FALSE) {
            				rejectInvoiceRule(invoice, validationRule);
            			}
                    }
                }
            }
            if(save) {
	            update(invoice);
	            commit();
            }
        }
    }

    /**
     * Evaluate an invoice for a validation rule type Script, EL or RULE_SET
     * @param invoice
     * @param methodContext
     * @param validationRule
     * @return FALSE if validation script fails, TRUE if validation success, IGNORE if null
     */
	private RuleValidationEnum evaluateRule(Invoice invoice, Map<String, Object> methodContext, InvoiceValidationRule validationRule) {
		Object validationResult = null;
		try {
			invoice.setRejectReason(null);
			if (validationRule.getType() == ValidationRuleTypeEnum.SCRIPT) {
				ScriptInterface validationRuleScript = injectScriptParameters(methodContext, validationRule);
				validationRuleScript.execute(methodContext);
				validationResult = methodContext.get(Script.INVOICE_VALIDATION_STATUS);
			} else if (validationRule.getType() == ValidationRuleTypeEnum.EXPRESSION_LANGUAGE) {
				validationResult = evaluateExpression(validationRule.getValidationEL(), Map.of("invoice", invoice), Boolean.class);
			} else if (validationRule.getType() == ValidationRuleTypeEnum.RULE_SET) {
				OperatorEnum operator = validationRule.getOperator();
				List<InvoiceValidationRule> subRules = validationRule.getSubRules();
			    sort(subRules, comparingInt(InvoiceValidationRule::getPriority));
			    Iterator<InvoiceValidationRule> subRuleIterator = subRules.iterator();
			    boolean breakValidation = false;
				List<RuleValidationEnum> ruleSetResult = new ArrayList<>();
				while(subRuleIterator.hasNext() && !breakValidation) {
					InvoiceValidationRule subRule = subRuleIterator.next();
					RuleValidationEnum eval = evaluateRule(invoice, methodContext, subRule);
					if (eval == RuleValidationEnum.RULE_IGNORE) {
						breakValidation = true;
					} else if (validationRule.getEvaluationMode() != EvaluationModeEnum.CONDITION){
						ruleSetResult.add(eval);
					}
				}
				validationResult = (ruleSetResult.isEmpty())? null : (operator == OperatorEnum.AND && !ruleSetResult.contains(RuleValidationEnum.RULE_FALSE))
						|| (operator == OperatorEnum.OR && ruleSetResult.contains(RuleValidationEnum.RULE_TRUE));
			}
		} catch (Exception exception) {
		    throw new BusinessException(exception);
		}
		return evaluateValidationRule((Boolean) validationResult, validationRule);
	}
	
	private RuleValidationEnum evaluateValidationRule(Boolean currentValidation, InvoiceValidationRule validationRule) {
		if (currentValidation == null) {
			return RuleValidationEnum.RULE_IGNORE;
		}
		switch (validationRule.getEvaluationMode()) {
		case CONDITION:
			return currentValidation ? RuleValidationEnum.RULE_TRUE : RuleValidationEnum.RULE_IGNORE;
		case REJECTION:
			return currentValidation ? RuleValidationEnum.RULE_FALSE : RuleValidationEnum.RULE_TRUE;
		case VALIDATION:
			return currentValidation ? RuleValidationEnum.RULE_TRUE : RuleValidationEnum.RULE_FALSE;
		}
		return RuleValidationEnum.RULE_TRUE;
	}

	private void rejectInvoiceRule(Invoice invoice, InvoiceValidationRule validationRule) {
		InvoiceValidationStatusEnum failStatus = validationRule.getFailStatus();
		if (InvoiceValidationStatusEnum.REJECTED.equals(failStatus)) {
			invoice.setStatus(InvoiceStatusEnum.REJECTED);
			invoice.setRejectedByRule(validationRule);
			if (invoice.getRejectReason() == null)
				invoice.setRejectReason("Rejected by rule " + validationRule.getDescription());
		} else if (InvoiceValidationStatusEnum.SUSPECT.equals(failStatus)) {
			invoice.setStatus(InvoiceStatusEnum.SUSPECT);
			invoice.setRejectedByRule(validationRule);
			if (invoice.getRejectReason() == null)
				invoice.setRejectReason("Suspected by rule " + validationRule.getDescription());
		}
	}
	
	private ScriptInterface injectScriptParameters(Map<String, Object> methodContext, InvoiceValidationRule validationRule) {
		ScriptInstance scriptInstance = scriptInstanceService.refreshOrRetrieve(validationRule.getValidationScript());
		ScriptInterface validationRuleScript = scriptInstanceService.getScriptInstance(scriptInstance.getCode());
		if(scriptInstance != null && !MapUtils.isEmpty(validationRule.getRuleValues())){
		    scriptInstance.getScriptParameters().stream().forEach(sp -> {
				if (validationRule.getRuleValues().containsKey(sp.getCode())) {
					methodContext.put(sp.getCode(), (sp.isCollection())?
									scriptInstanceService.parseListFromString(String.valueOf(validationRule.getRuleValues().get(sp.getCode())), sp.getClassName(), sp.getValuesSeparator())
									: scriptInstanceService.parseObjectFromString(String.valueOf(validationRule.getRuleValues().get(sp.getCode())), sp.getClassName()));
				}
			});
		}
		return validationRuleScript;
	}
	
	private Map<String, Object> initContext(Invoice invoice) {
		Map<String, Object> methodContext = new HashMap<>();
		methodContext.put(Script.CONTEXT_ENTITY, invoice);
		methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
		methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
		methodContext.put("billingRun", invoice.getBillingRun());
		return methodContext;
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
     * Execute a script to group invoiceable data by invoice type
     *
     * @param billingRun Billing run
     * @param invoiceType Current Invoice type
     * @param invoiceables Rated transactions to group
     * @param entity Entity to invoice
     * @param scriptInstanceCode Script to execute
     * @return A list of rated transaction groups
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    private List<RatedTransactionGroup> executeBCScript(BillingRun billingRun, InvoiceType invoiceType, List<IInvoiceable> invoiceables, IBillableEntity entity, String scriptInstanceCode, PaymentMethod paymentMethod)
            throws BusinessException {

        HashMap<String, Object> context = new HashMap<>();
        context.put(Script.CONTEXT_ENTITY, entity);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        context.put("br", billingRun);
        context.put("invoiceType", invoiceType);
        context.put("invoiceables", invoiceables);
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

        appendInvoiceAgregates(billingAccount, billingAccount, invoice, ratedTransactions, false, null, true);
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
            return emptyList();
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
        if (!StorageFactory.exists(invoiceXmlFile)) {
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
            generateInvoiceFile(billingTemplateName, resDir);
            generateInvoiceAdjustmentFile(isInvoiceAdjustment, billingTemplateName, resDir);

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

            JRXmlDataSource dataSource = StorageFactory.getJRXmlDataSource(invoiceXmlFile);

            String fileKey = jasperFile.getPath() + jasperFile.lastModified();
            JasperReport jasperReport = jasperReportMap.get(fileKey);
            if (jasperReport == null) {
                jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
                jasperReportMap.put(fileKey, jasperReport);
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
            JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.xpath.executer.factory", "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            OutputStream outStream = StorageFactory.getOutputStream(pdfFullFilename);
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            outStream.close();

            if ("true".equals(paramBeanFactory.getInstance().getProperty("invoice.pdf.addWaterMark", "true"))) {
                if (invoice.getInvoiceType().getCode().equals(paramBeanFactory.getInstance().getProperty("invoiceType.draft.code", "DRAFT")) || (invoice.isDraft() != null && invoice.isDraft())) {
                    PdfWaterMark.add(pdfFullFilename, paramBean.getProperty("invoice.pdf.waterMark", "PROFORMA"), null);
                }
            }
            invoice.setPdfFilename(pdfFilename);

            log.info("PDF file '{}' produced for invoice {}", pdfFullFilename, invoice.getInvoiceNumberOrTemporaryNumber());

        } catch (IOException | JRException e) {
            throw new BusinessException("Failed to generate a PDF file for " + pdfFilename, e);
        } catch(Throwable e) {
            throw new BusinessException("Failed to generate a PDF file for " + pdfFilename, e);
        } finally {
            IOUtils.closeQuietly(reportTemplate);
        }
    }
    
    /**
     * Generate Invoice File
     * @param billingTemplateName Billing Template Name
     * @param resDir Res Directory
     * @throws IOException {@link IOException}
     */
    public synchronized void generateInvoiceFile(String billingTemplateName, String resDir) throws IOException {
        File destDir = new File(resDir + File.separator + billingTemplateName + File.separator + "pdf");

        if (!destDir.exists()) {
            log.warn("PDF jasper report {} was not found. A default report will be used.", destDir.getAbsolutePath());
            String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + File.separator + billingTemplateName + File.separator + "invoice";
            File sourceFile = new File(sourcePath);
            
            if (!sourceFile.exists()) {
                VirtualFile vfDir = VFS
                    .getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/default");
                log.info("default jaspers path : {}", vfDir.getPathName());
                URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                sourceFile = new File(vfPath.getPath());
                if (!sourceFile.exists()) {
                    throw new BusinessException("A default embedded jasper PDF report [" + sourceFile.getAbsolutePath() + "] for invoice is missing..");
                }
            }

            destDir.mkdirs();
            FileUtils.copyDirectory(sourceFile, destDir);
        }
    }

    /**
     * Generate Invoice Adjustment File
     * @param isInvoiceAdjustment Is Invoice Adjustment
     * @param billingTemplateName Billing Template Name
     * @param resDir Res Directory 
     * @throws IOException {@link IOException}
     */
    public synchronized void generateInvoiceAdjustmentFile(boolean isInvoiceAdjustment, String billingTemplateName, String resDir) throws IOException {
        File destDirInvoiceAdjustment = new File(resDir + File.separator + billingTemplateName + File.separator + "invoiceAdjustmentPdf");

        if (!destDirInvoiceAdjustment.exists() && isInvoiceAdjustment) {
            destDirInvoiceAdjustment.mkdirs();
            String sourcePathInvoiceAdjustment = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + File.separator + billingTemplateName + "/invoiceAdjustment";
            File sourceFileInvoiceAdjustment = new File(sourcePathInvoiceAdjustment);
            
            if (!sourceFileInvoiceAdjustment.exists()) {
                VirtualFile vfDir = VFS
                    .getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/default/invoiceAdjustment");
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
        Map<Object, Object> userMap = new HashMap<>();
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

        String result = evaluateExpression(prefix, userMap, String.class);

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
        List<Invoice> result = new ArrayList<>();
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
            StorageFactory.mkdirs(new File(dir));
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
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("invoice", invoice);

            try {
                String value = evaluateExpression(expression, contextMap, String.class);
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
                    + getFileNameByStatus(invoice);
        }

        if (xmlFileName != null && !xmlFileName.toLowerCase().endsWith(".xml")) {
            xmlFileName = xmlFileName + ".xml";
        }
        xmlFileName = StringUtils.normalizeFileName(xmlFileName);
        return xmlFileName;
    }

    private String getFileNameByStatus(Invoice invoice) {
        if (invoice.getStatus().equals(InvoiceStatusEnum.DRAFT) || invoice.getStatus().equals(InvoiceStatusEnum.NEW)) {
            return invoice.getInvoiceType().getCode() + "-" + invoice.getId();
        }
        if (invoice.getStatus().equals(InvoiceStatusEnum.VALIDATED)) {
            return invoice.getInvoiceNumber();
        }
        return (invoice.getInvoiceNumber() != null && !StringUtils.isBlank(invoice.getInvoiceNumber())) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber();
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
            StorageFactory.mkdirs(new File(dir));
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
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("invoice", invoice);

            try {
                String value = evaluateExpression(expression, contextMap, String.class);

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
        return StorageFactory.exists(xmlFileName);
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
        if(InvoiceStatusEnum.DRAFT.equals(invoice.getStatus()) || InvoiceStatusEnum.NEW.equals(invoice.getStatus()) || InvoiceStatusEnum.DRAFT.equals(invoice.getStatus()) ){
    		produceInvoiceXmlNoUpdate(invoice, true);
    	}
        String xmlFileName = getFullXmlFilePath(invoice, false);
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists()) {
            produceInvoicePdfNoUpdate(invoice);        }
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
        return StorageFactory.exists(pdfFileName);
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
        if (!StorageFactory.exists(pdfFile)) {
            throw new BusinessException("Invoice PDF was not produced yet for invoice " + invoice.getInvoiceNumberOrTemporaryNumber());
        }

        InputStream fileInputStream = null;
        try {
            long fileSize = StorageFactory.length(pdfFile);
            if (fileSize > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("File is too big to put it to buffer in memory.");
            }
            fileInputStream = StorageFactory.getInputStream(pdfFile);
            assert fileInputStream != null;

            return fileInputStream.readAllBytes();

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
    public LinkedInvoice getLinkedInvoice(Invoice invoice) {
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
        return emptyList();
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
        if (!useV11Process) {
            ratedTransactionService.createRatedTransactions(entityToInvoice, lastTransactionDate);
        }
        List<Invoice> invoices = invoiceService.createInvoice(entityToInvoice, generateInvoiceRequestDto, ratedTxFilter, isDraft, useV11Process);

        List<Invoice> invoicesWNumber = new ArrayList<>();
        for (Invoice invoice : invoices) {
            if (customFieldValues != null) {
                invoice.setCfValues(customFieldValues);
            }
            try {
                if (invoice.getStatus().equals(InvoiceStatusEnum.DRAFT)) {
                    invoice.assignTemporaryInvoiceNumber();
                } else if (invoice.getStatus() != InvoiceStatusEnum.REJECTED && invoice.getStatus() != InvoiceStatusEnum.SUSPECT) {
                    invoicesWNumber.add(serviceSingleton.assignInvoiceNumber(invoice));
                }
            } catch (Exception e) {
                log.error("Failed to assign invoice number for invoice {}/{}", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber(), e);
                continue;
            }
            try {
                List<Long> drafWalletOperationIds;
                if (isDraft) {
                    drafWalletOperationIds = getDrafWalletOperationIds(entityToInvoice, generateInvoiceRequestDto.getFirstTransactionDate(), generateInvoiceRequestDto.getLastTransactionDate(),
                        generateInvoiceRequestDto.getLastTransactionDate());
                } else {
                    drafWalletOperationIds = new ArrayList<>();
                    invoice.setStatus(InvoiceStatusEnum.VALIDATED);
                    serviceSingleton.assignInvoiceNumber(invoice);
                }
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
        List<Invoice> invoices = emptyList();
        if (useV11Process) {
            MinAmountForAccounts minAmountForAccounts = invoiceLinesService.isMinAmountForAccountsActivated(entity, applyMinimumModeEnum);
            // Create invoice lines from grouped and filtered RT
            List<RatedTransaction> ratedTransactions = getRatedTransactions(entity, filter, firstTransactionDate, lastTransactionDate, lastTransactionDate, isDraft);
            List<Long> ratedTransactionIds = ratedTransactions.stream().filter(Objects::nonNull)
                    .map(RatedTransaction::getId)
                    .collect(toList());
            
            if (!ratedTransactionIds.isEmpty()) {
                Subscription subscription = getSubscriptionFromRT(ratedTransactions);
                List<Map<String, Object>> groupedRTs = ratedTransactionService.getGroupedRTs(ratedTransactionIds);
                AggregationConfiguration configuration = new AggregationConfiguration(appProvider.isEntreprise());
                
                InvoiceLineCreationStatistics stats = invoiceLinesService.createInvoiceLines(groupedRTs, configuration,
                        generateInvoiceRequestDto.getOpenOrderCode());
                
                List<InvoiceLine> invoiceLines =stats.getInvoiceLines();
                
                invoices = createAggregatesAndInvoiceUsingILAndSubscription(entity, null, filter, null, invoiceDate,
                        firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft,
                        !generateInvoiceRequestDto.getSkipValidation(), false, invoiceLines,
                        generateInvoiceRequestDto.getOpenOrderCode(), subscription);
            }
        } else {
            MinAmountForAccounts minAmountForAccounts = ratedTransactionService.isMinAmountForAccountsActivated(entity, applyMinimumModeEnum);
            invoices = createAgregatesAndInvoice(entity, null, filter, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, !generateInvoiceRequestDto.getSkipValidation());
        }
        return invoices;
    }

    private Subscription getSubscriptionFromRT(List<RatedTransaction> ratedTransactions) {
        if (CollectionUtils.isEmpty(ratedTransactions)) {
           return null;
        }

        Set<Subscription> subscriptions = ratedTransactions.stream()
                .map(RatedTransaction::getSubscription)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(subscriptions) || subscriptions.size() > 1) {
            // If we have more than one subscription for different selected RatedTransaction, the generated invoice shall not have a Subscription
            return null;
        }

        return subscriptions.iterator().next();

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
        recordedInvoiceService.generateRecordedInvoice(invoice, null);
        update(invoice);
    }

    /**
     * Cancel invoice and delete it.
     *
     * @param invoice invoice to cancel
     * @throws BusinessException business exception
     */
    public void cancelInvoice(Invoice invoice) throws BusinessException {
        cancelInvoice(invoice, true, null);
    }

    /**
     * Cancel invoice without delete.
     *
     * @param invoice invoice to cancel
     * @throws BusinessException business exception
     */
    public void cancelInvoiceWithoutDelete(Invoice invoice) throws BusinessException {
        cancelInvoice(invoice, false, null);
    }

    /**
     * Cancel invoice without delete.
     *
     * @param invoice invoice to cancel
     * @param rtAction to change RT status to OPEN or CANCELED
     * @throws BusinessException business exception
     */
    public void cancelInvoiceWithoutDeleteAndRTAction(Invoice invoice, RatedTransactionAction rtAction) throws BusinessException {
        cancelInvoice(invoice, false, rtAction);
    }

    private void cancelInvoice(Invoice invoice, boolean remove, RatedTransactionAction rtAction) {
    	invoice = refreshOrRetrieve(invoice);
        checkNonValidateInvoice(invoice);
        cancelInvoiceAndRts(invoice, rtAction);
        cancelInvoiceAdvances(invoice, null, true);
        List<Long> invoicesIds = new ArrayList<>();
        invoicesIds.add(invoice.getId());
        if (remove) {
            invoiceLinesService.cancelIlForRemoveByInvoices(invoicesIds);
            super.remove(invoice);
        } else {
            invoiceLinesService.cancelIlByInvoices(invoicesIds);
            cancelInvoiceById(invoice.getId());
        }
        updateBillingRunStatistics(invoice);
        log.debug("Invoice canceled {}", invoice.getTemporaryInvoiceNumber());
    }

	public void cancelInvoiceById(Long invoiceId) {
        getEntityManager().createNamedQuery("Invoice.cancelInvoiceById")
        .setParameter("now", new Date())
                .setParameter("invoiceId", invoiceId)
                .executeUpdate();
    }
    
    public void updateBillingRunStatistics(Invoice invoice) {
        invoice = refreshOrRetrieve(invoice);
        if(invoice != null) {
            if (invoice.getBillingRun() != null) {
                billingRunService.updateBillingRunStatistics(invoice.getBillingRun());
            }
        }
    }

    
    public void cancelInvoiceAndRts(Invoice invoice, RatedTransactionAction rtAction) {
        checkNonValidateInvoice(invoice);
        if (invoice.getRecordedInvoice() != null) {
            throw new BusinessException("Can't cancel an invoice that present in AR");
        }
        ratedTransactionService.deleteSupplementalRTs(invoice);
        ratedTransactionService.uninvoiceRTs(invoice, rtAction);
        invoice.setStatus(InvoiceStatusEnum.CANCELED);
    }
    
    private void checkNonValidateInvoice(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatusEnum.VALIDATED) {
            throw new BusinessException("You can't cancel a validated invoice");
        }
    }

    public void validateInvoice(Invoice invoice, boolean save) {
        if (InvoiceStatusEnum.REJECTED.equals(invoice.getStatus()) || InvoiceStatusEnum.SUSPECT.equals(invoice.getStatus())) {
            invoice.setStatus(InvoiceStatusEnum.DRAFT);
            if (save) {
                update(invoice);
            }
        }
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void validateInvoice(Invoice invoice) {
		invoice.setStatus(InvoiceStatusEnum.VALIDATED);
		serviceSingleton.assignInvoiceNumber(invoice, true);
	}
    
    /**
     * @param billingRunId
     * @param invoiceIds
     */
    public void rebuildInvoices(Long billingRunId, List<Long> invoiceIds) throws BusinessException {
        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.REJECTED, InvoiceStatusEnum.SUSPECT), Arrays.asList(InvoiceStatusEnum.DRAFT));
        for (Invoice invoice : invoices) {
            rebuildInvoice(invoice, true);
        }
    }

    /**
     * @param billingRunId
     * @param invoiceIds
     */
    public void rejectInvoices(Long billingRunId, List<Long> invoiceIds) {
        List<Invoice> invoices = extractInvalidInvoiceList(billingRunId, invoiceIds, Arrays.asList(InvoiceStatusEnum.SUSPECT, InvoiceStatusEnum.DRAFT));
        for (Invoice invoice : invoices) {
            rejectInvoice(invoice, null);
        }
    }

    public void rejectInvoice(Invoice invoice, RejectReasonInput rejectReasonInput) {
        InvoiceStatusEnum status = invoice.getStatus();
        if (!(InvoiceStatusEnum.SUSPECT.equals(status) || InvoiceStatusEnum.DRAFT.equals(status) || InvoiceStatusEnum.NEW.equals(status))) {
            throw new BusinessException("Can only reject invoices in statuses NEW/DRAFT/SUSPECT. current invoice status is :" + status.name());
        }
        BillingRun billingRun = invoice.getBillingRun();
        if(billingRun == null) {
            throw new BusinessException("Invoice not related to a billing run");
        }else {
            billingRun = billingRunService.retrieveIfNotManaged(billingRun);
        }

        if(InvoiceStatusEnum.DRAFT.equals(status)) {
            invoice.rebuildStatus(InvoiceStatusEnum.REJECTED);
        }else {
            invoice.setStatus(InvoiceStatusEnum.REJECTED);
        }
        
        if (rejectReasonInput != null) {
        	invoice.setRejectReason(rejectReasonInput.getRejectReason());
        } else {
        	invoice.setRejectReason(resourceMessages.getString("invoice.reject.reason.default.reason", currentUser.getFullNameOrUserName()));
        }
        
        update(invoice);
        
        if(InvoiceStatusEnum.REJECTED.equals(invoice.getStatus()) && billingRun.getRejectAutoAction() == null ){
            billingRun.setStatus(BillingRunStatusEnum.REJECTED);
            billingRunService.update(billingRun);
        }
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
    * @param invalidateXMLInvoices
    * @param invalidatePDFInvoices
    */
   @JpaAmpNewTx
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
   public void invalidateInvoiceDocuments(Long billingRunId, Boolean invalidateXMLInvoices, Boolean invalidatePDFInvoices) {
       BillingRun br = getBrById(billingRunId);
       
       if (Boolean.TRUE.equals(invalidateXMLInvoices)) {
           nullifyInvoiceXMLFileNames(br);
           nullifyBillingRunXMLExecutionResultIds(br);
       }

       if (Boolean.TRUE.equals(invalidatePDFInvoices)) {
           nullifyInvoicePDFFileNames(br);
           nullifyBillingRunPDFExecutionResultIds(br);
       }
   }

   /**
    * Nullify BR's invoices xml file names.
    *
    * @param billingRun the billing run
    */
   public void nullifyInvoiceXMLFileNames(BillingRun billingRun) {
       getEntityManager().createNamedQuery("Invoice.nullifyInvoiceXMLFileNames").setParameter("billingRun", billingRun).executeUpdate();
   }
   
   /**
    * Nullify BR's invoices pdf file names.
    *
    * @param billingRun the billing run
    */
   public void nullifyInvoicePDFFileNames(BillingRun billingRun) {
       getEntityManager().createNamedQuery("Invoice.nullifyInvoicePDFFileNames").setParameter("billingRun", billingRun).executeUpdate();
   }

   /**
    * Nullify BR XML execution result Id.
    *
    * @param billingRun the billing run
    */
   public void nullifyBillingRunXMLExecutionResultIds(BillingRun billingRun) {
       getEntityManager().createNamedQuery("BillingRun.nullifyBillingRunXMLExecutionResultIds").setParameter("billingRun", billingRun).executeUpdate();
   }
   
   /**
    * Nullify BR PDF execution result Id.
    *
    * @param billingRun the billing run
    */
   public void nullifyBillingRunPDFExecutionResultIds(BillingRun billingRun) {
       getEntityManager().createNamedQuery("BillingRun.nullifyBillingRunPDFExecutionResultIds").setParameter("billingRun", billingRun).executeUpdate();
   }
   

   /**
     * @param billingRunId
     */
    public void deleteInvoices(Long billingRunId) {
        BillingRun br = getBrById(billingRunId);
        deleteInvoicesByStatus(br, Arrays.asList(InvoiceStatusEnum.CANCELED));
        billingRunService.updateBillingRunStatistics(br);
    }

    /**
     * @param billingRunId
     * @param invoiceIds
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
        BillingRun nextBR = billingRunService.findOrCreateNextQuarantineBR(billingRunId, null);
        if (CollectionUtils.isEmpty(invoiceIds) && !CollectionUtils.isEmpty(invoices)) {
            invoiceIds = invoices.stream().map(invoice -> invoice.getId()).collect(Collectors.toList());
        }
        getEntityManager().createNamedQuery("Invoice.moveToBRByIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
        return nextBR.getId();
    }

    /**
     * @param billingRunId
     * @param invoices
     * @return billingRunId the id of the new billing run.
     */
    public Long moveInvoices(List<Invoice> invoices, Long billingRunId) {
        return moveInvoices(billingRunId, invoices.stream().map(x -> x.getId()).collect(Collectors.toList()));
    }

    private List<Invoice> extractInvalidInvoiceList(Long billingRunId, List<Long> invoiceIds, List<InvoiceStatusEnum> statusList) throws BusinessException {
        return extractInvalidInvoiceList(billingRunId, invoiceIds, statusList, new ArrayList<>());
    }

    private List<Invoice> extractInvalidInvoiceList(Long billingRunId, List<Long> invoiceIds, List<InvoiceStatusEnum> statusList, List<InvoiceStatusEnum> aditionalStatus) throws BusinessException {
        BillingRun br = null;
        List<Invoice> invoices = new ArrayList<>();
        if (billingRunId != null) {
            br = getBrById(billingRunId);
            final BillingRunStatusEnum brStatus = br.getStatus();
            if (brStatus != BillingRunStatusEnum.REJECTED && brStatus != BillingRunStatusEnum.POSTINVOICED) {
                throw new ActionForbiddenException("not possible to change invoice status because of billing run status:" + brStatus);
            }
        }
        if (CollectionUtils.isEmpty(invoiceIds)) {
            return br != null ? findInvoicesByStatusAndBR(billingRunId, statusList) : new ArrayList<>();
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
        invoice = findById(invoice.getId());
        invoice.setStatus(InvoiceStatusEnum.DRAFT);
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
        if (expression.contains(VAR_BILLING_ACCOUNT)) {
            userMap.put(VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.contains(VAR_INVOICE)) {
            userMap.put("invoice", invoice);
        }
        if (expression.contains("order")) {
            userMap.put("order", order);
        }
        Object res = evaluateExpression(expression, userMap, Integer.class);
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
            String value = evaluateExpression(expression, String.class, invoice);

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
    public InvoiceType determineInvoiceType(boolean isPrepaid, boolean isDraft, boolean isDepositInvoice, BillingCycle billingCycle, BillingRun billingRun, BillingAccount billingAccount) throws BusinessException {
        InvoiceType invoiceType = null;

        if (billingRun != null && billingRun.getInvoiceType() != null) {
            return billingRun.getInvoiceType();
        }
        if (isPrepaid) {
            invoiceType = invoiceTypeService.getDefaultPrepaid();
        } else if (isDepositInvoice) {
            invoiceType = invoiceTypeService.getDefaultDeposit();
        } else {
            if (billingCycle != null && !isBlank(billingCycle.getInvoiceTypeEl())) {
                String invoiceTypeCode = evaluateInvoiceType(billingCycle.getInvoiceTypeEl(), billingRun, billingAccount);
                invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
            }
            if (billingCycle != null && invoiceType == null) {
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
                String value = evaluateExpression(expression, contextMap, String.class);
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
        invoice.setStatus(InvoiceStatusEnum.VALIDATED);
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
     * @param invoiceId
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
        } else if (billingRun.getComputeDatesAtValidation() == null && !billingCycle.isComputeDatesAtValidation()) {
            return;
        }
        if (billingRun.getComputeDatesAtValidation() != null && billingRun.getComputeDatesAtValidation()) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
            update(invoice);
        } else if (billingRun.getComputeDatesAtValidation() == null && billingCycle.isComputeDatesAtValidation()) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
            update(invoice);
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void assignInvoiceNumberAndRecalculateDates(Long invoiceId, InvoicesToNumberInfo invoicesToNumberInfo) throws BusinessException {
        Invoice invoice = findById(invoiceId);

        BillingAccount billingAccount = invoice.getBillingAccount();
        //Recalculate dates :
        BillingCycle billingCycle = billingAccount.getBillingCycle();
        BillingRun billingRun = invoice.getBillingRun();
        if (billingRun != null && billingRun.getBillingCycle() != null) {
            billingCycle = billingRun.getBillingCycle();
        }
        billingCycle = PersistenceUtils.initializeAndUnproxy(billingCycle);
        if (billingRun == null) {
            return;
        }
        if (invoice.getStatus().equals(InvoiceStatusEnum.SUSPECT) || invoice.getStatus().equals(InvoiceStatusEnum.REJECTED)) {
            invoice.setStatus(InvoiceStatusEnum.DRAFT);
        }
        if ((billingRun.getComputeDatesAtValidation() != null && billingRun.getComputeDatesAtValidation()) 
                || (billingRun.getComputeDatesAtValidation() == null && billingCycle.isComputeDatesAtValidation())) {
            recalculateDate(invoice, billingRun, billingAccount, billingCycle);
        }
        if (!billingRun.isSkipValidationScript()) {
            applyAutomaticInvoiceCheck(invoice, true);
        }
        if (invoice.getStatus().equals(InvoiceStatusEnum.REJECTED)) {
            return;
        }

        //Assign invoice number :
        billingAccount = incrementBAInvoiceDate(invoice.getBillingRun(), billingAccount);
        assignInvoiceNumberFromReserve(invoice, invoicesToNumberInfo);
        invoice.setStatus(InvoiceStatusEnum.VALIDATED);
        // /!\ DO NOT REMOVE THIS LINE, A LasyInitializationException is throw and the invoice is not generated.
        billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
        invoice = update(invoice);

    }

    public void recalculateDatesForValidated(Long invoiceId) {
        Invoice invoice = findById(invoiceId);
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
        if ((billingRun.getComputeDatesAtValidation() != null && billingRun.getComputeDatesAtValidation()) 
                || billingRun.getComputeDatesAtValidation() == null) {
            recalculateDateByBR(invoice, billingRun, billingAccount, billingCycle, true);
        }
    }
    
    private void recalculateDate(Invoice invoice, BillingRun billingRun, BillingAccount billingAccount, BillingCycle billingCycle) {
        int delay = 0;
        boolean isCalculateInvoiceDateByDelayEL = (invoice.getBillingRun() == null);
        reCalculateDates(invoice, billingRun, billingAccount, billingCycle, isCalculateInvoiceDateByDelayEL);
    }
    
    private void recalculateDateByBR(Invoice invoice, BillingRun billingRun, BillingAccount billingAccount, 
            BillingCycle billingCycle, boolean isBillingRun) {
        int delay = 0;
        boolean isCalculateInvoiceDateByDelayEL = !isBillingRun;
        reCalculateDates(invoice, billingRun, billingAccount, billingCycle, isCalculateInvoiceDateByDelayEL);
    }

    private void reCalculateDates(Invoice invoice, BillingRun billingRun, BillingAccount billingAccount, BillingCycle billingCycle, boolean isCalculateInvoiceDateByDelayEL) {
        int delay;
        if (isCalculateInvoiceDateByDelayEL) {
            delay = billingCycle.getInvoiceDateDelayEL() == null ? 0 : InvoiceService.resolveImmediateInvoiceDateDelay(billingCycle.getInvoiceDateDelayEL(), invoice, billingAccount);
        }else {
            delay = billingCycle.getInvoiceDateProductionDelayEL() == null ? 0 : InvoiceService.resolveImmediateInvoiceDateDelay(billingCycle.getInvoiceDateProductionDelayEL(), invoice, billingAccount);
        }        
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
    public BillingAccount incrementBAInvoiceDate(BillingRun billingRun, BillingAccount billingAccount) throws BusinessException {

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
            return emptyList();
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
            String languageCode = billingAccount.getCustomerAccount().getTradingLanguage().getLanguage().getLanguageCode();
            String emailSubject = internationalSettingsService.resolveSubject(emailTemplate,languageCode);
            String emailContent = internationalSettingsService.resolveEmailContent(emailTemplate,languageCode);
            String htmlContent = internationalSettingsService.resolveHtmlContent(emailTemplate,languageCode);
            if (electronicBilling && mailingTypeEnum.equals(mailingType)) {
                Map<Object, Object> params = new HashMap<>();
                params.put("invoice", invoice);
                params.put("billingAccount", billingAccount);
                params.put("dayDate", DateUtils.formatDateWithPattern(new Date(), "dd/MM/yyyy"));
                String subject = evaluateExpression(emailSubject, params, String.class);
                String content = evaluateExpression(emailContent, params, String.class);
                String contentHtml = evaluateExpression(htmlContent, params, String.class);
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
        return invoice.getInvoiceNumber() == null;
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
        String result = evaluateExpression(overrideEmailEl, userMap, String.class);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        return result;
    }

    /**
     * Creates Invoice aggregates from given Rated transactions and appends them to an invoice
     *
     * @param entityToInvoice Entity to invoice
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param invoiceables A list of data to invoice
     * @param isInvoiceAdjustment Is this invoice adjustment
     * @param invoiceAggregateProcessingInfo RT to invoice aggregation information when invoice is created with paged RT retrieval. NOTE: should pass NULL in non-paginated invoicing cases
     * @param addDiscountAndTaxAggregates Indicates to append discount and tax aggregates as well.
     * @throws BusinessException BusinessException
     */
    protected void appendInvoiceAgregates(IBillableEntity entityToInvoice, BillingAccount billingAccount, Invoice invoice, List<? extends IInvoiceable> invoiceables, boolean isInvoiceAdjustment,
            InvoiceAggregateProcessingInfo invoiceAggregateProcessingInfo, boolean addDiscountAndTaxAggregates) throws BusinessException {

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
        Set<String> commercialOrderNumbers = invoiceAggregateProcessingInfo != null ? invoiceAggregateProcessingInfo.commercialOrderNumbers : new HashSet<>();

        String scaKey = null;

        if (log.isTraceEnabled()) {
            log.trace("ratedTransactions.totalAmountWithoutTax={}", invoiceables != null ? invoiceables.stream().mapToDouble(e -> e.getAmountWithoutTax().doubleValue()).sum() : "0");
        }

        boolean linkInvoiceToOrders = paramBeanFactory.getInstance().getPropertyAsBoolean("order.linkInvoiceToOrders", true);
        // TODO add  commercialOrder.linkInvoiceToCommercialOrders to paramBean
        boolean linkInvoiceToCommercialOrders = paramBeanFactory.getInstance().getPropertyAsBoolean("commercialOrder.linkInvoiceToCommercialOrders", true);
        boolean taxWasRecalculated = false;

        EntityManager em = getEntityManager();

        for (IInvoiceable invoiceable : invoiceables) {

            InvoiceSubCategory invoiceSubCategory = invoiceSubcategoryService.findById(invoiceable.getInvoiceSubCategoryId());

            scaKey = invoiceable.getInvoiceSubCategoryId().toString();
            if (isAggregateByUA) {
                scaKey = (invoiceable.getUserAccountId() != null ? invoiceable.getUserAccountId() : "") + "_" + (invoiceable.getWalletId() != null ? invoiceable.getWalletId() : "") + "_" + scaKey;
            }

            Tax tax = em.getReference(Tax.class, invoiceable.getTaxId());
            invoiceable.setTax(tax);
            UserAccount userAccount = invoiceable.getUserAccountId() != null ? em.getReference(UserAccount.class, invoiceable.getUserAccountId()) : null;
            WalletInstance wallet = em.getReference(WalletInstance.class, invoiceable.getWalletId());

            // Check if tax has to be recalculated. Does not apply to RatedTransactions that had tax explicitly set/overridden
            if (calculateTaxOnSubCategoryLevel && !invoiceable.isTaxOverriden()) {

                TaxClass taxClass = em.getReference(TaxClass.class, invoiceable.getTaxClassId());
                String taxChangeKey = billingAccount.getId() + "_" + invoiceable.getTaxClassId();

                Object[] changedToTax = taxChangeMap.get(taxChangeKey);
                if (changedToTax == null) {

                    taxZero = isExonerated && taxZero == null ? taxService.getZeroTax() : taxZero;
                    Object[] applicableTax = taxMappingService.checkIfTaxHasChanged(tax, isExonerated, invoice.getSeller(), invoice.getBillingAccount(), invoice.getInvoiceDate(), taxClass, userAccount, taxZero);

                    changedToTax = applicableTax;
                    taxChangeMap.put(taxChangeKey, changedToTax);
                    if ((boolean) changedToTax[1]) {
                        log.debug("Will update rated transactions of Billing account {} and tax class {} with new tax from {}/{}% to {}/{}%", billingAccount.getId(), invoiceable.getTaxClassId(), tax.getId(), tax.getPercent(),
                            ((Tax) changedToTax[0]).getId(), ((Tax) changedToTax[0]).getPercent());
                    }
                }
                taxWasRecalculated = (boolean) changedToTax[1];
                if (taxWasRecalculated) {
                    tax = (Tax) changedToTax[0];
                    invoiceable.setTaxRecalculated(true);
                }
            }

            SubCategoryInvoiceAgregate scAggregate = subCategoryAggregates.get(scaKey);
            if (scAggregate == null) {
                scAggregate = new SubCategoryInvoiceAgregate(invoiceSubCategory, billingAccount, isAggregateByUA ? userAccount : null, isAggregateByUA ? wallet : null, invoice, invoiceSubCategory.getAccountingCode());
                scAggregate.updateAudit(currentUser);

                String translationSCKey = "SC_" + invoiceable.getInvoiceSubCategoryId() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationSCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory.getDescriptionOrCode();
                    if (invoiceSubCategory.getDescriptionI18n() != null && invoiceSubCategory.getDescriptionI18n().get(languageCode) != null) {
                        descTranslated = invoiceSubCategory.getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationSCKey, descTranslated);
                }
                scAggregate.setDescription(descTranslated);

                subCategoryAggregates.put(scaKey, scAggregate);
                invoice.addInvoiceAggregate(scAggregate);
            }

            if (!(entityToInvoice instanceof Order) && linkInvoiceToOrders && invoiceable.getOrderNumber() != null) {
                orderNumbers.add(invoiceable.getOrderNumber());
            }
            // TODO ask if we have to use the same attribute : invoiceable.getOrderNumber()
            if (!(entityToInvoice instanceof CommercialOrder) && linkInvoiceToCommercialOrders && invoiceable.getOrderNumber() != null) {
                commercialOrderNumbers.add(invoiceable.getOrderNumber());
            }



            if (taxWasRecalculated) {
                invoiceable.setTax(tax);
                invoiceable.setTaxPercent(tax.getPercent());
                invoiceable.computeDerivedAmounts(isEnterprise, rtRounding, rtRoundingMode);
            }

            scAggregate.addInvoiceable(invoiceable, isEnterprise, true);
        }

        // Add discount and tax aggregates if requested
        if (addDiscountAndTaxAggregates) {
            addDiscountCategoryAndTaxAggregates(invoice, subCategoryAggregates.values());
        }
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

        subscription = subscriptionService.refreshOrRetrieve(subscription);
        if (subscription != null && subscription.getDiscountPlanInstances() != null && !subscription.getDiscountPlanInstances().isEmpty()) {
            addApplicableDiscount(subscriptionApplicableDiscountPlanItems,  subscription.getDiscountPlanInstances(), billingAccount, customerAccount, invoice);
        }

        // Calculate derived aggregate amounts for subcategory aggregate, create category aggregates, discount aggregates and tax aggregates
        BigDecimal[] amounts = null;
        Map<String, CategoryInvoiceAgregate> categoryAggregates = new HashMap<>();
        List<SubCategoryInvoiceAgregate> discountAggregates = new ArrayList<>();
        Map<String, TaxInvoiceAgregate> taxAggregates = new HashMap<>();

        // Create category aggregates
        boolean anyUseSpecificConversion = subCategoryAggregates.stream().anyMatch(SubCategoryInvoiceAgregate::isUseSpecificPriceConversion);
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates) {
            List<InvoiceLine> erronedLines = scAggregate.getInvoiceLinesToAssociate()
                    .stream()
                    .filter(invoiceLine -> invoiceLine.getTax() == null && !invoiceLine.getTaxMode().equals(InvoiceLineTaxModeEnum.RATE))
                    .collect(Collectors.toList());
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
            if(scAggregate.isUseSpecificPriceConversion()) {
            	invoice.setUseSpecificPriceConversion(true);
            	invoice.addTransactionalAmountWithoutTax(scAggregate.getTransactionalAmountWithoutTax());
            	invoice.addTransactionalAmountWithTax(scAggregate.getTransactionalAmountWithTax());
            	invoice.addTransactionalAmountTax(scAggregate.getTransactionalAmountTax());
            }
        }

        if(invoice.getDiscountPlan()!=null && discountPlanService.isDiscountPlanApplicable(billingAccount, invoice.getDiscountPlan(),invoice.getInvoiceDate())) {
            List<DiscountPlanItem> discountItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, invoice.getDiscountPlan(), 
                    null,invoice.getInvoiceDate(),invoice);
            
            subscriptionApplicableDiscountPlanItems.addAll(discountItems);
        }
        
        if (billingAccount.getDiscountPlanInstances() != null && !billingAccount.getDiscountPlanInstances().isEmpty()) {
            List<DiscountPlanInstance> discountPlanInstances = ofNullable(subscription)
                    .map(Subscription::getDiscountPlanInstances)
                    .orElse(emptyList());
            addApplicableDiscount(billingAccountApplicableDiscountPlanItems,
                    discountPlanInstances, billingAccount, customerAccount, invoice);
        }

        BigDecimal otherDiscount = ZERO;
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
                    otherDiscount = otherDiscount.add(discountAggregate.getAmountWithoutTax().abs());
                    if (invoice.getDiscountPlan() != null && invoice.getDiscountPlan().getStatus() != DiscountPlanStatusEnum.IN_USE.IN_USE) {
                        invoice.getDiscountPlan().setStatus(DiscountPlanStatusEnum.IN_USE.IN_USE);
                    }
                }
            }

            for (DiscountPlanItem discountPlanItem : billingAccountApplicableDiscountPlanItems) {
                SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, rounding, roundingMode, invoiceRounding, invoiceRoundingMode, scAggregate, amountAsDiscountBase,
                    cAggregate, discountPlanItem);
                if (discountAggregate != null) {
                    addAmountsToMap(amountCumulativeForTax, discountAggregate.getAmountsByTax());
                    discountAggregates.add(discountAggregate);
                    otherDiscount = otherDiscount.add(discountAggregate.getAmountWithoutTax().abs());
                }
            }

            // Add tax aggregate or update its amounts

            if (calculateTaxOnSubCategoryLevel && !isExonerated && !amountCumulativeForTax.isEmpty()) {

                for (Map.Entry<Tax, SubcategoryInvoiceAgregateAmount> amountByTax : amountCumulativeForTax.entrySet()) {
                    Tax tax = amountByTax.getKey();
                    if (BigDecimal.ZERO.compareTo(amountByTax.getValue().getAmount(!isEnterprise)) == 0) {
                        continue;
                    }

                    TaxInvoiceAgregate taxAggregate = addTaxAggregate(invoice, billingAccount, isEnterprise, languageCode, taxAggregates, amountByTax, tax);
                }
            }

        }

        // Calculate derived tax aggregate amounts
        if (calculateTaxOnSubCategoryLevel && !isExonerated) {
            calculateDerivedTaxAggregateAmounts(taxAggregates);
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
            Map<String, TaxInvoiceAgregate> simpleTaxMap = taxAggregates.entrySet().stream()
                  .filter(x -> !x.getValue().getTax().isComposite())
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            
            Map<String, TaxInvoiceAgregate> compositeTaxMap = taxAggregates.entrySet().stream()
                      .filter(x -> x.getValue().getTax().isComposite()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            compositeTaxMap.values().stream().forEach(a-> addCompositeTaxAggregate(invoice, billingAccount, isEnterprise, languageCode, simpleTaxMap, a));
            taxAggregates = simpleTaxMap;
            calculateDerivedTaxAggregateAmounts(taxAggregates);
            
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

        if(invoice.getInvoiceLines() != null && !invoice.getInvoiceLines().isEmpty()) {
            BigDecimal amountDiscount = invoice.getInvoiceLines()
                    .stream()
                    .map(InvoiceLine::getDiscountAmount)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);
            amountDiscount = amountDiscount.add(otherDiscount);
            // Always set AmountWithoutTaxBeforeDiscount = AmountWithoutTax before calculate discount : to manager case where amountDiscount == 0
            invoice.setAmountWithoutTaxBeforeDiscount(invoice.getAmountWithoutTax());
            if(!amountDiscount.equals(BigDecimal.ZERO)) {
                invoice.setDiscountAmount(amountDiscount);
                invoice.setAmountWithoutTaxBeforeDiscount(invoice.getAmountWithoutTax().add(amountDiscount.abs()));
            }
            if(amountDiscount.equals(BigDecimal.ZERO) && invoice.getDiscountPlan() != null
                    && invoice.getDiscountPlan().getDiscountPlanType() == DiscountPlanTypeEnum.INVOICE && discountAggregates != null) {
                amountDiscount = discountAggregates.stream()
                        .map(SubCategoryInvoiceAgregate::getAmountWithoutTax)
                        .map(BigDecimal::abs)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
                invoice.setDiscountAmount(amountDiscount);
                invoice.setAmountWithoutTaxBeforeDiscount(invoice.getAmountWithoutTax().add(amountDiscount.abs()));
            }
        }
        
        if(invoice.getBillingRun() == null && invoice.getInvoiceType() != null && !invoice.getInvoiceType().getCode().equals("ADV")) {
            applyAdvanceInvoice(invoice, checkAdvanceInvoice(invoice));
        }
    }

    private void calculateDerivedTaxAggregateAmounts( Map<String, TaxInvoiceAgregate> taxAggregates) {
        BigDecimal[] amounts;
        for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {

            amounts = NumberUtils.computeDerivedAmounts(taxAggregate.getAmountWithoutTax(), taxAggregate.getAmountWithTax(), taxAggregate.getTaxPercent(), appProvider.isEntreprise(), appProvider.getInvoiceRounding(),
                    appProvider.getInvoiceRoundingMode().getRoundingMode());
            taxAggregate.setAmountWithoutTax(amounts[0]);
            taxAggregate.setAmountWithTax(amounts[1]);
            taxAggregate.setAmountTax(amounts[2]);

        }
    }

    private void addCompositeTaxAggregate(Invoice invoice, BillingAccount billingAccount, boolean isEnterprise,
            String languageCode, Map<String, TaxInvoiceAgregate> taxAggregates, TaxInvoiceAgregate compositeTaxAgregate) {
        invoice.getInvoiceAgregates().remove(compositeTaxAgregate);
        Tax compositeTax = compositeTaxAgregate.getTax();
        BigDecimal compositePercent = compositeTax.getPercent();
        List<Tax> subTaxes = compositeTax.getSubTaxes();
        BigDecimal amountWithoutTax=compositeTaxAgregate.getAmountWithoutTax();
        BigDecimal composteTaxAmount=compositeTaxAgregate.getAmountTax();
        BigDecimal subTaxTotalAmount=BigDecimal.ZERO;
        for(int i=0; i<subTaxes.size(); i++) {
            Tax subTax = subTaxes.get(i);
            BigDecimal amountTax = BigDecimal.ZERO.equals(compositePercent)? BigDecimal.ZERO
                    : (i==subTaxes.size()-1)? composteTaxAmount.subtract(subTaxTotalAmount)
                            : composteTaxAmount.multiply(subTax.getPercent()).divide(compositePercent,
                    appProvider.getInvoiceRounding(),RoundingMode.HALF_UP);
            SubcategoryInvoiceAgregateAmount subcategoryInvoiceAgregateAmount= new SubcategoryInvoiceAgregateAmount(amountWithoutTax,amountWithoutTax.add(amountTax),amountTax);
            addTaxAggregate(invoice, billingAccount, isEnterprise, languageCode, taxAggregates, new AbstractMap.SimpleEntry<>(subTax, subcategoryInvoiceAgregateAmount), subTax);
        }
    }

    private TaxInvoiceAgregate addTaxAggregate(Invoice invoice, BillingAccount billingAccount, boolean isEnterprise,
            String languageCode, Map<String, TaxInvoiceAgregate> taxAggregates,
            Map.Entry<Tax, SubcategoryInvoiceAgregateAmount> amountByTax, Tax tax) {
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

        taxAggregate.addAmountWithoutTax(amountByTax.getValue().getAmountWithoutTax());
        taxAggregate.addAmountWithTax(amountByTax.getValue().getAmountWithTax());
        taxAggregate.addAmountTax(amountByTax.getValue().getAmountTax());
        taxAggregate.addTransactionAmountWithoutTax(amountByTax.getValue().getTransactionalAmountWithoutTax());
        taxAggregate.addTransactionAmountWithTax(amountByTax.getValue().getTransactionalAmountWithTax());
        taxAggregate.addTransactionAmountTax(amountByTax.getValue().getTransactionalAmountTax());

        return taxAggregate;
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

    @Deprecated
    private List<DiscountPlanItem> getApplicableDiscountPlanItems(BillingAccount billingAccount, List<DiscountPlanInstance> discountPlanInstances, Invoice invoice, CustomerAccount customerAccount)
            throws BusinessException {
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
        for (DiscountPlanInstance dpi : discountPlanInstances) {
            if (!dpi.isEffective(invoice.getInvoiceDate()) || dpi.getStatus().equals(DiscountPlanInstanceStatusEnum.EXPIRED)
                    || Arrays.asList(DiscountPlanTypeEnum.OFFER, DiscountPlanTypeEnum.PRODUCT, DiscountPlanTypeEnum.QUOTE).contains(dpi.getDiscountPlan().getDiscountPlanType())) {
                continue;
            }
            if (dpi.getDiscountPlan().isActive()) {
                List<DiscountPlanItem> discountPlanItems = dpi.getDiscountPlan().getDiscountPlanItems();
                for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                    if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(), customerAccount, billingAccount, invoice, dpi)) {
                        applicableDiscountPlanItems.add(discountPlanItem);
                    }
                }
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
        return applicableDiscountPlanItems;
    }
    

    private List<DiscountPlanItem> getApplicableDiscountPlanItemsV11(BillingAccount billingAccount, List<DiscountPlanInstance> discountPlanInstances, Invoice invoice, CustomerAccount customerAccount)
            throws BusinessException {
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
        for (DiscountPlanInstance dpi : discountPlanInstances) {
            if (!dpi.isEffective(invoice.getInvoiceDate()) || dpi.getStatus().equals(DiscountPlanInstanceStatusEnum.EXPIRED)) {
                continue;
            }
            if (dpi.getDiscountPlan().isActive()) {
            	 applicableDiscountPlanItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, dpi.getDiscountPlan(), null,invoice.getInvoiceDate(),invoice);
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
        return applicableDiscountPlanItems;
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
        Map<Object, Object> userMap = new HashMap<>();

        if (expression.indexOf(VAR_CUSTOMER_ACCOUNT) >= 0) {
            userMap.put(VAR_CUSTOMER_ACCOUNT, customerAccount);
        }
        if (expression.indexOf(VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(VAR_INVOICE_SHORT) >= 0) {
            userMap.put(VAR_INVOICE_SHORT, invoice);
        }
        if (expression.indexOf(VAR_INVOICE) >= 0) {
            userMap.put(VAR_INVOICE, invoice);
        }
        if (expression.indexOf(VAR_DISCOUNT_PLAN_INSTANCE) >= 0) {
            userMap.put(VAR_DISCOUNT_PLAN_INSTANCE, dpi);
        }
        if (expression.indexOf("su") >= 0) {
            userMap.put("su", invoice.getSubscription());
        }
        Object res = evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @param expression el expression
     * @param billingAccount billing account
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
        Map<Object, Object> userMap = new HashMap<>();
        userMap.put(VAR_CUSTOMER_ACCOUNT, billingAccount.getCustomerAccount());
        userMap.put(VAR_BILLING_ACCOUNT, billingAccount);
        userMap.put("iv", invoice);
        userMap.put("invoice", invoice);
        userMap.put("wa", wallet);
        userMap.put("amount", subCatTotal);

        BigDecimal result = evaluateExpression(expression, userMap, BigDecimal.class);
        return result;
    }

    private Invoice instantiateInvoice(IBillableEntity entity, BillingAccount billingAccount, Long sellerId, BillingRun billingRun, Date invoiceDate, boolean isDraft, BillingCycle billingCycle,
            PaymentMethod paymentMethod, InvoiceType invoiceType, boolean isPrepaid, boolean automaticInvoiceCheck) throws BusinessException {

        EntityManager em = getEntityManager();

        Invoice invoice = new Invoice();

        invoice.setBillingAccount(billingAccount);
        invoice.setSeller(em.getReference(Seller.class, sellerId));
        invoice.setStatus(InvoiceStatusEnum.DRAFT);
        invoice.setInvoiceType(invoiceType);
        invoice.setPrepaid(isPrepaid);
        invoice.setInvoiceDate(invoiceDate);
        if (billingRun != null) {
            invoice.setBillingRun(em.getReference(BillingRun.class, billingRun.getId()));
        }
        Order order;
        if (entity instanceof Order) {
            order = (Order) entity;
            invoice.setOrder(order);

        } else if (entity instanceof Subscription) {
            Subscription subscription = (Subscription) entity;
            invoice.setSubscription(subscription);
            invoice.setCommercialOrder(subscription.getOrder());
            invoice.setCpqQuote(subscription.getOrder()!=null?subscription.getOrder().getQuote():null);
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

        return invoice;
    }

    private void setInvoiceDueDate(Invoice invoice, BillingCycle billingCycle) {

        BillingAccount billingAccount = invoice.getBillingAccount();
        CustomerAccount customerAccount = invoice.getBillingAccount().getCustomerAccount();
        Order order = invoice.getOrder();

        Date dueDate = calculateDueDate(invoice, billingCycle, billingAccount, customerAccount, order);

        invoice.setDueDate(dueDate);
    }

    public Date calculateDueDate(Invoice invoice, BillingCycle billingCycle, BillingAccount billingAccount, CustomerAccount customerAccount, Order order) {
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
        return evaluateExpression(el, Integer.class, billingRun);
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

        Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<>();
        boolean isEnterprise = appProvider.isEntreprise();
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        Auditable auditable = new Auditable(currentUser);

        boolean isDetailledInvoiceMode = InvoiceModeEnum.DETAILLED == invoiceDTO.getInvoiceMode();

        Map<InvoiceSubCategory, List<RatedTransaction>> existingRtsTolinkMap = extractMappedRatedTransactionsTolink(invoiceDTO, billingAccount);
        Map<InvoiceCategory, List<InvoiceSubCategory>> subCategoryMap = existingRtsTolinkMap.isEmpty() ? new HashMap<>()
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
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        if (isDetailledInvoiceMode) {
            invoiceAgregateSubcat.setItemNumber(invoiceAgregateSubcat.getInvoiceablesToAssociate().size());
            putTaxInvoiceAgregate(billingAccount, taxInvoiceAgregateMap, isEnterprise, auditable, invoice, invoiceAgregateSubcat, invoiceRounding, invoiceRoundingMode);
            ratedTransactions = (List<RatedTransaction>) invoiceAgregateSubcat.getInvoiceablesToAssociate();
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
        invoiceAgregateSubcat.addInvoiceable(rt, isEntreprise, true);
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
        return new HashMap<>();
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
        invoice.setAutoMatching(buildAutoMatching(invoiceDTO.isAutoMatching(), invoiceType));
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
                LinkedInvoice linkedInvoice = new LinkedInvoice(invoice, invoiceTmp);
                if (!invoiceTmp.getLinkedInvoices().contains(linkedInvoice)) {
                    invoice.getLinkedInvoices().add(linkedInvoice);
                }
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
                LinkedInvoice linkedInvoice = new LinkedInvoice(invoice, invoiceTmp);
                if (!invoiceTmp.getLinkedInvoices().contains(linkedInvoice)) {
                    invoice.getLinkedInvoices().add(linkedInvoice);
                }
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
     * Refresh invoice amounts and invoice lines amounts
     * @param invoice invoice to refresh
     * @param currentRate current rate
     * @param currentRateFromDate current rate from date
     * @return Refreshed invoice
     */
    public Invoice refreshConvertedAmounts(Invoice invoice, BigDecimal currentRate, Date currentRateFromDate) {
    	invoice.getLinkedInvoices();
        invoice = refreshOrRetrieve(invoice);
        if(currentRate != null && !invoice.getTradingCurrency().getCurrency().getId().equals(appProvider.getCurrency().getId())) {
            invoice.setLastAppliedRate(currentRate);
        } else {
            invoice.setLastAppliedRate(ONE);
        }
        if(currentRateFromDate != null) {
            invoice.setLastAppliedRateDate(currentRateFromDate);
        } else {
            invoice.setLastAppliedRateDate(invoice.getAuditable().getCreated());
        }
        invoice.setUseCurrentRate(true);
        refreshInvoiceLineAndAggregateAmounts(invoice);
        return update(invoice);
    }

    public void cleanUpInvoiceWithFuntionalCurrencyDifferentFromOne(){
        List<Invoice> invoices = getEntityManager().createNamedQuery("Invoice.findWithFuntionalCurrencyDifferentFromOne")
                .setParameter("EXPECTED_RATE", ONE).getResultList();

        ofNullable(invoices).orElse(emptyList())
                .forEach(invoice ->
                        refreshConvertedAmounts(invoice, null, invoice.getTradingCurrency().getCurrentRateFromDate())
                );
    }

    private void refreshInvoiceLineAndAggregateAmounts(Invoice invoice) {
        invoice.getInvoiceLines()
                .forEach(invoiceLine -> invoiceLinesService.update(invoiceLine));
        invoice.getInvoiceAgregates()
                .stream()
                .filter(invoiceAggregate -> invoiceAggregate instanceof TaxInvoiceAgregate)
                .forEach(invoiceAggregate -> invoiceAgregateService.update(invoiceAggregate));
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
        private Set<String> orderNumbers = new HashSet<>();

        /**
         * Commercial Orders (numbers) referenced from Rated transactions
         */
        private Set<String> commercialOrderNumbers = new HashSet<>();
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
    @SuppressWarnings("unchecked")
    public List<Object[]> getTotalInvoiceableAmountByBR(BillingRun billingRun) {
        return (List<Object[]>) getEntityManager().createNamedQuery("Invoice.sumInvoiceableAmountByBR").setParameter("billingRunId", billingRun.getId()).getResultList();
    }

    /**
     * Return the total of positive rated transaction for a billing run.
     *
     * @param billingRun the billing run
     * @return a map of positive rated transaction for a billing run.
     */
    @SuppressWarnings("unchecked")
    public Amounts getTotalAmountsByBR(BillingRun billingRun) {
        Amounts amounts = new Amounts();
        try {
            Object[] result = (Object[]) getEntityManager().createNamedQuery("Invoice.sumAmountsByBR").setParameter("billingRunId", billingRun.getId()).getSingleResult();
            amounts.setAmountTax(result[0] != null ? (BigDecimal) result[0] : BigDecimal.ZERO);
            amounts.setAmountWithoutTax(result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO);
            amounts.setAmountWithTax(result[2] != null ? (BigDecimal) result[2] : BigDecimal.ZERO);
        } catch (NoResultException e) {
            //ignore
        }
        return amounts;     
    }

    /**
     * List billing accounts that are associated with invoice for a given billing run
     *
     * @param billingRun Billing run
     * @return A list of Billing accounts
     */
    public List<BillingAccount> getInvoicesBillingAccountsByBR(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("Invoice.billingAccountsByBr", BillingAccount.class).setParameter("billingRunId", billingRun.getId()).getResultList();
    }
    
    /**
     * Resolve Invoice production date delay for a given billing run
     *
     * @param el EL expression to resolve
     * @param billingRun Billing run
     * @return An integer value
     */
    public static Integer resolveInvoiceDateDelay(String el, BillingRun billingRun) {
        return evaluateExpression(el, Integer.class, billingRun);
    }

    /**
     * Resolve Invoice date delay for given parameters
     *
     * @param el EL expression to resolve
     * @param parameters A list of parameters
     * @return An integer value
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public static Integer resolveImmediateInvoiceDateDelay(String el, Object... parameters) throws InvalidELException{
        return evaluateExpression(el, Integer.class, parameters);
    }

    /**
     * @param billingRun
     * @param toMove
     */
    public void moveInvoicesByStatus(BillingRun billingRun, List<InvoiceStatusEnum> toMove) {
        List<Invoice> invoices = findInvoicesByStatusAndBR(billingRun.getId(), Arrays.asList(InvoiceStatusEnum.SUSPECT));
        
        if(!invoices.isEmpty()) {
            BillingRun nextBR = billingRunService.findOrCreateNextQuarantineBR(billingRun.getId(), null);
            getEntityManager().createNamedQuery("Invoice.moveToBR").setParameter("nextBR", nextBR).setParameter("billingRunId", billingRun.getId()).setParameter("statusList", toMove).executeUpdate();
        }
    }

    /**
     * @param toCancel
     */
    public void cancelInvoicesByStatus(BillingRun billingRun, List<InvoiceStatusEnum> toCancel) {
        List<Invoice> invoices = findInvoicesByStatusAndBR(billingRun.getId(), toCancel);
        invoices.stream().forEach(invoice -> cancelInvoiceWithoutDelete(invoice));
        billingRunService.updateBillingRunStatistics(billingRun);
    }

    public void cancelRejectedInvoicesByBR(BillingRun billingRun) {
        List<Invoice> invoices = findInvoicesByStatusAndBR(billingRun.getId(), Arrays.asList(InvoiceStatusEnum.REJECTED));
        invoices.stream().forEach(invoice -> cancelInvoiceWithoutDelete(invoice));
    }
    
    public void quarantineRejectedInvoicesByBR(BillingRun billingRun) {
        quarantineInvoicesByBR(billingRun, InvoiceStatusEnum.REJECTED, BillingRunStatusEnum.REJECTED);        
    }
    
    public void quarantineSuspectedInvoicesByBR(BillingRun billingRun) {
        quarantineInvoicesByBR(billingRun, InvoiceStatusEnum.SUSPECT, BillingRunStatusEnum.REJECTED);        
    }

    public void quarantineInvoicesByBR(BillingRun billingRun, InvoiceStatusEnum invoiceStatusEnum, BillingRunStatusEnum billingRunStatusEnum) {
        List<Invoice> invoices = findInvoicesByStatusAndBR(billingRun.getId(), Arrays.asList(invoiceStatusEnum));
        
        if(!invoices.isEmpty()) {
            List<Long> invoiceIds = new ArrayList<>();
            for (Invoice invoice : invoices) {
                invoiceIds.add(invoice.getId());
            }
            BillingRun nextBR = null;
            if(BillingRunStatusEnum.REJECTED.equals(billingRunStatusEnum)) {
                nextBR = billingRunService.findOrCreateNextQuarantineBR(billingRun.getId(), null);
                billingAccountService.getEntityManager().flush();
            }
            if (nextBR != null) {
                getEntityManager().createNamedQuery("Invoice.moveToBRByIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
                getEntityManager().createNamedQuery("InvoiceLine.moveToQuarantineBRByInvoiceIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
                getEntityManager().createNamedQuery("RatedTransaction.moveToQuarantineBRByInvoiceIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
                getEntityManager().createNamedQuery("SubCategoryInvoiceAgregate.moveToQuarantineBRByInvoiceIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
                
                billingRun = billingRunService.refreshOrRetrieve(billingRun);

                billingRunService.updateBillingRunStatistics(nextBR);
                billingRunService.updateBillingRunStatistics(billingRun);
            }            
        }
    }

    /**
     * Find by invoice number and invoice type id.
     *
     * @param invoiceNumber invoice's number
     * @param invoiceTypeId invoice's type
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
        final BigDecimal amountWithTax = resource.getAmountWithTax() != null ? resource.getAmountWithTax() : ZERO;
        final Date invoiceDate = resource.getInvoiceDate() != null ? resource.getInvoiceDate() : new Date();
        final String invoiceTypeCodeInput = resource.getInvoiceTypeCode();

        Order order = (Order) tryToFindByEntityClassAndCode(Order.class, resource.getOrderCode());
        BillingAccount billingAccount = (BillingAccount) tryToFindByEntityClassAndCode(BillingAccount.class, billingAccountCode);
        final String invoiceTypeCode = (invoiceTypeCodeInput != null && !invoiceTypeCodeInput.isEmpty() && !invoiceTypeCodeInput.isBlank()) ? resource.getInvoiceTypeCode() : "COM";
        InvoiceType invoiceType = (InvoiceType) tryToFindByEntityClassAndCode(InvoiceType.class, invoiceTypeCode);
        String comment = resource.getComment(); 
        
        Seller seller;
        if(resource.getSeller() != null) {
        	seller = (Seller) tryToFindByEntityClassAndCodeOrId(Seller.class, resource.getSeller().getCode(), resource.getSeller().getId());
        } else {
        	seller = billingAccount.getCustomerAccount().getCustomer().getSeller();
        }
        
        if(seller == null) {
        	throw new BusinessApiException("Billing account " + billingAccountCode + " doesn't have a default seller. Please provide a seller for this invoice.");
        }
        

        Invoice invoice = initBasicInvoiceInvoice(amountWithTax, invoiceDate, order, billingAccount, invoiceType, comment, seller,
                buildAutoMatching(resource.getAutoMatching(), invoiceType), resource.getDueDate());
        invoice.updateAudit(currentUser);
        getEntityManager().persist(invoice);
        postCreate(invoice);
        return invoice;
    }

    private boolean buildAutoMatching(Boolean autoMatchingInput, InvoiceType invoiceType){
        // auto-matching only for adj invoice
        boolean isAutoMatching = false;
        if (invoiceTypeService.getListAdjustementCode().contains(invoiceType.getCode())) {
            return autoMatchingInput != null ? autoMatchingInput : false;
        }
        return isAutoMatching;
    }

	public Invoice createBasicInvoiceFromSD(SecurityDeposit securityDepositInput) {
        InvoiceType advType = (InvoiceType) tryToFindByEntityClassAndCode(InvoiceType.class, "SECURITY_DEPOSIT");
        if(advType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, "SECURITY_DEPOSIT");
        }
        Seller defaultSeller = securityDepositInput.getSeller();
		Invoice invoice = initBasicInvoiceInvoice(securityDepositInput.getAmount(), new Date(), null, securityDepositInput.getBillingAccount(), advType, "", defaultSeller, false, null);
        invoice.updateAudit(currentUser);
        getEntityManager().persist(invoice);
        postCreate(invoice);
        return invoice;
    }
    
    /**
     * Initialize a new Invoice from Basic Invoice
     * @param amountWithTax Amount With Tax
     * @param invoiceDate Invoice Date
     * @param order {@link Order}
     * @param billingAccount {@link BillingAccount} 
     * @param advType {@link InvoiceType}
     * @param comment Comment
     * @return {@link Invoice}
     */
    private Invoice initBasicInvoiceInvoice(final BigDecimal amountWithTax, final Date invoiceDate, Order order, BillingAccount billingAccount, InvoiceType advType, String comment, Seller seller, boolean isAutoMatching, Date dueDate) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(advType);
        invoice.setBillingAccount(billingAccount);
        invoice.setOrder(order);
        invoice.setPaymentStatus(InvoicePaymentStatusEnum.NONE);
        invoice.setPaymentMethod(buildPaymentMethodForInitInvoice(billingAccount));
        invoice.setStartDate(invoiceDate);
        invoice.setAmountWithTax(amountWithTax);
        invoice.setRawAmount(amountWithTax);
        invoice.setAmountWithoutTax(amountWithTax);
        invoice.setAmountTax(BigDecimal.ZERO);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setDetailedInvoice(true);
        invoice.setNetToPay(amountWithTax);
		if(dueDate == null){
		    dueDate = calculateDueDate(invoice, billingAccount.getBillingCycle(), billingAccount, billingAccount.getCustomerAccount(), order);
	    }
        invoice.setDueDate(dueDate);
        setInitialCollectionDate(invoice, billingAccount.getBillingCycle(), null);
        invoice.setSeller(seller);
        invoice.setStatus(InvoiceStatusEnum.NEW);
        invoice.setAmountWithoutTaxBeforeDiscount(BigDecimal.ZERO);
        invoice.setComment(comment);
        invoice.setAutoMatching(isAutoMatching);
        return invoice;
    }

    private PaymentMethod buildPaymentMethodForInitInvoice(BillingAccount billingAccount) {
        if (billingAccount.getPaymentMethod() != null) {
            return billingAccount.getPaymentMethod();
        } else if (billingAccount.getCustomerAccount().getPreferredPaymentMethod() != null) {
            return billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        } else if (CollectionUtils.isNotEmpty(billingAccount.getCustomerAccount().getPaymentMethods())) {
            return billingAccount.getCustomerAccount().getPaymentMethods().get(0);
        }
        return null;
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
            Filter filter,Map<String, Object> filterParams, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft, PaymentMethod defaultPaymentMethod, Invoice existingInvoice, InvoiceProcessTypeEnum invoiceProcessTypeEnum, List<InvoiceLine> invoiceLines, String openOrderCode) throws BusinessException {
        if(CollectionUtils.isEmpty(invoiceLines)) {
            invoiceLines = existingInvoice != null ? invoiceLinesService.listInvoiceLinesByInvoice(existingInvoice.getId())
                    : getInvoiceLines(billingRun, entityToInvoice, filter,filterParams, firstTransactionDate, lastTransactionDate, isDraft);
        }
        boolean moreIls = invoiceLines.size() == rtPaginationSize;
        if (log.isDebugEnabled()) {
            log.debug("Split {} Invoice Lines for {}/{} in to billing account/seller/invoice type groups. {} invoice Lines to retrieve.", invoiceLines.size(), entityToInvoice.getClass().getSimpleName(),
                entityToInvoice.getId(), moreIls ? "More" : "No more");
        }
        Map<String, InvoiceLinesGroup> invoiceLinesGroup = new HashMap<>();

        BillingCycle billingCycle = defaultBillingCycle;
        PaymentMethod paymentMethod;
        if (defaultPaymentMethod == null && billingAccount != null) {
            defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
        }
        EntityManager em = getEntityManager();
        for (InvoiceLine invoiceLine : invoiceLines) {
            // Order can span multiple billing accounts and some Billing account-dependent values have to be recalculated
        	if ((entityToInvoice instanceof CommercialOrder || entityToInvoice instanceof Order || entityToInvoice instanceof CpqQuote || entityToInvoice instanceof BillingAccount ) && (billingAccount == null || !billingAccount.getId().equals(invoiceLine.getBillingAccount().getId()))) {
                billingAccount = invoiceLine.getBillingAccount();
                if (defaultPaymentMethod == null && billingAccount != null) {
                    defaultPaymentMethod = customerAccountService.getPreferredPaymentMethod(billingAccount.getCustomerAccount().getId());
                }
                if (defaultBillingCycle == null) {
                    billingCycle = billingAccount != null ? billingAccount.getBillingCycle() : null;
                }
            }
            
            InvoiceType invoiceType = null;
            
            if (invoiceProcessTypeEnum == null || invoiceProcessTypeEnum == InvoiceProcessTypeEnum.AUTOMATIC) {
                AccountingArticle accountingArticle = invoiceLine.getAccountingArticle();
                accountingArticle = accountingArticleService.refreshOrRetrieve(accountingArticle);
                if (!StringUtils.isBlank(accountingArticle.getInvoiceTypeEl())) {
                    String invoiceTypeCode = evaluateInvoiceTypeEl(accountingArticle.getInvoiceTypeEl(), invoiceLine);
                    invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
                }
                if (invoiceType == null) {
                    invoiceType = accountingArticle.getInvoiceType();
                }
                if (invoiceType == null) {
                    invoiceType = defaultInvoiceType;
                }
                if (invoiceType == null) {
                    invoiceType = determineInvoiceType(false, isDraft, billingCycle, billingRun, billingAccount);
                }
            }
            else {
                invoiceType = defaultInvoiceType;
            }
            
            paymentMethod = resolvePMethod(billingAccount, billingCycle, defaultPaymentMethod, invoiceLine);
            invoiceLine.setSubscription(subscriptionService.refreshOrRetrieve(invoiceLine.getSubscription()));
            Seller seller = getSelectedSeller(invoiceLine);
            String invoiceKey = billingAccount.getId() +  (seller!=null ? "_"+seller.getId():null) + "_" + invoiceType.getId() + "_" + paymentMethod.getId() + "_" + invoiceLine.getOpenOrderNumber();
            InvoiceLinesGroup ilGroup = invoiceLinesGroup.get(invoiceKey);
            if (ilGroup == null) {
                ilGroup = new InvoiceLinesGroup(billingAccount, billingCycle != null ? billingCycle : billingAccount.getBillingCycle(), seller, invoiceType, false, invoiceKey, paymentMethod, invoiceLine.getOpenOrderNumber());
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
    
    private String evaluateInvoiceTypeEl(String expression, InvoiceLine invoiceLine) throws InvalidELException {

        String invoiceTypeCode = null;

        if (!StringUtils.isBlank(expression)) {
            AccountingArticle accountingArticle = invoiceLine.getAccountingArticle();

            Map<Object, Object> contextMap = new HashMap<>();
            if (expression.indexOf("article") >= 0 || expression.indexOf("accountingArticle") >= 0) {
                contextMap.put("article", accountingArticle);
                contextMap.put("accountingArticle", accountingArticle);
            }
            if (expression.indexOf("il") >= 0 || expression.indexOf("invoiceLine") >= 0) {
                contextMap.put("il", invoiceLine);
                contextMap.put("invoiceLine", invoiceLine);
            }

            try {
                String value = evaluateExpression(expression, contextMap, String.class);
                if (value != null) {
                    invoiceTypeCode = value;
                }
            } catch (Exception e) {
                log.warn("Error when evaluate InvoiceTypeEl for accountingArticle id=" + accountingArticle.getId());
            }
        }

        return invoiceTypeCode;   
    }
    
    private PaymentMethod resolvePMethod(BillingAccount billingAccount, BillingCycle billingCycle, PaymentMethod defaultPaymentMethod, InvoiceLine invoiceLine) {
        if (BillingEntityTypeEnum.SUBSCRIPTION.equals(billingCycle.getType()) || (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) && billingCycle.isSplitPerPaymentMethod())) {
            if (invoiceLine.getSubscription() != null
                    && Objects.nonNull(invoiceLine.getSubscription().getPaymentMethod())) {
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

    private List<InvoiceLine> getInvoiceLines(BillingRun billingRun,IBillableEntity entityToInvoice, Filter filter,Map<String, Object> filterParams, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft) {
        return invoiceLinesService.listInvoiceLinesToInvoice(billingRun, entityToInvoice, firstTransactionDate, lastTransactionDate, filter,filterParams, rtPaginationSize);
    }
    
    private Seller getSelectedSeller(InvoiceLine invoiceLine) {
        Invoice invoice=invoiceLine.getInvoice();
        if(invoiceLine.getSubscription() != null) {
            if(invoiceLine.getSubscription().getSeller() != null)
                return invoiceLine.getSubscription().getSeller();
        }
        if(invoiceLine.getRatedTransactions() != null && !invoiceLine.getRatedTransactions().isEmpty())
        {
            if(invoiceLine.getRatedTransactions().get(0).getSeller() != null)
                return invoiceLine.getRatedTransactions().get(0).getSeller();
        }
        if(invoiceLine.getCommercialOrder() != null) {
            if(invoiceLine.getCommercialOrder().getSeller()!=null)
                return invoiceLine.getCommercialOrder().getSeller();
        }
        if(invoiceLine.getQuote() != null) {
            if(invoiceLine.getQuote().getSeller()!=null) {
                return invoiceLine.getQuote().getSeller();
            }
        }
        if(invoice!=null && invoice.getCpqQuote()!=null) {
            if(invoice.getCpqQuote().getSeller()!=null) {
                return invoice.getCpqQuote().getSeller();
            }
        }
        if (invoiceLine.getBillingAccount() != null) {
            return invoiceLine.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }
        return null;
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
        return createAggregatesAndInvoiceWithIL(entityToInvoice, billingRun, filter, null, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, automaticInvoiceCheck, isDepositInvoice);
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
    public List<Invoice> createAggregatesAndInvoiceWithIL(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter,Map<String, Object> filterParams, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck, boolean isDepositInvoice) throws BusinessException {
        return createAggregatesAndInvoiceUsingIL(entityToInvoice, billingRun, filter, filterParams, invoiceDate, firstTransactionDate, lastTransactionDate, minAmountForAccounts, isDraft, automaticInvoiceCheck, isDepositInvoice, null,null);
    }

    public List<Invoice> createAggregatesAndInvoiceUsingIL(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter,Map<String, Object> filterParams, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
                                                           MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck, boolean isDepositInvoice, List<InvoiceLine> invoiceLines, String openOrderCode) throws BusinessException {
        return createAggregatesAndInvoiceUsingILAndSubscription(entityToInvoice, billingRun, filter,filterParams, invoiceDate, firstTransactionDate, lastTransactionDate,
                minAmountForAccounts, isDraft, automaticInvoiceCheck, isDepositInvoice, invoiceLines, openOrderCode, null);
    }
    
    /**
     * Creates invoices and their aggregates
     * if the invoiceLinesList is given, it will be directly used to gcreate invoice
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
     * @param invoiceLines 
     * @param openOrderCode 
     * @return A list of created invoices
     * @throws BusinessException business exception
     */
    public List<Invoice> createAggregatesAndInvoiceUsingILAndSubscription(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter,Map<String, Object> filterParams, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate,
            MinAmountForAccounts minAmountForAccounts, boolean isDraft, boolean automaticInvoiceCheck, boolean isDepositInvoice, List<InvoiceLine> invoiceLines, String openOrderCode, Subscription subscription) throws BusinessException {
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
            }else {
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

            return createAggregatesAndInvoiceFromIlsAndSubscription(entityToInvoice, billingRun, filter,filterParams, invoiceDate, firstTransactionDate, lastTransactionDate, isDraft, billingCycle, ba, paymentMethod, invoiceType, balance,
                automaticInvoiceCheck, hasMin, null, null, invoiceLines, openOrderCode, subscription);
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
        return emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Invoice> createAggregatesAndInvoiceFromIls(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter, Map<String, Object> filterParams, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft,
                                                            BillingCycle defaultBillingCycle, BillingAccount billingAccount, PaymentMethod defaultPaymentMethod, InvoiceType defaultInvoiceType, BigDecimal balance, boolean automaticInvoiceCheck, boolean hasMin,
                                                            Invoice existingInvoice, InvoiceProcessTypeEnum invoiceProcessTypeEnum, List<InvoiceLine> existingInvoiceLines, String openOrderCode) throws BusinessException {
        return createAggregatesAndInvoiceFromIlsAndSubscription(entityToInvoice, billingRun, filter, filterParams, invoiceDate, firstTransactionDate, lastTransactionDate, isDraft,
                defaultBillingCycle, billingAccount, defaultPaymentMethod, defaultInvoiceType, balance, automaticInvoiceCheck, hasMin,
                existingInvoice, invoiceProcessTypeEnum, existingInvoiceLines, openOrderCode, null);
    }

    @SuppressWarnings("unchecked")
    private List<Invoice> createAggregatesAndInvoiceFromIlsAndSubscription(IBillableEntity entityToInvoice, BillingRun billingRun, Filter filter,Map<String, Object> filterParams, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate, boolean isDraft,
            BillingCycle defaultBillingCycle, BillingAccount billingAccount, PaymentMethod defaultPaymentMethod, InvoiceType defaultInvoiceType, BigDecimal balance, boolean automaticInvoiceCheck, boolean hasMin,
            Invoice existingInvoice, InvoiceProcessTypeEnum invoiceProcessTypeEnum, List<InvoiceLine> existingInvoiceLines, String openOrderCode, Subscription subscription) throws BusinessException {
        List<Invoice> invoiceList = new ArrayList<>();
        boolean moreInvoiceLinesExpected = true;
        Map<String, InvoiceAggregateProcessingInfo> invoiceLineGroupToInvoiceMap = new HashMap<>();

        boolean allIlsInOneRun = true;
 
        while (moreInvoiceLinesExpected) {

        	if (entityToInvoice instanceof Order) {
                billingAccount = null;
                defaultInvoiceType = null;
            }
                // TODO check getInvoiceLinesGroups(entityToInvoice
            InvoiceLinesToInvoice iLsToInvoice = getInvoiceLinesGroups(entityToInvoice, billingAccount, billingRun, defaultBillingCycle, defaultInvoiceType, filter,filterParams, firstTransactionDate, lastTransactionDate, isDraft,
                defaultPaymentMethod, existingInvoice, invoiceProcessTypeEnum, existingInvoiceLines, openOrderCode);
            List<InvoiceLinesGroup> invoiceLinesGroupsPaged = iLsToInvoice.invoiceLinesGroups;
            moreInvoiceLinesExpected = iLsToInvoice.moreInvoiceLines;
            if (moreInvoiceLinesExpected) {
                allIlsInOneRun = false;
            }

            if (invoiceLineGroupToInvoiceMap.isEmpty() && invoiceLinesGroupsPaged.isEmpty()) {
                log.warn("Account {}/{} has no billable transactions", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId());
                if(existingInvoice != null
                        && (existingInvoice.getInvoiceLines() == null || existingInvoice.getInvoiceLines().isEmpty())) {
                    cleanInvoiceAggregates(existingInvoice.getId());
                    initAmounts(existingInvoice.getId());
                    existingInvoice = refreshOrRetrieve(existingInvoice);
                }
                return new ArrayList<>();
            } else if (!invoiceLinesGroupsPaged.isEmpty()) {
                for (InvoiceLinesGroup invoiceLinesGroup : invoiceLinesGroupsPaged) {

                    if (entityToInvoice instanceof Order || entityToInvoice instanceof CommercialOrder) {
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
                            initAmounts(existingInvoice.getId());
                            invoiceAggregateProcessingInfo.invoice = refreshOrRetrieve(existingInvoice);
                        } else {
                            // TODO check instantiateInvoice(entityToInvoice
                            invoiceAggregateProcessingInfo.invoice = instantiateInvoice(entityToInvoice, invoiceLinesGroup.getBillingAccount(), invoiceLinesGroup.getSeller().getId(), billingRun, invoiceDate, isDraft,
                                invoiceLinesGroup.getBillingCycle(), invoiceLinesGroup.getPaymentMethod(), invoiceLinesGroup.getInvoiceType(), invoiceLinesGroup.isPrepaid(), automaticInvoiceCheck);
                        }
                        invoiceAggregateProcessingInfo.invoice.setOpenOrderNumber(invoiceLinesGroup.getOpenOrderNumber());
                        invoiceAggregateProcessingInfo.invoice.setDueBalance(balance);
                        invoiceList.add(invoiceAggregateProcessingInfo.invoice);
                    }

                    Invoice invoice = invoiceAggregateProcessingInfo.invoice;
                    invoice.setHasMinimum(hasMin);
                    if (subscription != null) {
                        invoice.setSubscription(subscription);
                    }

                    // TODO check  appendInvoiceAggregatesIL(entityToInvoice,
                    appendInvoiceAggregatesIL(entityToInvoice, invoiceLinesGroup.getBillingAccount(), invoice, invoiceLinesGroup.getInvoiceLines(), false, invoiceAggregateProcessingInfo, !allIlsInOneRun);
                    if(invoice.getOpenOrderNumber() != null) {
                        OpenOrder openOrder = openOrderService.findByOpenOrderNumber(invoice.getOpenOrderNumber());
                        ofNullable(openOrder)
                                .map(OpenOrder::getExternalReference)
                                .ifPresent(invoice::setExternalRef);
                        BigDecimal initialAmount = ofNullable(openOrder.getInitialAmount()).orElse(ZERO);
                        BigDecimal invoicedAmount = computeInvoicedAmount(openOrder, invoiceLinesGroup.getInvoiceLines());
                        openOrderService.updateBalance(openOrder.getId(), initialAmount.subtract(invoicedAmount));
                    }
                    List<Object[]> ilMassUpdates = new ArrayList<>();
                    List<Object[]> ilUpdates = new ArrayList<>();

                    for (SubCategoryInvoiceAgregate subAggregate : invoiceAggregateProcessingInfo.subCategoryAggregates.values()) {
                        if (subAggregate.getInvoiceablesToAssociate() == null) {
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

                    if (invoice.getDueDate() == null) {
                        setInvoiceDueDate(invoice, invoiceLinesGroup.getBillingCycle());
                    }
                    setInitialCollectionDate(invoice, invoiceLinesGroup.getBillingCycle(), billingRun);

                    EntityManager em = getEntityManager();
                    invoice.setNewInvoicingProcess(true);
                    invoice.setHasMinimum(true);
                    if (invoice.getId() == null) {
                        if (EntityManagerProvider.isDBOracle()) {
                        	// temporary set random string in the invoice number to avoid violate constraint uk_billing_invoice on oracle while running InvoicingJobV2
                        	invoice.setInvoiceNumber(UUID.randomUUID().toString());
                        }
                        this.create(invoice);
                    } else {
                        for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
                            if (invoiceAggregate.getId() == null) {
                                em.persist(invoiceAggregate);
                            }
                        }
                    }

                    em.flush();

                    final int maxValue =
                            ParamBean.getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
                    Date now = new Date();
                    for (Object[] aggregateAndILIds : ilMassUpdates) {
                        List<Long> ilIds = (List<Long>) aggregateAndILIds[1];
                        ListUtils.partition(ilIds, maxValue).forEach(ids -> {
                            SubCategoryInvoiceAgregate subCategoryAggregate = (SubCategoryInvoiceAgregate) aggregateAndILIds[0];
                            em.createNamedQuery("InvoiceLine.updateWithInvoice")
                                    .setParameter("billingRun", billingRun)
                                    .setParameter("invoice", invoice)
                                    .setParameter("now", now)
                                    .setParameter("invoiceAgregateF", subCategoryAggregate)
                                    .setParameter("ids", ids)
                                    .executeUpdate();
                        });
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

                    if (!ilMassUpdates.isEmpty() || !ilUpdates.isEmpty()) {
                        em.flush();
                        em.clear();
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

                if (orderNums != null && !orderNums.isEmpty()) {
                List<Order> orders = orderService.findByCodeOrExternalId(orderNums);
                if (!orders.isEmpty()) {
                    invoiceAggregateProcessingInfo.invoice.setOrders(orders);
                }
            }

            }

            if (entityToInvoice instanceof CommercialOrder) {
                orderNums.add(((CommercialOrder) entityToInvoice).getOrderNumber());

                if (orderNums != null && !orderNums.isEmpty()) {
                List<CommercialOrder> orders = commercialOrderService.findByCodeOrExternalId(orderNums);
                if (!orders.isEmpty()) {
                    invoiceAggregateProcessingInfo.invoice.setCommercialOrder(orders.get(0));
                }
            }
            }








            invoiceAggregateProcessingInfo.invoice.assignTemporaryInvoiceNumber();
            applyAutomaticInvoiceCheck(invoiceAggregateProcessingInfo.invoice, automaticInvoiceCheck);
            postCreate(invoiceAggregateProcessingInfo.invoice);
        }
        applyExchangeRateToInvoiceLineAndAggregate(invoiceList);
        return invoiceList;

    }

    private BigDecimal computeInvoicedAmount(OpenOrder openOrder, List<InvoiceLine> invoiceLines) {
        BigDecimal invoicedAmount = invoiceLinesService
                .invoicedAmountByOpenOrder(openOrder.getOpenOrderNumber(), openOrder.getBillingAccount().getId());
        if(invoiceLines != null && !invoiceLines.isEmpty()) {
            invoicedAmount = invoicedAmount.add(invoiceLines.stream()
                                    .map(InvoiceLine::getAmountWithTax)
                                    .reduce(ZERO, BigDecimal::add));
        }
        return invoicedAmount;
    }

    private void applyExchangeRateToInvoiceLineAndAggregate(List<Invoice> invoices) {
        invoices = refreshOrRetrieve(invoices);
        invoices.forEach(invoice -> refreshInvoiceLineAndAggregateAmounts(invoice));
    }

    /**
     * initialize invoice amounts : amountWithTax, amountWithoutTax, amountToPay, taxAmount, amount
     *
     * @param invoiceId
     */
    private void initAmounts(Long invoiceId) {
        getEntityManager()
                .createNamedQuery("Invoice.initAmounts")
                .setParameter("invoiceId", invoiceId)
                .executeUpdate();
    }

    /**
     * delete invoice aggregates
     * 
     * @param invoiceId
     */
    public void cleanInvoiceAggregates(Long invoiceId) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteInvoiceAggrByInvoice").setParameter("invoiceId", invoiceId).executeUpdate();
        getEntityManager().createNamedQuery("InvoiceLine.deleteInvoiceAggrByInvoice").setParameter("invoiceId", invoiceId).executeUpdate();
        getEntityManager().createNamedQuery("InvoiceAggregate.updateByInvoiceIds").setParameter("invoicesIds", Arrays.asList(invoiceId)).executeUpdate();
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
        if(!isAggregateByUA) {
            isAggregateByUA=billingAccount.getUsersAccounts().size()==1;
        }
        boolean isEnterprise = appProvider.isEntreprise();
        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();
        Boolean isExonerated = billingAccount.isExoneratedFromtaxes();
        if (isExonerated == null) {
            isExonerated = billingAccountService.isExonerated(billingAccount);
        }
        int rtRounding = appProvider.getRounding();
        RoundingModeEnum rtRoundingMode = appProvider.getRoundingMode();
        Tax defaultTax = isExonerated ? taxService.getZeroTax() : null;

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
        boolean anyILUseSpecificConversion = invoiceLines.stream().anyMatch(InvoiceLine::isUseSpecificPriceConversion);
        for (InvoiceLine invoiceLine : invoiceLines) {

            addFixedDiscount(invoiceLine);
            invoiceLine.setSubscription(subscriptionService.refreshOrRetrieve(invoiceLine.getSubscription()));
            invoiceLine.setAccountingArticle(accountingArticleService.refreshOrRetrieve(invoiceLine.getAccountingArticle()));
            InvoiceSubCategory invoiceSubCategory = invoiceLine.getAccountingArticle().getInvoiceSubCategory();

            scaKey = invoiceSubCategory.getId().toString();
            if (isAggregateByUA && invoiceLine.getSubscription() != null) {
                scaKey = (invoiceLine.getSubscription().getUserAccount() != null ? invoiceLine.getSubscription().getUserAccount().getId() : "") + "_" + scaKey;
            }

            Tax tax = invoiceLine.getTax();
            UserAccount userAccount = invoiceLine.getSubscription() == null ? null : invoiceLine.getSubscription().getUserAccount();
            if(userAccount==null) {
                userAccount=invoiceLine.getBillingAccount().getUsersAccounts().size()==1?invoiceLine.getBillingAccount().getUsersAccounts().get(0):null;
            }
            // Check if tax has to be recalculated. Does not apply to RatedTransactions that had tax explicitly set/overridden
            if (calculateTaxOnSubCategoryLevel && !invoiceLine.isTaxOverridden()) {

                TaxClass taxClass = invoiceLine.getAccountingArticle().getTaxClass();
                String taxChangeKey = billingAccount.getId() + "_" + taxClass.getId();

                Object[] changedToTax = taxChangeMap.get(taxChangeKey);
                if (changedToTax == null) {
                    defaultTax = defaultTax == null ? tax : defaultTax;
                    Object[] applicableTax = taxMappingService.checkIfTaxHasChanged(tax, isExonerated, invoice.getSeller(),invoice.getBillingAccount(),invoice.getInvoiceDate(), taxClass, userAccount, defaultTax);
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

            if (!(entityToInvoice instanceof Order || entityToInvoice instanceof CommercialOrder) && invoiceLine.getOrderNumber() != null) {
                orderNumbers.add(invoiceLine.getOrderNumber());
            }



            if (taxWasRecalculated || invoiceLine.isTaxRecalculated()) {
                invoiceLine.setTax(tax);
                invoiceLine.setTaxRate(tax.getPercent());
                invoiceLine.computeDerivedAmounts(isEnterprise, rtRounding, rtRoundingMode);
            }

            scAggregate.addInvoiceLine(invoiceLine, isEnterprise, true);
            if (anyILUseSpecificConversion) {
                BigDecimal appliedRate = invoice.getBillingAccount().getTradingCurrency().getCurrentRate();
                scAggregate.setUseSpecificPriceConversion(true);
                if (invoiceLine.isUseSpecificPriceConversion()) {
                    scAggregate.addTransactionAmountWithoutTax(invoiceLine.getTransactionalAmountWithoutTax());
                    scAggregate.addTransactionAmountWithTax(invoiceLine.getTransactionalAmountWithTax());
                    scAggregate.addTransactionAmountTax(invoiceLine.getTransactionalAmountTax());
                    scAggregate.getAmountsByTax().get(invoiceLine.getTax()).addTransactionalAmounts(invoiceLine.getTransactionalAmountWithoutTax(),
                            invoiceLine.getTransactionalAmountWithTax(),
                            invoiceLine.getTransactionalAmountTax());
                } else {
                    scAggregate.addTransactionAmountWithoutTax(toTransactional(invoiceLine.getAmountWithoutTax(), appliedRate));
                    scAggregate.addTransactionAmountWithTax(toTransactional(invoiceLine.getAmountWithTax(), appliedRate));
                    scAggregate.addTransactionAmountTax(toTransactional(invoiceLine.getAmountTax(), appliedRate));
                    scAggregate.getAmountsByTax().get(invoiceLine.getTax()).addTransactionalAmounts(toTransactional(invoiceLine.getAmountWithoutTax(), appliedRate),
                            toTransactional(invoiceLine.getAmountWithTax(), appliedRate),
                            toTransactional(invoiceLine.getAmountTax(), appliedRate));
                }
            } else {
                scAggregate.addTransactionAmountWithoutTax(invoiceLine.getTransactionalAmountWithoutTax());
                scAggregate.addTransactionAmountWithTax(invoiceLine.getTransactionalAmountWithTax());
                scAggregate.addTransactionAmountTax(invoiceLine.getTransactionalAmountTax());
                scAggregate.getAmountsByTax().get(invoiceLine.getTax()).addTransactionalAmounts(invoiceLine.getTransactionalAmountWithoutTax(),
                        invoiceLine.getTransactionalAmountWithTax(),
                        invoiceLine.getTransactionalAmountTax());
            }
        }
        if (moreInvoiceLinesExpected) {
            return;
        }

        addDiscountCategoryAndTaxAggregates(invoice, subCategoryAggregates.values());
        if(invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setAmountWithoutTaxBeforeDiscount(invoice.getAmountWithoutTax());
        }
    }

    private BigDecimal toTransactional(BigDecimal amount, BigDecimal rate) {
        return amount != null ? amount.multiply(rate) : ZERO;
    }

    private void addFixedDiscount(InvoiceLine invoiceLine) {
        if(invoiceLine.getDiscountPlan() != null && !DiscountPlanTypeEnum.INVOICE_LINE.equals(invoiceLine.getDiscountPlan().getDiscountPlanType()) && invoiceLine.getDiscountPlan().getDiscountPlanItems() != null && !invoiceLine.getDiscountPlan().getDiscountPlanItems().isEmpty()) {
        	List<DiscountPlanItem> discountPlanItems = discountPlanItemService.getFixedDiscountPlanItemsByDP(invoiceLine.getDiscountPlan().getId());
            for(DiscountPlanItem discountPlanItem : discountPlanItems) {
            if(discountPlanItem != null && discountPlanItemService.isDiscountPlanItemApplicable(invoiceLine.getBillingAccount(),discountPlanItem,invoiceLine,invoiceLine.getAccountingArticle(),invoiceLine.getSubscription()) ) {
                invoiceLine.setAmountWithoutTax(invoiceLine.getAmountWithoutTax().subtract(discountPlanItem.getDiscountValue()));
                invoiceLine.setAmountWithTax(invoiceLine.getAmountWithoutTax().add(invoiceLine.getAmountTax()));
                invoiceLine.setDiscountAmount(invoiceLine.getDiscountAmount().add(discountPlanItem.getDiscountValue()));
            }
            }
        }
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
     * @param invoiceResource
     * @param skipValidation
     * @param isDraft
     * @return invoice
     * @param isIncludeBalance
     * @param isAutoValidation
     * @param isVirtual
     * @param entity
     * @throws EntityDoesNotExistsException
     * @throws BusinessApiException
     * @throws BusinessException
     * @throws InvalidParameterException
     */
    public Invoice createInvoiceV11(org.meveo.apiv2.billing.Invoice invoiceResource,
                                    boolean skipValidation, boolean isDraft, boolean isVirtual,
                                    Boolean isIncludeBalance, Boolean isAutoValidation, Invoice entity)
            throws EntityDoesNotExistsException, BusinessApiException, BusinessException, InvalidParameterException {

        Seller seller = (Seller) tryToFindByEntityClassAndCode(Seller.class, invoiceResource.getSellerCode());
        BillingAccount billingAccount = (BillingAccount) tryToFindByEntityClassAndCode(BillingAccount.class, invoiceResource.getBillingAccountCode());
        InvoiceType invoiceType = (InvoiceType) tryToFindByEntityClassAndCode(InvoiceType.class, invoiceResource.getInvoiceTypeCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceResource.getInvoiceTypeCode());
        }
        Map<Long, TaxInvoiceAgregate> taxInvoiceAggregateMap = new HashMap<>();
        boolean isEnterprise = appProvider.isEntreprise();
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        Auditable auditable = new Auditable(currentUser);
        Map<InvoiceSubCategory, List<InvoiceLine>> existingInvoiceLinesToLinkMap
                = extractMappedInvoiceLinesTolink(invoiceResource, billingAccount);

        Map<InvoiceCategory, List<InvoiceSubCategory>> subCategoryMap = new HashMap<>();
        Invoice invoice = this.initValidatedInvoice(invoiceResource, billingAccount, invoiceType, seller, isDraft);
        if(entity != null && entity.getCfValues() != null) {
            invoice.setCfValues(entity.getCfValues());
        }

        if (invoiceResource.getDiscountPlan() != null) {
            final Long dpId = invoiceResource.getDiscountPlan().getId();
            DiscountPlan discountPlan = (DiscountPlan) tryToFindByEntityClassAndId(DiscountPlan.class, dpId);
            invoice.setDiscountPlan(discountPlan);
        }
        validateInvoiceResourceAgregates(invoiceResource);
        for (org.meveo.apiv2.billing.CategoryInvoiceAgregate catInvAgr : invoiceResource.getCategoryInvoiceAgregates()) {
            UserAccount userAccount = extractUserAccount(billingAccount, catInvAgr.getUserAccountCode());
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(catInvAgr.getCategoryInvoiceCode());
            if (invoiceCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, catInvAgr.getCategoryInvoiceCode());
            }
            CategoryInvoiceAgregate invoiceAggregateCat = initCategoryInvoiceAgregate(billingAccount, auditable, invoice,
                    userAccount, invoiceCategory, catInvAgr.getListSubCategoryInvoiceAgregate().size(), catInvAgr.getDescription());

            for (org.meveo.apiv2.billing.SubCategoryInvoiceAgregate subCatInvAgr : catInvAgr.getListSubCategoryInvoiceAgregate()) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubcategoryService.findByCode(subCatInvAgr.getInvoiceSubCategoryCode());
                if (invoiceSubCategory == null) {
                    throw new EntityDoesNotExistsException(InvoiceSubCategory.class, subCatInvAgr.getInvoiceSubCategoryCode());
                }
                SubCategoryInvoiceAgregate invoiceAggregateSubCat = initSubCategoryInvoiceAgregate(auditable, invoice,
                        userAccount, invoiceAggregateCat, subCatInvAgr.getDescription(), invoiceSubCategory);
                createAndLinkILsFromDTO(seller, billingAccount, isEnterprise, invoiceRounding, invoiceRoundingMode,
                        invoice, userAccount, subCatInvAgr, invoiceSubCategory, invoiceAggregateSubCat);
                linkExistingILs(invoiceResource, existingInvoiceLinesToLinkMap,
                        isEnterprise, invoice, userAccount, invoiceSubCategory, invoiceAggregateSubCat);
                saveInvoiceSubCatAndILs(invoice, invoiceAggregateSubCat, subCatInvAgr, billingAccount,
                        taxInvoiceAggregateMap, isEnterprise, auditable, invoiceRounding, invoiceRoundingMode);
                addSubCategoryAmountsToCategory(invoiceAggregateCat, invoiceAggregateSubCat);
            }

            if (!existingInvoiceLinesToLinkMap.isEmpty() && subCategoryMap.containsKey(invoiceCategory)) {
                List<InvoiceSubCategory> subCategories = subCategoryMap.get(invoiceCategory);
                linkILsAndSubCats(billingAccount, taxInvoiceAggregateMap, isEnterprise, invoiceRounding, invoiceRoundingMode,
                        auditable, existingInvoiceLinesToLinkMap, invoice, userAccount, invoiceAggregateCat, subCategories);
            }
            getEntityManager().flush();
            addCategoryAmountsToInvoice(invoice, invoiceAggregateCat);
            subCategoryMap.remove(invoiceCategory);
        }

        linkILsHavingCategoryOutOfInput(billingAccount, isEnterprise, auditable, existingInvoiceLinesToLinkMap,
                subCategoryMap, invoice, taxInvoiceAggregateMap, invoiceRounding, invoiceRoundingMode);

        invoice = finaliseInvoiceCreation(invoiceResource, isEnterprise,
                invoiceRounding, invoiceRoundingMode, invoice, isAutoValidation, isIncludeBalance);
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
        return new HashMap<>();
    }

    private void createAndLinkILsFromDTO(Seller seller, BillingAccount billingAccount, boolean isEnterprise, int invoiceRounding, RoundingModeEnum invoiceRoundingMode, Invoice invoice, UserAccount userAccount,
            org.meveo.apiv2.billing.SubCategoryInvoiceAgregate subCatInvAgr, InvoiceSubCategory invoiceSubCategory, SubCategoryInvoiceAgregate invoiceAgregateSubcat) {
        if (subCatInvAgr.getInvoiceLines() != null) {
            for (org.meveo.apiv2.billing.InvoiceLine invoiceLineRessource : subCatInvAgr.getInvoiceLines()) {
                InvoiceLine il = invoiceLinesService.initInvoiceLineFromResource(invoiceLineRessource, null);
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
        if (InvoiceStatusEnum.NEW.equals(status)) {            
            toUpdate.setStatus(InvoiceStatusEnum.DRAFT);
            toUpdate.assignTemporaryInvoiceNumber();
        }
        if (input.getComment() != null) {
            toUpdate.setComment(input.getComment());
        }
        if (input.getExternalRef() != null) {
            toUpdate.setExternalRef(input.getExternalRef());
        }
        if (input.getInvoiceDate() != null) {
            toUpdate.setInvoiceDate(input.getInvoiceDate());
            if(!toUpdate.isUseCurrentRate()) {

                BigDecimal lastAppliedRate = getCurrentRate(toUpdate, input.getInvoiceDate()) != null ? getCurrentRate(toUpdate, input.getInvoiceDate()) : ONE;

                toUpdate.setLastAppliedRate(lastAppliedRate);
                toUpdate.setLastAppliedRateDate(new Date());

                refreshInvoiceLineAndAggregateAmounts(toUpdate);
            }
        }

        //if the dueDate == null, it will be calculated at the level of the method invoiceService.calculateInvoice(updateInvoice)
        toUpdate.setDueDate(input.getDueDate());
        
        if (invoiceResource.getPaymentMethod() != null) {
            final Long pmId = invoiceResource.getPaymentMethod().getId();
            PaymentMethod pm = (PaymentMethod) tryToFindByEntityClassAndId(PaymentMethod.class, pmId);
            toUpdate.setPaymentMethod(pm);
        }

        if (invoiceResource.getSubscription() != null && invoiceResource.getSubscription().getId() != null) {
            // Subscription can only be edited for manual invoice in draft-like status (NEW, DRAFT, REJECTED, SUSPECT)
            if (!(toUpdate.getStatus() == InvoiceStatusEnum.NEW || toUpdate.getStatus() == InvoiceStatusEnum.DRAFT ||
                    toUpdate.getStatus() == InvoiceStatusEnum.REJECTED ||toUpdate.getStatus() == InvoiceStatusEnum.SUSPECT)) {
                throw new BusinessException("Subscription can only be edited for manual invoice in �draft-like� status (NEW, DRAFT, REJECTED, SUSPECT)");
            }

            // Check if the invoice is not generation by a billingRun
            if (toUpdate.getBillingRun()!=null) {
                throw new BusinessException("Subscription is not edited for invoices generated by a billing runs");
            }

            Subscription subscription = (Subscription) tryToFindByEntityClassAndId(Subscription.class, invoiceResource.getSubscription().getId());

            // check if the input subscription billing account is the same of invoice one
            if (subscription.getUserAccount().getBillingAccount() !=null &&
                    !subscription.getUserAccount().getBillingAccount().getCode().equals(toUpdate.getBillingAccount().getCode())) {
                throw new BusinessException("Subscription with code " + subscription.getCode()
                        + " is not linked to the same Invoice Customer " + toUpdate.getBillingAccount().getCode());
            }

            toUpdate.setSubscription(subscription);
        } else {
            toUpdate.setSubscription(null);
        }

        if (CollectionUtils.isNotEmpty(invoiceResource.getListLinkedInvoices())) {
            List<Long> toUpdateLinkedInvoice = toUpdate.getLinkedInvoices().stream()
                    .map(li -> li.getLinkedInvoiceValue().getId()).collect(Collectors.toList());
            
            List<Long> linkedInvoiceToAdd = new ArrayList<>(invoiceResource.getListLinkedInvoices());
            List<Long> linkedInvoiceToRemove = new ArrayList<>(toUpdateLinkedInvoice);
            linkedInvoiceToAdd.removeAll(toUpdateLinkedInvoice);
            for (Long invoiceId : linkedInvoiceToAdd) {
                Invoice invoiceTmp = findById(invoiceId);
                if (invoiceTmp == null) {
                    throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
                }
                if(invoiceTmp.getInvoiceType() != null && invoiceTmp.getInvoiceType().getCode().equals("ADV")) {
                    throw new BusinessApiException("The invoice of type Advance can not be linked manually");
                }
                if (!toUpdate.getInvoiceType().getAppliesTo().contains(invoiceTmp.getInvoiceType())) {
                    throw new BusinessApiException("InvoiceId " + invoiceId + " cant be linked");
                }
                LinkedInvoice linkedInvoice = new LinkedInvoice(invoiceTmp, toUpdate);
                if (!invoiceTmp.getLinkedInvoices().contains(linkedInvoice)) {
                    toUpdate.getLinkedInvoices().add(linkedInvoice);
                }

            }
            linkedInvoiceToRemove.removeAll(invoiceResource.getListLinkedInvoices());
            for(Long invoiceId : linkedInvoiceToRemove) {
                Invoice invoiceTmp = findById(invoiceId);
                if (invoiceTmp == null) {
                    throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
                }
                if(invoiceTmp.getInvoiceType() != null && invoiceTmp.getInvoiceType().getCode().equals("ADV")) {
                    throw new BusinessApiException("The invoice of type Advance can not be linked manually");
                }
                if (!toUpdate.getInvoiceType().getAppliesTo().contains(invoiceTmp.getInvoiceType())) {
                    throw new BusinessApiException("InvoiceId " + invoiceId + " cant be linked");
                }
                linkedInvoiceService.deleteByIdInvoiceAndLinkedInvoice(toUpdate.getId(), Arrays.asList(invoiceId));
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

        if(invoiceResource.getCpqQuote()!=null) {
            final Long cpqQuoteId = invoiceResource.getCpqQuote().getId();
            CpqQuote cpqQuote = (CpqQuote)tryToFindByEntityClassAndId(CpqQuote.class, cpqQuoteId);
            toUpdate.setCpqQuote(cpqQuote);
        }
        if (input.getCfValues() != null) {
            toUpdate.setCfValues(input.getCfValues());
        }

        if(invoiceResource.getDiscount() == null && toUpdate.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            toUpdate.setDiscountAmount(BigDecimal.ZERO);
        }
        
        
        if(toUpdate.getInvoiceType() != null) {
           InvoiceType advType = invoiceTypeService.findByCode("ADV");
           if(advType != null && "ADV".equals(toUpdate.getInvoiceType().getCode())) {
               boolean isAccountingArticleAdt = toUpdate.getInvoiceLines() != null && toUpdate.getInvoiceLines().stream().allMatch(il -> il.getAccountingArticle() != null && il.getAccountingArticle().getCode().equals("ADV-STD"));
               if(!isAccountingArticleAdt) {
                   throw new BusinessException("Invoice of type " + invoiceTypeService.getListAdjustementCode() + ", must use ADV-STD article");
               }
               toUpdate.setInvoiceBalance(invoiceResource.getAmountWithTax());
               BigDecimal currentRate = getCurrentRate(toUpdate,toUpdate.getInvoiceDate());
               toUpdate.setTransactionalInvoiceBalance(currentRate != null && invoiceResource.getAmountWithTax() != null ? currentRate.multiply(invoiceResource.getAmountWithTax()) : invoiceResource.getAmountWithTax());
           }
        }
        toUpdate.setAutoMatching(buildAutoMatching(invoiceResource.getAutoMatching(), toUpdate.getInvoiceType()));
        return update(toUpdate);
    }

    public BigDecimal getCurrentRate(Invoice toUpdate, Date exchangeDate) {
        BigDecimal currentRate = null;
        if (toUpdate.getTradingCurrency() != null) {
            TradingCurrency tradingCurrency = tradingCurrencyService.refreshOrRetrieve(toUpdate.getTradingCurrency());
            ExchangeRate exchangeRate = tradingCurrency.getExchangeRate(exchangeDate);
            if (exchangeRate != null) {
                currentRate = exchangeRate.getExchangeRate();
            }
        }
        return currentRate;
    }

    public void refreshAdvanceInvoicesConvertedAmount(Invoice toUpdate, BigDecimal lastAppliedRate) {
        if(lastAppliedRate == null) return;
        toUpdate.getLinkedInvoices().stream().filter(linkedInvoice ->
                        InvoiceTypeEnum.ADVANCEMENT_PAYMENT.equals(linkedInvoice.getType())
                                && linkedInvoice.getLinkedInvoiceValue() != null &&
                                linkedInvoice.getLinkedInvoiceValue().getStatus().equals(InvoiceStatusEnum.VALIDATED))
                .forEach(linkedInvoice -> linkedInvoice.setTransactionalAmount(linkedInvoice.getAmount() != null ? linkedInvoice.getAmount().multiply(lastAppliedRate) : null));
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
        invoiceService.updateBillingRunStatistics(invoice);
        return createAggregatesAndInvoiceFromIls(billingAccount, billingAccount.getBillingRun(), null,null, invoice.getInvoiceDate(), null, null, invoice.isDraft(), billingAccount.getBillingCycle(), billingAccount,
            billingAccount.getPaymentMethod(), invoice.getInvoiceType(), null, false, false, invoice, InvoiceProcessTypeEnum.MANUAL,null, null);
    }

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    private final String TAX_INVOICE_AGREGATE = "T";
    private final String CATEGORY_INVOICE_AGREGATE = "R";
    private final String SUBCATEGORY_INVOICE_AGREGATE = "F";

    public Invoice duplicate(Invoice invoice) {
        return duplicate(invoice, null);
    }

    public Invoice duplicate(Invoice invoice, List<Long> invoiceLinesIds) {
        return duplicateByType(invoice, invoiceLinesIds, false);
    }
    
    public Invoice duplicateByType(Invoice invoice, List<Long> invoiceLinesIds, boolean isAdjustment) {
        invoice = refreshOrRetrieve(invoice);

        if (invoice.getOrders() != null) {
            invoice.getOrders().size();
        }

        var invoiceAgregates = new ArrayList<InvoiceAgregate>();
        if (invoice.getInvoiceAgregates() != null) {
            invoice.getInvoiceAgregates().size();
            invoiceAgregates.addAll(invoice.getInvoiceAgregates());
        }

        List<InvoiceLine> invoiceLines = new ArrayList<>();
        if (invoiceLinesIds != null && !invoiceLinesIds.isEmpty()) {
            invoiceLines.addAll(invoiceLinesService.findByInvoiceAndIds(invoice, invoiceLinesIds));
        }
        else if (invoice.getInvoiceLines() != null) {
            invoice.getInvoiceLines().size();
            invoiceLines.addAll(invoice.getInvoiceLines());
        }

        detach(invoice);

        var duplicateInvoice = new Invoice(invoice);
        this.create(duplicateInvoice);

        if (invoiceLinesIds == null || invoiceLinesIds.isEmpty()) {
            for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {
    
                invoiceAgregateService.detach(invoiceAgregate);
    
                switch (invoiceAgregate.getDescriminatorValue()) {
                    case TAX_INVOICE_AGREGATE: {
                        var taxInvoiceAgregate = new TaxInvoiceAgregate((TaxInvoiceAgregate) invoiceAgregate);
                        taxInvoiceAgregate.setInvoice(duplicateInvoice);
                        invoiceAgregateService.create(taxInvoiceAgregate);
                        duplicateInvoice.getInvoiceAgregates().add(taxInvoiceAgregate);
                        break;
                    }
                    case CATEGORY_INVOICE_AGREGATE: {
                        var categoryInvoiceAgregate = new CategoryInvoiceAgregate((CategoryInvoiceAgregate) invoiceAgregate);
                        categoryInvoiceAgregate.setInvoice(duplicateInvoice);
                        invoiceAgregateService.create(categoryInvoiceAgregate);
                        duplicateInvoice.getInvoiceAgregates().add(categoryInvoiceAgregate);
                        break;
                    }
                    case SUBCATEGORY_INVOICE_AGREGATE: {
                        var subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate((SubCategoryInvoiceAgregate) invoiceAgregate);
                        subCategoryInvoiceAgregate.setInvoice(duplicateInvoice);
                        invoiceAgregateService.create(subCategoryInvoiceAgregate);
                        duplicateInvoice.getInvoiceAgregates().add(subCategoryInvoiceAgregate);
                        break;
                    }
                }
            }
        }

        for (InvoiceLine invoiceLine : invoiceLines) {
            invoiceLinesService.detach(invoiceLine);
            InvoiceLine duplicateInvoiceLine = new InvoiceLine(invoiceLine, duplicateInvoice);
            duplicateInvoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.NOT_ADJUSTED);
            invoiceLinesService.createInvoiceLineWithInvoice(duplicateInvoiceLine, invoice, true);
            duplicateInvoice.getInvoiceLines().add(duplicateInvoiceLine);
        }

        if (isAdjustment) {
            invoiceLinesService.validateAdjAmount(invoiceLines, invoice);
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
                entity = commercialOrderService.findByCodeOrExternalId(targetCode);
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

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Invoice createAdjustment(Invoice invoice, InvoiceLinesToReplicate invoiceLinesToReplicate) {

        List<InvoiceLineRTs> invoiceLineRTs = invoiceLinesToReplicate.getInvoiceLinesRTs();
        InvoiceType invoiceType = null;

        if (StringUtils.isNotBlank(invoiceLinesToReplicate.getAdjType())) {
            if (!invoiceTypeService.getListAdjustementCode().contains(invoiceLinesToReplicate.getAdjType())) {
                throw new BusinessException("The type with code '" +invoiceLinesToReplicate.getAdjType() +"' is not a valid InvoiceType Adjustment");
            }
            invoiceType = invoiceTypeService.findByCode(invoiceLinesToReplicate.getAdjType());
        }

        if (invoiceLineRTs != null && !invoiceLineRTs.isEmpty()) {
            return createAdjustmentFromRatedTransactions(invoice, invoiceLineRTs, invoiceType);
        } else {
            return createAdjustment(invoice, invoiceLinesToReplicate.getInvoiceLinesIds(), invoiceType);
        }
    }

    private Invoice createAdjustmentFromRatedTransactions(Invoice invoice, List<InvoiceLineRTs> invoiceLineRTs, InvoiceType type) {

        List<Long> invoiceLinesIds = invoiceLineRTs.stream().map(InvoiceLineRTs::getInvoiceLineId).collect(toList());

        Invoice adjustmentInvoice = duplicateAndUpdateInvoiceLines(invoice, invoiceLineRTs, invoiceLinesIds);
        populateAdjustmentInvoice(adjustmentInvoice, type, invoice);
        calculateOrUpdateInvoice(invoiceLinesIds, adjustmentInvoice);
        addLinkedInvoice(invoice, adjustmentInvoice);

        return adjustmentInvoice;
    }

    private Invoice duplicateAndUpdateInvoiceLines(Invoice invoice, List<InvoiceLineRTs> invoiceLineRTs, List<Long> invoiceLinesIds) {

        invoice = refreshOrRetrieve(invoice);

        var invoiceAgregates = new ArrayList<InvoiceAgregate>();
        if (invoice.getInvoiceAgregates() != null) {
            invoiceAgregates.addAll(invoice.getInvoiceAgregates());
        }

        List<InvoiceLine> invoiceLines = new ArrayList<>();
        if (invoiceLinesIds != null && !invoiceLinesIds.isEmpty()) {
            invoiceLines.addAll(invoiceLinesService.findByInvoiceAndIds(invoice, invoiceLinesIds));
        }
        else if (invoice.getInvoiceLines() != null) {
            invoiceLines.addAll(invoice.getInvoiceLines());
        }

        detach(invoice);

        var duplicateInvoice = new Invoice(invoice);
        this.create(duplicateInvoice);

        updateInvoiceLinesAmountFromRatedTransactions(invoiceLineRTs, invoiceLines);

        for (InvoiceLine invoiceLine : invoiceLines) {
            invoiceLinesService.detach(invoiceLine);
            InvoiceLine duplicateInvoiceLine = new InvoiceLine(invoiceLine, duplicateInvoice);
            duplicateInvoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.NOT_ADJUSTED);
            duplicateInvoiceLine.setRatedTransactions(invoiceLine.getRatedTransactions());
            invoiceLinesService.createInvoiceLineWithInvoice(duplicateInvoiceLine, invoice, true);
            duplicateInvoice.getInvoiceLines().add(duplicateInvoiceLine);
        }

        invoiceLinesService.validateAdjAmount(invoiceLines, invoice);

        return duplicateInvoice;
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Invoice createAdjustment(Invoice invoice, List<Long> invoiceLinesIds, InvoiceType type) {
        Invoice adjustmentInvoice = duplicateByType(invoice, invoiceLinesIds, true);
        addLinkedInvoice(invoice, adjustmentInvoice);
        populateAdjustmentInvoice(adjustmentInvoice, type, invoice);
        calculateOrUpdateInvoice(invoiceLinesIds, adjustmentInvoice);

        return adjustmentInvoice;
    }


    private void updateInvoiceLinesAmountFromRatedTransactions(List<InvoiceLineRTs> invoiceLinesRTs, List<InvoiceLine> invoiceLines) {

        for (InvoiceLineRTs invoiceLineRTs : invoiceLinesRTs) {
            InvoiceLine invoiceLine = invoiceLines.stream().filter(line -> line.getId().equals(invoiceLineRTs.getInvoiceLineId())).findFirst().orElse(null);

            List<Long> ratedTransactionsIds = invoiceLineRTs.getRatedTransactionsId();
            if (invoiceLine != null && ratedTransactionsIds != null && !ratedTransactionsIds.isEmpty()) {

                List<RatedTransaction> ratedTransactions = ratedTransactionService.findByIds(invoiceLineRTs.getRatedTransactionsId());
                BigDecimal amountWithTax = ratedTransactions.stream().map(RatedTransaction::getAmountWithTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal amountWithoutTax = ratedTransactions.stream().map(RatedTransaction::getAmountWithoutTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal amountTax = ratedTransactions.stream().map(RatedTransaction::getAmountTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal quantity = ratedTransactions.stream().map(RatedTransaction::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);

                invoiceLine.setAmountWithTax(amountWithTax);
                invoiceLine.setAmountWithoutTax(amountWithoutTax);
                invoiceLine.setAmountTax(amountTax);
                invoiceLine.setQuantity(quantity);
            }
        }
    }

    private void calculateOrUpdateInvoice(List<Long> invoiceLinesIds, Invoice adjustmentInvoice) {
        if (invoiceLinesIds != null && !invoiceLinesIds.isEmpty()) {
            calculateInvoice(adjustmentInvoice);
        }
        else {
            update(adjustmentInvoice);
        }
    }

    private void populateAdjustmentInvoice(Invoice duplicatedInvoice, InvoiceType type, Invoice srcInvoice) {
        duplicatedInvoice.setInvoiceDate(new Date());
        duplicatedInvoice.setInvoiceType(type != null ? type : invoiceTypeService.getDefaultAdjustement());
        duplicatedInvoice.setStatus(InvoiceStatusEnum.DRAFT);
        duplicatedInvoice.setOpenOrderNumber(StringUtils.EMPTY);
        // Update ADJ Invoice PaymentMethod from original Invoice
        duplicatedInvoice.setPaymentMethod(srcInvoice.getPaymentMethod());
        getEntityManager().flush();
    }

    private void addLinkedInvoice(Invoice invoice, Invoice duplicatedInvoice) {
        LinkedInvoice linkedInvoice = new LinkedInvoice(invoice, duplicatedInvoice, duplicatedInvoice.getAmountWithTax(), duplicatedInvoice.getTransactionalAmountWithTax(), InvoiceTypeEnum.ADJUSTMENT);
        duplicatedInvoice.getLinkedInvoices().addAll(of(linkedInvoice));
    }

    public Invoice duplicateInvoiceLines(Invoice invoice, List<Long> invoiceLineIds) {
        invoice = refreshOrRetrieve(invoice);
        for (Long idInvoiceLine : invoiceLineIds) {
            InvoiceLine invoiceLineSource = invoiceLinesService.findById(idInvoiceLine);  
            Invoice invoiceSource = invoiceLineSource.getInvoice();
            invoiceLinesService.detach(invoiceLineSource);
            var duplicateInvoiceLine = new InvoiceLine(invoiceLineSource, invoice);
            duplicateInvoiceLine.setStatus(InvoiceLineStatusEnum.BILLED);
            invoiceLinesService.createInvoiceLineWithInvoice(duplicateInvoiceLine, invoiceSource);
            invoice.getInvoiceLines().add(duplicateInvoiceLine);
        }

        calculateInvoice(invoice);
        return update(invoice);
    }

    public Invoice findByInvoiceNumber(String invoiceNumber) {
        try {
            return (Invoice) getEntityManager().createQuery("SELECT inv FROM Invoice inv WHERE inv.invoiceNumber = :invoiceNumber")
                    .setParameter("invoiceNumber", invoiceNumber).setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    /**
     * @return billingRunId the id of the new billing run.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long quarantineBillingRun(Invoice invoice, QuarantineBillingRunDto quarantineBillingRunDto) {
        List<Long> invoiceIds = new ArrayList<>();
        List<Invoice> invoices = new ArrayList<>();
        
        invoiceIds.add(invoice.getId());
        BillingRun billingRun = invoice.getBillingRun();
        invoices.add(invoiceService.refreshOrRetrieve(invoice));
       
        if (billingRun != null) {
            BillingRun nextBR = billingRunService.findOrCreateNextQuarantineBR(billingRun.getId(), quarantineBillingRunDto.getDescriptionsTranslated());
            getEntityManager().createNamedQuery("Invoice.moveToBRByIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
            getEntityManager().createNamedQuery("InvoiceLine.moveToQuarantineBRByInvoiceIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
            getEntityManager().createNamedQuery("RatedTransaction.moveToQuarantineBRByInvoiceIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
            getEntityManager().createNamedQuery("SubCategoryInvoiceAgregate.moveToQuarantineBRByInvoiceIds").setParameter("billingRun", nextBR).setParameter("invoiceIds", invoiceIds).executeUpdate();
            
            billingRun = billingRunService.refreshOrRetrieve(billingRun);
            
            billingRunService.updateBillingRunStatistics(nextBR);
            billingRunService.updateBillingRunStatistics(billingRun);

            return nextBR.getId();
        }else {
            throw new BusinessException("Invoice with invoice id " + invoice.getId() + " doesn't have a billing run.");
        }

    }
    
    private void addApplicableDiscount(List<DiscountPlanItem> applicableDiscountPlanItems,List<DiscountPlanInstance> discountPlanInstances, BillingAccount billingAccount, CustomerAccount customerAccount, Invoice invoice) {
        if(invoice.getInvoiceLines() != null && !invoice.getInvoiceLines().isEmpty()) {
            var filtredDiscountPlanInstanes= discountPlanInstances.stream().filter(dpi -> DiscountPlanTypeEnum.INVOICE == dpi.getDiscountPlan().getDiscountPlanType()).collect(Collectors.toList());
            // use getApplicableDiscountPlanItemsV11 instead of getApplicableDiscountPlanItems after merging DP US INTRD-5730
            applicableDiscountPlanItems.addAll(getApplicableDiscountPlanItemsV11(billingAccount, filtredDiscountPlanInstanes, invoice, customerAccount));
        }else{
            applicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, discountPlanInstances, invoice, customerAccount));
        }
    }

    public void triggersCollectionPlanLevelsJob() {
        JobInstance triggerCollectionPlanLevelsJob = jobInstanceService.findByCode("TriggerCollectionPlanLevelsJob");
        FinanceSettings lastOne = financeSettingsService.getFinanceSetting();
        if (triggerCollectionPlanLevelsJob != null && lastOne != null && lastOne.isActivateDunning()) {
            jobExecutionService.executeJob(triggerCollectionPlanLevelsJob, Collections.EMPTY_MAP, JobLauncherEnum.TRIGGER);
        }
    }

    public List<Long> getInvoicesByBR(Long billingRunId) {
        return getEntityManager().createNamedQuery("Invoice.loadByBillingRun", Long.class)
                    .setParameter("billingRunId", billingRunId)
                    .getResultList();
    }

    public List<Object[]> findLinkedInvoicesByIdAndType(Long invoiceId, String invoiceTypeCode) {

        return getEntityManager().createNamedQuery("Invoice.findLinkedInvoicesByIdAndType").
                setParameter("invoiceId", invoiceId).
                setParameter("invoiceTypeCode", invoiceTypeCode).getResultList();

    }


	public List<Long> getInvoicesByBRNotValidatedInvoices(Long billingRunId) {
        return getEntityManager().createNamedQuery("Invoice.loadByBillingRunNotValidatedInvoices", Long.class)
                    .setParameter("billingRunId", billingRunId)
                    .getResultList();
    }
    
    public void checkAndUpdatePaymentStatus(Invoice entity,InvoicePaymentStatusEnum oldInvoicePaymentStatusEnum, InvoicePaymentStatusEnum newInvoicePaymentStatusEnum) {
        if (!oldInvoicePaymentStatusEnum.equals(newInvoicePaymentStatusEnum)) {
            invoicePaymentStatusUpdated.fire(entity);
        }
        entity.setPaymentStatus(newInvoicePaymentStatusEnum);
    }
        
    /**
     * Update validated invoice
     * @param toUpdate Invoice to update {@link Invoice}
     * @param comment Comment
     * @param customFieldValues Custom Field {@link CustomFieldValues}
     * @return Updated Invoice {@link Invoice}
     */
    public Invoice updateValidatedInvoice(Invoice toUpdate, String comment, CustomFieldValues customFieldValues) {
        toUpdate = refreshOrRetrieve(toUpdate);

        if (isNotBlank(comment)) {
            toUpdate.setComment(comment);
        }
        
        if(customFieldValues != null) {
            toUpdate.setCfValues(customFieldValues);
        }

        return update(toUpdate);
    }

    public String getFilePathByInvoiceIdType(Long invoiceId, String type) {
        String fileName = "";
        Invoice invoice = findById(invoiceId);
        if (invoice != null) {
            if (type == "xml") {
                fileName = getFullXmlFilePath(invoice, false);
            }
            else if (type == "pdf") {
                fileName = getFullPdfFilePath(invoice, false);
            }
        }
        return fileName;
    }

    /**
     * Update Security Deposit Template
     * @param securityDepositTemplate {@link SecurityDepositTemplate}
     */
    public SecurityDepositTemplate updateSDTemplate(SecurityDepositTemplate securityDepositTemplate) {
        return serviceSingleton.incrementSDTemplateInstanciationNumber(securityDepositTemplate);
    }


    /**
     * Clear cached Jasper reports
     */
    public static void clearJasperReportCache() {
        jasperReportMap.clear();
    }
    
    @SuppressWarnings("unchecked")
    private List<Invoice> checkAdvanceInvoice(Invoice invoice) {
        if(invoice.getInvoiceType() != null) {
            String invoiceTypeCode = invoice.getInvoiceType().getCode();
            OperationCategoryEnum occCategoryOperation = invoice.getInvoiceType().getOccTemplate() != null ? invoice.getInvoiceType().getOccTemplate().getOccCategory() : null;
            if(invoiceTypeCode.equals("ADJ") || invoiceTypeCode.equals("ADV") || invoiceTypeCode.equals("SECURITY_DEPOSIT") || (occCategoryOperation != null && occCategoryOperation.equals(OperationCategoryEnum.CREDIT))) {
                return emptyList();
            }
        }else {
            return emptyList();
        }
        // check balance when invoicing plan created from order ==> adv from order must have balance set
        List<Invoice> invoicesAdv  = this.getEntityManager().createNamedQuery("Invoice.findValidatedInvoiceAdvOrder")
                .setParameter("billingAccountId", invoice.getBillingAccount().getId())
                .setParameter("commercialOrder", invoice.getCommercialOrder())
                .setParameter("tradingCurrencyId",invoice.getTradingCurrency() != null ? invoice.getTradingCurrency().getId() : null)
		        .setParameter("subscriptionId", invoice.getSubscription() != null ? invoice.getSubscription().getId() : null)
                .getResultList();
        return invoicesAdv;
    }
    
   @SuppressWarnings("unchecked")
   public Map<Invoice, List<Invoice>> applyligibleInvoiceForAdvancement(Long billingRunId) {
       List<Object[]>  result = this.getEntityManager().createNamedQuery("Invoice.findInvoiceEligibleAdv").setParameter("billingRunId", billingRunId).getResultList();
       
       Map<Invoice, List<Invoice>> invoicesWithAdv = new HashMap<Invoice, List<Invoice>>();
        result.stream().forEach(invoices -> {
            Invoice key = (Invoice)invoices[0];
            Invoice adv = (Invoice)invoices[1];
            if((key.getCommercialOrder() == null && adv.getCommercialOrder() != null) || (! "ADV".equals(adv.getInvoiceType().getCode()))) {
            	return;
            }
            if(invoicesWithAdv.get(key) == null) {
                List<Invoice> advs = new ArrayList<>();
                advs.add(adv);
                invoicesWithAdv.put(key, advs);
            }else {
                invoicesWithAdv.get(key).add(adv);
            }
        });
       invoicesWithAdv.keySet().forEach(inv -> applyAdvanceInvoice(inv, invoicesWithAdv.get(inv)));
       return null;
   }

    public void applyAdvanceInvoice(Invoice invoice, List<Invoice> advInvoices) {
        BigDecimal invoiceBalance = invoice.getTransactionalInvoiceBalance();
        if (invoiceBalance != null && CollectionUtils.isNotEmpty(invoice.getLinkedInvoices())) {
            CommercialOrder orderInvoice = invoice.getCommercialOrder();
			Subscription subscriptionInvoice = invoice.getSubscription();
            BigDecimal sum = invoice.getLinkedInvoices().stream()
                    .filter(i -> InvoiceTypeEnum.ADVANCEMENT_PAYMENT.equals(i.getType()))
                    .filter(li -> (subscriptionInvoice != null && subscriptionInvoice.equals(li.getLinkedInvoiceValue().getSubscription())) || (subscriptionInvoice == null && li.getLinkedInvoiceValue().getSubscription() == null) || (orderInvoice != null && orderInvoice.equals(li.getLinkedInvoiceValue().getCommercialOrder())) || (orderInvoice == null && li.getLinkedInvoiceValue().getCommercialOrder() == null))
                    .map(LinkedInvoice::getTransactionalAmount)
                    .reduce(BigDecimal::add).orElse(ZERO);
            //if balance is well calculated and balance=0, we don't need to recalculate
            if ((sum.add(invoiceBalance)).compareTo(invoice.getTransactionalAmountWithoutTax()) == 0) {
                CommercialOrder commercialOrder = CollectionUtils.isNotEmpty(advInvoices) ? advInvoices.get(0).getCommercialOrder() : null;
                if (BigDecimal.ZERO.compareTo(invoiceBalance) == 0 && !(commercialOrder != null && commercialOrder.equals(invoice.getCommercialOrder()))) {
                    return;
                }
            }
            cancelInvoiceAdvances(invoice, advInvoices, false);
        }
        if (CollectionUtils.isNotEmpty(advInvoices)) {
            sort(advInvoices, (inv1, inv2) -> {
                int compCommercialOrder = 0;
                if (inv1.getCommercialOrder() != null && inv2.getCommercialOrder() == null) {
                    compCommercialOrder = -1;
                } else if (inv1.getCommercialOrder() == null && inv2.getCommercialOrder() != null) {
                    compCommercialOrder = 1;
                } else if (inv1.getCommercialOrder() == null && inv2.getCommercialOrder() == null) {
                    compCommercialOrder = 0;
                } else {
                    compCommercialOrder = inv1.getCommercialOrder().getId().compareTo(inv2.getCommercialOrder().getId());
                }
                if (compCommercialOrder != 0) {
                    return compCommercialOrder;
                }
				if(inv1.getSubscription() != null && inv2.getSubscription() == null){
					compCommercialOrder = -1;
				}else if(inv1.getSubscription() == null && inv2.getSubscription() != null) {
					compCommercialOrder = 1;
				}else if(inv1.getSubscription() == null && inv2.getSubscription() == null) {
					compCommercialOrder = 0;
				}else {
					compCommercialOrder = inv1.getSubscription().getId().compareTo(inv2.getSubscription().getId());
				}
				if(compCommercialOrder != 0) {
					return compCommercialOrder;
				}
                int compCreationDate = inv1.getAuditable().getCreated().compareTo(inv2.getAuditable().getCreated());
                if (compCreationDate != 0) {
                    return compCreationDate;
                }
                return inv1.getInvoiceBalance().compareTo(inv2.getInvoiceBalance());

            });
            BigDecimal lastApliedRate = invoiceService.getCurrentRate(invoice,invoice.getInvoiceDate());

            BigDecimal remainingAmount = lastApliedRate != null ? invoice.getAmountWithTax().multiply(lastApliedRate) : invoice.getAmountWithTax();

            for (Invoice adv : advInvoices) {
                if (adv.getTransactionalInvoiceBalance() == null || adv.getTransactionalInvoiceBalance().toBigInteger().equals(BigInteger.ZERO)) {
                    continue;
                }
                final BigDecimal amount;
                BigDecimal transactionalCurrencyBalance;
                BigDecimal functionalCurrencyBalance;
                Optional<LinkedInvoice> toUpdate = invoice.getLinkedInvoices().stream()
                        .filter(li -> li.getLinkedInvoiceValue().getId() == adv.getId()).findAny();
                if (toUpdate.isPresent() && toUpdate.get().getLinkedInvoiceValue().getCommercialOrder() != null && invoice.getCommercialOrder() == null)
                    continue;

                if (adv.getTransactionalInvoiceBalance().compareTo(remainingAmount) >= 0) {
                    amount = remainingAmount;
                    transactionalCurrencyBalance = adv.getTransactionalInvoiceBalance().subtract(remainingAmount);
                    functionalCurrencyBalance = adv.getInvoiceBalance().subtract(remainingAmount);
                    //functionalCurrencyBalance = getFunctionalCurrencyBalance(adv, transactionalCurrencyBalance);

                    adv.setInvoiceBalance(functionalCurrencyBalance);
                    adv.setTransactionalInvoiceBalance(transactionalCurrencyBalance);

                    remainingAmount = ZERO;
                } else {
                    amount = adv.getTransactionalInvoiceBalance();
                    remainingAmount = remainingAmount.subtract(adv.getTransactionalInvoiceBalance());
                    adv.setInvoiceBalance(BigDecimal.ZERO);
                    adv.setTransactionalInvoiceBalance(ZERO);
                }
                if (amount.intValue() == ZERO.intValue()) continue;
                if (toUpdate.isPresent()) {
                    toUpdate.get().setTransactionalAmount(toUpdate.get().getTransactionalAmount().add(amount));
                } else {
                    createNewLinkedInvoice(invoice, amount, adv);
                }
                if (remainingAmount == ZERO) {
                    break;
                }
            }
            invoice.setInvoiceBalance(remainingAmount);
            invoice.setTransactionalInvoiceBalance(remainingAmount);
            invoice.getLinkedInvoices().removeIf(il -> ZERO.compareTo(il.getTransactionalAmount()) == 0 && InvoiceTypeEnum.ADVANCEMENT_PAYMENT.equals(il.getType()));
        }
    }

    private BigDecimal getFunctionalCurrencyBalance(Invoice adv, BigDecimal transactionalCurrencyBalance) {
        BigDecimal functionalCurrencyBalance;
        functionalCurrencyBalance = (adv.getTransactionalInvoiceBalance() != null && adv.getTransactionalInvoiceBalance().compareTo(ZERO) > 0) ?
                (transactionalCurrencyBalance.divide(adv.getTransactionalInvoiceBalance(),2,RoundingMode.HALF_UP)).multiply(adv.getInvoiceBalance()) :
                transactionalCurrencyBalance;
        return functionalCurrencyBalance.setScale(appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode().getRoundingMode());
    }

	private void createNewLinkedInvoice(Invoice invoice, BigDecimal amount, Invoice adv) {
		LinkedInvoice advanceMapping = new LinkedInvoice(invoice, adv, amount, InvoiceTypeEnum.ADVANCEMENT_PAYMENT);
		invoice.getLinkedInvoices().add(advanceMapping);
	}
    
	private void cancelInvoiceAdvances(Invoice invoice, List<Invoice> advInvoices, boolean delete) {
		invoice.setInvoiceBalance(null);
        invoice.setTransactionalInvoiceBalance(null);
		if (invoice.getLinkedInvoices() != null) {
			Predicate<LinkedInvoice> advFilter = i -> InvoiceTypeEnum.ADVANCEMENT_PAYMENT.equals(i.getType());
			if (delete) {
				invoice.getLinkedInvoices().stream().filter(advFilter).forEach(li -> li.getLinkedInvoiceValue().setInvoiceBalance(li.getLinkedInvoiceValue().getInvoiceBalance().add(li.getAmount())));
				linkedInvoiceService.deleteByInvoiceIdAndType(invoice.getId(), InvoiceTypeEnum.ADVANCEMENT_PAYMENT);
				invoice.getLinkedInvoices().removeIf(advFilter);
			} else {
				for (Invoice advInvoice : advInvoices) {
					invoice.getLinkedInvoices().stream().filter(advFilter).filter(linkedInvoice -> linkedInvoice.getLinkedInvoiceValue().getId() == advInvoice.getId()).findAny().ifPresent(li -> {
                        advInvoice.setTransactionalInvoiceBalance(advInvoice.getTransactionalInvoiceBalance().add(li.getTransactionalAmount()));
                        advInvoice.setInvoiceBalance(advInvoice.getInvoiceBalance().add(li.getTransactionalAmount()));
                        li.setTransactionalAmount(ZERO);
					});
				}
				List<LinkedInvoice> lis = invoice.getLinkedInvoices().stream().filter(advFilter).filter(li -> ZERO.compareTo(li.getTransactionalAmount()) < 0).collect(Collectors.toList());
					for(LinkedInvoice li : lis) {
						Invoice oldAdvanceInvoice = li.getLinkedInvoiceValue();
                        oldAdvanceInvoice.setTransactionalInvoiceBalance(oldAdvanceInvoice.getTransactionalInvoiceBalance().add(li.getTransactionalAmount()));
						advInvoices.add(oldAdvanceInvoice);
                        li.setTransactionalAmount(ZERO);
					};
			}
		}
	}

	public void rollBackAdvances(BillingRun billingRun) {
	    getEntityManager().createNamedQuery("Invoice.rollbackAdvance") .setParameter("billingRunId", billingRun.getId()).executeUpdate();
	}
	
    public long countInvoicesByValidationRule(Long ruleId) {
        return getEntityManager().createNamedQuery("Invoice.countByValidationRule", Long.class)
                    .setParameter("ruleId", ruleId)
                    .getSingleResult();
    }

    public LinkedInvoice findBySourceInvoiceByAdjId(Long invoiceAdjId) {
        List<LinkedInvoice> results = getEntityManager().createNamedQuery("LinkedInvoice.findBySourceInvoiceByAdjId")
                .setParameter("ID_INVOICE_ADJ", invoiceAdjId)
                .getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            return results.get(0);
        }

        return null;
    }

    /**
     * At the time of the validation of the ADJ via the API validate if the autoMatching is true so we automatically match its AO with that of the original invoice
     * (knowing that the API validate has the param generateAO=true from the portal)
     *
     * @param invoice invoice to check: should be ADJ type with autoMatching boolean as 'true'
     */
    public void autoMatchingAdjInvoice(Invoice invoice, AccountOperation aoAdjInvoice) {
        if (invoiceTypeService.getListAdjustementCode().contains(invoice.getInvoiceType().getCode()) && invoice.isAutoMatching()) {
            // Check if the invoice is not PAID
            if (invoice.getPaymentStatus() != InvoicePaymentStatusEnum.PAID) {
                LinkedInvoice linkedInvoice = findBySourceInvoiceByAdjId(invoice.getId());

                if (linkedInvoice != null) {
                    Invoice originalInvoice = linkedInvoice.getInvoice();

                    AccountOperation aoOriginalInvoice = accountOperationService.listByInvoice(originalInvoice).get(0);
                    if (aoAdjInvoice == null) { // in case of the call is from validate API without generatedAO yet
                        aoAdjInvoice = accountOperationService.listByInvoice(invoice).get(0);
                    }
                    if (aoAdjInvoice.getMatchingStatus() != MatchingStatusEnum.L) {
                        try {
                            matchingCodeService.matchOperations(aoAdjInvoice.getCustomerAccount().getId(), aoAdjInvoice.getCustomerAccount().getCode(),
                                    List.of(aoAdjInvoice.getId(), aoOriginalInvoice.getId()), aoOriginalInvoice.getId(),
                                    MatchingTypeEnum.A, aoOriginalInvoice.getUnMatchingAmount());
                        } catch (Exception e) {
                            log.error("Error on payment callback processing:", e);
                            throw new BusinessException(e.getMessage(), e);
                        }

                        // Check if the all invoice AO are matched with ADJ AO : if yes, the payment status of Invoice shall be set as ABONDONNED
                        if (originalInvoice.getRecordedInvoice() != null &&
                                MatchingStatusEnum.L == originalInvoice.getRecordedInvoice().getMatchingStatus()) {
                            Set<String> matchedAoCodes = originalInvoice.getRecordedInvoice().getMatchingAmounts().stream()
                                    .map(MatchingAmount::getAccountOperation)
                                    .map(AccountOperation::getCode)
                                    .collect(Collectors.toSet());

                            // remove ADJ code + default INV_STD code
                            matchedAoCodes.removeAll(Set.of("ADJ_REF", "ADJ_INV", "INV_CRN", "INV_STD"));

                            // if we have other AO except ADJ or default INV one, that mean that Invoice is matched with another kind of AO, and shall not be ABANDONED
                            // the rule : the ABANDONED is used when Invoice is "TOTALY" Adjusted
                            if (matchedAoCodes.size() == 0) {
                                originalInvoice.setPaymentStatus(InvoicePaymentStatusEnum.ABANDONED);
                                update(originalInvoice);
                            }
                        }
                    }
                }
            }
        }
    }
    
	@SuppressWarnings("unchecked")
	public List<Invoice> findByFilter(Map<String, Object> filters) {
		try {
			PaginationConfiguration configuration = getPaginationConfigurationFromFilter(filters);
			QueryBuilder query = getQuery(configuration);
			return query.getQuery(getEntityManager()).getResultList();
		} catch (Exception e) {
			return emptyList();
		}
	}

	private PaginationConfiguration getPaginationConfigurationFromFilter(Map<String, Object> filters) {
		GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
		filters = genericRequestMapper.evaluateFilters(filters, entityClass);
		return new PaginationConfiguration(filters);
	}
	/**
	 * get list of invoices that have ubl reference is false
	 *
	 * @param billingRunId
	 * @param statusList
	 * @return
	 */
	public List<Long> listInvoicesWithoutXml(List<InvoiceStatusEnum> statusList) {
		return getEntityManager().createNamedQuery("Invoice.xmlWithStatusForUBL", Long.class).setParameter("statusList", statusList).getResultList();
	}
	
	/**
	 * Produce invoice's XML file and update invoice record in DB.
	 *
	 * @param invoice Invoice to produce XML for
	 * @param draftWalletOperationsId Wallet operations (ids) to include in a draft invoice
	 * @return Update invoice entity
	 * @throws BusinessException business exception
	 */
	public Invoice produceInvoiceUBLFormat(Invoice invoice) throws BusinessException, JAXBException {
		invoice.setXmlDate(new Date());
		invoice.setUblReference(true);
		InvoiceUblHelper invoiceUblHelper = InvoiceUblHelper.getInstance();
		var invoiceUbl = invoiceUblHelper.createInvoiceUBL(invoice);
		// check directory if exist
		ParamBean paramBean = ParamBean.getInstance();
		File ublDirectory = new File (paramBean.getChrootDir("") + File.separator + paramBean.getProperty("meveo.ubl.directory", "/ubl"));
		if (!StorageFactory.existsDirectory(ublDirectory)) {
			StorageFactory.createDirectory(ublDirectory);
		}
		File xmlInvoiceFileName = new File(ublDirectory.getAbsolutePath() + File.separator + "invoice_" + invoice.getInvoiceNumber() + "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xml");
		try {
			Files.createFile(Paths.get(xmlInvoiceFileName.getAbsolutePath()));
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		try {
			invoiceUblHelper.toXml(invoiceUbl, xmlInvoiceFileName);
		} catch (javax.xml.bind.JAXBException e) {
			throw new BusinessException(e);
		}
		invoice = updateNoCheck(invoice);
		entityUpdatedEventProducer.fire(invoice);
		return invoice;
	}
	
}
