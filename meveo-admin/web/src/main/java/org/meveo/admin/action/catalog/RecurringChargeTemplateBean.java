/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link RecurringChargeTemplate} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class RecurringChargeTemplateBean extends
		CustomFieldBean<RecurringChargeTemplate> {
	private static final long serialVersionUID = 1L;
	/**
	 * Injected @{link RecurringChargeTemplate} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;

	private DualListModel<TriggeredEDRTemplate> edrTemplates;
	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public RecurringChargeTemplateBean() {
		super(RecurringChargeTemplate.class);
	}

	@Override
	public DataTable search() {
		getFilters();
		if (!filters.containsKey("disabled")) {
			filters.put("disabled", false);
		}
		return super.search();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {

		// check for unicity
		if (oneShotChargeTemplateService.findByCode(entity.getCode(),
				entity.getProvider()) != null
				|| usageChargeTemplateService.findByCode(entity.getCode(),
						entity.getProvider()) != null) {
			messages.error(new BundleKey("messages", "commons.uniqueField.code"));
			return null;
		}

        String outcome = super.saveOrUpdate(killConversation);
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()){
            return null;
        } else {
            return outcome;
        }
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<RecurringChargeTemplate> getPersistenceService() {
		return recurringChargeTemplateService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("calendar");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	 */
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "calendar");
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	public DualListModel<TriggeredEDRTemplate> getEdrTemplatesModel() {
		if (edrTemplates == null) {
			List<TriggeredEDRTemplate> source = triggeredEDRTemplateService
					.list();
			List<TriggeredEDRTemplate> target = new ArrayList<TriggeredEDRTemplate>();
			if (getEntity().getEdrTemplates() != null) {
				target.addAll(getEntity().getEdrTemplates());
			}
			source.removeAll(target);
			edrTemplates = new DualListModel<TriggeredEDRTemplate>(source,
					target);
		}
		return edrTemplates;
	}

	public void setEdrTemplatesModel(DualListModel<TriggeredEDRTemplate> temp) {
		getEntity().setEdrTemplates(temp.getTarget());
	}
	
	@Inject
	private TriggeredEDRTemplateService edrTemplateService;
	
	public void duplicate() {
		
		if(entity!=null&&entity.getId()!=null){
			entity.getCustomFields().size();
			entity.getEdrTemplates().size();
			recurringChargeTemplateService.detach(entity);
			entity.setId(null);
			Map<String,CustomFieldInstance> customFields= entity.getCustomFields();
			entity.setCustomFields(new HashMap<String,CustomFieldInstance>());
			for(String code:customFields.keySet()){
				CustomFieldInstance instance=customFields.get(code);
				customFieldInstanceService.detach(instance);
				instance.setId(null);
				entity.getCustomFields().put(code, instance);
			}
			List<TriggeredEDRTemplate> edrTemplates=entity.getEdrTemplates();
			entity.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
			if(edrTemplates!=null&edrTemplates.size()!=0){
				for(TriggeredEDRTemplate edrTemplate:edrTemplates){
					edrTemplateService.detach(edrTemplate);
					entity.getEdrTemplates().add(edrTemplate);
				}
			}
			entity.setChargeInstances(null);
			entity.setCode(entity.getCode()+"_copy");
			try{
				recurringChargeTemplateService.create(entity);
			}catch(Exception e){
				log.error("error when duplicate recurringChargeTemplate#{0}:#{1}",entity.getCode(),e);
			}
		}
	}
	
	public boolean isUsedInSubscription() {
		return (getEntity() != null && !getEntity().isTransient() && (recurringChargeTemplateService.findByCode(
				getEntity().getCode(), getCurrentProvider()) != null)) ? true : false;
	}
	
}
