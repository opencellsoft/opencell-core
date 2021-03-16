/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
		log.info("calling inbound servlet...");
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
			log.info("\nSending 'POST' request to URL : " + inboundServlet.getPath());
			log.info("Response Code : " + responseCode);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.error("error = {}", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("error = {}", e);
		}
	}

}