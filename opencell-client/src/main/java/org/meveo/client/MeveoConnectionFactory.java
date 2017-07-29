package org.meveo.client;



import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.meveo.commons.utils.StringUtils;



public class MeveoConnectionFactory {

	private static final PoolingHttpClientConnectionManager connPool;

	static {
		connPool = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
		connPool.setMaxTotal(200);// TODO paramBean
		// Increase default max connection per route to 50
		connPool.setDefaultMaxPerRoute(50);
	}
	private static  CloseableHttpClient httpClient = null;

	public static CloseableHttpClient getClient(ProxyInfos proxyInfos,String tlsVersion) {
		if (httpClient == null) {
			HttpClientBuilder httpClientBuilder = HttpClients.custom();
			httpClientBuilder.setConnectionManager(connPool);
			if (proxyInfos != null && proxyInfos.getHost() != null && proxyInfos.getHost().length() > 0) {
				int port = 80;
				if (proxyInfos.getPort() != null) {
					port = proxyInfos.getPort().intValue();
				}
				HttpHost proxy = new HttpHost(proxyInfos.getHost(), port,proxyInfos.getSchemeName());
				httpClientBuilder.setProxy(proxy);							
				if(proxyInfos.getLogin() != null && proxyInfos.getLogin().trim().length() > 0){
					Credentials credentials = new UsernamePasswordCredentials(proxyInfos.getLogin(),proxyInfos.getPasswd());
					AuthScope authScope = new AuthScope(proxyInfos.getLogin(), port);
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					credsProvider.setCredentials(authScope, credentials);
					httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
					httpClientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
				}						
			}
			if(!StringUtils.isBlank(tlsVersion)){
				System.out.println("tlsversion:"+tlsVersion);
				 try{

					SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
							SSLContexts.custom().useTLS().build(),
					    new String[]{tlsVersion},   
					    null,
					    new NoopHostnameVerifier());

					httpClient = httpClientBuilder
					    .setSSLSocketFactory(f)
					    .build();
					
				 }catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				httpClient = httpClientBuilder.build();
			}
		}
		return httpClient;
	}

}