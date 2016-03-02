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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceTypeEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Stateless
public class InvoiceService extends PersistenceService<Invoice> {

	private ParamBean paramBean = ParamBean.getInstance();

	public final static String INVOICE_ADJUSTMENT_SEQUENCE = "INVOICE_ADJUSTMENT_SEQUENCE";

	public final static String INVOICE_SEQUENCE = "INVOICE_SEQUENCE";

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private InvoiceAgregateService invoiceAgregateService;

	@Inject
	private ProviderService providerService;

	@Inject
	private SellerService sellerService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private RejectedBillingAccountService rejectedBillingAccountService;
	
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;
	

	private String PDF_DIR_NAME = "pdf";
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
	
	public Invoice findByInvoiceNumberAndType(String invoiceNumber, InvoiceTypeEnum type, Provider provider)
			throws BusinessException {
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null, provider);
		qb.addCriterion("invoiceNumber", "=", invoiceNumber, true);
		qb.addCriterionEnum("invoiceTypeEnum", type);
		try {
			return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
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
		return getInvoiceByNumber(invoiceNumber, InvoiceTypeEnum.COMMERCIAL);
	}

	public Invoice getInvoiceByNumber(String invoiceNumber, InvoiceTypeEnum type) throws BusinessException {
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null, null);
		try {
			qb.addCriterion("invoiceNumber", "=", invoiceNumber, true);
			qb.addCriterionEnum("invoiceTypeEnum", type);
			return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
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
		Seller seller = invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		String prefix = seller.getInvoicePrefix();

		if (prefix == null) {
			prefix = seller.getProvider().getInvoicePrefix();
		}
		if (prefix == null) {
			prefix = "";
		}

		if (prefix != null && !StringUtils.isBlank(prefix)) {
			if (prefix.indexOf("%") >= 0) {
				int startIndex = prefix.indexOf("%") + 1;
				int endIndex = prefix.indexOf("%", startIndex);
				if (endIndex > 0) {
					String datePattern = prefix.substring(startIndex, endIndex);
					String invioceDate = DateUtils.formatDateWithPattern(new Date(), datePattern);
					prefix = prefix.replace("%" + datePattern + "%", invioceDate);
				}
			}

			prefix = evaluatePrefixElExpression(prefix, invoice, DateUtils.getDayFromDate(invoice.getInvoiceDate()),
					DateUtils.getMonthFromDate(invoice.getInvoiceDate()),
					DateUtils.getYearFromDate(invoice.getInvoiceDate()));
		}
		if (currentUser != null) {
			seller.updateAudit(currentUser);
		} else {
			seller.updateAudit(seller.getAuditable().getCreator());
		}

		long nextInvoiceNb = getNextValue(seller, currentUser);
		int sequenceSize = getSequenceSize(seller, currentUser);
		StringBuffer num1 = new StringBuffer(org.apache.commons.lang3.StringUtils.leftPad("", sequenceSize, "0"));
		num1.append(nextInvoiceNb + "");

		String invoiceNumber = num1.substring(num1.length() - sequenceSize);
		// request to store invoiceNo in alias field
		invoice.setAlias(invoiceNumber);

		return (prefix + invoiceNumber);
	}

	public synchronized long getNextValue(Seller seller, User currentUser) {
		long result = 0;

		if (seller != null) {
			Date now = new Date();
			Object sequenceValObj = customFieldInstanceService.getCFValue(seller, INVOICE_SEQUENCE, now, currentUser);
			if (sequenceValObj != null) {
				Long sequenceVal = 1L;
				try {
					sequenceVal = Long.parseLong(sequenceValObj.toString());
				} catch (NumberFormatException e) {
					sequenceVal = 1L;
				}

				result = 1 + sequenceVal;
                try {
                    customFieldInstanceService.setCFValue(seller, INVOICE_SEQUENCE, result, now, currentUser);
                } catch (BusinessException e) {
                    log.error("Failed to set custom field " + INVOICE_SEQUENCE + " value on provider", e);
                }
			} else if (seller.getCurrentInvoiceNb() != null) {
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
		Date now = new Date();
		if (provider != null) {
		    
		    Object sequenceValObj = customFieldInstanceService.getCFValue(provider, INVOICE_SEQUENCE, now, currentUser);
			if (sequenceValObj != null) {
				Long sequenceVal = 1L;
				try {
					sequenceVal = Long.parseLong(sequenceValObj.toString());
				} catch (NumberFormatException e) {
					sequenceVal = 1L;
				}

				result = 1 + sequenceVal;
				try {
                    customFieldInstanceService.setCFValue(provider, INVOICE_SEQUENCE, result, now, currentUser);
                } catch (BusinessException e) {
                    log.error("Failed to set custom field " + INVOICE_SEQUENCE + " value on provider", e);
                }
			} else {   
				long currentInvoiceNbre = provider.getCurrentInvoiceNb()!= null ? provider.getCurrentInvoiceNb() : 0;
				result = 1 + currentInvoiceNbre;
				provider.setCurrentInvoiceNb(result);
			}

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
			log.error("failed to get validated invoices with no pdf", ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getInvoiceIdsWithNoAccountOperation(BillingRun br, Provider currentProvider) {
		try {
			QueryBuilder qb = new QueryBuilder("SELECT i.id FROM " + Invoice.class.getName() + " i");
			qb.addCriterionEntity("i.provider", currentProvider);
			qb.addCriterion("i.billingRun.status", "=", BillingRunStatusEnum.VALIDATED, true);
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
			invoice.setInvoiceTypeEnum(InvoiceTypeEnum.COMMERCIAL);
			invoice.setBillingAccount(billingAccount);
			invoice.setBillingRun(billingRun);
			invoice.setAuditable(billingRun.getAuditable());
			invoice.setProvider(billingRun.getProvider());
			// ticket 680
			Date invoiceDate = billingRun.getInvoiceDate();
			invoice.setInvoiceDate(invoiceDate);

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
			invoice.setProvider(billingRun.getProvider());

			em.persist(invoice);

			// create(invoice, currentUser, currentUser.getProvider());
			log.debug("created invoice entity with id={},  tx status={}, em open={}", invoice.getId(),
					txReg.getTransactionStatus(), em.isOpen());
			ratedTransactionService.createInvoiceAndAgregates(billingAccount, invoice,
					billingRun.getLastTransactionDate(), currentUser);
			log.debug("created aggregates tx status={}, em open={}", txReg.getTransactionStatus(), em.isOpen());
			em.joinTransaction();

			if (billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
				em.createNamedQuery("RatedTransaction.updateInvoicedDisplayFree")
						.setParameter("billingAccount", billingAccount)
						.setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
						.setParameter("billingRun", billingRun).setParameter("invoice", invoice).executeUpdate();
			} else {
				em.createNamedQuery("RatedTransaction.updateInvoiced").setParameter("billingAccount", billingAccount)
						.setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
						.setParameter("billingRun", billingRun).setParameter("invoice", invoice).executeUpdate();

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
			log.error("Error for BA=" + billingAccount.getCode() + " : ", e);

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
			log.warn("failed to find by billingRun", e);
			return null;
		}
	}

	public void produceInvoiceAdjustmentPdf(Map<String, Object> parameters, User currentUser) throws Exception {
		String meveoDir = paramBean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator
				+ currentUser.getProvider().getCode() + File.separator;

		Invoice invoice = (Invoice) parameters.get(PdfGeneratorConstants.INVOICE);
		File billingRundir = new File(meveoDir + "invoices" + File.separator + "xml" + File.separator
				+ invoice.getBillingRun().getId());

		String invoiceXmlFileName = billingRundir + File.separator
				+ paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + invoice.getInvoiceNumber()
				+ ".xml";

		producePdf(parameters, currentUser, invoiceXmlFileName, meveoDir, true);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void producePdf(Map<String, Object> parameters, User currentUser) throws Exception {
		String meveoDir = paramBean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator
				+ currentUser.getProvider().getCode() + File.separator;

		Invoice invoice = (Invoice) parameters.get(PdfGeneratorConstants.INVOICE);
		File billingRundir = new File(meveoDir
				+ "invoices"
				+ File.separator
				+ "xml"
				+ File.separator
				+ (invoice.getBillingRun() == null ? DateUtils.formatDateWithPattern(invoice.getAuditable()
						.getCreated(), paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss"))
						: invoice.getBillingRun().getId()));

		String invoiceXmlFileName = billingRundir
				+ File.separator
				+ (!StringUtils.isBlank(invoice.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice
						.getTemporaryInvoiceNumber()) + ".xml";

		producePdf(parameters, currentUser, invoiceXmlFileName, meveoDir, false);
	}

	public void producePdf(Map<String, Object> parameters, User currentUser, String invoiceXmlFileName,
			String meveoDir, boolean isInvoiceAdjustment) throws Exception {
		log.info("PDFInvoiceGenerationJob is invoice key exists="
				+ ((parameters != null) ? parameters.containsKey(PdfGeneratorConstants.INVOICE) + ""
						: "parameters is null"));

		String pdfDirectory = meveoDir + "invoices" + File.separator + "pdf" + File.separator;
		(new File(pdfDirectory)).mkdirs();

		Invoice invoice = (Invoice) parameters.get(PdfGeneratorConstants.INVOICE);
		String INVOICE_TAG_NAME = "invoice";

		File invoiceXmlFile = new File(invoiceXmlFileName);
		if (!invoiceXmlFile.exists()) {
			throw new InvoiceXmlNotFoundException("The xml invoice file doesn't exist.");
		}
		BillingAccount billingAccount = invoice.getBillingAccount();
		BillingCycle billingCycle = null;
		if (billingAccount!= null && billingAccount.getBillingCycle()!= null) {
			billingCycle=billingAccount.getBillingCycle();
		}
		String billingTemplate = (billingCycle != null && billingCycle.getBillingTemplateName() != null) ? billingCycle
				.getBillingTemplateName() : "default";
		String resDir = meveoDir + "jasper";
		File destDir = new File(resDir + File.separator + billingTemplate + File.separator + "pdf");
		File sourceJapsers = new File(resDir + File.separator + "default" + File.separator + "pdf");
		if (!destDir.exists()) {
			destDir.mkdirs();
			FileUtils.copyDirectory(sourceJapsers, destDir);
		}
		
		/*>> it's not working with meveo.war no-exploded
		 * 
		 * if (!destDir.exists()) {
			destDir.mkdirs();

			String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath();
			File sourceFile = new File(sourcePath);
			if (!sourceFile.exists()) {
				VirtualFile vfDir = VFS.getChild("/content/"
						+ ParamBean.getInstance().getProperty("meveo.moduleName", "meveo")
						+ ".war/WEB-INF/classes/jasper");
				URL vfPath = VFSUtils.getPhysicalURL(vfDir);
				sourceFile = new File(vfPath.getPath());
				if (!sourceFile.exists()) {
					throw new BusinessException("embedded jasper report for invoice isn't existed!");
				}
			}
			FileUtils.copyDirectory(sourceFile, destDir);
		}*/
		
		File jasperFile = getJasperTemplateFile(resDir, billingTemplate, billingAccount.getPaymentMethod());
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
			dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(invoiceNode).getBytes()),
					"/invoice");
		} else {
			dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(invoiceNode).getBytes()),
					"/invoice");
		}

		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		String pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(), (!StringUtils.isBlank(invoice
				.getInvoiceNumber()) ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()))
				+ ".pdf";
		if (isInvoiceAdjustment) {
			pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(),
					paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_") + invoice.getInvoiceNumber())
					+ ".pdf";
		}

		JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
		log.info(String.format("PDF file '%s' produced", pdfFileName));

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
			invoice.updateAudit(currentUser);
			updateNoCheck(invoice);
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

	private File getJasperTemplateFile(String resDir, String billingTemplate, PaymentMethodEnum paymentMethod) {
		String pdfDirName = new StringBuilder(resDir).append(File.separator).append(billingTemplate)
				.append(File.separator).append(PDF_DIR_NAME).toString();
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

	public String getInvoiceAdjustmentNumber(Invoice invoiceAdjustment, User currentUser) {
		Seller seller = invoiceAdjustment.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		String prefix = seller.getInvoiceAdjustmentPrefix();

		if (prefix == null) {
			prefix = seller.getProvider().getInvoiceAdjustmentPrefix();
		}
		if (prefix == null) {
			prefix = "";
		}

		if (prefix != null && !StringUtils.isBlank(prefix)) {
			if (prefix.indexOf("%") >= 0) {
				int startIndex = prefix.indexOf("%") + 1;
				int endIndex = prefix.indexOf("%", startIndex);
				if (endIndex > 0) {
					String datePattern = prefix.substring(startIndex, endIndex);
					String invoiceAdjustmentDate = DateUtils.formatDateWithPattern(new Date(), datePattern);
					prefix = prefix.replace("%" + datePattern + "%", invoiceAdjustmentDate);
				}
			}

			try {
				prefix = evaluatePrefixElExpression(prefix, invoiceAdjustment,
						DateUtils.getDayFromDate(invoiceAdjustment.getInvoiceDate()),
						DateUtils.getMonthFromDate(invoiceAdjustment.getInvoiceDate()),
						DateUtils.getYearFromDate(invoiceAdjustment.getInvoiceDate()));
			} catch (BusinessException e) {
				log.warn("Invalid prefix={}", e.getMessage());
				prefix = "";
			}
		}

		long nextInvoiceAdjustmentNb = getInvoiceAdjustmentNextValue(invoiceAdjustment, seller, currentUser);

		int padSize = getNBOfChars(seller, currentUser);

		StringBuffer num1 = new StringBuffer(org.apache.commons.lang3.StringUtils.leftPad("", padSize, "0"));
		num1.append(nextInvoiceAdjustmentNb + "");

		String invoiceAdjustmentNumber = num1.substring(num1.length() - padSize);
		
		invoiceAdjustment.setAlias(invoiceAdjustmentNumber);

		return (prefix + invoiceAdjustmentNumber);
	}

	public synchronized int getNBOfChars(Seller seller, User currentUser) {
		int result = 9;

		if (seller != null) {
			if (seller.getInvoiceAdjustmentSequenceSize() != null && seller.getInvoiceAdjustmentSequenceSize() != 0) {
				result = seller.getInvoiceAdjustmentSequenceSize();
			} else {
				if (seller.getProvider().getInvoiceAdjustmentSequenceSize() != null
						&& seller.getProvider().getInvoiceAdjustmentSequenceSize() != 0) {
					result = seller.getProvider().getInvoiceAdjustmentSequenceSize();
				}
			}
		}

		return result;
	}

	public synchronized long getInvoiceAdjustmentNextValue(Invoice invoiceAdjustment, Seller seller, User currentUser) {
		long result = 0;

		if (seller != null) {
		    Date now = new Date();
            Object sequenceValObj = customFieldInstanceService.getCFValue(seller, INVOICE_ADJUSTMENT_SEQUENCE, now, currentUser);
			if (sequenceValObj != null) {
				Long sequenceVal = 1L;
				try {
					sequenceVal = Long.parseLong(sequenceValObj.toString());
				} catch (NumberFormatException e) {
					sequenceVal = 1L;
				}

				result = sequenceVal;
				invoiceAdjustment.setInvoiceAdjustmentCurrentSellerNb(sequenceVal);
			} else if (seller.getCurrentInvoiceAdjustmentNb() != null) {
				long currentInvoiceAdjustmentNo = seller.getCurrentInvoiceAdjustmentNb();
				result = 1 + currentInvoiceAdjustmentNo;
			} else {
				result = getInvoiceAdjustmentNextValue(invoiceAdjustment, seller.getProvider(), currentUser);
			}
		}

		return result;
	}

	public synchronized long getInvoiceAdjustmentNextValue(Invoice invoiceAdjustment, Provider provider,
			User currentUser) {
		long result = 0;

		Date now = new Date();
		if (provider != null) {
		    Object sequenceValObj = customFieldInstanceService.getCFValue(provider, INVOICE_ADJUSTMENT_SEQUENCE, now, currentUser);
			if (sequenceValObj != null) {
				Long sequenceVal = 1L;
				try {
					sequenceVal = Long.parseLong(sequenceValObj.toString());
				} catch (NumberFormatException e) {
					sequenceVal = 1L;
				}

				result = sequenceVal;
				invoiceAdjustment.setInvoiceAdjustmentCurrentProviderNb(sequenceVal);
			} else {
				long currentInvoiceAdjustmentNo = provider.getCurrentInvoiceAdjustmentNb() != null ? provider
						.getCurrentInvoiceAdjustmentNb() : 0;
				result = 1 + currentInvoiceAdjustmentNo;
			}
		}

		return result;
	}

	public void updateCreditNoteNb(Invoice invoiceAdjustment, Long invoiceAdjustmentNo) {
		Seller seller = invoiceAdjustment.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		if (seller != null && seller.getCurrentInvoiceAdjustmentNb() != null
				&& (invoiceAdjustmentNo - 1 == seller.getCurrentInvoiceAdjustmentNb())) {
			seller.setCurrentInvoiceAdjustmentNb(invoiceAdjustmentNo);

			sellerService.update(seller, getCurrentUser());
		} else {
			Provider provider = seller.getProvider();
			provider.setCurrentInvoiceAdjustmentNb(invoiceAdjustmentNo);

			providerService.update(provider, getCurrentUser());
		}
	}

	public synchronized Integer getSequenceSize(Seller seller, User currentUser) {
		int result = 9;
		if (seller != null) {
			if (seller.getInvoiceSequenceSize() != null && seller.getInvoiceSequenceSize()!=0) {
				result = seller.getInvoiceSequenceSize();
			} else { 
				Provider provider=seller.getProvider();
				if (provider.getInvoiceSequenceSize() != null && provider.getInvoiceSequenceSize()!=0) {
					result = provider.getInvoiceSequenceSize();
				}
			}
		}
		return result;
	}

	public String evaluatePrefixElExpression(String prefix, Invoice invoice, int day, int month, int year)
			throws BusinessException {
		String result = null;
		Date invoiceDate = invoice.getInvoiceDate();
		if (StringUtils.isBlank(prefix)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		if (prefix.indexOf("invoice") >= 0) {
			userMap.put("invoice", invoice);
		}
		if (prefix.indexOf("day") >= 0) {
			userMap.put("day", DateUtils.getDayFromDate(invoiceDate));
		}
		if (prefix.indexOf("month") >= 0) {
			userMap.put("month", DateUtils.getMonthFromDate(invoiceDate) + 1);
			;
		}
		if (prefix.indexOf("year") >= 0) {
			userMap.put("year", DateUtils.getYearFromDate(invoiceDate));
			;
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
		boolean entreprise = invoice.getBillingAccount().getProvider().isEntreprise();
		int rounding = invoice.getBillingAccount().getProvider().getRounding() == null ? 2 : invoice
				.getBillingAccount().getProvider().getRounding();
		boolean exoneratedFromTaxes = invoice.getBillingAccount().getCustomerAccount().getCustomer()
				.getCustomerCategory().getExoneratedFromTaxes();
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

			subCategoryInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax().setScale(
					rounding, RoundingMode.HALF_UP));

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
			invoice.setAmountWithTax(invoice.getAmountWithoutTax().add(invoice.getAmountTax()));
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

		Object[] object = invoiceAgregateService.findTotalAmountsForDiscountAggregates(invoice.getAdjustedInvoice());
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

	public void updateInvoiceAdjustmentCurrentNb(Invoice invoice, User currentUser) {
		// update seller or provider current invoice sequence value
		if (invoice.getInvoiceAdjustmentCurrentSellerNb() != null) {
			Seller seller = invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
			try {
                customFieldInstanceService.setCFValue(seller, INVOICE_ADJUSTMENT_SEQUENCE, invoice.getInvoiceAdjustmentCurrentSellerNb() + 1, new Date(), currentUser);
            } catch (BusinessException e) {
                log.error("Failed to set custom field " + INVOICE_ADJUSTMENT_SEQUENCE + " value on seller {}",seller, e);
            }			
			sellerService.update(seller, getCurrentUser());
		} else if (invoice.getInvoiceAdjustmentCurrentProviderNb() != null) {
			Provider provider = invoice.getProvider();
			try {
                customFieldInstanceService.setCFValue(provider, INVOICE_ADJUSTMENT_SEQUENCE, invoice.getInvoiceAdjustmentCurrentProviderNb() + 1, new Date(), currentUser);
            } catch (BusinessException e) {
                log.error("Failed to set custom field " + INVOICE_ADJUSTMENT_SEQUENCE + " value on provider {}",provider, e);
            }   
			providerService.update(provider, getCurrentUser());
		}
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
	public List<Invoice> findInvoiceAdjustmentByInvoice(Invoice adjustedInvoice) {
		QueryBuilder qb = new QueryBuilder(Invoice.class, "i", null, adjustedInvoice.getProvider());
		qb.addCriterionEntity("adjustedInvoice", adjustedInvoice);
		qb.addCriterionEnum("invoiceTypeEnum", InvoiceTypeEnum.CREDIT_NOTE_ADJUST);

		try {
			return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
