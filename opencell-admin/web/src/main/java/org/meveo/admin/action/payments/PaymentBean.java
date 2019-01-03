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

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.Payment;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link AutomatedPayment} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentBean extends CustomFieldBean<Payment> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AutomatedPayment} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private PaymentService paymentService;
	

	@Inject
	private AccountOperationService accountOperationService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public PaymentBean() {
		super(Payment.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
		accountOperationService.refreshOrRetrieve(entity.getCustomerAccount().getAccountOperations()).add(entity);
		return super.saveOrUpdate(killConversation);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Payment> getPersistenceService() {
		return paymentService;
	}



	public LazyDataModel<Payment> getDunningPayments(DunningDocument dc){
		if (!dc.isTransient()) {
			filters.put("dunningDocument", dc);
			return getLazyDataModel();
		} else {
			return null;
		}
	}
}
