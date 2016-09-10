package org.meveo.admin.wf;

import java.util.List;

import org.meveo.model.BaseEntity;

public interface IWorkflowType<E extends BaseEntity> {

	public  List<String> getStatusList();
    public  void changeStatus(String newStatus);
    public  String getActualStatus();
    public  E getEntity();
    
}
