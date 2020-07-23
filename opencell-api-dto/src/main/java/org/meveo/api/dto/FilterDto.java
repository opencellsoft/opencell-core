package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.filter.Filter;

/**
 * DTO class for Filter entity
 *
 * @author Tyshan Shi
 */
@XmlRootElement(name = "Filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The shared. */
    private Boolean shared;

    /** The input xml. */
    private String inputXml;
    
    private String pollingQuery;

    /**
     * Instantiate a new Filter Dto
     */
    public FilterDto() {

    }

    /**
     * Convert Filter entity to DTO
     * 
     * @param filter Entity to convert
     */
    public FilterDto(Filter filter) {
        super(filter);

        shared = filter.getShared();
        inputXml = filter.getInputXml();
    }

    /**
     * Gets the shared.
     *
     * @return the shared
     */
    public Boolean getShared() {
        return shared;
    }

    /**
     * Sets the shared.
     *
     * @param shared the new shared
     */
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    /**
     * Gets the input xml.
     *
     * @return the input xml
     */
    public String getInputXml() {
        return inputXml;
    }

    /**
     * Sets the input xml.
     *
     * @param inputXml the new input xml
     */
    public void setInputXml(String inputXml) {
        this.inputXml = inputXml;
    }

	public String getPollingQuery() {
		return pollingQuery;
	}

	public void setPollingQuery(String pollingQuery) {
		this.pollingQuery = pollingQuery;
	}

	@Override
	public String toString() {
		return "FilterDto [shared=" + shared + ", inputXml=" + inputXml + ", pollingQuery=" + pollingQuery + "]";
	}

    
  
}