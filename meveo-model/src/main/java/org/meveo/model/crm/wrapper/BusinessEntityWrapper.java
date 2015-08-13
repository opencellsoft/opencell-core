package org.meveo.model.crm.wrapper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.meveo.model.BusinessEntity;

public class BusinessEntityWrapper extends BaseWrapper implements Serializable{
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
	@Override
	public boolean isEmpty(){
		return StringUtils.isEmpty(label)&&(businessEntity==null||businessEntity.isTransient());
	}
	@Override
	public boolean isNotEmpty(){
		return !isEmpty();
	}	
	@Override
	public String toString() {
		if(StringUtils.isEmpty(label)){
			return String.valueOf(businessEntity.getId());
		}else{
			return String.format(FORMAT, label,businessEntity!=null&&businessEntity.getId()!=null?String.valueOf(businessEntity.getId()):NULL);
		}
		
	}
	public static BusinessEntity parse(String value){
		BusinessEntity entity=new BusinessEntity();
		if(value.indexOf(EQUAL)>0){
			String[] str=value.split(EQUAL);
			try{
				entity.setId(Long.parseLong(str[1]));
			}catch(Exception e){
				return null;
			}
		}else{
			try{
				entity.setId(Long.parseLong(value));
			}catch(Exception e){return null;}
		}
		return entity;
	}
}
