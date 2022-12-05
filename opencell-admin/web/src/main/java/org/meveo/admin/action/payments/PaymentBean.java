/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.payments;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
        entity = paymentService.refreshOrRetrieve(entity);
        accountOperationService.refreshOrRetrieve(entity.getCustomerAccount().getAccountOperations()).add(entity);
        String outcome = super.saveOrUpdate(killConversation);
        if (outcome != null) {
            return getEditViewName();
        }
        return null;
    }

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Payment> getPersistenceService() {
		return paymentService;
	}



	public LazyDataModel<Payment> getDunningPayments(DunningDocument dunningDocument){
		if (!dunningDocument.isTransient()) {
			filters.put("dunningDocument", dunningDocument);
			return getLazyDataModel();
		} else {
			return null;
		}
	}
}
