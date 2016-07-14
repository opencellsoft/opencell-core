package org.meveo.admin.wf;

import java.util.List;

public interface IWorkflowType {

	public  List<String> getStatusList();
    public  void changeStatus(String newStatus);
    public  String getActualStatus();
}
