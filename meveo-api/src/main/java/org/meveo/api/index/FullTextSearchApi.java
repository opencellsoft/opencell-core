package org.meveo.api.index;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;
import org.meveo.service.index.ReindexingStatistics;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FullTextSearchApi extends BaseApi {

    @Inject
    private ElasticClient elasticClient;

    public void cleanAndReindex(User currentUser) throws AccessDeniedException, BusinessException {

        if (!currentUser.hasPermission("superAdmin", "superAdminManagement")) {
            throw new AccessDeniedException("Super administrator permission is required to clean and reindex full text search");
        }
        try {
            Future<ReindexingStatistics> future = elasticClient.cleanAndReindex();
            future.get();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public String search(String[] classnamesOrCetCodes, String query, Integer from, Integer size, User currentUser) throws MissingParameterException, BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false, currentUser);

        return elasticClient.search(query, from, size, null, null, null, currentUser, classInfo);
    }

    public String search(String[] classnamesOrCetCodes, Map<String, String> queryValues, Integer from, Integer size, User currentUser) throws MissingParameterException,
            BusinessException {

        if (classnamesOrCetCodes == null || classnamesOrCetCodes.length == 0) {
            missingParameters.add("classnamesOrCetCodes");
        }

        handleMissingParameters();

        List<ElasticSearchClassInfo> classInfo = elasticClient.getSearchScopeInfo(classnamesOrCetCodes, false, currentUser);

        return elasticClient.search(queryValues, from, size, null, null, null, currentUser, classInfo);
    }
}
