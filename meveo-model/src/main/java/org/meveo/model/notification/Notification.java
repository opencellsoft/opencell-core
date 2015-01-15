package org.meveo.model.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.meveo.model.BusinessEntity;
import org.meveo.validation.constraint.ClassName;

@Entity
@Table(name = "ADM_NOTIFICATION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_NOTIFICATION_SEQ")
@Inheritance(strategy=InheritanceType.JOINED)
public class Notification extends BusinessEntity {
	

	private static final long serialVersionUID = 2634877161620665288L;

	@Column(name="CLASS_NAME_FILTER",length=255,nullable=false)
	@NotNull
	@Length(max=255)
	@ClassName
	String classNameFilter;
	
	@Column(name="EVENT_TYPE_FILTER",length=10,nullable=false)
	@NotNull
	@Enumerated(EnumType.STRING)
	NotificationEventTypeEnum eventTypeFilter;

	@Column(name="EVENT_EXPRESSION_FILTER",length=1000)
	@Length(max=1000)
	String elFilter;

	@Column(name="ACTION_EXPRESSION",length=2000)	
	@Length(max=2000)
	String elAction;

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

	public String getElAction() {
		return elAction;
	}

	public void setElAction(String elAction) {
		this.elAction = elAction;
	}
	
}
