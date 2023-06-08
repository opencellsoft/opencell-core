package org.meveo.apiv2.esignature.service;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.apiv2.esignature.yousign.payload.IntiateSignatureRequest;
import org.meveo.model.esignature.DeliveryMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public abstract class SigantureRequestProcessus {
	
	protected static Logger log = LoggerFactory.getLogger(SigantureRequestProcessus.class);
	
	public abstract String  getSignatureApiKey();
	public abstract String getSignaturetUrl();
	
	public abstract String getModeOperator();
	
	private SigantureRequest sigantureRequest;
	
	private Client client;
	private WebTarget webTarget;
	
	
	
	public SigantureRequestProcessus(SigantureRequest sigantureRequest) {
		this.sigantureRequest = sigantureRequest;
	}
	
	public void process(){
		checkUrlAndApiKey();
		log.info("start singing to e-sign = " + getModeOperator() + " - step 1 : Initiate a signature request");
		this.client = ClientBuilder.newClient().register(AddHeaderAuthorization.class);
		this.webTarget = this.client.target(getSignaturetUrl());
		processStepOne();
	}
	
	/**
	 * this will initiate a siganture document and
	 */
	private void processStepOne(){
		this.webTarget.path("/signature_requests");
		final IntiateSignatureRequest intiateSignatureRequest = new IntiateSignatureRequest(sigantureRequest.getName(), DeliveryMode.EMAIL.getValue(sigantureRequest.getDelivery_mode()));
		final Entity<IntiateSignatureRequest> entity = Entity.entity(intiateSignatureRequest, MediaType.APPLICATION_JSON_TYPE);
		Response response = this.webTarget.request().post(entity, Response.class);
		log.info(response.toString());
	}
	
	private void checkUrlAndApiKey(){
		if(StringUtils.isEmpty(getSignatureApiKey()) && StringUtils.isEmpty(getSignaturetUrl())){
			throw new MissingParameterException("apikey, url");
		}else if(StringUtils.isEmpty(getSignatureApiKey())){
			throw new BusinessApiException("the apikey is mandatory to " + getModeOperator());
		}else if(StringUtils.isEmpty(getSignaturetUrl())) {
			throw new BusinessApiException("the url is mandatory to " + getModeOperator());
		}
	}
	
	private class AddHeaderAuthorization implements ClientRequestFilter {
		@Override
		public void filter(ClientRequestContext clientRequestContext) throws IOException {
			clientRequestContext.getHeaders().add("Authorization", "Bearer " + getSignatureApiKey());
		}
	}
}
