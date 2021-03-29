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

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListUnitOfMeasureResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.service.catalog.impl.UnitOfMeasureService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mounir Bahije
 */
@Stateless
public class UnitOfMeasureApi extends BaseCrudApi<UnitOfMeasure, UnitOfMeasureDto> {

    @Inject
    private UnitOfMeasureService unitOfMeasureService;

    @Override
    public UnitOfMeasure create(UnitOfMeasureDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);

        if (unitOfMeasureService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(UnitOfMeasure.class, postData.getCode());
        }

        UnitOfMeasure unitOfMeasure = dtoToBo(postData,null);

        unitOfMeasureService.create(unitOfMeasure);

        return unitOfMeasure;

    }

    @Override
    public UnitOfMeasure update(UnitOfMeasureDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);
        
        UnitOfMeasure unitOfMeasure = unitOfMeasureService.findByCode(postData.getCode());
        if (unitOfMeasure == null) {
            throw new EntityDoesNotExistsException(UnitOfMeasure.class, postData.getCode());
        }
        unitOfMeasure=dtoToBo(postData, unitOfMeasure);
        unitOfMeasure = unitOfMeasureService.update(unitOfMeasure);
        return unitOfMeasure;
    }

    /**
     * 
     * @param code unitOfMeasure's code
     * @return found unitOfMeasure
     * @throws MeveoApiException meveo api exception.
     */
    public UnitOfMeasureDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        UnitOfMeasureDto unitOfMeasureDto = null;

        UnitOfMeasure unitOfMeasure = unitOfMeasureService.findByCode(code);

        if (unitOfMeasure == null) {
            throw new EntityDoesNotExistsException(UnitOfMeasure.class, code);
        }

        unitOfMeasureDto = new UnitOfMeasureDto(unitOfMeasure);

        return unitOfMeasureDto;

    }

    /**
     * 
     * 
     * @return list of unitOfMeasures
     * @throws MeveoApiException meveo api exception
     */
    public List<UnitOfMeasureDto> list() throws MeveoApiException {
        List<UnitOfMeasureDto> unitOfMeasureDtos = new ArrayList<UnitOfMeasureDto>();

        List<UnitOfMeasure> unitOfMeasures = unitOfMeasureService.list();
        if (unitOfMeasures != null && !unitOfMeasures.isEmpty()) {
            for (UnitOfMeasure unitOfMeasure  : unitOfMeasures) {
                UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto(unitOfMeasure);
                unitOfMeasureDtos.add(unitOfMeasureDto);
            }
        }

        return unitOfMeasureDtos;
    }

    public GetListUnitOfMeasureResponseDto list(PagingAndFiltering pagingAndFiltering) {
        GetListUnitOfMeasureResponseDto result = new GetListUnitOfMeasureResponseDto();
        result.setPaging( pagingAndFiltering );

        List<UnitOfMeasure> unitOfMeasures = unitOfMeasureService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (unitOfMeasures != null) {
            for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
                result.getListUnitOfMeasure().add(new UnitOfMeasureDto(unitOfMeasure));
            }
        }

        return result;
    }

    /**
     * 
     * @param unitOfMeasureId unitOfMeasure's id
     * 
     * @return unitOfMeasureDto for given id
     * @throws MeveoApiException meveo api exception.
     */
    public UnitOfMeasureDto findById(String unitOfMeasureId) throws MeveoApiException {
        UnitOfMeasureDto unitOfMeasureDto = null;

        if (!StringUtils.isBlank(unitOfMeasureId)) {
            try {
                long id = Integer.parseInt(unitOfMeasureId);
                UnitOfMeasure unitOfMeasure = unitOfMeasureService.findById(id);
                if (unitOfMeasure == null) {
                    throw new EntityDoesNotExistsException(UnitOfMeasure.class, id);
                }
                unitOfMeasureDto = new UnitOfMeasureDto(unitOfMeasure);

            } catch (NumberFormatException nfe) {
                throw new MeveoApiException("Passed unitOfMeasureId is invalid.");
            }

        }

        return unitOfMeasureDto;
    }
    
	private UnitOfMeasure dtoToBo(UnitOfMeasureDto postData, UnitOfMeasure unitOfMeasure)
			throws EntityDoesNotExistsException {
		if (unitOfMeasure != null) {
			unitOfMeasure.setCode(
					StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		} else {
			unitOfMeasure = new UnitOfMeasure();
			unitOfMeasure.setCode(postData.getCode());
		}
		if (postData.getParentUOMCode() != null) {
			UnitOfMeasure parentUnitOfMeasure = unitOfMeasureService.findByCode(postData.getParentUOMCode());
			if (parentUnitOfMeasure == null) {
				throw new EntityDoesNotExistsException(UnitOfMeasure.class, postData.getParentUOMCode());
			}
			unitOfMeasure.setParentUnitOfMeasure(parentUnitOfMeasure);
		}
		if (postData.getMultiplicator() != null) {
			unitOfMeasure.setMultiplicator(postData.getMultiplicator());
		}
		unitOfMeasure.setDescription(postData.getDescription());
		unitOfMeasure.setSymbol(postData.getSymbol());
		
		return unitOfMeasure;
	}

}
