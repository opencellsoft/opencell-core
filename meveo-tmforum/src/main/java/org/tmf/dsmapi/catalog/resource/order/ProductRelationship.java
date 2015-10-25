package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonSerialize(include=Inclusion.NON_NULL)
public class ProductRelationship implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 22693302638836241L;
	private String type;
	private Product product;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
}
