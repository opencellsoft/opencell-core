package org.meveo.api.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import org.meveo.api.TaxApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.rest.impl.TaxRsImpl;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class TaxRsTest extends BaseRsTest {

	private static Logger log = Logger.getLogger(TaxRsTest.class.getName());

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap.create(WebArchive.class, "tax-api.war");

		result = initArchive(result);

		// country
		result = result.addClasses(TaxRsImpl.class, TaxRs.class,
				GetTaxResponse.class, TaxApi.class, TaxService.class,
				CatMessagesService.class);

		return result;
	}

	@RunAsClient
	@Test
	public void testVersion(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		ActionStatus result = resource.index();
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(1)
	public void testCreate(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("E_TEST");
		postData.setAccountingCode("E_TEST");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(2)
	public void testCreateAlreadyExists(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("E_TEST");
		postData.setAccountingCode("E_TEST");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(3)
	public void testCreateLanguageNotSupportedByProvider(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("E_TEST-NONE");
		postData.setAccountingCode("E_TEST");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG-NONE");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(4)
	public void testCreateMissingParameters(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("");
		postData.setAccountingCode("");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.create(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(100)
	public void testFind(@ArquillianResteasyResource("api/rest") TaxRs resource) {
		GetTaxResponse result = resource.find("E_TEST");
		log.info("response=" + result);

		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(100)
	public void testFindOneLanguage(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		GetTaxResponse result = resource.find("E_TEST");
		log.info("response=" + result);

		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.SUCCESS);
		// count language
		Assert.assertEquals(1, result.getTax().getLanguageDescriptions().size());
	}

	@RunAsClient
	@Test
	@InSequence(201)
	public void testUpdate(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("E_TEST");
		postData.setAccountingCode("E_TEST");
		postData.setDescription("E_TEST-Updated");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld1 = new LanguageDescriptionDto();
		ld1.setLanguageCode("ENG");
		ld1.setDescription("English");		
		languageDescriptions.add(ld1);
		
		LanguageDescriptionDto ld2 = new LanguageDescriptionDto();
		ld2.setLanguageCode("FRA");
		ld2.setDescription("French");		
		languageDescriptions.add(ld2);
		
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(202)
	public void testUpdateDoesNotExists(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("E_TEST-NONE");
		postData.setAccountingCode("E_TEST");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(203)
	public void testUpdateLanguageNotSupportedByProvider(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("E_TEST-NONE");
		postData.setAccountingCode("E_TEST");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG-NONE");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(204)
	public void testUpdateMissingParameters(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		TaxDto postData = new TaxDto();
		postData.setCode("");
		postData.setAccountingCode("");
		postData.setDescription("E_TEST");
		postData.setPercent(new BigDecimal(.2));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		LanguageDescriptionDto ld = new LanguageDescriptionDto();
		ld.setLanguageCode("ENG");
		ld.setDescription("English");
		languageDescriptions.add(ld);
		postData.setLanguageDescriptions(languageDescriptions);

		ActionStatus result = resource.update(postData);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(300)
	public void testFindTwoLanguage(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		GetTaxResponse result = resource.find("E_TEST");
		log.info("response=" + result);

		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.SUCCESS);
		// count language
		Assert.assertEquals(2, result.getTax().getLanguageDescriptions().size());
	}

	@RunAsClient
	@Test
	@InSequence(400)
	public void testRemove(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		ActionStatus result = resource.remove("E_TEST");
		log.info("response=" + result);
	}

	@RunAsClient
	@Test
	@InSequence(401)
	public void testRemoveDoesNotExists(
			@ArquillianResteasyResource("api/rest") TaxRs resource) {
		ActionStatus result = resource.remove("E_TEST-NONE");
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

}
