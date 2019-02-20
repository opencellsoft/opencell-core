package org.meveo.api.dto.response.generic.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GenericWorkflowsResponseDto.
 */
@XmlRootElement(name = "GenericWorkflowsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericWorkflowsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1262341691039525086L;

    /** The workflows. */
    @XmlElementWrapper(name = "workflows")
    @XmlElement(name = "workflow")
    private List<GenericWorkflowDto> workflows = new ArrayList<>();

    public List<GenericWorkflowDto> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<GenericWorkflowDto> workflows) {
        this.workflows = workflows;
    }

    @Override
    public String toString() {
        return "GenericWorkflowsResponseDto [workflows=" + workflows + "]";
    }
}
