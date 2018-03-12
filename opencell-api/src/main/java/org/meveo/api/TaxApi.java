package org.meveo.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TaxesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Tax;
import org.meveo.service.catalog.impl.TaxService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class TaxApi extends BaseApi {

    @Inject
    private TaxService taxService;

    public ActionStatus create(TaxDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getPercent())) {
            missingParameters.add("percent");
        }

        handleMissingParametersAndValidate(postData);

        ActionStatus result = new ActionStatus();

        // check if tax exists
        if (taxService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Tax.class, postData.getCode());
        }

        Tax tax = new Tax();
        tax.setCode(postData.getCode());
        tax.setDescription(postData.getDescription());
        tax.setPercent(postData.getPercent());
        tax.setAccountingCode(postData.getAccountingCode());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), tax, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        tax.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        taxService.create(tax);

        return result;
    }

    public ActionStatus update(TaxDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getPercent())) {
            missingParameters.add("percent");
        }

        handleMissingParametersAndValidate(postData);

        ActionStatus result = new ActionStatus();

        // check if tax exists
        Tax tax = taxService.findByCode(postData.getCode());
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, postData.getCode());
        }
        tax.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        tax.setDescription(postData.getDescription());
        tax.setPercent(postData.getPercent());
        tax.setAccountingCode(postData.getAccountingCode());

        if (postData.getLanguageDescriptions() != null) {
            tax.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), tax.getDescriptionI18n()));
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), tax, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        tax = taxService.update(tax);

        return result;
    }

    public TaxDto find(String taxCode) throws MeveoApiException {

        if (StringUtils.isBlank(taxCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TaxDto result = new TaxDto();

        Tax tax = taxService.findByCode(taxCode);
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, taxCode);
        }

        result = new TaxDto(tax, entityToDtoConverter.getCustomFieldsDTO(tax, true));

        return result;
    }

    public ActionStatus remove(String taxCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(taxCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        ActionStatus result = new ActionStatus();

        Tax tax = taxService.findByCode(taxCode);
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, taxCode);
        }

        taxService.remove(tax);
        return result;
    }

    public void createOrUpdate(TaxDto postData) throws MeveoApiException, BusinessException {
        Tax tax = taxService.findByCode(postData.getCode());

        if (tax == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    public TaxesDto list() throws MeveoApiException {
        TaxesDto taxesDto = new TaxesDto();

        List<Tax> taxes = taxService.list();
        if (taxes != null && !taxes.isEmpty()) {
            for (Tax tax : taxes) {
                TaxDto taxDto = new TaxDto(tax, entityToDtoConverter.getCustomFieldsDTO(tax, true));
                taxesDto.getTax().add(taxDto);
            }
        }

        return taxesDto;
    }
}
