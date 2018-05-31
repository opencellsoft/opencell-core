package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CatMessagesDto;

/**
 * The Class CatMessagesListDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CatMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatMessagesListDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The cat message. */
    private List<CatMessagesDto> catMessage;

    /**
     * Gets the cat message.
     *
     * @return the cat message
     */
    public List<CatMessagesDto> getCatMessage() {
        if (catMessage == null) {
            catMessage = new ArrayList<CatMessagesDto>();
        }
        return catMessage;
    }

    /**
     * Sets the cat message.
     *
     * @param catMessage the new cat message
     */
    public void setCatMessage(List<CatMessagesDto> catMessage) {
        this.catMessage = catMessage;
    }


    @Override
    public String toString() {
        return "CatMessagesListDto [catMessage=" + catMessage + "]";
    }

}
