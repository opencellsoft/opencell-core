package org.meveo.admin.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.ParamProperty;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

@Named
@ConversationScoped
public class ParamActionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4570971790276879220L;

	@Inject
	protected Conversation conversation;
	
	@Inject
	private org.slf4j.Logger log;
	
	private ParamBean paramBean= ParamBean.getInstance();
	
	private List<ParamProperty> properties = null;
	
	private void beginConversation() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
	}
	
	public void preRenderView(){
		beginConversation();
	}
	
	public void reset(){
		log.debug("load properties from paramBean");
		properties = new ArrayList<ParamProperty>();
		Set<Object> keys=paramBean.getProperties().keySet();
		if(keys!=null){
			for(Object key:keys){
				ParamProperty paramProp=new ParamProperty(log);
				paramProp.setKey(key.toString());
				paramProp.setValue(paramBean.getProperties().getProperty(key.toString()));
				properties.add(paramProp);
			}
		}
	}
	
	public List<ParamProperty> getProperties(){
		if(properties==null){
			reset();
		}
		return properties;
	}
	
	public void setProperties(List<ParamProperty> properties){
		this.properties=properties;
	}
	
	public void save(){
		log.info("update and save paramBean properties "+properties.size());
		for(ParamProperty property:properties){
			log.info(property.getKey()+"->"+property.getValue());
			paramBean.setProperty(property.getKey(), property.getValue());
		}
		paramBean.saveProperties();
		reset();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "success", "properties.save.successful");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
	}
	
	public void onCellEdit(CellEditEvent event) {
	  Object oldValue = event.getOldValue();  
      Object newValue = event.getNewValue();  
      DataTable o=(DataTable) event.getSource();
      ParamProperty property=(ParamProperty) o.getRowData();
      property.setValue(newValue==null?null:newValue.toString());
      log.debug("Old: " + oldValue + ", New:" + newValue);
	}
}
