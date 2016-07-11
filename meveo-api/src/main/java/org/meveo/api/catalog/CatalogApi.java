package org.meveo.api.catalog;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.BaseApi;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.Price;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

@Stateless
public class CatalogApi extends BaseApi {

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    public ProductOffering findProductOffering(String code, User currentUser, UriInfo uriInfo, Category category) throws EntityDoesNotExistsException {
        OfferTemplate offerTemplate = offerTemplateService.findByCode(code, currentUser.getProvider());
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, code);
        }
        Map<String, Price> servicePrices = getOfferPrices(offerTemplate, currentUser);
        return offerTemplate == null ? null : ProductOffering.parseFromOfferTemplate(offerTemplate, uriInfo, category, servicePrices);
    }

    public List<ProductOffering> findProductOfferings(UriInfo uriInfo, Category category, User currentUser) {
        List<OfferTemplate> offerTemplates = offerTemplateService.list(currentUser.getProvider(), true);
        Map<String, Map<String, Price>> offerPrices = new HashMap<>();

        for (OfferTemplate offerTemplate : offerTemplates) {
            Map<String, Price> servicePrices = getOfferPrices(offerTemplate, currentUser);
            offerPrices.put(offerTemplate.getCode(), servicePrices);
        }

        return ProductOffering.parseFromOfferTemplates(offerTemplates, uriInfo, category, offerPrices);
    }

    private Map<String, Price> getOfferPrices(OfferTemplate offerTemplate, User currentUser) {
        Map<String, Price> servicePrices = new HashMap<>();

        for (OfferServiceTemplate ost : offerTemplate.getOfferServiceTemplates()) {
            ServiceTemplate st = ost.getServiceTemplate();
            if (st.getServiceSubscriptionCharges() != null) {
                Price price = new Price();
                price.setDutyFreeAmount(new BigDecimal(0));
                price.setTaxIncludedAmount(new BigDecimal(0));

                for (ServiceChargeTemplateSubscription serviceChargeTemplateSubscription : st.getServiceSubscriptionCharges()) {
                    List<PricePlanMatrix> offerPricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(offerTemplate.getCode(), serviceChargeTemplateSubscription
                        .getChargeTemplate().getCode(), currentUser.getProvider());
                    if (serviceChargeTemplateSubscription.getChargeTemplate().getInvoiceSubCategory().getInvoiceSubcategoryCountries() != null
                            && serviceChargeTemplateSubscription.getChargeTemplate().getInvoiceSubCategory().getInvoiceSubcategoryCountries().get(0).getTax() != null) {
                        price.setTaxRate(serviceChargeTemplateSubscription.getChargeTemplate().getInvoiceSubCategory().getInvoiceSubcategoryCountries().get(0).getTax()
                            .getPercent());
                    }

                    if (offerPricePlans != null && offerPricePlans.size() > 0) {
                        price.setDutyFreeAmount(price.getDutyFreeAmount().add(offerPricePlans.get(0).getAmountWithoutTax()));
                        if (!currentUser.getProvider().isEntreprise()) {
                            price.setTaxIncludedAmount(price.getTaxIncludedAmount().add(offerPricePlans.get(0).getAmountWithTax()));
                        }
                    } else {
                        List<PricePlanMatrix> pricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(null, serviceChargeTemplateSubscription.getChargeTemplate()
                            .getCode(), currentUser.getProvider());
                        if (pricePlans != null && pricePlans.size() > 0) {
                            price.setDutyFreeAmount(price.getDutyFreeAmount().add(pricePlans.get(0).getAmountWithoutTax()));
                            if (!currentUser.getProvider().isEntreprise()) {
                                price.setTaxIncludedAmount(price.getTaxIncludedAmount().add(pricePlans.get(0).getAmountWithTax()));
                            }
                        }
                    }
                }

                if (currentUser.getProvider().isEntreprise()) {
                    if (price.getDutyFreeAmount() != null && price.getTaxRate() != null) {
                        BigDecimal taxRate = price.getTaxRate().divide(new BigDecimal(100)).add(new BigDecimal(1));
                        price.setTaxIncludedAmount(price.getDutyFreeAmount().multiply(taxRate));
                    } else if (price.getDutyFreeAmount() != null && price.getTaxRate() == null) {
                        price.setTaxIncludedAmount(price.getDutyFreeAmount());
                    } else {
                        price.setTaxIncludedAmount(new BigDecimal(0));
                    }
                }
                servicePrices.put(st.getCode() + "_SUB", price);
            }

            if (st.getServiceRecurringCharges() != null) {
                Price price = new Price();
                price.setDutyFreeAmount(new BigDecimal(0));
                price.setTaxIncludedAmount(new BigDecimal(0));

                for (ServiceChargeTemplateRecurring serviceChargeTemplateRecurring : st.getServiceRecurringCharges()) {
                    List<PricePlanMatrix> offerPricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(offerTemplate.getCode(), serviceChargeTemplateRecurring
                        .getChargeTemplate().getCode(), currentUser.getProvider());
                    if (serviceChargeTemplateRecurring.getChargeTemplate().getInvoiceSubCategory().getInvoiceSubcategoryCountries() != null
                            && serviceChargeTemplateRecurring.getChargeTemplate().getInvoiceSubCategory().getInvoiceSubcategoryCountries().get(0).getTax() != null) {
                        price.setTaxRate(serviceChargeTemplateRecurring.getChargeTemplate().getInvoiceSubCategory().getInvoiceSubcategoryCountries().get(0).getTax().getPercent());
                    }

                    if (offerPricePlans != null && offerPricePlans.size() > 0) {
                        price.setDutyFreeAmount(price.getDutyFreeAmount().add(offerPricePlans.get(0).getAmountWithoutTax()));
                        if (!currentUser.getProvider().isEntreprise()) {
                            price.setTaxIncludedAmount(price.getTaxIncludedAmount().add(offerPricePlans.get(0).getAmountWithTax()));
                        }
                    } else {
                        List<PricePlanMatrix> pricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(null, serviceChargeTemplateRecurring.getChargeTemplate()
                            .getCode(), currentUser.getProvider());
                        if (pricePlans != null && pricePlans.size() > 0) {
                            price.setDutyFreeAmount(price.getDutyFreeAmount().add(pricePlans.get(0).getAmountWithoutTax()));
                            if (!currentUser.getProvider().isEntreprise()) {
                                if (price.getTaxIncludedAmount() != null) {
                                    price.setTaxIncludedAmount(price.getTaxIncludedAmount().add(
                                        pricePlans.get(0).getAmountWithTax() != null ? pricePlans.get(0).getAmountWithTax() : BigDecimal.ZERO));
                                }
                            }
                        }
                    }
                }

                if (currentUser.getProvider().isEntreprise()) {
                    if (price.getDutyFreeAmount() != null && price.getTaxRate() != null) {
                        BigDecimal taxRate = price.getTaxRate().divide(new BigDecimal(100)).add(new BigDecimal(1));
                        price.setTaxIncludedAmount(price.getDutyFreeAmount().multiply(taxRate));
                    } else if (price.getDutyFreeAmount() != null && price.getTaxRate() == null) {
                        price.setTaxIncludedAmount(price.getDutyFreeAmount());
                    } else {
                        price.setTaxIncludedAmount(new BigDecimal(0));
                    }
                }
                servicePrices.put(st.getCode() + "_REC" + (st.getInvoicingCalendar() == null ? "" : "_" + st.getInvoicingCalendar().getCode()), price);
            }
        }

        return servicePrices;
    }

    public ProductSpecification findProductSpecification(String code, User currentUser, UriInfo uriInfo) throws EntityDoesNotExistsException {
        OfferTemplate offerTemplate = offerTemplateService.findByCode(code, currentUser.getProvider());
        if (offerTemplate != null) {
            return ProductSpecification.parseFromOfferTemplate(offerTemplate, uriInfo);
        } else {
            throw new EntityDoesNotExistsException(OfferTemplate.class, code);
        }
    }

    public List<ProductSpecification> findProductSpecifications(User currentUser, UriInfo uriInfo) {
        List<OfferTemplate> offerTemplates = offerTemplateService.list(currentUser.getProvider(), true);
        return ProductSpecification.parseFromOfferTemplates(offerTemplates, uriInfo);
    }

}
