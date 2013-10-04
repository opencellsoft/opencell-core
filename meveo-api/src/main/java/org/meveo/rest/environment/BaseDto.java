package org.meveo.rest.environment;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BaseDto {
	
	private Long providerId;
	private Long userId;

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
