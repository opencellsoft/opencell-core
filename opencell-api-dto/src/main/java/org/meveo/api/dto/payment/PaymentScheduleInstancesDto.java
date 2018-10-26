/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class PaymentScheduleInstancesDto.
 *
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleInstancesDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstancesDto extends SearchResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623298L;

    /** The PaymentScheduleInstances Dtos. */
    @XmlElementWrapper
    @XmlElement(name = "instance")
    private List<PaymentScheduleInstanceDto> instances = new ArrayList<>();
    
    
    
    /**
     * Instantiates a new payment schedule instances dto.
     */
    public PaymentScheduleInstancesDto() {
        
    }

    /**
     * Gets the instances.
     *
     * @return the instances
     */
    public List<PaymentScheduleInstanceDto> getInstances() {
        return instances;
    }

    /**
     * Sets the instances.
     *
     * @param instances the instances to set
     */
    public void setInstances(List<PaymentScheduleInstanceDto> instances) {
        this.instances = instances;
    }
    
    

}
