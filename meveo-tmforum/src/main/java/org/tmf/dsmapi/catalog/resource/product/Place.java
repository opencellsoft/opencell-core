package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;

import org.meveo.api.dto.account.AddressDto;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "id": "12", "href": "http://serverlocation:port/marketSales/place/12", "name": "France" }
 *        30/12/2016 : add our AddressDto
 *         { "id": "13", "href": "http://serverlocation:port/marketSales/place/13", "name": "France",address:{"address1":"17 lot figuig", "address2":"allée des citronniers", "address3":"ain sebaa", "city": "Casablanca", "zipCode": "25080", "state": "Grand casablanca", "country": "Maroc" } }
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class Place implements Serializable {
    public final static long serialVersionUID = 1L;

    private String id;

    private String href;

    private String name;
    
    private AddressDto address;

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

    public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
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
        return "Place{" + "id=" + id + ", href=" + href + ", name=" + name + ", address="+address+'}';
    }

    public static Place createProto() {
        Place place = new Place();

        place.id = "id";
        place.href = "href";
        place.name = "name";
        AddressDto addressDto = new AddressDto();
        addressDto.setAddress1("address1");
        addressDto.setAddress2("address2");
        addressDto.setAddress3("address3");
        addressDto.setCity("city");
        addressDto.setCountry("country");
        addressDto.setState("state");
        addressDto.setZipCode("zipCode");
        place.address = addressDto;
        return place;
    }

}
