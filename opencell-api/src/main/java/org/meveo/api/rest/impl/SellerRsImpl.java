package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.SellerApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.SellerRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SellerRsImpl extends BaseRs implements SellerRs {

    @Inject
    private SellerApi sellerApi;

    @Override
    public ActionStatus create(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Seller seller = sellerApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(seller.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetSellerResponse find(String sellerCode, CustomFieldInheritanceEnum inheritCF) {
        GetSellerResponse result = new GetSellerResponse();

        try {
            result.setSeller(sellerApi.find(sellerCode, inheritCF != null ? inheritCF : CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String sellerCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.remove(sellerCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public SellerResponseDto list() {
        SellerResponseDto result = new SellerResponseDto();

        try {
            result.setSellers(sellerApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public SellerCodesResponseDto listSellerCodes() {
        SellerCodesResponseDto result = new SellerCodesResponseDto();

        try {
            result = sellerApi.listSellerCodes();
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            Seller seller = sellerApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(seller.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
