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
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;

/**
 * Standard backing bean for {@link InvoiceCategory} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas
 * @created Dec 15, 2010
 */
@Named
@ConversationScoped
public class InvoiceCategoryBean extends BaseBean<InvoiceCategory> {

	private static final long serialVersionUID = 1L;

	@Inject
	private CatMessagesService catMessagesService;

	/**
	 * Injected @{link InvoiceCategory} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceCategoryBean() {
		super(InvoiceCategory.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("invoiceCategory")
	public InvoiceCategory init() {
		InvoiceCategory invoicecat = initEntity();
		languageMessagesMap.clear();
		if (invoicecat.getId() != null) {
			for (CatMessages msg : catMessagesService.getCatMessagesList(InvoiceCategory.class
					.getSimpleName() + "_" + invoicecat.getId())) {
				languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
			}
		}

		return invoicecat;
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
				CatMessages catMsg = catMessagesService.getCatMessages(entity.getClass()
						.getSimpleName() + "_" + entity.getId(), msgKey);
				if (catMsg != null) {
					catMsg.setDescription(description);
					catMessagesService.update(catMsg);
				} else {
					CatMessages catMessages = new CatMessages(entity.getClass().getSimpleName()
							+ "_" + entity.getId(), msgKey, description);
					catMessagesService.create(catMessages);
				}
			}
			back = saveOrUpdate(entity);

		} else {
			back = saveOrUpdate(entity);
			for (String msgKey : languageMessagesMap.keySet()) {
				String description = languageMessagesMap.get(msgKey);
				CatMessages catMessages = new CatMessages(entity.getClass().getSimpleName() + "_"
						+ entity.getId(), msgKey, description);
				catMessagesService.create(catMessages);
			}

		}

		return back;
	}

	/**
	 * Override default list view name. (By default its class name starting
	 * lower case + 's').
	 * 
	 * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
	 */
	protected String getDefaultViewName() {
		return "invoiceCategories";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<InvoiceCategory> getPersistenceService() {
		return invoiceCategoryService;
	}

}
