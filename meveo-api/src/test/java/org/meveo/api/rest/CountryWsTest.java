package org.meveo.api.rest;

import java.net.URI;
import java.net.URL;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class CountryWsTest {

	private Logger log = LoggerFactory.getLogger(CountryWsTest.class);

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap.create(WebArchive.class,
				"rest-country.war");

		// File[] files = Maven.resolver().loadPomFromFile("pom.xml")
		// .resolve("org.meveo:meveo-api").withTransitivity().asFile();

		result.addPackage("org.meveo");

		result.addAsResource("META-INF/test-persistence.xml",
				"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-ds.xml", "test-ds.xml");

		return result;
	}

	URI buildUri(URL deploymentUrl, String... paths) {
		UriBuilder builder = UriBuilder.fromUri(deploymentUrl.toString());
		for (String path : paths) {
			builder.path(path);
		}
		return builder.build();
	}

	@RunAsClient
	@Test
	public void testGetVersion(@ArquillianResource URL deploymentUrl) {
		log.debug("Version test");
		URI uri = buildUri(deploymentUrl, "api/rest", "country", "version");

		log.debug("URI={}", uri.toString());

		RestAssured.given().body(new CountryWs()).contentType(ContentType.JSON)
				.expect().contentType(ContentType.JSON)
				.statusCode(Status.OK.getStatusCode()).when().post(uri);
	}

}
