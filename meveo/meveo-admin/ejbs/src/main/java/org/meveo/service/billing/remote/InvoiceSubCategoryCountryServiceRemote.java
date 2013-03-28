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
package org.meveo.service.billing.remote;

import javax.ejb.Remote;

import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.service.base.local.IPersistenceService;

/**
 * @author Marouane ALAMI
 * @created 18-03-2013
 */
@Remote
public interface InvoiceSubCategoryCountryServiceRemote extends IPersistenceService<InvoiceSubcategoryCountry> {

}
