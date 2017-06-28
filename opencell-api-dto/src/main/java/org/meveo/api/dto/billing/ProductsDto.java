package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductsDto implements Serializable {

	private static final long serialVersionUID = -6816362012819614661L;

	private List<ProductDto> products;

	public List<ProductDto> getProducts() {
		if (products == null) {
			products = new ArrayList<ProductDto>();
		}

		return products;
	}

	public void setProducts(List<ProductDto> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "ProductsDto [products=" + products + "]";
	}

}
