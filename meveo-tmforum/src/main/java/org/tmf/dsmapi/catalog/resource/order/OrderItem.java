package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;

@XmlRootElement
@JsonSerialize(include=Inclusion.NON_NULL)
public class OrderItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2224931518265750159L;
	private String id;
	private String action;
	private String state;
	private String appointment;
	private BillingAccount billingAccount;
	private ProductOffering productOffering;
	private Product product;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getAppointment() {
		return appointment;
	}
	public void setAppointment(String appointment) {
		this.appointment = appointment;
	}
	public BillingAccount getBillingAccount() {
		return billingAccount;
	}
	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}
	public ProductOffering getProductOffering() {
		return productOffering;
	}
	public void setProductOffering(ProductOffering productOffering) {
		this.productOffering = productOffering;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}
