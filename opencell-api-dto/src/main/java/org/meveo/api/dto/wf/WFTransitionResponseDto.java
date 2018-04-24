package org.meveo.api.dto.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class WFTransitionResponseDto.
 *
 * @author TyshanaShi(tyshan@manaty.net)
 */
@XmlRootElement(name = "WFTransitionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WFTransitionResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9076373795496333905L;
    
    /** The wf transition dto. */
    private WFTransitionDto wfTransitionDto;

    /**
     * Gets the wf transition dto.
     *
     * @return the wfTransitionDto
     */
    public WFTransitionDto getWfTransitionDto() {
        return wfTransitionDto;
    }

    /**
     * Sets the wf transition dto.
     *
     * @param wfTransitionDto the wfTransitionDto to set
     */
    public void setWfTransitionDto(WFTransitionDto wfTransitionDto) {
        this.wfTransitionDto = wfTransitionDto;
    }

    @Override
    public String toString() {
        return "WFTransitionResponseDto [wfTransitionDto=" + wfTransitionDto + "]";
    }
}