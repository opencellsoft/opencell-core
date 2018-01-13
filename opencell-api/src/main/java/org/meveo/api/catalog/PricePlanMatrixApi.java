package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
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
    private CalendarService calendarService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public PricePlanMatrix create(PricePlanMatrixDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getEventCode())) {
            missingParameters.add("eventCode");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getAmountWithoutTax() == null && appProvider.isEntreprise()) {
            missingParameters.add("amountWithoutTax");
        }
        if (postData.getAmountWithTax() == null && !appProvider.isEntreprise()) {
            missingParameters.add("amountWithTax");
        }

        handleMissingParametersAndValidate(postData);

        // search for eventCode
        if (chargeTemplateServiceAll.findByCode(postData.getEventCode()) == null) {
            throw new EntityDoesNotExistsException(ChargeTemplate.class, postData.getEventCode());
        }

        if (pricePlanMatrixService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(PricePlanMatrix.class, postData.getCode());
        }

        PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
        pricePlanMatrix.setCode(postData.getCode());
        pricePlanMatrix.setEventCode(postData.getEventCode());

        if (!StringUtils.isBlank(postData.getSeller())) {
            Seller seller = sellerService.findByCode(postData.getSeller());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
            pricePlanMatrix.setSeller(seller);
        }

        if (!StringUtils.isBlank(postData.getCountry())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());
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
                String dateFormat = ParamBean.getInstance().getDateTimeFormat();
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

        try {
            populateCustomFields(postData.getCustomFields(), pricePlanMatrix, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        pricePlanMatrixService.create(pricePlanMatrix);

        return pricePlanMatrix;
    }

    public PricePlanMatrix update(PricePlanMatrixDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getEventCode())) {
            missingParameters.add("eventCode");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getAmountWithoutTax() == null && appProvider.isEntreprise()) {
            missingParameters.add("amountWithoutTax");
        }
        if (postData.getAmountWithTax() == null && !appProvider.isEntreprise()) {
            missingParameters.add("amountWithTax");
        }

        handleMissingParametersAndValidate(postData);

        // search for eventCode
        if (chargeTemplateServiceAll.findByCode(postData.getEventCode()) == null) {
            throw new EntityDoesNotExistsException(ChargeTemplate.class, postData.getEventCode());
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
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());
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
                String dateFormat = ParamBean.getInstance().getDateTimeFormat();
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
        pricePlanMatrix.setWoDescriptionEL(postData.getWoDescriptionEL());

        if (postData.getLanguageDescriptions() != null) {
            pricePlanMatrix.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), pricePlanMatrix.getDescriptionI18n()));
        }

        try {
            populateCustomFields(postData.getCustomFields(), pricePlanMatrix, false);
        } catch (MissingParameterException e) {
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

        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanCode);
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanCode);
        }

        return new PricePlanMatrixDto(pricePlanMatrix, entityToDtoConverter.getCustomFieldsDTO(pricePlanMatrix, true));
    }

    public void remove(String pricePlanCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(pricePlanCode)) {
            missingParameters.add("pricePlanCode");
            handleMissingParameters();
        }

        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanCode);
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanCode);
        }

        pricePlanMatrixService.remove(pricePlanMatrix);
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
            pricePlanDtos.add(new PricePlanMatrixDto(pricePlanMatrix, entityToDtoConverter.getCustomFieldsDTO(pricePlanMatrix, true)));
        }

        return pricePlanDtos;
    }

    public PricePlanMatrix createOrUpdate(PricePlanMatrixDto postData) throws MeveoApiException, BusinessException {
        if (pricePlanMatrixService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }
}