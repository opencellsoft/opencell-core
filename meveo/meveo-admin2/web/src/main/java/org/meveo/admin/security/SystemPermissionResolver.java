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
package org.meveo.admin.security;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.permission.PersistentPermissionResolver;

/**
 * org.jboss.seam.security.permission.PersistentPermissionResolver component fix
 * to prevent system from large amount of SQL queries when checking user
 * permissions
 * 
 * @author Gediminas Ubartas
 */
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("org.jboss.seam.security.persistentPermissionResolver")
@Install(precedence = Install.APPLICATION)
@Startup
public class SystemPermissionResolver extends PersistentPermissionResolver {
    private static final long serialVersionUID = -9063212676529968783L;

    @Logger
    private Log log;

    private Map<String, Map<String, String>> permissions = new HashMap<String, Map<String, String>>();

    @Observer( { "System.Permission.Removed", "System.Permission.Updated" })
    public void onSecurityUpdate() {
        permissions.clear();
    }

    @Override
    public boolean hasPermission(Object target, String action) {

        log.trace("Checking permissions: [identity = " + Identity.instance().getCredentials().getUsername()
                + "], [target = " + target + "]. [action = " + action + "]");

        Map<String, String> identityPermissions = permissions.get(Identity.instance().getCredentials().getUsername());

        if (identityPermissions == null) {
            identityPermissions = new HashMap<String, String>();
            permissions.put(Identity.instance().getCredentials().getUsername(), identityPermissions);
        }

        String permission = identityPermissions.get(target.toString());

        if (permission != null && permission.equals(action)) {
            return true;
        } else {
            boolean hasPermission = super.hasPermission(target, action);

            if (hasPermission) {
                identityPermissions.put(target.toString(), action);
                return true;
            }
        }

        return false;
    }
}
