package org.meveo.model.catalog;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "cpq_price_plan_matrix_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_line_sq"), })
public class PricePlanMatrixLine extends AuditableEntity {

    @OneToOne
    @JoinColumn(name = "ppm_version_id")
    private PricePlanMatrixVersion pricePlanMatrixVersion;

    @Column( name = "description")
    private String description;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal pricetWithoutTax;

    public PricePlanMatrixVersion getPricePlanMatrixVersion() {
        return pricePlanMatrixVersion;
    }

    public void setPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        this.pricePlanMatrixVersion = pricePlanMatrixVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricetWithoutTax() {
        return pricetWithoutTax;
    }

    public void setPricetWithoutTax(BigDecimal pricetWithoutTax) {
        this.pricetWithoutTax = pricetWithoutTax;
    }
}
