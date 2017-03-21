package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ScriptInstanceErrorDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ScriptInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceReponseDto  extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<ScriptInstanceErrorDto> compilationErrors = new ArrayList<ScriptInstanceErrorDto>();

	public ScriptInstanceReponseDto() {
	}

	/**
	 * @return the errors
	 */
	public List<ScriptInstanceErrorDto> getCompilationErrors() {
		return compilationErrors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setCompilationErrors(List<ScriptInstanceErrorDto> errors) {
		this.compilationErrors = errors;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScriptInstanceReponseDto [errors=" + compilationErrors + ", getActionStatus()=" + getActionStatus() + "]";
	}
}
