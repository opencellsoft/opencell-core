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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.FileUtils;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
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
import org.meveo.model.admin.User;
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
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
	private ProviderService providerService;
	
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
	


	private String PDF_DIR_NAME = "pdf";
	private String ADJUSTEMENT_DIR_NAME = "invoiceAdjustmentPdf";
	private String INVOICE_TEMPLATE_FILENAME = "invoice.jasper";
	private String DATE_PATERN = "yyyy.MM.dd";

	public Invoice getInvoiceByNumber(String invoiceNumber, String providerCode) throws BusinessException {
		try {
			Query q = getEntityManager().createQuery(
					"from Invoice where invoiceNumber = :invoiceNumber and provider=:provider");
			q.setParameter("invoiceNumber", invoiceNumber).setParameter("provider",
					providerService.findByCode(providerCode));
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


	public Invoice getInvoice(String invoiceNumber, CustomerAccount customerAccount) throws BusinessException {
		return getInvoice(getEntityManager(), invoiceNumber, customerAccount);
	}

	public Invoice getInvoice(EntityManager em, String invoiceNumber, CustomerAccount customerAccount)
			throws BusinessException {
		try {
			Query q = em
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

	public Invoice getInvoiceByNumber(String invoiceNumber,User user) throws BusinessException {
		return getInvoiceByNumber(invoiceNumber, invoiceTypeService.getDefaultCommertial(user));
	}

	public Invoice findByInvoiceNumberAndType(String invoiceNumber, InvoiceType invoiceType, Provider provider)
			throws BusinessException {
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null, provider);
		qb.addCriterion("i.invoiceNumber", "=", invoiceNumber, true);
		qb.addCriterionEntity("i.invoiceType", invoiceType);
		try {
			return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.info("Invoice with invoice number {} was not found for provider {}. Returning null.", invoiceNumber,provider.getCode());
			return null;
		} catch (NonUniqueResultException e) {
			log.info("Multiple invoices with invoice number {} was found for provider {}. Returning null.", invoiceNumber,provider.getCode());
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public Invoice getInvoiceByNumber(String invoiceNumber, InvoiceType invoiceType) throws BusinessException {
		return findByInvoiceNumberAndType(invoiceNumber, invoiceType, invoiceType.getProvider());
	}

	public List<Invoice> getInvoices(BillingRun billingRun) throws BusinessException {
		return getInvoices(getEntityManager(), billingRun);
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices(EntityManager em, BillingRun billingRun) throws BusinessException {
		try {
			Query q = em.createQuery("from Invoice where billingRun = :billingRun");
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
		invoice.setInvoiceNumber(getInvoiceNumber(invoice, null));
	}

	public void setInvoiceNumber(Invoice invoice, User currentUser) throws BusinessException {
		invoice.setInvoiceNumber(getInvoiceNumber(invoice, currentUser));
	}

	public String getInvoiceNumber(Invoice invoice) throws BusinessException {
		return getInvoiceNumber(invoice, null);
	}

	public String getInvoiceNumber(Invoice invoice, User currentUser) throws BusinessException {		
		String cfName = "INVOICE_SEQUENCE_"+invoice.getInvoiceType().getCode().toUpperCase();			
		if(invoiceTypeService.getAdjustementCode().equals(invoice.getInvoiceType().getCode())){
			cfName = "INVOICE_ADJUSTMENT_SEQUENCE";
		}
		if(invoiceTypeService.getCommercialCode().equals(invoice.getInvoiceType().getCode())){
			cfName = "INVOICE_SEQUENCE";
		}
		Customer cust = customerService.refreshOrRetrieve(invoice.getBillingAccount().getCustomerAccount().getCustomer());
		Seller seller = chooseSeller(cust.getSeller(), cfName, invoice.getInvoiceDate(), invoice.getInvoiceType(), currentUser);

        Sequence sequence = getSequence(invoice, seller,cfName,1,true,currentUser);
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

	
	public synchronized Sequence getSequence(Invoice invoice ,Seller seller,String cfName,int step,boolean increment,User currentUser)throws BusinessException{			
		Long currentNbFromCF = null;				
		Object currentValObj = customFieldInstanceService.getCFValue(seller, cfName, invoice.getInvoiceDate(), currentUser);
		if(currentValObj != null){			
			currentNbFromCF = (Long)currentValObj;
			if(increment){
				currentNbFromCF = currentNbFromCF + step;
				 customFieldInstanceService.setCFValue(seller, cfName,currentNbFromCF, invoice.getInvoiceDate(), currentUser);
				 customFieldInstanceService.commit();
			}
		}else{
			currentValObj = customFieldInstanceService.getCFValue(seller.getProvider(), cfName, invoice.getInvoiceDate(), currentUser);
			if(currentValObj != null){
				currentNbFromCF = (Long)currentValObj;
				if(increment){
					currentNbFromCF = currentNbFromCF + step;
					 customFieldInstanceService.setCFValue(seller.getProvider(), cfName,currentNbFromCF, invoice.getInvoiceDate(), currentUser);
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
				invoiceTypeService.update(invoiceType,currentUser);
			}
		}else{			
			if(invoiceType.getSequence() != null){				
				sequence =  invoiceType.getSequence();
				if(increment && currentNbFromCF == null){					
					sequence.setCurrentInvoiceNb((sequence.getCurrentInvoiceNb() == null?0L:sequence.getCurrentInvoiceNb()) +step);
					invoiceType.setSequence(sequence);
					invoiceTypeService.update(invoiceType,currentUser);
				}
			}
		}
		if(sequence == null){			
			sequence = new Sequence();
			sequence.setCurrentInvoiceNb(1L);
			sequence.setSequenceSize(9);
			sequence.setPrefixEL("");
			invoiceType.setSequence(sequence);
			invoiceTypeService.update(invoiceType,currentUser);			
		}
		if(currentNbFromCF != null){			
			sequence.setCurrentInvoiceNb(currentNbFromCF);
		}	
		log.debug("getSequence:"+sequence);
		invoiceTypeService.commit();
		
		return sequence;		
	}
	
	
	public List<Invoice> getValidatedInvoicesWithNoPdf(BillingRun br, Provider provider) {
		return getValidatedInvoicesWithNoPdf(getEntityManager(), br, provider);
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getValidatedInvoicesWithNoPdf(EntityManager em, BillingRun br, Provider provider) {
		try {
			QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
			qb.addCriterionEntity("i.billingRun.status", BillingRunStatusEnum.VALIDATED);
			qb.addCriterionEntity("provider", provider);
			qb.addSql("i.pdf is null");

			if (br != null) {
				qb.addCriterionEntity("i.billingRun", br);
			}
			return (List<Invoice>) qb.getQuery(em).getResultList();
		} catch (Exception ex) {
			log.error("failed to get validated invoices with no pdf", ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getInvoiceIdsWithNoAccountOperation(BillingRun br, Provider currentProvider) {
		try {
			QueryBuilder qb = new QueryBuilder("SELECT i.id FROM " + Invoice.class.getName() + " i");
			qb.addCriterionEntity("i.provider", currentProvider);			
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
	public Invoice createAgregatesAndInvoice(BillingAccount billingAccount, Long billingRunId,Filter ratedTransactionFilter,String orderNumber,
			Date invoiceDate,Date lastTransactionDate, User currentUser)
			throws BusinessException {
		Invoice invoice =null;
		log.debug("createAgregatesAndInvoice billingAccount={} , billingRunId={} , ratedTransactionFilter={} , orderNumber{}, lastTransactionDate={} ,invoiceDate={} ", 
				billingAccount,billingRunId,ratedTransactionFilter,orderNumber,lastTransactionDate,invoiceDate);
		log.debug("createAgregatesAndInvoice tx status={}", txReg.getTransactionStatus());

		EntityManager em = getEntityManager();
		BillingRun billingRun=null;
		if(billingRunId!=null){
			billingRun = em.find(BillingRun.class, billingRunId);
			em.refresh(billingRun);
		} else {
			if(invoiceDate==null){
				throw new BusinessException("invoiceDate must be set if billingRun is null");	
			}
			if(StringUtils.isBlank(lastTransactionDate) && 
					StringUtils.isBlank(orderNumber) && 
					ratedTransactionFilter == null){
				throw new BusinessException("lastTransactionDate or orderNumber or ratedTransactionFilter must be set if billingRun is null");
			}
		}
		
		if(billingAccount.getInvoicingThreshold() != null){
			BigDecimal invoiceAmount  = billingAccountService.computeBaInvoiceAmount(billingAccount, billingRun==null?lastTransactionDate:billingRun.getLastTransactionDate());
			if(invoiceAmount == null){
				throw new BusinessException("Cant compute invoice amount");
			}			
			if (billingAccount.getInvoicingThreshold().compareTo(invoiceAmount) > 0) {
				throw new BusinessException("Invoice amount below the threshold");	
			}
		}
		
		try {
			billingAccount = em.find(billingAccount.getClass(), billingAccount.getId());
			em.refresh(billingAccount);
			currentUser = em.find(User.class, currentUser.getId());
			em.refresh(currentUser);

			Long startDate = System.currentTimeMillis();
			BillingCycle billingCycle = billingRun==null?billingAccount.getBillingCycle():billingRun.getBillingCycle();
			if (billingCycle == null) {
				billingCycle = billingAccount.getBillingCycle();
			}
			if(billingCycle == null){
				throw new BusinessException("Cant find the billing cycle");
			}
			InvoiceType invoiceType = billingCycle.getInvoiceType();
			if(invoiceType == null){
				invoiceType = invoiceTypeService.getDefaultCommertial(currentUser);
			}
			log.debug("invoiceType {}",invoiceType.getCode());
			invoice = new Invoice();
			invoice.setInvoiceType(invoiceType);
			invoice.setBillingAccount(billingAccount);
			invoice.setBillingRun(billingRun);
			invoice.setAuditable(billingRun==null?billingAccount.getAuditable():billingRun.getAuditable());
			invoice.setProvider(currentUser.getProvider());
			
			if(invoiceDate!=null){
				invoice.setInvoiceDate(invoiceDate);
			}else {
				invoice.setInvoiceDate(billingRun.getInvoiceDate());
			}

			Integer delay = billingCycle.getDueDateDelay();
			Date dueDate = invoiceDate;
			if (delay != null) {
				dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
			}
			invoice.setDueDate(dueDate);

			PaymentMethodEnum paymentMethod = billingAccount.getPaymentMethod();
			if (paymentMethod == null) {
				paymentMethod = billingAccount.getCustomerAccount().getPaymentMethod();
			}
			invoice.setPaymentMethod(paymentMethod);
			invoice.setProvider(currentUser.getProvider());

			em.persist(invoice);

			// create(invoice, currentUser, currentUser.getProvider());
			log.debug("created invoice entity with id={},  tx status={}, em open={}", invoice.getId(),
					txReg.getTransactionStatus(), em.isOpen());
			ratedTransactionService.createInvoiceAndAgregates(billingAccount, invoice,
					ratedTransactionFilter,orderNumber,billingRun==null?lastTransactionDate:billingRun.getLastTransactionDate(), currentUser);
			log.debug("created aggregates tx status={}, em open={}", txReg.getTransactionStatus(), em.isOpen());
			em.joinTransaction();

			// Note that rated transactions get updated in
			// ratedTransactionservice in case of Filter or orderNumber not empty
            if (ratedTransactionFilter == null && StringUtils.isBlank(orderNumber)) {
                Query query = em.createNamedQuery("RatedTransaction.updateInvoiced" + (billingRun == null ? "NoBR" : "")).setParameter("billingAccount", billingAccount)
                    .setParameter("lastTransactionDate", billingRun == null ? lastTransactionDate : billingRun.getLastTransactionDate()).setParameter("invoice", invoice);
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
					orders.add(orderService.findByCodeOrExternalId(orderNum, invoice.getProvider()));
				}
				invoice.setOrders(orders);
			}			
			Long endDate = System.currentTimeMillis();

			log.info("createAgregatesAndInvoice BR_ID=" +( billingRun==null?"null":billingRun.getId() )+ ", BA_ID=" + billingAccount.getId()
					+ ", Time en ms=" + (endDate - startDate));
		} catch (BusinessException e) {
			log.error("Error for BA=" + billingAccount.getCode() + " : ", e);
			if(billingRun != null){
				RejectedBillingAccount rejectedBA = new RejectedBillingAccount(billingAccount, billingRun, e.getMessage());
				rejectedBillingAccountService.create(rejectedBA, currentUser);
			}
			throw e;
		}		
		return invoice;
	}
	
    public Invoice createAgregatesAndInvoiceVirtual(List<RatedTransaction> ratedTransactions, BillingAccount billingAccount, InvoiceType invoiceType, User currentUser)
            throws BusinessException {

        if (invoiceType == null) {
            invoiceType = invoiceTypeService.getDefaultCommertial(currentUser);
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
        invoice.setProvider(currentUser.getProvider());

        ratedTransactionService.createInvoiceAndAgregates(billingAccount, invoice, null, ratedTransactions, null, null, currentUser, false, true);

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
	public void producePdfInNewTransaction(Long invoiceId, User currentUser) throws BusinessException {
	    producePdf(invoiceId, currentUser);
	}

   public void producePdf(Long invoiceId, User currentUser) throws BusinessException {
       Invoice invoice = findById(invoiceId);
       producePdf(invoice, false, currentUser);
   }
   
   public void producePdf(Invoice invoice, boolean isVirtual, User currentUser) throws BusinessException  {
        
        String meveoDir = paramBean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + currentUser.getProvider().getCode() + File.separator;
        String invoiceXmlFileName = getFullXmlFilePath(invoice);
        Map<String, Object> parameters = pDFParametersConstruction.constructParameters(invoice, currentUser);
       
		log.info("PDFInvoiceGenerationJob is invoice key exists="
				+ ((parameters != null) ? parameters.containsKey(PdfGeneratorConstants.INVOICE) + ""
						: "parameters is null"));

        String pdfDirectory = meveoDir + "invoices" + File.separator + "pdf" + File.separator;
        (new File(pdfDirectory)).mkdirs();

        String INVOICE_TAG_NAME = "invoice";

        boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());

        File invoiceXmlFile = new File(invoiceXmlFileName);
        if (!invoiceXmlFile.exists()) {
            throw new InvoiceXmlNotFoundException("The xml invoice file doesn't exist.");
        }
        BillingAccount billingAccount = invoice.getBillingAccount();
        BillingCycle billingCycle = null;
        if (billingAccount != null && billingAccount.getBillingCycle() != null) {
            billingCycle = billingAccount.getBillingCycle();
        }
        String billingTemplate = (billingCycle != null && billingCycle.getBillingTemplateName() != null) ? billingCycle.getBillingTemplateName() : "default";
        String resDir = meveoDir + "jasper";

        String pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(),
            (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()))
                + ".pdf";
        if (isInvoiceAdjustment) {
            pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(),
                paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + invoice.getInvoiceNumber())
                    + ".pdf";
        }

        try {
            File destDir = new File(resDir + File.separator + billingTemplate + File.separator + "pdf");
            if (!destDir.exists()) {
                destDir.mkdirs();
                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath();
                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    VirtualFile vfDir = VFS.getChild("content/" + ParamBean.getInstance().getProperty("meveo.moduleName", "meveo") + ".war/WEB-INF/classes/jasper");
                    log.info("default jaspers path :" + vfDir.getPathName());
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFile = new File(vfPath.getPath());
                    if (!sourceFile.exists()) {
                        throw new BusinessException("embedded jasper report for invoice is missing!");
                    }
                }
                FileUtils.copyDirectory(sourceFile, destDir);
            }
            File destDirInvoiceAdjustment = new File(resDir + File.separator + billingTemplate + File.separator + "invoiceAdjustmentPdf");
            if (!destDirInvoiceAdjustment.exists()) {
                destDirInvoiceAdjustment.mkdirs();
                String sourcePathInvoiceAdjustment = Thread.currentThread().getContextClassLoader().getResource("./invoiceAdjustment").getPath();
                File sourceFileInvoiceAdjustment = new File(sourcePathInvoiceAdjustment);
                if (!sourceFileInvoiceAdjustment.exists()) {
                    VirtualFile vfDir = VFS.getChild("content/" + ParamBean.getInstance().getProperty("meveo.moduleName", "meveo") + ".war/WEB-INF/classes/invoiceAdjustment");
                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
                    sourceFileInvoiceAdjustment = new File(vfPath.getPath());
                    if (!sourceFileInvoiceAdjustment.exists()) {
                        throw new BusinessException("embedded jasper report for invoice is missing!");
                    }
                }
                FileUtils.copyDirectory(sourceFileInvoiceAdjustment, destDirInvoiceAdjustment);
            }
            File jasperFile = getJasperTemplateFile(resDir, billingTemplate, billingAccount.getPaymentMethod(), isInvoiceAdjustment);
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
            log.info(String.format("PDF file '%s' produced", pdfFileName));
            if(invoice.getInvoiceNumber() == null){            	
            	 PdfWaterMark.add(pdfFileName, paramBean.getProperty("invoice.pdf.waterMark", "PROFORMA"), null);
            }            

        } catch (IOException |JRException | XPathExpressionException | TransformerException | ParserConfigurationException | SAXException e) {
            throw new BusinessException("Failed to generate a PDF file " + pdfFileName, e);
        }

		FileInputStream fileInputStream = null;
		try {
			File file = new File(pdfFileName);
			long fileSize = file.length();
			if (fileSize > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("File is too big to put it to buffer in memory.");
			}
			byte[] fileBytes = new byte[(int) file.length()];
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(fileBytes);
			
			invoice.setPdf(fileBytes);	
            if (!isVirtual) {
                update(invoice, currentUser);
            }
			log.debug("invoice.setPdf update ok");
		} catch (Exception e) {
			log.error("Error saving file to DB as blob. {}", e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					log.error("Error closing file input stream.");
				}
			}
		}
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

	public void recomputeAggregates(Invoice invoice, User currentUser) throws BusinessException {		
		boolean entreprise = invoice.getProvider().isEntreprise();
		int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();
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
					.getCustomerAccount().getCode(), invoice.getDueDate(), invoice.getProvider());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = invoice.getAmountWithTax().add(balance);
		}

		invoice.setNetToPay(netToPay);
	}

	public void recomputeSubCategoryAggregate(Invoice invoice, User currentUser) {
		int rounding = invoice.getBillingAccount().getProvider().getRounding() == null ? 2 : invoice
				.getBillingAccount().getProvider().getRounding();

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
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null, invoiceType.getProvider());
		qb.addCriterionEntity("billingAccount", ba);
		qb.addCriterionEntity("invoiceType", invoiceType);
		try {
			result =  (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {			
		}
		return result;
	}
	
	public String getBillingRunPath(BillingRun billingRun,Date invoiceCreation,String providerCode){
		ParamBean paramBean = ParamBean.getInstance();
		String providerDir = paramBean.getProperty("providers.rootDir", "/tmp/meveo");
		String sep = File.separator;
		String brPath = providerDir + sep + providerCode + sep + "invoices" + sep + "xml" + sep + 
				(billingRun == null ? DateUtils.formatDateWithPattern(invoiceCreation, paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss")) : billingRun.getId());
		return brPath;
	}
	
	public String getBillingRunPath(Invoice invoice){
	    return getBillingRunPath(invoice.getBillingRun(), invoice.getInvoiceDate(), invoice.getProvider().getCode());
	}
	
    public String getInvoiceXMLFilename(Invoice invoice) {

        boolean isInvoiceAdjustment = invoice.getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode());
        if (isInvoiceAdjustment) {
            return paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + invoice.getInvoiceNumber() + ".xml";
        
        } else {
            return (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()) + ".xml";
        }
    }
        
    public String getFullXmlFilePath(Invoice invoice) {
        return getBillingRunPath(invoice) + File.separator + getInvoiceXMLFilename(invoice);
    }
	
	/**
	 *  if the sequence not found on cust.seller, we try in seller.parent (until seller.parent=null)
	 *  
	 * @param seller
	 * @param cfName
	 * @param date
	 * @param invoiceType
	 * @param currentUser
	 * @return
	 */
	private Seller chooseSeller(Seller seller,String cfName,Date date,InvoiceType invoiceType,User currentUser){		
		if(seller.getSeller() == null){
			return seller;
		}
		Object currentValObj = customFieldInstanceService.getCFValue(seller, cfName,date, currentUser);
		if(currentValObj != null){		
			return seller;
		}
		if(invoiceType.getSellerSequence() != null && invoiceType.isContainsSellerSequence(seller)){
			return  seller;
		}
		
		return chooseSeller(seller.getSeller(), cfName, date, invoiceType, currentUser);
		
		
	}
	
	public String getXMLInvoice(Invoice invoice, String invoiceNumber, User currentUser, boolean refreshInvoice) throws BusinessException, FileNotFoundException {

        File xmlFile = null;
        if(refreshInvoice){
        	xmlFile = xmlInvoiceCreator.createXMLInvoice(invoice.getId(),currentUser);
        } else {
        	xmlFile = xmlInvoiceCreator.createXMLInvoice(invoice, false,currentUser);
        }

        Scanner scanner = new Scanner(xmlFile);
        String xmlContent = scanner.useDelimiter("\\Z").next();
        scanner.close();
        log.debug("getXMLInvoice  invoiceNumber:{} done.", invoiceNumber);

        return xmlContent;
    }


	public byte[] generatePdfInvoice(Invoice invoice, String invoiceNumber, User currentUser) throws BusinessException {
		if (invoice.getPdf() == null) {
			producePdf(invoice.getId(), currentUser);
		}
		log.debug("getXMLInvoice invoiceNumber:{} done.", invoiceNumber);		
		return invoice.getPdf();
	}
	
	public void generateXmlAndPdfInvoice(Invoice invoice, User currentUser) throws Exception {
		getXMLInvoice(invoice, invoice.getInvoiceNumber(), currentUser, false);
		generatePdfInvoice(invoice, invoice.getInvoiceNumber(), currentUser);
	}
	
	public Invoice getLinkedInvoice(Invoice invoice){
		if(invoice == null || invoice.getLinkedInvoices() == null || invoice.getLinkedInvoices().isEmpty()){
			return null;
		}
		return invoice.getLinkedInvoices().iterator().next();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoicesWithAccountOperation(BillingAccount billingAccount, Provider currentProvider) {
		try {
			QueryBuilder qb = new QueryBuilder("SELECT i FROM " + Invoice.class.getName() + " i");
			qb.addCriterionEntity("i.provider", currentProvider);	 
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
	 * @param currentUser
	 * @return
	 * @throws BusinessException
	 */
	public Invoice generateInvoice(BillingAccount billingAccount,Date invoiceDate, Date lastTransactionDate,Filter ratedTxFilter,String orderNumber,boolean isDraft,User currentUser ) throws BusinessException {

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
		
		ratedTransactionService.createRatedTransaction(billingAccount.getId(), currentUser, invoiceDate);				
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
		
		Invoice invoice = createAgregatesAndInvoice(billingAccount,null,ratedTxFilter, orderNumber,invoiceDate,lastTransactionDate,currentUser);		
		if(!isDraft){
			invoice.setInvoiceNumber(getInvoiceNumber(invoice, currentUser));
		}
		invoice.setPdf(null);							
		update(invoice, currentUser);						
		
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
}
