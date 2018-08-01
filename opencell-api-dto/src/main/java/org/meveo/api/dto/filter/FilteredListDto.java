package org.meveo.api.dto.filter;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class FilteredListDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "FilteredList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilteredListDto {

    /** The xml input. */
    private String xmlInput;
    
    /** The parameters. */
    private Map<String, String> parameters;
    
    /** The first row. */
    private int firstRow;
    
    /** The number of rows. */
    private int numberOfRows;

    /**
     * Gets the xml input.
     *
     * @return the xml input
     */
    public String getXmlInput() {
        return xmlInput;
    }

    /**
     * Sets the xml input.
     *
     * @param xmlInput the new xml input
     */
    public void setXmlInput(String xmlInput) {
        this.xmlInput = xmlInput;
    }

    /**
     * Gets the first row.
     *
     * @return the first row
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Sets the first row.
     *
     * @param firstRow the new first row
     */
    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * Gets the number of rows.
     *
     * @return the number of rows
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Sets the number of rows.
     *
     * @param numberOfRows the new number of rows
     */
    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the parameters
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}