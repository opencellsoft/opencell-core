package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.List;

public class ProductOffering implements Serializable {

	private String id;
	private String href;
	private List<BundledProductOffering> bundledProductOffering;
}
