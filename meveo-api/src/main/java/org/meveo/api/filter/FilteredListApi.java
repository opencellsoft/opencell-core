package org.meveo.api.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.service.filter.FilterService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilteredListApi extends BaseApi {

    @Inject
    private FilterService filterService;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    private CustomFieldsCacheContainerProvider cfCache;

    public String list(String filterCode, Integer firstRow, Integer numberOfRows, User currentUser) throws MeveoApiException {
        String result = "";

        Filter filter = filterService.findByCode(filterCode, currentUser.getProvider());
        if (filter == null) {
            throw new EntityDoesNotExistsException(Filter.class, filterCode);
        }

        // check if user owned the filter
        if (filter.getShared() == null || !filter.getShared()) {
            if (filter.getAuditable().getCreator().getId() != currentUser.getId()) {
                throw new MeveoApiException("INVALID_FILTER_OWNER");
            }
        }

        try {
            result = filterService.filteredList(filter, firstRow, numberOfRows);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        return result;
    }

    public String listByXmlInput(FilteredListDto postData, User currentUser) throws MeveoApiException {
        String result = "";

        try {
            Filter filter = filterService.parse(postData.getXmlInput());

            // check if user owned the filter
            if (filter.getShared() == null || !filter.getShared()) {
                if (filter.getAuditable() != null) {
                    if (filter.getAuditable().getCreator().getId() != currentUser.getId()) {
                        throw new MeveoApiException("INVALID_FILTER_OWNER");
                    }
                }
            }

            result = filterService.filteredList(filter, postData.getFirstRow(), postData.getNumberOfRows());
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        return result;
    }

    @SuppressWarnings({ "unchecked" })
    public String search(String[] classnamesOrCetCodes, String query, Integer from, Integer size, User currentUser) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = new ArrayList<>();

        for (String classnameOrCetCode : classnamesOrCetCodes) {
            try {
                classInfo.add(new ElasticSearchClassInfo((Class<? extends BusinessEntity>) Class.forName(classnameOrCetCode), null));

                // If not a real class, then might be a Custom Entity Instance. Check if CustomEntityTemplate exists with such name
            } catch (ClassNotFoundException e) {
                CustomEntityTemplate cet = cfCache.getCustomEntityTemplate(classnameOrCetCode, currentUser.getProvider());
                if (cet != null) {
                    classInfo.add(new ElasticSearchClassInfo(CustomEntityInstance.class, classnameOrCetCode));
                } else {
                    throw new BusinessException("Class or custom entity template by name " + classnameOrCetCode + " not found");
                }
            }
        }

        return elasticClient.search(query, from, size, currentUser, classInfo);
    }

    @SuppressWarnings({ "unchecked" })
    public String search(String[] classnamesOrCetCodes, Map<String, String> queryValues, Integer from, Integer size, User currentUser) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = new ArrayList<>();

        for (String classnameOrCetCode : classnamesOrCetCodes) {
            try {
                classInfo.add(new ElasticSearchClassInfo((Class<? extends BusinessEntity>) Class.forName(classnameOrCetCode), null));

                // If not a real class, then might be a Custom Entity Instance. Check if CustomEntityTemplate exists with such name
            } catch (ClassNotFoundException e) {
                CustomEntityTemplate cet = cfCache.getCustomEntityTemplate(classnameOrCetCode, currentUser.getProvider());
                if (cet != null) {
                    classInfo.add(new ElasticSearchClassInfo(CustomEntityInstance.class, classnameOrCetCode));
                } else {
                    throw new BusinessException("Class or custom entity template by name " + classnameOrCetCode + " not found");
                }
            }
        }

        return elasticClient.search(queryValues, from, size, currentUser, classInfo);
    }
}
