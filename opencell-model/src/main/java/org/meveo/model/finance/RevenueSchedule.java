package org.meveo.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ChargeInstance;

@Entity
@Table(name = "ar_revenue_schedule", uniqueConstraints = @UniqueConstraint(columnNames = { "charge_instance_id", "revenue_date" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_revenue_schedule_seq"), })
public class RevenueSchedule extends BaseEntity {

    private static final long serialVersionUID = 7793758853731725829L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_instance_id")
    @NotNull
    private ChargeInstance chargeInstance;

    @Column(name = "revenue_date", nullable = false)
    @NotNull
    private Date revenueDate;

    @Column(name = "recognized_revenue")
    private BigDecimal recognizedRevenue = BigDecimal.ZERO;

    @Column(name = "invoiced_revenue")
    private BigDecimal invoicedRevenue = BigDecimal.ZERO;

    @Column(name = "accrued_revenue")
    private BigDecimal accruedRevenue = BigDecimal.ZERO;

    @Column(name = "deffered_revenue")
    private BigDecimal defferedRevenue = BigDecimal.ZERO;

    public ChargeInstance getChargeInstance() {
        return chargeInstance;
    }

    public void setChargeInstance(ChargeInstance chargeInstance) {
        this.chargeInstance = chargeInstance;
    }

    public Date getRevenueDate() {
        return revenueDate;
    }

    public void setRevenueDate(Date revenueDate) {
        this.revenueDate = revenueDate;
    }

    public BigDecimal getRecognizedRevenue() {
        return recognizedRevenue;
    }

    public void setRecognizedRevenue(BigDecimal recognizedRevenue) {
        this.recognizedRevenue = recognizedRevenue;
    }

    public BigDecimal getInvoicedRevenue() {
        return invoicedRevenue;
    }

    public void setInvoicedRevenue(BigDecimal invoicedRevenue) {
        this.invoicedRevenue = invoicedRevenue;
    }

    public BigDecimal getAccruedRevenue() {
        return accruedRevenue;
    }

    public void setAccruedRevenue(BigDecimal accruedRevenue) {
        this.accruedRevenue = accruedRevenue;
    }

    public BigDecimal getDefferedRevenue() {
        return defferedRevenue;
    }

    public void setDefferedRevenue(BigDecimal defferedRevenue) {
        this.defferedRevenue = defferedRevenue;
    }

}
