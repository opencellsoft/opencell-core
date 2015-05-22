/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.InvoiceXmlNotFoundException;
import org.meveo.admin.job.PdfGeneratorConstants;
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
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Stateless
public class InvoiceService extends PersistenceService<Invoice> {

	@Inject
	private ProviderService providerService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private RejectedBillingAccountService rejectedBillingAccountService;
	
	private String PDF_DIR_NAME = "pdf";
	private  String INVOICE_TEMPLATE_FILENAME = "invoice.jasper";
	private  String DATE_PATERN = "yyyy.MM.dd";


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

	public Invoice getInvoiceByNumber(String invoiceNumber) throws BusinessException {
		try {
			Query q = getEntityManager().createQuery("from Invoice where invoiceNumber = :invoiceNumber");
			q.setParameter("invoiceNumber", invoiceNumber);
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log.info("Invoice with invoice number #0 was not found. Returning null.", invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log.info("Multiple invoices with invoice number #0 was found. Returning null.", invoiceNumber);
			return null;
		}
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
	public List<Invoice> getInvoices(BillingAccount billingAccount, String invoiceType) throws BusinessException {
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

	public void setInvoiceNumber(Invoice invoice) {
		invoice.setInvoiceNumber(getInvoiceNumber(invoice, null));
	}

	public void setInvoiceNumber(Invoice invoice, User currentUser) {
		invoice.setInvoiceNumber(getInvoiceNumber(invoice, currentUser));
	}

	public String getInvoiceNumber(Invoice invoice) {
		return getInvoiceNumber(invoice, null);
	}

	public String getInvoiceNumber(Invoice invoice, User currentUser) {
		Seller seller = invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		String prefix = seller.getInvoicePrefix();
        
		if (prefix == null) {
			prefix = seller.getProvider().getInvoicePrefix();
		}
		if (prefix == null) {
			prefix = "";
		}
		
		if(prefix!=null && !StringUtils.isBlank(prefix)){
			  if(prefix.indexOf("%")>=0){
	              int startIndex=prefix.indexOf("%")+1;
	              int endIndex=prefix.indexOf("%",startIndex);
	              if(endIndex>0){
	                String datePattern=prefix.substring(startIndex,endIndex); 
	                String invioceDate=DateUtils.formatDateWithPattern(new Date(), datePattern);
	                prefix=prefix.replace("%"+datePattern+"%", invioceDate);
	              }       
	             } 
		    }
		if (currentUser != null) {
			seller.updateAudit(currentUser);
		} else {
			seller.updateAudit(seller.getAuditable().getCreator());
		}

		long nextInvoiceNb = getNextValue(seller, currentUser);

		StringBuffer num1 = new StringBuffer("000000000");
		num1.append(nextInvoiceNb + "");

		String invoiceNumber = num1.substring(num1.length() - 9);
		// request to store invoiceNo in alias field
		invoice.setAlias(invoiceNumber);

		return (prefix + invoiceNumber);
	}

	public synchronized long getNextValue(Seller seller, User currentUser) {
		long result = 0;

		if (seller != null) {
			if (seller.getCurrentInvoiceNb() != null) {
				long currentInvoiceNbre = seller.getCurrentInvoiceNb();
				result = 1 + currentInvoiceNbre;
				seller.setCurrentInvoiceNb(result);
			} else {
				result = getNextValue(seller.getProvider(), currentUser);
			}
		}

		return result;
	}

	public synchronized long getNextValue(Provider provider, User currentUser) {
		long result = 0;

		if (provider != null) {
			long currentInvoiceNbre = provider.getCurrentInvoiceNb() != null ? provider.getCurrentInvoiceNb() : 0;
			result = 1 + currentInvoiceNbre;
			provider.setCurrentInvoiceNb(result);

			if (currentUser != null) {
				provider.updateAudit(currentUser);
			} else {
				provider.updateAudit(provider.getAuditable().getCreator());
			}
		}

		return result;
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
			log.error("failed to get validated invoices with no pdf",ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoicesWithNoAccountOperation(BillingRun br) {
		try {
			QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
			qb.addCriterionEntity("i.billingRun.status", BillingRunStatusEnum.VALIDATED);
			qb.addSql("i.recordedInvoice is null");
			if (br != null) {
				qb.addCriterionEntity("i.billingRun", br);
			}
			return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
		} catch (Exception ex) {
			log.error("failed to get invoices with no account operation",ex);
		}
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createAgregatesAndInvoice(BillingAccount billingAccount, Long billingRunId, User currentUser)
			throws BusinessException, Exception {
		log.debug("createAgregatesAndInvoice tx status={}", txReg.getTransactionStatus());
		EntityManager em = getEntityManager();
		BillingRun billingRun = em.find(BillingRun.class, billingRunId);
		em.refresh(billingRun);
		try {
			billingAccount = em.find(billingAccount.getClass(), billingAccount.getId());
			em.refresh(billingAccount);
			currentUser = em.find(currentUser.getClass(), currentUser.getId());
			em.refresh(currentUser);

			Long startDate = System.currentTimeMillis();
			BillingCycle billingCycle = billingRun.getBillingCycle();
			if (billingCycle == null) {
				billingCycle = billingAccount.getBillingCycle();
			}

			Invoice invoice = new Invoice();
			invoice.setBillingAccount(billingAccount);
			invoice.setBillingRun(billingRun);
			invoice.setAuditable(billingRun.getAuditable());
			invoice.setProvider(billingRun.getProvider());
			//ticket 680
			Date invoiceDate = billingRun.getInvoiceDate();
			invoice.setInvoiceDate(invoiceDate);

			Integer delay = billingCycle.getDueDateDelay();
			Date dueDate = invoiceDate;
			if (delay != null) {
				dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
			}
			invoice.setDueDate(dueDate);

            PaymentMethodEnum paymentMethod= billingAccount.getPaymentMethod();
            if(paymentMethod==null){
                paymentMethod=billingAccount.getCustomerAccount().getPaymentMethod();
            }
			invoice.setPaymentMethod(paymentMethod);
			invoice.setProvider(billingRun.getProvider());
			em.persist(invoice);
			// create(invoice, currentUser, currentUser.getProvider());
			log.debug("created invoice entity with id={},  tx status={}, em open={}", invoice.getId(),
					txReg.getTransactionStatus(), em.isOpen());
			ratedTransactionService.createInvoiceAndAgregates(billingAccount, invoice,billingRun.getLastTransactionDate(), currentUser);
			log.debug("created aggregates tx status={}, em open={}", txReg.getTransactionStatus(), em.isOpen());
			em.joinTransaction();

			if (billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
				em.createNamedQuery("RatedTransaction.updateInvoicedDisplayFree")
						.setParameter("billingAccount", billingAccount)
						.setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
						.setParameter("billingRun", billingRun)
						.setParameter("invoice", invoice).executeUpdate();
			} else {
				em.createNamedQuery("RatedTransaction.updateInvoiced")
				        .setParameter("billingAccount", billingAccount)
				        .setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
						.setParameter("billingRun", billingRun)
						.setParameter("invoice", invoice)
						.executeUpdate();

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
			Long endDate = System.currentTimeMillis();

			log.info("createAgregatesAndInvoice BR_ID=" + billingRun.getId() + ", BA_ID=" + billingAccount.getId()
					+ ", Time en ms=" + (endDate - startDate));
		} catch (Exception e) {
			log.error("Error for BA=" + billingAccount.getCode() + " : " + e);

			RejectedBillingAccount rejectedBA = new RejectedBillingAccount(billingAccount, billingRun, e.getMessage());
			rejectedBillingAccountService.create(rejectedBA, currentUser, currentUser.getProvider());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> findByBillingRun(BillingRun billingRun) {
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
		qb.addCriterionEntity("billingRun", billingRun);

		try {
			return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("failed to find by billingRun",e);
			return null;
		}
	}
	

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void producePdf(Map<String, Object> parameters, User currentUser) throws Exception {
		 
			ParamBean paramBean = ParamBean.getInstance();
			log.info("PDFInvoiceGenerationJob is invoice key exists="
					+ ((parameters != null) ? parameters
					.containsKey(PdfGeneratorConstants.INVOICE) + ""
					: "parameters is null"));
			
			Invoice invoice = (Invoice) parameters
					.get(PdfGeneratorConstants.INVOICE);
			String meveoDir = paramBean.getProperty("providers.rootDir",
					"/tmp/meveo/")
					+ File.separator
					+ invoice.getProvider().getCode() + File.separator;
			String pdfDirectory = meveoDir + "invoices" + File.separator
					+ "pdf" + File.separator;

			(new File(pdfDirectory)).mkdirs();
			String INVOICE_TAG_NAME = "invoice";

			File billingRundir = new File(meveoDir + "invoices"
					+ File.separator + "xml" + File.separator
					+ invoice.getBillingRun().getId());
			String invoiceXmlFileName = billingRundir + File.separator
			+ (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()) + ".xml";
			File invoiceXmlFile = new File(invoiceXmlFileName);
			if (!invoiceXmlFile.exists()) {
				throw new InvoiceXmlNotFoundException(
						"The xml invoice file doesn't exist.");
			}
			BillingCycle billingCycle = invoice.getBillingRun()
					.getBillingCycle();
			BillingAccount billingAccount = invoice.getBillingAccount();
			String billingTemplate = (billingCycle != null
					&& billingCycle.getBillingTemplateName() != null) ? billingCycle
					.getBillingTemplateName() : "default";
			String resDir = meveoDir + "jasper";
			File jasperFile = getJasperTemplateFile(resDir, billingTemplate,
					billingAccount.getPaymentMethod());
			if(!jasperFile.exists()){
				throw new InvoiceJasperNotFoundException(
						"The jasper file doesn't exist.");
			  }
			log.info(String.format("Jasper template used: %s",
					jasperFile.getCanonicalPath()));

			InputStream reportTemplate = new FileInputStream(jasperFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmlDocument = db.parse(invoiceXmlFile);
			xmlDocument.getDocumentElement().normalize(); // TODO check this out
			Node invoiceNode = xmlDocument.getElementsByTagName(
					INVOICE_TAG_NAME).item(0);
			JRXmlDataSource dataSource = new JRXmlDataSource(
					new ByteArrayInputStream(getNodeXmlString(invoiceNode)
							.getBytes()),
					"/invoice/detail/userAccounts/userAccount/categories/category/subCategories/subCategory/line");
			JasperReport jasperReport = (JasperReport) JRLoader
					.loadObject(reportTemplate);
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, parameters, dataSource);
			String pdfFileName = getNameWoutSequence(pdfDirectory,
					invoice.getInvoiceDate(), (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber())) 
					+ ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
			log.info(String.format("PDF file '%s' produced", pdfFileName));

			FileInputStream fileInputStream = null;
			try {
				File file = new File(pdfFileName);
				long fileSize = file.length();
				if (fileSize > Integer.MAX_VALUE) {
					throw new IllegalArgumentException(
							"File is too big to put it to buffer in memory.");
				}
				byte[] fileBytes = new byte[(int) file.length()];
				fileInputStream = new FileInputStream(file);
				fileInputStream.read(fileBytes);
				invoice.setPdf(fileBytes);
				invoice.updateAudit(currentUser);
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
	
	private File getJasperTemplateFile(String resDir, String billingTemplate,
			PaymentMethodEnum paymentMethod) {
		String pdfDirName = new StringBuilder(resDir).append(File.separator)
				.append(billingTemplate).append(File.separator)
				.append(PDF_DIR_NAME).toString();
		File pdfDir = new File(pdfDirName);
		String paymentMethodFileName = new StringBuilder("invoice_")
				.append(paymentMethod).append(".jasper").toString();
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
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer
					.transform(new DOMSource(node), new StreamResult(buffer));
			return buffer.toString();
		} catch (Exception e) {
			log.error(
					"Error converting xml node to its string representation. {}",
					e);
			throw new ConfigurationException();
		}
	}
	
	public  String getNameWoutSequence(String tempDir, Date invoiceDate,
			String invoiceNumber) {
		return new StringBuilder(tempDir).append(File.separator)
				.append(formatInvoiceDate(invoiceDate)).append("_")
				.append(invoiceNumber).toString();
	}
	
	public  String formatInvoiceDate(Date invoiceDate) {
		DateFormat dateFormat = new SimpleDateFormat(DATE_PATERN);
		return dateFormat.format(invoiceDate);
	}
}
