package org.meveo.admin.wf.types;

import java.util.Arrays;
import java.util.List;

import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.billing.Invoice;

@WorkflowTypeClass
public class InvoiceValidationWF extends WorkflowType<Invoice>{

	public InvoiceValidationWF(Invoice e) {
		super(e);		
	}

	@Override
	public List<String> getStatusList() {		
		return Arrays.asList("NEW","CONFORMED","VALIDATED");
	}

	@Override
	public void changeStatus(String newStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getActualStatus() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}