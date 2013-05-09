/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.primefaces.component.datatable.DataTable;

/**
 * 
 * 
 * @author MBAREK
 * 
 */
@Named
@ConversationScoped
public class UsageChargeTemplateBean extends BaseBean<UsageChargeTemplate> {
	private static final long serialVersionUID = 1L;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

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

	public UsageChargeTemplate initEntity() {
		UsageChargeTemplate usageChargeTemplate = super.initEntity();
		if (usageChargeTemplate.getId() != null) {
			for (CatMessages msg : catMessagesService.getCatMessagesList(ChargeTemplate.class
					.getSimpleName() + "_" + usageChargeTemplate.getId())) {
				languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
			}
		}
		return usageChargeTemplate;
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
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	public String saveOrUpdate(boolean killConversation) {
		String back = null;
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
}
