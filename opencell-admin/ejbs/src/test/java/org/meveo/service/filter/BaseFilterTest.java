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
import org.meveo.export.ExportImportConfig;
import org.meveo.export.ExportTemplate;
import org.meveo.export.IEntityClassConverter;
import org.meveo.export.IEntityExportIdentifierConverter;
import org.meveo.export.IEntityHibernateProxyConverter;
import org.meveo.export.ImportFKNotFoundException;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.ProviderService;

import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.hibernate.util.Hibernate;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author Edward P. Legaspi
 **/
public class BaseFilterTest {

    public static WebArchive initArchive(WebArchive result) {
        // add seam security
        File[] seamDependencies = Maven.resolver().resolve("org.jboss.seam.security:seam-security:3.1.0.Final").withTransitivity().asFile();
        result.addAsLibraries(seamDependencies);

        // apache commons
        File[] apacheCommonsDependencies = Maven.resolver().resolve("commons-lang:commons-lang:2.3").withTransitivity().asFile();
        result.addAsLibraries(apacheCommonsDependencies);

        File[] apacheCommons3Dependencies = Maven.resolver().resolve("org.apache.commons:commons-lang3:3.4").withTransitivity().asFile();
        result.addAsLibraries(apacheCommons3Dependencies);

        // xstream
        File[] xstreamDependencies = Maven.resolver().resolve("com.thoughtworks.xstream:xstream:1.4.8").withTransitivity().asFile();
        result.addAsLibraries(xstreamDependencies);

        File[] apacheCommonsValidator = Maven.resolver().resolve("commons-validator:commons-validator:1.4.1").withTransitivity().asFile();
        result.addAsLibraries(apacheCommonsValidator);

        // producers
        result = result.addClasses(LoggerProducer.class, ComponentResources.class, MeveoUser.class);

        // common classes
        result = result.addClasses(StringUtils.class, Sha1Encrypt.class, ReflectionUtils.class);

        // base services
        result = result.addClasses(PersistenceService.class, IPersistenceService.class, BaseService.class, BusinessService.class, ProviderService.class, UserService.class,
            TitleService.class, PaginationConfiguration.class, QueryBuilder.class, ParamBean.class, FilteredQueryBuilder.class);

        result = result.addClasses(RemoteAuthenticationException.class, ExportImportConfig.class, ExportTemplate.class);

        result = result.addClasses(IEntityHibernateProxyConverter.class, IEntityExportIdentifierConverter.class, HibernatePersistentCollectionConverter.class,
            HibernatePersistentMapConverter.class, HibernatePersistentSortedMapConverter.class, HibernatePersistentSortedSetConverter.class, HibernateProxyConverter.class,
            IEntityClassConverter.class, HibernateMapper.class, Hibernate.class, ImportFKNotFoundException.class, MapperWrapper.class);

        // add models
        result = result.addPackages(true, "org/meveo/model");

        // filter
        result = result.addPackages(true, "com/google/common");
        result = result.addPackages(true, "org/reflections");
        result = result.addPackages(true, "org/meveo/service/filter");
        result = result.addPackages(true, "org/meveo/service/base");
        // result = result.addPackages(true, "org/meveo/cache");

        // add exceptions
        result = result.addPackage("org/meveo/admin/exception");

        result = result.addAsResource("META-INF/test-filter-persistence.xml", "META-INF/persistence.xml").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            // Deploy our test datasource
            .addAsWebInfResource("test-filter-ds.xml", "test-ds.xml")
            // initialize db
            .addAsResource("import-filter.sql", "import.sql");

        return result;
    }

}
