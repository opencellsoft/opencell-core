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

import static org.meveo.commons.utils.NumberUtils.getRoundingMode;
import static org.meveo.commons.utils.NumberUtils.round;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.InvoiceXmlNotFoundException;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.admin.util.PdfWaterMark;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.commons.exceptions.ConfigurationException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.sf.jasperreports.engine.JRException;
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
 * @lastModifiedVersion 5.1 
 */
@Stateless
public class InvoiceService extends PersistenceService<Invoice> {

    /** The Constant INVOICE_ADJUSTMENT_SEQUENCE. */
    public final static String INVOICE_ADJUSTMENT_SEQUENCE = "INVOICE_ADJUSTMENT_SEQUENCE";

    /** The Constant INVOICE_SEQUENCE. */
    public final static String INVOICE_SEQUENCE = "INVOICE_SEQUENCE";

    /** The p DF parameters construction. */
    @EJB
    private PDFParametersConstruction pDFParametersConstruction;

    /** The xml invoice creator. */
    @EJB
    private XMLInvoiceCreator xmlInvoiceCreator;

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The invoice agregate service. */
    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    /** The billing account service. */
    @Inject
    private BillingAccountService billingAccountService;

    /** The rated transaction service. */
    @Inject
    private RatedTransactionService ratedTransactionService;
    
    @Inject
    private BillingCycleService billingCycleService;

    /** The rejected billing account service. */
    @Inject
    private RejectedBillingAccountService rejectedBillingAccountService;

    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;

    /** The resource messages. */
    @Inject
    private ResourceBundle resourceMessages;

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
    private FilterService filterService;
    
    @Inject
    private UserAccountService userAccountService;
    
    @Inject
    private InvoiceCategoryService invoiceCategoryService;
    
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;
    
    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;
    
    @Inject
    protected SellerService sellerService;

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
     * Assign invoice number to an invoice
     * 
     * @param invoice invoice
     * @throws BusinessException business exception
     */
    public void assignInvoiceNumber(Invoice invoice) throws BusinessException {
        String cfName = invoiceTypeService.getCustomFieldCode(invoice.getInvoiceType());
        Customer cust = invoice.getBillingAccount().getCustomerAccount().getCustomer();

        InvoiceType invoiceType = invoiceTypeService.findById(invoice.getInvoiceType().getId());
        
        Seller seller = invoice.getSeller();
        if(seller == null && cust.getSeller() != null) {
            seller = cust.getSeller().findSellerForInvoiceNumberingSequence(cfName, invoice.getInvoiceDate(), invoiceType);
        }

        InvoiceSequence sequence = serviceSingleton.incrementInvoiceNumberSequence(invoice.getInvoiceDate(), invoiceType, seller, cfName, 1);
        InvoiceTypeSellerSequence invoiceTypeSellerSequence = null;
        if(seller != null) {
            invoiceTypeSellerSequence = invoiceType.getSellerSequenceByType(seller);
        }

        int sequenceSize = sequence.getSequenceSize();
        String prefix = invoiceType.getPrefixEL();
        if(invoiceTypeSellerSequence != null) {
        	prefix = invoiceTypeSellerSequence.getPrefixEL();
        }
        if (prefix != null && !StringUtils.isBlank(prefix)) {
            prefix = evaluatePrefixElExpression(prefix, invoice);
        } else {
            prefix = "";
        }

        long nextInvoiceNb = sequence.getCurrentInvoiceNb();
        String invoiceNumber = StringUtils.getLongAsNChar(nextInvoiceNb, sequenceSize);
        // request to store invoiceNo in alias field
        invoice.setAlias(invoiceNumber);
        invoice.setInvoiceNumber(prefix + invoiceNumber);
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
        
        //TODO: 3508
        Seller seller = null;
        if(invoice.getBillingAccount() != null && invoice.getBillingAccount().getCustomerAccount() != null
        		&& invoice.getBillingAccount().getCustomerAccount().getCustomer() != null
        		&& invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller()  != null) {
        	seller = invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }
        
        InvoiceTypeSellerSequence invoiceTypeSellerSequence = invoiceType.getSellerSequenceByType(seller);
        if(invoiceTypeSellerSequence != null) {
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
    public List<Long> queryInvoiceIdsWithNoAccountOperation(BillingRun br, boolean excludeInvoicesWithoutAmount) {
        try {
            QueryBuilder qb = queryInvoiceIdsWithNoAccountOperation(br);
            if (excludeInvoicesWithoutAmount) {
                qb.addSql("i.amount != 0 ");
            }
            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to get invoices with amount and with no account operation", ex);
        }
        return null;
    }

    
    /**
     * Get rated transactions for entity grouped by billing account and seller, which allows invoice generation by seller and billing account
     * 
     * @param entity entity to be billed
     * @param billingRun billing run
     * @param ratedTransactionFilter rated transaction filter
     * @param invoiceDate date of invoice
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @return list of rated transaction groups for entity.
     */
    public List<RatedTransactionGroup> getRatedTransactionGroups(IBillableEntity entity, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate,
            Date firstTransactionDate, Date lastTransactionDate) throws BusinessException {
        
        List<RatedTransactionGroup> ratedTransactionGroups = new ArrayList<RatedTransactionGroup>();
        
        BillingCycle billingCycle = null;
        Map<BillingAccount, List<RatedTransaction>> mapBillingAccountRT = new HashMap<BillingAccount, List<RatedTransaction>>();
        
        // Get entity RTs grouped by billing account
        if (ratedTransactionFilter != null) {
            List<RatedTransaction> ratedTransactions = (List<RatedTransaction>) filterService.filteredListAsObjects(ratedTransactionFilter);
            if (ratedTransactions == null || ratedTransactions.isEmpty()) {
                mapBillingAccountRT.put((BillingAccount) entity, ratedTransactions);
            }
        } else {
            if (entity instanceof Subscription) {
                billingCycle = billingRun == null ? ((Subscription)entity).getBillingCycle() : billingRun.getBillingCycle();
                List<RatedTransaction> ratedTransactions = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceBySubscription", RatedTransaction.class)
                        .setParameter("subscription", entity)
                        .setParameter("firstTransactionDate", firstTransactionDate)
                        .setParameter("lastTransactionDate", lastTransactionDate).getResultList();
                
                UserAccount ua = userAccountService.refreshOrRetrieve(((Subscription)entity).getUserAccount());
                if (ratedTransactions != null && !ratedTransactions.isEmpty()) {
                    mapBillingAccountRT.put(ua.getBillingAccount(), ratedTransactions);
                }
            } else if (entity instanceof BillingAccount) {
                billingCycle = billingRun == null ? ((BillingAccount)entity).getBillingCycle() : billingRun.getBillingCycle();
                List<RatedTransaction> ratedTransactions = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccount", RatedTransaction.class)
                        .setParameter("billingAccount", entity)
                        .setParameter("firstTransactionDate", firstTransactionDate)
                        .setParameter("lastTransactionDate", lastTransactionDate).getResultList();
                if (ratedTransactions != null && !ratedTransactions.isEmpty()) {
                    mapBillingAccountRT.put((BillingAccount) entity, ratedTransactions);
                }
            } else if (entity instanceof Order) {
                billingCycle = billingRun == null ? ((Order)entity).getBillingCycle() : billingRun.getBillingCycle();
                List<RatedTransaction> ratedTransactions = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByOrderNumber", RatedTransaction.class)
                        .setParameter("orderNumber", ((Order) entity).getOrderNumber())
                        .setParameter("firstTransactionDate", firstTransactionDate)
                        .setParameter("lastTransactionDate", lastTransactionDate).getResultList();
                for(RatedTransaction rt : ratedTransactions) {
                    if(mapBillingAccountRT.get(rt.getBillingAccount()) == null) {
                        mapBillingAccountRT.put(rt.getBillingAccount(), new ArrayList<RatedTransaction>());
                    } 
                    mapBillingAccountRT.get(rt.getBillingAccount()).add(rt);
                }
            } 
        }
        
        // Split RTs billing account groups to billing account/seller groups
        log.info("Split RTs billing account groups to billing account/seller groups");
        for (Map.Entry<BillingAccount, List<RatedTransaction>> entryBaTr : mapBillingAccountRT.entrySet()) {
            BillingAccount billingAccount = entryBaTr.getKey();
            List<RatedTransaction> ratedTransactions = entryBaTr.getValue();
            
            Map<Seller, List<RatedTransaction>> mapSellerRT = new HashMap<Seller, List<RatedTransaction>>();
            for(RatedTransaction rt: ratedTransactions) {
                rt.setBillingRun(billingRun);
                Seller seller = null;
                if(rt.getSeller() != null) {
                   seller = rt.getSeller();
                }
                if(seller == null) {
                    seller = rt.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
                }
                // refresh seller
                seller = sellerService.findById(seller.getId());
                
                if(mapSellerRT.get(seller) == null) {
                    mapSellerRT.put(seller, new ArrayList<RatedTransaction>());
                }
                List<RatedTransaction> sellerRTs = mapSellerRT.get(seller);
                sellerRTs.add(rt);
                mapSellerRT.put(seller, sellerRTs);
            }
            
            for (Map.Entry<Seller, List<RatedTransaction>> entrySellerTr : mapSellerRT.entrySet()) {
                RatedTransactionGroup ratedTransactionGroup = new RatedTransactionGroup();
                ratedTransactionGroup.setBillingAccount(billingAccount);
                ratedTransactionGroup.setSeller(entrySellerTr.getKey());
                ratedTransactionGroup.setRatedTransactions(entrySellerTr.getValue());
                ratedTransactionGroup.setBillingCycle(billingCycle);
                ratedTransactionGroups.add(ratedTransactionGroup);
            }
        }
        log.info("end Split RTs");
        
        return ratedTransactionGroups;
       
    }
    
    /**
     * Creates the agregates and invoice.
     *
     * @param entity entity to be billed
     * @param billingRun billing run
     * @param ratedTransactionFilter rated transaction filter
     * @param invoiceDate date of invoice
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @param minAmountTransactions Min amount rated transactions
     * @return created invoice
     * @throws BusinessException business exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Invoice> createAgregatesAndInvoice(IBillableEntity entity, BillingRun billingRun, Filter ratedTransactionFilter, Date invoiceDate,
            Date firstTransactionDate, Date lastTransactionDate, List<RatedTransaction> minAmountTransactions, boolean isDraft) throws BusinessException {
        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }

        if (billingRun == null) {
            if (invoiceDate == null) {
                throw new BusinessException("invoiceDate must be set if billingRun is null");
            }
            if (StringUtils.isBlank(lastTransactionDate) && ratedTransactionFilter == null) {
                throw new BusinessException("lastTransactionDate or ratedTransactionFilter must be set if billingRun is null");
            }
        } else {
            lastTransactionDate = billingRun.getLastTransactionDate();
            invoiceDate = billingRun.getInvoiceDate();
        }
        
        if(minAmountTransactions != null) {
            for (RatedTransaction minRatedTransaction : minAmountTransactions) {
                BillingAccount ba = billingAccountService.findById(minRatedTransaction.getBillingAccount().getId());
                minRatedTransaction.setBillingAccount(ba);
                ratedTransactionService.create(minRatedTransaction);
            }
        }

        List<Invoice> invoiceList = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            List<RatedTransactionGroup> ratedTransactionGroups = getRatedTransactionGroups(entity, billingRun, ratedTransactionFilter, invoiceDate, firstTransactionDate, lastTransactionDate);
            if (ratedTransactionGroups.isEmpty()) {
                throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));
            }

            for (RatedTransactionGroup ratedTransactionGroup : ratedTransactionGroups) {
                
                BillingAccount billingAccount = billingAccountService.findById(ratedTransactionGroup.getBillingAccount().getId(), true);
                List<RatedTransaction> ratedTransactions = ratedTransactionGroup.getRatedTransactions();
                
                Map<InvoiceType, List<RatedTransaction>> mapInvTypeRT = new HashMap<InvoiceType, List<RatedTransaction>>();
                
                BillingCycle billingCycle = ratedTransactionGroup.getBillingCycle();
                if (billingCycle == null) {
                    billingCycle = billingAccount.getBillingCycle();
                }
                if (billingCycle == null) {
                    throw new BusinessException("Cant find the billing cycle");
                }
                
                billingCycle = billingCycleService.refreshOrRetrieve(billingCycle);
                ScriptInstance scriptInstance = billingCycle.getScriptInstance();
                if (scriptInstance != null) {
                    InvoiceType invoiceType = invoiceTypeService.refreshOrRetrieve(billingCycle.getInvoiceType());
                    log.debug("start to execute script instance for billingCycle {}", billingCycle);
                    mapInvTypeRT = executeBCScript(billingRun, invoiceType, ratedTransactions, entity, scriptInstance.getCode());
                } else {
                    InvoiceType invoiceType = null;
                    if (!StringUtils.isBlank(billingCycle.getInvoiceTypeEl())) {
                        String invoiceTypeCode = evaluateInvoiceType(billingCycle.getInvoiceTypeEl(), billingRun);
                        invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
                    }
					if (isDraft) {
						invoiceType = invoiceTypeService.getDefaultDraft();
					} else {
						if (invoiceType == null) {
							invoiceType = billingCycle.getInvoiceType();
						}
						if (invoiceType == null) {
							invoiceType = invoiceTypeService.getDefaultCommertial();
						}
					}
                    mapInvTypeRT.put(invoiceType, ratedTransactions);
                }
                
                for (Map.Entry<InvoiceType, List<RatedTransaction>> entry : mapInvTypeRT.entrySet()) {
                    InvoiceType invoiceType = invoiceTypeService.findById(entry.getKey().getId(), true);
                    List<RatedTransaction> ratedTransactionSelection = entry.getValue();
                    Invoice invoice = new Invoice();
                    invoice.setSeller(ratedTransactionGroup.getSeller());
                    invoice.setInvoiceType(invoiceType);
                    invoice.setBillingAccount(billingAccount);
                   
                    if (billingRun != null) {
                        invoice.setBillingRun(em.getReference(BillingRun.class, billingRun.getId()));
                    }
                    invoice.setInvoiceDate(invoiceDate);
                    
                    PaymentMethod paymentMethod = null;
                    
                    Order order = null;
                    if (entity instanceof Order) {
                        order = (Order) entity;
                        paymentMethod = order.getPaymentMethod();
                    }
                    
                    if (entity instanceof Subscription) {
                        invoice.setSubscription((Subscription) entity);
                    }
    
                    CustomerAccount customerAccount = customerAccountService.refreshOrRetrieve(billingAccount.getCustomerAccount());
                    if (paymentMethod == null) {
                        paymentMethod = customerAccountService.getPreferredPaymentMethod(customerAccount.getId());
                        // or this option: paymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
                    }
    
                    if (paymentMethod != null) {
                        invoice.setPaymentMethodType(paymentMethod.getPaymentType());
                        invoice.setPaymentMethod(paymentMethod);
                    }
    
                    Integer delay = billingCycle.getDueDateDelay();
                    if (order != null && !StringUtils.isBlank(order.getDueDateDelayEL())) {
                        delay = evaluateIntegerExpression(order.getDueDateDelayEL(), billingAccount, invoice, order);
                    } else {
                        if (!StringUtils.isBlank(customerAccount.getDueDateDelayEL())) {
                            delay = evaluateIntegerExpression(customerAccount.getDueDateDelayEL(), billingAccount, invoice, order);
                        } else if (!StringUtils.isBlank(billingCycle.getDueDateDelayEL())) {
                            delay = evaluateIntegerExpression(billingCycle.getDueDateDelayEL(), billingAccount, invoice, order);
                        }
                    }
    
                    Date dueDate = invoiceDate;
                    if (delay != null) {
                        dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
                    } else {
                        throw new BusinessException("Due date delay is null");
                    }
                    invoice.setDueDate(dueDate);

                    // compute dueBalance
                    int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
                    BigDecimal balanceDue = customerAccountService.customerAccountBalanceDue(billingAccount.getCustomerAccount(), new Date());
                    BigDecimal totalInvoiceBalance = customerAccountService.customerAccountFutureBalanceExigibleWithoutLitigation(billingAccount.getCustomerAccount());
                    invoice.setDueBalance(balanceDue.add(totalInvoiceBalance));
                    invoice.setDueBalance(invoice.getDueBalance().setScale(rounding, RoundingMode.HALF_UP));
            
                    
                    this.create(invoice);
                    
                    ratedTransactionService.appendInvoiceAgregates(billingAccount, invoice, ratedTransactionFilter, ratedTransactionSelection, firstTransactionDate, lastTransactionDate, false, false, billingRun);
                    log.debug("appended aggregates");
    
                    for (RatedTransaction ratedTransaction : invoice.getRatedTransactions()) {
                        ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
                        ratedTransaction.setInvoice(invoice);
                    }
    
                    List<String> orderNums = null;
                    if (order != null) {
                        orderNums = new ArrayList<String>();
                        orderNums.add(order.getOrderNumber());
                    } else {
                        orderNums = (List<String>) getEntityManager().createNamedQuery("RatedTransaction.getDistinctOrderNumsByInvoice", String.class).setParameter("invoice", invoice)
                            .getResultList();
                        if (orderNums != null && orderNums.size() == 1 && orderNums.get(0) == null) {
                            orderNums = null;
                        }
                    }
    
                    if (orderNums != null && !orderNums.isEmpty()) {
                        List<Order> orders = new ArrayList<Order>();
                        for (String orderNum : orderNums) {
                            orders.add(orderService.findByCodeOrExternalId(orderNum));
                        }
                        invoice.setOrders(orders);
                    }
    
                    invoice.assignTemporaryInvoiceNumber();
                    invoiceList.add(invoice);
                }
            }
        } catch (Exception e) {
            log.error("Error for entity {}", entity.getCode(), e);
            if(entity instanceof BillingAccount) {
                BillingAccount ba = (BillingAccount) entity;
                if (billingRun != null) {
                    rejectedBillingAccountService.create(ba, em.getReference(BillingRun.class, billingRun.getId()), e.getMessage());
                } else {
                    throw new BusinessException(e.getMessage());
                }
            } else {
                throw new BusinessException(e.getMessage());
            }
        }
        
        return invoiceList;
    }
    
    private Map<InvoiceType, List<RatedTransaction>> executeBCScript(BillingRun billingRun, InvoiceType invoiceType, List<RatedTransaction> ratedTransactions, IBillableEntity entity, String scriptInstanceCode) throws BusinessException {
        try {
            log.debug("execute priceplan script " + scriptInstanceCode);
            ScriptInterface script = scriptInstanceService.getCachedScriptInstance(scriptInstanceCode);
            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put(Script.CONTEXT_ENTITY, entity);
            context.put("br", billingRun);
            context.put("invoiceType", invoiceType);
            context.put("ratedTransactions", ratedTransactions);
            script.execute(context);
            return (Map<InvoiceType, List<RatedTransaction>>) context.get(Script.RESULT_VALUE);
        } catch (Exception e) {
            log.error("Error when run script {}", scriptInstanceCode, e);
            throw new BusinessException("failed when run script " + scriptInstanceCode + ", info " + e.getMessage());
        }
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
        assignInvoiceNumber(invoice);

        PaymentMethod preferedPaymentMethod = invoice.getBillingAccount().getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            invoice.setPaymentMethodType(preferedPaymentMethod.getPaymentType());
        }

        ratedTransactionService.appendInvoiceAgregates(billingAccount, invoice, null, ratedTransactions, null, null, false, true, null);

        for (RatedTransaction ratedTransaction : ratedTransactions) {
            ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
        }

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
    public void produceInvoicePdfInNewTransaction(Long invoiceId) throws BusinessException {
        Invoice invoice = findById(invoiceId);
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
        invoice = updateNoCheck(invoice);
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
            throw new InvoiceXmlNotFoundException("The xml invoice file " + invoiceXmlFileName + " doesn't exist.");
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

                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath() + File.separator + billingTemplateName + File.separator
                        + "invoice";

                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    VirtualFile vfDir = VFS.getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell")
                            + ".war/WEB-INF/classes/jasper/" + billingTemplateName + File.separator + "invoice");
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
            if (!destDirInvoiceAdjustment.exists()) {
                destDirInvoiceAdjustment.mkdirs();
                String sourcePathInvoiceAdjustment = Thread.currentThread().getContextClassLoader().getResource("./jasper/" + billingTemplateName + "/invoiceAdjustment").getPath();
                File sourceFileInvoiceAdjustment = new File(sourcePathInvoiceAdjustment);
                if (!sourceFileInvoiceAdjustment.exists()) {
                    VirtualFile vfDir = VFS.getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell")
                            + ".war/WEB-INF/classes/jasper/" + billingTemplateName + "/invoiceAdjustment");
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
            log.info(String.format("Jasper template used: %s", jasperFile.getCanonicalPath()));

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

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFullFilename);
            if ("true".equals(paramBeanFactory.getInstance().getProperty("invoice.pdf.addWaterMark", "true"))) {
                if (invoice.getInvoiceType().getCode().equals(paramBeanFactory.getInstance().getProperty("invoiceType.draft.code", "DRAFT"))) {
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
        String pdfDirName = new StringBuilder(resDir).append(File.separator).append(billingTemplate).append(File.separator)
            .append(isInvoiceAdjustment ? ADJUSTEMENT_DIR_NAME : PDF_DIR_NAME).toString();

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
     * Delete invoice.
     *
     * @param invoice invoice to delete
     * @throws BusinessException business exception
     */
    public void deleteInvoice(Invoice invoice) throws BusinessException {
        getEntityManager().createNamedQuery("RatedTransaction.deleteInvoice").setParameter("invoice", invoice).executeUpdate();

        super.remove(invoice);
    }

    /**
     * Evaluate prefix el expression.
     *
     * @param prefix prefix of EL expression
     * @param invoice invoice
     * @return evaluated value
     * @throws BusinessException business exception
     */
    public String evaluatePrefixElExpression(String prefix, Invoice invoice) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(prefix)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (prefix.indexOf("entity") >= 0) {
            userMap.put("entity", invoice);
        }
        if (prefix.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }

        Object res = ValueExpressionWrapper.evaluateExpression(prefix, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + prefix + " do not evaluate to String but " + res);
        }
        return result;
    }

    /**
     * Recompute aggregates.
     *
     * @param invoice invoice
     * @throws BusinessException business exception
     */
    public void recomputeAggregates(Invoice invoice) throws BusinessException {
        
        boolean entreprise = appProvider.isEntreprise();
        // #3036 : using invoice rounding & rounding mode instead of the rating rounding
        int invoiceRounding = appProvider.getInvoiceRounding() == null ? 2 : appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();
        
        
        BillingAccount billingAccount = billingAccountService.findById(invoice.getBillingAccount().getId());
        boolean exoneratedFromTaxes = billingAccountService.isExonerated(billingAccount);
        BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;

        Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();
        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregate>();
        invoice.setAmountTax(null);
        invoice.setAmountWithoutTax(null);
        invoice.setAmountWithTax(null);

        // update the aggregated subcat of an invoice
        for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
            if (invoiceAggregate instanceof CategoryInvoiceAgregate) {
                invoiceAggregate.resetAmounts();
            } else if (invoiceAggregate instanceof TaxInvoiceAgregate) {
                TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAggregate;
                taxInvoiceAgregateMap.put(taxInvoiceAgregate.getTax().getId(), taxInvoiceAgregate);
            } else if (invoiceAggregate instanceof SubCategoryInvoiceAgregate) {
                subCategoryInvoiceAgregates.add((SubCategoryInvoiceAgregate) invoiceAggregate);
            }
        }

        for (TaxInvoiceAgregate taxInvoiceAgregate : taxInvoiceAgregateMap.values()) {
            taxInvoiceAgregate.setAmountWithoutTax(new BigDecimal(0));
            for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : subCategoryInvoiceAgregates) {
                if (subCategoryInvoiceAgregate.getQuantity().signum() != 0) {
                    if (subCategoryInvoiceAgregate.getSubCategoryTaxes().contains(taxInvoiceAgregate.getTax())) {
                        taxInvoiceAgregate.addAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
                    }
                }
            }

            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountWithoutTax().multiply(taxInvoiceAgregate.getTaxPercent()).divide(new BigDecimal("100")));
            // then round the tax
            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax().setScale(rounding, getRoundingMode(roundingMode) ));

            taxInvoiceAgregate.setAmountWithTax(taxInvoiceAgregate.getAmountWithoutTax().add(taxInvoiceAgregate.getAmountTax()));
        }

        // update the amount with and without tax of all the tax aggregates in
        // each sub category aggregate
        SubCategoryInvoiceAgregate biggestSubCat = null;
        BigDecimal biggestAmount = new BigDecimal("-100000000");

        for (InvoiceAgregate invoiceAgregate : subCategoryInvoiceAgregates) {
            SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;

            if (!entreprise) {
                nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add(subCategoryInvoiceAgregate.getAmountWithTax());
            }

            BigDecimal amountWithoutTax = subCategoryInvoiceAgregate.getAmountWithoutTax();
            subCategoryInvoiceAgregate.setAmountWithoutTax(amountWithoutTax != null ? amountWithoutTax.setScale(rounding, getRoundingMode(roundingMode)) : BigDecimal.ZERO);

            subCategoryInvoiceAgregate.getCategoryInvoiceAgregate().addAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());

            if (subCategoryInvoiceAgregate.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
                biggestAmount = subCategoryInvoiceAgregate.getAmountWithoutTax();
                biggestSubCat = subCategoryInvoiceAgregate;
            }
        }

        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
                invoice.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax().setScale(invoiceRounding, getRoundingMode(invoiceRoundingMode) ));
            }

            if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
                invoice.addAmountTax(taxInvoiceAgregate.getAmountTax().setScale(invoiceRounding, getRoundingMode(invoiceRoundingMode)));
            }
        }

        if (invoice.getAmountWithoutTax() != null) {
            invoice.setAmountWithTax(invoice.getAmountWithoutTax().add(invoice.getAmountTax() == null ? BigDecimal.ZERO : invoice.getAmountTax()));
        }

        if (!entreprise && biggestSubCat != null && !exoneratedFromTaxes) {
            BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
            log.debug("delta={}-{}={}", nonEnterprisePriceWithTax, invoice.getAmountWithTax(), delta);

            biggestSubCat.setAmountWithoutTax(biggestSubCat.getAmountWithoutTax().add(delta).setScale(rounding, getRoundingMode(roundingMode) ));
            for (Tax tax : biggestSubCat.getSubCategoryTaxes()) {
                TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(tax.getId());
                log.debug("tax3 ht={}", invoiceAgregateT.getAmountWithoutTax());

                invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT.getAmountWithoutTax().add(delta).setScale(rounding, getRoundingMode(roundingMode) ));
                log.debug("tax4 ht={}", invoiceAgregateT.getAmountWithoutTax());

            }

            CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
            invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR.getAmountWithoutTax().add(delta).setScale(rounding, getRoundingMode(roundingMode) ));

            invoice.setAmountWithoutTax(invoice.getAmountWithoutTax().add(delta).setScale(invoiceRounding, getRoundingMode(invoiceRoundingMode) ));
            invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(invoiceRounding, getRoundingMode(invoiceRoundingMode) ));
        }

        // calculate discounts here
        // no need to create discount aggregates we will use the one from
        // adjustedInvoice

        Object[] discountAmount = invoiceAgregateService.findTotalAmountsForDiscountAggregates(getLinkedInvoice(invoice));
        BigDecimal discountAmountWithoutTax = (BigDecimal) discountAmount[0];
        BigDecimal discountAmountTax = (BigDecimal) discountAmount[1];
        BigDecimal discountAmountWithTax = (BigDecimal) discountAmount[2];

        log.debug("discountAmountWithoutTax= {}, discountAmountTax={}, discountAmountWithTax={}", discountAmount[0], discountAmount[1], discountAmount[2]);

        invoice.addAmountWithoutTax( round(discountAmountWithoutTax, invoiceRounding, invoiceRoundingMode) );
        invoice.addAmountTax( round(discountAmountTax, invoiceRounding, invoiceRoundingMode) );
        invoice.addAmountWithTax( round(discountAmountWithTax, invoiceRounding, invoiceRoundingMode) );

        // compute net to pay
        BigDecimal netToPay = BigDecimal.ZERO;
        if (entreprise) {
            netToPay = invoice.getAmountWithTax();
        } else {
            BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount().getCustomerAccount().getCode(), invoice.getDueDate());

            if (balance == null) {
                throw new BusinessException("account balance calculation failed");
            }
            netToPay = invoice.getAmountWithTax().add( round(balance, invoiceRounding, invoiceRoundingMode) );
        }

        invoice.setNetToPay(netToPay);
    }

    /**
     * Recompute sub category aggregate.
     *
     * @param invoice invoice used to recompute
     */
    public void recomputeSubCategoryAggregate(Invoice invoice) {
        
        int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        List<TaxInvoiceAgregate> taxInvoiceAgregates = new ArrayList<TaxInvoiceAgregate>();
        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregate>();

        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                taxInvoiceAgregates.add((TaxInvoiceAgregate) invoiceAgregate);
            } else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                subCategoryInvoiceAgregates.add((SubCategoryInvoiceAgregate) invoiceAgregate);
            }
        }

        for (TaxInvoiceAgregate taxInvoiceAgregate : taxInvoiceAgregates) {
            taxInvoiceAgregate.setAmountWithoutTax(new BigDecimal(0));
            for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : subCategoryInvoiceAgregates) {
                if (subCategoryInvoiceAgregate.getQuantity().signum() != 0) {
                    if (subCategoryInvoiceAgregate.getSubCategoryTaxes().contains(taxInvoiceAgregate.getTax())) {
                        taxInvoiceAgregate.addAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
                    }
                }
            }

            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountWithoutTax().multiply(taxInvoiceAgregate.getTaxPercent()).divide(new BigDecimal("100")));
            // then round the tax
            taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax().setScale(rounding, getRoundingMode(roundingMode)));

            taxInvoiceAgregate.setAmountWithTax(taxInvoiceAgregate.getAmountWithoutTax().add(taxInvoiceAgregate.getAmountTax()));
        }
    }

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
                Object value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);

                if (value == null) {
                } else if (value instanceof String) {
                    xmlFileName = (String) value;
                } else {
                    xmlFileName = value.toString();
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default XML filename will be used instead. Error is logged in EL evaluation
            }
        }

        // Default to invoiceDateOrBillingRunId/invoiceNumber.xml or invoiceDateOrBillingRunId/_IA_invoiceNumber.xml for adjustment invoice
        if (StringUtils.isBlank(xmlFileName)) {

            boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());

            BillingRun billingRun = invoice.getBillingRun();
            String brPath = billingRun == null ? DateUtils.formatDateWithPattern(invoice.getInvoiceDate(), paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss"))
                    : billingRun.getId().toString();

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
                Object value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);

                if (value == null) {
                } else if (value instanceof String) {
                    pdfFileName = (String) value;
                } else {
                    pdfFileName = value.toString();
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
            }
        }

        // Default to invoiceDate_invoiceNumber.pdf or invoiceDate_IA_invoiceNumber.pdf for adjustment invoice
        if (StringUtils.isBlank(pdfFileName)) {

            boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());

            pdfFileName = formatInvoiceDate(invoice.getInvoiceDate())
                    + (isInvoiceAdjustment ? paramBeanFactory.getInstance().getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") : "_")
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
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceInvoiceXmlInNewTransaction(Long invoiceId) throws BusinessException {
        Invoice invoice = findById(invoiceId);
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
        invoice = updateNoCheck(invoice);
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

        String xmlFileName = getFullXmlFilePath(invoice, false);
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists()) {
            throw new BusinessException("Invoice XML was not produced yet for invoice " + invoice.getInvoiceNumberOrTemporaryNumber());
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(xmlFile);
            String xmlContent = scanner.useDelimiter("\\Z").next();
            scanner.close();
            return xmlContent;
        } catch (Exception e) {
            log.error("Error reading invoice XML file {} contents", xmlFileName, e);

        } finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    log.error("Error closing file scanner", e);
                }
            }

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
     * Create RatedTransaction and generate invoice for the billingAccount. 
     *
     * @param entity entity to be billed.
     * @param generateInvoiceRequestDto the generate invoice request dto
     * @param ratedTxFilter the rated tx filter
     * @param isDraft the is draft
     * @return the invoice
     * @throws BusinessException the business exception
     * @throws InvoiceExistException the invoice exist exception
     * @throws ImportInvoiceException the import invoice exception
     */
    public List<Invoice> generateInvoice(IBillableEntity entity, GenerateInvoiceRequestDto generateInvoiceRequestDto, Filter ratedTxFilter, boolean isDraft)
            throws BusinessException, InvoiceExistException, ImportInvoiceException {

        Date invoiceDate = generateInvoiceRequestDto.getInvoicingDate();
        Date firstTransactionDate = generateInvoiceRequestDto.getFirstTransactionDate();
        Date lastTransactionDate = generateInvoiceRequestDto.getLastTransactionDate();

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
            lastTransactionDate = new Date();
        }

        if (entity.getBillingRun() != null && (entity.getBillingRun().getStatus().equals(BillingRunStatusEnum.NEW)
                || entity.getBillingRun().getStatus().equals(BillingRunStatusEnum.PREVALIDATED)
                || entity.getBillingRun().getStatus().equals(BillingRunStatusEnum.POSTVALIDATED))) {

            throw new BusinessException("The entity is already in an billing run with status " + entity.getBillingRun().getStatus());
        }
        
        ratedTransactionService.createRatedTransaction(entity, invoiceDate);

        entity = billingAccountService.calculateInvoicing(entity, firstTransactionDate, lastTransactionDate, null);
        List<Invoice> invoices = createAgregatesAndInvoice(entity, null, ratedTxFilter, invoiceDate, firstTransactionDate, lastTransactionDate, entity.getMinRatedTransactions(), isDraft);
        
//        if (!isDraft) {
            for(Invoice invoice: invoices) {
                assignInvoiceNumber(invoice);
            }
//        }
        
        // TODO : delete this commit since generating PDF/XML and producing AOs are now outside this service !
        // Only added here so invoice changes would be pushed to DB before constructing XML and PDF as those are independent tasks
        // Why not add a new method on another bean with Tx.Requires_New?
        commit();
        return invoices;
    }

    /**
     * Produce files and AO.
     *
     * @param produceXml the produce xml
     * @param producePdf the produce pdf
     * @param generateAO the generate AO
     * @param invoice the invoice
     * @param isDraft is it a draft
     * @throws BusinessException the business exception
     * @throws InvoiceExistException the invoice exist exception
     * @throws ImportInvoiceException the import invoice exception
     */
    public void produceFilesAndAO(boolean produceXml, boolean producePdf, boolean generateAO, Invoice invoice, boolean isDraft)
            throws BusinessException, InvoiceExistException, ImportInvoiceException {
        if (produceXml) {
            produceInvoiceXmlNoUpdate(invoice);
        }
        if (producePdf) {
            produceInvoicePdfNoUpdate(invoice);
        }
        if (generateAO && !isDraft) {
            recordedInvoiceService.generateRecordedInvoice(invoice);
        }
        update(invoice);
    }

    /**
     * Create RatedTransaction and generate invoice for the billingAccount.
     * 
     * @param billingAccount billing account
     * @param invoiceDate date of invoice
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate date of last transaction
     * @param ratedTxFilter rated transaction filter
     * @param orderNumber Order number associated to subscription
     * @param isDraft Is it a draft
     * @param produceXml Produce invoice XML file
     * @param producePdf Produce invoice PDF file
     * @param generateAO Generate AOs
     * @return invoice
     * @throws BusinessException business exception
     * @throws ImportInvoiceException import invoice exception
     * @throws InvoiceExistException invoice exists exception
     * 
     * @deprecated : - It contains a lot of args.
     *               - It breaks the 'Separation of responsibilities' pattern by creating the Invoice, creating the PDF/XML file and producing the AOs !! <br>
     *                use generateInvoice(BillingAccount, GenerateInvoiceRequestDto) + produceFilesAndAO(boolean, boolean, boolean, Invoice) instead.
     */
    @Deprecated
    public List<Invoice> generateInvoice(BillingAccount billingAccount, Date invoiceDate, Date firstTransactionDate, Date lastTransactionDate, Filter ratedTxFilter, String orderNumber,
            boolean isDraft, boolean produceXml, boolean producePdf, boolean generateAO) throws BusinessException, InvoiceExistException, ImportInvoiceException {

        GenerateInvoiceRequestDto generateInvoiceRequestDto = new GenerateInvoiceRequestDto();
        generateInvoiceRequestDto.setGenerateXML(produceXml);
        generateInvoiceRequestDto.setGeneratePDF(producePdf);
        generateInvoiceRequestDto.setGenerateAO(generateAO);
        generateInvoiceRequestDto.setInvoicingDate(invoiceDate);
        generateInvoiceRequestDto.setFirstTransactionDate(firstTransactionDate);
        generateInvoiceRequestDto.setLastTransactionDate(lastTransactionDate);
        generateInvoiceRequestDto.setOrderNumber(orderNumber);
        
        List<Invoice> invoices = this.generateInvoice(billingAccount, generateInvoiceRequestDto, ratedTxFilter, isDraft);
        for(Invoice invoice : invoices) {
            this.produceFilesAndAO(produceXml, producePdf, generateAO, invoice, isDraft);
        }
        
        return invoices;
    }

    /**
     * Delete min RT.
     *
     * @param invoice invoice to delete
     * @throws BusinessException business exception
     */
    public void deleteMinRT(Invoice invoice) throws BusinessException {
        getEntityManager().createNamedQuery("RatedTransaction.deleteMinRT").setParameter("invoice", invoice).executeUpdate();
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

        deleteMinRT(invoice);
        deleteInvoice(invoice);
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
    public Integer evaluateIntegerExpression(String expression, BillingAccount billingAccount, Invoice invoice, Order order) throws BusinessException {
        Integer result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("invoice") >= 0) {
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
        String billingTemplateName = null;

        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("invoice", invoice);

            try {
                Object value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);

                if (value == null) {
                } else if (value instanceof String) {
                    billingTemplateName = (String) value;
                } else {
                    billingTemplateName = value.toString();
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
            }
        }

        billingTemplateName = StringUtils.normalizeFileName(billingTemplateName);
        return billingTemplateName;
    }

    public String evaluateInvoiceType(String expression, BillingRun billingRun) {
        String invoiceTypeCode = null;

        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("br", billingRun);

            try {
                Object value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);
                if (value == null) {
                } else if (value instanceof String) {
                    invoiceTypeCode = (String) value;
                } else {
                    invoiceTypeCode = value.toString();
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

        String billingTemplateName = "default";
        if (invoiceType != null && !StringUtils.isBlank(invoiceType.getBillingTemplateNameEL())) {
            billingTemplateName = evaluateBillingTemplateName(invoiceType.getBillingTemplateNameEL(), invoice);

        } else if (billingCycle != null && !StringUtils.isBlank(billingCycle.getBillingTemplateNameEL())) {
            billingTemplateName = evaluateBillingTemplateName(billingCycle.getBillingTemplateNameEL(), invoice);

        } else if (invoiceType != null && !StringUtils.isBlank(invoiceType.getBillingTemplateName())) {
            billingTemplateName = invoiceType.getBillingTemplateName();

        } else if (billingCycle != null && billingCycle.getInvoiceType() != null && !StringUtils.isBlank(billingCycle.getInvoiceType().getBillingTemplateName())) {
            billingTemplateName = billingCycle.getInvoiceType().getBillingTemplateName();

        } else if (billingCycle != null && !StringUtils.isBlank(billingCycle.getBillingTemplateName())) {
            billingTemplateName = billingCycle.getBillingTemplateName();
        }

        return billingTemplateName;
    }

    /**
     * Assign invoice number and increment BA invoice date.
     *
     * @param invoiceId invoice id
     * @param invoicesToNumberInfo instance of InvoicesToNumberInfo
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void assignInvoiceNumberAndIncrementBAInvoiceDate(Long invoiceId, InvoicesToNumberInfo invoicesToNumberInfo) throws BusinessException {

        Invoice invoice = findById(invoiceId);
        assignInvoiceNumberFromReserve(invoice, invoicesToNumberInfo);

        BillingAccount billingAccount = invoice.getBillingAccount();

        Date initCalendarDate = billingAccount.getSubscriptionDate();
        if (initCalendarDate == null) {
            initCalendarDate = billingAccount.getAuditable().getCreated();
        }
        Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate(initCalendarDate);
        billingAccount.setNextInvoiceDate(nextCalendarDate);
        billingAccount.updateAudit(currentUser);
        invoice = update(invoice);
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
        return getEntityManager().createNamedQuery("Invoice.byBrItSelDate", Long.class).setParameter("billingRunId", billingRunId).setParameter("invoiceTypeId", invoiceTypeId)
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
     * Return all invoices with now - invoiceDate date &gt; n years.
     * @param nYear age of the invoices
     * @return Filtered list of invoices
     */
    @SuppressWarnings("unchecked")
	public List<Invoice> listInactiveInvoice(int nYear) {
    	QueryBuilder qb = new QueryBuilder(Invoice.class, "e");
    	Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);
    	
    	qb.addCriterionDateRangeToTruncatedToDay("invoiceDate", higherBound);
    	
    	return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
    }

	public void bulkDelete(List<Invoice> inactiveInvoices) throws BusinessException {
		for (Invoice e : inactiveInvoices) {
			remove(e);
		}
	}

    /**
     * Nullify BR's invoices file names (xml and pdf).
     *
     * @param billingRun the billing run
     */
    public void nullifyInvoiceFileNames(BillingRun billingRun) {
        getEntityManager().createNamedQuery("Invoice.nullifyInvoiceFileNames").setParameter("billingRun", billingRun).executeUpdate();
    }      
}