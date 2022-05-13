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

package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.ordering.OpenOrderTypeEnum;

import javax.ws.rs.core.Response;
import java.util.Collections;


public class  OpenOrderTemplateResourceImpl  implements OpenOrderTemplateResource{


    public Response createOpenOrderTemplate(OpenOrderTemplateInput openOrderTemplateInput){

        checkParameters(openOrderTemplateInput);
        return null;
    }

    private void checkParameters(OpenOrderTemplateInput input) {
        if(input.getOpenOrderType() == OpenOrderTypeEnum.ARTICLES && input.getProducts() != null && !input.getProducts().isEmpty())
        {
            throw new BusinessApiException("Open order template of type ARTICLE can not be applied on products");
        }

        if(input.getOpenOrderType() == OpenOrderTypeEnum.PRODUCTS && input.getArticles() != null && !input.getArticles().isEmpty())
        {
            throw new BusinessApiException("Open order template of type PRODUCT can not be applied on articles");
        }


    }


    public Response updateOpenOrderTemplate( String code, OpenOrderTemplateInput openOrderTemplateInput){
        checkParameters(openOrderTemplateInput);
        return null;
    }

}
