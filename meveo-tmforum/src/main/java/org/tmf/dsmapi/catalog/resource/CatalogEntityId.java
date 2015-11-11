package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 */
public class CatalogEntityId implements Serializable {
    private final static long serialVersionUID = 1L;

    private String catalogId;
    private String catalogVersion;
    private String id;
    private String version;

    public CatalogEntityId() {
    }

    public CatalogEntityId(String catalogId, String catalogVersion, String id, String version) {
        this.catalogId = catalogId;
        this.catalogVersion = catalogVersion;
        this.id = id;
        this.version = version;
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
        this.catalogVersion = catalogVersion;
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
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 83 * hash + (this.catalogId != null ? this.catalogId.hashCode() : 0);
        hash = 83 * hash + (this.catalogVersion != null ? this.catalogVersion.hashCode() : 0);
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 83 * hash + (this.version != null ? this.version.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final CatalogEntityId other = (CatalogEntityId) object;
        if (Utilities.areEqual(this.catalogId, other.catalogId) == false) {
            return false;
        }

        if (Utilities.areEqual(this.catalogVersion, other.catalogVersion) == false) {
            return false;
        }

        if (Utilities.areEqual (this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.version, other.version) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "CatalogEntityId{" + "catalogId=" + catalogId + ", catalogVersion=" + catalogVersion + ", id=" + id + ", version=" + version + '}';
    }

}
