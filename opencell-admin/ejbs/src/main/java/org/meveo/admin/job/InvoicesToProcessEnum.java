package org.meveo.admin.job;

/**
 * 
 * Enum of different way to process invoices (PDF or XML) job :
 * 
 * Possible values are : - FinalOnly - DraftOnly - All
 * 
 * @author Said Ramli
 */
public enum InvoicesToProcessEnum {

	FinalOnly, DraftOnly, All;
	
	public String getLabel() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

}
