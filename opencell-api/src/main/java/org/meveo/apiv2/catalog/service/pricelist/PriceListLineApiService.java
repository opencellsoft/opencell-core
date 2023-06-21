package org.meveo.apiv2.catalog.service.pricelist;

import org.apache.commons.collections4.CollectionUtils;
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
import org.meveo.model.catalog.ChargeTemplateStatusEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.model.shared.RegexUtils;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        if (!RegexUtils.checkCode(postDto.getCode())) {
            throw new BusinessApiException("PriceList code should not contain special characters");
        }

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

            checkOfferTemplateCompatibility(entityToSave, offerTemplate);

            entityToSave.setOfferTemplate(offerTemplate);
        }

        if(StringUtils.isNotBlank(postDto.getProductCategoryCode())) {
            ProductLine productLine = productLineService.findByCode(postDto.getProductCategoryCode());
            if(productLine == null) {
                throw new EntityDoesNotExistsException(ProductLine.class, postDto.getProductCategoryCode());
            }

            checkProductLineCompatibility(entityToSave, productLine);

            entityToSave.setProductCategory(productLine);
        }

        if(StringUtils.isNotBlank(postDto.getProductCode())) {
            Product product = productService.findByCode(postDto.getProductCode());
            if(product == null) {
                throw new EntityDoesNotExistsException(Product.class, postDto.getProductCode());
            }

            checkProductCompatibility(entityToSave, product);

            entityToSave.setProduct(product);
        }

        if(StringUtils.isNotBlank(postDto.getChargeTemplateCode())) {
            ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(postDto.getChargeTemplateCode());
            if(chargeTemplate == null) {
                throw new EntityDoesNotExistsException(ChargeTemplate.class, postDto.getChargeTemplateCode());
            } else if (!Arrays.asList(ChargeTemplateStatusEnum.DRAFT, ChargeTemplateStatusEnum.ACTIVE).contains(chargeTemplate.getStatus())) {
                throw new BusinessApiException("Only Draft and Active charges can be used");
            }

            checkChargeTemplateCompatibility(entityToSave, chargeTemplate);

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
                throw new EntityDoesNotExistsException(PricePlanMatrix.class, postDto.getPricePlanCode());
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

    private static void checkOfferTemplateCompatibility(PriceListLine entityToSave, OfferTemplate offerTemplate) {
        if(entityToSave.getOfferCategory() != null &&
            !CollectionUtils.emptyIfNull(offerTemplate.getOfferTemplateCategories()).contains(entityToSave.getOfferCategory())) {
                throw new BusinessApiException("The selected Offer isn't compatible with the Offer category");
        }
    }

    private static void checkProductLineCompatibility(PriceListLine entityToSave, ProductLine productLine) {
        // Check Offer Compatibility
        if(entityToSave.getOfferTemplate() != null) {
            boolean check = CollectionUtils.emptyIfNull(entityToSave.getOfferTemplate().getOfferComponents())
                    .stream()
                    .map(OfferComponent::getProduct)
                    .filter(Objects::nonNull)
                    .map(Product::getProductLine)
                    .filter(Objects::nonNull)
                    .anyMatch(pl -> pl.getCode().equals(productLine.getCode()));
            if(!check){
                throw new BusinessApiException("The selected Product Category isn't compatible with the Offer Template");
            }
        }

        // Check Offer category Compatibility
        if(entityToSave.getOfferCategory() != null) {
            boolean check = CollectionUtils.emptyIfNull(entityToSave.getOfferCategory().getProductOffering())
                    .stream()
                    .filter(po -> po instanceof OfferTemplate && ((OfferTemplate) po).getOfferComponents() != null)
                    .flatMap(po -> ((OfferTemplate) po).getOfferComponents().stream())
                    .map(OfferComponent::getProduct)
                    .filter(Objects::nonNull)
                    .map(Product::getProductLine)
                    .filter(Objects::nonNull)
                    .anyMatch(pc -> pc.getCode().equals(productLine.getCode()));
            if(!check){
                throw new BusinessApiException("The selected Product Category isn't compatible with the Offer Template");
            }
        }
    }

    private static void checkChargeTemplateCompatibility(PriceListLine entityToSave, ChargeTemplate chargeTemplate) {
        // Check product compatibility
        if(entityToSave.getProduct() != null) {
            boolean check = chargeTemplate.getProductCharges()
                    .stream()
                    .map(ProductChargeTemplateMapping::getProduct)
                    .filter(Objects::nonNull)
                    .anyMatch(p -> p.getCode().equals(entityToSave.getProduct().getCode()));
            if(!check) {
                throw new BusinessApiException("The selected ChargeTemplate isn't compatible with the Product");
            }
        }

        // Check ProductLine compatibility
        if(entityToSave.getProductCategory() != null) {
            boolean check =  CollectionUtils.emptyIfNull(chargeTemplate.getProductCharges())
                    .stream()
                    .map(ProductChargeTemplateMapping::getProduct)
                    .filter(Objects::nonNull)
                    .map(Product::getProductLine)
                    .filter(Objects::nonNull)
                    .anyMatch(pc -> pc.getCode().equals(entityToSave.getProductCategory().getCode()));
            if(!check) {
                throw new BusinessApiException("The selected ChargeTemplate isn't compatible with the Product category");
            }
        }

        // Check OfferTemplate compatibility
        if(entityToSave.getOfferTemplate() != null) {

            boolean check = CollectionUtils.emptyIfNull(entityToSave.getOfferTemplate().getOfferComponents())
                    .stream()
                    .flatMap(oc -> CollectionUtils.emptyIfNull(oc.getProduct().getProductCharges()).stream())
                    .map(pc -> pc.getChargeTemplate().getCode())
                    .anyMatch(c -> c.equals(chargeTemplate.getCode()));
            if(!check) {
                throw new BusinessApiException("The selected ChargeTemplate isn't compatible with the Offer template");
            }
        }

        // Check OfferTemplate compatibility
        if(entityToSave.getOfferCategory() != null) {

            boolean check = CollectionUtils.emptyIfNull(entityToSave.getOfferCategory().getProductOffering())
                    .stream()
                    .filter(po -> po instanceof OfferTemplate && ((OfferTemplate) po).getOfferComponents() != null)
                    .flatMap(po -> ((OfferTemplate) po).getOfferComponents().stream())
                    .flatMap(oc -> CollectionUtils.emptyIfNull(oc.getProduct().getProductCharges()).stream())
                    .map(pc -> pc.getChargeTemplate().getCode())
                    .anyMatch(c -> c.equals(chargeTemplate.getCode()));
            if(!check) {
                throw new BusinessApiException("The selected ChargeTemplate isn't compatible with the Offer Category");
            }
        }
    }

    private static void checkProductCompatibility(PriceListLine entityToSave, Product product) {
        if(entityToSave.getProductCategory() != null
                && (product.getProductLine() == null || !entityToSave.getProductCategory().getCode().equals(product.getProductLine().getCode()))
        ) {
            throw new BusinessApiException("The selected Product isn't compatible with the Product line");
        }

        if(entityToSave.getOfferTemplate() != null) {
            boolean check = CollectionUtils.emptyIfNull(entityToSave.getOfferTemplate().getOfferComponents())
                    .stream()
                    .map(oc -> oc.getProduct().getCode())
                    .filter(Objects::nonNull)
                    .anyMatch(c -> c.equals(product.getCode()));
            if(!check) {
                throw new BusinessApiException("The selected Product isn't compatible with the Offer template");
            }
        }

        if(entityToSave.getOfferCategory() != null) {
            boolean check = CollectionUtils.emptyIfNull(entityToSave.getOfferCategory().getProductOffering())
                    .stream()
                    .filter(po -> po instanceof OfferTemplate && ((OfferTemplate) po).getOfferComponents() != null)
                    .flatMap(po -> ((OfferTemplate) po).getOfferComponents().stream())
                    .map(oc -> oc.getProduct().getCode())
                    .anyMatch(c -> c.equals(product.getCode()));
            if(!check) {
                throw new BusinessApiException("The selected Product isn't compatible with the Offer category");
            }
        }
    }

    public Long update(Long priceListLineId, PriceListLineDto postDto) {

        checkMandatoryFields(postDto);

        if (!RegexUtils.checkCode(postDto.getCode())) {
            throw new BusinessApiException("PriceList code should not contain special characters");
        }

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

        if(postDto.getChargeTemplateCode() != null) {
            if(StringUtils.isNotBlank(postDto.getChargeTemplateCode())) {
                ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(postDto.getChargeTemplateCode());
                if (chargeTemplate == null) {
                    throw new EntityDoesNotExistsException(ChargeTemplate.class, postDto.getChargeTemplateCode());
                } else if (!Arrays.asList(ChargeTemplateStatusEnum.DRAFT, ChargeTemplateStatusEnum.ACTIVE).contains(chargeTemplate.getStatus())) {
                    throw new BusinessApiException("Only Draft and Active charges can be used");
                }
                priceListLineToUpdate.setChargeTemplate(chargeTemplate);
            }
        }

        // sanity checks
        if(priceListLineToUpdate.getOfferTemplate() != null) {
            checkOfferTemplateCompatibility(priceListLineToUpdate, priceListLineToUpdate.getOfferTemplate());
        }

        if(priceListLineToUpdate.getProductCategory() != null) {
            checkProductLineCompatibility(priceListLineToUpdate, priceListLineToUpdate.getProductCategory());
        }

        if(priceListLineToUpdate.getProduct() != null) {
            checkProductCompatibility(priceListLineToUpdate, priceListLineToUpdate.getProduct());
        }

        if(priceListLineToUpdate.getChargeTemplate()!= null) {
            checkChargeTemplateCompatibility(priceListLineToUpdate, priceListLineToUpdate.getChargeTemplate());
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
                    throw new EntityDoesNotExistsException(PricePlanMatrix.class, postDto.getPricePlanCode());
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
        if(StringUtils.isNotBlank(postDto.getOfferTemplateCode()) && StringUtils.isBlank(postDto.getProductCode())) {
            missingFields.add("productCode");
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
