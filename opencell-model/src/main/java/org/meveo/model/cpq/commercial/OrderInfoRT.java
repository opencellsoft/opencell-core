package org.meveo.model.cpq.commercial;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.meveo.model.cpq.ProductVersion;

@Embeddable
public class OrderInfoRT {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", table = "billing_rt_detail")
    private CommercialOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_version_id", table = "billing_rt_detail")
    private ProductVersion productVersion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_lot_id", table = "billing_rt_detail")
    private OrderLot orderLot;

    public OrderInfoRT() {
    }

    public OrderInfoRT(OrderInfo orderInfo) {
        this.order = orderInfo.getOrder();
        this.productVersion = orderInfo.getProductVersion();
        this.orderLot = orderInfo.getOrderLot();    
    }

    /**
     * @return the order
     */
    public CommercialOrder getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(CommercialOrder order) {
        this.order = order;
    }

    /**
     * @return the productVersion
     */
    public ProductVersion getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    public void setOrderLot(OrderLot orderLot) {
        this.orderLot = orderLot;
    }

    public OrderLot getOrderLot() {
        return orderLot;
    }
}