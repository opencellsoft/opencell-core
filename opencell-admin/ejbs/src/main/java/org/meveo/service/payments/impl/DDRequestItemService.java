package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
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
    public DDRequestItem createDDRequestItem(BigDecimal amountToPay, DDRequestLOT ddRequestLOT, String caFullName, String errorMsg, List<AccountOperation> listAO)
            throws BusinessException {
        DDRequestItem ddDequestItem = new DDRequestItem();
        ddDequestItem.setErrorMsg(errorMsg);
        ddDequestItem.setAmount(amountToPay);
        ddDequestItem.setDdRequestLOT(ddRequestLOT);
        ddDequestItem.setBillingAccountName(caFullName);
        ddDequestItem.setDueDate(listAO.get(0).getDueDate());
        ddDequestItem.setPaymentInfo(listAO.get(0).getPaymentInfo());
        ddDequestItem.setPaymentInfo1(listAO.get(0).getPaymentInfo1());
        ddDequestItem.setPaymentInfo2(listAO.get(0).getPaymentInfo2());
        ddDequestItem.setPaymentInfo3(listAO.get(0).getPaymentInfo3());
        ddDequestItem.setPaymentInfo4(listAO.get(0).getPaymentInfo4());
        ddDequestItem.setPaymentInfo5(listAO.get(0).getPaymentInfo5());
        ddDequestItem.setAccountOperations(listAO);
        create(ddDequestItem);
        for (AccountOperation ao : listAO) {
            ao.setDdRequestItem(ddDequestItem);
        }
        log.info("ddrequestItem: {} amount {} ", ddDequestItem.getId(), amountToPay);
        return ddDequestItem;
    }
}
