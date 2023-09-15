package org.meveo.apiv2.esignature.impl;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.sign.YousignEventEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.apiv2.esignature.SignatureRequestWebHookPayload;
import org.meveo.apiv2.esignature.SignatureRequestWebhook;
import org.meveo.apiv2.esignature.resource.SignatureRequestResource;
import org.meveo.apiv2.esignature.service.SignatureRequestApiService;
import org.meveo.model.esignature.Operator;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.Map;

@Interceptors({ WsRestApiInterceptor.class })
public class SignatureRequestResourceImpl extends BaseRs implements SignatureRequestResource {
	
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
	
	@Override
	public ActionStatus signatureRequestDone(Operator operator, SignatureRequestWebHookPayload signatureRequestWebHookPayload) {
		log.info("callback from yousign : " + signatureRequestWebHookPayload);
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			String eventName = signatureRequestWebHookPayload.getEventName();
			if(StringUtils.isEmpty(eventName) || !"signature_request.done".equalsIgnoreCase(eventName)) {
				return new ActionStatus(ActionStatusEnum.FAIL, " Event not supported : " + eventName);
			}
			if(signatureRequestWebHookPayload.getData() != null &&
					signatureRequestWebHookPayload.getData().getSignatureRequestWebhook() != null &&
					signatureRequestWebHookPayload.getData().getSignatureRequestWebhook().getDocuments() != null){
				String signatureRequestId = signatureRequestWebHookPayload.getData().getSignatureRequestWebhook().getId();
				signatureRequestApiService.download(operator, signatureRequestId);
			}
		} catch (Exception e) {
			processException(e, new ActionStatus(ActionStatusEnum.FAIL, e.getMessage()));
		}
		return result;
	}
}
