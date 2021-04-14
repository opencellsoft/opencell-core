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

package org.meveo.api.tax;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.tax.TaxMappingListResponseDto;
import org.meveo.api.dto.tax.TaxMappingDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
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

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.function.BiFunction;

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

    public TaxMappingListResponseDto list(PagingAndFiltering pagingAndFiltering) {
        TaxMappingListResponseDto result = new TaxMappingListResponseDto();
        result.setPaging( pagingAndFiltering );

        List<TaxMapping> taxMappings = entityService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (taxMappings != null) {
            for (TaxMapping taxMapping : taxMappings) {
                result.getDtos().add(new TaxMappingDto(taxMapping, null));
            }
        }

        return result;
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
    protected BiFunction<TaxMapping, CustomFieldsDto, TaxMappingDto> getEntityToDtoFunction() {
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