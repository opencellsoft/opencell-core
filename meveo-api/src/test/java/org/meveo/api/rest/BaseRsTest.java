package org.meveo.api.rest;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.meveo.admin.util.ComponentResources;
import org.meveo.admin.util.LoggerProducer;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.logging.Logged;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.security.RSSecured;
import org.meveo.api.rest.security.RSUser;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.Resources;

/**
 * @author Edward P. Legaspi
 **/
public class BaseRsTest {

	@ArquillianResource
	protected URL deploymentURL;

	public static WebArchive initArchive(WebArchive result) {
		// add seam security
		File[] seamDependencies = Maven.resolver()
				.resolve("org.jboss.seam.security:seam-security:3.1.0.Final")
				.withTransitivity().asFile();
		result.addAsLibraries(seamDependencies);

		// apache commons
		File[] apacheCommonsDependencies = Maven.resolver()
				.resolve("commons-lang:commons-lang:2.3").withTransitivity()
				.asFile();
		result.addAsLibraries(apacheCommonsDependencies);

		// producers
		result = result.addClasses(DefaultUserProducer.class, Resources.class,
				LoggerProducer.class, MeveoJpa.class, MeveoJpaForJobs.class,
				ComponentResources.class, MeveoUser.class);

		// common classes
		result = result.addClasses(StringUtils.class, Sha1Encrypt.class);

		// base services
		result = result.addClasses(PersistenceService.class,
				IPersistenceService.class, BaseService.class,
				ProviderService.class, UserService.class, RoleService.class,
				TitleService.class, PaginationConfiguration.class,
				QueryBuilder.class, ParamBean.class);

		// base api
		result = result
				.addClasses(JaxRsActivator.class, RSUser.class,
						RSSecured.class, IBaseRs.class, BaseRs.class,
						BaseApi.class, LoggingInterceptor.class, Logged.class,
						MeveoApiErrorCode.class);

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

}
