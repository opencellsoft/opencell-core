/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.web.filter.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import org.meveo.commons.utils.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
public class Page {

	@XmlAttribute(name = "view-id")
	private String viewId;
	@XmlElement(name = "constraint")
	private List<Constraint> constraints;
	@XmlElement(name = "param")
	private List<Param> parameters;

	private String writeConstraint;
	private String readConstraint;
	private Pattern pattern;

	public Page() {
		constraints = new ArrayList<>();
		parameters = new ArrayList<>();
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public List<Param> getParameters() {
		return parameters;
	}

	public void setParameters(List<Param> parameters) {
		this.parameters = parameters;
	}

	public Pattern getPattern() {
		if (pattern == null) {
			pattern = Pattern.compile(viewId);
		}
		return pattern;
	}
	
	public String getExpression(ConstraintType type){
		switch (type) {
		case READ:
			return getReadConstraint();
		case WRITE:
			return getWriteConstraint();
		default:
			return null;
		}
	}
	
	private String getWriteConstraint() {
		if(StringUtils.isBlank(writeConstraint)){
			ConstraintType[] types = {ConstraintType.WRITE, ConstraintType.READ_WRITE};
			writeConstraint = buildExpression(Arrays.asList(types));
		}
		return writeConstraint;
	}
	
	private String getReadConstraint() {
		if(StringUtils.isBlank(readConstraint)){
			ConstraintType[] types = {ConstraintType.READ, ConstraintType.READ_WRITE};
			readConstraint = buildExpression(Arrays.asList(types));
		}
		return readConstraint;
	}
	
	private String buildExpression(List<ConstraintType> types){
		StringBuilder expression = new StringBuilder();
		boolean empty = true;
		for (Constraint constraint : constraints) {
			if(types.contains(constraint.getType())){
				if(!empty){
					expression.append(constraint.getPrepend().value());
				}
				if(!StringUtils.isBlank(constraint.getExpression())){
					empty = false;
					expression.append(" (");
					expression.append(constraint.getExpression());
					expression.append(") ");
				}
			}
		}
		expression.append("}");
		return "#{" + expression.toString();
	}

}
