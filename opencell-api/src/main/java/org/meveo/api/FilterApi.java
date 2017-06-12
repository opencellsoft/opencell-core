package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;

/**
 * @author Tyshan Shi
 * 
 **/
@Stateless
public class FilterApi extends BaseCrudApi<Filter, FilterDto> {

    @Inject
    private FilterService filterService;

    private Filter create(FilterDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInputXml())) {
            missingParameters.add("inputXml");
        }

        handleMissingParametersAndValidate(postData);

        Filter filter = new Filter();
        mapDtoToFilter(postData, filter);
        filterService.create(filter);

        return filter;
    }

    private Filter update(FilterDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInputXml())) {
            missingParameters.add("inputXml");
        }

        handleMissingParametersAndValidate(postData);

        
        Filter filter = filterService.findByCode(postData.getCode());

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, postData.getCode());
        }

        mapDtoToFilter(postData, filter);
        filter = filterService.update(filter);

        return filter;
    }

    private void mapDtoToFilter(FilterDto dto, Filter filter) {
        if (filter.isTransient()) {
            filter.setCode(dto.getCode());
            filter.clearUuid();
        }
        filter.setCode(StringUtils.isBlank(dto.getUpdatedCode()) ? dto.getCode() : dto.getUpdatedCode());
        filter.setDescription(dto.getDescription());
        filter.setInputXml(dto.getInputXml());
        filter.setShared(dto.getShared());
    }

    @Override
    public Filter createOrUpdate(FilterDto dto) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        
        Filter existed = filterService.findByCode(dto.getCode());
        if (existed != null) {
            return update(dto);
        } else {
            return create(dto);
        }
    }

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public FilterDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Filter filter = filterService.findByCode(code);

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, code);
        }

        return FilterDto.toDto(filter);
    }
}