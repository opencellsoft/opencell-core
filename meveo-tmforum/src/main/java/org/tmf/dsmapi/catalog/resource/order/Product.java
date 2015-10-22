package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.tmf.dsmapi.catalog.resource.product.Place;

public class Product implements Serializable {

	private String id;
	private String href;
	private Place place;
	private List<ProductCharacteristic> productCharacteristic=new ArrayList<ProductCharacteristic>();
	private List<RelatedParty> relatedParty;
	private List<ProductRelationship> productRelationship;
}
