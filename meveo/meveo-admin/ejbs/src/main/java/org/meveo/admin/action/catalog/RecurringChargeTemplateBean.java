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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.primefaces.component.datatable.DataTable;

/**
 * Standard backing bean for {@link RecurringChargeTemplate} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Nov 18, 2010
 * 
 */
@Named
@ConversationScoped
public class RecurringChargeTemplateBean extends BaseBean<RecurringChargeTemplate> {
	private static final long serialVersionUID = 1L;
	/**
	 * Injected @{link RecurringChargeTemplate} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private CatMessagesService catMessagesService;

	private String descriptionFr;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public RecurringChargeTemplateBean() {
		super(RecurringChargeTemplate.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("recurringChargeTemplate")
	public RecurringChargeTemplate init() {
		RecurringChargeTemplate recuChargeTemplate = initEntity();
		if (recuChargeTemplate.getId() != null) {
			for (CatMessages msg : catMessagesService.getCatMessagesList(ChargeTemplate.class
					.getSimpleName() + "_" + recuChargeTemplate.getId())) {
				languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
			}
		}
		return recuChargeTemplate;
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
	public String saveOrUpdate() {
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
			back = saveOrUpdate(entity);

		} else {
			back = saveOrUpdate(entity);
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
		return Arrays.asList("calendar");
	}

	public String getDescriptionFr() {
		return descriptionFr;
	}

	public void setDescriptionFr(String descriptionFr) {
		this.descriptionFr = descriptionFr;
	}
}
