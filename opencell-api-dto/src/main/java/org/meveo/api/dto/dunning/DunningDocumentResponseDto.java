package org.meveo.api.dto.dunning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class DunningDocumentsResponseDto.
 *
 * @author abdelmounaim akadid
 */
@XmlRootElement(name = "DunningDocumentsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class DunningDocumentResponseDto extends BaseResponse {

    /**
     * 
     */
    private static final long serialVersionUID = -4896934078440314084L;
    
    /** The dunningDocuments. */
    public DunningDocumentDto dunningDocument;

    /**
     * Gets the dunningDocuments.
     *
     * @return the dunningDocuments
     */
    public DunningDocumentDto getDunningDocument() {
        return dunningDocument;
    }

    /**
     * Sets the dunningDocuments.
     *
     * @param dunningDocument the new dunningDocument
     */
    public void setDunningDocument(DunningDocumentDto dunningDocument) {
        this.dunningDocument = dunningDocument;
    }

    @Override
    public String toString() {
        return "ListDunningDocumentsResponseDto [dunningDocument=" + dunningDocument + ", toString()=" + super.toString() + "]";
    }
}