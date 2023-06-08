package org.meveo.apiv2.esignature.impl;

import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.apiv2.esignature.resource.SignatureRequestResource;
import org.meveo.apiv2.esignature.service.SignatureRequestApiService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class SignatureRequestResourceImpl implements SignatureRequestResource {
	
	@Inject
	private SignatureRequestApiService signatureRequestApiService;
	@Override
	public Response sigantureRequest(SigantureRequest sigantureRequest) {
		return null;
	}
}
