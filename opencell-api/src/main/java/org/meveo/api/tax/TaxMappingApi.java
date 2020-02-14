package org.meveo.api.tax;

import java.util.function.Function;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.tax.TaxMappingDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.DatePeriod;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.model.tax.TaxMapping;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.tax.TaxCategoryService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.service.tax.TaxMappingService;

/**
 * CRUD API for {@link TaxMapping} - Tax mapping
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class TaxMappingApi extends BaseCrudApi<TaxMapping, TaxMappingDto> {

    @Inject
    private TaxMappingService entityService;

    @Inject
    private TaxCategoryService taxCategoryService;

    @Inject
    private TaxClassService taxClassService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TaxService taxService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Creates a new TaxMapping entity
     * 
     * @param dto Posted Tax mapping data to API
     * 
     * @throws MeveoApiException Api exception
     * @throws BusinessException General business exception.
     */
    @Override
    public TaxMapping create(TaxMappingDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getAccountTaxCategoryCode())) {
            missingParameters.add("accountTaxCategoryCode");
        }

        if (StringUtils.isBlank(dto.getTaxCode()) && StringUtils.isBlank(dto.getTaxEL()) && StringUtils.isBlank(dto.getTaxELSpark())) {
            missingParameters.add("taxCode, taxCodeEL or taxCodeELSpark");
        }

        handleMissingParametersAndValidate(dto);

        TaxMapping entity = new TaxMapping();

        dtoToEntity(entity, dto);
        entityService.create(entity);

        return entity;
    }

    /**
     * Updates a Tax mapping based on id)
     * 
     * @param dto Posted Tax mapping data to API
     * 
     * @throws MeveoApiException API exception
     * @throws BusinessException business exception.
     */
    @Override
    public TaxMapping update(TaxMappingDto dto) throws MeveoApiException, BusinessException {

        Long id = dto.getId();

        if (id == null) {
            missingParameters.add("id");
        }

        handleMissingParametersAndValidate(dto);

        TaxMapping entity = entityService.findById(id);
        if (entity == null) {
            throw new EntityDoesNotExistsException(TaxMapping.class, id.toString());
        }

        dtoToEntity(entity, dto);

        entity = entityService.update(entity);
        return entity;
    }

    @Override
    protected Function<TaxMapping, TaxMappingDto> getEntityToDtoFunction() {
        return TaxMappingDto::new;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param entity Entity to populate
     * @param dto DTO entity object to populate from
     **/
    private void dtoToEntity(TaxMapping entity, TaxMappingDto dto) {

        if (dto.getAccountTaxCategoryCode() != null) {
            if (StringUtils.isBlank(dto.getAccountTaxCategoryCode())) {
                entity.setAccountTaxCategory(null);

            } else {
                TaxCategory fkEntity = taxCategoryService.findByCode(dto.getAccountTaxCategoryCode());
                if (fkEntity == null) {
                    throw new EntityDoesNotExistsException(TaxCategory.class, dto.getAccountTaxCategoryCode());
                }
                entity.setAccountTaxCategory(fkEntity);
            }
        }

        if (dto.getChargeTaxClassCode() != null) {
            if (StringUtils.isBlank(dto.getChargeTaxClassCode())) {
                entity.setChargeTaxClass(null);

            } else {
                TaxClass fkEntity = taxClassService.findByCode(dto.getChargeTaxClassCode());
                if (fkEntity == null) {
                    throw new EntityDoesNotExistsException(TaxClass.class, dto.getChargeTaxClassCode());
                }
                entity.setChargeTaxClass(fkEntity);
            }
        }

        if (dto.getValidFrom() != null || dto.getValidTo() != null) {
            entity.setValid(new DatePeriod(dto.getValidFrom(), dto.getValidTo()));
        }

        if (dto.getSellerCountryCode() != null) {
            if (StringUtils.isBlank(dto.getSellerCountryCode())) {
                entity.setSellerCountry(null);

            } else {
                TradingCountry fkEntity = tradingCountryService.findByCode(dto.getSellerCountryCode());
                if (fkEntity == null) {
                    throw new EntityDoesNotExistsException(TradingCountry.class, dto.getSellerCountryCode());
                }
                entity.setSellerCountry(fkEntity);
            }
        }

        if (dto.getBuyerCountryCode() != null) {
            if (StringUtils.isBlank(dto.getBuyerCountryCode())) {
                entity.setBuyerCountry(null);

            } else {
                TradingCountry fkEntity = tradingCountryService.findByCode(dto.getBuyerCountryCode());
                if (fkEntity == null) {
                    throw new EntityDoesNotExistsException(TradingCountry.class, dto.getBuyerCountryCode());
                }
                entity.setBuyerCountry(fkEntity);
            }
        }

        if (dto.getFilterEL() != null) {
            entity.setFilterEL(StringUtils.isEmpty(dto.getFilterEL()) ? null : dto.getFilterEL());
        }

        if (dto.getFilterELSpark() != null) {
            entity.setFilterELSpark(StringUtils.isEmpty(dto.getFilterELSpark()) ? null : dto.getFilterELSpark());
        }

        if (dto.getTaxCode() != null) {
            if (StringUtils.isBlank(dto.getTaxCode())) {
                entity.setTax(null);

            } else {
                Tax fkEntity = taxService.findByCode(dto.getTaxCode());
                if (fkEntity == null) {
                    throw new EntityDoesNotExistsException(Tax.class, dto.getTaxCode());
                }
                entity.setTax(fkEntity);
            }
        }

        if (dto.getTaxEL() != null) {
            entity.setTaxEL(StringUtils.isEmpty(dto.getTaxEL()) ? null : dto.getTaxEL());
        }

        if (dto.getTaxELSpark() != null) {
            entity.setTaxELSpark(StringUtils.isEmpty(dto.getTaxELSpark()) ? null : dto.getTaxELSpark());
        }

        if (dto.getTaxScriptCode() != null) {
            if (StringUtils.isBlank(dto.getTaxScriptCode())) {
                entity.setTaxScript(null);

            } else {
                ScriptInstance fkEntity = scriptInstanceService.findByCode(dto.getTaxScriptCode());
                if (fkEntity == null) {
                    throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getTaxScriptCode());
                }
                entity.setTaxScript(fkEntity);
            }
        }

        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }
        if (dto.getSource() != null) {
            entity.setSource(StringUtils.isEmpty(dto.getSource()) ? null : dto.getSource());
        }

        if (dto.getOriginId() != null) {
            entity.setOriginId(dto.getOriginId());
        }

    }
}