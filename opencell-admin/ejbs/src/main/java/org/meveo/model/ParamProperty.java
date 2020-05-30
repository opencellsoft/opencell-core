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

package org.meveo.model;

import org.slf4j.Logger;

import java.io.Serializable;

import javax.validation.constraints.Size;


/**
 * @author Khalid HORRI
 * @lastModifiedVersion 5.2
 *
 */
public class ParamProperty implements Comparable<ParamProperty>, IEntity{

	public static final String PROPERTY_PATTERN = "^[a-zA-Z0-9_\\.]+$";

	private org.slf4j.Logger log;
	
	@Size(max=100)
	private String key;

	@Size(max = 500)
	private String value;


	private String category;

	public ParamProperty(){

	}
	public ParamProperty(org.slf4j.Logger log){
		this.log=log;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		if(log!=null)
			log.debug("setKey :"+key);
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if(log!=null)
			log.debug("setValue :"+key+" -> "+value);
		this.value = value;
	}

	@Override
	public int compareTo(ParamProperty arg0) {
		int result=0;
		if(arg0!=null){
			result=this.key.compareTo(arg0.key);
		}
		return result;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		if(log != null)
			log.debug("setCategory :"+key+" -> "+category);
		this.category = category;
	}


	@Override
	public Serializable getId() {
		return null;
	}

	@Override
	public void setId(Long id) {

	}

	@Override
	public boolean isTransient() {
		return false;
	}
}
