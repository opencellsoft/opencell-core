package org.meveo.admin.wf.types;

import java.util.ArrayList;
import java.util.List;

import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.ProductOffering;

@WorkflowTypeClass
public class OfferValidatioWF extends WorkflowType<ProductOffering> {

	public OfferValidatioWF(ProductOffering e) {
		super(e);
	}

	@Override
	public List<String> getStatusList() {		
		List<String> values = new ArrayList<String>();
		for (LifeCycleStatusEnum anEnum : LifeCycleStatusEnum.values()) {
			values.add(anEnum.name());
		}
		return values;
	}

	@Override
	public void changeStatus(String newStatus) {
		entity.setLifeCycleStatus(LifeCycleStatusEnum.valueOf(newStatus));		
	}

	@Override
	public String getActualStatus() {
		return entity.getLifeCycleStatus() == null ? null : entity.getLifeCycleStatus().name();
	}

}