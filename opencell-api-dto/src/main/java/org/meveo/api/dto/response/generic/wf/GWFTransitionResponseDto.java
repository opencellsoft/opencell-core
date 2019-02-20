package org.meveo.api.dto.response.generic.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.generic.wf.GWFTransitionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GWFTransitionResponseDto.
 */
@XmlRootElement(name = "GWFTransitionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GWFTransitionResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9076373795496333905L;

    /** The wf transition dto. */
    private GWFTransitionDto gwfTransitionDto;

    public GWFTransitionDto getGwfTransitionDto() {
        return gwfTransitionDto;
    }

    public void setGwfTransitionDto(GWFTransitionDto gwfTransitionDto) {
        this.gwfTransitionDto = gwfTransitionDto;
    }

    @Override
    public String toString() {
        return "GWFTransitionResponseDto [gwfTransitionDto=" + gwfTransitionDto + "]";
    }
}