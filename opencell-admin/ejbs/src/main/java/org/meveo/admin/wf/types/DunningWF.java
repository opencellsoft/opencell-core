package org.meveo.admin.wf.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;

@WorkflowTypeClass
public class DunningWF extends WorkflowType<CustomerAccount> {
	
	public DunningWF(){
		super();
	}
	
	public DunningWF(CustomerAccount ca){
		super(ca);
	}
	

    @Override
    public List<String> getStatusList() {
        // return Arrays.asList(Arrays.stream(DunningLevelEnum.values()).map(Enum::name).toArray(String[]::new));
        List<String> values = new ArrayList<String>();
        for (DunningLevelEnum dunningLevelEnum : DunningLevelEnum.values()) {
            values.add(dunningLevelEnum.name());
        }
        return values;
    }

    @Override
    public void changeStatus(String newStatus) throws BusinessException {
        entity.setDunningLevel(DunningLevelEnum.valueOf(newStatus));
    }

    @Override
    public String getActualStatus() {
        return entity.getDunningLevel().name();
    }
}