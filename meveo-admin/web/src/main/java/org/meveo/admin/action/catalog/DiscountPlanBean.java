package org.meveo.admin.action.catalog;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class DiscountPlanBean extends BaseBean<DiscountPlan> {

	private static final long serialVersionUID = -2345373648137067066L;

	@Inject
	private DiscountPlanService discountPlanService;

	@Inject
	private DiscountPlanItemService discountPlanItemService;

	private DiscountPlanItem discountPlanItem = new DiscountPlanItem();

	public DiscountPlanBean() {
		super(DiscountPlan.class);
	}

	@Override
	public DiscountPlan initEntity() {
		discountPlanItem.setAccountingCode(getCurrentProvider().getDiscountAccountingCode());

		return super.initEntity();
	}

	@Override
	protected IPersistenceService<DiscountPlan> getPersistenceService() {
		return discountPlanService;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		super.saveOrUpdate(killConversation);

		return "";
	}

	@Named
	@Produces
	public DiscountPlanItem getDiscountPlanItem() {
		return discountPlanItem;
	}

	public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
		this.discountPlanItem = discountPlanItem;
	}

	public void saveOrUpdateDiscountPlan() {
		if (discountPlanItem.getInvoiceCategory() == null && discountPlanItem.getInvoiceSubCategory() == null) {
			messages.error(new BundleKey("messages", "message.discountPlanItem.error.requiredFields"));
			return;
		}

		discountPlanItem.setDiscountPlan(getEntity());
		if (getEntity().getDiscountPlanItems().contains(discountPlanItem)) {
			messages.error(new BundleKey("messages", "discountPlan.discountPlanItem.unique"));
		} else {
			if (discountPlanItem.isTransient()) {
				try {
					discountPlanItemService.create(discountPlanItem);
					getEntity().getDiscountPlanItems().add(discountPlanItem);
				} catch (BusinessException e) {
					log.error(e.getMessage());
					messages.error(new BundleKey("messages", e.getMessage()));
				}
			} else {
				discountPlanItemService.update(discountPlanItem);
			}
		}

		discountPlanItem = new DiscountPlanItem();
		discountPlanItem.setAccountingCode(getCurrentProvider().getDiscountAccountingCode());
	}

	public void deleteDiscountPlan(DiscountPlanItem discountPlanItem) {
		getEntity().getDiscountPlanItems().remove(discountPlanItem);
		discountPlanItemService.remove(discountPlanItem);
	}

}
