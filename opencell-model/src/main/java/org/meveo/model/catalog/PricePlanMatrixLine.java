package org.meveo.model.catalog;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@ExportIdentifier({"code"})
@Table(name = "cpq_price_plan_matrix_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_line_sq"),})
@NamedQuery(name = "PricePlanMatrixLine.findByPricePlanMatrixVersion", query = "select p from PricePlanMatrixLine p where p.pricePlanMatrixVersion=:pricePlanMatrixVersion")
public class PricePlanMatrixLine extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "ppm_version_id")
    private PricePlanMatrixVersion pricePlanMatrixVersion;

    @Column(name = "description")
    private String description;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal pricetWithoutTax;

    @OneToMany(mappedBy = "pricePlanMatrixLine", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PricePlanMatrixValue> pricePlanMatrixValues = new HashSet<>();

    @Column(name = "priority")
    @NotNull
    private Integer priority = 0;

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

    public Set<PricePlanMatrixValue> getPricePlanMatrixValues() {
        return pricePlanMatrixValues;
    }

    public void setPricePlanMatrixValues(Set<PricePlanMatrixValue> pricePlanMatrixValues) {
        this.pricePlanMatrixValues = pricePlanMatrixValues;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority != null ? priority : 0;
    }

    public boolean match(Set<AttributeValue> attributeValues) {
        return pricePlanMatrixValues.stream()
                .allMatch(v -> v.match(attributeValues));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PricePlanMatrixLine)) return false;
        if (!super.equals(o)) return false;
        PricePlanMatrixLine that = (PricePlanMatrixLine) o;
        return Objects.equals(getPricePlanMatrixVersion(), that.getPricePlanMatrixVersion()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getPricetWithoutTax(), that.getPricetWithoutTax()) &&
                Objects.equals(getPriority(), that.getPriority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPricePlanMatrixVersion(), getDescription(), getPricetWithoutTax(), getPriority());
    }
}
