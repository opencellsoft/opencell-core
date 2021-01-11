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

package org.meveo.api.rest.catalog.impl;

import org.meveo.api.catalog.PricePlanMatrixApi;
import org.meveo.api.catalog.PricePlanMatrixColumnApi;
import org.meveo.api.catalog.PricePlanMatrixLineApi;
import org.meveo.api.catalog.PricePlanMatrixVersionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.LoadPricesRequest;
import org.meveo.api.dto.catalog.PricePlanMatrixColumnDto;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.catalog.GetPricePlanMatrixColumnResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanMatrixLineResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.PricePlanRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class PricePlanRsImpl extends BaseRs implements PricePlanRs {

    @Inject
    private PricePlanMatrixApi pricePlanApi;
    @Inject
    private PricePlanMatrixVersionApi pricePlanMatrixVersionApi;
    @Inject
    private PricePlanMatrixColumnApi pricePlanMatrixColumnApi;
    @Inject
    private PricePlanMatrixLineApi pricePlanMatrixLineApi;

    @Override
    public ActionStatus create(PricePlanMatrixDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(PricePlanMatrixDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetPricePlanResponseDto find(String pricePlanCode) {
        GetPricePlanResponseDto result = new GetPricePlanResponseDto();

        try {
            result.setPricePlan(pricePlanApi.find(pricePlanCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String pricePlanCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.remove(pricePlanCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public PricePlanMatrixesResponseDto listPricePlanByEventCode(String eventCode) {
        PricePlanMatrixesResponseDto result = new PricePlanMatrixesResponseDto();

        try {
            result.getPricePlanMatrixes().setPricePlanMatrix(pricePlanApi.list(eventCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(PricePlanMatrixDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            pricePlanApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            pricePlanApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public Response createOrUpdateMatrixPricePlanVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionApi.createOrUpdate(pricePlanMatrixVersionDto);
            return Response.ok(new GetPricePlanVersionResponseDto(pricePlanMatrixVersion)).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, new GetPricePlanVersionResponseDto().getActionStatus());
        }
    }

    @Override
    public Response removeMatrixPricePlanVersion(String pricePlanMatrixCode, int pricePlanMatrixVersion) {
        ActionStatus result = new ActionStatus();
        try {
            pricePlanMatrixVersionApi.removePricePlanMatrixVersion(pricePlanMatrixCode, pricePlanMatrixVersion);
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result);
        }
    }

    @Override
    public Response updatePricePlanMatrixVersionStatus(String pricePlanMatrixCode, int pricePlanMatrixVersion, VersionStatusEnum status) {
        GetPricePlanVersionResponseDto result = new GetPricePlanVersionResponseDto();
        try {
            result=pricePlanMatrixVersionApi.updateProductVersionStatus(pricePlanMatrixCode, pricePlanMatrixVersion, status);
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result.getActionStatus());
        }
    }

    @Override
    public Response duplicatePricePlanVersion(String pricePlanMatrixCode, int pricePlanMatrixVersion) {
        GetPricePlanVersionResponseDto result = new GetPricePlanVersionResponseDto();
        try {
            result =pricePlanMatrixVersionApi.duplicateProductVersion(pricePlanMatrixCode, pricePlanMatrixVersion);
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result.getActionStatus());
        }
    }

    @Override
    public Response create(PricePlanMatrixColumnDto postData) {
        GetPricePlanMatrixColumnResponseDto response = new GetPricePlanMatrixColumnResponseDto();
        try {
            PricePlanMatrixColumn pricePlanMatrixColumn = pricePlanMatrixColumnApi.create(postData);
            response.setPricePlanMatrixColumnDto(new PricePlanMatrixColumnDto(pricePlanMatrixColumn));
            return Response.created(LinkGenerator.getUriBuilderFromResource(PricePlanRs.class, pricePlanMatrixColumn.getId()).build())
                    .entity(response)
                    .build();
        } catch(MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }

    @Override
    public Response update(PricePlanMatrixColumnDto postData) {
        GetPricePlanMatrixColumnResponseDto response = new GetPricePlanMatrixColumnResponseDto();
        try {
            PricePlanMatrixColumn pricePlanMatrixColumn = pricePlanMatrixColumnApi.update(postData);
            response.setPricePlanMatrixColumnDto(new PricePlanMatrixColumnDto(pricePlanMatrixColumn));
            return Response.ok(response).build();
        } catch(MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }

    @Override
    public Response findPricePlanMatrixColumn(String pricePlanMatrixColumnCode) {
        GetPricePlanMatrixColumnResponseDto response = new GetPricePlanMatrixColumnResponseDto();
        try {
            PricePlanMatrixColumnDto pricePlanMatrixColumnDto = pricePlanMatrixColumnApi.find(pricePlanMatrixColumnCode);
            response.setPricePlanMatrixColumnDto(pricePlanMatrixColumnDto);
            return Response.ok(response).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }

    @Override
    public Response removePricePlanMatrixColumnCode(String pricePlanMatrixColumnCode) {
        ActionStatus result = new ActionStatus();
        try {
            pricePlanMatrixColumnApi.remove(pricePlanMatrixColumnCode);
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result);
        }
    }

    @Override
    public Response addPricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        GetPricePlanMatrixLineResponseDto response = new GetPricePlanMatrixLineResponseDto();
        try {
            pricePlanMatrixLineDto = pricePlanMatrixLineApi.addPricePlanMatrixLine(pricePlanMatrixLineDto);

            response.setPricePlanMatrixLineDto(pricePlanMatrixLineDto);
            return Response.created(LinkGenerator.getUriBuilderFromResource(PricePlanRs.class, pricePlanMatrixLineDto.getPpmLineId()).build())
                    .entity(response)
                    .build();
        }catch(MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }

    @Override
    public Response updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        GetPricePlanMatrixLineResponseDto response = new GetPricePlanMatrixLineResponseDto();
        try {

            pricePlanMatrixLineDto = pricePlanMatrixLineApi.updatePricePlanMatrixLine(pricePlanMatrixLineDto);
            response.setPricePlanMatrixLineDto(pricePlanMatrixLineDto);
            return Response.ok(response).build();
        }catch(MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }

    @Override
    public ActionStatus removePricePlanMatrixLine(Long ppmLineId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanMatrixLineApi.remove(ppmLineId);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;

    }

    @Override
    public Response getPricePlanMatrixLine(Long ppmLineId) {
        GetPricePlanMatrixLineResponseDto response = new GetPricePlanMatrixLineResponseDto();
        try {

            PricePlanMatrixLineDto pricePlanMatrixLineDto = pricePlanMatrixLineApi.load(ppmLineId);
            response.setPricePlanMatrixLineDto(pricePlanMatrixLineDto);
            return Response.ok(response).build();
        }catch(MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }

    @Override
    public Response loadPrices(LoadPricesRequest loadPricesRequest) {
        GetPricePlanMatrixLineResponseDto response = new GetPricePlanMatrixLineResponseDto();
        try {

            List<PricePlanMatrixLineDto> pricePlanMatrixLineDto = pricePlanApi.loadPrices(loadPricesRequest);
            response.setPricePlanMatrixLinesDto(pricePlanMatrixLineDto);
            return Response.ok(response).build();
        }catch(MeveoApiException e) {
            return errorResponse(e, response.getActionStatus());
        }
    }


}