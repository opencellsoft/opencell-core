package org.meveo.api.dto;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.model.Auditable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Equivalent of AuditableEntity in DTO
 * Contain the entity creation and modification dates.
 * @author Edward P. Legaspi
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public @Hidden class AuditableDto implements Serializable {

    private static final long serialVersionUID = -8188627756883991240L;

    /**
     * Created date
     */
    @XmlAttribute()
    @Schema(description = "Created date",hidden=true)
    private Date created;

    /**
     * Updated date
     */
    @XmlAttribute()
    @Schema(description = "Updated date",hidden=true)
    private Date updated;

    /**
     * User who created the record
     */
    @XmlAttribute()
    @Schema(description = "User who created the record",hidden=true)
    private String creator;

    /**
     * Set to the user who updated this record
     */
    @XmlAttribute()
    @Schema(description = "Set to the user who updated this record",hidden=true)
    private String updater;

    public AuditableDto() {

    }

    public AuditableDto(Auditable e) {
        created = e.getCreated();
        updated = e.getUpdated();
        creator = e.getCreator();
        updater = e.getUpdater();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}
