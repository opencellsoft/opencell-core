package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerBrands")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerBrandsDto implements Serializable {

	private static final long serialVersionUID = -3495786003526429089L;

	private List<CustomerBrandDto> customerBrand;

	public List<CustomerBrandDto> getCustomerBrand() {
		if (customerBrand == null)
			customerBrand = new ArrayList<CustomerBrandDto>();
		return customerBrand;
	}

	public void setCustomerBrand(List<CustomerBrandDto> customerBrand) {
		this.customerBrand = customerBrand;
	}

	@Override
	public String toString() {
		return "CustomerBrandsDto [customerBrand=" + customerBrand + "]";
	}

}
