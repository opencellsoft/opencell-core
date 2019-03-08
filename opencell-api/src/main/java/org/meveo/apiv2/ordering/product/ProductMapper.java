package org.meveo.apiv2.ordering.product;

import org.meveo.apiv2.NotYetImplementedResource;
import org.meveo.apiv2.common.LinkGenerator;
import org.meveo.apiv2.models.ImmutableProduct;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Product;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.BaseEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.*;
import org.meveo.model.shared.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class ProductMapper {
    static ImmutableProduct toProduct(ProductTemplate productOffering) {

        return ImmutableProduct.builder().id(productOffering.getId()).description(productOffering.getDescription())
                .addOfferTemplateCategories(getImmutableResources(productOffering.getOfferTemplateCategories(), NotYetImplementedResource.class))
                .addChannels(getImmutableResources(productOffering.getChannels(), NotYetImplementedResource.class))
                .addWalletTemplates(getImmutableResources(productOffering.getWalletTemplates(), NotYetImplementedResource.class))
                .addProductChargeTemplates(getImmutableResources(productOffering.getProductChargeTemplates(), NotYetImplementedResource.class))
                .businessProductModel(getImmutableResource(NotYetImplementedResource.class, productOffering.getBusinessProductModel()))
                .invoicingCalendar(getImmutableResource(NotYetImplementedResource.class, productOffering.getInvoicingCalendar()))
                .name(productOffering.getName())
                .code(productOffering.getCode()).lifeCycleStatus(productOffering.getLifeCycleStatus().getValue())
                .imageUrl(productOffering.getImagePath())
                .validFrom(toFormattedDate(productOffering, () -> productOffering.getValidity().getFrom()))
                .validTo(toFormattedDate(productOffering, () -> productOffering.getValidity().getTo()))
                .isDisabled(productOffering.isDisabled())
                .build();
    }

    private static Resource[] getImmutableResources(List<? extends BaseEntity> elements, Class resource) {
        return elements==null ? new Resource[]{} : elements.stream()
                .map(element -> getImmutableResource(resource, element))
                .toArray(Resource[]::new);
    }

    private static  <T extends BaseEntity> ImmutableResource getImmutableResource(Class resource,T element) {
        LinkGenerator.SelfLinkGenerator resourceLinkBuilder = new LinkGenerator.SelfLinkGenerator(resource)
                .withGetAction().withPostAction().withPatchAction().withDeleteAction();
        return element != null ? ImmutableResource.builder()
                .id(element.getId())
                .addLinks(resourceLinkBuilder.withId(element.getId()).build())
                .build() : ImmutableResource.builder().build();
    }

    private static String toFormattedDate(ProductOffering productOffering, Supplier<Date> date) {
        return productOffering.getValidity() != null ? DateUtils.formatDateWithPattern(date.get(), DateUtils.DATE_PATTERN) : null;
    }

    static ProductTemplate toProductTemplate(Product product) {
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