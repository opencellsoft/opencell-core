package org.meveo.model.billing;


public enum AdjustmentStatusEnum {
	
	ADJUSTED,
    
	NOT_ADJUSTED,

    
	TO_ADJUST;
	
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}