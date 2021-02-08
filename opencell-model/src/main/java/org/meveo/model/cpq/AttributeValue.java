package org.meveo.model.cpq;

import org.meveo.model.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
public class AttributeValue extends AuditableEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpq_attribute_id", nullable = false)
    protected Attribute attribute;


    @Column(name = "string_value")
    protected String stringValue;

    @Column(name = "date_value")
    protected Date dateValue;

    @Column(name = "double_value")
    protected Double doubleValue;

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AttributeValue that = (AttributeValue) o;
        return Objects.equals(attribute, that.attribute) &&
                Objects.equals(stringValue, that.stringValue) &&
                Objects.equals(dateValue, that.dateValue) &&
                Objects.equals(doubleValue, that.doubleValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attribute, stringValue, dateValue, doubleValue);
    }
}
