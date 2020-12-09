package org.meveo.api.dto.custom;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@XmlRootElement(name = "GenericCodeResponse")
@XmlAccessorType(FIELD)
public class GetGenericCodeResponseDto {

    private GenericCodeDto genericCodeDto;

    public GenericCodeDto getGenericCodeDto() {
        return genericCodeDto;
    }

    public void setGenericCodeDto(GenericCodeDto genericCodeDto) {
        this.genericCodeDto = genericCodeDto;
    }
}