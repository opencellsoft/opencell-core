package org.meveo.service.script.product;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.catalog.impl.ProductChargeTemplateMappingService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderProductService;
import org.meveo.service.script.Script;

import javax.transaction.Transactional;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OneShotOtherTypeMigrationScript extends Script {

    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());

    private OneShotChargeTemplateService oneShotChargeTemplateService = (OneShotChargeTemplateService) getServiceInterface(OneShotChargeTemplateService.class.getSimpleName());

    private ProductChargeTemplateMappingService productChargeTemplateMappingService = (ProductChargeTemplateMappingService) getServiceInterface(ProductChargeTemplateMappingService.class.getSimpleName());

    private OrderProductService orderProductService = (OrderProductService) getServiceInterface(OrderProductService.class.getSimpleName());

    private PricePlanMatrixService pricePlanMatrixService = (PricePlanMatrixService) getServiceInterface(PricePlanMatrixService.class.getSimpleName());

    private PricePlanMatrixVersionService pricePlanMatrixVersionService = (PricePlanMatrixVersionService) getServiceInterface(PricePlanMatrixVersionService.class.getSimpleName());

    @Override
    @Transactional
    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("{} EXECUTE context {}", this.getClass().getCanonicalName(), context);

        try {
            List<CommercialOrder> commercialOrderList = commercialOrderService.findWithInvoicingPlanNotNull();

            for (CommercialOrder commercialOrder : commercialOrderList) {
                List<OrderOffer> orderOffersList = commercialOrder.getOffers();
                for (OrderOffer orderOffer : orderOffersList) {
                    List<OrderProduct> orderProducts = orderOffer.getProducts();
                    for (OrderProduct orderProduct : orderProducts) {
                        if (orderProduct.getProductVersion() != null && orderProduct.getProductVersion().getProduct() != null) {

                            List<ProductChargeTemplateMapping> productChargeTemplates = orderProduct.getProductVersion().getProduct().getProductCharges();
                            for (ProductChargeTemplateMapping productChargeTemplateMapping : productChargeTemplates) {

                                if (productChargeTemplateMapping.getChargeTemplate().getChargeMainType().equals(ChargeTemplate.ChargeMainTypeEnum.ONESHOT)) {

                                    OneShotChargeTemplate oneShotChargeTemplate = ((OneShotChargeTemplate) Hibernate.unproxy(productChargeTemplateMapping.getChargeTemplate()));

                                    if (oneShotChargeTemplate.getOneShotChargeTemplateType().equals(OneShotChargeTemplateTypeEnum.OTHER)) {

                                        String codePrefix = UUID.randomUUID().toString().substring(0,6).toUpperCase();
                                        String newChargeCode = oneShotChargeTemplate.getCode() + "_INV_PLAN_" + codePrefix;
                                        OneShotChargeTemplate invoicingPLanOneShotChargeTemplate = createInvoicingPlanOneShotCharge(oneShotChargeTemplate, newChargeCode);

                                        oneShotChargeTemplateService.create(invoicingPLanOneShotChargeTemplate);
                                        setPricePlans(oneShotChargeTemplate,codePrefix,invoicingPLanOneShotChargeTemplate);

                                        ProductChargeTemplateMapping<OneShotChargeTemplate> invoicingPLanChargeMapping = createProductChargeMapping(orderProduct, productChargeTemplateMapping, invoicingPLanOneShotChargeTemplate);
                                        productChargeTemplateMappingService.create(invoicingPLanChargeMapping);

                                    }
                                }
                            }
                        }

                    }
                }


            }

        } catch (Exception exception) {
            log.error("Error : {}", exception.getMessage(), exception);
        }

    }

    private void setPricePlans(OneShotChargeTemplate oneShotChargeTemplate, String codePrefix, OneShotChargeTemplate invoicingPLanOneShotChargeTemplate) {
        List<PricePlanMatrix> pricePlanMatrixList = pricePlanMatrixService.getActivePricePlansByChargeCode(oneShotChargeTemplate.getCode());
        for(PricePlanMatrix pricePlanMatrix : pricePlanMatrixList){
            PricePlanMatrix duplicatedPricePlanMatrix = createPricePlanMatrix(pricePlanMatrix, codePrefix, invoicingPLanOneShotChargeTemplate);
            pricePlanMatrixService.create(duplicatedPricePlanMatrix);
            duplicatedPricePlanMatrix.setVersions(createMatrixPlanVersions(pricePlanMatrix.getVersions(),duplicatedPricePlanMatrix,codePrefix));
            pricePlanMatrixService.update(duplicatedPricePlanMatrix);
        }
    }

    private PricePlanMatrix createPricePlanMatrix(PricePlanMatrix pricePlanMatrix, String codePrefix, ChargeTemplate newChargeTemplate) {
        PricePlanMatrix newPricePlanMatrix = new PricePlanMatrix();
        newPricePlanMatrix.setTradingCurrency(pricePlanMatrix.getTradingCurrency());
        newPricePlanMatrix.setSeller(pricePlanMatrix.getSeller());
        newPricePlanMatrix.setValidityFrom(pricePlanMatrix.getValidityFrom());
        newPricePlanMatrix.setAmountWithTaxEL(pricePlanMatrix.getAmountWithTaxEL());
        newPricePlanMatrix.setCode(pricePlanMatrix.getCode() + "_" + codePrefix);
        newPricePlanMatrix.setAmountWithTax(pricePlanMatrix.getAmountWithTax());
        newPricePlanMatrix.setAmountWithoutTax(pricePlanMatrix.getAmountWithoutTax());
        newPricePlanMatrix.setOfferTemplate(pricePlanMatrix.getOfferTemplate());
        newPricePlanMatrix.setChargeTemplates(Set.of(newChargeTemplate));
        newPricePlanMatrix.setDiscountPlanItems(List.copyOf(pricePlanMatrix.getDiscountPlanItems()));
        newPricePlanMatrix.setContractItems(List.copyOf(pricePlanMatrix.getContractItems()));
        newPricePlanMatrix.setValidityCalendar(pricePlanMatrix.getValidityCalendar());
        newPricePlanMatrix.setValidityDate(pricePlanMatrix.getValidityDate());
        newPricePlanMatrix.setCriteriaEL(pricePlanMatrix.getCriteriaEL());
        newPricePlanMatrix.setPriority(pricePlanMatrix.getPriority());
        newPricePlanMatrix.setEndSubscriptionDate(pricePlanMatrix.getEndSubscriptionDate());
        newPricePlanMatrix.setStartSubscriptionDate(pricePlanMatrix.getStartSubscriptionDate());
        newPricePlanMatrix.setCriteriaEL(pricePlanMatrix.getCriteriaEL());
        newPricePlanMatrix.setMinQuantity(pricePlanMatrix.getMinQuantity());
        newPricePlanMatrix.setWoDescriptionEL(pricePlanMatrix.getWoDescriptionEL());

        return newPricePlanMatrix;

    }

    private List<PricePlanMatrixVersion> createMatrixPlanVersions(List<PricePlanMatrixVersion> versions, PricePlanMatrix pricePlanMatrix, String codePrefix) {
        List<PricePlanMatrixVersion> duplicatedVersions = new ArrayList<>();

        for (PricePlanMatrixVersion pricePlanMatrixVersion : versions) {
            PricePlanMatrixVersion duplicatedPriceVersion = new PricePlanMatrixVersion();
            duplicatedPriceVersion.setPricePlanMatrix(pricePlanMatrix);
            duplicatedPriceVersion.setMatrix(pricePlanMatrixVersion.isMatrix());
            duplicatedPriceVersion.setPrice(pricePlanMatrixVersion.getPrice());
            duplicatedPriceVersion.setPriceVersionType(pricePlanMatrixVersion.getPriceVersionType());
            duplicatedPriceVersion.setStatus(pricePlanMatrixVersion.getStatus());
            duplicatedPriceVersion.setValidity(pricePlanMatrixVersion.getValidity());
            duplicatedPriceVersion.setColumns(Set.copyOf(pricePlanMatrixVersion.getColumns()));
            duplicatedPriceVersion.setLines(Set.copyOf(pricePlanMatrixVersion.getLines()));
            duplicatedPriceVersion.setPriceEL(pricePlanMatrixVersion.getPriceEL());
            duplicatedPriceVersion.setStatusDate(pricePlanMatrixVersion.getStatusDate());
            duplicatedPriceVersion.setPriority(pricePlanMatrixVersion.getPriority());
            duplicatedPriceVersion.setLabel(pricePlanMatrixVersion.getLabel() + "_INV_PLAN_" + codePrefix);

            pricePlanMatrixVersionService.create(duplicatedPriceVersion);
            duplicatedVersions.add(duplicatedPriceVersion);
        }
        return duplicatedVersions;
    }

    private static ProductChargeTemplateMapping createProductChargeMapping(OrderProduct orderProduct, ProductChargeTemplateMapping productChargeTemplateMapping, OneShotChargeTemplate invoicingPLanOneShotChargeTemplate) {
        ProductChargeTemplateMapping<OneShotChargeTemplate> invoicingPLanChargeMapping = new ProductChargeTemplateMapping();
        invoicingPLanChargeMapping.setChargeTemplate(invoicingPLanOneShotChargeTemplate);
        invoicingPLanChargeMapping.setProduct(orderProduct.getProductVersion().getProduct());
        invoicingPLanChargeMapping.setCounterTemplate(productChargeTemplateMapping.getCounterTemplate());
        invoicingPLanChargeMapping.setWalletTemplates(List.copyOf(productChargeTemplateMapping.getWalletTemplates()));
        invoicingPLanChargeMapping.setAccumulatorCounterTemplates(List.copyOf(productChargeTemplateMapping.getAccumulatorCounterTemplates()));
        invoicingPLanChargeMapping.setVersion(productChargeTemplateMapping.getVersion());
        return invoicingPLanChargeMapping;
    }

    private static OneShotChargeTemplate createInvoicingPlanOneShotCharge(OneShotChargeTemplate oneShotChargeTemplate, String newChargeCode) throws ValidationException {
        OneShotChargeTemplate invoicingPLanOneShotChargeTemplate = new OneShotChargeTemplate();
        invoicingPLanOneShotChargeTemplate.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.INVOICING_PLAN);
        invoicingPLanOneShotChargeTemplate.setStatus(oneShotChargeTemplate.getStatus());
        invoicingPLanOneShotChargeTemplate.setRatingScript(oneShotChargeTemplate.getRatingScript());
        invoicingPLanOneShotChargeTemplate.setType(oneShotChargeTemplate.getType());
        invoicingPLanOneShotChargeTemplate.setImmediateInvoicing(oneShotChargeTemplate.getImmediateInvoicing());
        invoicingPLanOneShotChargeTemplate.setEdrTemplates(List.copyOf(oneShotChargeTemplate.getEdrTemplates()));
        invoicingPLanOneShotChargeTemplate.setRoundingMode(oneShotChargeTemplate.getRoundingMode());
        invoicingPLanOneShotChargeTemplate.setRevenueRecognitionRule(oneShotChargeTemplate.getRevenueRecognitionRule());
        invoicingPLanOneShotChargeTemplate.setAttributes(Set.copyOf(oneShotChargeTemplate.getAttributes()));
        invoicingPLanOneShotChargeTemplate.setDescriptionI18n(oneShotChargeTemplate.getDescriptionI18n());
        invoicingPLanOneShotChargeTemplate.setAmountEditable(oneShotChargeTemplate.getAmountEditable());
        invoicingPLanOneShotChargeTemplate.setFilterExpression(oneShotChargeTemplate.getFilterExpression());
        invoicingPLanOneShotChargeTemplate.setInputUnitDescription(oneShotChargeTemplate.getInputUnitDescription());
        invoicingPLanOneShotChargeTemplate.setInternalNote(oneShotChargeTemplate.getInternalNote());
        invoicingPLanOneShotChargeTemplate.setProductCharges(List.copyOf(oneShotChargeTemplate.getProductCharges()));
        invoicingPLanOneShotChargeTemplate.setInvoiceSubCategory(oneShotChargeTemplate.getInvoiceSubCategory());
        invoicingPLanOneShotChargeTemplate.setRatingUnitDescription(oneShotChargeTemplate.getRatingUnitDescription());
        invoicingPLanOneShotChargeTemplate.setSortIndexEl(oneShotChargeTemplate.getSortIndexEl());
        invoicingPLanOneShotChargeTemplate.setTaxClass(oneShotChargeTemplate.getTaxClass());
        invoicingPLanOneShotChargeTemplate.setTaxClassEl(oneShotChargeTemplate.getTaxClassEl());
        invoicingPLanOneShotChargeTemplate.setActive(oneShotChargeTemplate.isActive());
        invoicingPLanOneShotChargeTemplate.setDropZeroWo(oneShotChargeTemplate.isDropZeroWo());
        invoicingPLanOneShotChargeTemplate.setNotified(oneShotChargeTemplate.isNotified());
        invoicingPLanOneShotChargeTemplate.setCode(newChargeCode);
        return invoicingPLanOneShotChargeTemplate;
    }
}
