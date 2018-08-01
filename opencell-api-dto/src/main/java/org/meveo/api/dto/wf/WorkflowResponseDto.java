package org.meveo.api.dto.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class WorkflowResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 6:08:59 AM
 */
@XmlRootElement(name = "WorkflowResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2492883573757679482L;
    
    /** The workflow. */
    private WorkflowDto workflow;

    /**
     * Gets the workflow.
     *
     * @return the workflowDto
     */
    public WorkflowDto getWorkflow() {
        return workflow;
    }

    /**
     * Sets the workflow.
     *
     * @param workflow the workflowDto to set
     */
    public void setWorkflow(WorkflowDto workflow) {
        this.workflow = workflow;
    }

    @Override
    public String toString() {
        return "WorkflowResponseDto [workflow=" + workflow + "]";
    }
}