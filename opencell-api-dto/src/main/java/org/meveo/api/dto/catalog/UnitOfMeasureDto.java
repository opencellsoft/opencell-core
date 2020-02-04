package org.meveo.api.dto.catalog;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.catalog.UnitOfMeasure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * The Class UnitOfMeasureDto.
 *
 * @author Mounir Bahije
 */
@XmlRootElement(name = "UnitOfMeasure")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitOfMeasureDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1343916104721562522L;

    /** The symbol. */
    @XmlElement(required = true)
    private String symbol;
    
    private Long multiplicator;
    
    private String parentUOMCode;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new unitOfMeasure dto.
     */
    public UnitOfMeasureDto() {

    }

    /**
     * Instantiates a new unitOfMeasure dto.
     *
     * @param unitOfMeasure the unitOfMeasure entity
     */
	public UnitOfMeasureDto(UnitOfMeasure unitOfMeasure) {
		super(unitOfMeasure);
		symbol = unitOfMeasure.getSymbol();
		multiplicator = unitOfMeasure.getMultiplicator();
		UnitOfMeasure parentUnitOfMeasure = unitOfMeasure.getParentUnitOfMeasure();
		parentUOMCode = parentUnitOfMeasure != null ? parentUnitOfMeasure.getCode() : null;
	}

    /**
     * Gets the language descriptions.
     *
     * @return the language descriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the new language descriptions
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    /**
     *
     * @return
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     *
     * @param symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "UnitOfMeasureDto [code=" + getCode() + ", description=" + getDescription() + ", symbol=" + symbol + "]";
    }

	/**
	 * @return the multiplicator
	 */
	public Long getMultiplicator() {
		return multiplicator;
	}

	/**
	 * @param multiplicator the multiplicator to set
	 */
	public void setMultiplicator(Long multiplicator) {
		this.multiplicator = multiplicator;
	}

	/**
	 * @return the parentCode
	 */
	public String getParentUOMCode() {
		return parentUOMCode;
	}

	/**
	 * @param parentCode the parentCode to set
	 */
	public void setParentUOMCode(String parentCode) {
		this.parentUOMCode = parentCode;
	}
}