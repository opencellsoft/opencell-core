/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.primefaces.model.LazyDataModel;

import java.util.List;

/**
 * Standard backing bean for {@link RecordedInvoice} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class RecordedInvoiceBean extends CustomFieldBean<RecordedInvoice> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link RecordedInvoice} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private RecordedInvoiceService recordedInvoiceService;

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
	 * @return recored invoice. 
	 */
	@Produces
	@Named("recordedInvoice")
	public RecordedInvoice init() {
		return initEntity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
		entity.getCustomerAccount().getAccountOperations().add(entity);
		super.saveOrUpdate(killConversation);
		return null;
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
			recordedInvoiceService.addLitigation(entity);
			messages.info(new BundleKey("messages",
					"customerAccount.addLitigationSuccessful"));
		} catch (Exception e) {
			log.error("failed to add litigation",e);
			messages.error(e.getMessage());
		}
		return null;
	}

	public String cancelLitigation() {

		try {
			recordedInvoiceService.cancelLitigation(entity);
			messages.info(new BundleKey("messages",
					"customerAccount.cancelLitigationSuccessful"));
		} catch (Exception e) {
			log.error("error while canceling litigation",e);
			messages.error(e.getMessage());
		}
		return null;
	}

	public LazyDataModel<RecordedInvoice> getDueInvoices(DunningDocument dunningDocument){
		if (!dunningDocument.isTransient()) {
			filters.put("dunningDocument", dunningDocument);
			return getLazyDataModel();
		} else {
			return null;
		}
	}

}

