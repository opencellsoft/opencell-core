package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "id": "12",
 *     "href": "http://serverlocation:port/marketSales/place/12",
 *     "name": "France"
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class Place implements Serializable {
    public final static long serialVersionUID = 1L;

    @Column(name = "PLACE_ID", nullable = true)
    private String id;

    @Column(name = "PLACE_HREF", nullable = true)
    private String href;

    @Column(name = "PLACE_NAME", nullable = true)
    private String name;

    public Place() {
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
        int hash = 7;

        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Place other = (Place) object;
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
        return "Place{" + "id=" + id + ", href=" + href + ", name=" + name + '}';
    }

    public static Place createProto() {
        Place place = new Place();

        place.id = "id";
        place.href = "href";
        place.name = "name";

        return place;
    }

}
