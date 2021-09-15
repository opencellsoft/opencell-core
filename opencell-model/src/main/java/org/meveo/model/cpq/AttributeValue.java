package org.meveo.model.cpq;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.meveo.model.AuditableCFEntity;

@MappedSuperclass
public class AttributeValue<T extends AttributeValue> extends AuditableCFEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpq_attribute_id", nullable = false)
    protected Attribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
	protected T parentAttributeValue;

    @OneToMany(mappedBy = "parentAttributeValue", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
	protected List<T> assignedAttributeValue;


    @Column(name = "string_value")
    protected String stringValue;

    @Column(name = "date_value")
    protected Date dateValue;

    @Column(name = "double_value")
    protected Double doubleValue;

    @Type(type = "numeric_boolean")
    @Column(name = "boolean_value")
    protected Boolean booleanValue; 

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

    public T getParentAttributeValue() {
        return parentAttributeValue;
    }

    public void setParentAttributeValue(T parentAttributeValue) {
        this.parentAttributeValue = parentAttributeValue;
    }

    public List<T> getAssignedAttributeValue() {
        return assignedAttributeValue;
    }

    public void setAssignedAttributeValue(List<T> assignedAttributeValue) {
        this.assignedAttributeValue = assignedAttributeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeValue that = (AttributeValue) o;
        return Objects.equals(attribute, that.attribute) &&
                Objects.equals(parentAttributeValue, that.parentAttributeValue) &&
                Objects.equals(id, that.id) &&
                Objects.equals(assignedAttributeValue, that.assignedAttributeValue) &&
                Objects.equals(stringValue, that.stringValue) &&
                Objects.equals(dateValue, that.dateValue) &&
                Objects.equals(doubleValue, that.doubleValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attribute, parentAttributeValue, assignedAttributeValue, stringValue, dateValue, doubleValue);
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
