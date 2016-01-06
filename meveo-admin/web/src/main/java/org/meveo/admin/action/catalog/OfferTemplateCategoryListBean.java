package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class OfferTemplateCategoryListBean extends OfferTemplateCategoryBean {

	private static final long serialVersionUID = 5774070245274693692L;

	@Override
	public String getEditViewName() {
		return "offerTemplateCategoryDetail";
	}

}
