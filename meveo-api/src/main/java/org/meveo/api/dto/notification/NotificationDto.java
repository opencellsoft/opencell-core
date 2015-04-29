package org.meveo.api.dto.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.notification.Notification;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationDto extends BaseDto {

	private static final long serialVersionUID = 3931479706274647165L;

	@XmlAttribute(required = true)
	private String code;

	@XmlElement(required = true)
	private String classNameFilter;

	/**
	 * Valid values: CREATED, UPDATED, REMOVED, TERMINATED, DISABLED, PROCESSED,
	 * REJECTED, REJECTED_CDR, LOGGED_IN, INBOUND_REQ, ENABLED
	 */
	@XmlElement(required = true)
	private String eventTypeFilter;

	private String elFilter;
	private String elAction;

	private String counterTemplate;

	public NotificationDto() {

	}

	public NotificationDto(Notification e) {
		code = e.getCode();
		classNameFilter = e.getClassNameFilter();
		eventTypeFilter = e.getEventTypeFilter().name();
		elFilter = e.getElFilter();
		elAction = e.getElAction();
		if (e.getCounterTemplate() != null) {
			counterTemplate = e.getCounterTemplate().getCode();
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClassNameFilter() {
		return classNameFilter;
	}

	public void setClassNameFilter(String classNameFilter) {
		this.classNameFilter = classNameFilter;
	}

	public String getEventTypeFilter() {
		return eventTypeFilter;
	}

	public void setEventTypeFilter(String eventTypeFilter) {
		this.eventTypeFilter = eventTypeFilter;
	}

	public String getElFilter() {
		return elFilter;
	}

	public void setElFilter(String elFilter) {
		this.elFilter = elFilter;
	}

	public String getElAction() {
		return elAction;
	}

	public void setElAction(String elAction) {
		this.elAction = elAction;
	}

	@Override
	public String toString() {
		return "NotificationDto [code=" + code + ", classNameFilter=" + classNameFilter + ", eventTypeFilter=" + eventTypeFilter + ", elFilter=" + elFilter + ", elAction="
				+ elAction + ", counterTemplate=" + counterTemplate + "]";
	}

	public String getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(String counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

}
