package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.LanguageIsoApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.LanguageIsoDto;
import org.meveo.api.dto.response.GetLanguageIsoResponse;
import org.meveo.api.dto.response.GetLanguagesIsoResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.LanguageIsoRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class LanguageIsoRsImpl extends BaseRs implements LanguageIsoRs {

    @Inject
    private LanguageIsoApi languageIsoApi;

    @Override
    public ActionStatus create(LanguageIsoDto languageIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.create(languageIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetLanguageIsoResponse find(String languageCode) {
        GetLanguageIsoResponse result = new GetLanguageIsoResponse();

        try {
            result.setLanguage(languageIsoApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(LanguageIsoDto languageIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.update(languageIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(LanguageIsoDto languageIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.createOrUpdate(languageIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

	@Override
	public GetLanguagesIsoResponse list() {
		GetLanguagesIsoResponse result = new GetLanguagesIsoResponse();

        try {
            result.setLanguages(languageIsoApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
	}
}
