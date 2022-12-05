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

package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudVersionedApi;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.catalog.DigitalResourceDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;

public abstract class ProductOfferingApi<E extends ProductOffering, T extends BusinessEntityDto> extends BaseCrudVersionedApi<E, T> {

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private DigitalResourceService digitalResourceService;

    @Inject
    private ProductChargeTemplateService productChargeTemplateService;

    @Inject
    protected SellerService sellerService;

    @Inject
    private DigitalResourceApi digitalResourceApi;

    @Inject
    protected ChannelService channelService;

    protected void processProductChargeTemplateToDto(ProductTemplate productTemplate, ProductTemplateDto productTemplateDto) {
        List<ProductChargeTemplate> productChargeTemplates = productTemplate.getProductChargeTemplates();
        ProductChargeTemplateDto productChargeTemplateDto = null;
        List<ProductChargeTemplateDto> chargeDtos = new ArrayList<>();
        if (productChargeTemplates != null) {
            for (ProductChargeTemplate productChargeTemplate : productChargeTemplates) {
                if (productChargeTemplate != null) {
                    productChargeTemplate.setProductTemplates(Arrays.asList(productTemplate));
                    productChargeTemplateDto = new ProductChargeTemplateDto(productChargeTemplate, entityToDtoConverter.getCustomFieldsDTO(productChargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                    chargeDtos.add(productChargeTemplateDto);
                }
            }
            productTemplateDto.setProductChargeTemplates(chargeDtos);
        }
    }

    protected void processOfferTemplateCategories(ProductTemplateDto postData, ProductTemplate productTemplate) throws EntityDoesNotExistsException {
        List<OfferTemplateCategoryDto> offerTemplateCategories = postData.getOfferTemplateCategories();
        if (offerTemplateCategories != null) {
            productTemplate.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());
            for (OfferTemplateCategoryDto offerTemplateCategoryDto : offerTemplateCategories) {
                OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(offerTemplateCategoryDto.getCode());
                if (offerTemplateCategory == null) {
                    throw new EntityDoesNotExistsException(OfferTemplateCategory.class, offerTemplateCategoryDto.getCode());
                }
                productTemplate.getOfferTemplateCategories().add(offerTemplateCategory);
            }
        }
    }

    protected void processSellers(ProductTemplateDto postData, ProductTemplate productTemplate) throws EntityDoesNotExistsException {
        if (postData.getSellers() != null) {
            productTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                productTemplate.addSeller(seller);
            }
        }
    }

    protected void processDigitalResources(ProductTemplateDto postData, ProductTemplate productTemplate) throws BusinessException, MeveoApiException {
        List<DigitalResourceDto> attachmentDtos = postData.getAttachments();
        boolean hasAttachmentDtos = attachmentDtos != null && !attachmentDtos.isEmpty();
        List<DigitalResource> existingAttachments = productTemplate.getAttachments();
        boolean hasExistingAttachments = existingAttachments != null && !existingAttachments.isEmpty();
        if (hasAttachmentDtos) {
            DigitalResource attachment = null;
            List<DigitalResource> newAttachments = new ArrayList<>();
            for (DigitalResourceDto attachmentDto : attachmentDtos) {
                attachment = digitalResourceService.findByCode(attachmentDto.getCode());
                if (attachment == null) {
                    throw new EntityDoesNotExistsException(DigitalResource.class, attachmentDto.getCode());
                }
                attachment = digitalResourceApi.convertFromDto(attachmentDto, attachment);
                newAttachments.add(attachment);
            }
            productTemplate.setAttachments(newAttachments);
        } else if (hasExistingAttachments) {
            productTemplate.setAttachments(null);
        }
    }

    protected void processProductChargeTemplate(ProductTemplateDto postData, ProductTemplate productTemplate) throws BusinessException, MeveoApiException {
        List<ProductChargeTemplate> newProductChargeTemplates = new ArrayList<>();
        ProductChargeTemplate productChargeTemplate = null;
        for (ProductChargeTemplateDto productChargeTemplateDto : postData.getProductChargeTemplates()) {
            productChargeTemplate = productChargeTemplateService.findByCode(productChargeTemplateDto.getCode());
            if (productChargeTemplate == null) {
                throw new EntityDoesNotExistsException(ProductChargeTemplate.class, productChargeTemplateDto.getCode());
            }
            productChargeTemplate.setProductTemplates(Arrays.asList(productTemplate));
            newProductChargeTemplates.add(productChargeTemplate);
        }

        List<ProductChargeTemplate> existingProductChargeTemplates = productTemplate.getProductChargeTemplates();
        boolean hasExistingProductChargeTemplates = existingProductChargeTemplates != null && !existingProductChargeTemplates.isEmpty();
        boolean hasNewProductChargeTemplates = !newProductChargeTemplates.isEmpty();

        if (hasNewProductChargeTemplates) {
            if (hasExistingProductChargeTemplates) {
                List<ProductChargeTemplate> productChargeTemplatesForRemoval = new ArrayList<>(existingProductChargeTemplates);
                productChargeTemplatesForRemoval.removeAll(newProductChargeTemplates);
                productTemplate.getProductChargeTemplates().removeAll(productChargeTemplatesForRemoval);
            }
            newProductChargeTemplates.removeAll(existingProductChargeTemplates);
            productTemplate.getProductChargeTemplates().addAll(newProductChargeTemplates);
        } else if (hasExistingProductChargeTemplates) {
            productTemplate.getProductChargeTemplates().removeAll(existingProductChargeTemplates);
        }
    }

}
