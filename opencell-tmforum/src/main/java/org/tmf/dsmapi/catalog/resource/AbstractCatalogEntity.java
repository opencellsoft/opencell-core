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

import jakarta.persistence.PostLoad;

import org.tmf.dsmapi.commons.ParsedVersion;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author bahman.barzideh
 * 
 */
public abstract class AbstractCatalogEntity extends AbstractEntity implements Serializable {

    public final static String ROOT_CATALOG_ID = "";

    @JsonIgnore
    private String catalogId;

    @JsonIgnore
    private String catalogVersion;

    @JsonIgnore
    private ParsedVersion parsedCatalogVersion;

    public AbstractCatalogEntity() {
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogVersion() {
        return catalogVersion;
    }

    public void setCatalogVersion(String catalogVersion) {
        loadCatalogVersions(catalogVersion);
    }

    public ParsedVersion getParsedCatalogVersion() {
        if (parsedCatalogVersion == null && catalogVersion != null) {
            setCatalogVersion(catalogVersion);
        }

        return parsedCatalogVersion;
    }

    public void setParsedCatalogVersion(ParsedVersion parsedCatalogVersion) {
        this.parsedCatalogVersion = parsedCatalogVersion;
        this.catalogVersion = (this.parsedCatalogVersion != null) ? this.parsedCatalogVersion.getInternalView() : null;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 53 * hash + super.hashCode();

        hash = 31 * hash + (this.catalogId != null ? this.catalogId.hashCode() : 0);
        hash = 31 * hash + (this.catalogVersion != null ? this.catalogVersion.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
            return false;
        }

        final AbstractCatalogEntity other = (AbstractCatalogEntity) object;
        if (Utilities.areEqual(this.catalogId, other.catalogId) == false) {
            return false;
        }

        if (Utilities.areEqual(this.catalogVersion, other.catalogVersion) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "AbstractCatalogEntity{" + "catalogId=" + catalogId + ", catalogVersion=" + catalogVersion + ", parsedCatalogVersion=" + parsedCatalogVersion + '}';
    }

    @Override
    public boolean keysMatch(AbstractEntity input) {
        if (input == null) {
            return false;
        }

        if (input == this) {
            return true;
        }

        if (super.keysMatch(input) == false) {
            return false;
        }

        AbstractCatalogEntity other = (AbstractCatalogEntity) input;
        if (Utilities.areEqual(this.catalogId, other.catalogId) == false) {
            return false;
        }

        if (Utilities.areEqual(this.catalogVersion, other.catalogVersion) == false) {
            return false;
        }

        return true;
    }

    public void configureCatalogIdentifier() {
        setCatalogId(ROOT_CATALOG_ID);
        setParsedCatalogVersion(ParsedVersion.ROOT_CATALOG_VERSION);
    }

    @PostLoad
    @Override
    protected void onLoad() {
        super.onLoad();

        loadCatalogVersions(catalogVersion);

    }

    private void loadCatalogVersions(String catalogVersion) {
        if (ParsedVersion.ROOT_CATALOG_VERSION.getInternalView().equals(catalogVersion) == true) {
            this.parsedCatalogVersion = ParsedVersion.ROOT_CATALOG_VERSION;
            this.catalogVersion = this.parsedCatalogVersion.getInternalView();
            return;
        }

        if (catalogVersion == null) {
            this.parsedCatalogVersion = null;
            this.catalogVersion = null;
            return;
        }

        this.parsedCatalogVersion = new ParsedVersion(catalogVersion);
        this.catalogVersion = this.parsedCatalogVersion.getInternalView();
    }

}
