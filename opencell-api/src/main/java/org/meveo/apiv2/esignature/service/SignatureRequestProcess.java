package org.meveo.apiv2.esignature.service;

import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
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
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.esignature.DeliveryMode;
import org.meveo.model.esignature.NatureDocument;
import org.meveo.model.esignature.SigantureAuthentificationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class SignatureRequestProcess {
	
	protected static Logger log = LoggerFactory.getLogger(SignatureRequestProcess.class);
	protected final ParamBean PARAMBEAN = ParamBean.getInstance();
	public abstract String  getSignatureApiKey();
	public abstract String getSignatureUrl();
	
	public abstract String getModeOperator();
	
	public abstract Map<String, Object> process();
	
	protected final SigantureRequest sigantureRequest;
	
	protected final Gson gson = new Gson();
	
	protected HttpClient httpClient = HttpClient.newHttpClient();
	
	protected  enum  HttpMethod {
		POST, GET
	}
	
	
	public SignatureRequestProcess(SigantureRequest sigantureRequest) {
		this.sigantureRequest = sigantureRequest;
	}
	
	
	protected HttpResponse<String> getHttpRequestPost(String path, Object entity) throws IOException, InterruptedException {
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		if(entity == null){
			throw new BusinessApiException("The payload for path " + path + " is mandatory");
		}
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header("Authorization", "Bearer " + getSignatureApiKey())
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.method("POST", HttpRequest.BodyPublishers.ofString(gson.toJson(entity)));
		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
	}
	
	protected HttpResponse<String> getHttpRequestWithoutBody(String path, HttpMethod httpMethod) throws IOException, InterruptedException {
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header("Authorization", "Bearer " + getSignatureApiKey())
				.method( httpMethod.name(), HttpRequest.BodyPublishers.noBody());
		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
	}
	
	protected HttpResponse<String> getHttpRequestPost(String path) throws IOException, InterruptedException {
		if(!path.startsWith("/")){
			path += "/" + path;
		}
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header("Authorization", "Bearer " + getSignatureApiKey())
				.method("POST", HttpRequest.BodyPublishers.noBody());
		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
	}
	
	protected void checkUrlAndApiKey(){
		List<String> errors = new ArrayList<>();
		if(StringUtils.isEmpty(getSignatureApiKey()) && StringUtils.isEmpty(getSignatureUrl())){
			errors.add("apikey");
			errors.add("url");
		}else if(StringUtils.isEmpty(getSignatureApiKey())){
			throw new BusinessApiException("the apikey is mandatory to " + getModeOperator());
		}else if(StringUtils.isEmpty(getSignatureUrl())) {
			throw new BusinessApiException("the url is mandatory to " + getModeOperator());
		}
		if(CollectionUtils.isEmpty(sigantureRequest.getFilesToSign())){
			errors.add("filesToSign");
		}
		if(CollectionUtils.isEmpty(sigantureRequest.getSigners())){
			throw new BusinessApiException("At least one signer must exist");
		}
		for(Signers signer: sigantureRequest.getSigners()) {
			if(signer.getInfo() == null) {
				errors.add("signers.info");
			}else	if(StringUtils.isEmpty(signer.getInfo().getLast_name())){
				errors.add("signers.info.last_name");
			}
		}
		if(!errors.isEmpty()){
			throw  new MissingParameterException(errors);
		}
	}
}
