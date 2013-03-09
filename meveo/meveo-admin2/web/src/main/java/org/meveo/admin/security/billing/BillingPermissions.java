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
package org.meveo.admin.security.billing;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.security.permission.Identifier;
import org.jboss.seam.annotations.security.permission.Permission;
import org.jboss.seam.annotations.security.permission.Permissions;
import org.jboss.seam.security.permission.ClassIdentifierStrategy;

@Name("billingPermissions")
@Identifier(name = "billing", value = ClassIdentifierStrategy.class)
@Permissions( { @Permission(action = "billingVisualization"), @Permission(action = "billingManagement") })
@Startup
@Scope(ScopeType.APPLICATION)
public class BillingPermissions {

}
