package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class BusinessOfferModelListBean extends BusinessOfferModelBean {

	private static final long serialVersionUID = 2963552034235893444L;

	@Override
	public String getEditViewName() {
		return "businessOfferModelDetail";
	}

}
