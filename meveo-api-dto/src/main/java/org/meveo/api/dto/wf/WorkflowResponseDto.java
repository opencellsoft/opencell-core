package org.meveo.api.dto.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:08:59 AM
 *
 */
@XmlRootElement(name="WorkflowResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResponseDto extends BaseResponse {

	private static final long serialVersionUID = 2492883573757679482L;
	private WorkflowDto workflowDto;
	/**
	 * @return the workflowDto
	 */
	public WorkflowDto getWorkflowDto() {
		return workflowDto;
	}
	/**
	 * @param workflowDto the workflowDto to set
	 */
	public void setWorkflowDto(WorkflowDto workflowDto) {
		this.workflowDto = workflowDto;
	}

	@Override
	public String toString() {
		return "WorkflowResponseDto [workflowDto=" + workflowDto + "]";
	}
	
}

