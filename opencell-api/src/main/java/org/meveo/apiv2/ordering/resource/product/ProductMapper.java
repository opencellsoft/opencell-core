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

package org.meveo.apiv2.ordering.resource.product;

import com.google.common.annotations.VisibleForTesting;
import org.meveo.apiv2.generic.NotYetImplementedResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.shared.DateUtils;

import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Collectors;
@VisibleForTesting
public class ProductMapper extends ResourceMapper<Product, ProductTemplate> {
    @Override
    public Product toResource(ProductTemplate productTemplate) {
        return ImmutableProduct.builder().id(productTemplate.getId()).description(productTemplate.getDescription())
                .longDescription(productTemplate.getLongDescription())
                .addOfferTemplateCategories(getImmutableResources(productTemplate.getOfferTemplateCategories(), NotYetImplementedResource.class))
                .addChannels(getImmutableResources(productTemplate.getChannels(), NotYetImplementedResource.class))
                .addWalletTemplates(getImmutableResources(productTemplate.getWalletTemplates(), NotYetImplementedResource.class))
                .addProductChargeTemplates(getImmutableResources(productTemplate.getProductChargeTemplates(), NotYetImplementedResource.class))
                .businessProductModel(buildImmutableResource(NotYetImplementedResource.class, productTemplate.getBusinessProductModel()))
                .invoicingCalendar(buildImmutableResource(NotYetImplementedResource.class, productTemplate.getInvoicingCalendar()))
                .name(productTemplate.getName())
                .code(productTemplate.getCode()).lifeCycleStatus(productTemplate.getLifeCycleStatus().getValue())
                .imageUrl(productTemplate.getImagePath())
                .validFrom(toFormattedDate(productTemplate, () -> productTemplate.getValidity().getFrom()))
                .validTo(toFormattedDate(productTemplate, () -> productTemplate.getValidity().getTo()))
                .isDisabled(productTemplate.isDisabled())
                .build();
    }

    private static String toFormattedDate(ProductOffering productOffering, Supplier<Date> date) {
        return productOffering.getValidity() != null ? DateUtils.formatDateWithPattern(date.get(), DateUtils.DATE_PATTERN) : null;
    }

    @Override
    public ProductTemplate toEntity(Product product) {
        ProductTemplate productTemplate = new ProductTemplate();
        if(product.getId() != null){
            productTemplate.setId(product.getId());
        }
        productTemplate.setCode(product.getCode());
        productTemplate.setDescription(product.getDescription());
        productTemplate.setLongDescription(product.getLongDescription());
        productTemplate.setName(product.getName());
        productTemplate.setValidity(new DatePeriod(DateUtils.parseDateWithPattern(product.getValidFrom(), DateUtils.DATE_PATTERN),
                DateUtils.parseDateWithPattern(product.getValidTo(), DateUtils.DATE_PATTERN)));
        if(product.getLifeCycleStatus() != null) {
            productTemplate.setLifeCycleStatus(LifeCycleStatusEnum.valueOf(product.getLifeCycleStatus()));
        }
        if (product.isDisabled() != null) {
            productTemplate.setDisabled(product.isDisabled());
        }
        productTemplate.setChannels(null);
        if(product.getChannels() != null) {
            productTemplate.setChannels(product.getChannels().stream().map(resource -> {
                Channel channel = new Channel();
                channel.setId(resource.getId());
                return channel;
            }).collect(Collectors.toList()));
        }
        productTemplate.setOfferTemplateCategories(null);
        if(product.getOfferTemplateCategories() != null) {
            productTemplate.setOfferTemplateCategories(product.getOfferTemplateCategories().stream().map(resource -> {
                OfferTemplateCategory offerTemplateCategory = new OfferTemplateCategory();
                offerTemplateCategory.setId(resource.getId());
                return offerTemplateCategory;
            }).collect(Collectors.toList()));
        }
        return productTemplate;
    }

}