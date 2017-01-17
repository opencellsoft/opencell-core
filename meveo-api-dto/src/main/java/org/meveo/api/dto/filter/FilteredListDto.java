package org.meveo.api.dto.filter;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "FilteredList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilteredListDto {

	private String xmlInput;
	private Map<String,String> parameters;
	private int firstRow;
	private int numberOfRows;

	public String getXmlInput() {
		return xmlInput;
	}

	public void setXmlInput(String xmlInput) {
		this.xmlInput = xmlInput;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

}
