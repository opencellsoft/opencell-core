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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.cpq.ContractItemService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.QuoteProductService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 **/
@Stateless
public class PricePlanMatrixApi extends BaseCrudApi<PricePlanMatrix, PricePlanMatrixDto> {

    @Inject
    private ChargeTemplateServiceAll chargeTemplateServiceAll;

    @Inject
    private SellerService sellerService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ProductService productService;

    @Inject
    private QuoteProductService quoteProductService;

    @Inject
    private ChargeInstanceService<ChargeInstance> serviceInstanceService;
    
    @Inject
    private DiscountPlanItemService discountPlanItemService;
    
    @Inject
    private ContractItemService contractItemService;

    @Override
    public PricePlanMatrix create(PricePlanMatrixDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getEventCode())) {
            missingParameters.add("eventCode");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(PricePlanMatrix.class.getName(), postData);
        }

        handleMissingParametersAndValidate(postData);

        // search for eventCode
        if (chargeTemplateServiceAll.findByCode(postData.getEventCode()) == null && 
        		discountPlanItemService.findByCode(postData.getEventCode()) == null && 
        				contractItemService.findByCode(postData.getEventCode()) == null) {
            throw new EntityDoesNotExistsException("No event code exist");
        }

        if (pricePlanMatrixService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(PricePlanMatrix.class, postData.getCode());
        }

        PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
        pricePlanMatrix.setCode(postData.getCode());
        pricePlanMatrix.setEventCode(postData.getEventCode());
        if (postData.isDisabled() != null) {
            pricePlanMatrix.setDisabled(postData.isDisabled());
        }

        if (!StringUtils.isBlank(postData.getSeller())) {
            Seller seller = sellerService.findByCode(postData.getSeller());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
            pricePlanMatrix.setSeller(seller);
        }

        if (!StringUtils.isBlank(postData.getCountry())) {
            TradingCountry tradingCountry = tradingCountryService.findByCode(postData.getCountry());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
            }
            pricePlanMatrix.setTradingCountry(tradingCountry);
        }

        if (!StringUtils.isBlank(postData.getCurrency())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
            }
            pricePlanMatrix.setTradingCurrency(tradingCurrency);
        }

        if (postData.getOfferTemplateVersion() != null && !StringUtils.isBlank(postData.getOfferTemplateVersion().getCode())) {
            OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(postData.getOfferTemplateVersion().getCode(),
                postData.getOfferTemplateVersion().getValidFrom(), postData.getOfferTemplateVersion().getValidTo());
            if (offerTemplate == null) {
                String dateFormat = paramBeanFactory.getInstance().getDateTimeFormat();
                throw new EntityDoesNotExistsException(OfferTemplate.class,
                    postData.getOfferTemplateVersion().getCode() + " / " + DateUtils.formatDateWithPattern(postData.getOfferTemplateVersion().getValidFrom(), dateFormat) + " / "
                            + DateUtils.formatDateWithPattern(postData.getOfferTemplateVersion().getValidTo(), dateFormat));
            }
            pricePlanMatrix.setOfferTemplate(offerTemplate);

        } else if (!StringUtils.isBlank(postData.getOfferTemplate())) {
            OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate());
            if (offerTemplate == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateVersion().getCode() + " / Current date");
            }
            pricePlanMatrix.setOfferTemplate(offerTemplate);
        }

        if (!StringUtils.isBlank(postData.getValidityCalendarCode())) {
            Calendar calendar = calendarService.findByCode(postData.getValidityCalendarCode());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getValidityCalendarCode());
            }
            pricePlanMatrix.setValidityCalendar(calendar);
        }

        if (postData.getScriptInstance() != null) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstance());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstance());
            }
            pricePlanMatrix.setScriptInstance(scriptInstance);
        }

        if (postData.getPriority() == null) {
            postData.setPriority(1);
        }

        pricePlanMatrix.setMinQuantity(postData.getMinQuantity());
        pricePlanMatrix.setMaxQuantity(postData.getMaxQuantity());
        pricePlanMatrix.setStartSubscriptionDate(postData.getStartSubscriptionDate());
        pricePlanMatrix.setEndSubscriptionDate(postData.getEndSubscriptionDate());
        pricePlanMatrix.setStartRatingDate(postData.getStartRatingDate());
        pricePlanMatrix.setEndRatingDate(postData.getEndRatingDate());
        pricePlanMatrix.setMinSubscriptionAgeInMonth(postData.getMinSubscriptionAgeInMonth());
        pricePlanMatrix.setMaxSubscriptionAgeInMonth(postData.getMaxSubscriptionAgeInMonth());
        pricePlanMatrix.setAmountWithoutTax(postData.getAmountWithoutTax());
        pricePlanMatrix.setAmountWithTax(postData.getAmountWithTax());
        pricePlanMatrix.setAmountWithoutTaxEL(postData.getAmountWithoutTaxEL());
        pricePlanMatrix.setAmountWithTaxEL(postData.getAmountWithTaxEL());
        pricePlanMatrix.setPriority(postData.getPriority());
        pricePlanMatrix.setCriteria1Value(postData.getCriteria1());
        pricePlanMatrix.setCriteria2Value(postData.getCriteria2());
        pricePlanMatrix.setCriteria3Value(postData.getCriteria3());
        pricePlanMatrix.setDescription(postData.getDescription());
        pricePlanMatrix.setCriteriaEL(postData.getCriteriaEL());
        pricePlanMatrix.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        pricePlanMatrix.setWoDescriptionEL(postData.getWoDescriptionEL());
        pricePlanMatrix.setInvoiceSubCategoryEL(postData.getInvoiceSubCategoryEL());
        
        // backward compatibility 5.2
		if (!StringUtils.isBlank(postData.getRatingWithoutTaxEL())
				|| !StringUtils.isBlank(postData.getRatingWithTaxEL())) {
			postData.setTotalAmountEL(
					!StringUtils.isBlank(postData.getRatingWithoutTaxEL()) ? postData.getRatingWithoutTaxEL()
							: postData.getRatingWithTaxEL());
		}
        if (!StringUtils.isBlank(postData.getMinimumAmountWithoutTaxEl()) || !StringUtils.isBlank(postData.getMinimumAmountWithTaxEl())) {
            postData.setMinimumAmountEL(
                    !StringUtils.isBlank(postData.getMinimumAmountWithoutTaxEl()) ? postData.getMinimumAmountWithoutTaxEl() : postData.getMinimumAmountWithTaxEl());
        }

        pricePlanMatrix.setTotalAmountEL(postData.getTotalAmountEL());
        pricePlanMatrix.setMinimumAmountEL(postData.getMinimumAmountEL());
        pricePlanMatrix.setParameter1El(postData.getParameter1El());
        pricePlanMatrix.setParameter2El(postData.getParameter2El());
        pricePlanMatrix.setParameter3El(postData.getParameter3El());

        try {
            populateCustomFields(postData.getCustomFields(), pricePlanMatrix, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        pricePlanMatrixService.create(pricePlanMatrix);

        return pricePlanMatrix;
    }

    @Override
    public PricePlanMatrix update(PricePlanMatrixDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getEventCode())) {
            missingParameters.add("eventCode");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        // search for eventCode
        if (chargeTemplateServiceAll.findByCode(postData.getEventCode()) == null && 
        		discountPlanItemService.findByCode(postData.getEventCode()) == null && 
				contractItemService.findByCode(postData.getEventCode()) == null) {
            throw new EntityDoesNotExistsException("No event code exist");
        }

        // search for price plan
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(postData.getCode());
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, postData.getCode());
        }
        pricePlanMatrix.setEventCode(postData.getEventCode());

        if (!StringUtils.isBlank(postData.getSeller())) {
            Seller seller = sellerService.findByCode(postData.getSeller());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
            pricePlanMatrix.setSeller(seller);
        }

        if (!StringUtils.isBlank(postData.getCountry())) {
            TradingCountry tradingCountry = tradingCountryService.findByCode(postData.getCountry());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
            }
            pricePlanMatrix.setTradingCountry(tradingCountry);
        }

        if (!StringUtils.isBlank(postData.getCurrency())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
            }
            pricePlanMatrix.setTradingCurrency(tradingCurrency);
        }

        if (postData.getOfferTemplateVersion() != null && !StringUtils.isBlank(postData.getOfferTemplateVersion().getCode())) {
            OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(postData.getOfferTemplateVersion().getCode(),
                postData.getOfferTemplateVersion().getValidFrom(), postData.getOfferTemplateVersion().getValidTo());
            if (offerTemplate == null) {
                String dateFormat = paramBeanFactory.getInstance().getDateTimeFormat();
                throw new EntityDoesNotExistsException(OfferTemplate.class,
                    postData.getOfferTemplateVersion().getCode() + " / " + DateUtils.formatDateWithPattern(postData.getOfferTemplateVersion().getValidFrom(), dateFormat) + " / "
                            + DateUtils.formatDateWithPattern(postData.getOfferTemplateVersion().getValidTo(), dateFormat));
            }
            pricePlanMatrix.setOfferTemplate(offerTemplate);

        } else if (!StringUtils.isBlank(postData.getOfferTemplate())) {
            OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate());
            if (offerTemplate == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateVersion().getCode() + " / Current date");
            }
            pricePlanMatrix.setOfferTemplate(offerTemplate);
        }

        if (!StringUtils.isBlank(postData.getValidityCalendarCode())) {
            Calendar calendar = calendarService.findByCode(postData.getValidityCalendarCode());
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getValidityCalendarCode());
            }
            pricePlanMatrix.setValidityCalendar(calendar);
        } else {
            pricePlanMatrix.setValidityCalendar(null);
        }

        if (postData.getScriptInstance() != null) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstance());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstance());
            }
            pricePlanMatrix.setScriptInstance(scriptInstance);
        }
        pricePlanMatrix.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        if (pricePlanMatrix.getMinQuantity() != null) {
            pricePlanMatrix.setMinQuantity(pricePlanMatrix.getMinQuantity());
        }

        if (postData.getMinQuantity() != null) {
            pricePlanMatrix.setMinQuantity(postData.getMinQuantity());
        }
        if (postData.getMaxQuantity() != null) {
            pricePlanMatrix.setMaxQuantity(postData.getMaxQuantity());
        }
        if (postData.getStartSubscriptionDate() != null) {
            pricePlanMatrix.setStartSubscriptionDate(postData.getStartSubscriptionDate());
        }
        if (postData.getEndSubscriptionDate() != null) {
            pricePlanMatrix.setEndSubscriptionDate(postData.getEndSubscriptionDate());
        }
        if (postData.getStartRatingDate() != null) {
            pricePlanMatrix.setStartRatingDate(postData.getStartRatingDate());
        }
        if (postData.getEndRatingDate() != null) {
            pricePlanMatrix.setEndRatingDate(postData.getEndRatingDate());
        }
        if (postData.getMinSubscriptionAgeInMonth() != null) {
            pricePlanMatrix.setMinSubscriptionAgeInMonth(postData.getMinSubscriptionAgeInMonth());
        }
        if (postData.getMaxSubscriptionAgeInMonth() != null) {
            pricePlanMatrix.setMaxSubscriptionAgeInMonth(postData.getMaxSubscriptionAgeInMonth());
        }
        if (postData.getAmountWithoutTax() != null) {
            pricePlanMatrix.setAmountWithoutTax(postData.getAmountWithoutTax());
        }
        if (postData.getAmountWithTax() != null) {
            pricePlanMatrix.setAmountWithTax(postData.getAmountWithTax());
        }
        if (postData.getAmountWithoutTaxEL() != null) {
            pricePlanMatrix.setAmountWithoutTaxEL(postData.getAmountWithoutTaxEL());
        }
        if (postData.getAmountWithTaxEL() != null) {
            pricePlanMatrix.setAmountWithTaxEL(postData.getAmountWithTaxEL());
        }
        if (postData.getPriority() != null) {
            pricePlanMatrix.setPriority(postData.getPriority());
        }
        if (postData.getCriteria1() != null) {
            pricePlanMatrix.setCriteria1Value(postData.getCriteria1());
        }
        if (postData.getCriteria2() != null) {
            pricePlanMatrix.setCriteria2Value(postData.getCriteria2());
        }
        if (postData.getCriteria3() != null) {
            pricePlanMatrix.setCriteria3Value(postData.getCriteria3());
        }
        if (postData.getDescription() != null) {
            pricePlanMatrix.setDescription(postData.getDescription());
        }
        if (postData.getCriteriaEL() != null) {
            pricePlanMatrix.setCriteriaEL(postData.getCriteriaEL());
        }
        if (postData.getWoDescriptionEL() != null) {
            pricePlanMatrix.setWoDescriptionEL(postData.getWoDescriptionEL());
        }
        if (postData.getInvoiceSubCategoryEL() != null) {
            pricePlanMatrix.setInvoiceSubCategoryEL(postData.getInvoiceSubCategoryEL());
        }
        if (postData.getLanguageDescriptions() != null) {
            pricePlanMatrix.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), pricePlanMatrix.getDescriptionI18n()));
		}
        
		// backward compatibility 5.2
		if (!StringUtils.isBlank(postData.getRatingWithoutTaxEL())
				|| !StringUtils.isBlank(postData.getRatingWithTaxEL())) {
			postData.setTotalAmountEL(
					!StringUtils.isBlank(postData.getRatingWithoutTaxEL()) ? postData.getRatingWithoutTaxEL()
							: postData.getRatingWithTaxEL());
		}
		if (!StringUtils.isBlank(postData.getMinimumAmountWithoutTaxEl())
				|| !StringUtils.isBlank(postData.getMinimumAmountWithTaxEl())) {
			postData.setMinimumAmountEL(!StringUtils.isBlank(postData.getMinimumAmountWithoutTaxEl())
					? postData.getMinimumAmountWithoutTaxEl()
					: postData.getMinimumAmountWithTaxEl());
        }
        if (postData.getTotalAmountEL() != null) {
            pricePlanMatrix.setTotalAmountEL(postData.getTotalAmountEL());
        }
        if (postData.getMinimumAmountEL() != null) {
            pricePlanMatrix.setMinimumAmountEL(postData.getMinimumAmountEL());
        }
        if (postData.getParameter1El() != null) {
            pricePlanMatrix.setParameter1El(postData.getParameter1El());
        }
        if (postData.getParameter2El() != null) {
            pricePlanMatrix.setParameter2El(postData.getParameter2El());
        }
        if (postData.getParameter3El() != null) {
            pricePlanMatrix.setParameter3El(postData.getParameter3El());
        }

        try {
            populateCustomFields(postData.getCustomFields(), pricePlanMatrix, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        pricePlanMatrix = pricePlanMatrixService.update(pricePlanMatrix);

        return pricePlanMatrix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public PricePlanMatrixDto find(String pricePlanCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(pricePlanCode)) {
            missingParameters.add("pricePlanCode");
            handleMissingParameters();
        }

        return  pricePlanMatrixService.findPricePlanMatrix(pricePlanCode);
    }

    public List<PricePlanMatrixDto> list(String eventCode) throws MeveoApiException {
        if (StringUtils.isBlank(eventCode)) {
            missingParameters.add("eventCode");
            handleMissingParameters();
        }

        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(eventCode);
        if (pricePlanMatrixes == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, eventCode);
        }

        List<PricePlanMatrixDto> pricePlanDtos = new ArrayList<>();
        for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
            pricePlanDtos.add(new PricePlanMatrixDto(pricePlanMatrix, entityToDtoConverter.getCustomFieldsDTO(pricePlanMatrix, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
        }

        return pricePlanDtos;
    }

    public List<PricePlanMatrixLineDto> loadPrices(String ppmCode, int version, Long quoteProductId){

        if (StringUtils.isBlank(ppmCode)) {
            missingParameters.add("ppmCode");
        }
        if (StringUtils.isBlank(version)) {
            missingParameters.add("ppmVersion");
        }
        if (StringUtils.isBlank(quoteProductId)) {
            missingParameters.add("quoteProductId");
        }
        handleMissingParameters();

        PricePlanMatrixVersion ppmVersion = loadPublishedMatrixVersion(ppmCode, version);

        QuoteProduct quoteProduct = quoteProductService.findById(quoteProductId);
        if(quoteProduct == null)
            throw new EntityDoesNotExistsException(QuoteProduct.class, quoteProductId);

        try {
        	return pricePlanMatrixService.loadPrices(ppmVersion, quoteProduct);
        }catch(BusinessException e) {
        	throw new MeveoApiException(e.getMessage());
        }
    }

    private PricePlanMatrixVersion loadPublishedMatrixVersion(String ppmCode, Integer ppmVersion) {
        PricePlanMatrixVersion ppm = pricePlanMatrixVersionService.findByPricePlanAndVersion(ppmCode, ppmVersion);
        if(ppm == null)
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, "ppmCode", ppmCode, "version", ppmVersion.toString());
        if(ppm.getStatus() != VersionStatusEnum.PUBLISHED)
            throw new BusinessApiException("Price Plan Matrix: (code: " + ppmCode + ", version: " + ppmVersion + ") is not published");
        return ppm;
    }

    public PricePlanMatrixLineDto loadPrices(String ppmCode, int version, String chargeInstanceCode) {
        if (StringUtils.isBlank(ppmCode)) {
            missingParameters.add("ppmCode");
        }
        if (StringUtils.isBlank(version)) {
            missingParameters.add("ppmVersion");
        }
        if (StringUtils.isBlank(chargeInstanceCode)) {
            missingParameters.add("chargeInstanceCode");
        }
        handleMissingParameters();

        PricePlanMatrixVersion pricePlanMatrixVersion = loadPublishedMatrixVersion(ppmCode, version);

        ChargeInstance chargeInstance = loadEntityByCode(serviceInstanceService, chargeInstanceCode, ChargeInstance.class);
        try {
        	return new PricePlanMatrixLineDto(pricePlanMatrixService.loadPrices(pricePlanMatrixVersion, chargeInstance));
        }catch(BusinessException e) {
        	throw new MeveoApiException(e.getMessage());
        }
    }

    public PricePlanMatrixesResponseDto list(PagingAndFiltering pagingAndFiltering) {
        PricePlanMatrixesResponseDto result = new PricePlanMatrixesResponseDto();
        result.setPaging( pagingAndFiltering );

        List<PricePlanMatrix> pricePlanMatrices = pricePlanMatrixService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (pricePlanMatrices != null) {
            for (PricePlanMatrix pricePlanMatrix : pricePlanMatrices) {
                result.getPricePlanMatrixes().getPricePlanMatrix()
                        .add(new PricePlanMatrixDto(pricePlanMatrix, entityToDtoConverter.getCustomFieldsDTO(pricePlanMatrix, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        return result;
    }

    @Transactional
    public PricePlanMatrixDto duplicatePricePlan(String pricePlanMatrixCode, String pricePlanMatrixNewCode, int version) {
    	PricePlanMatrix ppm = loadEntityByCode(pricePlanMatrixService, pricePlanMatrixCode, PricePlanMatrix.class);
    	if(!Strings.isEmpty(pricePlanMatrixNewCode) && pricePlanMatrixService.findByCode(pricePlanMatrixNewCode) != null)
    		throw new EntityAlreadyExistsException(PricePlanMatrix.class, pricePlanMatrixNewCode);
    	PricePlanMatrixVersion ppmv = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, version);
    	if(ppmv == null) {
    		ppmv = pricePlanMatrixVersionService.getLasPricePlanMatrixtVersion(pricePlanMatrixCode);
    		if(ppmv == null)
    			throw new MeveoApiException("No version exist for price plan matrix code : " + pricePlanMatrixCode);
    	}
    	PricePlanMatrix duplicate = pricePlanMatrixService.duplicatePricePlanMatrix(ppm, ppmv, pricePlanMatrixNewCode);
    	return new PricePlanMatrixDto(pricePlanMatrixService.findById(duplicate.getId()), null);
    }
}