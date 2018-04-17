package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CatMessagesDto;

/**
 * The Class GetDescriptionsResponse.
 */
@XmlRootElement(name = "GetDescriptionsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDescriptionsResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The cat messages dto. */
    private CatMessagesDto catMessagesDto;

    /**
     * Gets the cat messages dto.
     *
     * @return the cat messages dto
     */
    public CatMessagesDto getCatMessagesDto() {
        return catMessagesDto;
    }

    /**
     * Sets the cat messages dto.
     *
     * @param catMessagesDto the new cat messages dto
     */
    public void setCatMessagesDto(CatMessagesDto catMessagesDto) {
        this.catMessagesDto = catMessagesDto;
    }

    @Override
    public String toString() {
        return "GetDescriptionsResponse [catMessagesDto=" + catMessagesDto + "]";
    }
}