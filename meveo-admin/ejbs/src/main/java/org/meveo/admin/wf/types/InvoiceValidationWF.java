package org.meveo.admin.wf.types;

import java.util.Arrays;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;

@WorkflowTypeClass
public class InvoiceValidationWF extends WorkflowType<Invoice> {

    @Override
    public List<String> getStatusList() {
        return Arrays.asList("NEW", "CONFORMED", "VALIDATED");
    }

    @Override
    public void changeStatus(String newStatus, User currentUser) throws BusinessException {

    }

    @Override
    public String getActualStatus() {
        return null;
    }
}