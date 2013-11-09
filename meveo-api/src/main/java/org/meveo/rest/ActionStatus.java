package org.meveo.rest;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 3, 2013
 **/
@XmlRootElement(name = "actionStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionStatus {

	@XmlElement(required = true)
	@Enumerated(EnumType.STRING)
	private ActionStatusEnum status;

	private int errorCode;

	@XmlElement(required = true)
	private String message;

	public ActionStatus() {
	}

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

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
