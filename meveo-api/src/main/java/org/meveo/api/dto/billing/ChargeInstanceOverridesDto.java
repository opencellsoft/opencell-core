package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ChargeInstanceOverrides")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceOverridesDto implements Serializable {

	private static final long serialVersionUID = 3049268862157592555L;

	private List<ChargeInstanceOverrideDto> chargeInstanceOverride;

	public List<ChargeInstanceOverrideDto> getChargeInstanceOverride() {
		if (chargeInstanceOverride == null)
			chargeInstanceOverride = new ArrayList<ChargeInstanceOverrideDto>();
		return chargeInstanceOverride;
	}

	public void setChargeInstanceOverride(List<ChargeInstanceOverrideDto> chargeInstanceOverride) {
		this.chargeInstanceOverride = chargeInstanceOverride;
	}

	@Override
	public String toString() {
		return "ChargeInstanceOverridesDto [chargeInstanceOverride=" + chargeInstanceOverride + ", toString()=" + super.toString() + "]";
	}

}
