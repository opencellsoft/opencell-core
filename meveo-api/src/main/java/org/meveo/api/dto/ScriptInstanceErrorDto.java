package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.ScriptInstanceError;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ScriptInstanceError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceErrorDto {

	@XmlAttribute(required = true)
	private long lineNumber;

	@XmlAttribute(required = true)
	private long columnNumber;

	@XmlElement(required = true)
	private String message;

	public ScriptInstanceErrorDto(){
	}



	public ScriptInstanceErrorDto(ScriptInstanceError error) {
	    setLineNumber(error.getLineNumber());
		setColumnNumber(error.getColumnNumber());
		setMessage(error.getMessage());
	}



	/**
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the columnNumber
	 */
	public long getColumnNumber() {
		return columnNumber;
	}

	/**
	 * @param columnNumber the columnNumber to set
	 */
	public void setColumnNumber(long columnNumber) {
		this.columnNumber = columnNumber;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScriptInstanceErrorDto [lineNumber=" + lineNumber + ", columnNumber=" + columnNumber + ", message=" + message + "]";
	}


}
