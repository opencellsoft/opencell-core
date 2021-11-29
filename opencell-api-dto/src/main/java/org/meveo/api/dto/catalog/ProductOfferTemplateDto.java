package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.cpq.OfferProductsDto;


@XmlRootElement(name = "ProductOfferTemplateDto")
@XmlType(name = "ProductOfferTemplateDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductOfferTemplateDto extends BusinessEntityDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5213351345129753071L;
	

    /** The valid from. */
    @XmlAttribute()
    private Date validFrom;

    /** The valid to. */
    @XmlAttribute()
    private Date validTo;
    

	@XmlElementWrapper(name = "products", required = true)
    @XmlElement(name = "products", required = true)
	@NotNull
    private List<OfferProductsDto> products = new ArrayList<OfferProductsDto>();


	public Date getValidFrom() {
		return validFrom;
	}


	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}


	public Date getValidTo() {
		return validTo;
	}


	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}


	public List<OfferProductsDto> getProducts() {
		return products;
	}


	public void setProducts(List<OfferProductsDto> products) {
		this.products = products;
	}
	
}
