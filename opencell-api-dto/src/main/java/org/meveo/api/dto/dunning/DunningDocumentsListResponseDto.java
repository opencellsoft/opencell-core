package org.meveo.api.dto.dunning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class DunningDocumentsListResponseDto.
 * 
 * @author akadid abdelmounaim
 */
@XmlRootElement(name = "DunningDocumentsListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class DunningDocumentsListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2861319507019272869L;
    
    /** The dunningDocuments. */
    public DunningDocumentListDto dunningDocuments;

    /**
     * Gets the dunningDocuments.
     *
     * @return the dunningDocuments
     */
    public DunningDocumentListDto getDunningDocuments() {
        if (dunningDocuments == null) {
            dunningDocuments = new DunningDocumentListDto();
        }
        return dunningDocuments;
    }

    /**
     * Sets the dunningDocuments.
     *
     * @param dunningDocuments the new dunningDocuments
     */
    public void setDunningDocuments(DunningDocumentListDto dunningDocuments) {
        this.dunningDocuments = dunningDocuments;
    }

    @Override
    public String toString() {
        return "DunningDocumentsListResponseDto [dunningDocuments=" + dunningDocuments + ", toString()=" + super.toString() + "]";
    }
}