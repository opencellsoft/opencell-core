package org.meveo.model.billing;

import static javax.persistence.FetchType.LAZY;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.catalog.OfferTemplate;

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
@Table(name = "billing_offer_amount")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@Parameter(name = "sequence_name", value = "bill_offer_amount_seq"), })
@NamedQueries({
        @NamedQuery(name = "OfferAmount.deleteByBillingReport", query = "DELETE FROM OfferAmount WHERE billingRunReport.id = :billingRunReportId")
})
public class OfferAmount extends AuditableEntity {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offer;

    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "billing_run_report_id")
    private BillingRunReport billingRunReport;

    public OfferTemplate getOffer() {
        return offer;
    }

    public void setOffer(OfferTemplate offer) {
        this.offer = offer;
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
