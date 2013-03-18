/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.base;

import java.util.Random;

import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.meveo.service.admin.local.UserServiceLocal;
import org.slf4j.Logger;

public class BaseService {
	private static final Random RANDOM = new Random();

	@Inject
	protected Logger log;

	protected User getCurrentUser() {
		User user = null; /* TODO: (User) Component.getInstance("currentUser"); */
		if (user == null) {
			log.debug("MEVEOADMIN - Current user was not found in the seam context");

			UserServiceLocal userService = null; /*
												 * TODO: (UserServiceLocal)
												 * Component
												 * .getInstance("userService");
												 */
			log.debug("MEVEOADMIN - Looking for the user system");
			user = userService.getSystemUser();
		}

		return user;
	}

	protected String generateRequestId() {
		return "MEVEOADMIN-" + String.valueOf(RANDOM.nextInt());
	}
}