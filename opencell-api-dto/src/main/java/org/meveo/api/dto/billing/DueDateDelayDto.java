package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.DueDateDelayEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "DueDateDelay")
@XmlAccessorType(XmlAccessType.FIELD)
public class DueDateDelayDto implements Serializable {

	private static final long serialVersionUID = -8887054188898878461L;

	private DueDateDelayEnum delayOrigin;
	private int computedDelay;
	private String delayEL;

	public DueDateDelayEnum getDelayOrigin() {
		return delayOrigin;
	}

	public void setDelayOrigin(DueDateDelayEnum delayOrigin) {
		this.delayOrigin = delayOrigin;
	}

	public int getComputedDelay() {
		return computedDelay;
	}

	public void setComputedDelay(int computedDelay) {
		this.computedDelay = computedDelay;
	}

	public String getDelayEL() {
		return delayEL;
	}

	public void setDelayEL(String delayEL) {
		this.delayEL = delayEL;
	}

	@Override
	public String toString() {
		return "DueDateDelayDto [delayOrigin=" + delayOrigin + ", computedDelay=" + computedDelay + ", delayEL="
				+ delayEL + "]";
	}

}
