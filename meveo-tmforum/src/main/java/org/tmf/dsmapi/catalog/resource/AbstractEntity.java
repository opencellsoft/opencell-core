package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.tmf.dsmapi.commons.ParsedVersion;
import org.tmf.dsmapi.commons.Utilities;
import org.tmf.dsmapi.commons.annotation.VersionProperty;

/**
 *
 * @author bahman.barzideh
 *
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Id
    @Column(name = "VERSION", nullable = false)
    @VersionProperty
    private String version;

    @Transient
    @JsonIgnore
    private ParsedVersion parsedVersion;

    @Column(name = "HERF", nullable = true)
    private String href;

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "LAST_UPDATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Column(name = "LIFECYCLE_STATUS", nullable = true)
    private LifecycleStatus lifecycleStatus;

    private TimeRange validFor;

    protected AbstractEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version == null) {
            this.parsedVersion = null;
            this.version = null;
            return;
        }

        this.parsedVersion = new ParsedVersion(version);
        this.version = this.parsedVersion.getInternalView();
    }

    public ParsedVersion getParsedVersion() {
        if (parsedVersion == null && version != null) {
            parsedVersion = new ParsedVersion(version);
        }

        return parsedVersion;
    }

    public void setParsedVersion(ParsedVersion parsedVersion) {
        this.parsedVersion = parsedVersion;
        this.version = (this.parsedVersion != null) ? this.parsedVersion.getInternalView() : null;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LifecycleStatus getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    @JsonProperty(value = "version")
    public String versionToJson() {
        ParsedVersion thisParsedVersion = getParsedVersion();
        return (thisParsedVersion != null) ? thisParsedVersion.getExternalView() : null;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 47 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 47 * hash + (this.lastUpdate != null ? this.lastUpdate.hashCode() : 0);
        hash = 47 * hash + (this.lifecycleStatus != null ? this.lifecycleStatus.hashCode() : 0);
        hash = 47 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final AbstractEntity other = (AbstractEntity) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.version, other.version) == false) {
            return false;
        }

        if (Utilities.areEqual(this.href, other.href) == false) {
            return false;
        }

        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (Utilities.areEqual(this.lastUpdate, other.lastUpdate) == false) {
            return false;
        }

        if (this.lifecycleStatus != other.lifecycleStatus) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "AbstractEntity{" + "id=" + id + ", version=" + version + ", parsedVersion=" + parsedVersion + ", href=" + href + ", name=" + name + ", description=" + description + ", lastUpdate=" + lastUpdate + ", lifecycleStatus=" + lifecycleStatus + ", validFor=" + validFor + '}';
    }

    @JsonIgnore
    public abstract Logger getLogger();

    public boolean keysMatch(AbstractEntity input) {
        if (input == null || getClass() != input.getClass()) {
            return false;
        }

        if (input == this) {
            return true;
        }

        if (Utilities.areEqual(this.id, input.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.version, input.version) == false) {
            return false;
        }

        return true;
    }

    @JsonIgnore
    public void setCreateDefaults() {
        if (lifecycleStatus == null) {
            lifecycleStatus = LifecycleStatus.IN_STUDY;
        }
    }

    public void edit(AbstractEntity input) {
        if (input == null || input == this) {
            return;
        }

        if (this.href == null) {
            this.href = input.href;
        }

        if (this.name == null) {
            this.name = input.name;
        }

        if (this.description == null) {
            this.description = input.description;
        }

        if (this.lastUpdate == null) {
            this.lastUpdate = input.lastUpdate;
        }

        if (this.lifecycleStatus == null) {
            this.lifecycleStatus = input.lifecycleStatus;
        }

        if (this.validFor == null) {
            this.validFor = input.validFor;
        }
    }

    @JsonIgnore
    public boolean isValid() {
        Logger logger = getLogger();

        if (Utilities.hasValue(this.name) == false) {
            logger.log(Level.FINE, " invalid: name is required");
            return false;
        }

        if (this.validFor != null && this.validFor.isValid() == false) {
            logger.log(Level.FINE, " invalid: validFor");
            return false;
        }

        return true;
    }

    public boolean hasHigherVersionThan(AbstractEntity other) {
        ParsedVersion thisParsedVersion = getParsedVersion();
        if (thisParsedVersion == null) {
            throw new IllegalArgumentException ("invalid parsed version object");
        }

        return (thisParsedVersion.isGreaterThan((other != null) ? other.getParsedVersion() : null));
    }

    public boolean canLifecycleTransitionFrom(LifecycleStatus fromStatus) {
        if (lifecycleStatus == null) {
            return false;
        }

        return (lifecycleStatus.canTransitionFrom(fromStatus));
    }

    @PrePersist
    private void onCreate() {
        lastUpdate = new Date ();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = new Date ();
    }

    @PostLoad
    protected void onLoad() {
        this.parsedVersion = new ParsedVersion(this.version);
        this.version = this.parsedVersion.getInternalView();
    }

}
