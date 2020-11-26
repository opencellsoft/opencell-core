/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListProductsResponseDto.
 * 
 * @author Rachid.AIT
 */
@XmlRootElement(name = "GetListProductVersionsResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListProductVersionsResponseDto extends SearchResponse {

    /**
	 * 
	 *
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4603307996134173567L;
   

    /** products list. */
    @XmlElementWrapper(name = "productVersions")
    @XmlElement(name = "productVersions")
    private List<ProductVersionDto> productVersions  = new ArrayList<>();;

    /**
     * Instantiates a new gets the list offer template response dto.
     */
    public GetListProductVersionsResponseDto() {

    }

	/**
	 * @return the productVersions
	 */
	public List<ProductVersionDto> getProductVersions() {
		return productVersions;
	}








	/**
	 * @param productVersions the productVersions to set
	 */
	public void setProductVersions(List<ProductVersionDto> productVersions) {
		this.productVersions = productVersions;
	}








	/**
     * Adds the product version.
     *
     * @param productVersion the offer template
     */
	public void addProductVersion(ProductVersionDto productVersion) { 
		if(!productVersions.contains(productVersion)) {
			productVersions.add(productVersion);
		}
	}
}