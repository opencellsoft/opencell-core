package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

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
	private String scriptInstanceCode;
    private Map<String,String> scriptParams = new HashMap<String,String>();
	private String counterTemplate;
	

	public NotificationDto() {

	}

	public NotificationDto(Notification e) {
		code = e.getCode();
		classNameFilter = e.getClassNameFilter();
		eventTypeFilter = e.getEventTypeFilter().name();
		elFilter = e.getElFilter();
		scriptInstanceCode = e.getScriptInstance()==null?null:e.getScriptInstance().getCode();
		if (e.getCounterTemplate() != null) {
			counterTemplate = e.getCounterTemplate().getCode();
		}
		scriptParams = e.getParams();
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

	

	/**
	 * @return the scriptParams
	 */
	public Map<String, String> getScriptParams() {
		return scriptParams;
	}

	/**
	 * @param scriptParams the scriptParams to set
	 */
	public void setScriptParams(Map<String, String> scriptParams) {
		this.scriptParams = scriptParams;
	}

	/**
	 * @return the scriptInstanceCode
	 */
	public String getScriptInstanceCode() {
		return scriptInstanceCode;
	}

	/**
	 * @param scriptInstanceCode the scriptInstanceCode to set
	 */
	public void setScriptInstanceCode(String scriptInstanceCode) {
		this.scriptInstanceCode = scriptInstanceCode;
	}

	@Override
	public String toString() {
		return "NotificationDto [code=" + code + ", classNameFilter=" + classNameFilter + ", eventTypeFilter=" + eventTypeFilter + ", elFilter=" + elFilter + ", scriptInstanceCode="
				+ scriptInstanceCode + ", counterTemplate=" + counterTemplate + "]";
	}

	public String getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(String counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

}
