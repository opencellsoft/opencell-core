package org.meveo.api.dto.dunning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class DunningDocumentsDto.
 *
 * @author akadid abdelmounaim
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningDocumentsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4086241876387501134L;

    /** The dunningDocument. */
    private List<DunningDocumentDto> dunningDocument;

    /**
     * Gets the dunningDocument.
     *
     * @return the dunningDocument
     */
    public List<DunningDocumentDto> getDunningDocument() {
        if (dunningDocument == null) {
            dunningDocument = new ArrayList<>();
        }

        return dunningDocument;
    }

    /**
     * Sets the dunningDocument.
     *
     * @param dunningDocument the new subscription
     */
    public void setDunningDocument(List<DunningDocumentDto> dunningDocument) {
        this.dunningDocument = dunningDocument;
    }

    @Override
    public String toString() {
        return "DunningDocumentsDto [dunningDocument=" + dunningDocument + "]";
    }

}