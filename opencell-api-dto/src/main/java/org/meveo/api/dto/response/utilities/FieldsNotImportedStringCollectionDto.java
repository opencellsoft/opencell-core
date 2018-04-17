package org.meveo.api.dto.response.utilities;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class FieldsNotImportedStringCollectionDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "FieldsNotImportedStringCollection")
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldsNotImportedStringCollectionDto {

    /** The fields not imported. */
    private Collection<String> fieldsNotImported;

    /**
     * Instantiates a new fields not imported string collection dto.
     */
    public FieldsNotImportedStringCollectionDto() {

    }

    /**
     * Instantiates a new fields not imported string collection dto.
     *
     * @param fieldsNotImported the fields not imported
     */
    public FieldsNotImportedStringCollectionDto(Collection<String> fieldsNotImported) {
        this.fieldsNotImported = fieldsNotImported;
    }

    /**
     * Gets the fields not imported.
     *
     * @return the fields not imported
     */
    public Collection<String> getFieldsNotImported() {
        return fieldsNotImported;
    }

    /**
     * Sets the fields not imported.
     *
     * @param fieldsNotImported the new fields not imported
     */
    public void setFieldsNotImported(Collection<String> fieldsNotImported) {
        this.fieldsNotImported = fieldsNotImported;
    }

}