package org.meveo.service.cpq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
import org.meveo.admin.exception.ConfigurationException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.job.PdfGeneratorConstants;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.CatalogHierarchyBuilderService;
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

public class CpqQuoteService extends BusinessService<CpqQuote> {

	@Inject private CatalogHierarchyBuilderService catalogHierarchyBuilderService;
	
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
	    public String getFullXmlFilePath(CpqQuote quote, boolean createDirs) {

	        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

	        String xmlFilename = meveoDir + "quotes" + File.separator + "xml" + File.separator + quote.getXmlFilename()+".xml";

	        if (createDirs) {
	            int pos = Integer.max(xmlFilename.lastIndexOf("/"), xmlFilename.lastIndexOf("\\"));
	            String dir = xmlFilename.substring(0, pos);
	            (new File(dir)).mkdirs();
	        }

	        return xmlFilename;
	    }
	    
	    public String generateFileName(CpqQuote quote) {
	        String quoteDate = new SimpleDateFormat("ddMMyyyy").format(quote.getQuoteDate());
	        ParamBean paramBean = ParamBean.getInstance();
	        String prefix = paramBean.getProperty("quote.filename.prefix", "quote");
	        String identifier = quote.getQuoteNumber() != null ? quote.getQuoteNumber() : quote.getCode();
	        return String.format("%s_%s-%s", quoteDate, prefix, identifier);
	    }
	    
	    
	    /**
	     * Return a pdf filename that was assigned to quote
	     *
	     *
	     * @param CpqQuote CpqQuote
	     * @return Pdf file name
	     */
	    public String getOrGeneratePdfFilename(CpqQuote quote) {
	        if (quote.getPdfFilename() != null) {
	            return quote.getPdfFilename();
	        } 
	        String pdfFileName = null;   
	        if (StringUtils.isBlank(pdfFileName)) { 

	            pdfFileName = generateFileName(quote);
	        }
	        if (pdfFileName != null && !pdfFileName.toLowerCase().endsWith(".pdf")) {
	            pdfFileName = pdfFileName + ".pdf";
	        }
	        pdfFileName = StringUtils.normalizeFileName(pdfFileName);
	        return pdfFileName;
	    }
	    
	    public String getFullPdfFilePath(CpqQuote quote, boolean createDirs) {

	        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

	        String pdfFilename = meveoDir + "quotes" + File.separator + "pdf" + File.separator + getOrGeneratePdfFilename(quote);

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
	    
	    public void generateQuotePdf(CpqQuote quote) throws BusinessException {
	        log.debug("Creating pdf for quote number={} code={}", quote.getQuoteNumber(), quote.getCode());
	        ParamBean paramBean = paramBeanFactory.getInstance();
	        String quoteDir = paramBean.getChrootDir(currentUser.getProviderCode()) + File.separator;
	        String quoteXmlFileName = getFullXmlFilePath(quote, false); 
	        String QUOTE_TAG_NAME = "quote"; 
	        File quoteXmlFile = new File(quoteXmlFileName);
	        if (!quoteXmlFile.exists()) {
	            //createXmlQuote(quote);
	        }    
	        String resDir = quoteDir + "quotes"  + File.separator + "jasper";
	        String pdfFilename = getOrGeneratePdfFilename(quote);
	        quote.setPdfFilename(pdfFilename);
	        String pdfFullFilename = getFullPdfFilePath(quote, true);
	        InputStream reportTemplate = null;
	        try {
	            File destDir = new File(resDir);

	            if (!destDir.exists()) {

	                String sourcePath = Thread.currentThread().getContextClassLoader().getResource("./jasper").getPath()  + File.separator + paramBean.getProperty("provider.rootDir", "default") + File.separator+ "quote";

	                File sourceFile = new File(sourcePath);
	                if (!sourceFile.exists()) {
	                    VirtualFile vfDir = VFS
	                        .getChild("content/" + ParamBeanFactory.getAppScopeInstance().getProperty("opencell.moduleName", "opencell") + ".war/WEB-INF/classes/jasper/" + "quote");
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
	  
	            File jasperFile = new File(resDir, "quote.jasper");
	            if (!jasperFile.exists()) {
	                throw new InvoiceJasperNotFoundException("The jasper file doesn't exist.");
	            }
	            log.debug("Jasper template used: {}", jasperFile.getCanonicalPath());

	            reportTemplate = new FileInputStream(jasperFile);
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            Document xmlDocument = db.parse(quoteXmlFile);
	            xmlDocument.getDocumentElement().normalize();
	            Node quoteNode = xmlDocument.getElementsByTagName(QUOTE_TAG_NAME).item(0);
	            Transformer trans = TransformerFactory.newInstance().newTransformer();
	            trans.setOutputProperty(OutputKeys.INDENT, "yes");
	            StringWriter writer = new StringWriter();
	            trans.transform(new DOMSource(xmlDocument), new StreamResult(writer));

	            JRXmlDataSource dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(quoteNode).getBytes(StandardCharsets.UTF_8)), "/quote");

	            String fileKey = jasperFile.getPath() + jasperFile.lastModified();
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
	            
	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

	            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFullFilename);   

	            log.info("PDF file '{}' produced for quote {}", pdfFullFilename, quote.getQuoteNumber());

	        } catch (IOException | JRException | TransformerException | ParserConfigurationException | SAXException e) {
	            throw new BusinessException("Failed to generate a PDF file for " + pdfFilename, e);
	        } finally {
	            IOUtils.closeQuietly(reportTemplate);
	        }
	    }
	    
	   
	    public CpqQuote produceQuotePdf(CpqQuote quote) throws BusinessException {
	    	generateQuotePdf(quote);
	    	quote.setStatusDate(new Date()); 
	    	quote = updateNoCheck(quote);
	        return quote;
	    }
	    
	    public boolean isCpqQuotePdfExist(CpqQuote quote) {
	        String pdfFileName = getFullPdfFilePath(quote, false);
	        File pdfFile = new File(pdfFileName);
	        return pdfFile.exists();
	    }
	    
	    public byte[] getQuotePdf(CpqQuote quote) throws BusinessException {

	        String pdfFileName = getFullPdfFilePath(quote, false);
	        File pdfFile = new File(pdfFileName);
	        if (!pdfFile.exists()) {
	            throw new BusinessException("quote PDF was not produced yet for quote " + quote.getQuoteNumber());
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
}
