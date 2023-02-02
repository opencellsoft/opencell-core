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

package org.meveo.service.payments.impl;

import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 *  An abstract class to centralize some common methods such as getting the list of AOs to pay.
 *  @author Said Ramli
 *  @author anasseh
 *  @lastModifiedVersion 10.0
 */
public abstract class AbstractDDRequestBuilder implements DDRequestBuilderInterface {
    
    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }

	@Override
	public List<Long> findListAoToPay(DDRequestLotOp ddrequestLotOp) throws BusinessException {
		
		AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(
				AccountOperationService.class.getSimpleName());

		Date fromDueDate = ddrequestLotOp.getFromDueDate();
		Date toDueDate = ddrequestLotOp.getToDueDate();

		if (fromDueDate == null) {
			throw new BusinessEntityException("fromDuDate is empty");
		}
		if (toDueDate == null) {
			throw new BusinessEntityException("toDueDate is empty");
		}
		if (fromDueDate.after(toDueDate)) {
			throw new BusinessEntityException("fromDueDate is after toDueDate");
		}
		return accountOperationService.getAOsToPayOrRefund(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate,
				ddrequestLotOp.getPaymentOrRefundEnum().getOperationCategoryToProcess(), ddrequestLotOp.getSeller());
	}
    
}
