package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.message.exception.InvalidDTOException;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BaseDto implements Serializable {

	private static final long serialVersionUID = 4456089256601996946L;
	private User currentUser;

	@XmlTransient
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public void validate() throws InvalidDTOException {

	}

	@Override
	public String toString() {
		return "BaseDto [currentUser=" + currentUser + "]";
	}

}
