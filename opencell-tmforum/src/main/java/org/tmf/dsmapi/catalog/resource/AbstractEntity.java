/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.meveo.commons.utils.CustomDateSerializer;
import org.slf4j.Logger;
import org.tmf.dsmapi.commons.ParsedVersion;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author bahman.barzideh
 * 
 */
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1821894884787055051L;

    private String id;

    private String version;

    @JsonIgnore
    private ParsedVersion parsedVersion;

    private String href;

    private String name;

    private String description;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date lastUpdate;

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
        return "AbstractEntity{" + "id=" + id + ", version=" + version + ", parsedVersion=" + parsedVersion + ", href=" + href + ", name=" + name + ", description=" + description
                + ", lastUpdate=" + lastUpdate + ", lifecycleStatus=" + lifecycleStatus + ", validFor=" + validFor + '}';
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
            logger.trace(" invalid: name is required");
            return false;
        }

        if (this.validFor != null && this.validFor.isValid() == false) {
            logger.trace(" invalid: validFor");
            return false;
        }

        return true;
    }

    public boolean hasHigherVersionThan(AbstractEntity other) {
        ParsedVersion thisParsedVersion = getParsedVersion();
        if (thisParsedVersion == null) {
            throw new IllegalArgumentException("invalid parsed version object");
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
        lastUpdate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = new Date();
    }

    @PostLoad
    protected void onLoad() {
        this.parsedVersion = new ParsedVersion(this.version);
        this.version = this.parsedVersion.getInternalView();
    }

}
