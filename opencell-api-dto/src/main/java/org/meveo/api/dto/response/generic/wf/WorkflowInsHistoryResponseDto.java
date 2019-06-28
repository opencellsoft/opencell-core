package org.meveo.api.dto.response.generic.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.generic.wf.WorkflowInstanceHistoryDto;
import org.meveo.api.dto.payment.WorkflowHistoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class WorkflowInsHistoryResponseDto.
 */
@XmlRootElement(name = "WorkflowInsHistoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowInsHistoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The workflow histories. */
    @XmlElementWrapper(name = "workflowInsHistories")
    @XmlElement(name = "workflowHistory")
    private List<WorkflowInstanceHistoryDto> workflowInsHistories = new ArrayList<>();

    public List<WorkflowInstanceHistoryDto> getWorkflowInsHistories() {
        return workflowInsHistories;
    }

    public void setWorkflowInsHistories(List<WorkflowInstanceHistoryDto> workflowInsHistories) {
        this.workflowInsHistories = workflowInsHistories;
    }

    @Override
    public String toString() {
        return "WorkflowInsHistoryResponseDto [workflowInsHistories=" + workflowInsHistories + "]";
    }
}