package org.meveo.model.order;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ServiceInstance;

/**
 * @author Edward P. Legaspi
 * @version 12 Mar 2018
 * @lastModifiedVersion 5.0
 **/
@Entity
@Table(name = "ord_order_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ord_order_history_seq"), })
public class OrderHistory extends BaseEntity {

    private static final long serialVersionUID = -12431741574625844L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_instance_id", nullable = false)
    private ServiceInstance serviceInstance;

    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 25, nullable = false)
    private OrderItemActionEnum action;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_date", nullable = false)
    private Date eventDate;

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public OrderItemActionEnum getAction() {
        return action;
    }

    public void setAction(OrderItemActionEnum action) {
        this.action = action;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

}
