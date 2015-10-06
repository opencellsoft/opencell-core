package org.meveo.model.notification;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.validation.constraint.ClassName;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "ADM_NOTIFICATION", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_NOTIFICATION_SEQ")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({ @NamedQuery(name = "Notification.getNotificationsForCache", query = "SELECT n from Notification n where n.disabled=false") })
public class Notification extends BusinessEntity {

    private static final long serialVersionUID = 2634877161620665288L;

    @Column(name = "CLASS_NAME_FILTER", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    @ClassName
    private String classNameFilter;

    @Column(name = "EVENT_TYPE_FILTER", length = 20, nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationEventTypeEnum eventTypeFilter;

    @Column(name = "EVENT_EXPRESSION_FILTER", length = 1000)
    @Size(max = 1000)
    private String elFilter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTER_TEMPLATE_ID")
    private CounterTemplate counterTemplate;

    @OneToOne()
    @JoinColumn(name = "COUNTER_INSTANCE_ID")
    private CounterInstance counterInstance;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCRIPT_INSTANCE_ID")
    private ScriptInstance scriptInstance;
    
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ADM_NOTIFICATION_PARAMS") 
	private Map<String, String> params = new HashMap<String, String>();

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

    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    public CounterInstance getCounterInstance() {
        return counterInstance;
    }

    public void setCounterInstance(CounterInstance counterInstance) {
        this.counterInstance = counterInstance;
    }
    

    /**
	 * @return the scriptInstance
	 */
	public ScriptInstance getScriptInstance() {
		return scriptInstance;
	}

	/**
	 * @param scriptInstance the scriptInstance to set
	 */
	public void setScriptInstance(ScriptInstance scriptInstance) {
		this.scriptInstance = scriptInstance;
	}
	

	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
    public String toString() {
        return String.format("Notification [%s, classNameFilter=%s, eventTypeFilter=%s, elFilter=%s, scriptInstance=%s, counterTemplate=%s, counterInstance=%s]", super.toString(),
            classNameFilter, eventTypeFilter, elFilter, scriptInstance.getId(), counterTemplate != null ? counterTemplate.getId() : null, counterInstance != null ? counterInstance.getId()
                    : null);
    }
}