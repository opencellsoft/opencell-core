package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.response.TitleDto;

/**
 * The Class TitleResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "TitleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1990918305354682187L;

    /** The title dto. */
    private TitleDto titleDto;

    /**
     * Gets the title dto.
     *
     * @return the title dto
     */
    public TitleDto getTitleDto() {
        return titleDto;
    }

    /**
     * Sets the title dto.
     *
     * @param titleDto the new title dto
     */
    public void setTitleDto(TitleDto titleDto) {
        this.titleDto = titleDto;
    }

    @Override
    public String toString() {
        return "TitleResponse [title=" + titleDto + ", toString()=" + super.toString() + "]";
    }
}