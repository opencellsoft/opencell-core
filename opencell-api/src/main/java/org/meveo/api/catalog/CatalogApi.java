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

package org.meveo.api.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.billing.impl.RecurringRatingService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.Price;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductOfferingPrice;
import org.tmf.dsmapi.catalog.resource.product.ProductOfferingPriceType;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

@Stateless
public class CatalogApi extends BaseApi {

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private RecurringRatingService recurringRatingService;

    public ProductOffering findProductOffering(String code, Date validFrom, Date validTo, UriInfo uriInfo, Category category) throws EntityDoesNotExistsException, BusinessException {
        OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, code);
        }
        List<ProductOfferingPrice> offerPrices = getOfferPrices(offerTemplate);
        return new ProductOffering(offerTemplate, uriInfo, category, offerPrices);
    }

    public List<ProductOffering> findProductOfferings(Date validFrom, Date validTo, UriInfo uriInfo, Category category) throws BusinessException {
        List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
        List<OfferTemplate> offerTemplates = offerTemplateService.list(null, validFrom, validTo, LifeCycleStatusEnum.ACTIVE);

        for (OfferTemplate offerTemplate : offerTemplates) {
            List<ProductOfferingPrice> offerPrices = getOfferPrices(offerTemplate);
            ProductOffering productOffering = new ProductOffering(offerTemplate, uriInfo, category, offerPrices);
            productOfferings.add(productOffering);
        }
        return productOfferings;
    }

    private List<ProductOfferingPrice> getOfferPrices(OfferTemplate offerTemplate) throws BusinessException {
        List<ProductOfferingPrice> offerPrices = new ArrayList<>();
        for (OfferServiceTemplate offerServiceTemplate : offerTemplate.getOfferServiceTemplates()) {
            if (!offerServiceTemplate.isMandatory()) {
                continue;
            }
            ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
            offerPrices.addAll(getProductOfferingPricesFromSubscriptionCharges(offerTemplate, serviceTemplate));
            offerPrices.addAll(getProductOfferingPricesFromRecurringCharges(offerTemplate, serviceTemplate));
        }
        for (OfferProductTemplate offerProductTemplate : offerTemplate.getOfferProductTemplates()) {
            ProductTemplate productTemplate = offerProductTemplate.getProductTemplate();
            offerPrices.addAll(getProductOfferingPricesFromOfferProducts(offerTemplate, productTemplate));
        }
        return offerPrices;
    }

    private List<ProductOfferingPrice> getProductOfferingPricesFromSubscriptionCharges(OfferTemplate offerTemplate, ServiceTemplate serviceTemplate) throws BusinessException {
        List<ProductOfferingPrice> offerPrices = new ArrayList<>();
        if (serviceTemplate.getServiceSubscriptionCharges() != null) {
            Price price = new Price();
            price.setDutyFreeAmount(new BigDecimal(0));
            price.setTaxIncludedAmount(new BigDecimal(0));

            String chargeCode = null;
            for (ServiceChargeTemplateSubscription serviceChargeTemplateSubscription : serviceTemplate.getServiceSubscriptionCharges()) {

                // TODO AKK Impossible or not accurate to determine a tax rate as it depends on an account
                price.setTaxRate(BigDecimal.ZERO);

                chargeCode = serviceChargeTemplateSubscription.getChargeTemplate().getCode();

                List<PricePlanMatrix> pricePlans = pricePlanMatrixService.getActivePricePlansByOfferAndChargeCode(offerTemplate.getCode(), chargeCode);
                if (pricePlans == null || pricePlans.isEmpty()) {
                    pricePlans = pricePlanMatrixService.getActivePricePlansByOfferAndChargeCode(null, chargeCode);
                }
                if (pricePlans != null && !pricePlans.isEmpty()) {

                    // Nothing to do with WO here, other then reusing an existing method to calculate price amounts based on amountWithoutTax or amountWithTax depending on
                    // provider.isEnterprise() value
                    WalletOperation wo = new WalletOperation();
                    wo.setPriceplan(pricePlans.get(0));
                    wo.setQuantity(BigDecimal.ONE);
                    wo.setTaxPercent(price.getTaxRate());
                    recurringRatingService.calculateAmounts(wo, pricePlans.get(0).getAmountWithoutTax(), pricePlans.get(0).getAmountWithTax());
                    recurringRatingService.computeTransactionalAmounts(wo);
                    price.setDutyFreeAmount(price.getDutyFreeAmount().add(wo.getUnitAmountWithoutTax()));
                    price.setTaxIncludedAmount(price.getTaxIncludedAmount().add(wo.getUnitAmountWithTax()));
                }
            }

            ProductOfferingPrice offerPrice = new ProductOfferingPrice();
            offerPrice.setPriceName(serviceTemplate.getCode());
            offerPrice.setPriceType(ProductOfferingPriceType.ONE_TIME);
            offerPrice.setPrice(price);
            offerPrice.setPriceDescription(serviceTemplate.getDescriptionOrCode());
            offerPrices.add(offerPrice);
        }

        return offerPrices;
    }

    private List<ProductOfferingPrice> getProductOfferingPricesFromRecurringCharges(OfferTemplate offerTemplate, ServiceTemplate serviceTemplate) throws BusinessException {
        List<ProductOfferingPrice> offerPrices = new ArrayList<>();
        if (serviceTemplate.getServiceRecurringCharges() != null) {
            ProductOfferingPrice offerPrice = null;
            Price price = null;
            String chargeCode = null;
            for (ServiceChargeTemplateRecurring serviceChargeTemplateRecurring : serviceTemplate.getServiceRecurringCharges()) {

                price = new Price();
                price.setDutyFreeAmount(new BigDecimal(0));
                price.setTaxIncludedAmount(new BigDecimal(0));

                chargeCode = serviceChargeTemplateRecurring.getChargeTemplate().getCode();

                // TODO AKK Impossible or not accurate to determine a tax rate as it depends on an account
                price.setTaxRate(BigDecimal.ZERO);

                List<PricePlanMatrix> pricePlans = pricePlanMatrixService.getActivePricePlansByOfferAndChargeCode(offerTemplate.getCode(), chargeCode);
                if (pricePlans == null || pricePlans.isEmpty()) {
                    pricePlans = pricePlanMatrixService.getActivePricePlansByOfferAndChargeCode(null, chargeCode);
                }
                if (pricePlans != null && !pricePlans.isEmpty()) {

                    // Nothing to do with WO here, other then reusing an existing method to calculate price amounts based on amountWithoutTax or amountWithTax depending on
                    // provider.isEnterprise() value
                    WalletOperation wo = new WalletOperation();
                    wo.setQuantity(BigDecimal.ONE);
                    wo.setPriceplan(pricePlans.get(0));
                    wo.setTaxPercent(price.getTaxRate());
                    recurringRatingService.calculateAmounts(wo, pricePlans.get(0).getAmountWithoutTax(), pricePlans.get(0).getAmountWithTax());
                    recurringRatingService.computeTransactionalAmounts(wo);
                    price.setDutyFreeAmount(wo.getUnitAmountWithoutTax());
                    price.setTaxIncludedAmount(wo.getUnitAmountWithTax());
                }

                // TODO Might not be accurate as neither service instance nor charge instance is present to resolve EL
                String calendarCode = serviceChargeTemplateRecurring.getChargeTemplate().getCalendar().getCode();
                if (!StringUtils.isBlank(serviceChargeTemplateRecurring.getChargeTemplate().getCalendarCodeEl())) {
                    calendarCode = recurringRatingService
                        .getCalendarFromEl(serviceChargeTemplateRecurring.getChargeTemplate().getCalendarCodeEl(), null, serviceTemplate, serviceChargeTemplateRecurring.getChargeTemplate(), null).getCode();
                }
                String priceName = StringUtils.isBlank(calendarCode) ? serviceTemplate.getCode() : serviceTemplate.getCode() + " " + calendarCode;

                offerPrice = new ProductOfferingPrice();
                offerPrice.setPriceName(priceName);
                offerPrice.setPriceType(ProductOfferingPriceType.RECURRING);
                offerPrice.setPrice(price);
                offerPrice.setPriceDescription(serviceTemplate.getDescriptionOrCode());
                offerPrices.add(offerPrice);
            }
        }
        return offerPrices;
    }

    private List<ProductOfferingPrice> getProductOfferingPricesFromOfferProducts(OfferTemplate offerTemplate, ProductTemplate productTemplate) throws BusinessException {
        List<ProductOfferingPrice> offerPrices = new ArrayList<>();
        if (productTemplate.getProductChargeTemplates() != null) {
            ProductOfferingPrice offerPrice = null;
            Price price = null;
            String chargeCode = null;
            for (ProductChargeTemplate productChargeTemplate : productTemplate.getProductChargeTemplates()) {

                price = new Price();
                price.setDutyFreeAmount(new BigDecimal(0));
                price.setTaxIncludedAmount(new BigDecimal(0));

                chargeCode = productChargeTemplate.getCode();

                // TODO AKK Impossible or not accurate to determine a tax rate as it depends on an account
                price.setTaxRate(BigDecimal.ZERO);

                List<PricePlanMatrix> pricePlans = pricePlanMatrixService.getActivePricePlansByOfferAndChargeCode(offerTemplate.getCode(), chargeCode);
                if (pricePlans == null || pricePlans.isEmpty()) {
                    pricePlans = pricePlanMatrixService.getActivePricePlansByOfferAndChargeCode(null, chargeCode);
                }
                if (pricePlans != null && !pricePlans.isEmpty()) {

                    // Nothing to do with WO here, other then reusing an existing method to calculate price amounts based on amountWithoutTax or amountWithTax depending on
                    // provider.isEnterprise() value
                    WalletOperation wo = new WalletOperation();
                    wo.setQuantity(BigDecimal.ONE);
                    wo.setPriceplan(pricePlans.get(0));
                    wo.setTaxPercent(price.getTaxRate());
                    recurringRatingService.calculateAmounts(wo, pricePlans.get(0).getAmountWithoutTax(), pricePlans.get(0).getAmountWithTax());
                    recurringRatingService.computeTransactionalAmounts(wo);
                    price.setDutyFreeAmount(wo.getUnitAmountWithoutTax());
                    price.setTaxIncludedAmount(wo.getUnitAmountWithTax());
                }

                offerPrice = new ProductOfferingPrice();
                offerPrice.setPriceName(productTemplate.getCode());
                offerPrice.setPriceType(ProductOfferingPriceType.ONE_TIME);
                offerPrice.setPrice(price);
                offerPrice.setPriceDescription(productTemplate.getDescriptionOrCode());
                offerPrices.add(offerPrice);
            }
        }
        return offerPrices;
    }

    public ProductSpecification findProductSpecification(String code, Date validFrom, Date validTo, UriInfo uriInfo) throws EntityDoesNotExistsException {
        OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (offerTemplate != null) {
            return ProductSpecification.parseFromOfferTemplate(offerTemplate, uriInfo);
        } else {
            throw new EntityDoesNotExistsException(OfferTemplate.class, code);
        }
    }

    public List<ProductSpecification> findProductSpecifications(UriInfo uriInfo) {
        List<OfferTemplate> offerTemplates = offerTemplateService.list(true);
        return ProductSpecification.parseFromOfferTemplates(offerTemplates, uriInfo);
    }
}