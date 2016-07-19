package org.meveo.api.dto.wf;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WorkflowsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:11:07 AM
 *
 */
@XmlRootElement(name="WorkflowsResponse")
public class WorkflowsResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1262341691039525086L;
	private WorkflowsDto workflowsDto;
	public WorkflowsDto getWorkflowsDto() {
		return workflowsDto;
	}
	public void setWorkflowsDto(WorkflowsDto workflowsDto) {
		this.workflowsDto = workflowsDto;
	}
	
}

