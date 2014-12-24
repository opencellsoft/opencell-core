package org.meveo.admin.web;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.servlet.ServletContext;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.reflections.Reflections;
import org.slf4j.Logger;

@Singleton
@Startup
public class ModuleLoader implements Integrator {

	@Inject
	private ServletContext servletContext;

	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		System.out.println("Initializing ModuleLoader " + this);
	}

	public void addItem(String menuName, String itemName, String action) {
		System.out.println("addItem " + menuName + "," + itemName + ","
				+ action);
	}

	@Override
	public void disintegrate(SessionFactoryImplementor arg0,
			SessionFactoryServiceRegistry arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void integrate(Configuration configuration,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		System.out.println("ModuleLoader integrate " + this
				+ " servlet context " + servletContext);

		if (servletContext != null) {

			@SuppressWarnings("unchecked")
			List<String> libJars = (List<String>) servletContext
					.getAttribute(ServletContext.ORDERED_LIBS);
			System.out.println("found " + libJars.size() + " libs to parse");
			for (String jarName : libJars) {
				System.out.println("Parsing jar : " + jarName);
				Reflections reflections = new Reflections("org.meveo");
				Set<String> entityClasses = reflections.getStore()
						.getTypesAnnotatedWith(Entity.class.getName());
				Set<String> mappedSuperClasses = reflections
						.getStore()
						.getTypesAnnotatedWith(MappedSuperclass.class.getName());
				if (entityClasses.size() > 0 || mappedSuperClasses.size() > 0) {
					for (String mappedClass : mappedSuperClasses) {

						System.out
								.println("found mappedClass : " + mappedClass);
						try {
							configuration.addAnnotatedClass(Class
									.forName(mappedClass));
						} catch (ClassNotFoundException e) {
							log.error(e.getMessage());
						}
					}

					for (String entityClass : entityClasses) {
						System.out
								.println("found entityClass : " + entityClass);
						try {
							configuration.addAnnotatedClass(Class
									.forName(entityClass));
						} catch (ClassNotFoundException e) {
							log.error(e.getMessage());
						}
					}
				}
			}

			configuration.buildMappings();
		}
	}

	@Override
	public void integrate(MetadataImplementor arg0,
			SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {
		// TODO Auto-generated method stub

	}
}
