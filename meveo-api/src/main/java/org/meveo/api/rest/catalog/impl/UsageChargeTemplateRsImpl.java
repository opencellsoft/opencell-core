package org.meveo.api.rest.catalog.impl;

import java.util.Set;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.UsageChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.UsageChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UsageChargeTemplateRsImpl extends BaseRs implements UsageChargeTemplateRs {

    @Inject
    private UsageChargeTemplateApi usageChargeTemplateApi;

    @Override
    public ActionStatus create(UsageChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.create(postData, getCurrentUser());
        } catch (EJBTransactionRolledbackException e) {
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) (t);
                Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
                String errMsg = "";
                for (ConstraintViolation<?> cv : violations) {
                    errMsg += cv.getPropertyPath() + " " + cv.getMessage() + ",";
                }
                errMsg = errMsg.substring(0, errMsg.length() - 1);
                result.setErrorCode(MeveoApiErrorCodeEnum.INVALID_PARAMETER);
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage(errMsg);
            } else {
                log.error("Failed to execute API", e);
                result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage(e.getMessage());
            }
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus update(UsageChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.update(postData, getCurrentUser());
        } catch (EJBTransactionRolledbackException e) {
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) (t);
                Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
                String errMsg = "";
                for (ConstraintViolation<?> cv : violations) {
                    errMsg += cv.getPropertyPath() + " " + cv.getMessage() + ",";
                }
                errMsg = errMsg.substring(0, errMsg.length() - 1);
                result.setErrorCode(MeveoApiErrorCodeEnum.INVALID_PARAMETER);
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage(errMsg);
            } else {
                log.error("Failed to execute API", e);
                result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage(e.getMessage());
            }
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public GetUsageChargeTemplateResponseDto find(String usageChargeTemplateCode) {
        GetUsageChargeTemplateResponseDto result = new GetUsageChargeTemplateResponseDto();

        try {
            result.setUsageChargeTemplate(usageChargeTemplateApi.find(usageChargeTemplateCode, getCurrentUser()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String usageChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.remove(usageChargeTemplateCode, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UsageChargeTemplateDto postData) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.createOrUpdate(postData, getCurrentUser());
        } catch (EJBTransactionRolledbackException e) {
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) (t);
                Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
                String errMsg = "";
                for (ConstraintViolation<?> cv : violations) {
                    errMsg += cv.getPropertyPath() + " " + cv.getMessage() + ",";
                }
                errMsg = errMsg.substring(0, errMsg.length() - 1);
                result.setErrorCode(MeveoApiErrorCodeEnum.INVALID_PARAMETER);
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage(errMsg);
            } else {
                log.error("Failed to execute API", e);
                result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage(e.getMessage());
            }
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

}