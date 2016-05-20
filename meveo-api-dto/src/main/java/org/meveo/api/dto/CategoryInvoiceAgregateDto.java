/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "CategoryInvoiceAgregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryInvoiceAgregateDto extends BaseDto {

	private static final long serialVersionUID = 6165612614574594919L;

	private String categoryInvoiceCode;
	private String description;
	private List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto=new ArrayList<SubCategoryInvoiceAgregateDto>();
	
	
	public CategoryInvoiceAgregateDto() {
	}


	/**
	 * @return the categoryInvoiceCode
	 */
	public String getCategoryInvoiceCode() {
		return categoryInvoiceCode;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @param categoryInvoiceCode the categoryInvoiceCode to set
	 */
	public void setCategoryInvoiceCode(String categoryInvoiceCode) {
		this.categoryInvoiceCode = categoryInvoiceCode;
	}


	/**
	 * @return the listSubCategoryInvoiceAgregateDto
	 */
	public List<SubCategoryInvoiceAgregateDto> getListSubCategoryInvoiceAgregateDto() {
		return listSubCategoryInvoiceAgregateDto;
	}


	/**
	 * @param listSubCategoryInvoiceAgregateDto the listSubCategoryInvoiceAgregateDto to set
	 */
	public void setListSubCategoryInvoiceAgregateDto(List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto) {
		this.listSubCategoryInvoiceAgregateDto = listSubCategoryInvoiceAgregateDto;
	}
	

}
