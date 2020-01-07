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
