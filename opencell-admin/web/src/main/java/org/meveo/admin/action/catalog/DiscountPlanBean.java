package org.meveo.admin.action.catalog;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 **/
@Named
@ViewScoped
public class DiscountPlanBean extends CustomFieldBean<DiscountPlan> {

    private static final long serialVersionUID = -2345373648137067066L;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private DiscountPlanItemService discountPlanItemService;

    private DiscountPlanItem discountPlanItem = new DiscountPlanItem();
	
	private int activeDPITabIndex = 0;

    public DiscountPlanBean() {
        super(DiscountPlan.class);
    }

    @Override
    public DiscountPlan initEntity() {
        discountPlanItem.setAccountingCode(appProvider.getDiscountAccountingCode());
        
        entity = super.initEntity();
        
        return entity;
    }

    @Override
    protected IPersistenceService<DiscountPlan> getPersistenceService() {
        return discountPlanService;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        boolean newEntity = (entity.getId() == null);

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return newEntity ? getEditViewName() : outcome;
        }

        return null;
        // return "/pages/catalog/discountPlans/discountPlanDetail?objectId=" + entity.getId() + "&faces-redirect=true&includeViewParams=true";
    }

    public DiscountPlanItem getDiscountPlanItem() {
		return discountPlanItem;
	}

	public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
		customFieldDataEntryBean.refreshFieldsAndActions(discountPlanItem);
		this.discountPlanItem = discountPlanItem;
	}

	@ActionMethod
	public void saveOrUpdateDiscountPlanItem() throws BusinessException {

		if (discountPlanItem.getId() != null) {
			discountPlanItem = discountPlanItemService.update(discountPlanItem);
			customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) discountPlanItem,
					discountPlanItem.isTransient());
			messages.info(new BundleKey("messages", "update.successful"));

			entity = discountPlanService.findById(entity.getId());
		} else {

			discountPlanItem.setDiscountPlan(entity);

			try {
				if (entity.getDiscountPlanItems().contains(discountPlanItem)) {
					messages.error(new BundleKey("messages", "discountPlan.discountPlanItem.unique"));
				} else {
					customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) discountPlanItem,
							discountPlanItem.isTransient());
					discountPlanItemService.create(discountPlanItem);

					messages.info(new BundleKey("messages", "save.successful"));
				}

				entity = discountPlanService.findById(entity.getId());

			} catch (BusinessException e) {
				log.error("failed to save or update discount plan", e);
				messages.error(new BundleKey("messages", e.getMessage()));
			}

		}

		activeDPITabIndex = 0;
		discountPlanItem = new DiscountPlanItem();
		discountPlanItem.setAccountingCode(appProvider.getDiscountAccountingCode());
	}

	public void newDiscountPlanItem() {
		discountPlanItem = new DiscountPlanItem();
		activeDPITabIndex = 0;
	}

	@ActionMethod
	public void deleteDiscountPlanItem(DiscountPlanItem discountPlanItem) {
		try {
			discountPlanItemService.remove(discountPlanItem.getId());
			entity = discountPlanService.findById(entity.getId());
			messages.info(new BundleKey("messages", "delete.successful"));

		} catch (Exception e) {
			messages.error(new BundleKey("messages", "error.delete.unexpected"));
		}
		newDiscountPlanItem();
	}

	public void refreshEntity() {
		entity = discountPlanService.findById(entity.getId());
	}

	public int getActiveDPITabIndex() {
		return activeDPITabIndex;
	}

	public void setActiveDPITabIndex(int activeDPITabIndex) {
		this.activeDPITabIndex = activeDPITabIndex;
	}
}
