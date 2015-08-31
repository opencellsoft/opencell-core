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
package org.meveo.admin.exception;

import javax.ejb.ApplicationException;
import javax.naming.InitialContext;

import org.meveo.commons.utils.ParamBean;
import org.meveo.event.monitoring.CreateEventHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationException(rollback = true)
public class BusinessException extends Exception {
	private static final long serialVersionUID = 1L;

	public BusinessException() {
		super();
		registerEvent();
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		registerEvent();
	}

	public BusinessException(String message) {
		super(message);
		registerEvent();
	}

	public BusinessException(Throwable cause) {
		super(cause);
		registerEvent();
	}

	public void registerEvent() {
		if ("true".equals(ParamBean.getInstance().getProperty("monitoring.sendException", "false"))) {
			try {
				InitialContext ic = new InitialContext();
				CreateEventHelper createEventHelper = (CreateEventHelper) ic.lookup("java:global/" + ParamBean.getInstance().getProperty("meveo.moduleName", "meveo") + "/CreateEventHelper");
				createEventHelper.register(this);
			} catch (Exception e) {
				Logger log = LoggerFactory.getLogger(this.getClass());
				log.error("Failed to access event helper", e);
			}
		}
	}
}
