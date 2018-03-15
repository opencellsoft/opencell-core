package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "Notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationDto extends BusinessDto {

	private static final long serialVersionUID = 3931479706274647165L;

	@XmlElement(required = true)
	private String classNameFilter;

	/**
	 * Valid values: CREATED, UPDATED, REMOVED, TERMINATED, DISABLED, PROCESSED,
	 * REJECTED, REJECTED_CDR, LOGGED_IN, INBOUND_REQ, ENABLED
	 */
	@XmlElement(required = true)
	private NotificationEventTypeEnum eventTypeFilter;

	private String elFilter;
	private String scriptInstanceCode;
    private Map<String,String> scriptParams = new HashMap<String,String>();
	private String counterTemplate;
	private int priority;
	private Boolean active = null;

	public NotificationDto() {

	}

	/**
     * v5.0: add active field
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
	public NotificationDto(Notification e) {
		super(e);
		classNameFilter = e.getClassNameFilter();
		eventTypeFilter = e.getEventTypeFilter();
		elFilter = e.getElFilter();
		scriptInstanceCode = e.getScriptInstance()==null?null:e.getScriptInstance().getCode();
		if (e.getCounterTemplate() != null) {
			counterTemplate = e.getCounterTemplate().getCode();
		}
		if(e.getParams()!=null){
			scriptParams.putAll(e.getParams());
		}
		priority = e.getPriority();
		active = e.isActive();
//		scriptParams = e.getParams();
	}	

	public String getClassNameFilter() {
		return classNameFilter;
	}

	public void setClassNameFilter(String classNameFilter) {
		this.classNameFilter = classNameFilter;
	}

	public NotificationEventTypeEnum getEventTypeFilter() {
		return eventTypeFilter;
	}

	public void setEventTypeFilter(NotificationEventTypeEnum eventTypeFilter) {
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
		return "NotificationDto [classNameFilter=" + classNameFilter + ", eventTypeFilter=" + eventTypeFilter
				+ ", elFilter=" + elFilter + ", scriptInstanceCode=" + scriptInstanceCode + ", scriptParams="
				+ scriptParams + ", counterTemplate=" + counterTemplate + ", priority=" + priority + "]";
	}

	public String getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(String counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Getter for active state
	 * @author akadid abdelmounaim
	 * @lastModifiedVersion 5.0
	 */
    public Boolean isActive() {
        return active;
    }

    /**
     * Setter for active state
     * @param active active state
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

}
