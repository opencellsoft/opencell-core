package org.meveo.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;



public class MeveoConnectionFactory   {

	private static final PoolingHttpClientConnectionManager connPool;

	static {

	        connPool = new PoolingHttpClientConnectionManager();
	        // Increase max total connection to 200
	        connPool.setMaxTotal(200);//configurable through app.properties
	        // Increase default max connection per route to 50
	        connPool.setDefaultMaxPerRoute(20);//configurable through app.properties

	}

	static CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connPool).build();

	
	}