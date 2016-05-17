package org.meveo.admin.sepa;

import java.io.File;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.sepa.jaxb.Pain008;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class SepaFile {
	Logger log = LoggerFactory.getLogger(SepaFile.class);
	
	@Inject
	private SepaFileBuilder sepaFileBuilder;
	
	

	
	public String getDDFileName(DDRequestLOT ddRequestLot) {
		String fileName = ArConfig.getDDRequestFileNamePrefix() + ddRequestLot.getId();
		fileName = fileName + "_" + ddRequestLot.getProvider().getCode();
		fileName = fileName + "_" + DateUtils.formatDateWithPattern(new Date(), "yyyyMMdd") + ArConfig.getDDRequestFileNameExtension();

		String outputDir = ParamBean.getInstance().getProperty("providers.rootDir", "/tmp/meveo");

		outputDir = outputDir + File.separator + ddRequestLot.getProvider().getCode() + File.separator + ArConfig.getDDRequestOutputDirectory();
		outputDir = outputDir.replaceAll("\\..", "");

		log.info("DDRequest output directory=" + outputDir);
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return outputDir + File.separator + fileName;
	}
	
	
	
	public void exportDDRequestLot(DDRequestLOT ddRequestLot) throws Exception {
		Pain008 document = new Pain008();
		CstmrDrctDbtInitn Message = new CstmrDrctDbtInitn();
		document.setCstmrDrctDbtInitn(Message);
		document.setXmlns("urn:iso:std:iso:20022:tech:xsd:pain.008.001.02");
		sepaFileBuilder.addHeader(Message, ddRequestLot);
		for (DDRequestItem ddrequestItem : ddRequestLot.getDdrequestItems()) {
			if( ! ddrequestItem.hasError()){
				sepaFileBuilder.addPaymentInformation(Message, ddrequestItem);
			}
		}
		String schemaLocation = ParamBean.getInstance().getProperty("sepa.schemaLocation.pain008", "https://github.com/w2c/sepa-sdd-xml-generator/blob/master/validation_schemes/pain.008.001.02.xsd");
		JAXBUtils.marshaller(document, new File(ddRequestLot.getFileName()), schemaLocation);
		
	}
	

}
