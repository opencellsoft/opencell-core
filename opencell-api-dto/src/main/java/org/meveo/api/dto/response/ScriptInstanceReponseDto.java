package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ScriptInstanceErrorDto;

/**
 * The Class ScriptInstanceReponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ScriptInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceReponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The compilation errors. */
    List<ScriptInstanceErrorDto> compilationErrors = new ArrayList<ScriptInstanceErrorDto>();

    /**
     * Instantiates a new script instance reponse dto.
     */
    public ScriptInstanceReponseDto() {
    }

    /**
     * Gets the compilation errors.
     *
     * @return the errors
     */
    public List<ScriptInstanceErrorDto> getCompilationErrors() {
        return compilationErrors;
    }

    /**
     * Sets the compilation errors.
     *
     * @param errors the errors to set
     */
    public void setCompilationErrors(List<ScriptInstanceErrorDto> errors) {
        this.compilationErrors = errors;
    }

    @Override
    public String toString() {
        return "ScriptInstanceReponseDto [errors=" + compilationErrors + ", getActionStatus()=" + getActionStatus() + "]";
    }
}