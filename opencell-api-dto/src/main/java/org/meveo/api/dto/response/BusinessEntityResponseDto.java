package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;

/**
 * The Class BusinessEntityResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BusinessEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessEntityResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7750620521980139640L;

    /** The business entities. */
    public List<BusinessEntityDto> businessEntities;

    /**
     * Gets the business entities.
     *
     * @return the business entities
     */
    public List<BusinessEntityDto> getBusinessEntities() {
        return businessEntities;
    }

    /**
     * Sets the business entities.
     *
     * @param businessEntities the new business entities
     */
    public void setBusinessEntities(List<BusinessEntityDto> businessEntities) {
        this.businessEntities = businessEntities;
    }

}
