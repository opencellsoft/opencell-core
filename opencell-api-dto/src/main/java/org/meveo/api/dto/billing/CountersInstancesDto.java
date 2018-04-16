package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class CountersInstancesDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CountersInstancesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 49018302870831847L;

    /** The counter instance. */
    private List<CounterInstanceDto> counterInstance;

    /**
     * Gets the counter instance.
     *
     * @return the counter instance
     */
    public List<CounterInstanceDto> getCounterInstance() {
        if (counterInstance == null) {
            counterInstance = new ArrayList<CounterInstanceDto>();
        }

        return counterInstance;
    }

    /**
     * Sets the counter instance.
     *
     * @param counterInstance the new counter instance
     */
    public void setCounterInstance(List<CounterInstanceDto> counterInstance) {
        this.counterInstance = counterInstance;
    }

    @Override
    public String toString() {
        return "CountersInstancesDto [counterInstance=" + counterInstance + "]";
    }

}