package org.meveo.admin.wf.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.admin.wf.WorkflowType;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;

public class DunningWF extends WorkflowType<CustomerAccount> {

	public DunningWF(CustomerAccount e) {
		super(e);
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
	public void changeStatus(String newStatus) {
		entity.setDunningLevel(DunningLevelEnum.valueOf(newStatus));
		entity.setDateDunningLevel(new Date());
		

	}

	@Override
	public String getActualStatus() {
		return entity.getDunningLevel().name();
	}

}