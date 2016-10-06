package org.meveo.admin.action.catalog;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.catalog.BusinessOfferModel;

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

	public List<BusinessOfferModel> listInstalled() {
		return businessOfferModelService.listInstalled(getCurrentProvider());
	}

}
