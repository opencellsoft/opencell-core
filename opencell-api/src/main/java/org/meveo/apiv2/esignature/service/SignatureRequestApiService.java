package org.meveo.apiv2.esignature.service;

import org.meveo.apiv2.esignature.SigantureRequest;

import java.util.Map;

public class SignatureRequestApiService {

	public Map<String, Object> youSignRequest(SigantureRequest sigantureRequest) {
		YouSignProcessus youSignProcessus = new YouSignProcessus(sigantureRequest);
		return youSignProcessus.process();
	}
}
