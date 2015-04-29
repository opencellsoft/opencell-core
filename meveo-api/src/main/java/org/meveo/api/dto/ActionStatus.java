package org.meveo.api.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Determine the status of the MEVEO API web service response.
 * 
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ActionStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionStatus {

	/**
	 * Tells whether the instance of this <code>ActionStatus</code> object ok or
	 * not.
	 */
	@XmlElement(required = true)
	@Enumerated(EnumType.STRING)
	private ActionStatusEnum status;

	/**
	 * {@link https://www.assembla.com/spaces/meveo/wiki/Error_Codes}.
	 */
	private String errorCode;

	/**
	 * Customer message.
	 */
	@XmlElement(required = true)
	private String message;

	public ActionStatus() {
		status = ActionStatusEnum.SUCCESS;
	}

	/**
	 * Sets status and message.
	 * 
	 * @param status
	 * @param message
	 */
	public ActionStatus(ActionStatusEnum status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ActionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ActionStatusEnum status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "ActionStatus [status=" + status + ", errorCode=" + errorCode
				+ ", message=" + message + "]";
	}
}
