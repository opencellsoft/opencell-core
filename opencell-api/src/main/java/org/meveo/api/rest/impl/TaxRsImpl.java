/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.impl;

import static org.meveo.api.dto.ActionStatusEnum.SUCCESS;

import org.meveo.api.TaxApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.TaxRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.ExceptionUtils;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxRsImpl extends BaseRs implements TaxRs {

    @Inject
    private TaxApi taxApi;

    @Override
    public ActionStatus create(TaxDto postData) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            taxApi.create(postData);
        } catch (Exception exception) {
            processException(processExceptionMessage(exception), result);
        }
        return result;
    }

    @Override
    public ActionStatus update(TaxDto postData) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            taxApi.update(postData);
        } catch (Exception exception) {
            processException(processExceptionMessage(exception), result);
        }
        return result;
    }

    @Override
    public GetTaxResponse find(String taxCode) {
        GetTaxResponse result = new GetTaxResponse();

        try {
            result.setTax(taxApi.find(taxCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String taxCode) {
        ActionStatus result = new ActionStatus(SUCCESS, "");

        try {
            taxApi.remove(taxCode);
        } catch (Exception e) {
            processException(beautifyForeignConstraintViolationMessage(e, taxCode), result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(TaxDto postData) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            TaxDto tax = taxApi.createOrUpdate(postData);
            result.setEntityId(tax.getId());
            result.setEntityCode(tax.getCode());
        } catch (Exception exception) {
            processException(processExceptionMessage(exception), result);
        }
        return result;
    }

    private MeveoApiException processExceptionMessage(Exception exception) {
        if(exception instanceof EJBException) {
            return new MeveoApiException(exception.getCause().getMessage());
        }
        return new MeveoApiException(exception.getMessage());
    }

    @Override
    public GetTaxesResponse list() {
        GetTaxesResponse result = new GetTaxesResponse();

        try {
            result.setTaxesDto(taxApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public GetTaxesResponse listGetAll() {

        GetTaxesResponse result = new GetTaxesResponse();

        try {
            result = taxApi.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    private Exception beautifyForeignConstraintViolationMessage(Exception e, String taxCode) {
        if(ExceptionUtils.getRootCause(e).getMessage().contains("violates foreign key constraint"))
        {
            return new InvalidParameterException(String.format("You can only delete a tax if it has not been used. Tax %s is still referenced in other entities.", taxCode));
        }
        return e;
    }
}