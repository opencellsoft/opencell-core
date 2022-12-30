package org.meveo.model.catalog;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.AttributeValue;

@Entity
@Table(name = "cpq_price_plan_matrix_value")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_value_sq"), })
@NamedQuery(name="PricePlanMatrixValue.findByPricePlanMatrixLine", query = "select p from PricePlanMatrixValue p where p.pricePlanMatrixLine=:pricePlanMatrixLine")
public class PricePlanMatrixValue extends BaseEntity {


	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2339904876547686701L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH )
    @JoinColumn(name = "ppm_column_id")
    @NotNull
    private PricePlanMatrixColumn pricePlanMatrixColumn;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH )
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
    
    @Type(type = "numeric_boolean")
    @Column(name = "boolean_value")
    protected Boolean booleanValue; 
    
    

    public PricePlanMatrixValue() {
	}

	public PricePlanMatrixValue(PricePlanMatrixValue copy) {
		this.pricePlanMatrixColumn = copy.pricePlanMatrixColumn;
		this.pricePlanMatrixLine = copy.pricePlanMatrixLine;
		this.longValue = copy.longValue;
		this.doubleValue = copy.doubleValue;
		this.stringValue = copy.stringValue;
		this.dateValue = copy.dateValue;
		this.fromDateValue = copy.fromDateValue;
		this.toDateValue = copy.toDateValue;
		this.fromDoubleValue = copy.fromDoubleValue;
		this.toDoubleValue = copy.toDoubleValue;
		this.booleanValue = copy.booleanValue;
	}

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

	/**
	 * @return the booleanValue
	 */
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	/**
	 * @param booleanValue the booleanValue to set
	 */
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
}
