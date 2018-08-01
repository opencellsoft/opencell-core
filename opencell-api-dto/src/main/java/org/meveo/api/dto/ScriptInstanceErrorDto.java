package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.ScriptInstanceError;

/**
 * The Class ScriptInstanceErrorDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ScriptInstanceError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceErrorDto {

    /** The line number. */
    @XmlAttribute(required = true)
    private long lineNumber;

    /** The column number. */
    @XmlAttribute(required = true)
    private long columnNumber;

    /** The message. */
    @XmlElement(required = true)
    private String message;

    /**
     * Instantiates a new script instance error dto.
     */
    public ScriptInstanceErrorDto() {
    }

    /**
     * Instantiates a new script instance error dto.
     *
     * @param error the error
     */
    public ScriptInstanceErrorDto(ScriptInstanceError error) {
        setLineNumber(error.getLineNumber());
        setColumnNumber(error.getColumnNumber());
        setMessage(error.getMessage());
    }

    /**
     * Gets the line number.
     *
     * @return the lineNumber
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the line number.
     *
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the column number.
     *
     * @return the columnNumber
     */
    public long getColumnNumber() {
        return columnNumber;
    }

    /**
     * Sets the column number.
     *
     * @param columnNumber the columnNumber to set
     */
    public void setColumnNumber(long columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ScriptInstanceErrorDto [lineNumber=" + lineNumber + ", columnNumber=" + columnNumber + ", message=" + message + "]";
    }

}
