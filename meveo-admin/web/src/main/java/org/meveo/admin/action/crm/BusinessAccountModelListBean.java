package org.meveo.admin.action.crm;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class BusinessAccountModelListBean extends BusinessAccountModelBean {

	private static final long serialVersionUID = 3574716066981246932L;
	
	@Override
	public String getEditViewName() {
		return "businessAccountModelDetail";
	}

}
