package org.meveo.admin.web.filter.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Constraint {

	@XmlAttribute
	private ConstraintType type;

	@XmlAttribute
	private PrependType prepend;
	
	@XmlValue
	private String expression;
	
	public ConstraintType getType() {
		if(type == null){
			type = ConstraintType.READ_WRITE;
		}
		return type;
	}

	public void setType(ConstraintType type) {
		this.type = type;
	}
	
	public PrependType getPrepend() {
		if(prepend == null){
			prepend = PrependType.OR;
		}
		return prepend;
	}
	
	public void setPrepend(PrependType prepend) {
		this.prepend = prepend;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
