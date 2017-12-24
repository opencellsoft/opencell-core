package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.ProviderContactApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;
import org.meveo.api.dto.response.account.ProviderContactsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.ProviderContactRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 4:40:19 AM
 *
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ProviderContactRsImpl extends BaseRs implements ProviderContactRs {

    @Inject
    private ProviderContactApi providerContactApi;

    @Override
    public ActionStatus create(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.create(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.update(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProviderContactResponseDto find(String code) {
        ProviderContactResponseDto result = new ProviderContactResponseDto();

        try {
            result.setProviderContact(providerContactApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProviderContactsResponseDto list() {
        ProviderContactsResponseDto result = new ProviderContactsResponseDto();

        try {
            result.setProviderContacts(providerContactApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.createOrUpdate(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

}
