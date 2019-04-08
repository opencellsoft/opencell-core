package org.meveo.apiv2.ordering.product;

import org.meveo.apiv2.NotYetImplementedResource;
import org.meveo.apiv2.ResourceMapper;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.*;
import org.meveo.model.shared.DateUtils;

import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class ProductMapper extends ResourceMapper<Product, ProductTemplate> {
    @Override
    public Product toResource(ProductTemplate productTemplate) {
        return ImmutableProduct.builder().id(productTemplate.getId()).description(productTemplate.getDescription())
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