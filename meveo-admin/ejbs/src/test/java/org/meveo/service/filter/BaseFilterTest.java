package org.meveo.service.filter;

import java.io.File;

import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.meveo.admin.util.ComponentResources;
import org.meveo.admin.util.LoggerProducer;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.BusinessService;
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
public class BaseFilterTest {

	public static WebArchive initArchive(WebArchive result) {
		// add seam security
		File[] seamDependencies = Maven.resolver().resolve("org.jboss.seam.security:seam-security:3.1.0.Final")
				.withTransitivity().asFile();
		result.addAsLibraries(seamDependencies);

		// apache commons
		File[] apacheCommonsDependencies = Maven.resolver().resolve("commons-lang:commons-lang:2.3").withTransitivity()
				.asFile();
		result.addAsLibraries(apacheCommonsDependencies);
		
		File[] apacheCommons3Dependencies = Maven.resolver().resolve("org.apache.commons:commons-lang3:3.4").withTransitivity()
				.asFile();
		result.addAsLibraries(apacheCommons3Dependencies);

		// xstream
		File[] xstreamDependencies = Maven.resolver().resolve("com.thoughtworks.xstream:xstream:1.4.8")
				.withTransitivity().asFile();
		result.addAsLibraries(xstreamDependencies);
		
		File[] apacheCommonsValidator = Maven.resolver().resolve("commons-validator:commons-validator:1.4.1")
				.withTransitivity().asFile();
		result.addAsLibraries(apacheCommonsValidator);

		// producers
		result = result.addClasses(Resources.class, LoggerProducer.class, MeveoJpa.class, MeveoJpaForJobs.class,
				ComponentResources.class, MeveoUser.class);

		// common classes
		result = result.addClasses(StringUtils.class, Sha1Encrypt.class, ReflectionUtils.class);

		// base services
		result = result.addClasses(PersistenceService.class, IPersistenceService.class, BaseService.class,
				BusinessService.class, ProviderService.class, UserService.class, RoleService.class, TitleService.class,
				PaginationConfiguration.class, QueryBuilder.class, ParamBean.class, FilteredQueryBuilder.class);

		// add models
		result = result.addPackages(true, "org/meveo/model");

		// filter
		result = result.addPackages(true, "org/meveo/service/filter");
		result = result.addPackages(true, "org/meveo/service/base");

		// add exceptions
		result = result.addPackage("org/meveo/admin/exception");

		result = result.addAsResource("META-INF/test-filter-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-filter-ds.xml", "test-ds.xml")
				// initialize db
				.addAsResource("import-filter.sql", "import.sql");

		return result;
	}

}
