package org.meveo.apiv2.esignature.service;

import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.api.YouSignApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.esignature.SigantureRequest;
import org.meveo.apiv2.esignature.Signers;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SignatureRequestProcess {
	
	public static final String AUTHORIZATION = "Authorization";
	protected static Logger log = LoggerFactory.getLogger(SignatureRequestProcess.class);
	protected final ParamBean PARAMBEAN = ParamBean.getInstance();
	public abstract String  getSignatureApiKey();
	public abstract String getSignatureUrl();
	
	public abstract String getModeOperator();
	
	public abstract Map<String, Object> process();
	
	protected final SigantureRequest sigantureRequest;
	
	protected final Gson gson = new Gson();
	
	protected static HttpClient httpClient = HttpClient.newHttpClient();
	
	protected  enum  HttpMethod {
		POST, GET
	}
	
	protected SignatureRequestProcess(SigantureRequest sigantureRequest) {
		this.sigantureRequest = sigantureRequest;
	}
	
	
	protected HttpResponse<String> getHttpRequestPost(String path, Object entity) throws IOException, InterruptedException {
		path = checkSlash(path);
		if(entity == null){
			throw new BusinessApiException("The payload for path " + path + " is mandatory");
		}
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header(AUTHORIZATION, getBarearToken())
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.method("POST", HttpRequest.BodyPublishers.ofString(gson.toJson(entity)));
		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
	}
	
	protected HttpResponse<String> getHttpRequestWithoutBody(String path, HttpMethod httpMethod) throws IOException, InterruptedException {
		path = checkSlash(path);
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header(AUTHORIZATION, getBarearToken())
				.method( httpMethod.name(), HttpRequest.BodyPublishers.noBody());
		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
	}
	
	protected HttpResponse<String> getHttpRequestPost(String path) throws IOException, InterruptedException {
		path = checkSlash(path);
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header(AUTHORIZATION, getBarearToken())
				.method("POST", HttpRequest.BodyPublishers.noBody());
		
		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
	}
	
	protected HttpResponse<byte[]> download(String path, HttpMethod httpMethod) throws IOException, InterruptedException {
		path = checkSlash(path);
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(getSignatureUrl() + path))
				.header(AUTHORIZATION, getBarearToken())
				.header("accept", "application/pdf")
				.method(httpMethod.name(), HttpRequest.BodyPublishers.noBody());
		return  httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
	}
	
	private String checkSlash(String path){
		if(!path.startsWith("/")){
			path += "/" + path;
		}
		return path;
	}
	
	protected void checkUrlAndApiKey(){
		List<String> errors = checkApiAndUrl();
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
	
	protected  List<String> checkApiAndUrl() {
		List<String> errors = new ArrayList<>();
		if(StringUtils.isEmpty(getSignatureApiKey()) && StringUtils.isEmpty(getSignatureUrl())){
			errors.add(YouSignApi.YOUSIGN_API_TOKEN_PROPERTY_KEY);
			errors.add(YouSignApi.YOUSIGN_API_URL_PROPERTY_KEY);
		}else if(StringUtils.isEmpty(getSignatureApiKey())){
			throw new BusinessApiException("the "+YouSignApi.YOUSIGN_API_TOKEN_PROPERTY_KEY+" is mandatory to " + getModeOperator());
		}else if(StringUtils.isEmpty(getSignatureUrl())) {
			throw new BusinessApiException("the "+YouSignApi.YOUSIGN_API_URL_PROPERTY_KEY+" is mandatory to " + getModeOperator());
		}
		if(!errors.isEmpty()){
			throw  new MissingParameterException(errors);
		}
		return errors;
	}
	
	private String getBarearToken(){
		return "Bearer " + getSignatureApiKey();
	}
}
