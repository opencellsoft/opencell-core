package org.meveo.model.notification;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.validation.constraint.ClassName;

/**
 * Base notification information
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Entity
@ModuleItem
@ExportIdentifier({ "code" })
@Table(name = "adm_notification", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_notification_seq"), })
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "Notification.getNotificationsForCache", query = "SELECT n from Notification n where n.disabled=false", hints = {
                @QueryHint(name = "org.hibernate.readOnly", value = "true") }),
        @NamedQuery(name = "Notification.getActiveNotificationsByEventAndClasses", query = "SELECT n from Notification n where n.disabled=false and n.eventTypeFilter=:eventTypeFilter and n.classNameFilter in :classNameFilter order by priority ASC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class Notification extends EnableBusinessEntity {

    private static final long serialVersionUID = 2634877161620665288L;

    /**
     * Classname of an entity that notification applies to
     */
    @Column(name = "class_name_filter", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    @ClassName
    private String classNameFilter;

    /**
     * Event that notification applies to
     */
    @Column(name = "event_type_filter", length = 20, nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationEventTypeEnum eventTypeFilter;

    /**
     * Expression to determine if notification applies
     */
    @Column(name = "event_expression_filter", length = 2000)
    @Size(max = 2000)
    private String elFilter;

    /**
     * Counter template/definition to limit a number of notifications fired
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_template_id")
    private CounterTemplate counterTemplate;

    /**
     * Counter instance to limit a number of notifications fired
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "counter_instance_id")
    private CounterInstance counterInstance;

    /**
     * Script to run before notification firing or after in case of WebHook type notification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /**
     * Parameters to be passed to script execution in a form of expressions.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notification_params")
    private Map<String, String> params = new HashMap<String, String>();

    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority", columnDefinition = "int DEFAULT 1")
    private int priority = 1;
    
    /**
     * Run in async mode?
     */
    @Type(type = "numeric_boolean")
    @Column(name = "run_async")
    private boolean runAsync = false;

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
            classNameFilter, eventTypeFilter, elFilter, scriptInstance != null ? scriptInstance.getId() : null, counterTemplate != null ? counterTemplate.getId() : null,
            counterInstance != null ? counterInstance.getId() : null);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets boolean value of whether this notification will be run in async mode.
     * @return true / false
     */
	public boolean isRunAsync() {
		return runAsync;
	}

	/**
     * Sets boolean value of whether this notification will be run in async mode.
     * @return true / false
     */
	public void setRunAsync(boolean runAsync) {
		this.runAsync = runAsync;
	}
}