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

import java.util.Map;
import java.util.Set;

import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.config.FilterResultsConfig;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.security.MeveoUser;

/**
 * This is the default result filter. I does not do any filtering. It is used if
 * a resultFilter attribute is not defined in the
 * {@link SecuredBusinessEntityMethod}. i.e. when the method result does not
 * need to be filtered.
 * 
 * @author Tony Alejandro
 *
 */
public class NullFilter extends SecureMethodResultFilter {

	@Override
	public Object filterResult(FilterResultsConfig filterResultsConfig, Object result, MeveoUser currentUser, Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap) {
		return result;
	}

}
