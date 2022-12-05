package org.meveo.model.cpq;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.AuditableCFEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

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

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "boolean_value")
    protected Boolean booleanValue; 

    /**
	 * @param attribute
	 * @param value
	 */
	public AttributeValue(Attribute attribute, Object value) {
		this.attribute=attribute;
		if(attribute!=null) {
			switch (attribute.getAttributeType()) {
			case BOOLEAN:
				if(value instanceof Boolean) {
					this.booleanValue=(Boolean)value;
				}
				break;
			case DATE:
				if(value instanceof Date) {
					this.dateValue=(Date)value;
				}
				break;
			case NUMERIC:
				if(value instanceof Number) {
					this.doubleValue=((Number)value).doubleValue();
				}
				break;
			default:
				this.stringValue = value.toString();
				break;
			}
		}
	}
	
	public AttributeValue() {
		super();
	}
	

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
	
	@SuppressWarnings("incomplete-switch")
	public Object getValue() {
		if(attribute.getAttributeType()!=null) {
			switch (attribute.getAttributeType()) {
				case TOTAL :
				case COUNT :
				case NUMERIC :
				case INTEGER: return this.getDoubleValue() != null;
				case LIST_MULTIPLE_TEXT:
				case LIST_TEXT:
				case EXPRESSION_LANGUAGE :
				case TEXT:	return this.getStringValue();  
				case DATE: return this.getDateValue();  
			}
		}
		return null;
	}
	
	
}
