package org.meveo.model.catalog;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "cpq_price_plan_matrix_value")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_value_sq"), })
@NamedQuery(name="PricePlanMatrixValue.findByPricePlanMatrixLine", query = "select p from PricePlanMatrixValue p where p.pricePlanMatrixLine=:pricePlanMatrixLine")
public class PricePlanMatrixValue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH })
    @JoinColumn(name = "ppm_column_id")
    @NotNull
    private PricePlanMatrixColumn pricePlanMatrixColumn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "ppml_id")
    @NotNull
    private PricePlanMatrixLine pricePlanMatrixLine;

    @Column(name = "long_value")
    private Long longValue;

    @Column (name = "double_value")
    private Double doubleValue;

    @Column (name = "string_value")
    private String stringValue;

    @Column (name = "date_value")
    private Date dateValue;

    @Column (name = "from_date_value")
    private Date fromDateValue;

    @Column (name = "to_date_value")
    private Date toDateValue;

    @Column (name = "from_double_value")
    private Double fromDoubleValue;

    @Column (name = "to_double_value")
    private Double toDoubleValue;

    public PricePlanMatrixColumn getPricePlanMatrixColumn() {
        return pricePlanMatrixColumn;
    }

    public void setPricePlanMatrixColumn(PricePlanMatrixColumn pricePlanMatrixColumn) {
        this.pricePlanMatrixColumn = pricePlanMatrixColumn;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public PricePlanMatrixLine getPricePlanMatrixLine() {
        return pricePlanMatrixLine;
    }

    public void setPricePlanMatrixLine(PricePlanMatrixLine pricePlanMatrixLine) {
        this.pricePlanMatrixLine = pricePlanMatrixLine;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Date getFromDateValue() {
        return fromDateValue;
    }

    public void setFromDateValue(Date fromDateValue) {
        this.fromDateValue = fromDateValue;
    }

    public Date getToDateValue() {
        return toDateValue;
    }

    public void setToDateValue(Date toDateValue) {
        this.toDateValue = toDateValue;
    }

    public Double getFromDoubleValue() {
        return fromDoubleValue;
    }

    public void setFromDoubleValue(Double fromDoubleValue) {
        this.fromDoubleValue = fromDoubleValue;
    }

    public Double getToDoubleValue() {
        return toDoubleValue;
    }

    public void setToDoubleValue(Double toDoubleValue) {
        this.toDoubleValue = toDoubleValue;
    }

    public boolean match(Set<AttributeValue> attributesValue) {
        return attributesValue.stream()
                .anyMatch( attributeValue -> attributeValue.getAttribute().equals(pricePlanMatrixColumn.getAttribute())
                        && pricePlanMatrixColumn.getType().valueMatch(this, attributeValue));
    }

    public boolean matchWithAllValues() {
        return pricePlanMatrixColumn.getType().matchWithAllValues(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PricePlanMatrixValue)) return false;
        PricePlanMatrixValue that = (PricePlanMatrixValue) o;
        return Objects.equals(getPricePlanMatrixLine(), that.getPricePlanMatrixLine()) &&
                Objects.equals(getLongValue(), that.getLongValue()) &&
                Objects.equals(getDoubleValue(), that.getDoubleValue()) &&
                Objects.equals(getStringValue(), that.getStringValue()) &&
                Objects.equals(getDateValue(), that.getDateValue()) &&
                Objects.equals(getFromDateValue(), that.getFromDateValue()) &&
                Objects.equals(getToDateValue(), that.getToDateValue()) &&
                Objects.equals(getFromDoubleValue(), that.getFromDoubleValue()) &&
                Objects.equals(getToDoubleValue(), that.getToDoubleValue()) &&
                Objects.equals(getPricePlanMatrixColumn(), that.getPricePlanMatrixColumn())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPricePlanMatrixLine(), getLongValue(), getDoubleValue(), getStringValue(), getDateValue(), getFromDateValue(), getToDateValue(), getFromDoubleValue(), getToDoubleValue());
    }
}
