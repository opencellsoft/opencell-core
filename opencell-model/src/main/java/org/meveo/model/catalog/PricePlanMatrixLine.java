package org.meveo.model.catalog;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.cpq.QuoteAttribute;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "cpq_price_plan_matrix_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_line_sq"), })
@NamedQuery(name="PricePlanMatrixLine.findByPricePlanMatrixVersion", query = "select p from PricePlanMatrixLine p where p.pricePlanMatrixVersion=:pricePlanMatrixVersion")
public class PricePlanMatrixLine extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "ppm_version_id")
    private PricePlanMatrixVersion pricePlanMatrixVersion;

    @Column( name = "description")
    private String description;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal pricetWithoutTax;

    @OneToMany(mappedBy = "pricePlanMatrixLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricePlanMatrixValue> pricePlanMatrixValues;

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

    public List<PricePlanMatrixValue> getPricePlanMatrixValues() {
        return pricePlanMatrixValues;
    }

    public void setPricePlanMatrixValues(List<PricePlanMatrixValue> pricePlanMatrixValues) {
        this.pricePlanMatrixValues = pricePlanMatrixValues;
    }

    public boolean match(List<QuoteAttribute> quoteAttributes) {
        return pricePlanMatrixValues.stream()
                .allMatch(v -> v.match(quoteAttributes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PricePlanMatrixLine that = (PricePlanMatrixLine) o;
        return Objects.equals(pricePlanMatrixVersion, that.pricePlanMatrixVersion) &&
                Objects.equals(description, that.description) &&
                Objects.equals(pricetWithoutTax, that.pricetWithoutTax) &&
                Objects.equals(pricePlanMatrixValues, that.pricePlanMatrixValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pricePlanMatrixVersion, description, pricetWithoutTax, pricePlanMatrixValues);
    }
}
