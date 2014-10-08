package org.meveo.model.catalog;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class UsageChargeEDRTemplate  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "QUANTITY_EL", length = 1000)
	@Size(max = 1000)
	private String quantityEl;

	@Column(name = "PARAM_1_EL", length = 1000)
	@Size(max = 1000)
	private String param1El;

	@Column(name = "PARAM_2_EL", length = 1000)
	@Size(max = 1000)
	private String param2El;

	@Column(name = "PARAM_3_EL", length = 1000)
	@Size(max = 1000)
	private String param3El;

	@Column(name = "PARAM_4_EL", length = 1000)
	@Size(max = 1000)
	private String param4El;

	public String getQuantityEl() {
		return quantityEl;
	}

	public void setQuantityEl(String quantityEl) {
		this.quantityEl = quantityEl;
	}

	public String getParam1El() {
		return param1El;
	}

	public void setParam1El(String param1El) {
		this.param1El = param1El;
	}

	public String getParam2El() {
		return param2El;
	}

	public void setParam2El(String param2El) {
		this.param2El = param2El;
	}

	public String getParam3El() {
		return param3El;
	}

	public void setParam3El(String param3El) {
		this.param3El = param3El;
	}

	public String getParam4El() {
		return param4El;
	}

	public void setParam4El(String param4El) {
		this.param4El = param4El;
	}

	

}
