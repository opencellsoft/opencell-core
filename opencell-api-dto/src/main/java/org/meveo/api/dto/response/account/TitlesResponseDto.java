package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.response.TitlesDto;

/**
 * The Class TitlesResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TitlesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitlesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2597451278315980777L;

    /** The titles. */
    private TitlesDto titles = new TitlesDto();

    /**
     * Gets the titles.
     *
     * @return the titles
     */
    public TitlesDto getTitles() {
        return titles;
    }

    /**
     * Sets the titles.
     *
     * @param titles the new titles
     */
    public void setTitles(TitlesDto titles) {
        this.titles = titles;
    }

    @Override
    public String toString() {
        return "TitlesResponseDto [titles=" + titles + "]";
    }
}