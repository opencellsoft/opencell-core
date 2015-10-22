package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.List;

public class OrderItem implements Serializable {

	private String id;
	private String action;
	private String state;
	private String appointment;
	private List<BillingAccount> billingAccount;
	private ProductOffering productOffering;
	private Product product;
}
