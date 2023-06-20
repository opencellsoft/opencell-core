package org.meveo.apiv2.esignature.service;

import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.model.esignature.Operator;

import java.util.Map;
import java.util.Objects;

public class SignatureRequestApiService {

	private SignatureRequestProcess signatureRequestProcess;
	
	public Map<String, Object> youSignRequest(SigantureRequest sigantureRequest) {
		signatureRequestProcess = new YouSignProcessus(sigantureRequest);
		return signatureRequestProcess.process();
	}
	
	public Map<String, Object> fetchSignatureRequest(Operator operator, String signatureRequestId){
		if (Objects.requireNonNull(operator) == Operator.YOUSIGN) {
			signatureRequestProcess = new YouSignProcessus();
			return ((YouSignProcessus) signatureRequestProcess).fetch(signatureRequestId);
		}
		return null;
	}
}
