package org.meveo.api.rest.category.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.logging.LoggingInterceptor;
//import org.meveo.api.rest.category.CategoryRs;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.category.Category;

//@RequestScoped
//@Interceptors({ LoggingInterceptor.class })
//public class CategoryRsImpl implements CategoryRs {
//
////	@Inject
////	private UriInfo uriInfo;
//	private static Category category = new Category();
//	static{
//		category.setId("1");
//		category.setName("Default");
//		category.setDescription("Default category");
//		Calendar c = Calendar.getInstance();
//		c.set(1970, 1, 1, 0, 0, 0);
//		category.setLastUpdate(c.getTime());
//		category.setLifecycleStatus(LifecycleStatus.ACTIVE);
//		TimeRange timeRange = new TimeRange();
//		timeRange.setStartDateTime(c.getTime());
//		category.setValidFor(timeRange);
//		category.setIsRoot(Boolean.TRUE);
//		category.setParentId(null);
//	}
//	@Override
//	public List<Category> findAll() {
//		List<Category> categories = new ArrayList<Category>();
////		String url = uriInfo.getAbsolutePath().toString();
////		category.setHref(url + category.getId());
//		categories.add(category);
//		return categories;
//	}
//
//	@Override
//	public Category find(String id) {
//		category.setId(id);
////		String url = uriInfo.getAbsolutePath().toString();
////		category.setHref(url + category.getId());
//		return category;
//	}
//
//}
