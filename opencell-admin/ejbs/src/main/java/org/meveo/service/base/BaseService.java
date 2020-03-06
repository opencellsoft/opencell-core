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
package org.meveo.service.base;

import java.util.Random;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseService {
	private static final Random RANDOM = new Random();

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private BeanManager beanManager;

    protected String generateRequestId() {
        return "MEVEOADMIN-" + RANDOM.nextInt();
    }

	@SuppressWarnings("unchecked")
	protected <E> E getManagedBeanInstance(Class<E> beanClazz) {
		Bean<E> bean = (Bean<E>) beanManager.getBeans(beanClazz).iterator().next();
		CreationalContext<E> ctx = beanManager.createCreationalContext(bean);
		return (E) beanManager.getReference(bean, beanClazz, ctx);
	}
}
