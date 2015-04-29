package org.meveo.api.rest;

import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.SellerApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.rest.impl.SellerRsImpl;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class SellerRsTest extends BaseRsTest {

	private static Logger log = Logger.getLogger(SellerRsTest.class.getName());

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap.create(WebArchive.class,
				"seller-api.war");

		result = initArchive(result);

		// seller
		result = result.addClasses(SellerRsImpl.class, SellerRs.class,
				GetSellerResponse.class, SellerApi.class, SellerService.class,
				TradingCountryService.class, TradingLanguageService.class,
				TradingCurrencyService.class);

		return result;
	}

	@RunAsClient
	@Test
	public void testVersion(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		ActionStatus result = resource.index();
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(100)
	public void testCreate(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(101)
	public void testCreateCurrencyDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST1");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD-");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(102)
	public void testCreateCountryDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST1");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US-");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(103)
	public void testCreateLanguageDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST1");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG-");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(104)
	public void testCreateParentSellerDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST1");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("-");

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(105)
	public void testCreateAlreadyExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(106)
	public void testUpdate(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(107)
	public void testUpdateCurrencyDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD-");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(108)
	public void testUpdateCountryDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US-");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(109)
	public void testUpdateLanguageDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG-");
		postData.setInvoicePrefix("");
		postData.setParentSeller("");

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(110)
	public void testUpdateParentSellerDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		SellerDto postData = new SellerDto();
		postData.setCode("E_TEST");
		postData.setDescription("TEST");
		postData.setCurrencyCode("USD");
		postData.setCountryCode("US");
		postData.setLanguageCode("ENG");
		postData.setInvoicePrefix("");
		postData.setParentSeller("-");

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(112)
	public void testFind(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		GetSellerResponse result = resource.find("E_TEST");
		log.info("response=" + result);
		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(113)
	public void testFindDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		GetSellerResponse result = resource.find("NONE");
		log.info("response=" + result);
		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(114)
	public void testRemove(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		ActionStatus result = resource.remove("E_TEST");
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(115)
	public void testRemoveDoesNotExists(
			@ArquillianResteasyResource("api/rest") SellerRs resource) {
		ActionStatus result = resource.remove("NONE");
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

}
