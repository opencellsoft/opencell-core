package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.ParamProperty;

@Named
public class ParamActionBean {

	@Inject
	protected Messages messages;
	
	@Inject
	private org.slf4j.Logger log;
	
	private ParamBean paramBean= ParamBean.getInstance();
	
	private List<ParamProperty> properties = null;
	
	public void reset(){
		log.debug("load properties from paramBean");
		properties = new ArrayList<ParamProperty>();
		Set<Object> keys=paramBean.getProperties().keySet();
		if(keys!=null){
			for(Object key:keys){
				ParamProperty paramProp=new ParamProperty();
				paramProp.setKey(key.toString());
				paramProp.setValue(paramBean.getProperties().getProperty(key.toString()));
				properties.add(paramProp);
			}
		}
		messages.info(new BundleKey("messages", "properties.reset.successful"));
	}
	
	public List<ParamProperty> getProperties(){
		if(properties==null){
			reset();
		}
		return properties;
	}
	
	public void save(){
		log.info("update and save paramBean properties");
		for(ParamProperty property:properties){
			paramBean.setProperty(property.getKey(), property.getValue());
		}
		paramBean.saveProperties();
		messages.info(new BundleKey("messages", "properties.save.successful"));
	}
}
