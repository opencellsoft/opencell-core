package org.meveo.service.script.payment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.script.Script;

public class IngenicoReconciliationFileScript extends Script {

    public void execute(Map<String, Object> context) throws BusinessException {
		    log.info("generate reconcialition file...");
            InboundRequest inboundRequest = (InboundRequest) context.get("event");
            ParamBean parambean = ParamBean.getInstance();
            Provider provider = (Provider) context.get("CONTEXT_APP_PROVIDER");  
                String env = inboundRequest.getParameters().get("ENV");
                String reportName = inboundRequest.getParameters().get("REPORTNAME"); 
				log.info("env={}, reportName={}",env,reportName);
                String fileContent = inboundRequest.getParameters().get("FILE");
				log.info("fileContent={}",fileContent);
                String fileName=env+"_"+reportName+"_"+DateUtils.formatDateWithPattern(new Date(),"yyyyMMdd_HHmmss");
                String path =  parambean.getProperty("cre.api.download.dir","/imports/ingenico")+File.separator+fileName+".csv"; 
                String fullPath = parambean.getChrootDir(provider.getCode()) + File.separator + path;
                log.info("IngenicoReconciliationFileScript fullPath={}",fullPath);
                if(!StringUtils.isBlank(fileContent)) {
                	try (FileWriter fileWriter = new FileWriter(fullPath)) {
                		File file = new File(fullPath);
                		file.createNewFile();
                		StringBuilder sb = new StringBuilder();
                		sb.append(fileContent);
                		fileWriter.write(sb.toString()); 
                		log.info("file reconcialition generated......");
                	} catch (IOException e) {
                		log.error("ingenicoReconciliation script error",e);
                	}
                } 
    }

     
}