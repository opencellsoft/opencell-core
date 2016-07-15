package org.meveo.admin.wf;

import java.util.List;

import org.meveo.model.BaseEntity;

public abstract class WorkflowType<E extends BaseEntity> implements IWorkflowType  {
	protected E entity;
	public WorkflowType(E e){
		entity = e;
	}
	public abstract List<String> getStatusList();
    public abstract void changeStatus(String newStatus);
    public abstract String getActualStatus();    
}
