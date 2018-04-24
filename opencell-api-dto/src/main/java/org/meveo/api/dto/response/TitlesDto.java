package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TitlesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Titles")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitlesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8214042837650403747L;

    /** The title. */
    private List<TitleDto> title;

    /**
     * Gets the title.
     *
     * @return the title
     */
    public List<TitleDto> getTitle() {
        if (title == null)
            title = new ArrayList<TitleDto>();
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(List<TitleDto> title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TitlesDto [title=" + title + "]";
    }
}