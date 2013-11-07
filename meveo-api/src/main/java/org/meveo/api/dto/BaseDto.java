package org.meveo.api.dto;

import java.io.Serializable;

import org.meveo.api.message.exception.InvalidDTOException;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BaseDto implements Serializable {

	private static final long serialVersionUID = 4456089256601996946L;
	private Long providerId;
	private Long currentUserId;
	private String requestId;

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Long getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Long currentUserId) {
		this.currentUserId = currentUserId;
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
		return StringUtils.concat("providerId=", providerId,
				", currentUserId=", currentUserId, ", requestId=", requestId);
	}

	public void validate() throws InvalidDTOException {

	}

}
