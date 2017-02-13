package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DualListModel;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class ProductChargeTemplateBean extends BaseBean<ProductChargeTemplate> {

	private static final long serialVersionUID = -1167691337353764450L;

	@Inject
	protected ProductChargeTemplateService productChargeTemplateService;

	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;
	
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	private DualListModel<TriggeredEDRTemplate> edrTemplatesDM;

	public ProductChargeTemplateBean() {
		super(ProductChargeTemplate.class);
	}

	@Override
	protected IPersistenceService<ProductChargeTemplate> getPersistenceService() {
		return productChargeTemplateService;
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// check for unicity
		if (oneShotChargeTemplateService.findByCode(entity.getCode(), entity.getProvider()) != null
				|| usageChargeTemplateService.findByCode(entity.getCode(), entity.getProvider()) != null
				|| recurringChargeTemplateService.findByCode(entity.getCode(), entity.getProvider()) != null) {
			messages.error(new BundleKey("messages", "chargeTemplate.uniqueField.code"));
			return null;
		}

		getEntity().getEdrTemplates().clear();
		getEntity().getEdrTemplates().addAll(triggeredEDRTemplateService.refreshOrRetrieve(edrTemplatesDM.getTarget()));

		boolean newEntity = (entity.getId() == null);
		
		String outcome = super.saveOrUpdate(killConversation);

		if (outcome != null) {
			return newEntity ? getEditViewName() : outcome;
		}
		
		return null;
	}

	public DualListModel<TriggeredEDRTemplate> getEdrTemplatesDM() {
		if (edrTemplatesDM == null) {
			List<TriggeredEDRTemplate> source = triggeredEDRTemplateService.list();
			List<TriggeredEDRTemplate> target = new ArrayList<TriggeredEDRTemplate>();
			if (getEntity().getEdrTemplates() != null) {
				target.addAll(getEntity().getEdrTemplates());
			}

			source.removeAll(target);
			edrTemplatesDM = new DualListModel<TriggeredEDRTemplate>(source, target);
		}
		return edrTemplatesDM;
	}

	public void setEdrTemplatesDM(DualListModel<TriggeredEDRTemplate> edrTemplatesDM) {
		this.edrTemplatesDM = edrTemplatesDM;
	}

}
