package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.DueDateDelayEnum;

/**
 * The Class DueDateDelayDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "DueDateDelay")
@XmlAccessorType(XmlAccessType.FIELD)
public class DueDateDelayDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8887054188898878461L;

    /** The delay origin. */
    private DueDateDelayEnum delayOrigin;

    /** The computed delay. */
    private int computedDelay;

    /** The delay EL. */
    private String delayEL;

    /**
     * Gets the delay origin.
     *
     * @return the delay origin
     */
    public DueDateDelayEnum getDelayOrigin() {
        return delayOrigin;
    }

    /**
     * Sets the delay origin.
     *
     * @param delayOrigin the new delay origin
     */
    public void setDelayOrigin(DueDateDelayEnum delayOrigin) {
        this.delayOrigin = delayOrigin;
    }

    /**
     * Gets the computed delay.
     *
     * @return the computed delay
     */
    public int getComputedDelay() {
        return computedDelay;
    }

    /**
     * Sets the computed delay.
     *
     * @param computedDelay the new computed delay
     */
    public void setComputedDelay(int computedDelay) {
        this.computedDelay = computedDelay;
    }

    /**
     * Gets the delay EL.
     *
     * @return the delay EL
     */
    public String getDelayEL() {
        return delayEL;
    }

    /**
     * Sets the delay EL.
     *
     * @param delayEL the new delay EL
     */
    public void setDelayEL(String delayEL) {
        this.delayEL = delayEL;
    }

    @Override
    public String toString() {
        return "DueDateDelayDto [delayOrigin=" + delayOrigin + ", computedDelay=" + computedDelay + ", delayEL=" + delayEL + "]";
    }

}