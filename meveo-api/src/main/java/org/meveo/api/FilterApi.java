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
public class FilterApi extends BaseApi {

    @Inject
    private FilterService filterService;

    private void create(FilterDto dto, User currentUser, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getInputXml())) {
            missingParameters.add("inputXml");
        }
        
        handleMissingParameters();
        

        Filter filter = filterService.parse(dto.getInputXml());

        try {
            filterService.initFilterFromInputXml(filter);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        filter.setCode(dto.getCode());
        filter.setDescription(dto.getDescription());
        filter.setInputXml(dto.getInputXml());
        filter.setShared(dto.getShared());

        filterService.create(filter, currentUser, provider);
    }

    public void update(FilterDto dto, User currentUser) throws MeveoApiException {

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

        filter.setDescription(dto.getDescription());
        filter.setInputXml(dto.getInputXml());
        filter.setShared(dto.getShared());
        filterService.update(filter, currentUser);
    }

    public void createOrUpdate(FilterDto dto, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();
        Filter existed = filterService.findByCode(dto.getCode(), provider);
        if (existed != null) {
            update(dto, currentUser);
        } else {
            create(dto, currentUser, provider);
        }
    }

    public FilterDto findFilter(String code, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Filter filter = filterService.findByCode(code, provider);

        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, code);
        }

        return FilterDto.toDto(filter);
    }
}