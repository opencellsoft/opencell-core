package org.meveo.api.dto.wf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:11:07 AM
 *
 */
@XmlRootElement(name="WorkflowsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowsResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1262341691039525086L;

    @XmlElementWrapper(name="workflows")
    @XmlElement(name="workflow")
    private List<WorkflowDto> workflows = new ArrayList<WorkflowDto>();

    public List<WorkflowDto> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<WorkflowDto> workflows) {
        this.workflows = workflows;
    }
}

