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
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
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
    public void createDDRequestItem(BigDecimal amountToPay, DDRequestLOT ddRequestLOT, String caFullName, String errorMsg, List<AccountOperation> listAO)
            throws BusinessException {
        DDRequestItem newItem = new DDRequestItem();
    	newItem.setErrorMsg(errorMsg);
    	newItem.setAmount(amountToPay);
    	newItem.setDdRequestLOT(ddRequestLOT);
        newItem.setBillingAccountName(caFullName);
        newItem.setDueDate(listAO.get(0).getDueDate());
        newItem.setPaymentInfo(listAO.get(0).getPaymentInfo());
        newItem.setPaymentInfo1(listAO.get(0).getPaymentInfo1());
        newItem.setPaymentInfo2(listAO.get(0).getPaymentInfo2());
        newItem.setPaymentInfo3(listAO.get(0).getPaymentInfo3());
        newItem.setPaymentInfo4(listAO.get(0).getPaymentInfo4());
        newItem.setPaymentInfo5(listAO.get(0).getPaymentInfo5());
        newItem.setAccountOperations(listAO);
        if(listAO.size() == 1 && !StringUtils.isBlank(listAO.get(0).getReference())) {
            newItem.setReference(listAO.get(0).getReference());
        }
        create(newItem);
        for (AccountOperation ao : listAO) {
            ao.setDdRequestItem(newItem);
        }
        log.info("ddrequestItem: {} amount {} ", newItem.getId(), amountToPay);              
    }
}
