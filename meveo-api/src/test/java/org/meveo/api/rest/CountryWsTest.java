package org.meveo.api.rest;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.ComponentResources;
import org.meveo.admin.util.LoggerProducer;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.CountryApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.logging.Logged;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.security.WSSecured;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.Resources;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class CountryWsTest {

	@ArquillianResource
	private URL deploymentURL;

	private static Logger log = Logger.getLogger(CountryWsTest.class.getName());

	@BeforeClass
	public static void init() {
		log.info("beforeClass");
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap.create(WebArchive.class,
				"meveo-api-test.war");

		// add seam security
		File[] seamDependencies = Maven.resolver()
				.resolve("org.jboss.seam.security:seam-security:3.1.0.Final")
				.withTransitivity().asFile();
		result.addAsLibraries(seamDependencies);

		// producers
		result = result
				.addClasses(Resources.class, LoggerProducer.class,
						MeveoJpa.class, MeveoJpaForJobs.class,
						ComponentResources.class);

		// common classes
		result = result.addClasses(StringUtils.class);

		// base services
		result = result.addClasses(PersistenceService.class,
				IPersistenceService.class, BaseService.class,
				ProviderService.class, UserService.class, RoleService.class,
				TitleService.class, PaginationConfiguration.class,
				QueryBuilder.class, ParamBean.class);

		// base api
		result = result
				.addClasses(JaxRsActivator.class, BaseWs.class,
						WSSecured.class, IBaseWs.class, BaseApi.class,
						LoggingInterceptor.class, Logged.class,
						MeveoApiErrorCode.class);

		// country
		result = result.addClasses(CountryWsImpl.class, CountryWs.class,
				GetCountryResponse.class, CountryApi.class,
				CountryService.class, TradingCountryService.class,
				CurrencyService.class, TradingCurrencyService.class);

		result = result.addPackages(true, "org/meveo/api/dto");

		// add models
		result = result.addPackages(true, "org/meveo/model");

		// add exceptions
		result = result.addPackage("org/meveo/api/message/exception");
		result = result.addPackage("org/meveo/api/exception");
		result = result.addPackage("org/meveo/admin/exception");

		result = result
				.addAsResource("META-INF/test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-ds.xml", "test-ds.xml")
				// initialize db
				.addAsResource("import.sql", "import.sql");

		return result;
	}

	@RunAsClient
	@Test
	public void testVersion(
			@ArquillianResteasyResource("api/rest") CountryWs countryWs) {
		ActionStatus result = countryWs.index();
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	public void testCreate(
			@ArquillianResteasyResource("api/rest") CountryWs countryWs) {
		CountryDto countryDto = new CountryDto();
		countryDto.setCountryCode("PH");
		countryDto.setCurrencyCode("PHP");
		countryDto.setLanguageCode("ENG");
		countryDto.setName("Philippines");

		ActionStatus result = countryWs.create(countryDto);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	public void testFind(
			@ArquillianResteasyResource("api/rest") CountryWs countryWs) {
		GetCountryResponse result = countryWs.find("PH");
		log.info("response=" + result);
		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	public void testRemove(
			@ArquillianResteasyResource("api/rest") CountryWs countryWs) {
		ActionStatus result = countryWs.remove("PH", "PHP");
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

}
