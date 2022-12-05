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

package org.meveo.api.rest.payment.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.IBanApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.IBanRs;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

/**
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class IBanRsImpl extends BaseRs implements IBanRs {

    @Inject
    private IBanApi ibanApi;

    @Override
    public ActionStatus validate(String iban, String bic) {

        ActionStatus result = new ActionStatus();

        if (!ibanApi.validate(iban, bic)) {
            result.setStatus(ActionStatusEnum.FAIL);
        }

        return result;
    }

}
