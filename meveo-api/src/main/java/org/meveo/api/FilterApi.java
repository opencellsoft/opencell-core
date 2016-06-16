package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.BusinessApiException;
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

    private void create(FilterDto dto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getInputXml())) {
            missingParameters.add("inputXml");
        }

        handleMissingParameters();

        try {
        	Filter filter = new Filter();
        	mapDtoToFilter(dto, filter);
            filterService.create(filter, currentUser);

        } catch (BusinessException e) {
            throw new BusinessApiException(e);
        }
    }

    private void update(FilterDto dto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getInputXml())) {
            missingParameters.add("inputXml");
        }

        handleMissingParameters();

        try {
            
        	Provider provider = currentUser.getProvider();
            Filter filter = filterService.findByCode(dto.getCode(), provider);
            
            if (filter == null) {
                throw new EntityDoesNotExistsException(Filter.class, dto.getCode());
            }

            mapDtoToFilter(dto, filter);
            filterService.update(filter, currentUser);

        } catch (BusinessException e) {
            throw new BusinessApiException(e);
        }
    }
    
    private void mapDtoToFilter(FilterDto dto, Filter filter){
    	if(filter.isTransient()){
        	filter.setCode(dto.getCode());
        	filter.clearUuid();
        }
    	filter.setDescription(dto.getDescription());
        filter.setInputXml(dto.getInputXml());
        filter.setShared(dto.getShared());
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
            create(dto, currentUser);
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