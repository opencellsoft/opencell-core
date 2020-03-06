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

import java.util.Map;

import org.meveo.model.notification.NotificationEventTypeEnum;

@ObservableEntity
public class CustomTableEvent {

	

	public CustomTableEvent(String cetCode, Long id, Map<String, Object> values, NotificationEventTypeEnum type) {
		this.type=type;
		this.values = values;
		this.cetCode=cetCode;
		this.setId(id);
	}
	
	private Long id;
	
	private NotificationEventTypeEnum type;
	
	private String cetCode;

	private Map<String, Object> values;

	/**
	 * @return the values
	 */
	public Map<String, Object> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	/**
	 * @return the type
	 */
	public NotificationEventTypeEnum getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(NotificationEventTypeEnum type) {
		this.type = type;
	}

	/**
	 * @return the cetCode
	 */
	public String getCetCode() {
		return cetCode;
	}

	/**
	 * @param cetCode the cetCode to set
	 */
	public void setCetCode(String cetCode) {
		this.cetCode = cetCode;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
