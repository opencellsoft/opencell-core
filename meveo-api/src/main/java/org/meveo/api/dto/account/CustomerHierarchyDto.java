package org.meveo.api.dto.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.SellerDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerHierarchyDto extends BaseDto {

	private static final long serialVersionUID = -7727040970378439778L;

	private List<SellerDto> sellers;

	public List<SellerDto> getSellers() {
		return sellers;
	}

	public void setSellers(List<SellerDto> sellers) {
		this.sellers = sellers;
	}

}
