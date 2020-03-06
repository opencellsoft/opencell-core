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

package org.meveo.admin.web;

//@Singleton
//@Startup
public class ModuleLoader { // implements Integrator {
//
//	@Inject
//	private ServletContext servletContext;
//
//	@Inject
//	private Logger log;
//
//	@PostConstruct
//	public void init() {
//		System.out.println("Initializing ModuleLoader " + this);
//	}
//
//	public void addItem(String menuName, String itemName, String action) {
//		System.out.println("addItem " + menuName + "," + itemName + ","
//				+ action);
//	}
//
//	@Override
//	public void disintegrate(SessionFactoryImplementor arg0,
//			SessionFactoryServiceRegistry arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void integrate(Configuration configuration,
//			SessionFactoryImplementor sessionFactory,
//			SessionFactoryServiceRegistry serviceRegistry) {
//		System.out.println("ModuleLoader integrate " + this
//				+ " servlet context " + servletContext);
//
//		if (servletContext != null) {
//
//			@SuppressWarnings("unchecked")
//			List<String> libJars = (List<String>) servletContext
//					.getAttribute(ServletContext.ORDERED_LIBS);
//			System.out.println("found " + libJars.size() + " libs to parse");
//			for (String jarName : libJars) {
//				System.out.println("Parsing jar : " + jarName);
//				Reflections reflections = new Reflections("org.meveo");
//				Set<String> entityClasses = reflections.getStore()
//						.getTypesAnnotatedWith(Entity.class.getName());
//				Set<String> mappedSuperClasses = reflections
//						.getStore()
//						.getTypesAnnotatedWith(MappedSuperclass.class.getName());
//				if (entityClasses.size() > 0 || mappedSuperClasses.size() > 0) {
//					for (String mappedClass : mappedSuperClasses) {
//
//						System.out
//								.println("found mappedClass : " + mappedClass);
//						try {
//							configuration.addAnnotatedClass(Class
//									.forName(mappedClass));
//						} catch (ClassNotFoundException e) {
//							log.error(e.getMessage());
//						}
//					}
//
//					for (String entityClass : entityClasses) {
//						System.out
//								.println("found entityClass : " + entityClass);
//						try {
//							configuration.addAnnotatedClass(Class
//									.forName(entityClass));
//						} catch (ClassNotFoundException e) {
//							log.error(e.getMessage());
//						}
//					}
//				}
//			}
//
//			configuration.buildMappings();
//		}
//	}
//
//	@Override
//	public void integrate(MetadataImplementor arg0,
//			SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {
//		// TODO Auto-generated method stub
//
//	}
}
