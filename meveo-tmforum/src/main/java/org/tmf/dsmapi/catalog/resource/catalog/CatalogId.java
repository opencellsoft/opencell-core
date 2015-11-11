package org.tmf.dsmapi.catalog.resource.catalog;

import java.io.Serializable;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 */
public class CatalogId implements Serializable {
    private final static long serialVersionUID = 1L;

    private String id;
    private String version;

    public CatalogId() {
    }

    public CatalogId(String id, String version) {
        this.id = id;
        this.version = version;
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
        int hash = 7;

        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 43 * hash + (this.version != null ? this.version.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final CatalogId other = (CatalogId) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.version, other.version) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "CatalogId{" + "id=" + id + ", version=" + version + '}';
    }

}
