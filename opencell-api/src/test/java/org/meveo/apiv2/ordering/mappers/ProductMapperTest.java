package org.meveo.apiv2.ordering.mappers;

import org.junit.Test;
import org.meveo.apiv2.ordering.product.Product;
import org.meveo.apiv2.ordering.product.ProductMapper;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.shared.DateUtils;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ProductMapperTest implements ResourceMapperTest{
    @Test
    public void mapDtoToEntityAndEntityToDtoTest() {
        ProductMapper productMapper = new ProductMapper();
        ProductTemplate productTemplate = generateEntity();

        Product product = productMapper.toResource(productTemplate);
        ProductTemplate productTemplateFromJson = productMapper.toEntity(product);

        assertSame(productTemplateFromJson.getId(), productTemplate.getId());
        assertEquals(productTemplateFromJson.getCode(), productTemplate.getCode());
        assertEquals(productTemplateFromJson.getDescription(), productTemplate.getDescription());
        assertEquals(productTemplateFromJson.getLongDescription(), productTemplate.getLongDescription());
        assertEquals(productTemplateFromJson.getName(), productTemplate.getName());
        assertEquals(productTemplateFromJson.getValidity(), productTemplate.getValidity());
        assertEquals(productTemplateFromJson.getChannels(), productTemplate.getChannels());
        assertEquals(productTemplateFromJson.getOfferTemplateCategories(), productTemplate.getOfferTemplateCategories());
    }

    public ProductTemplate generateEntity() {
        ProductTemplate productTemplate = new ProductTemplate();
        productTemplate.setId(123L);
        productTemplate.setCode("CODE");
        productTemplate.setDescription("Description");
        productTemplate.setLongDescription("Long description");
        productTemplate.setName("Name");
        productTemplate.setValidity(new DatePeriod(DateUtils.parseDateWithPattern("2019-01-01",
                DateUtils.DATE_PATTERN), DateUtils.parseDateWithPattern("2019-12-01", DateUtils.DATE_PATTERN)));
        productTemplate.setLifeCycleStatus(LifeCycleStatusEnum.valueOf("IN_STUDY"));
        productTemplate.setDisabled(true);

        Channel channel = new Channel();
        channel.setId(234L);
        productTemplate.setChannels(Collections.singletonList(channel));

        OfferTemplateCategory offerTemplateCategory = new OfferTemplateCategory();
        offerTemplateCategory.setId(345L);
        productTemplate.setOfferTemplateCategories(Collections.singletonList(offerTemplateCategory));
        return productTemplate;
    }
}
