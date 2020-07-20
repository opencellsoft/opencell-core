package org.meveo.api.dto.billing;

import org.meveo.model.billing.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.math.BigDecimal;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomServiceInstanceDto {

    private static final long serialVersionUID = -4084004747483067153L;

    @XmlAttribute
    private Long id;

    @XmlAttribute(required = true)
    private String code;

    private String description;

    private InstanceStatusEnum status;

    private Date subscriptionDate;

    private Date terminationDate;

    private BigDecimal quantity;


    public CustomServiceInstanceDto(ServiceInstance serviceInstance) {
        id = serviceInstance.getId();
        code = serviceInstance.getCode();
        id = serviceInstance.getId();
        description = serviceInstance.getDescription();
        status = serviceInstance.getStatus();
        subscriptionDate = serviceInstance.getSubscriptionDate();
        terminationDate = serviceInstance.getTerminationDate();
        quantity = serviceInstance.getQuantity();
    }


    public InstanceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InstanceStatusEnum status) {
        this.status = status;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}