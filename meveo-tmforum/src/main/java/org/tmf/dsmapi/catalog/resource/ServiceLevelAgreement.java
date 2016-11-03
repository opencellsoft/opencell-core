package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;

import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "id": "28", "href": "http://serverlocation:port/slaManagement/serviceLevelAgreement/28", "name": "Standard SLA" }
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class ServiceLevelAgreement implements Serializable {
    public final static long serialVersionUID = 1L;

    private String id;

    private String href;

    private String name;

    public ServiceLevelAgreement() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ServiceLevelAgreement other = (ServiceLevelAgreement) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.href, other.href) == false) {
            return false;
        }

        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ServiceLevelAgreement{" + "id=" + id + ", href=" + href + ", name=" + name + '}';
    }

    public static ServiceLevelAgreement createProto() {
        ServiceLevelAgreement ServiceLevelAgreement = new ServiceLevelAgreement();

        ServiceLevelAgreement.id = "id";
        ServiceLevelAgreement.href = "href";
        ServiceLevelAgreement.name = "name";

        return ServiceLevelAgreement;
    }

}
