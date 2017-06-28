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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.InvoiceXmlNotFoundException;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.admin.job.PdfGeneratorConstants;
import org.meveo.admin.util.PdfWaterMark;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.exceptions.ConfigurationException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.billing.Sequence;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.crm.Customer;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
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

@Stateless
public class InvoiceService extends PersistenceService<Invoice> {

	private ParamBean paramBean = ParamBean.getInstance();

	public final static String INVOICE_ADJUSTMENT_SEQUENCE = "INVOICE_ADJUSTMENT_SEQUENCE";

	public final static String INVOICE_SEQUENCE = "INVOICE_SEQUENCE";
	
	@EJB
	private PDFParametersConstruction pDFParametersConstruction;
	
	@EJB
	private XMLInvoiceCreator xmlInvoiceCreator;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private InvoiceAgregateService invoiceAgregateService;
	
	@Inject
	private BillingAccountService billingAccountService;
	
	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private RejectedBillingAccountService rejectedBillingAccountService;
	
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;
    
    @Inject
    private InvoiceTypeService invoiceTypeService;
    
    @Inject
    private CustomerService customerService;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private OrderService orderService;
	
    @Inject
    private RecordedInvoiceService recordedInvoiceService;

	private String PDF_DIR_NAME = "pdf";
	private String ADJUSTEMENT_DIR_NAME = "invoiceAdjustmentPdf";
	private String INVOICE_TEMPLATE_FILENAME = "invoice.jasper";
	private String DATE_PATERN = "yyyy.MM.dd";

	public Invoice getInvoice(String invoiceNumber, CustomerAccount customerAccount) throws BusinessException {
		try {
			Query q = getEntityManager()
					.createQuery("from Invoice where invoiceNumber = :invoiceNumber and billingAccount.customerAccount=:customerAccount");
			q.setParameter("invoiceNumber", invoiceNumber).setParameter("customerAccount", customerAccount);
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log.info("Invoice with invoice number #0 was not found. Returning null.", invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log.info("Multiple invoices with invoice number #0 was found. Returning null.", invoiceNumber);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Invoice getInvoiceByNumber(String invoiceNumber) throws BusinessException {
		return getInvoiceByNumber(invoiceNumber, invoiceTypeService.getDefaultCommertial());
	}

	public Invoice findByInvoiceNumberAndType(String invoiceNumber, InvoiceType invoiceType)
			throws BusinessException {
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
	
	public Invoice getInvoiceByNumber(String invoiceNumber, InvoiceType invoiceType) throws BusinessException {
		return findByInvoiceNumberAndType(invoiceNumber, invoiceType);
	}

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

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices(BillingAccount billingAccount, InvoiceType invoiceType) throws BusinessException {
		try {
			Query q = getEntityManager().createQuery(
					"from Invoice where billingAccount = :billingAccount and invoiceType=:invoiceType");
			q.setParameter("billingAccount", billingAccount);
			q.setParameter("invoiceType", invoiceType);
			List<Invoice> invoices = q.getResultList();
			log.info("getInvoices: founds #0 invoices with BA_code={} and type=#2 ", invoices.size(),
					billingAccount.getCode(), invoiceType);
			return invoices;
		} catch (Exception e) {
			return null;
		}
	}

	public void setInvoiceNumber(Invoice invoice) throws BusinessException {
		invoice.setInvoiceNumber(getInvoiceNumber(invoice));
	}

	public String getInvoiceNumber(Invoice invoice) throws BusinessException {		
		String cfName = "INVOICE_SEQUENCE_"+invoice.getInvoiceType().getCode().toUpperCase();			
		if(invoiceTypeService.getAdjustementCode().equals(invoice.getInvoiceType().getCode())){
			cfName = "INVOICE_ADJUSTMENT_SEQUENCE";
		}
		if(invoiceTypeService.getCommercialCode().equals(invoice.getInvoiceType().getCode())){
			cfName = "INVOICE_SEQUENCE";
		}
		Customer cust = customerService.refreshOrRetrieve(invoice.getBillingAccount().getCustomerAccount().getCustomer());
		
		InvoiceType invoiceType = invoiceTypeService.refreshOrRetrieve(invoice.getInvoiceType());
		Seller seller = chooseSeller(cust.getSeller(), cfName, invoice.getInvoiceDate(), invoiceType);

        Sequence sequence = getSequence(invoice, seller,cfName,1,true);
		String prefix = sequence.getPrefixEL();
		int sequenceSize = sequence.getSequenceSize();

		if (prefix != null && !StringUtils.isBlank(prefix)) {
			prefix = evaluatePrefixElExpression(prefix, invoice);
		}

		long nextInvoiceNb = sequence.getCurrentInvoiceNb();		
		String invoiceNumber = StringUtils.getLongAsNChar(nextInvoiceNb, sequenceSize);
		// request to store invoiceNo in alias field
		invoice.setAlias(invoiceNumber);

		return (prefix + invoiceNumber);
	}

	
	public synchronized Sequence getSequence(Invoice invoice ,Seller seller,String cfName,int step,boolean increment)throws BusinessException{			
		Long currentNbFromCF = null;				
		Object currentValObj = customFieldInstanceService.getCFValue(seller, cfName, invoice.getInvoiceDate());
		if(currentValObj != null){			
			currentNbFromCF = (Long)currentValObj;
			if(increment){
				currentNbFromCF = currentNbFromCF + step;
				 customFieldInstanceService.setCFValue(seller, cfName,currentNbFromCF, invoice.getInvoiceDate());
				 customFieldInstanceService.commit();
			}
		}else{
			currentValObj = customFieldInstanceService.getCFValue(appProvider, cfName, invoice.getInvoiceDate());
			if(currentValObj != null){
				currentNbFromCF = (Long)currentValObj;
				if(increment){
					currentNbFromCF = currentNbFromCF + step;
					 customFieldInstanceService.setCFValue(appProvider, cfName,currentNbFromCF, invoice.getInvoiceDate());
					 customFieldInstanceService.commit();
				}
			}
		}
		
		InvoiceType invoiceType = invoice.getInvoiceType();
		invoiceType = invoiceTypeService.refreshOrRetrieve(invoiceType);
		Sequence sequence = null;			
		if(invoiceType.getSellerSequence() != null && invoiceType.isContainsSellerSequence(seller)){			
			sequence =  invoiceType.getSellerSequenceByType(seller).getSequence();
			if(increment && currentNbFromCF == null){				
				sequence.setCurrentInvoiceNb((sequence.getCurrentInvoiceNb() == null?0L:sequence.getCurrentInvoiceNb()) +step);
				invoiceType.getSellerSequenceByType(seller).setSequence(sequence);
				invoiceTypeService.update(invoiceType);
			}
		}else{			
			if(invoiceType.getSequence() != null){				
				sequence =  invoiceType.getSequence();
				if(increment && currentNbFromCF == null){					
					sequence.setCurrentInvoiceNb((sequence.getCurrentInvoiceNb() == null?0L:sequence.getCurrentInvoiceNb()) +step);
					invoiceType.setSequence(sequence);
					invoiceTypeService.update(invoiceType);
				}
			}
		}
		if(sequence == null){			
			sequence = new Sequence();
			sequence.setCurrentInvoiceNb(1L);
			sequence.setSequenceSize(9);
			sequence.setPrefixEL("");
			invoiceType.setSequence(sequence);
			invoiceTypeService.update(invoiceType);			
		}
		if(currentNbFromCF != null){			
			sequence.setCurrentInvoiceNb(currentNbFromCF);
		}	
		log.debug("getSequence:"+sequence);
		invoiceTypeService.commit();
		
		return sequence;		
	}
	
	
	@SuppressWarnings("unchecked")
    public List<Invoice> getValidatedInvoicesWithNoPdf(BillingRun br) {
		try {
			QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
			qb.addCriterionEntity("i.billingRun.status", BillingRunStatusEnum.VALIDATED);
			qb.addSql("i.isPdfGenerated is false");

			if (br != null) {
				qb.addCriterionEntity("i.billingRun", br);
			}
			return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
		} catch (Exception ex) {
			log.error("failed to get validated invoices with no pdf", ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getInvoiceIdsWithNoAccountOperation(BillingRun br) {
		try {
			QueryBuilder qb = new QueryBuilder("SELECT i.id FROM " + Invoice.class.getName() + " i");
			qb.addSql("i.invoiceNumber is not null");
			qb.addSql("i.recordedInvoice is null");
			if (br != null) {
				qb.addCriterionEntity("i.billingRun", br);
			}
			return (List<Long>) qb.getQuery(getEntityManager()).getResultList();
		} catch (Exception ex) {
			log.error("failed to get invoices with no account operation", ex);
		}
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Invoice createAgregatesAndInvoice(BillingAccount billingAccount, BillingRun billingRun, Filter ratedTransactionFilter,String orderNumber,
			Date invoiceDate,Date lastTransactionDate)
			throws BusinessException {
		Invoice invoice =null;
		log.debug("createAgregatesAndInvoice billingAccount={} , billingRunId={} , ratedTransactionFilter={} , orderNumber{}, lastTransactionDate={} ,invoiceDate={} ", 
				billingAccount,billingRun!=null?billingRun.getId():null,ratedTransactionFilter,orderNumber,lastTransactionDate,invoiceDate);

		EntityManager em = getEntityManager();
		if (billingRun == null){
			if(invoiceDate==null){
				throw new BusinessException("invoiceDate must be set if billingRun is null");	
			}
			if(StringUtils.isBlank(lastTransactionDate) && 
					StringUtils.isBlank(orderNumber) && 
					ratedTransactionFilter == null){
				throw new BusinessException("lastTransactionDate or orderNumber or ratedTransactionFilter must be set if billingRun is null");
			}
		} else {
		    lastTransactionDate = billingRun.getLastTransactionDate();		 
		    invoiceDate = billingRun.getInvoiceDate();
		}
		
		if(billingAccount.getInvoicingThreshold() != null){
			BigDecimal invoiceAmount  = billingAccountService.computeBaInvoiceAmount(billingAccount, lastTransactionDate);
			if(invoiceAmount == null){
				throw new BusinessException("Cant compute invoice amount");
			}			
			if (billingAccount.getInvoicingThreshold().compareTo(invoiceAmount) > 0) {
				throw new BusinessException("Invoice amount below the threshold");	
			}
		}
		
		try {
		    billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
		
            Long startDate = System.currentTimeMillis();
            BillingCycle billingCycle = billingRun == null ? billingAccount.getBillingCycle() : billingRun.getBillingCycle();
            if (billingCycle == null) {
				billingCycle = billingAccount.getBillingCycle();
			}
			if(billingCycle == null){
				throw new BusinessException("Cant find the billing cycle");
			}
			InvoiceType invoiceType = billingCycle.getInvoiceType();
			if(invoiceType == null){
				invoiceType = invoiceTypeService.getDefaultCommertial();
			}			
			invoice = new Invoice();
			invoice.setInvoiceType(invoiceType);
			invoice.setBillingAccount(billingAccount);
            if (billingRun != null) {
                invoice.setBillingRun(em.getReference(BillingRun.class, billingRun.getId()));
            }
			invoice.setInvoiceDate(invoiceDate);

			PaymentMethodEnum paymentMethod = billingAccount.getPaymentMethod();
			if (paymentMethod == null) {
				paymentMethod = billingAccount.getCustomerAccount().getPaymentMethod();
			}
			invoice.setPaymentMethod(paymentMethod);
			
			Integer delay = billingCycle.getDueDateDelay();			
			if (!StringUtils.isBlank(billingAccount.getCustomerAccount().getDueDateDelayEL())) {
				delay = evaluateIntegerExpression(billingAccount.getCustomerAccount().getDueDateDelayEL(), billingAccount, invoice);
			} else if (!StringUtils.isBlank(billingCycle.getDueDateDelayEL())) {
				delay = evaluateIntegerExpression(billingCycle.getDueDateDelayEL(), billingAccount, invoice);
			}
			
			Date dueDate = invoiceDate;
			if (delay != null) {
				dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
			}
			invoice.setDueDate(dueDate);

			create(invoice);

			ratedTransactionService.createInvoiceAndAgregates(billingAccount, invoice,
					ratedTransactionFilter,orderNumber,lastTransactionDate);
			log.debug("created aggregates");

			// Note that rated transactions get updated in
			// ratedTransactionservice in case of Filter or orderNumber not empty
            if (ratedTransactionFilter == null && StringUtils.isBlank(orderNumber)) {
                Query query = em.createNamedQuery("RatedTransaction.updateInvoiced" + (billingRun == null ? "NoBR" : "")).setParameter("billingAccount", billingAccount)
                    .setParameter("lastTransactionDate", lastTransactionDate).setParameter("invoice", invoice);
                if (billingRun != null) {
                    query = query.setParameter("billingRun", billingRun);
                }
                query.executeUpdate();
            }

			StringBuffer num1 = new StringBuffer("000000000");
			num1.append(invoice.getId() + "");
			String invoiceNumber = num1.substring(num1.length() - 9);
			int key = 0;

			for (int i = 0; i < invoiceNumber.length(); i++) {
				key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
			}

			invoice.setTemporaryInvoiceNumber(invoiceNumber + "-" + key % 10);
			// getEntityManager().merge(invoice);
			
			List<String> orderNums = null;
			if(!StringUtils.isBlank(orderNumber)){
				orderNums = new ArrayList<String>();
				orderNums.add(orderNumber);
			}else{
				ratedTransactionService.commit();				
				orderNums = (List<String>) getEntityManager().createNamedQuery("RatedTransaction.getDistinctOrderNumsByInvoice", String.class).setParameter("invoice", invoice).getResultList();
				if(orderNums != null && orderNums.size() == 1 && orderNums.get(0) == null ){
					orderNums = null;
				}
			}		
			if(orderNums != null && !orderNums.isEmpty()){							
				List<Order> orders = new ArrayList<Order>();
				for(String orderNum : orderNums){
					orders.add(orderService.findByCodeOrExternalId(orderNum));
				}
				invoice.setOrders(orders);
			}			
			Long endDate = System.currentTimeMillis();

			log.info("createAgregatesAndInvoice BR_ID=" +( billingRun==null?"null":billingRun.getId() )+ ", BA_ID=" + billingAccount.getId()
					+ ", Time en ms=" + (endDate - startDate));
		} catch (BusinessException e) {
			log.error("Error for BA=" + billingAccount.getCode() + " : ", e);
			if(billingRun != null){
				RejectedBillingAccount rejectedBA = new RejectedBillingAccount(billingAccount, em.getReference(BillingRun.class, billingRun.getId()), e.getMessage());
				rejectedBillingAccountService.create(rejectedBA);
			}
			throw e;
		}		
		return invoice;
	}
	
    public Invoice createAgregatesAndInvoiceVirtual(List<RatedTransaction> ratedTransactions, BillingAccount billingAccount, InvoiceType invoiceType)
            throws BusinessException {

        if (invoiceType == null) {
            invoiceType = invoiceTypeService.getDefaultCommertial();
        }
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(invoiceType);
        invoice.setBillingAccount(billingAccount);
        invoice.setInvoiceDate(new Date());

        PaymentMethodEnum paymentMethod = billingAccount.getPaymentMethod();
        if (paymentMethod == null) {
            paymentMethod = billingAccount.getCustomerAccount().getPaymentMethod();
        }
        invoice.setPaymentMethod(paymentMethod);

        ratedTransactionService.createInvoiceAndAgregates(billingAccount, invoice, null, ratedTransactions, null, null, false, true);

        for (RatedTransaction ratedTransaction : ratedTransactions) {
            ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
        }

        invoice.setTemporaryInvoiceNumber(UUID.randomUUID().toString());

        return invoice;
    }
	

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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void produceInvoicePdfInNewTransaction(Long invoiceId) throws BusinessException {
	    Invoice invoice = findById(invoiceId);
	    produceInvoicePdf(invoice);
	}

    /**
     * Produce invoice's XML file and update invoice record in DB
     * 
     * @param invoice Invoice
     * @return XML File
     * @throws BusinessException
     */
    public Invoice produceInvoicePdf(Invoice invoice) throws BusinessException {

        produceInvoicePdfNoUpdate(invoice);
        invoice = updateNoCheck(invoice);
        return invoice;
    }

    /**
     * Produce invoice 
     * @param invoice
     * @throws BusinessException
     */
    public void produceInvoicePdfNoUpdate(Invoice invoice) throws BusinessException {

        String meveoDir = paramBean.getProperty("providers.rootDir", "./opencelldata/") + File.separator + appProvider.getCode() + File.separator;
        String invoiceXmlFileName = getFullXmlFilePath(invoice, false);
        Map<String, Object> parameters = pDFParametersConstruction.constructParameters(invoice);

        log.info("PDFInvoiceGenerationJob is invoice key exists=" + ((parameters != null) ? parameters.containsKey(PdfGeneratorConstants.INVOICE) + "" : "parameters is null"));

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
        
        String billingTemplateName = InvoiceService.getInvoiceTemplateName(billingCycle, invoice.getInvoiceType());
        
        String resDir = meveoDir + "jasper";

        String pdfFileName = getFullPdfFilePath(invoice, true);

        try {
            File destDir = new File(resDir + File.separator + billingTemplateName + File.separator + "pdf");
            if (!destDir.exists()) {
                destDir.mkdirs();
                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath();
                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    VirtualFile vfDir = VFS.getChild("content/" + ParamBean.getInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper");
                    log.info("default jaspers path :" + vfDir.getPathName());
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFile = new File(vfPath.getPath());
                    if (!sourceFile.exists()) {
                        throw new BusinessException("embedded jasper report for invoice is missing!");
                    }
                }
                FileUtils.copyDirectory(sourceFile, destDir);
            }
            File destDirInvoiceAdjustment = new File(resDir + File.separator + billingTemplateName + File.separator + "invoiceAdjustmentPdf");
            if (!destDirInvoiceAdjustment.exists()) {
                destDirInvoiceAdjustment.mkdirs();
                String sourcePathInvoiceAdjustment = Thread.currentThread().getContextClassLoader().getResource("./invoiceAdjustment").getPath();
                File sourceFileInvoiceAdjustment = new File(sourcePathInvoiceAdjustment);
                if (!sourceFileInvoiceAdjustment.exists()) {
                    VirtualFile vfDir = VFS.getChild("content/" + ParamBean.getInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/invoiceAdjustment");
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFileInvoiceAdjustment = new File(vfPath.getPath());
                    if (!sourceFileInvoiceAdjustment.exists()) {
                        throw new BusinessException("embedded jasper report for invoice is missing!");
                    }
                }
                FileUtils.copyDirectory(sourceFileInvoiceAdjustment, destDirInvoiceAdjustment);
            }
            File jasperFile = getJasperTemplateFile(resDir, billingTemplateName, billingAccount.getPaymentMethod(), isInvoiceAdjustment);
            if (!jasperFile.exists()) {
                throw new InvoiceJasperNotFoundException("The jasper file doesn't exist.");
            }
            log.info(String.format("Jasper template used: %s", jasperFile.getCanonicalPath()));

            InputStream reportTemplate = new FileInputStream(jasperFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDocument = db.parse(invoiceXmlFile);
            xmlDocument.getDocumentElement().normalize();
            Node invoiceNode = xmlDocument.getElementsByTagName(INVOICE_TAG_NAME).item(0);

            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            trans.transform(new DOMSource(xmlDocument), new StreamResult(writer));
            log.debug(writer.getBuffer().toString().replaceAll("\n|\r", ""));

            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("/invoice");
            Object result = expr.evaluate(xmlDocument, XPathConstants.NODE);
            Node node = (Node) result;

            JRXmlDataSource dataSource = null;

            if (node != null) {
                dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(invoiceNode).getBytes(StandardCharsets.UTF_8)), "/invoice");
            } else {
                dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(invoiceNode).getBytes(StandardCharsets.UTF_8)), "/invoice");
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
           
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
            log.info("PDF file '{}' produced for invoice {}", pdfFileName, invoice.getInvoiceNumberOrTemporaryNumber());
            if(invoice.getInvoiceNumber() == null){            	
            	 PdfWaterMark.add(pdfFileName, paramBean.getProperty("invoice.pdf.waterMark", "PROFORMA"), null);
            }            
            invoice.setPdfGenerated(true);            
            
        } catch (IOException |JRException | XPathExpressionException | TransformerException | ParserConfigurationException | SAXException e) {
            throw new BusinessException("Failed to generate a PDF file " + pdfFileName, e);
        }		
	}

    /**
     * Delete invoice's PDF file
     * 
     * @param invoice Invoice
     * @return True if file was deleted
     * @throws BusinessException
     */
    public Invoice deleteInvoicePdf(Invoice invoice) throws BusinessException {

        invoice.setPdfGenerated(false);
        invoice = update(invoice);

        String pdfFilename = getFullPdfFilePath(invoice, false);

        File file = new File(pdfFilename);
        if (file.exists()) {
            file.delete();
        }
        return invoice;
    }

	private File getJasperTemplateFile(String resDir, String billingTemplate, PaymentMethodEnum paymentMethod,boolean isInvoiceAdjustment) {
		String pdfDirName = new StringBuilder(resDir).append(File.separator).append(billingTemplate)
				.append(File.separator).append(isInvoiceAdjustment?ADJUSTEMENT_DIR_NAME:PDF_DIR_NAME).toString();
		
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

	public String getNameWoutSequence(String tempDir, Date invoiceDate, String invoiceNumber) {
		return new StringBuilder(tempDir).append(File.separator).append(formatInvoiceDate(invoiceDate)).append("_")
				.append(invoiceNumber).toString();
	}

	public String formatInvoiceDate(Date invoiceDate) {
		DateFormat dateFormat = new SimpleDateFormat(DATE_PATERN);
		return dateFormat.format(invoiceDate);
	}

	@SuppressWarnings("unchecked")
	public void deleteInvoice(Invoice invoice) {
		getEntityManager().createNamedQuery("RatedTransaction.deleteInvoice").setParameter("invoice", invoice)
				.executeUpdate();

		Query queryTrans = getEntityManager()
				.createQuery(
						"update " + RatedTransaction.class.getName()
								+ " set invoice=null,invoiceAgregateF=null,invoiceAgregateR=null,invoiceAgregateT=null where invoice=:invoice");
		queryTrans.setParameter("invoice", invoice);
		queryTrans.executeUpdate();

		Query queryAgregate = getEntityManager().createQuery(
				"from " + InvoiceAgregate.class.getName() + " where invoice=:invoice");

		queryAgregate.setParameter("invoice", invoice);
		List<InvoiceAgregate> invoiceAgregates = (List<InvoiceAgregate>) queryAgregate.getResultList();
		for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {
			getEntityManager().remove(invoiceAgregate);
		}
		getEntityManager().flush();
		
		Query queryInvoices = getEntityManager().createQuery(
				"delete from " + Invoice.class.getName() + " where id=:invoiceId");
		queryInvoices.setParameter("invoiceId", invoice.getId());
		queryInvoices.executeUpdate();
	}



	public String evaluatePrefixElExpression(String prefix, Invoice invoice)
			throws BusinessException {
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

	public void recomputeAggregates(Invoice invoice) throws BusinessException {		
		boolean entreprise = appProvider.isEntreprise();
		int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
		BillingAccount billingAccount=billingAccountService.findById(invoice.getBillingAccount().getId());
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

			taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountWithoutTax()
					.multiply(taxInvoiceAgregate.getTaxPercent()).divide(new BigDecimal("100")));
			// then round the tax
			taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax().setScale(rounding, RoundingMode.HALF_UP));

			taxInvoiceAgregate.setAmountWithTax(taxInvoiceAgregate.getAmountWithoutTax().add(
					taxInvoiceAgregate.getAmountTax()));
		}

		// update the amount with and without tax of all the tax aggregates in
		// each sub category aggregate
		SubCategoryInvoiceAgregate biggestSubCat = null;
		BigDecimal biggestAmount = new BigDecimal("-100000000");

		for (InvoiceAgregate invoiceAgregate : subCategoryInvoiceAgregates) {
			SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;

			if (!entreprise) {
				nonEnterprisePriceWithTax = nonEnterprisePriceWithTax
						.add(subCategoryInvoiceAgregate.getAmountWithTax());
			}

			subCategoryInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax()!=null ? subCategoryInvoiceAgregate.getAmountWithoutTax().setScale(
					rounding, RoundingMode.HALF_UP):BigDecimal.ZERO);

			subCategoryInvoiceAgregate.getCategoryInvoiceAgregate().addAmountWithoutTax(
					subCategoryInvoiceAgregate.getAmountWithoutTax());

			if (subCategoryInvoiceAgregate.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
				biggestAmount = subCategoryInvoiceAgregate.getAmountWithoutTax();
				biggestSubCat = subCategoryInvoiceAgregate;
			}
		}

		for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
			if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
				CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
				invoice.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax().setScale(rounding,
						RoundingMode.HALF_UP));
			}

			if (invoiceAgregate instanceof TaxInvoiceAgregate) {
				TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
				invoice.addAmountTax(taxInvoiceAgregate.getAmountTax().setScale(rounding, RoundingMode.HALF_UP));
			}
		}

		if (invoice.getAmountWithoutTax() != null) {
			invoice.setAmountWithTax(invoice.getAmountWithoutTax().add(invoice.getAmountTax() == null ? BigDecimal.ZERO  : invoice.getAmountTax()));
		}

		if (!entreprise && biggestSubCat != null && !exoneratedFromTaxes) {
			BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
			log.debug("delta={}-{}={}", nonEnterprisePriceWithTax, invoice.getAmountWithTax(), delta);

			biggestSubCat.setAmountWithoutTax(biggestSubCat.getAmountWithoutTax().add(delta)
					.setScale(rounding, RoundingMode.HALF_UP));
			for (Tax tax : biggestSubCat.getSubCategoryTaxes()) {
				TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(tax.getId());
				log.debug("tax3 ht={}", invoiceAgregateT.getAmountWithoutTax());

				invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT.getAmountWithoutTax().add(delta)
						.setScale(rounding, RoundingMode.HALF_UP));
				log.debug("tax4 ht={}", invoiceAgregateT.getAmountWithoutTax());

			}

			CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
			invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR.getAmountWithoutTax().add(delta)
					.setScale(rounding, RoundingMode.HALF_UP));

			invoice.setAmountWithoutTax(invoice.getAmountWithoutTax().add(delta)
					.setScale(rounding, RoundingMode.HALF_UP));
			invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(rounding, RoundingMode.HALF_UP));
		}

		// calculate discounts here
		// no need to create discount aggregates we will use the one from
		// adjustedInvoice

		Object[] object = invoiceAgregateService.findTotalAmountsForDiscountAggregates(getLinkedInvoice(invoice));
		BigDecimal discountAmountWithoutTax = (BigDecimal) object[0];
		BigDecimal discountAmountTax = (BigDecimal) object[1];
		BigDecimal discountAmountWithTax = (BigDecimal) object[2];

		log.debug("discountAmountWithoutTax= {}, discountAmountTax={}, discountAmountWithTax={}", object[0], object[1],
				object[2]);

		invoice.addAmountWithoutTax(discountAmountWithoutTax);
		invoice.addAmountTax(discountAmountTax);
		invoice.addAmountWithTax(discountAmountWithTax);

		// compute net to pay
		BigDecimal netToPay = BigDecimal.ZERO;
		if (entreprise) {
			netToPay = invoice.getAmountWithTax();
		} else {
			BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount()
					.getCustomerAccount().getCode(), invoice.getDueDate());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = invoice.getAmountWithTax().add(balance);
		}

		invoice.setNetToPay(netToPay);
	}

	public void recomputeSubCategoryAggregate(Invoice invoice) {
		int rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();

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

			taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountWithoutTax()
					.multiply(taxInvoiceAgregate.getTaxPercent()).divide(new BigDecimal("100")));
			// then round the tax
			taxInvoiceAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax().setScale(rounding, RoundingMode.HALF_UP));

			taxInvoiceAgregate.setAmountWithTax(taxInvoiceAgregate.getAmountWithoutTax().add(
					taxInvoiceAgregate.getAmountTax()));
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<Invoice> findInvoicesByType(InvoiceType invoiceType, BillingAccount ba) {
		List<Invoice> result = new ArrayList<Invoice>();
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null);
		qb.addCriterionEntity("billingAccount", ba);
		qb.addCriterionEntity("invoiceType", invoiceType);
		try {
			result =  (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {			
		}
		return result;
	}
	
	private String getBillingRunPath(Invoice invoice){
	    BillingRun billingRun = invoice.getBillingRun();
		ParamBean paramBean = ParamBean.getInstance();
		String providerDir = paramBean.getProperty("providers.rootDir", "./opencelldata");
		String sep = File.separator;
		String brPath = providerDir + sep + appProvider.getCode() + sep + "invoices" + sep + "xml" + sep + 
				(billingRun == null ? DateUtils.formatDateWithPattern(invoice.getInvoiceDate(), paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss")) : billingRun.getId());
		return brPath;
	}
		
    public String getInvoiceXMLFilename(Invoice invoice) {

        boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());
        if (isInvoiceAdjustment) {
            return paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + invoice.getInvoiceNumber() + ".xml";
        
        } else {
            return (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()) + ".xml";
        }
    }

    public String getInvoiceAdjustmentXMLFilename(Invoice invoice) {
       
        return paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()) + ".xml";
    }
    
    /**
     * Get a full path to an invoice's XML file
     * 
     * @param invoice Invoice
     * @param createDirs Should missing directories be created
     * @return Absolute path to an XML file
     */
    public String getFullXmlFilePath(Invoice invoice, boolean createDirs) {
        
        String path = getBillingRunPath(invoice);
        if (createDirs){
            (new File(path)).mkdirs();
        }
        return path + File.separator + getInvoiceXMLFilename(invoice);
    }
    
    /**
     * Get a full path to an invoice's adjustment XML file
     * 
     * @param invoice Invoice
     * @return Absolute path to an XML file
     */
    public String getFullAdjustmentXmlFilePath(Invoice invoice) {
        return getBillingRunPath(invoice) + File.separator + getInvoiceAdjustmentXMLFilename(invoice);
    }

    /**
     * Get a full path to an invoice's PDF file
     * 
     * @param invoice Invoice
     * @param createDirs Should missing directories be created
     * @return Absolute path to a PDF file
     */
    public String getFullPdfFilePath(Invoice invoice, boolean createDirs) {

        String meveoDir = paramBean.getProperty("providers.rootDir", "./opencelldata/") + File.separator + appProvider.getCode() + File.separator;

        String pdfDirectory = meveoDir + "invoices" + File.separator + "pdf" + File.separator;
        if (createDirs) {
            (new File(pdfDirectory)).mkdirs();
        }
        
        String pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(),
            (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber())) + ".pdf";
        boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());
        if (isInvoiceAdjustment) {
            pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(),
                paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + invoice.getInvoiceNumber()) + ".pdf";
        }

        return pdfFileName;
    }
	
	/**
	 *  if the sequence not found on cust.seller, we try in seller.parent (until seller.parent=null)
	 *  
	 * @param seller
	 * @param cfName
	 * @param date
	 * @param invoiceType
	 * @return
	 */
	private Seller chooseSeller(Seller seller,String cfName,Date date,InvoiceType invoiceType){		
		if(seller.getSeller() == null){
			return seller;
		}
		Object currentValObj = customFieldInstanceService.getCFValue(seller, cfName,date);
		if(currentValObj != null){		
			return seller;
		}
		if(invoiceType.getSellerSequence() != null && invoiceType.isContainsSellerSequence(seller)){
			return  seller;
		}
		
		return chooseSeller(seller.getSeller(), cfName, date, invoiceType);
		
		
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceInvoiceXmlInNewTransaction(Long invoiceId) throws BusinessException {
        Invoice invoice = findById(invoiceId);
        produceInvoiceXml(invoice);
    }
	
    /**
     * Produce invoice's XML file
     * 
     * @param invoice Invoice
     * @return XML File
     * @throws BusinessException
     */
    public File produceInvoiceXml(Invoice invoice) throws BusinessException {

        File xmlFile = xmlInvoiceCreator.createXMLInvoice(invoice, false);
        return xmlFile;
    }

    /**
     * Delete invoice's XML file
     * 
     * @param invoice Invoice
     * @return True if file was deleted
     */
    public boolean deleteInvoiceXml(Invoice invoice) {

        String xmlFilename = getFullXmlFilePath(invoice, false);

        File file = new File(xmlFilename);
        if (file.exists()) {
            return file.delete();
        } else {
            return true;
        }
    }

    /**
     * Check if invoice's XML file exists
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
     * Check if invoice's adjustment XML file exists
     * 
     * @param invoice Invoice
     * @return True if invoice's adjustment XML file exists
     */
    public boolean isInvoiceAdjustmentXmlAlreadyGenerated(Invoice invoice) {
        String xmlFileName = getFullAdjustmentXmlFilePath(invoice);
        File xmlFile = new File(xmlFileName);
        return !xmlFile.exists();
    }

    /**
     * Retrieve invoice's XML file contents as a string
     * 
     * @param invoice Invoice
     * @return Invoice's XML file contents as a string
     * @throws BusinessException
     * @throws FileNotFoundException
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
     * Check if invoice's PDF file exists
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
     * Retrieve invoice's PDF file contents as a byte array
     * 
     * @param invoice Invoice
     * @return Invoice's PDF file contents as a byte array
     * @throws BusinessException
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
     * Generate XML and PDF files for Invoice
     * @param invoice Invoice
     * @throws BusinessException
     */
	public Invoice generateXmlAndPdfInvoice(Invoice invoice) throws BusinessException {
		
	    produceInvoiceXml(invoice);		
		invoice = produceInvoicePdf(invoice);		
		return invoice;
	}
	
	public Invoice getLinkedInvoice(Invoice invoice){
		if(invoice == null || invoice.getLinkedInvoices() == null || invoice.getLinkedInvoices().isEmpty()){
			return null;
		}
		return invoice.getLinkedInvoices().iterator().next();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoicesWithAccountOperation(BillingAccount billingAccount) {
		try {
			QueryBuilder qb = new QueryBuilder("SELECT i FROM " + Invoice.class.getName() + " i");
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
	 * Create RatedTransaction and generate invoice for the billingAccount
	 * 
	 * @param billingAccount
	 * @param invoiceDate
	 * @param lastTransactionDate
	 * @param ratedTxFilter
	 * @param orderNumber Order number associated to subscription
	 * @param isDraft Is it a draft
	 * @param produceXml Produce invoice XML file
	 * @param producePdf Produce invoice PDF file
	 * @param generateAO Generate AOs
	 * @return
	 * @throws BusinessException
	 * @throws ImportInvoiceException 
	 * @throws InvoiceExistException 
	 */
	public Invoice generateInvoice(BillingAccount billingAccount,Date invoiceDate, Date lastTransactionDate,Filter ratedTxFilter,String orderNumber,boolean isDraft, boolean produceXml, boolean producePdf, boolean generateAO) throws BusinessException, InvoiceExistException, ImportInvoiceException {

		if (StringUtils.isBlank(billingAccount)) {
			throw new BusinessException("billingAccount is null");
		}
		if (StringUtils.isBlank(invoiceDate)) {
			throw new BusinessException("invoicingDate is null");
		}

		if (ratedTxFilter == null && StringUtils.isBlank(lastTransactionDate) && StringUtils.isBlank(orderNumber)) {
			throw new BusinessException("lastTransactionDate or filter or orderNumber is null");
		}
		
        if (billingAccount.getBillingRun() != null
                && (billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.NEW)
                 || billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.PREVALIDATED) 
                 || billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.POSTVALIDATED))) {

			throw new BusinessException("The billingAccount is already in an billing run with status " + billingAccount.getBillingRun().getStatus());
		}
		
		ratedTransactionService.createRatedTransaction(billingAccount.getId(), invoiceDate);				
		if(ratedTxFilter == null && StringUtils.isBlank(orderNumber)){			
			if( ! ratedTransactionService.isBillingAccountBillable(billingAccount, lastTransactionDate)){
				throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));		
			}
		}
		if(!StringUtils.isBlank(orderNumber)){			
			if( ! ratedTransactionService.isBillingAccountBillable(billingAccount, orderNumber)){
				throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));		
			}
		}
		
		Invoice invoice = createAgregatesAndInvoice(billingAccount,null,ratedTxFilter, orderNumber,invoiceDate,lastTransactionDate);		
		if(!isDraft){
			invoice.setInvoiceNumber(getInvoiceNumber(invoice));
		}						
		
		if (produceXml){
		    produceInvoiceXml(invoice);
		}
		
		if (producePdf){
		    produceInvoicePdfNoUpdate(invoice);
		}
		
		if (generateAO){
		    recordedInvoiceService.generateRecordedInvoice(invoice);
		}
		
		update(invoice);						
		
		return invoice;
	}
	
	@SuppressWarnings("unchecked")
    public void cancelInvoice(Invoice invoice) throws BusinessException {		
		if(invoice.getInvoiceNumber() != null){
			throw new BusinessException("Can't cancel an invoice validated");
		}
		if(invoice.getRecordedInvoice() != null){
			throw new BusinessException("Can't cancel an invoice that present in AR");
		}
		Query queryTrans = getEntityManager().createQuery("update "+ RatedTransaction.class.getName()+ " set invoice=null,invoiceAgregateF=null,invoiceAgregateR=null,invoiceAgregateT=null,status=:status where invoice=:invoice");
		queryTrans.setParameter("invoice", invoice);
		queryTrans.setParameter("status", RatedTransactionStatusEnum.OPEN);
		queryTrans.executeUpdate();		
		Query queryAgregate = getEntityManager().createQuery("from "+ InvoiceAgregate.class.getName()+" where invoice=:invoice");
		queryAgregate.setParameter("invoice", invoice);
		List<InvoiceAgregate> invoiceAgregates=(List<InvoiceAgregate>)queryAgregate.getResultList();
		for(InvoiceAgregate invoiceAgregate:invoiceAgregates){			
			if(invoiceAgregate instanceof SubCategoryInvoiceAgregate){
				((SubCategoryInvoiceAgregate)invoiceAgregate).setSubCategoryTaxes(null);
			}
		}		
		invoice.setOrders(null);		
		Query dropAgregats = getEntityManager().createQuery("delete from " + InvoiceAgregate.class.getName() + " where invoice=:invoice");
		dropAgregats.setParameter("invoice",invoice);
		dropAgregats.executeUpdate();		
		getEntityManager().flush();				
		Query queryInvoices = getEntityManager().createQuery("delete from " + Invoice.class.getName() + " where id=:id");
		queryInvoices.setParameter("id", invoice.getId());
		queryInvoices.executeUpdate();
		log.debug("cancel invoice:{} done",invoice.getTemporaryInvoiceNumber());
	}
	
	private Integer evaluateIntegerExpression(String expression, BillingAccount billingAccount, Invoice invoice) throws BusinessException {
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

		Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Integer.class);
		try {
			result = (Integer) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to Integer but " + res);
		}
		return result;
	}

    /**
     * Determine an invoice template to use. Rule for selecting an invoiceTemplate is: InvoiceType > BillingCycle > default
     * 
     * @param billingCycle Billing cycle
     * @param invoiceType Invoice type
     * @return Invoice template name
     */
    public static String getInvoiceTemplateName(BillingCycle billingCycle, InvoiceType invoiceType) {

        String billingTemplateName = "default";
        if (invoiceType != null && !StringUtils.isBlank(invoiceType.getBillingTemplateName())) {
            billingTemplateName = invoiceType.getBillingTemplateName();
        
        } else if (billingCycle != null && billingCycle.getInvoiceType() != null && !StringUtils.isBlank(billingCycle.getInvoiceType().getBillingTemplateName())) {
            billingTemplateName = billingCycle.getInvoiceType().getBillingTemplateName();

        } else if (billingCycle != null && !StringUtils.isBlank(billingCycle.getBillingTemplateName())) {
            billingTemplateName = billingCycle.getBillingTemplateName();
        }
        return billingTemplateName;
    }
}