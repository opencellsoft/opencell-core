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

package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;

/**
 * The Class ProductTemplateDto.
 * 
 * @author anasseh
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "ProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTemplateDto extends ProductOfferingDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1866373944715745993L;

    /** The product charge templates. */
    @XmlElementWrapper(name = "productChargeTemplates")
    @XmlElement(name = "productChargeTemplate", required = true)
    private List<ProductChargeTemplateDto> productChargeTemplates;

    /** The business product model. */
    private BusinessProductModelDto businessProductModel;

    /** The wallet templates. */
    @XmlElementWrapper(name = "walletTemplates")
    @XmlElement(name = "walletTemplate")
    private List<WalletTemplateDto> walletTemplates;

    /**
     * Instantiates a new product template dto.
     */
    public ProductTemplateDto() {
    }

    /**
     * Instantiates a new product template dto.
     *
     * @param productTemplate the product template
     * @param customFieldsDto the custom fields dto
     * @param asLink the as link
     * @param loadProductChargeTemplate whether to load the product charge template or not
     */
	public ProductTemplateDto(ProductTemplate productTemplate, CustomFieldsDto customFieldsDto, boolean asLink, boolean loadProductChargeTemplate) {
		super(productTemplate, customFieldsDto, asLink);
		
        // set serviceChargeTemplateRecurrings
        if (loadProductChargeTemplate && !productTemplate.getProductChargeTemplates().isEmpty()) {
            productChargeTemplates = new ArrayList<>();

            for (ProductChargeTemplate charge : productTemplate.getProductChargeTemplates()) {
                ProductChargeTemplateDto dto = new ProductChargeTemplateDto();
                dto.setCode(charge.getCode());

                productChargeTemplates.add(dto);
            }
        }

        if (asLink) {
            return;
        }

        BusinessProductModel businessProductModel = productTemplate.getBusinessProductModel();
        BusinessProductModelDto businessProductModelDto = null;
        if (businessProductModel != null) {
            businessProductModelDto = new BusinessProductModelDto(businessProductModel);
        }
        this.setBusinessProductModel(businessProductModelDto);
        List<WalletTemplate> walletTemplates = productTemplate.getWalletTemplates();
        if (walletTemplates != null && !walletTemplates.isEmpty()) {
            WalletTemplateDto walletDto = null;
            this.setWalletTemplates(new ArrayList<WalletTemplateDto>());
            for (WalletTemplate walletTemplate : walletTemplates) {
                walletDto = new WalletTemplateDto(walletTemplate);
                this.getWalletTemplates().add(walletDto);
            }
        }
    }

    /**
     * Gets the product charge templates.
     *
     * @return the product charge templates
     */
    public List<ProductChargeTemplateDto> getProductChargeTemplates() {
        return productChargeTemplates;
    }

    /**
     * Sets the product charge templates.
     *
     * @param productChargeTemplates the new product charge templates
     */
    public void setProductChargeTemplates(List<ProductChargeTemplateDto> productChargeTemplates) {
        this.productChargeTemplates = productChargeTemplates;
    }

    /**
     * Gets the business product model.
     *
     * @return the business product model
     */
    public BusinessProductModelDto getBusinessProductModel() {
        return businessProductModel;
    }

    /**
     * Sets the business product model.
     *
     * @param businessProductModel the new business product model
     */
    public void setBusinessProductModel(BusinessProductModelDto businessProductModel) {
        this.businessProductModel = businessProductModel;
    }

    /**
     * Gets the wallet templates.
     *
     * @return the wallet templates
     */
    public List<WalletTemplateDto> getWalletTemplates() {
        return walletTemplates;
    }

    /**
     * Sets the wallet templates.
     *
     * @param walletTemplates the new wallet templates
     */
    public void setWalletTemplates(List<WalletTemplateDto> walletTemplates) {
        this.walletTemplates = walletTemplates;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && (productChargeTemplates == null || productChargeTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }
}
