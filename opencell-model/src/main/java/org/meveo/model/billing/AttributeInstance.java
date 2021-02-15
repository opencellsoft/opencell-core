package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.OrderAttribute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "cpq_attribute_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_attribute_seq")})
public class AttributeInstance extends AttributeValue {

    @ManyToOne
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;
    @OneToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public AttributeInstance() {
    }

    public AttributeInstance(QuoteAttribute quoteAttribute) {
        attribute=quoteAttribute.getAttribute();
        stringValue=quoteAttribute.getStringValue();
        dateValue=quoteAttribute.getDateValue();
        doubleValue=quoteAttribute.getDoubleValue();
        assignedAttributeValue = quoteAttribute.getAssignedAttributeValue()
                                        .stream()
                                        .map(qa -> new AttributeInstance(new AttributeValue(qa)))
                                        .collect(Collectors.toList());
    }

    public AttributeInstance(OrderAttribute orderAttribute) {
        attribute=orderAttribute.getAttribute();
        stringValue=orderAttribute.getStringValue();
        dateValue=orderAttribute.getDateValue();
        doubleValue=orderAttribute.getDoubleValue();
        assignedAttributeValue = orderAttribute.getAssignedAttributeValue()
                .stream()
                .map(qa -> new AttributeInstance(new AttributeValue(qa)))
                .collect(Collectors.toList());
    }

    public AttributeInstance(AttributeValue attributeValue) {
        attribute=attributeValue.getAttribute();
        stringValue=attributeValue.getStringValue();
        dateValue=attributeValue.getDateValue();
        doubleValue=attributeValue.getDoubleValue();
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AttributeInstance that = (AttributeInstance) o;
        return Objects.equals(serviceInstance, that.serviceInstance) &&
                Objects.equals(subscription, that.subscription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceInstance, subscription);
    }
}
