package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ParentEntitiesDto.
 *
 * @author Tony Alejandro.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParentEntitiesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The parent. */
    private List<ParentEntityDto> parent;

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public List<ParentEntityDto> getParent() {
        if (parent == null) {
            parent = new ArrayList<>();
        }
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(List<ParentEntityDto> parent) {
        this.parent = parent;
    }
}