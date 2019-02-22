package org.meveo.api.dto.response.generic.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GenericWorkflowResponseDto.
 */
@XmlRootElement(name = "GenericWorkflowResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericWorkflowResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2492883573757679482L;

    /** The workflow. */
    private GenericWorkflowDto genericWorkflow;

    public GenericWorkflowDto getGenericWorkflow() {
        return genericWorkflow;
    }

    public void setGenericWorkflow(GenericWorkflowDto genericWorkflow) {
        this.genericWorkflow = genericWorkflow;
    }

    @Override
    public String toString() {
        return "GenericWorkflowResponseDto [genericWorkflow=" + genericWorkflow + "]";
    }

}