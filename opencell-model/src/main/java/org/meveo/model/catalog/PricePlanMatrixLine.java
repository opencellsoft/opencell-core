package org.meveo.model.catalog;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;
import org.meveo.model.cpq.AttributeValue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cpq_price_plan_matrix_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_line_sq") })
@NamedQueries({
	@NamedQuery(name = "PricePlanMatrixLine.findByPricePlanMatrixVersion", query = "select distinct(p) from PricePlanMatrixLine p left join fetch p.pricePlanMatrixValues pv where p.pricePlanMatrixVersion=:pricePlanMatrixVersion"),
    @NamedQuery(name = "PricePlanMatrixLine.findByPricePlanMatrixVersionIds", query = "select p from PricePlanMatrixLine p where p.pricePlanMatrixVersion.id in (:ppmvIds)")})
public class PricePlanMatrixLine extends AuditableEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -4919786663248378605L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH )
    @JoinColumn(name = "ppm_version_id")
    private PricePlanMatrixVersion pricePlanMatrixVersion;

    @Column(name = "description")
    private String description;

    @Column(name = "value_el")
    private String valueEL;

    @Deprecated
	@Column(name = "value", precision = NB_PRECISION, scale = NB_DECIMALS, insertable = false, updatable = false)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal priceWithoutTax;

    /**
     * Used as price and discount percentage
     */
    @Column(name = "value", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal value;

    @OneToMany(mappedBy = "pricePlanMatrixLine", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PricePlanMatrixValue> pricePlanMatrixValues = new HashSet<>();

    @Column(name = "priority")
    @NotNull
    private Integer priority = 0;
    
    public PricePlanMatrixLine() {
        super();
    }

    public PricePlanMatrixLine(Auditable auditable) {
        super(auditable);
    }

    public PricePlanMatrixLine(PricePlanMatrixLine copy) {
        this.pricePlanMatrixVersion = copy.pricePlanMatrixVersion;
        this.description = copy.description;
        this.priceWithoutTax = copy.priceWithoutTax;
        this.pricePlanMatrixValues = new HashSet<PricePlanMatrixValue>();
        this.priority = copy.priority;
        this.valueEL = copy.valueEL;
        this.value = copy.value;
    }

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

    @Deprecated
    public BigDecimal getPriceWithoutTax() {
		return value;
	}

    @Deprecated
	public void setPriceWithoutTax(BigDecimal priceWithoutTax) {
		this.value = priceWithoutTax;
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

    public boolean isDefaultLine(){
        return pricePlanMatrixValues.stream()
                .allMatch(v -> v.matchWithAllValues());
    }

    public String getValueEL() {
        return valueEL;
    }

    public void setValueEL(String priceEL) {
        this.valueEL = priceEL;
    }
    
    public boolean match(Set<AttributeValue> attributeValues) {
        return pricePlanMatrixValues.stream()
                .allMatch(v -> v.match(attributeValues));
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal price) {
        this.value = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PricePlanMatrixLine)) return false;
        PricePlanMatrixLine that = (PricePlanMatrixLine) o;
        return Objects.equals(getPricePlanMatrixVersion(), that.getPricePlanMatrixVersion()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(getValueEL(), that.getValueEL()) &&
                Objects.equals(getPriority(), that.getPriority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPricePlanMatrixVersion(), getDescription(), getValue(), getPriority());
    }
}
