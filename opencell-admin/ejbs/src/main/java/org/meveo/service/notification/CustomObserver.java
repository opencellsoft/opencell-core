package org.meveo.service.notification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.event.logging.LoggedEvent;
import org.meveo.event.qualifier.PDFGenerated;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
 
@Singleton
@Startup
@LoggedEvent
@Lock(LockType.READ)
public class CustomObserver {

	private static Logger log = LoggerFactory.getLogger(CustomObserver.class);

	@Inject
	CpqQuoteService cpqQuoteService;
	
	@Inject
	CustomFieldInstanceService customFieldInstanceService;
	
	@Inject
	QuoteVersionService quoteVersionService; 
	
	@Inject
	CustomEntityTemplateService customEntityTemplateService;
	
	@Inject
	CustomFieldTemplateService customFieldTemplateService; 
	
	@Inject
	CustomTableService customTableService; 
	
	
	
	private static final String CF_LAST_PRINT_REQUEST_ANNEXE_SELECTED ="CF_LAST_PRINT_REQUEST_ANNEXE_SELECTED"; 
	
	
	private String getPathById(Long annexId) {
		String queryString = "SELECT cf_path from ce_devis_annexes where id=:id";
		return (String) customEntityTemplateService.getEntityManager()
					.createNativeQuery(queryString)
					.setParameter("id", annexId)
					.getSingleResult();
	}
	
	
	public static void addAnnexToQuotePdf(String originalFilePath, String fileToInsertPath, String outputFile) {
		PdfReader originalFileReader = null;
		try {
			originalFileReader = new PdfReader(originalFilePath); 
		} catch (IOException ex) {
			log.info("addAnnexToQuotePdf : can't read original file: " + ex);
		}
		PdfReader fileToAddReader = null;
		try {
			fileToAddReader = new PdfReader(fileToInsertPath);
		} catch (IOException ex) {
			log.info("addAnnexToQuotePdf: can't read fileToInsert: " + ex);
		}
		if (originalFileReader != null && fileToAddReader != null) {
			int numberOfOriginalPages = originalFileReader.getNumberOfPages(); 
			Document document = new Document();
			 
			try {
				PdfCopy copy = new PdfCopy(document, new FileOutputStream(outputFile));
				document.open();
				for (int i =1; i <= numberOfOriginalPages; i++) {
					copy.addPage(copy.getImportedPage(originalFileReader, i));
					// add annexes
					if (i==numberOfOriginalPages) {
						for (int j = 1; j <= fileToAddReader.getNumberOfPages(); j++) {
							copy.addPage(copy.getImportedPage(fileToAddReader, j));
						}
					} 
				}
				
			} catch (DocumentException | FileNotFoundException ex) {
				log.info("addAnnexToQuotePdf: can't read output location: " + ex);
			} catch (IOException ex) { 
			}
			finally {
				originalFileReader.close();
				fileToAddReader.close();
				document.close();
	        }
		}
	}
	 
	
	public void pdfGenerated(@Observes @PDFGenerated QuoteVersion quoteVersion) throws BusinessException, IOException, DocumentException {
		String lastPrintCFValue=(String)customFieldInstanceService.getCFValue(quoteVersion, CF_LAST_PRINT_REQUEST_ANNEXE_SELECTED); 
		List<String> annexes=new ArrayList<String>();
		if(lastPrintCFValue!=null) {
		 String[] customEntityIds = lastPrintCFValue.split("\\|");
		 List<Long> ids = Arrays.asList(customEntityIds).stream().map(x -> Long.valueOf(x)).collect(Collectors.toList()); 
         for (Long id : ids) {
            String path=getPathById(id);
            log.info("annex path={}",path);
            if(path!=null) {
            	annexes.add(path);
            }
         }}
		String pdfFileName = cpqQuoteService.getFullPdfFilePath(quoteVersion, false); 
		int pos = Integer.max(pdfFileName.lastIndexOf("/"), pdfFileName.lastIndexOf("\\"));
	    String dir = pdfFileName.substring(0, pos)+File.separator+"quoteWithAnnex";
        (new File(dir)).mkdirs();
        log.info("Custom Observer : PDF generated with Annex {} ", dir+File.separator+quoteVersion.getPdfFilename());
		for(String annex:annexes) {
			addAnnexToQuotePdf(pdfFileName,annex,dir+File.separator+quoteVersion.getPdfFilename());
		}
		 
	}

}


