package org.meveo.api.dto.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WorkflowHistoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class WorkflowHistoryResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "WorkflowHistoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHistoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The workflow histories. */
    @XmlElementWrapper(name = "workflowHistories")
    @XmlElement(name = "workflowHistory")
    private List<WorkflowHistoryDto> workflowHistories = new ArrayList<WorkflowHistoryDto>();

    /**
     * Gets the workflow histories.
     *
     * @return the workflow histories
     */
    public List<WorkflowHistoryDto> getWorkflowHistories() {
        return workflowHistories;
    }

    /**
     * Sets the workflow histories.
     *
     * @param workflowHistories the new workflow histories
     */
    public void setWorkflowHistories(List<WorkflowHistoryDto> workflowHistories) {
        this.workflowHistories = workflowHistories;
    }

    @Override
    public String toString() {
        return "WorkflowHistoryResponseDto [workflowHistories=" + workflowHistories + "]";
    }
}