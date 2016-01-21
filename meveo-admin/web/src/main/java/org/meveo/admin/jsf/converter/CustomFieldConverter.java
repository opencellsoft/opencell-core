package org.meveo.admin.jsf.converter;

import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.job.Job;
import org.slf4j.Logger;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 **/
@FacesConverter("customFieldConverter")
public class CustomFieldConverter implements Converter {

	private List<String> customFields =Arrays.asList("ACCT_CUST", "ACCT_BA", "ACCT_CA", "ACCT_UA", "CHARGE", "SUB",
			"SELLER", "SERVICE", "CA", "UA", "JOB", "CE", "OFFER", "BA", "ACC", "PROVIDER", "CUST");
	
	@Inject
	private ResourceBundle resourceBundle;
	@Inject
	private Logger log;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// TODO Auto-generated method stub
		return new String(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object obj) {
		log.debug("read customField value {}",obj);
		if (obj == null || obj.toString().length() == 0) {
			return "";
		}
		String value=null;
		if(obj instanceof String){
			value=(String)obj.toString();
		}
		if(StringUtils.isEmpty(value)){
			return "";
		}
		if(value.indexOf(Job.CFT_PREFIX+"_")>=0){
			value=Job.CFT_PREFIX;
			
		}else if(value.indexOf(CustomEntityTemplate.CFT_PREFIX+"_")>=0){
			value=CustomEntityTemplate.CFT_PREFIX;
		}
		log.debug("cft 's value {}",value);
		if(customFields.contains(value)){
			return resourceBundle.getString(value);
		}
		return "";
	}

}
