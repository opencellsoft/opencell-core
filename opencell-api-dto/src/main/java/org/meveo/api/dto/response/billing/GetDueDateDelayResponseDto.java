package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.billing.DueDateDelayDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
public class GetDueDateDelayResponseDto extends BaseResponse {

	private static final long serialVersionUID = -7106268154657860158L;
	
	private DueDateDelayDto dueDateDelay;

	public DueDateDelayDto getDueDateDelay() {
		return dueDateDelay;
	}

	public void setDueDateDelay(DueDateDelayDto dueDateDelay) {
		this.dueDateDelay = dueDateDelay;
	}

	@Override
	public String toString() {
		return "GetDueDateDelayResponseDto [dueDateDelay=" + dueDateDelay + "]";
	}

}
