package org.meveo.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PPMScript extends org.meveo.service.script.Script {

	/**
	 * 
	 */
	//#{mv:executeScript(op,"org.meveo.util.PPMScript","zonegeo=ATT_ZONE_GEO&metier=ATTR_METIER")}
	private static final long serialVersionUID = -4058947880850580982L;
	private static final Logger log = LoggerFactory.getLogger(PPMScript.class);

    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("EXECUTE context {}", context);
        WalletOperation wo = (WalletOperation) context.get(Script.CONTEXT_ENTITY);
        String zoneGeoParam = (String) context.get("zonegeo");
        String metierParam = (String) context.get("metier");
        String activiteParam = (String) context.get("activite");
        
        String zoneGeoValue = null;
        String metierValue = null;
        String activiteValue = null; 
        for(AttributeInstance ai:wo.getServiceInstance().getAttributeInstances()) {
        	if(ai.getAttribute().getCode().equals(zoneGeoParam)) {
        		zoneGeoValue=ai.getStringValue();
        	}else if(ai.getAttribute().getCode().equals(metierParam)) {
        		metierValue=ai.getStringValue();
        	}else if(ai.getAttribute().getCode().equals(activiteParam)) {
        		activiteValue=ai.getStringValue();
        	}
        }
        
        //call external API with : zoneGeoValue,metierValue,activiteValue
        List<String>  apiResult=new ArrayList<String>();
        apiResult.add("1-100 : 500");
        apiResult.add("101-1000 : 800");
        String result=String.join(";",apiResult);
        context.put(Script.RESULT_VALUE,result);
         
    }
}

