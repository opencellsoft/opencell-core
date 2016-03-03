package org.meveo.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;



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

	public static CloseableHttpClient getClient(String proxyHost, Integer proxyPort,String proxyLogin,String proxyPasswd) {
		if (httpClient == null) {
			HttpClientBuilder httpClientBuilder = HttpClients.custom();
			httpClientBuilder.setConnectionManager(connPool);
			if (proxyHost != null && proxyHost.trim().length() > 0) {
				int port = 80;
				if (proxyPort != null) {
					port = proxyPort.intValue();
				}
				HttpHost proxy = new HttpHost(proxyHost, port);
				httpClientBuilder.setProxy(proxy);							
				if(proxyLogin != null && proxyLogin.trim().length() > 0){
					Credentials credentials = new UsernamePasswordCredentials(proxyLogin,proxyPasswd);
					AuthScope authScope = new AuthScope(proxyHost, port);
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					credsProvider.setCredentials(authScope, credentials);
					httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
					httpClientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
				}						
			}
			httpClient = httpClientBuilder.build();
		}
		return httpClient;
	}

}