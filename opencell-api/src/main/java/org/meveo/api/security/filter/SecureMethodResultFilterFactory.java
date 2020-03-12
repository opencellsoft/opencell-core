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

package org.meveo.api.security.filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

/**
 * This factory encapsulates the creation and retrieval of
 * {@link SecureMethodResultFilter} instances.
 * 
 * @author Tony Alejandro
 *
 */
@Singleton
public class SecureMethodResultFilterFactory implements Serializable {

	private static final long serialVersionUID = 2249067511854832348L;

	@Any
	@Inject
	private Instance<SecureMethodResultFilter> filters;

	@Inject
	private Logger log;

	private Map<Class<? extends SecureMethodResultFilter>, SecureMethodResultFilter> filterMap = new HashMap<>();

	public SecureMethodResultFilter getFilter(Class<? extends SecureMethodResultFilter> filterClass) {
		initialize();
		SecureMethodResultFilter filter = filterMap.get(filterClass);
		if (filter == null) {
			log.warn("No SecuredBusinessEntityFilter instance of type {} found.", filterClass.getName());
		}
		return filter;
	}

	private void initialize() {
		if (filterMap.isEmpty()) {
			log.debug("Initializing filter map.");
			for (SecureMethodResultFilter filter : filters) {
				filterMap.put(filter.getClass(), filter);
			}
			log.debug("Filter map Initialization done. Found {} filters.", filterMap.size());
		}
	}
}
