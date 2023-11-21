package org.meveo.service.cpq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ConfigurationException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.job.PdfGeneratorConstants;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.PDFGenerated;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.catalog.impl.CatalogHierarchyBuilderService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.InternationalSettingsService;
import org.w3c.dom.Document;
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

@Stateless
public class CpqQuoteService extends BusinessService<CpqQuote> {

	@Inject private CatalogHierarchyBuilderService catalogHierarchyBuilderService;
	
	@Inject
	private InvoiceTypeService invoiceTypeService;
	
	@Inject
	private QuoteArticleLineService quoteArticleLineService;
	
	@Inject
	private QuoteVersionService quoteVersionService;

	@Inject
	private EmailSender emailSender;

	@Inject
	private InternationalSettingsService internationalSettingsService;
	
	@Inject
    @PDFGenerated
    private Event<QuoteVersion> pdfGeneratedEventProducer;
	
	  /** map used to store temporary jasper report. */
    private Map<String, JasperReport> jasperReportMap = new HashMap<>();
    
    /** date format. */
    private String DATE_PATERN = "yyyy.MM.dd";
	
	public CpqQuote duplicate(CpqQuote quote, QuoteVersion quoteVersion, boolean preserveCode, boolean duplicateHierarchy) {
		
		final CpqQuote duplicate = new CpqQuote(quote);
		duplicate.setStatus(QuoteStatusEnum.IN_PROGRESS.toString());
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		detach(quote);
	   	 if(!preserveCode) {
	         String code = findDuplicateCode(duplicate);
	   	   	duplicate.setCode(code);
	   	 }

		 try {
		   	 	super.create(duplicate);
	   	 }catch(BusinessException e) {
	   		 throw new MeveoApiException(e);
	   	 }
		 if(duplicateHierarchy) {
			 catalogHierarchyBuilderService.duplicateQuoteVersion(duplicate, quoteVersion);
		 }
		return duplicate;
	}
	
	
	 public String formatQuoteDate(Date quoteDate) {
	        DateFormat dateFormat = new SimpleDateFormat(DATE_PATERN);
	        return dateFormat.format(quoteDate);
	    }
	    
	    /**
	     * Get a full path to an quote XML file.
	     *
	     * @param quote quote
	     * @param createDirs Should missing directories be created
	     * @return Absolute path to an XML file
	     */
	    public String getFullXmlFilePath(QuoteVersion quoteVersion, boolean createDirs) {

	        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

	        String xmlFilename = meveoDir + "quotes" + File.separator + "xml" + File.separator + quoteVersion.getXmlFilename()+".xml";

	        if (createDirs) {
	            int pos = Integer.max(xmlFilename.lastIndexOf("/"), xmlFilename.lastIndexOf("\\"));
	            String dir = xmlFilename.substring(0, pos);
	            (new File(dir)).mkdirs();
	        }

	        return xmlFilename;
	    }
	    
	    public String generateFileName(QuoteVersion quoteVersion) {
	    	if (StringUtils.isNotBlank(quoteVersion.getXmlFilename())) {
	            return quoteVersion.getXmlFilename();
	        }
	    	CpqQuote quote=quoteVersion.getQuote();
	    	 InvoiceType quoteType=invoiceTypeService.getDefaultQuote();
	    	// Generate a name for xml file from EL expression
	         String xmlFileName = null;
	         String expression = quoteType.getXmlFilenameEL();
	         if (!StringUtils.isBlank(expression)) {
	             Map<Object, Object> contextMap = new HashMap<Object, Object>();
	             contextMap.put("quoteVersion",quoteVersion);
	             try {
	                 String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);
	                 if (value != null) {
	                     xmlFileName = value;
	                 }
	             } catch (BusinessException e) {
	                 // Ignore exceptions here - a default XML filename will be used instead. Error is logged in EL evaluation
	             }
	         }
	         if (StringUtils.isBlank(xmlFileName)) {
	        	  String quoteDate = new SimpleDateFormat("ddMMyyyy").format(quote.getQuoteDate());
	  	        ParamBean paramBean = ParamBean.getInstance();
	  	        String prefix = paramBean.getProperty("quote.filename.prefix", "quote");
	  	        String identifier = quote.getQuoteNumber() != null ? quote.getQuoteNumber() : quote.getCode();
	  	        xmlFileName= String.format("%s_%s-%s", quoteDate, prefix, identifier);
	         }
	    	return xmlFileName;
	      
	    }
	    
	    
	    /**
	     * Return a pdf filename that was assigned to quote
	     *
	     *
	     * @param QuoteVersion quoteVersion
	     * @return Pdf file name
	     */
	    public String getOrGeneratePdfFilename(QuoteVersion quoteVersion) {
	    	CpqQuote quote =quoteVersion.getQuote();
	        if (StringUtils.isNotBlank(quoteVersion.getPdfFilename())) {
	            return quoteVersion.getPdfFilename();
	        } 
	        String pdfFileName = null; 
	        InvoiceType quoteType=invoiceTypeService.getDefaultQuote();
	    	// Generate a name for pdf file from EL expression
	         String xmlFileName = null;
	         String expression = quoteType.getPdfFilenameEL();
	         if (!StringUtils.isBlank(expression)) {
	             Map<Object, Object> contextMap = new HashMap<Object, Object>();
	             contextMap.put("quoteVersion", quoteVersion);
	             try {
	                 String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);
	                 if (value != null) {
	                	 pdfFileName = value;
	                 }
	             } catch (BusinessException e) {
	                 // Ignore exceptions here - a default XML filename will be used instead. Error is logged in EL evaluation
	             }
	         }
	        if (StringUtils.isBlank(pdfFileName)) { 

	        	  String quoteDate = new SimpleDateFormat("ddMMyyyy").format(quote.getQuoteDate());
		  	        ParamBean paramBean = ParamBean.getInstance();
		  	        String prefix = paramBean.getProperty("quote.filename.prefix", "quote");
		  	        String identifier = quote.getQuoteNumber() != null ? quote.getQuoteNumber() : quote.getCode();
		  	      pdfFileName= String.format("%s_%s-%s", quoteDate, prefix, identifier);
	        }
	        if (pdfFileName != null && !pdfFileName.toLowerCase().endsWith(".pdf")) {
	            pdfFileName = pdfFileName + ".pdf";
	        }
	        pdfFileName = StringUtils.normalizeFileName(pdfFileName);
	        return pdfFileName;
	    }
	    
	    public String getFullPdfFilePath(QuoteVersion quoteVersion, boolean createDirs) {

	        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

	        String pdfFilename = meveoDir + "quotes" + File.separator + "pdf" + File.separator + getOrGeneratePdfFilename(quoteVersion);

	        if (createDirs) {
	            int pos = Integer.max(pdfFilename.lastIndexOf("/"), pdfFilename.lastIndexOf("\\"));
	            String dir = pdfFilename.substring(0, pos);
	            (new File(dir)).mkdirs();
	        }

	        return pdfFilename;
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
	    
	    public void generateQuotePdf(QuoteVersion quoteVersion) throws BusinessException {
	    	CpqQuote quote =quoteVersion.getQuote();
	        log.debug("Creating pdf for quote number={} code={} quoteVersion={}",quote.getQuoteNumber(), quote.getCode(),quoteVersion.getQuoteVersion());
	        ParamBean paramBean = paramBeanFactory.getInstance();
	        String quoteDir = paramBean.getChrootDir(currentUser.getProviderCode()) + File.separator;
	        String quoteXmlFileName = getFullXmlFilePath(quoteVersion, false); 
	        String QUOTE_TAG_NAME = "quote";
	        File quoteXmlFile = new File(quoteXmlFileName);
	        if (!StorageFactory.exists(quoteXmlFile)) {
	            //createXmlQuote(quote);
	        }    
	        String resDir = quoteDir + "quotes"  + File.separator + "jasper";
	        String pdfFilename = getOrGeneratePdfFilename(quoteVersion);
	        quoteVersion.setPdfFilename(pdfFilename);
	        String pdfFullFilename = getFullPdfFilePath(quoteVersion, true);
	        InputStream reportTemplate = null;
	        try {
	            File destDir = new File(resDir);

	            if (!destDir.exists()) {

	                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath()  + File.separator + paramBean.getProperty("provider.rootDir", "default") + File.separator+ "quote";

	                File sourceFile = new File(sourcePath);
	                if (!sourceFile.exists()) {
	                    VirtualFile vfDir = VFS.getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/default/quote");
	                    log.info("default jaspers path :" + vfDir.getPathName());
	                    URL vfPath = VFSUtils.getPhysicalURL(vfDir);
	                    sourceFile = new File(vfPath.getPath());
	                    if (!sourceFile.exists()) {
	                        throw new BusinessException("embedded jasper report for quote is missing..");
	                    }
	                }
	                destDir.mkdirs();
	                FileUtils.copyDirectory(sourceFile, destDir);
	            }   
	            InvoiceType quoteType=invoiceTypeService.getDefaultQuote();
	            String jasperFilename="quote";
	            if(quoteType!=null && quoteType.getBillingTemplateNameEL()!=null) {
	            	jasperFilename=evaluateQuoteTemplateName(quoteType.getBillingTemplateNameEL(), quote);
	            }
	            File jasperFile = new File(resDir, jasperFilename+".jasper");
	            if (!jasperFile.exists()) {
	                throw new InvoiceJasperNotFoundException("The jasper file doesn't exist.");
	            }
	            log.debug("Jasper template used: {}", jasperFile.getCanonicalPath());

	            reportTemplate = new FileInputStream(jasperFile);
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
				Document xmlDocument = StorageFactory.parse(db, quoteXmlFile);
	            xmlDocument.getDocumentElement().normalize();
	            Node quoteNode = xmlDocument.getElementsByTagName(QUOTE_TAG_NAME).item(0);
	            Transformer trans = TransformerFactory.newInstance().newTransformer();
	            trans.setOutputProperty(OutputKeys.INDENT, "yes");
	            StringWriter writer = new StringWriter();
	            trans.transform(new DOMSource(xmlDocument), new StreamResult(writer));

	            JRXmlDataSource dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(quoteNode).getBytes(StandardCharsets.UTF_8)), "/quote");

	            String fileKey = jasperFile.getPath();
	            JasperReport jasperReport = jasperReportMap.get(fileKey);
	            if (jasperReport == null) {
	                jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
	                jasperReportMap.put(fileKey, jasperReport);
	            }

	            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
	            JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.xpath.executer.factory", "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");

	            Map<String, Object> parameters = new HashMap<>();
	            String templateDir = new StringBuilder(resDir).toString();
	            parameters.put(PdfGeneratorConstants.LOGO_PATH_KEY, templateDir + File.separator);
	            parameters.put(PdfGeneratorConstants.MESSAGE_PATH_KEY, templateDir + File.separator);

	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

				OutputStream outStream = StorageFactory.getOutputStream(pdfFullFilename);
				JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
				assert outStream != null; // new code for S3
				outStream.close(); // new code for S3

	            log.info("PDF file '{}' produced for quote {}", pdfFullFilename, quote.getQuoteNumber());

	        } catch (IOException | JRException | TransformerException | ParserConfigurationException e) {
	            throw new BusinessException("Failed to generate a PDF file for " + pdfFilename, e);
	        } finally {
	            IOUtils.closeQuietly(reportTemplate);
	        }
	    }
	    
	   
	    public QuoteVersion produceQuotePdf(QuoteVersion quoteVersion) throws BusinessException {
	    	generateQuotePdf(quoteVersion);
	    	pdfGeneratedEventProducer.fire(quoteVersion); 
	    	return quoteVersion;
	    }
	    
	    public boolean isCpqQuotePdfExist(QuoteVersion quoteVersion) {
	        String pdfFileName = getFullPdfFilePath(quoteVersion, false);
	        File pdfFile = new File(pdfFileName);
	        return pdfFile.exists();
	    }
	    
	    public byte[] getQuotePdf(QuoteVersion quoteVersion) throws BusinessException {

	        String pdfFileName = getFullPdfFilePath(quoteVersion, false);
	        File pdfFile = new File(pdfFileName);
			if (!StorageFactory.exists(pdfFileName)) {
	            throw new BusinessException("quote PDF was not produced yet for quote {} and quoteVersion {} " +quoteVersion.getQuote().getQuoteNumber());
	        }

	        InputStream fileInputStream = null;
	        try {
	            long fileSize = pdfFile.length();
	            if (fileSize > Integer.MAX_VALUE) {
	                throw new IllegalArgumentException("File is too big to put it to buffer in memory.");
	            }
	            byte[] fileBytes = new byte[(int) fileSize];
				fileInputStream = StorageFactory.getInputStream(pdfFile);
	            fileInputStream.read(fileBytes);
				fileInputStream.close();
	            return fileBytes;

	        } catch (Exception e) {
	            log.error("Error reading quote PDF file {} contents", pdfFileName, e);

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
	    
	    public void clearExistingQuotations(QuoteVersion quoteVersion) {
	        if(quoteVersion.getQuoteArticleLines() != null) {
	             if (!quoteVersion.getQuoteArticleLines().isEmpty()) {
	                 quoteVersion.getQuoteArticleLines()
	                         .stream()
	                         .forEach(
	                        		 quoteArticleLine -> {
	                        			 quoteArticleLineService.remove(quoteArticleLine);
	                        			 quoteArticleLineService.commit();
	                        		 });
	                 quoteVersion.getQuoteArticleLines().clear();
	                 quoteVersionService.update(quoteVersion);
	                 quoteVersionService.commit();
	             }
	        }
	    }
	     
	    /**
	     * Evaluate quote template name.
	     *
	     * @param expression the expression
	     * @param quote the cpqquote
	     * @return the string
	     */
	    public String evaluateQuoteTemplateName(String expression, CpqQuote quote) {

	        try {
	            String value = ValueExpressionWrapper.evaluateExpression(expression, String.class, quote);

	            if (value != null) {
	                return StringUtils.normalizeFileName(value);
	            }
	        } catch (BusinessException e) {
	            // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
	        }

	        return null;
	    }
	    /**
	     * Find by quote number and code. 
	     */
	    /*public CpqQuote findByQuoteNumberAndCode(String quoteNumber, String code) throws BusinessException {
	        QueryBuilder qb = new QueryBuilder(CpqQuote.class, "c", null);
	        qb.addCriterion("c.quoteNumber", "=", quoteNumber, true);
	        qb.addCriterionEntity("c.code", code);
	        try {
	            return (CpqQuote) qb.getQuery(getEntityManager()).getSingleResult();
	        } catch (NoResultException e) {
	            log.info("Quote with number {} was not found. Returning null.", quoteNumber);
	            return null;
	        } catch (NonUniqueResultException e) {
	            log.info("Multiple quotes with number {} was found. Returning null.", quoteNumber);
	            return null;
	        } catch (Exception e) {
	            return null;
	        }
	    }*/

	    public boolean sendByEmail(QuoteVersion quoteVersion, String overrideEmail, EmailTemplate overrideEmailTemplate) throws BusinessException {
	    	CpqQuote quote=quoteVersion.getQuote();
			if (quote == null) {
				log.error("The quote to be sent by Email is null!!");
				return false;
			}
			if (quoteVersion.getPdfFilename() == null) {
				log.warn("The Pdf for the quote is not generated!!");
				return false;
			}
			List<String> to = new ArrayList<>();
			List<String> cc = new ArrayList<>();
			List<File> files = new ArrayList<>();

			String fullPdfFilePath = getFullPdfFilePath(quoteVersion, false);
			File file = new File(fullPdfFilePath);

			if (!file.exists()) {
				log.warn("No Pdf file exists for the quote {}", quote.getQuoteNumber());
				return false;
			}
			files.add(file);

			BillingAccount billableAccount = quote.getBillableAccount();
			Seller seller = quote.getSeller();
			EmailTemplate emailTemplate = billableAccount.getEmailTemplate();

			if (billableAccount.getContactInformation() != null) {
				to.add(billableAccount.getContactInformation().getEmail());
			}
			if (!StringUtils.isBlank(billableAccount.getCcedEmails())) {
				cc.addAll(Arrays.asList(billableAccount.getCcedEmails().split(",")));
			}
			if (overrideEmail != null) {
				to.clear();
				to.add(overrideEmail);
				cc.clear();
			}
			if(overrideEmailTemplate != null){
				emailTemplate = overrideEmailTemplate;
			}
			if (to.isEmpty() || emailTemplate == null) {
				log.warn("No Email or  EmailTemplate is configured to receive the quote!!");
				return false;
			}
			if (seller == null || seller.getContactInformation() == null) {
				log.warn("The Seller or it's contact information is null!!");
				return false;
			}

			Map<Object, Object> params = new HashMap<>();
			params.put("cpqQuote", quote);
			String languageCode = billableAccount.getCustomerAccount().getTradingLanguage().getLanguage().getLanguageCode();
			String emailSubject = internationalSettingsService.resolveSubject(emailTemplate,languageCode);
			String emailContent = internationalSettingsService.resolveEmailContent(emailTemplate,languageCode);
			String htmlContent = internationalSettingsService.resolveHtmlContent(emailTemplate,languageCode);

			String subject = ValueExpressionWrapper.evaluateExpression(emailSubject, params, String.class);
			String content = ValueExpressionWrapper.evaluateExpression(emailContent, params, String.class);
			String contentHtml = ValueExpressionWrapper.evaluateExpression(htmlContent, params, String.class);
			String from = seller.getContactInformation().getEmail();

			emailSender.send(from, Arrays.asList(from), to, cc, null, subject, content, contentHtml, files, null, false);

			return true;
		}

}
