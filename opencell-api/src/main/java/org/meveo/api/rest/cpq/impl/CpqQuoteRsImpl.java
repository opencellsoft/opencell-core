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

import java.util.Collections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.billing.CpqQuoteApi;
import org.meveo.api.dto.cpq.QuoteAttributeDTO;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.response.cpq.GetQuoteDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.cpq.CpqQuoteRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.cpq.CpqQuote;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CpqQuoteRsImpl extends BaseRs implements CpqQuoteRs {

    @Inject
    private CpqQuoteApi cpqQuoteApi;



	@Override
	public Response createQuote(QuoteDTO quote, UriInfo info) {
		 try {
	            Long id = cpqQuoteApi.createQuote(quote);
	            return Response.ok(Collections.singletonMap("id", id)).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e);
	        }
	}


	@Override
	public Response getQuote(String code, UriInfo info) {
		 GetQuoteDtoResponse getQuoteDtoResponse = new GetQuoteDtoResponse();
		 try {
			 getQuoteDtoResponse.setQuoteDto(cpqQuoteApi.getQuote(code));
	            return Response.ok(getQuoteDtoResponse).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, getQuoteDtoResponse.getActionStatus());
	        }
	}


	@Override
	public Response findQuotes(UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response updateQuote(String code, QuoteDTO quote, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response updateQuoteItem(String code, QuoteAttributeDTO quoteitem, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response deleteQuote(String code, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response placeOrder(String id, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response createQuoteItem(QuoteAttributeDTO quoteItem, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response createQuoteVersion(QuoteVersionDto quoteVersion, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response updateQuoteVersion(QuoteVersionDto quoteVersion, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response deleteQuoteVersion(String quoteCode, int quoteVersion, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response deleteQuoteItem(String code, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response quoteQuotation(String quoteCode, int quoteVersion, UriInfo info) {
		// TODO Auto-generated method stub
		return null;
	}
   
}