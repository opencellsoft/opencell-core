package org.meveo.service.script.product;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateMappingService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderProductService;
import org.meveo.service.script.Script;

import java.util.List;
import java.util.Map;

public class OneShotOtherTypeMigrationScript extends Script {

    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());

    private OneShotChargeTemplateService oneShotChargeTemplateService = (OneShotChargeTemplateService) getServiceInterface(OneShotChargeTemplateService.class.getSimpleName());

    private ProductChargeTemplateMappingService productChargeTemplateMappingService = (ProductChargeTemplateMappingService) getServiceInterface(ProductChargeTemplateMappingService.class.getSimpleName());

    private OrderProductService orderProductService = (OrderProductService) getServiceInterface(OrderProductService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("{} EXECUTE TTTT context {}", this.getClass().getCanonicalName(), context);

        try {
            List<CommercialOrder> commercialOrderList = commercialOrderService.findWithInvoicingPlanNotNull();

            for (CommercialOrder commercialOrder : commercialOrderList) {
                List<OrderOffer> orderOffersList = commercialOrder.getOffers();
                for (OrderOffer orderOffer : orderOffersList) {
                    List<OrderProduct> orderProducts = orderOffer.getProducts();

                    for (OrderProduct orderProduct : orderProducts) {
                        if (orderProduct.getProductVersion() != null && orderProduct.getProductVersion().getProduct() != null) {
                            List<ProductChargeTemplateMapping> productChargeTemplates = orderProduct.getProductVersion().getProduct().getProductCharges();

                            log.info("charges" + productChargeTemplates.size());

                            for (ProductChargeTemplateMapping productChargeTemplateMapping : productChargeTemplates) {
                                if (productChargeTemplateMapping.getChargeTemplate() != null && productChargeTemplateMapping.getChargeTemplate() instanceof OneShotChargeTemplate) {
                                    OneShotChargeTemplate oneShotChargeTemplate = (OneShotChargeTemplate) productChargeTemplateMapping.getChargeTemplate();
                                    if (oneShotChargeTemplate.getOneShotChargeTemplateType().equals(OneShotChargeTemplateTypeEnum.OTHER)) {
                                        log.info("found one shot other" + oneShotChargeTemplate.getId());
                                        OneShotChargeTemplate invoicingPLanOneShotChargeTemplate = new OneShotChargeTemplate();
                                        invoicingPLanOneShotChargeTemplate.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.INVOICING_PLAN);
                                        invoicingPLanOneShotChargeTemplate.setStatus(oneShotChargeTemplate.getStatus());
                                        invoicingPLanOneShotChargeTemplate.setRatingScript(oneShotChargeTemplate.getRatingScript());
                                        invoicingPLanOneShotChargeTemplate.setType(oneShotChargeTemplate.getType());
                                        invoicingPLanOneShotChargeTemplate.setImmediateInvoicing(oneShotChargeTemplate.getImmediateInvoicing());
                                        invoicingPLanOneShotChargeTemplate.setEdrTemplates(oneShotChargeTemplate.getEdrTemplates());
                                        invoicingPLanOneShotChargeTemplate.setRoundingMode(oneShotChargeTemplate.getRoundingMode());
                                        invoicingPLanOneShotChargeTemplate.setRevenueRecognitionRule(oneShotChargeTemplate.getRevenueRecognitionRule());
                                        invoicingPLanOneShotChargeTemplate.setAttributes(oneShotChargeTemplate.getAttributes());
                                        invoicingPLanOneShotChargeTemplate.setDescriptionI18n(oneShotChargeTemplate.getDescriptionI18n());
                                        invoicingPLanOneShotChargeTemplate.setAmountEditable(oneShotChargeTemplate.getAmountEditable());
                                        invoicingPLanOneShotChargeTemplate.setFilterExpression(oneShotChargeTemplate.getFilterExpression());
                                        invoicingPLanOneShotChargeTemplate.setInputUnitDescription(oneShotChargeTemplate.getInputUnitDescription());
                                        invoicingPLanOneShotChargeTemplate.setInternalNote(oneShotChargeTemplate.getInternalNote());
                                        invoicingPLanOneShotChargeTemplate.setProductCharges(oneShotChargeTemplate.getProductCharges());
                                        invoicingPLanOneShotChargeTemplate.setInvoiceSubCategory(oneShotChargeTemplate.getInvoiceSubCategory());
                                        invoicingPLanOneShotChargeTemplate.setRatingUnitDescription(oneShotChargeTemplate.getRatingUnitDescription());
                                        invoicingPLanOneShotChargeTemplate.setSortIndexEl(oneShotChargeTemplate.getSortIndexEl());
                                        invoicingPLanOneShotChargeTemplate.setTaxClass(oneShotChargeTemplate.getTaxClass());
                                        invoicingPLanOneShotChargeTemplate.setTaxClassEl(oneShotChargeTemplate.getTaxClassEl());
                                        invoicingPLanOneShotChargeTemplate.setActive(oneShotChargeTemplate.isActive());
                                        invoicingPLanOneShotChargeTemplate.setDropZeroWo(oneShotChargeTemplate.isDropZeroWo());
                                        invoicingPLanOneShotChargeTemplate.setNotified(oneShotChargeTemplate.isNotified());

                                        oneShotChargeTemplateService.create(invoicingPLanOneShotChargeTemplate);


                                        ProductChargeTemplateMapping invoicingPLanChargeMapping = new ProductChargeTemplateMapping();
                                        invoicingPLanChargeMapping.setChargeTemplate(invoicingPLanOneShotChargeTemplate);
                                        invoicingPLanChargeMapping.setProduct(orderProduct.getProductVersion().getProduct());
                                        invoicingPLanChargeMapping.setCounterTemplate(productChargeTemplateMapping.getCounterTemplate());
                                        invoicingPLanChargeMapping.setWalletTemplates(productChargeTemplateMapping.getWalletTemplates());
                                        invoicingPLanChargeMapping.setAccumulatorCounterTemplates(productChargeTemplateMapping.getAccumulatorCounterTemplates());
                                        invoicingPLanChargeMapping.setVersion(productChargeTemplateMapping.getVersion());

                                        productChargeTemplateMappingService.create(invoicingPLanChargeMapping);

                                        orderProduct.getProductVersion().getProduct().getProductCharges().add(invoicingPLanChargeMapping);

                                        orderProductService.update(orderProduct);

                                    }
                                }
                            }

                        }
                    }
                }

            }
        } catch (Exception exception) {
            log.error("problem occured during excecution " + exception.getMessage());
        }


    }
}
