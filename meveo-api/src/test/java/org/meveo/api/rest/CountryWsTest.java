package org.meveo.api.rest;

import java.io.File;
import java.net.URL;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.ComponentResources;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.CountryServiceApi;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
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
import org.meveo.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class CountryWsTest {

	private static Logger log = LoggerFactory.getLogger(CountryWsTest.class);

	@ArquillianResource
	private URL deploymentURL;

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap.create(WebArchive.class,
				"meveo-api-country-test.war");

		// add seam security
		File[] seamDependencies = Maven.resolver()
				.resolve("org.jboss.seam.security:seam-security:3.1.0.Final")
				.withTransitivity().asFile();
		result.addAsLibraries(seamDependencies);

		// base services
		result.addClasses(Resources.class, PersistenceService.class,
				IPersistenceService.class, BaseService.class,
				PaginationConfiguration.class, QueryBuilder.class);

		// base api
		result.addClasses(BaseApi.class, ProviderService.class,
				UserService.class, RoleService.class, ParamBean.class,
				ComponentResources.class, TitleService.class);

		result.addClasses(CountryWs.class, CountryWsImpl.class,
				CountryServiceApi.class, CountryService.class,
				TradingCountryService.class, CurrencyService.class,
				TradingCurrencyService.class);

		result.addPackages(true, "org/meveo/api/dto");

		// add models
		result.addPackages(true, "org/meveo/model");

		// add exceptions
		result.addPackage("org/meveo/api/message/exception");
		result.addPackage("org/meveo/api/exception");
		result.addPackage("org/meveo/admin/exception");

		result.addAsResource("META-INF/test-persistence.xml",
				"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-ds.xml", "test-ds.xml");

		return result;
	}

	@RunAsClient
	@Test
	public void testFind(@ArquillianResteasyResource CountryWs countryWs) {
		GetCountryResponse result = countryWs.find("PH");

		log.debug("response" + result);
	}

}
