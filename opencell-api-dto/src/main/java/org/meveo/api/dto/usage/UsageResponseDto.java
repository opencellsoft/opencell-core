package org.meveo.api.dto.usage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class UsageResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "UsageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The list cat usage. */
    @XmlElementWrapper
    @XmlElement(name = "catUsage")
    List<CatUsageDto> listCatUsage = new ArrayList<CatUsageDto>();

    /**
     * Instantiates a new usage response dto.
     */
    public UsageResponseDto() {

    }

    /**
     * Gets the list cat usage.
     *
     * @return the listCatUsage
     */
    public List<CatUsageDto> getListCatUsage() {
        return listCatUsage;
    }

    /**
     * Sets the list cat usage.
     *
     * @param listCatUsage the listCatUsage to set
     */
    public void setListCatUsage(List<CatUsageDto> listCatUsage) {
        this.listCatUsage = listCatUsage;
    }
}