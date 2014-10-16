/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.admin.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.model.security.Permission;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 * @since Apr 4, 2013
 */
@Stateless
@LocalBean
public class PermissionService extends PersistenceService<Permission> {

}
