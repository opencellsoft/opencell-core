package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
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

    private Filter create(FilterDto dto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getInputXml())) {
            missingParameters.add("inputXml");
        }

        handleMissingParameters();

        Filter filter = new Filter();
        mapDtoToFilter(dto, filter);
        filterService.create(filter, currentUser);

        return filter;
    }

    private Filter update(FilterDto dto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getInputXml())) {
            missingParameters.add("inputXml");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();
        Filter filter = filterService.findByCode(dto.getCode(), provider);

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, dto.getCode());
        }

        mapDtoToFilter(dto, filter);
        filter = filterService.update(filter, currentUser);

        return filter;
    }

    private void mapDtoToFilter(FilterDto dto, Filter filter) {
        if (filter.isTransient()) {
            filter.setCode(dto.getCode());
            filter.clearUuid();
        }
        filter.setDescription(dto.getDescription());
        filter.setInputXml(dto.getInputXml());
        filter.setShared(dto.getShared());
    }

    @Override
    public Filter createOrUpdate(FilterDto dto, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();
        Filter existed = filterService.findByCode(dto.getCode(), provider);
        if (existed != null) {
            return update(dto, currentUser);
        } else {
            return create(dto, currentUser);
        }
    }

    @Override
    public FilterDto find(String code, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Filter filter = filterService.findByCode(code, currentUser.getProvider());

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, code);
        }

        return FilterDto.toDto(filter);
    }
}