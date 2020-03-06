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

package org.meveo.service.script.product;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * @author Edward P. Legaspi
 * 
 * This interface is implemented by a custom user class.
 */
public interface ProductScriptInterface extends ScriptInterface {

	/**
	 * Called at the beginning of BusinessOfferModelService.createOfferFromBOM
	 * method at the beginning of product template creation for each product to
	 * duplicate.
	 * 
	 * @param methodContext
	 *            Method variables in a form of a map where
	 *            CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
	 * @throws BusinessException business exception
	 */
	void beforeCreate(Map<String, Object> methodContext) throws BusinessException;

	/**
	 * Called at the end of BusinessOfferModelService.createOfferFromBOM method
	 * at the end of product template creation for each product to duplicate.
	 * 
	 * @param methodContext
	 *            Method variables in a form of a map where
	 *            CONTEXT_ENTITY=ServiceTemplate,
	 *            CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
	 * @throws BusinessException business exception
	 */
	void afterCreate(Map<String, Object> methodContext) throws BusinessException;

}
