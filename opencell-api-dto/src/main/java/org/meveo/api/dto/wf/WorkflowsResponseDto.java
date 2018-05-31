package org.meveo.api.dto.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class WorkflowsResponseDto.
 *
 * @author TyshanaShi(tyshan@manaty.net)
 */
@XmlRootElement(name = "WorkflowsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1262341691039525086L;

    /** The workflows. */
    @XmlElementWrapper(name = "workflows")
    @XmlElement(name = "workflow")
    private List<WorkflowDto> workflows = new ArrayList<WorkflowDto>();

    /**
     * Gets the workflows.
     *
     * @return the workflows
     */
    public List<WorkflowDto> getWorkflows() {
        return workflows;
    }

    /**
     * Sets the workflows.
     *
     * @param workflows the new workflows
     */
    public void setWorkflows(List<WorkflowDto> workflows) {
        this.workflows = workflows;
    }
}
