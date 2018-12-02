package org.meveo.service.payments.impl;

import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.filter.FilterService;

/**
 *  An abstract class to centralize some common methods such as getting the list of AOs to pay.
 *  @author Said Ramli
 *  @author anasseh
 *  @lastModifiedVersion 5.3
 */
public abstract class AbstractDDRequestBuilder implements DDRequestBuilderInterface {
    
    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }

    @Override
    public List<AccountOperation> findListAoToPay(DDRequestLotOp ddrequestLotOp) throws BusinessException {
        
        FilterService filterService = (FilterService) getServiceInterface(FilterService.class.getSimpleName());
        AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());

        Date fromDueDate = ddrequestLotOp.getFromDueDate();
        Date toDueDate = ddrequestLotOp.getToDueDate();
        Filter filter = ddrequestLotOp.getFilter();

        List<AccountOperation> listAoToPay = null;
        if (filter == null) {
            if (fromDueDate == null) {
                throw new BusinessEntityException("fromDuDate is empty");
            }
            if (toDueDate == null) {
                throw new BusinessEntityException("toDueDate is empty");
            }
            if (fromDueDate.after(toDueDate)) {
                throw new BusinessEntityException("fromDueDate is after toDueDate");
            }
            listAoToPay = accountOperationService.getAOsToPayOrRefund(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate,ddrequestLotOp.getOperationCategoryToProcess());
        } else {
            listAoToPay = (List<AccountOperation>) filterService.filteredListAsObjects(filter);
        }

        return listAoToPay;
    }
    
}
