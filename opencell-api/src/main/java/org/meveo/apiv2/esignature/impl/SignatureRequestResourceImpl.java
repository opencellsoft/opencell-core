package org.meveo.apiv2.esignature.impl;

import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.apiv2.esignature.resource.SignatureRequestResource;
import org.meveo.apiv2.esignature.service.SignatureRequestApiService;
import org.meveo.model.esignature.Operator;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class SignatureRequestResourceImpl implements SignatureRequestResource {
	
	@Inject
	private SignatureRequestApiService signatureRequestApiService;
	@Override
	public Response sigantureRequest(SigantureRequest sigantureRequest) {
		return Response.ok(signatureRequestApiService.youSignRequest(sigantureRequest)).build();
	}
	
	@Override
	public Response fetchSignatureRequest(Operator operator, String signatureRequestId) {
		return Response.ok(signatureRequestApiService.fetchSignatureRequest(operator, signatureRequestId)).build();
	}
	
	@Override
	public Response download(Operator operator, String signatureRequestId) {
		return Response.ok(signatureRequestApiService.download(operator, signatureRequestId)).build();
	}
}
