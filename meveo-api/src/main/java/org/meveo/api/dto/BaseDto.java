package org.meveo.api.dto;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BaseDto {

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
}
