package org.meveo.apiv2.billing;

public class InvoiceNotValidated {

	private Long id;
	private String reason;
	
	public InvoiceNotValidated(Long id, String reason) {
		super();
		this.id = id;
		this.reason = reason;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
