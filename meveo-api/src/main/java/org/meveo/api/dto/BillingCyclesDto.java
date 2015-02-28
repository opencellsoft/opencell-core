package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BillingCycles")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCyclesDto implements Serializable {

	private static final long serialVersionUID = -9161253039720538973L;

	private List<BillingCycleDto> billingCycle;

	public List<BillingCycleDto> getBillingCycle() {
		if (billingCycle == null)
			billingCycle = new ArrayList<BillingCycleDto>();
		return billingCycle;
	}

	public void setBillingCycle(List<BillingCycleDto> billingCycle) {
		this.billingCycle = billingCycle;
	}

	@Override
	public String toString() {
		return "BillingCyclesDto [billingCycle=" + billingCycle + "]";
	}

}
