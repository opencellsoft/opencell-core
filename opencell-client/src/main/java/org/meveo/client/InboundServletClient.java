package org.meveo.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
public class InboundServletClient {
	
	private static final Logger log = LoggerFactory.getLogger(InboundServletClient.class);

	public static void main(String args[]) {
		new InboundServletClient().callClient();
	}

	public void callClient() {
		System.out.println("calling inboud servlet...");
		URL inboundServlet = null;
		try {
			inboundServlet = new URL("http://192.168.0.120:8080/meveo/inbound/demo/");
			HttpURLConnection servletConnection = (HttpURLConnection) inboundServlet.openConnection();

			// String userCredentials = "meveo.admin:meveo.admin";
			// String basicAuth = "Basic " + new
			// String(Base64.encodeBytes(userCredentials.getBytes()));
			// servletConnection.setRequestProperty("Authorization", basicAuth);

			String urlParameters = "param1=a&param2=b&param3=c";
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;

			servletConnection.setDoOutput(true);
			servletConnection.setRequestMethod("POST");
			servletConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			servletConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			servletConnection.setRequestProperty("Content-Language", "en-US");
			servletConnection.setRequestProperty("charset", "utf-8");
			servletConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			servletConnection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(servletConnection.getOutputStream());
			wr.write(postData);
			wr.flush();
			wr.close();

			int responseCode = servletConnection.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + inboundServlet.getPath());
			System.out.println("Response Code : " + responseCode);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.error("error = {}", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("error = {}", e);
		}
	}

}