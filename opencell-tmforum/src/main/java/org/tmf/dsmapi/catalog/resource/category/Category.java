package org.tmf.dsmapi.catalog.resource.category;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.catalog.resource.AbstractCatalogEntity;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "id": "42", "version": "1.2", "href": "http://serverlocation:port/catalogManagement/category/42", "name": "Cloud Services", "description":
 *         "A category to hold all available cloud service offers", "lastUpdate": "2013-04-19T16:42:23-04:00", "lifecycleStatus": "Active", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime": "" }, "parentId": "41", "isRoot": "false" }
 * 
 */
@XmlRootElement(name="Category", namespace="http://www.tmforum.org")
@XmlType(name="Category", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class Category extends AbstractCatalogEntity implements Serializable {
    private final static long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(Category.class.getName());

    private String parentId;

    private Boolean isRoot;

    public Category() {
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean isRoot) {
        this.isRoot = isRoot;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 53 * hash + super.hashCode();

        hash = 53 * hash + (this.parentId != null ? this.parentId.hashCode() : 0);
        hash = 53 * hash + (this.isRoot != null ? this.isRoot.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
            return false;
        }

        final Category other = (Category) object;
        if (Utilities.areEqual(this.parentId, other.parentId) == false) {
            return false;
        }

        if (Utilities.areEqual(this.isRoot, other.isRoot) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Category{<" + super.toString() + ">, parentId=" + parentId + ", isRoot=" + isRoot + '}';
    }

    @Override
    @JsonIgnore
    public Logger getLogger() {
        return logger;
    }

    @Override
    @JsonIgnore
    public void setCreateDefaults() {
        super.setCreateDefaults();

        if (isRoot == null) {
            isRoot = true;
        }
    }

    public void edit(Category input) {
        if (input == null || input == this) {
            return;
        }

        super.edit(input);

        if (this.parentId == null) {
            this.parentId = input.parentId;
        }

        if (this.isRoot == null) {
            this.isRoot = input.isRoot;
        }
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        logger.log(Level.FINE, "Category:isValid ()");

        if (super.isValid() == false) {
            return false;
        }

        if (this.isRoot == Boolean.FALSE && Utilities.hasValue(parentId) == false) {
            logger.log(Level.FINE, " invalid: parentId must be specified when isRoot is false");
            return false;
        }

        if (this.isRoot == Boolean.TRUE && parentId != null) {
            logger.log(Level.FINE, " invalid: parentId must not be specififed when isRoot is true");
            return false;
        }

        return true;
    }

    public static Category createProto(UriInfo uriInfo) {
        Category category = new Category();

        category.setId("1");
        category.setVersion("1.0");
        category.setHref(String.format("%scatalogManagement/category/%s", uriInfo.getBaseUri().toString(), category.getId()));
        category.setName("Default");
        category.setDescription("Default category");
        category.setLastUpdate(new Date());
        category.setLifecycleStatus(LifecycleStatus.ACTIVE);
        category.setValidFor(TimeRange.createProto());

        category.parentId = null;
        category.isRoot = Boolean.TRUE;

        return category;
    }

    public CatalogReference getCatalogReference() {
        CatalogReference reference = new CatalogReference();
        reference.setReferencedId(this.getCatalogId());
        reference.setReferencedVersion(this.getVersion());
        reference.setReferencedHref(this.getHref());
        reference.setReferencedName(this.getName());
        reference.setReferencedDescription(this.getDescription());
        return reference;
    }

}
