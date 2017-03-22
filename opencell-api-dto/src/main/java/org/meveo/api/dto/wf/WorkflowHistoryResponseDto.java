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

@XmlRootElement(name = "WorkflowHistoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHistoryResponseDto extends BaseResponse {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "workflowHistories")
    @XmlElement(name = "workflowHistory")
    private List<WorkflowHistoryDto> workflowHistories = new ArrayList<WorkflowHistoryDto>();

    public List<WorkflowHistoryDto> getWorkflowHistories() {
        return workflowHistories;
    }

    public void setWorkflowHistories(List<WorkflowHistoryDto> workflowHistories) {
        this.workflowHistories = workflowHistories;
    }

    @Override
    public String toString() {
        return "WorkflowHistoryResponseDto [workflowHistories=" + workflowHistories + "]";
    }
}