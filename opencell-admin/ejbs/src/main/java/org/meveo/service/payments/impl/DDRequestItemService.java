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

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.*;
import org.meveo.service.base.PersistenceService;

/**
 * The Class DDRequestItemService.
 *
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class DDRequestItemService extends PersistenceService<DDRequestItem> {

  
   
    /**
     * Creates the DD request item.
     *
     * @param amountToPay the amount to pay
     * @param ddRequestLOT the dd request LOT
     * @param caFullName the ca full name
     * @param errorMsg the error msg
     * @param listAO the list AO
     * @return the DD request item
     * @throws BusinessException the business exception
     */
    public DDRequestItem createDDRequestItem(BigDecimal amountToPay, DDRequestLOT ddRequestLOT, String caFullName, String errorMsg, List<AccountOperation> listAO)
            throws BusinessException {
        CustomerAccount ca = listAO.get(0).getCustomerAccount();
        PaymentMethod pm = ca.getPreferredPaymentMethod();
        if (pm == null || pm.getPaymentType() != PaymentMethodEnum.DIRECTDEBIT
                || !(pm instanceof DDPaymentMethod)) {
            throw new BusinessException("can' create DDR Item. CA preferred PM is null or it's not a DDPayment method");
        }
        DDPaymentMethod ddPaymentMethod = (DDPaymentMethod) pm;

        DDRequestItem ddDequestItem = new DDRequestItem();
        ddDequestItem.setErrorMsg(errorMsg);
        ddDequestItem.setAmount(amountToPay);
        ddDequestItem.setDdRequestLOT(ddRequestLOT);
        ddDequestItem.setBillingAccountName(caFullName);
        ddDequestItem.setDueDate(listAO.get(0).getDueDate());
        ddDequestItem.setPaymentInfo(ddPaymentMethod.getMandateIdentification());
        ddDequestItem.setPaymentInfo1(ddPaymentMethod.getInfo1());
        ddDequestItem.setPaymentInfo2(ddPaymentMethod.getInfo2());
        ddDequestItem.setPaymentInfo3(ddPaymentMethod.getInfo3());
        ddDequestItem.setPaymentInfo4(ddPaymentMethod.getInfo4());
        ddDequestItem.setPaymentInfo5(ddPaymentMethod.getInfo5());
        ddDequestItem.setAccountOperations(listAO);
        if(listAO.size() == 1 && !StringUtils.isBlank(listAO.get(0).getReference())) {
            ddDequestItem.setReference(listAO.get(0).getReference());
        }
        create(ddDequestItem);
        for (AccountOperation ao : listAO) {
            ao.setDdRequestItem(ddDequestItem);
        }
        log.info("ddrequestItem: {} amount {} ", ddDequestItem.getId(), amountToPay);
        return ddDequestItem;
    }
}
