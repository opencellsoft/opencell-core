package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.billing.CountersInstancesDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetCountersInstancesResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8796230062356654392L;
	
	private CountersInstancesDto countersInstances = new CountersInstancesDto();
	
	public CountersInstancesDto getCountersInstances() {
		return countersInstances;
	}
	
	public void setCountersInstances(CountersInstancesDto countersInstances) {
		this.countersInstances = countersInstances;
	}
	
	@Override
	public String toString() {
		return "GetCountersInstancesResponseDto [countersInstances=" + countersInstances + ", toString()=" + super.toString()
				+ "]";
	}

}
