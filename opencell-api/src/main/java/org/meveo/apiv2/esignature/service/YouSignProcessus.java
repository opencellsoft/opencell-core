package org.meveo.apiv2.esignature.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.meveo.api.YouSignApi;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class YouSignProcessus extends SignatureRequestProcess {
	
	
	private final FilesApi filesApi = (FilesApi) EjbUtils.getServiceInterface(FilesApi.class.getSimpleName());
	public YouSignProcessus(SigantureRequest sigantureRequest){
		super(sigantureRequest);
	}
	public YouSignProcessus(){
		super(null);
	}
	@Override
	public String getSignatureApiKey() {
		return paramBean.getProperty(YouSignApi.YOUSIGN_API_TOKEN_PROPERTY_KEY, null);
	}
	
	@Override
	public String getSignatureUrl() {
		return paramBean.getProperty(YouSignApi.YOUSIGN_API_URL_PROPERTY_KEY, null);
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
			addSigner(requestId, documentIds);
			log.info("start singing to e-sign = " + getModeOperator() + " - step 3 : adding signers : finished");
			
			log.info("start singing to e-sign = " + getModeOperator() + " - step 4 : activate signature request : ");
			return activateSiganture(requestId);
		} catch (IOException | InterruptedException e) {
			throw new BusinessApiException(e.getMessage());
		}
	}
	
	public Map<String, Object> fetch(String signatureRequestId)  {
		checkApiAndUrl();
		try {
			HttpResponse<String> response = getHttpRequestWithoutBody("/signature_requests/" + signatureRequestId, HttpMethod.GET);
			return gson.fromJson(response.body(), Map.class);
		} catch (IOException | InterruptedException e) {
			throw new BusinessApiException(e);
		}
	}
	
	public InputStream download(String signatureRequestId){
		checkApiAndUrl();
		try {
			HttpResponse<byte[]> downloadableFile = download("/signature_requests/" + signatureRequestId + "/documents/download", HttpMethod.GET);
			String fileName = null;
			if(downloadableFile.headers().map().get("content-disposition") != null) {
				String contentDisposition = downloadableFile.headers().map().get("content-disposition").get(0);
				fileName = contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
				if(fileName.contains("filename*")){
					fileName = contentDisposition.split(";")[1].split("filename=")[1];
				}
			}
			if(fileName == null) {
				throw new BusinessApiException("the file doesn't exist");
			}
			String yousignDir =  paramBean.getProperty(YouSignApi.YOUSIGN_API_DOWNLOAD_DIR_KEY, "/signeddocs");
			filesApi.createDir(yousignDir);
			File absoluteYousingDir = filesApi.checkAndGetExistingFile("signeddocs");
			Path filePath = Path.of(absoluteYousingDir + File.separator + fileName);
			Path newFile = Files.write(filePath, downloadableFile.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return new FileInputStream(newFile.toFile());
		} catch (IOException | InterruptedException e) {
			log.error("error while downloading file : ", e);
			throw new BusinessApiException(e.getMessage());
		}
	}
	
	private String processGenerateRequestId() throws IOException, InterruptedException {
		final IntiateSignatureRequest intiateSignatureRequest = new IntiateSignatureRequest(sigantureRequest.getName(), DeliveryMode.email.getValue(sigantureRequest.getDeliveryMode()));
		var response = getHttpRequestPost( "/signature_requests", intiateSignatureRequest);
		var sigantureRequestId = gson.fromJson(response.body(), Map.class);
		if(sigantureRequestId.get("id") == null){
			log.error(response.body());
			throw new BusinessApiException("Error while generating a signature request id : detail " + sigantureRequestId.get("detail"));
		}
		return sigantureRequestId.get("id").toString();
	}
	
	private Map<FilesSignature, String> uploadDocument(String requestId) throws IOException {
		Map<FilesSignature, String> result = new HashMap<>();
		for(FilesSignature fileSigners: Objects.requireNonNull(sigantureRequest.getFilesToSign())){
			if(StringUtils.isEmpty(fileSigners.getFilePath())){
				throw new MissingParameterException("filesToSign.filePath");
			}
			File file = filesApi.checkAndGetExistingFile(fileSigners.getFilePath());
			HttpEntity entity = MultipartEntityBuilder.create()
					.addPart("file", new FileBody(file))
					.addTextBody("nature", NatureDocument.getValue(fileSigners.getNature()))
					.addTextBody("parse_anchors", fileSigners.getParseAnchors() + "")
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
	
	private void addSigner(String signatureRequestId, Map<FilesSignature, String> documentIds) throws IOException, InterruptedException {
		Map<String, String> result = new HashMap<>();
		for(Signers signer: Objects.requireNonNull(sigantureRequest.getSigners())){
			for(FilesSignature docInfo : documentIds.keySet()){
				InfoSigner info = signer.getInfo();
				Signer signerToSend = new Signer(info.getFirstName(), info.getLastName(), info.getEmail(),
						info.getPhoneNumber(), info.getLocale(), "electronic_signature", SigantureAuthentificationMode.getValue(signer.getSignatureAuthenticationMode()) );
				for(SignatureFields fields : Objects.requireNonNull(signer.getFields())){
					signerToSend.addFields(documentIds.get(docInfo).toString(), fields.getPage(), fields.getWidth(), fields.getX(), fields.getY());
				}
				var response = getHttpRequestPost( "/signature_requests" + "/" +signatureRequestId + "/signers", signerToSend);
				Map<String, Object> jsonBody = gson.fromJson(response.body(), Map.class);
				if(jsonBody.get("id") == null) {
					log.error(response.body());
					throw new BusinessApiException("Problem occur when adding signer : " + info.getLastName() + ". detail : " + jsonBody.get("detail"));
				}
				result.put(info.getEmail(), jsonBody.get("id").toString());
				log.info("signer " + info.getLastName() + " for document " + documentIds.get(docInfo).toString());
			}
		}
	}
	
	private Map<String, Object> activateSiganture(String sigantureRequestId) throws IOException, InterruptedException {
		HttpResponse<String> response =getHttpRequestPost("/signature_requests/" + sigantureRequestId + "/activate");
		return gson.fromJson(response.body(), Map.class);
	}
}
