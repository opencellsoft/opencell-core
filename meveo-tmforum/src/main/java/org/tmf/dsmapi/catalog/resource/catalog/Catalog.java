package org.tmf.dsmapi.catalog.resource.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.AbstractEntity;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.RelatedParty;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.commons.ParsedVersion;
import org.tmf.dsmapi.commons.Utilities;
import org.tmf.dsmapi.commons.annotation.EntityReferenceProperty;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "id": "10",
 *     "version": "1.1",
 *     "href": "http://serverlocation:port/catalogManagement/catalog/10",
 *     "name": "Catalog Wholesale Business",
 *     "description": "A catalog to hold categories, products, services, and resources",
 *     "lastUpdate": "2013-04-19T16:42:23-04:00",
 *     "lifecycleStatus": "Active",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00"
 *     },
 *     "type": "Product Catalog",
 *     "category": [
 *         {
 *             "id": "12",
 *             "version": "1.2",
 *             "href": "http://serverlocation:port/catalogManagement/category/12",
 *             "name": "Cloud offerings",
 *             "description": " A category to hold all available cloud service offers "
 *         }
 *     ],
 *     "relatedParty": [
 *         {
 *             "role": "Owner",
 *             "id": "1234",
 *             "href": "http://serverLocation:port/partyManagement/partyRole/1234"
 *         },
 *         {
 *             "role": "Reviser",
 *             "name": "Roger Collins"
 *         }
 *     ]
 * }
 *
 */
@MappedSuperclass
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Catalog extends AbstractEntity implements Serializable {
    private final static long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(Catalog.class.getName());

    @Column(name = "TYPE", nullable = true)
    private CatalogType type;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_CATALOG_R_CATEGORY", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "VERSION")
    })
    @EntityReferenceProperty(classId=Category.class)
    private List<CatalogReference> category;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_CATALOG_R_PARTY", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "VERSION")
    })
    private List<RelatedParty> relatedParty;

    public Catalog() {
    }

    @Override
    public void setVersion(String version) {
        if (Utilities.areEqual(ParsedVersion.ROOT_CATALOG_VERSION.getExternalView(), version) == true) {
            super.setParsedVersion(ParsedVersion.ROOT_CATALOG_VERSION);
            return;
        }

        super.setVersion(version);
    }

    public CatalogType getType() {
        return type;
    }

    public void setType(CatalogType type) {
        this.type = type;
    }

    public List<CatalogReference> getCategory() {
        return category;
    }

    public void setCategory(List<CatalogReference> category) {
        this.category = category;
    }

    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    @JsonProperty(value = "category")
    public List<CatalogReference> categoryToJson() {
        return (category != null && category.size() > 0) ? category : null;
    }

    @JsonProperty(value = "relatedParty")
    public List<RelatedParty> relatedPartyToJson() {
        return (relatedParty != null && relatedParty.size() > 0) ? relatedParty : null;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 53 * hash + super.hashCode();

        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 53 * hash + (this.relatedParty != null ? this.relatedParty.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
            return false;
        }

        final Catalog other = (Catalog) object;
        if (Utilities.areEqual(this.type, other.type) == false) {
            return false;
        }

        if (Utilities.areEqual(this.category, other.category) == false) {
            return false;
        }

        if (Utilities.areEqual(this.relatedParty, other.relatedParty) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Catalog{<" + super.toString() + ">, type=" + type + ", category=" + category + ", relatedParty=" + relatedParty + '}';
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

        if (getVersion() == null) {
             super.setParsedVersion(ParsedVersion.ROOT_CATALOG_VERSION);
        }
    }

    public void edit(Catalog input) {
        if (input == null || input == this) {
            return;
        }

        super.edit(input);

        if (this.type == null) {
            this.type = input.type;
        }

        if (this.category == null) {
            this.category = input.category;
        }

        if (this.relatedParty == null) {
            this.relatedParty = input.relatedParty;
        }
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        logger.log(Level.FINE, "Catalog:valid ()");

        if (super.isValid() == false) {
            return false;
        }

        return true;
    }

    @Override
    @PostLoad
    protected void onLoad() {
        if (Utilities.areEqual(ParsedVersion.ROOT_CATALOG_VERSION.getInternalView(), this.getVersion()) == true) {
            this.setParsedVersion(ParsedVersion.ROOT_CATALOG_VERSION);
            return;
        }

        super.setVersion(getVersion());
    }

    public static Catalog createProto() {
        Catalog catalog = new Catalog();

        catalog.setId("id");
        catalog.setVersion("1.1");
        catalog.setHref("href");
        catalog.setName("name");
        catalog.setDescription("description");
        catalog.setLastUpdate(new Date ());
        catalog.setLifecycleStatus(LifecycleStatus.ACTIVE);
        catalog.setValidFor(TimeRange.createProto ());

        catalog.type = CatalogType.PRODUCT_CATALOG;

        catalog.category = new ArrayList<CatalogReference>();
        catalog.category.add(CatalogReference.createProto());

        catalog.relatedParty = new ArrayList<RelatedParty>();
        catalog.relatedParty.add(RelatedParty.createProto());

        return catalog;
    }

}
