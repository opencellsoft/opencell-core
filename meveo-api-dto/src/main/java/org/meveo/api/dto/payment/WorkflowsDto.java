package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Workflows")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowsDto implements Serializable{

	private static final long serialVersionUID = 5327236171806867044L;
	
	@XmlElementWrapper
    @XmlElement(name="workflowDto")
	private List<WorkflowDto> listWorkflowDto = new ArrayList<WorkflowDto>();
	
	public WorkflowsDto(){
		
	}

	/**
	 * @return the listWorkflowDto
	 */
	public List<WorkflowDto> getListWorkflowDto() {
		return listWorkflowDto;
	}

	/**
	 * @param listWorkflowDto the listWorkflowDto to set
	 */
	public void setListWorkflowDto(List<WorkflowDto> listWorkflowDto) {
		this.listWorkflowDto = listWorkflowDto;
	}
	
	
}

