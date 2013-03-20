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
package org.meveo.admin.action.payments;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.User;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * Standard backing bean for {@link RecordedInvoice} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Named
@ConversationScoped
public class RecordedInvoiceBean extends BaseBean<RecordedInvoice> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link RecordedInvoice} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private RecordedInvoiceService recordedInvoiceService;

	@Inject
	private User currentUser;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public RecordedInvoiceBean() {
		super(RecordedInvoice.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("recordedInvoice")
	public RecordedInvoice init() {
		return initEntity();
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @return
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Produces
	@Named("recordedInvoices")
	@ConversationScoped
	public PaginationDataModel<RecordedInvoice> list() {
		return super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	public String saveOrUpdate() {
		entity.getCustomerAccount().getAccountOperations().add(entity);
		return saveOrUpdate(entity);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<RecordedInvoice> getPersistenceService() {
		return recordedInvoiceService;
	}

	public String addLitigation() {
		try {
			recordedInvoiceService.addLitigation(entity, currentUser);
			messages.info(new BundleKey("messages", "customerAccount.addLitigationSuccessful"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
		return null;
	}

	public String cancelLitigation() {

		try {
			recordedInvoiceService.cancelLitigation(entity, currentUser);
			messages.info(new BundleKey("messages", "customerAccount.cancelLitigationSuccessful"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
		return null;
	}

}
