package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class DiscountPlanItemBean extends StatelessBaseBean<DiscountPlanItem> {

	private static final long serialVersionUID = -2345373648137067066L;

	@Inject
	private DiscountPlanItemService discountPlanItemService;

	public DiscountPlanItemBean() {
		super(DiscountPlanItem.class);
	}

	@Override
	protected IPersistenceService<DiscountPlanItem> getPersistenceService() {
		return discountPlanItemService;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// check for required fields
		if (getEntity().getOfferTemplate() == null && getEntity().getInvoiceCategory() == null
				&& getEntity().getInvoiceSubCategory() == null && getEntity().getChargeTemplate() == null) {
			messages.error(new BundleKey("messages", "message.discountPlanItem.error.requiredFields"));
			return null;
		}

		return super.saveOrUpdate(killConversation);
	}

}
