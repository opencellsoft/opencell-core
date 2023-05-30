package org.meveo.apiv2.catalog.service.pricelist;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.PriceListLineDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PriceListLineService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PriceListLineApiService extends BaseApi {

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

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    public Long create(PriceListLineDto postDto) {

        checkMandatoryFields(postDto);

        PriceListLine entityToSave = new PriceListLine();

        PriceList priceList = priceListService.findByCode(postDto.getPriceListCode());
        if(priceList == null) {
            throw new EntityDoesNotExistsException(PriceList.class, postDto.getPriceListCode());
        } else if (!PriceListStatusEnum.DRAFT.equals(priceList.getStatus())) {
            throw new BusinessApiException("PriceList Line cannot be created for PriceList status other than DRAFT");
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
        entityToSave.setPriceListType(postDto.getPriceListRateType());
        entityToSave.setAmount(postDto.getAmount());
        entityToSave.setApplicationEl(postDto.getApplicationEl());
        entityToSave.setDescription(postDto.getDescription());

        if(StringUtils.isNotBlank(postDto.getPricePlanCode())) {
            PricePlanMatrix ppm = pricePlanMatrixService.findByCode(postDto.getPricePlanCode());
            if(ppm == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrix.class, postDto.getCode());
            }
            entityToSave.setPricePlan(ppm);
        }


        try {
            populateCustomFields(postDto.getCustomFields(), entityToSave, true);
            priceListLineService.create(entityToSave);
        } catch(BusinessException e) {
            throw new MeveoApiException(e);
        }

        return entityToSave.getId();
    }

    public Long update(Long priceListLineId, PriceListLineDto postDto) {

        checkMandatoryFields(postDto);

        PriceListLine priceListLineToUpdate = priceListLineService.findById(priceListLineId);
        if(priceListLineToUpdate == null) {
            throw new EntityDoesNotExistsException(PriceListLine.class, priceListLineId);
        } else if (!PriceListStatusEnum.DRAFT.equals(priceListLineToUpdate.getPriceList().getStatus())) {
            throw new BusinessApiException("PriceList Line cannot be updated for PriceList status other than DRAFT");
        } else if(StringUtils.isNotBlank(postDto.getCode()) && !priceListLineToUpdate.getCode().equals(postDto.getCode()) && priceListLineService.findByCode(postDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(PriceListLine.class, postDto.getCode());
        } else if (StringUtils.isNotBlank(postDto.getCode())) {
            priceListLineToUpdate.setCode(postDto.getCode());
        }


        if(postDto.getProductCategoryCode() != null) {
            if(StringUtils.isNotBlank(postDto.getProductCategoryCode())) {
                ProductLine productLine = productLineService.findByCode(postDto.getProductCategoryCode());
                if(productLine == null) {
                    throw new EntityDoesNotExistsException(ProductLine.class, postDto.getProductCategoryCode());
                }
                priceListLineToUpdate.setProductCategory(productLine);
            } else {
                priceListLineToUpdate.setProductCategory(null);
            }
        }

        if(postDto.getProductCode() != null) {
            if(StringUtils.isNotBlank(postDto.getProductCode())) {
                Product product = productService.findByCode(postDto.getProductCode());
                if (product == null) {
                    throw new EntityDoesNotExistsException(Product.class, postDto.getProductCode());
                }
                priceListLineToUpdate.setProduct(product);
            } else {
                priceListLineToUpdate.setProduct(null);
            }
        }

        if(postDto.getOfferCategoryCode() != null) {
            if(StringUtils.isNotBlank(postDto.getOfferCategoryCode())) {
                OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postDto.getOfferCategoryCode());
                if (offerTemplateCategory == null) {
                    throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postDto.getOfferCategoryCode());
                }
                priceListLineToUpdate.setOfferCategory(offerTemplateCategory);
            } else {
                priceListLineToUpdate.setOfferCategory(null);
            }
        }

        if(postDto.getOfferTemplateCode() != null) {
            if(StringUtils.isNotBlank(postDto.getOfferTemplateCode())) {
                OfferTemplate offerTemplate = offerTemplateService.findByCode(postDto.getOfferTemplateCode());
                if (offerTemplate == null) {
                    throw new EntityDoesNotExistsException(OfferTemplate.class, postDto.getOfferTemplateCode());
                }
                priceListLineToUpdate.setOfferTemplate(offerTemplate);
            } else {
                priceListLineToUpdate.setOfferTemplate(null);
            }
        }

        if(postDto.getChargeTemplateCode() != null) {
            if(StringUtils.isNotBlank(postDto.getChargeTemplateCode())) {
                ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(postDto.getChargeTemplateCode());
                if (chargeTemplate == null) {
                    throw new EntityDoesNotExistsException(ChargeTemplate.class, postDto.getChargeTemplateCode());
                }
                priceListLineToUpdate.setChargeTemplate(chargeTemplate);
            }
        }

        if(postDto.getRate() != null) {
            priceListLineToUpdate.setRate(BigDecimal.valueOf(postDto.getRate()));
        } else {
            priceListLineToUpdate.setRate(null);
        }

        if(postDto.getPriceListRateType() != null) {
            priceListLineToUpdate.setPriceListType(postDto.getPriceListRateType());
        }
        if(postDto.getAmount() != null) {
            priceListLineToUpdate.setAmount(postDto.getAmount());
        }

        if(postDto.getDescription() != null) {
            if(StringUtils.isNotBlank(postDto.getDescription())) {
                priceListLineToUpdate.setDescription(postDto.getDescription());
            } else {
                priceListLineToUpdate.setDescription(null);
            }
        }
        if(postDto.getApplicationEl() != null) {
            if(StringUtils.isNotBlank(postDto.getApplicationEl())) {
                priceListLineToUpdate.setApplicationEl(postDto.getApplicationEl());
            } else {
                priceListLineToUpdate.setApplicationEl(null);
            }
        }

        if(postDto.getPricePlanCode() != null) {
            if(StringUtils.isNotBlank(postDto.getPricePlanCode())) {
                PricePlanMatrix ppm = pricePlanMatrixService.findByCode(postDto.getPricePlanCode());
                if(ppm == null) {
                    throw new EntityDoesNotExistsException(PricePlanMatrix.class, postDto.getCode());
                }
                priceListLineToUpdate.setPricePlan(ppm);
            } else {
                priceListLineToUpdate.setPricePlan(null);
            }
        }

        try {
            populateCustomFields(postDto.getCustomFields(), priceListLineToUpdate, false);
            priceListLineService.update(priceListLineToUpdate);
        } catch(BusinessException e) {
            throw new MeveoApiException(e);
        }

        return priceListLineToUpdate.getId();
    }

    private static void checkMandatoryFields(PriceListLineDto postDto) {
        List<String> missingFields = new ArrayList<>();
        if(StringUtils.isBlank(postDto.getPriceListCode())) {
            missingFields.add("priceListCode");
        }
        if(StringUtils.isBlank(postDto.getChargeTemplateCode())) {
            missingFields.add("chargeTemplateCode");
        }
        if(StringUtils.isBlank(postDto.getPriceListRateType())) {
            missingFields.add("priceListRateType");
        }
        if(!missingFields.isEmpty()) {
            throw new MissingParameterException(missingFields);
        }
    }

    public void delete(Long priceListLineId) {
        PriceListLine priceListLineToDelete = priceListLineService.findById(priceListLineId);
        if(priceListLineToDelete == null) {
            throw new EntityDoesNotExistsException(PriceListLine.class, priceListLineId);
        } else if (!PriceListStatusEnum.DRAFT.equals(priceListLineToDelete.getPriceList().getStatus())) {
            throw new BusinessApiException("PriceList Line cannot be deleted for PriceList status other than DRAFT");
        }
        priceListLineService.remove(priceListLineToDelete);
    }
}
