package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.SellersDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerHierarchyDto extends BaseDto {

	private static final long serialVersionUID = -7727040970378439778L;

	@XmlElement(required = true)
	private SellersDto sellers;

	public SellersDto getSellers() {
		return sellers;
	}

	public void setSellers(SellersDto sellers) {
		this.sellers = sellers;
	}

}
