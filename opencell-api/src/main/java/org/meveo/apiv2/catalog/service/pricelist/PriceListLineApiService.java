package org.meveo.apiv2.catalog.service.pricelist;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.PriceListLineDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PriceListLineService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PriceListLineApiService {

    @Inject
    private PriceListService priceListService;

    @Inject
    private PriceListLineService priceListLineService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private ProductService productService;

    @Inject
    private ProductLineService productLineService;

    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    public Long create(PriceListLineDto postDto) {

        List<String> missingFields = new ArrayList<>();
        if(StringUtils.isBlank(postDto.getPriceListCode())) {
            missingFields.add("priceListCode");
        }
        if(StringUtils.isBlank(postDto.getChargeTemplateCode())) {
            missingFields.add("chargeTemplateCode");
        }
        if(!missingFields.isEmpty()) {
            throw new MissingParameterException(missingFields);
        }

        PriceListLine entityToSave = new PriceListLine();

        PriceList priceList = priceListService.findByCode(postDto.getPriceListCode());
        if(priceList == null) {
            throw new EntityDoesNotExistsException(PriceList.class, postDto.getPriceListCode());
        }
        entityToSave.setPriceList(priceList);

        int linesIndex = priceList.getLines() != null ? priceList.getLines().size()+1 : 1;
        entityToSave.setCode(priceList.getCode() + "-" + linesIndex);

        if(StringUtils.isNotBlank(postDto.getProductCategoryCode())) {
            ProductLine productLine = productLineService.findByCode(postDto.getProductCategoryCode());
            if(productLine == null) {
                throw new EntityDoesNotExistsException(ProductLine.class, postDto.getProductCategoryCode());
            }
            entityToSave.setProductCategory(productLine);
        }

        if(StringUtils.isNotBlank(postDto.getProductCode())) {
            Product product = productService.findByCode(postDto.getProductCode());
            if(product == null) {
                throw new EntityDoesNotExistsException(Product.class, postDto.getProductCode());
            }
            entityToSave.setProduct(product);
        }

        if(StringUtils.isNotBlank(postDto.getOfferCategoryCode())) {
            OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postDto.getOfferCategoryCode());
            if(offerTemplateCategory == null) {
                throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postDto.getOfferCategoryCode());
            }
            entityToSave.setOfferCategory(offerTemplateCategory);
        }

        if(StringUtils.isNotBlank(postDto.getOfferTemplateCode())) {
            OfferTemplate offerTemplate = offerTemplateService.findByCode(postDto.getOfferTemplateCode());
            if(offerTemplate == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class, postDto.getOfferTemplateCode());
            }
            entityToSave.setOfferTemplate(offerTemplate);
        }

        if(StringUtils.isNotBlank(postDto.getChargeTemplateCode())) {
            ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(postDto.getChargeTemplateCode());
            if(chargeTemplate == null) {
                throw new EntityDoesNotExistsException(ChargeTemplate.class, postDto.getChargeTemplateCode());
            }
            entityToSave.setChargeTemplate(chargeTemplate);
        }

        if(postDto.getRate() != null) {
            entityToSave.setRate(BigDecimal.valueOf(postDto.getRate()));
        }
        entityToSave.setAmount(postDto.getAmount());
        entityToSave.setApplicationEl(postDto.getApplicationEl());
        entityToSave.setDescription(postDto.getDescription());

        priceListLineService.create(entityToSave);

        return entityToSave.getId();
    }

}
