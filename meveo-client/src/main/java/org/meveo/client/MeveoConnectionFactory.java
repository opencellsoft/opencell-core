package org.meveo.client;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
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

	public static CloseableHttpClient getClient(String proxyHost, Integer proxyPort) {
		if (httpClient == null) {
			HttpClientBuilder httpClientBuilder = HttpClients.custom();
			httpClientBuilder.setConnectionManager(connPool);
			if (proxyHost != null && proxyHost.trim().length() > 0) {
				int port = 80;
				if (proxyPort != null) {
					port = proxyPort.intValue();
				}
				HttpHost proxy = new HttpHost(proxyHost, port);
				DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
				httpClientBuilder.setRoutePlanner(routePlanner);
			}
			httpClient = httpClientBuilder.build();
		}
		return httpClient;
	}

}