package org.meveo.model.crm.wrapper;

import java.io.Serializable;

import org.meveo.model.BusinessEntity;

public class BusinessEntityWrapper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BusinessEntity businessEntity;
	private String label;
	public BusinessEntityWrapper(){}
	public BusinessEntityWrapper(BusinessEntity businessEntity){
		this.businessEntity=businessEntity;
	}
	public BusinessEntityWrapper(String label,BusinessEntity businessEntity){
		this(businessEntity);
		this.label=label;
	}
	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}
	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
