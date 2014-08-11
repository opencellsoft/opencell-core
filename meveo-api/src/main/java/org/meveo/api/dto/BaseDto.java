package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.message.exception.InvalidDTOException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BaseDto implements Serializable {

	private static final long serialVersionUID = 4456089256601996946L;
	private User currentUser;
	private String requestId;

	@XmlTransient
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return StringUtils.concat("BaseDTO [", getClass().getName(), "] { ",
				innerString(), " }");
	}

	protected String innerString() {
		return StringUtils.concat("providerId=", currentUser.getProvider()
				.getId(), ", currentUserId=", currentUser.getId(),
				", requestId=", requestId);
	}

	public void validate() throws InvalidDTOException {

	}

}
