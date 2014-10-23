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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.primefaces.component.datatable.DataTable;

@Named
@ConversationScoped
public class OneShotChargeTemplateBean extends BaseBean<OneShotChargeTemplate> {
	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link OneShotChargeTemplate} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private CatMessagesService catMessagesService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	private String descriptionFr;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public OneShotChargeTemplateBean() {
		super(OneShotChargeTemplate.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public OneShotChargeTemplate initEntity() {
		OneShotChargeTemplate oneShotChargeTemplate = super.initEntity();
		if (oneShotChargeTemplate.getId() != null) {
			for (CatMessages msg : catMessagesService.getCatMessagesList(ChargeTemplate.class
					.getSimpleName() + "_" + oneShotChargeTemplate.getId())) {
				languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
			}
		}
		return oneShotChargeTemplate;
	}

	@Override
	public DataTable search() {
		getFilters();
		if (!filters.containsKey("disabled")) {
			filters.put("disabled", false);
		}
		return super.search();
	}

	// /**
	// * Data model of entities for data table in GUI. Filters charges of Usage
	// * type.
	// *
	// * @return filtered entities.
	// */
	// // @Out(value = "oneShotChargeTemplatesForUsageType", required = false)
	// protected PaginationDataModel<OneShotChargeTemplate>
	// getDataModelForUsageType() {
	// return entities;
	// }

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it. Filters charges of Usage type.
	 * 
	 * @return
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	// @Produces
	// @Named("oneShotChargeTemplatesForUsageType")
	// public PaginationDataModel<OneShotChargeTemplate> listForUsageType() {
	// getFilters();
	// if (!filters.containsKey("disabled")) {
	// filters.put("disabled", false);
	// }
	// filters.put("oneShotChargeTemplateType",
	// OneShotChargeTemplateTypeEnum.USAGE);
	// return super.list();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) {
		String back = null;

		// check for unicity
		if (recurringChargeTemplateService.findByCode(entity.getCode(),entity.getProvider()) != null
				|| usageChargeTemplateService.findByCode(entity.getCode(),entity.getProvider()) != null) {
			messages.error(new BundleKey("messages", "commons.uniqueField.code"));
			return null;
		}

		if (entity.getId() != null) {
			for (String msgKey : languageMessagesMap.keySet()) {
				String description = languageMessagesMap.get(msgKey);
				CatMessages catMsg = catMessagesService.getCatMessages(
						ChargeTemplate.class.getSimpleName() + "_" + entity.getId(), msgKey);
				if (catMsg != null) {
					catMsg.setDescription(description);
					catMessagesService.update(catMsg);
				} else {
					CatMessages catMessages = new CatMessages(ChargeTemplate.class.getSimpleName()
							+ "_" + entity.getId(), msgKey, description);
					catMessagesService.create(catMessages);
				}
			}
			back = super.saveOrUpdate(killConversation);

		} else {
			back = super.saveOrUpdate(killConversation);
			for (String msgKey : languageMessagesMap.keySet()) {
				String description = languageMessagesMap.get(msgKey);
				CatMessages catMessages = new CatMessages(ChargeTemplate.class.getSimpleName()
						+ "_" + entity.getId(), msgKey, description);
				catMessagesService.create(catMessages);
			}
		}
		return back;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<OneShotChargeTemplate> getPersistenceService() {
		return oneShotChargeTemplateService;
	}

	public String getDescriptionFr() {
		return descriptionFr;
	}

	public void setDescriptionFr(String descriptionFr) {
		this.descriptionFr = descriptionFr;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
}
