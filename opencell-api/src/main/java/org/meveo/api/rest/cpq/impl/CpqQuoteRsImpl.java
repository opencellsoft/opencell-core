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

package org.meveo.api.rest.cpq.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.billing.CpqQuoteApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.CpqQuotesListResponseDto;
import org.meveo.api.dto.response.cpq.GetQuoteDtoResponse;
import org.meveo.api.dto.response.cpq.GetQuoteOfferDtoResponse;
import org.meveo.api.dto.response.cpq.GetQuoteVersionDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.cpq.CpqQuoteRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CpqQuoteRsImpl extends BaseRs implements CpqQuoteRs {

    @Inject
    private CpqQuoteApi cpqQuoteApi;



	@Override
	public Response createQuote(boolean executeQuotation, QuoteDTO quote, UriInfo info) {
		 GetQuoteDtoResponse getQuoteDtoResponse = new GetQuoteDtoResponse();
		 try {
			 getQuoteDtoResponse.setQuoteDto(cpqQuoteApi.createQuote(quote));
	            return Response.ok(getQuoteDtoResponse).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, getQuoteDtoResponse.getActionStatus());
	        }
	}


	@Override
	public Response getQuote(String code, UriInfo info) {
		 GetQuoteDtoResponse getQuoteDtoResponse = new GetQuoteDtoResponse();
		 try {
			 getQuoteDtoResponse=cpqQuoteApi.getQuote(code);
	            return Response.ok(getQuoteDtoResponse).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, getQuoteDtoResponse.getActionStatus());
	        }
	}


	@Override
	public Response findQuotes(PagingAndFiltering pagingAndFiltering, UriInfo info) {
		CpqQuotesListResponseDto result = new CpqQuotesListResponseDto();
		try {
			result = cpqQuoteApi.findQuotes(pagingAndFiltering);
			result.getQuotes().setListSize(result.getQuotes().getQuoteDtos().size());
			result.setActionStatus(new ActionStatus());
            return Response.ok(result).build();
		}catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}


	@Override
	public Response updateQuote(boolean executeQuotation, QuoteDTO quote, UriInfo info) {
		 GetQuoteDtoResponse getQuoteDtoResponse = new GetQuoteDtoResponse();
		 try {
			 getQuoteDtoResponse.setQuoteDto(cpqQuoteApi.updateQuote(quote));
	            return Response.ok(getQuoteDtoResponse).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, getQuoteDtoResponse.getActionStatus());
	        }
	}


	@Override
	public Response updateQuoteItem(QuoteOfferDTO quoteOfferDTO, UriInfo info) {
		GetQuoteOfferDtoResponse result=new GetQuoteOfferDtoResponse();
		 try {
			 result.setQuoteOfferDto(cpqQuoteApi.updateQuoteItem(quoteOfferDTO));
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e);
	        }
	}


	@Override
	public Response deleteQuote(String code, UriInfo info) {
		ActionStatus status = new ActionStatus();
		 try {
			 cpqQuoteApi.deleteQuote(code);
	            return Response.ok(status).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, status);
	        }
	}


	@Override
	public Response placeOrder(String quoteCode, int quoteVersion, UriInfo info) {
		ActionStatus status = new ActionStatus();
		 try {
			 cpqQuoteApi.placeOrder(quoteCode, quoteVersion);
	            return Response.ok(status).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, status);
	        }
	}


	@Override
	public Response createQuoteItem(QuoteOfferDTO quoteItem, UriInfo info) {
		GetQuoteOfferDtoResponse result=new GetQuoteOfferDtoResponse();
		 try {
	            result.setQuoteOfferDto(cpqQuoteApi.createQuoteItem(quoteItem));
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}


	@Override
	public Response createQuoteVersion(QuoteVersionDto quoteVersion, UriInfo info) {
		GetQuoteVersionDtoResponse result = new GetQuoteVersionDtoResponse();
		try {
            result.setQuoteVersionDto(cpqQuoteApi.createQuoteVersion(quoteVersion));
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}



	@Override
	public Response deleteQuoteVersion(String quoteCode, int quoteVersion, UriInfo info) {
		ActionStatus status = new ActionStatus();
		 try {
			 cpqQuoteApi.deleteQuoteVersion(quoteCode, quoteVersion);
	            return Response.ok(status).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, status);
	        }
	}


	@Override
	public Response deleteQuoteItem(Long quoteItemId, UriInfo info) {
		ActionStatus status = new ActionStatus();
		 try {
			 cpqQuoteApi.deleteQuoteItem(quoteItemId);
	            return Response.ok(status).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, status);
	        }
	}


//	@Override
//	public Response quoteQuotation(String quoteCode, int quoteVersion, UriInfo info) {
//		// TODO Auto-generated method stub
//		return null;
//	}
   
}