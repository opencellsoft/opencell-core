package org.meveo.model.order;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.ProductOffering;

@Entity
@ExportIdentifier({ "orderItem.code", "productOffering.code", "productOffering.provider" })
@Table(name = "ORD_ITEM_OFFERINGS")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ORD_ITEM_OFFERINGS_SEQ")
public class OrderItemProductOffering implements IEntity {

    public OrderItemProductOffering() {

    }

    public OrderItemProductOffering(OrderItem orderItem, ProductOffering productOffering, int itemOrder) {
        this.orderItem = orderItem;
        this.productOffering = productOffering;
        this.itemOrder = itemOrder;
    }

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_ITEM_ID")
    @NotNull
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRD_OFFERING_ID")
    @NotNull
    private ProductOffering productOffering;

    @Column(name = "ITEM_ORDER", nullable = false)
    @NotNull
    private int itemOrder;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;

    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public ProductOffering getProductOffering() {
        return productOffering;
    }

    public void setProductOffering(ProductOffering productOffering) {
        this.productOffering = productOffering;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }
}