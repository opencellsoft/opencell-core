package org.meveo.api.dto.tax;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.tax.TaxClass;

/**
 * DTO implementation of Tax class. Tax class
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "TaxClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxClassDto extends BusinessEntityDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     **/
    protected List<LanguageDescriptionDto> descriptionI18n;

    /**
     * Default constructor
     */
    public TaxClassDto() {
        super();
    }

    /**
     * Instantiates a new TaxClass Dto.
     *
     * @param entity The Tax class entity
     */
    public TaxClassDto(TaxClass entity) {
        super(entity);

        if (entity.getDescriptionI18n() != null) {
            this.descriptionI18n = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(entity.getDescriptionI18n());
        }
    }

    /**
     * @return descriptionI18n Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public List<LanguageDescriptionDto> getDescriptionI18n() {
        return this.descriptionI18n;
    }

    /**
     * @param descriptionI18n Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public void setDescriptionI18n(List<LanguageDescriptionDto> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

}