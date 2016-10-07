package org.meveo.api.dto.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WorkflowHistoryDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name="WorkflowHistoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHistoryResponseDto extends BaseResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<WorkflowHistoryDto>  listWorkflowHistoryDto = new ArrayList<WorkflowHistoryDto>();
	
	public WorkflowHistoryResponseDto(){
		
	}

	/**
	 * @return the listWorkflowHistoryDto
	 */
	public List<WorkflowHistoryDto> getListWorkflowHistoryDto() {
		return listWorkflowHistoryDto;
	}

	/**
	 * @param listWorkflowHistoryDto the listWorkflowHistoryDto to set
	 */
	public void setListWorkflowHistoryDto(List<WorkflowHistoryDto> listWorkflowHistoryDto) {
		this.listWorkflowHistoryDto = listWorkflowHistoryDto;
	}
	
	@Override
	public String toString() {
		return "WorkflowHistoryResponseDto [listWorkflowHistoryDto=" + listWorkflowHistoryDto + "]";
	}
	
	

}
