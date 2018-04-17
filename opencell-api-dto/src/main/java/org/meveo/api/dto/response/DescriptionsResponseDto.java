package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class DescriptionsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DescriptionsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DescriptionsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The cat messages. */
    private CatMessagesListDto catMessages = new CatMessagesListDto();

    /**
     * Gets the cat messages.
     *
     * @return the cat messages
     */
    public CatMessagesListDto getCatMessages() {
        return catMessages;
    }

    /**
     * Sets the cat messages.
     *
     * @param catMessages the new cat messages
     */
    public void setCatMessages(CatMessagesListDto catMessages) {
        this.catMessages = catMessages;
    }

    @Override
    public String toString() {
        return "CatMessagesListResponseDto [catMessages=" + catMessages + "]";
    }
}
