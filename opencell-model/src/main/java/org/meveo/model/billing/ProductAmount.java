package org.meveo.model.billing;

import static javax.persistence.FetchType.LAZY;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.cpq.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_amount")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@Parameter(name = "sequence_name", value = "product_amount_seq"),})
@NamedQueries({
        @NamedQuery(name = "ProductAmount.deleteByBillingReport", query = "DELETE FROM ProductAmount WHERE billingRunReport.id = :billingRunReportId")
})
public class ProductAmount extends AuditableEntity {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "billing_run_report_id")
    private BillingRunReport billingRunReport;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BillingRunReport getBillingRunReport() {
        return billingRunReport;
    }

    public void setBillingRunReport(BillingRunReport billingRunReport) {
        this.billingRunReport = billingRunReport;
    }
}
