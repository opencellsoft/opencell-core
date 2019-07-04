package org.meveo.api.dto.dunning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class DunningDocumentListDto.
 * 
 * @author abdelmounaim akadid
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningDocumentListDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4086241876387501134L;

    /** The list size. */
    private int listSize;

    /** The DunningDocument. */
    private List<DunningDocumentDto> dunningDocuments;

    /**
     * Gets the dunningDocuments.
     *
     * @return the dunningDocuments
     */
    public List<DunningDocumentDto> getDunningDocuments() {
        if (dunningDocuments == null) {
            dunningDocuments = new ArrayList<DunningDocumentDto>();
        }

        return dunningDocuments;
    }

    /**
     * Sets the dunningDocuments.
     *
     * @param dunningDocuments the new dunningDocuments
     */
    public void setDunningDocuments(List<DunningDocumentDto> dunningDocuments) {
        this.dunningDocuments = dunningDocuments;
    }

    /**
     * Gets the list size.
     *
     * @return the list size
     */
    public int getListSize() {
        return listSize;
    }

    /**
     * Sets the list size.
     *
     * @param listSize the new list size
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "DunningDocumentListDto [dunningDocuments=" + dunningDocuments + "]";
    }

}