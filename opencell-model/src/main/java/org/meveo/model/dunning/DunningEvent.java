package org.meveo.model.dunning;

import org.meveo.model.AuditableEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Dunning Event class
 * @author mboukayoua
 */
@Entity
@Table(name="dunning_event")
public class DunningEvent extends AuditableEntity {

    /** dunning doc */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_document_id")
    private DunningDocument dunningDocument;

    /** dunning event name */
    @Column(name = "event_name")
    private String name;

    /** dunning event datetime */
    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /** dunning event type */
    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private DunningEventTypeEnum type;

    /**
     * get dunning doc
     * @return dunning doc
     */
    public DunningDocument getDunningDocument() {
        return dunningDocument;
    }

    /**
     * set dunning doc
     * @param dunningDocument dunning doc
     */
    public void setDunningDocument(DunningDocument dunningDocument) {
        this.dunningDocument = dunningDocument;
    }

    /**
     * get event name
     * @return event name
     */
    public String getName() {
        return name;
    }

    /**
     * set get event name
     * @param name event name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get event date
     * @return event date
     */
    public Date getDate() {
        return date;
    }

    /**
     * set event date
     * @param date event date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * get event type
     * @return event type
     */
    public DunningEventTypeEnum getType() {
        return type;
    }

    /**
     * set event type
     * @param type event type
     */
    public void setType(DunningEventTypeEnum type) {
        this.type = type;
    }
}
