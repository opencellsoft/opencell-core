package org.meveo.api.rest;

import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.TaxApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class TaxWsTest extends BaseWsTest {

	private static Logger log = Logger.getLogger(TaxWsTest.class.getName());

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap.create(WebArchive.class, "tax-api.war");

		result = initArchive(result);

		// country
		result = result.addClasses(TaxWsImpl.class, TaxWs.class,
				GetTaxResponse.class, TaxApi.class, TaxService.class,
				CatMessagesService.class);

		return result;
	}
	
	@RunAsClient
	@Test
	public void testVersion(
			@ArquillianResteasyResource("api/rest") TaxWs resource) {
		ActionStatus result = resource.index();
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

}
