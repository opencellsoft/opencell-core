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
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.StatefulBaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.DualListModel;

@Named
@Stateful
@ConversationScoped
public class UsageChargeTemplateBean extends StatefulBaseBean<UsageChargeTemplate> {
	private static final long serialVersionUID = 1L;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;

	private DualListModel<TriggeredEDRTemplate> edrTemplates;

	@Inject
	private CatMessagesService catMessagesService;

	private String descriptionFr;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public UsageChargeTemplateBean() {
		super(UsageChargeTemplate.class);
	}

	@Override
	public UsageChargeTemplate initEntity() {
		UsageChargeTemplate usageChargeTemplate = super.initEntity();
		if (usageChargeTemplate.getId() != null) {
			for (CatMessages msg : catMessagesService
					.getCatMessagesList(ChargeTemplate.class.getSimpleName()
							+ "_" + usageChargeTemplate.getId())) {
				languageMessagesMap.put(msg.getLanguageCode(),
						msg.getDescription());
			}
		}

		return usageChargeTemplate;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "edrTemplates");
	}

	@Override
	public DataTable search() {
		getFilters();
		if (!filters.containsKey("disabled")) {
			filters.put("disabled", false);
		}
		return super.search();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @throws BusinessException
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		String back = null;

		// check for unicity
		if (oneShotChargeTemplateService.findByCode(entity.getCode(),
				entity.getProvider()) != null
				|| recurringChargeTemplateService.findByCode(entity.getCode(),
						entity.getProvider()) != null) {
			messages.error(new BundleKey("messages", "commons.uniqueField.code"));
			return null;
		}

		if (entity.getId() != null) {
			for (String msgKey : languageMessagesMap.keySet()) {
				String description = languageMessagesMap.get(msgKey);
				CatMessages catMsg = catMessagesService.getCatMessages(
						ChargeTemplate.class.getSimpleName() + "_"
								+ entity.getId(), msgKey);
				if (catMsg != null) {
					catMsg.setDescription(description);
					catMessagesService.update(catMsg);
				} else {
					CatMessages catMessages = new CatMessages(
							ChargeTemplate.class.getSimpleName() + "_"
									+ entity.getId(), msgKey, description);
					catMessagesService.create(catMessages);
				}
			}
			back = super.saveOrUpdate(killConversation);

		} else {
			back = super.saveOrUpdate(killConversation);
			for (String msgKey : languageMessagesMap.keySet()) {
				String description = languageMessagesMap.get(msgKey);
				CatMessages catMessages = new CatMessages(
						ChargeTemplate.class.getSimpleName() + "_"
								+ entity.getId(), msgKey, description);
				catMessagesService.create(catMessages);
			}
		}
		return back;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<UsageChargeTemplate> getPersistenceService() {
		return usageChargeTemplateService;
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

}
