package org.meveo.apiv2.esignature.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.meveo.api.admin.FilesApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.esignature.FilesSignature;
import org.meveo.apiv2.esignature.InfoSigner;
import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.apiv2.esignature.SignatureFields;
import org.meveo.apiv2.esignature.Signers;
import org.meveo.apiv2.esignature.yousign.payload.IntiateSignatureRequest;
import org.meveo.apiv2.esignature.yousign.payload.Signer;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.esignature.DeliveryMode;
import org.meveo.model.esignature.NatureDocument;
import org.meveo.model.esignature.Operator;
import org.meveo.model.esignature.SigantureAuthentificationMode;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class YouSignProcessus extends SignatureRequestProcess {
	
	
	private final FilesApi filesApi = (FilesApi) EjbUtils.getServiceInterface(FilesApi.class.getSimpleName());
	public YouSignProcessus(SigantureRequest sigantureRequest){
		super(sigantureRequest);
	}
	@Override
	public String getSignatureApiKey() {
		return PARAMBEAN.getProperty("gateway.yousign.apikey", null);
	}
	
	@Override
	public String getSignatureUrl() {
		return PARAMBEAN.getProperty("gateway.yousign.url", null);
	}
	
	@Override
	public String getModeOperator() {
		return Operator.YOUSIGN.toString();
	}
	
	public Map<String, Object> process(){
		checkUrlAndApiKey();
		try {
			log.info("start singing to e-sign = " + getModeOperator() + " - step 1 : Initiate a signature request");
			String requestId = processGenerateRequestId();
			log.info("start singing to e-sign = " + getModeOperator() + " - step 1 : finish request id = " + requestId);
			
			log.info("start singing to e-sign = " + getModeOperator() + " - step 2 : uploading files : ");
			Map<FilesSignature, String> documentIds =  uploadDocument(requestId);
			log.info("start singing to e-sign = " + getModeOperator() + " - step 2 : uploading files : finished ");
			
			log.info("start singing to e-sign = " + getModeOperator() + " - step 3 : adding signers : ");
			Map<String, String> signers = addSigner(requestId, documentIds);
			log.info("start singing to e-sign = " + getModeOperator() + " - step 3 : adding signers : finished");
			
			log.info("start singing to e-sign = " + getModeOperator() + " - step 4 : activate signature request : ");
			return activateSiganture(requestId);
		} catch (IOException | InterruptedException e) {
			throw new BusinessApiException(e.getMessage());
		}
	}
	public String processGenerateRequestId() throws IOException, InterruptedException {
		final IntiateSignatureRequest intiateSignatureRequest = new IntiateSignatureRequest(sigantureRequest.getName(), DeliveryMode.email.getValue(sigantureRequest.getDelivery_mode()));
		var response = getHttpRequestPost( "/signature_requests", intiateSignatureRequest);
		var sigantureRequestId = gson.fromJson(response.body(), Map.class);
		if(sigantureRequestId.get("id") == null){
			log.error(response.body());
			throw new BusinessApiException("Error while generating a signature request id : detail " + sigantureRequestId.get("detail"));
		}
		return sigantureRequestId.get("id").toString();
	}
	
	public Map<FilesSignature, String> uploadDocument(String requestId) throws IOException {
		Map<FilesSignature, String> result = new HashMap<>();
		for(FilesSignature fileSigners: Objects.requireNonNull(sigantureRequest.getFilesToSign())){
			if(StringUtils.isEmpty(fileSigners.getFilePath())){
				throw new MissingParameterException("filesToSign.filePath");
			}
			File file = filesApi.checkAndGetExistingFile(fileSigners.getFilePath());
			HttpEntity entity = MultipartEntityBuilder.create()
					.addPart("file", new FileBody(file))
					.addTextBody("nature", NatureDocument.getValue(fileSigners.getNature()))
					.addTextBody("parse_anchors", fileSigners.getParse_anchors() + "")
					.build();
			HttpPost request = new HttpPost(URI.create(getSignatureUrl() + "/signature_requests/" + requestId + "/documents"));
			request.setHeader("Authorization", "Bearer " + getSignatureApiKey());
			request.setHeader(HttpHeaders.ACCEPT, MediaType.MULTIPART_FORM_DATA);
			request.setEntity(entity);
			CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request);
			var currentResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), Map.class);
			if( currentResponse.get("id") != null){
				result.put(fileSigners, currentResponse.get("id").toString());
				log.info("file : " + file.getName() + " uploaded successfully");
			}
		}
		return result;
		
	}
	
	public Map<String, String> addSigner(String signatureRequestId, Map<FilesSignature, String> documentIds) throws IOException, InterruptedException {
		Map<String, String> result = new HashMap<>();
		for(Signers signer: Objects.requireNonNull(sigantureRequest.getSigners())){
			for(FilesSignature docInfo : documentIds.keySet()){
				InfoSigner info = signer.getInfo();
				Signer signerToSend = new Signer(info.getFirst_name(), info.getLast_name(), info.getEmail(),
						info.getPhone_number(), info.getLocale(), "electronic_signature", SigantureAuthentificationMode.getValue(signer.getSignature_authentication_mode()) );
				if(!docInfo.getParse_anchors()) {
					for(SignatureFields fields : Objects.requireNonNull(signer.getFields())){
						signerToSend.addFields(documentIds.get(docInfo).toString(), fields.getPage(), fields.getWidth(), fields.getX(), fields.getY());
					}
				}
				var response = getHttpRequestPost( "/signature_requests" + "/" +signatureRequestId + "/signers", signerToSend);
				Map<String, Object> jsonBody = gson.fromJson(response.body(), Map.class);
				if(jsonBody.get("id") == null) {
					log.error(response.body());
					throw new BusinessApiException("Problem occur when adding signer : " + info.getLast_name() + ". detail : " + jsonBody.get("detail"));
				}
				result.put(info.getEmail(), jsonBody.get("id").toString());
				log.info("signer " + info.getLast_name() + " for document " + documentIds.get(docInfo).toString());
			}
		}
		return result;
	}
	
	private Map<String, Object> activateSiganture(String sigantureRequestId) throws IOException, InterruptedException {
		HttpResponse<String> response =getHttpRequestPost("/signature_requests/" + sigantureRequestId + "/activate");
		return gson.fromJson(response.body(), Map.class);
	}
}
